package com.tomy.tomy.controller;

import com.tomy.tomy.dto.ErrorResponse;
import com.tomy.tomy.dto.ReceiptCheckResponse;
import com.tomy.tomy.dto.ReceiptUploadResponse;
import com.tomy.tomy.dto.ReceiptVerifyRequest;
import com.tomy.tomy.dto.ReceiptVerifyResponse;
import com.tomy.tomy.domain.Receipt;
import com.tomy.tomy.service.ReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/receipt")
@RequiredArgsConstructor
public class ReceiptController {

    private final ReceiptService receiptService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadReceipt(@RequestHeader("Authorization") String authorizationHeader,
                                           @RequestParam("file") MultipartFile file) {
        // TODO: Extract userId from JWT token in authorizationHeader
        Long userId = 1L; // Placeholder

        try {
            // TODO: Implement actual OCR processing and extract recognizedText, recognizedDate, ocrRawJson
            String recognizedText = "구미상회 123,000원";
            LocalDate recognizedDate = LocalDate.now();
            String ocrRawJson = "{ \"raw\": \"ocr_data\" }";

            Receipt receipt = receiptService.uploadReceipt(userId, recognizedText, recognizedDate, ocrRawJson);
            return ResponseEntity.ok(new ReceiptUploadResponse(receipt.getRecognizedText(), receipt.getRecognizedDate().toString(), "success"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("영수증 업로드 실패."));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyReceipt(@RequestHeader("Authorization") String authorizationHeader,
                                           @RequestBody ReceiptVerifyRequest request) {
        // TODO: Extract userId from JWT token in authorizationHeader
        Long userId = 1L; // Placeholder

        try {
            Receipt receipt = receiptService.verifyReceiptAndEarnPoints(userId, request.getRecognizedText(), LocalDate.parse(request.getRecognizedDate()));
            // TODO: Fetch current points from PointService or Pet entity
            Integer currentPoint = 2300; // Placeholder
            return ResponseEntity.ok(new ReceiptVerifyResponse(receipt.getStore().getName(), receipt.getTotalPrice(), (int)(receipt.getTotalPrice() * 0.01), currentPoint));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("영수증 인증 실패."));
        }
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkReceipts(@RequestHeader("Authorization") String authorizationHeader) {
        // TODO: Extract userId from JWT token in authorizationHeader
        Long userId = 1L; // Placeholder

        try {
            List<Receipt> receipts = receiptService.getUserReceipts(userId);
            List<ReceiptCheckResponse> response = receipts.stream()
                    .map(receipt -> new ReceiptCheckResponse(receipt.getId(), receipt.getStore().getName(), receipt.getTotalPrice(), "메뉴 정보 없음")) // "메뉴" is not in ERD
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("영수증 조회 실패."));
        }
    }
}
