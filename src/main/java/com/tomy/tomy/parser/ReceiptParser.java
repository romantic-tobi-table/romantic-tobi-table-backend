package com.tomy.tomy.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomy.tomy.dto.EnhancedParsedReceipt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.text.NumberFormat;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ReceiptParser {

    private static final Logger logger = Logger.getLogger(ReceiptParser.class.getName());

    private final String openAiApiKey;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final String CHATGPT_PROMPT_TEMPLATE = """
        당신은 한국어 영수증 OCR 텍스트에서 **가게명(store_name)**, **주소(address)**, **총 결제 금액(total_amount)**, **결제 일시(paid_at)**만 뽑는 도구다.
        다음 규칙을 따른다:
        단 네 필드만 반환: store_name, address, total_amount, paid_at
        불명확하거나 누락이면 null
        주소는 불완전해도 가장 그럴듯한 단일 라인으로 제공(줄바꿈 병합).
        전화/URL/TEL, 사업자번호 등은 주소에서 제외.
        가능한 한 “시/군/구/읍/면/동/리 + 로/길/번길/대로 …” 형태의 도로명 주소를 선호.
        여러 후보가 있으면 문서 상단·메인 블록에 가까운 것을 우선.
        total_amount는 숫자만 포함하며, 천단위 구분 쉼표는 제거한다. (예: 10,000 -> 10000)
        paid_at은 YYYY-MM-DD HH:MM:SS 형식으로 반환한다. (예: 2023-10-26 14:30:00)
        출력은 아래 JSON만, 추가 텍스트 금지.
        출력 예시:
        {"store_name":"카페쎄미","address":"서울 강남구 봉은사로 524 (삼성동, 코엑스인터콘티넨탈서울)","total_amount":15000,"paid_at":"2023-10-26 14:30:00"}
        입력은 다음과 같다:
        """;

    public ReceiptParser(@Value("${openai.api.key}") String openAiApiKey) {
        this.openAiApiKey = openAiApiKey;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public EnhancedParsedReceipt parse(String rawText) {
        // Step 2: Extract amount (regex first)
        AmountExtractionResult amountResult = extractAmount(rawText);

        // Step 3: Extract paid_at (regex first)
        DateExtractionResult dateResult = extractPaidAt(rawText);

        // Step 1: Call ChatGPT for all fields (store_name, address, total_amount, paid_at)
        GptApiResponse gptResponse = callGptApi(rawText);

        String storeName = null;
        String address = null;
        String amount = null;
        String paidAt = null;
        String chatgptReason = "";

        // Prioritize regex results if available
        amount = amountResult.formattedAmount();
        paidAt = dateResult.firstMatch();

        if (gptResponse.parsedJson() != null) {
            storeName = gptResponse.parsedJson().has("store_name") ? gptResponse.parsedJson().get("store_name").asText(null) : null;
            address = gptResponse.parsedJson().has("address") ? gptResponse.parsedJson().get("address").asText(null) : null;

            // If regex failed, use ChatGPT's result
            if (amount == null) {
                amount = gptResponse.parsedJson().has("total_amount") ? gptResponse.parsedJson().get("total_amount").asText(null) : null;
            }
            if (paidAt == null) {
                paidAt = gptResponse.parsedJson().has("paid_at") ? gptResponse.parsedJson().get("paid_at").asText(null) : null;
            }
        } else {
            chatgptReason = gptResponse.rawContent(); // If parsing failed, raw content is the reason
        }

        // Step 4: Combine results
        EnhancedParsedReceipt.DebugInfo debugInfo = new EnhancedParsedReceipt.DebugInfo(
                amountResult.candidates(),
                dateResult.candidates(),
                chatgptReason
        );

        return new EnhancedParsedReceipt(storeName, address, amount, paidAt, debugInfo);
    }

    // New record to hold GPT API response
    private record GptApiResponse(JsonNode parsedJson, String rawContent) {}

    private GptApiResponse callGptApi(String rawText) {
        String apiUrl = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(openAiApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String userContent = "<<CHATGPT_RECEIPT_EXTRACTION_PROMPT>>\n\n---\nRAW_RECEIPT_TEXT:\n" + rawText;
        userContent = userContent.replace("<<CHATGPT_RECEIPT_EXTRACTION_PROMPT>>", CHATGPT_PROMPT_TEMPLATE);

        Map<String, Object> messageSystem = new HashMap<>();
        messageSystem.put("role", "system");
        messageSystem.put("content", "You extract only store_name and address from noisy Korean receipts. Return strict JSON."); // This will be replaced by the new prompt

        Map<String, Object> messageUser = new HashMap<>();
        messageUser.put("role", "user");
        messageUser.put("content", userContent);

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-4o-mini");
        body.put("messages", Arrays.asList(messageSystem, messageUser));
        body.put("temperature", 0);

        try {
            logger.info("Sending request to OpenAI API. Body: " + objectMapper.writeValueAsString(body));
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);

            logger.info("Received response from OpenAI API. Status: " + response.getStatusCode() + ", Body: " + response.getBody());
            JsonNode root = objectMapper.readTree(response.getBody());
            String content = root.path("choices").get(0).path("message").path("content").asText();
            logger.info("Extracted content from OpenAI response: " + content);

            try {
                JsonNode parsedContent = objectMapper.readTree(content);
                logger.info("Successfully parsed content as JSON.");
                return new GptApiResponse(parsedContent, content); // Return parsed JSON and raw content
            } catch (Exception jsonParseEx) {
                logger.warning("Content was not strict JSON. Raw content: " + content + ". Error: " + jsonParseEx.getMessage());
                // If content is not strict JSON, return null for parsedJson and raw content for reason
                return new GptApiResponse(null, content);
            }
        } catch (Exception e) {
            logger.severe("OpenAI API call failed: " + e.getMessage());
            // In case of API error, return null for parsedJson and error message for reason
            return new GptApiResponse(null, "API call failed: " + e.getMessage());
        }
    }

    private record AmountExtractionResult(String formattedAmount, List<String> candidates) {}

    private AmountExtractionResult extractAmount(String text) {
        Pattern pattern = Pattern.compile("\\b\\d{1,3}(?:[.,]\\d{3})+\\b|\\b\\d+[.,]\\d+\\b");
        Matcher matcher = pattern.matcher(text);
        
        List<String> candidates = new ArrayList<>();
        long maxAmount = 0;

        while (matcher.find()) {
            String match = matcher.group();
            candidates.add(match);
            try {
                long currentAmount = Long.parseLong(match.replaceAll("[.,]", ""));
                if (currentAmount > maxAmount) {
                    maxAmount = currentAmount;
                }
            } catch (NumberFormatException e) {
                // Ignore if parsing fails
            }
        }

        if (maxAmount == 0) {
            return new AmountExtractionResult(null, candidates);
        }

        return new AmountExtractionResult(NumberFormat.getNumberInstance(Locale.US).format(maxAmount), candidates);
    }

    private record DateExtractionResult(String firstMatch, List<String> candidates) {}

    private DateExtractionResult extractPaidAt(String text) {
        // Relaxed regex: allows single-digit month/day/hour, optional seconds
        Pattern pattern = Pattern.compile("\\b\\d{4}[-/.]\\d{1,2}[-/.]\\d{1,2}\\s+\\d{1,2}:\\d{2}(:\\d{2})?\\b");
        Matcher matcher = pattern.matcher(text);
        
        List<String> candidates = new ArrayList<>();
        while (matcher.find()) {
            candidates.add(matcher.group());
        }

        String firstMatch = candidates.isEmpty() ? null : candidates.get(0);
        return new DateExtractionResult(firstMatch, candidates);
    }
}