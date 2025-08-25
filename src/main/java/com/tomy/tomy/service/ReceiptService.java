package com.tomy.tomy.service;

import com.tomy.tomy.domain.Pet;
import com.tomy.tomy.domain.Receipt;
import com.tomy.tomy.domain.Store;
import com.tomy.tomy.domain.User;
import com.tomy.tomy.dto.AchievementUpdateRequest;
import com.tomy.tomy.dto.EnhancedParsedReceipt;
import com.tomy.tomy.dto.ReceiptCheckResponse;
import com.tomy.tomy.dto.ReceiptUploadResponse;
import com.tomy.tomy.exception.DuplicateReceiptException;
import com.tomy.tomy.ocr.OcrService;
import com.tomy.tomy.parser.ReceiptParser;
import com.tomy.tomy.repository.PetRepository;
import com.tomy.tomy.repository.ReceiptRepository;
import com.tomy.tomy.repository.StoreRepository;
import com.tomy.tomy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final OcrService ocrService;
    private final ReceiptParser receiptParser;
    private final AchievementService achievementService;

    @Transactional
    public ReceiptUploadResponse uploadReceipt(Long userId, MultipartFile file) {
        Store matchedStore = null;
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        var ocrResult = ocrService.extractText(file);
        String rawText = ocrResult.fullText();

        EnhancedParsedReceipt parsedData = receiptParser.parse(rawText);

        int amount = 0;
        if (parsedData.amount() != null) {
            try {
                amount = Integer.parseInt(parsedData.amount().replaceAll(",", ""));
            } catch (NumberFormatException e) {
                // 금액 파싱 실패 시 처리
            }
        }

        LocalDate paidAt = null;
        if (parsedData.paidAt() != null) {
            try {
                String dateString = parsedData.paidAt().substring(0, 10).replace('/', '-').replace('.', '-');
                paidAt = LocalDate.parse(dateString);
            } catch (Exception e) {
                // 날짜 파싱 실패 시 처리
            }
        }

        if (paidAt != null) {
            if (receiptRepository.existsByStoreNameAndRecognizedDateAndTotalPrice(parsedData.storeName(), paidAt, amount)) {
                throw new DuplicateReceiptException("이미 인증된 영수증입니다.");
            }
        }



        Optional<Store> exactMatch = storeRepository.findByName(parsedData.storeName());
        if (exactMatch.isPresent()) {
            // Check if address matches for exact name match
            if (isAddressMatch(parsedData.address(), exactMatch.get().getAddress())) {
                matchedStore = exactMatch.get();
            }
        }

        if (matchedStore == null) { // If no exact match or address mismatch, try fuzzy
            Page<Store> fuzzyMatchedStoresPage = storeRepository.findByNameContainingIgnoreCase(parsedData.storeName(), PageRequest.of(0, 5));
            List<Store> fuzzyMatchedStores = fuzzyMatchedStoresPage.getContent();
            for (Store store : fuzzyMatchedStores) {
                if (isAddressMatch(parsedData.address(), store.getAddress())) {
                    matchedStore = store;
                    break;
                }
            }
        }

        if (matchedStore == null) {
            return new ReceiptUploadResponse(null, null, null, null, null, null, "소상공인 가게가 아닙니다", null);
        }

        long pointsEarned = amount / 100;

        Pet pet = petRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Pet not found for user."));
        pet.setCurrentPoint(pet.getCurrentPoint() + pointsEarned);
        petRepository.save(pet);

        Receipt receipt = new Receipt();
        receipt.setUser(user);
        receipt.setStore(matchedStore);
        receipt.setStoreName(parsedData.storeName());
        receipt.setAddress(parsedData.address());

        receipt.setTotalPrice(amount);
        receipt.setRecognizedText(rawText);
        receipt.setRecognizedDate(paidAt);
        receipt.setCreatedAt(LocalDateTime.now());
        receiptRepository.save(receipt);
        achievementService.updateUserAchievementProgress(userId, AchievementUpdateRequest.AchievementType.RECEIPT);

        return new ReceiptUploadResponse(parsedData.storeName(), parsedData.paidAt(), parsedData.address(), parsedData.amount(), pointsEarned, pet.getCurrentPoint(), "영수증 인증 완료", null);
    }

    @Transactional(readOnly = true)
    public List<ReceiptCheckResponse> getReceipts(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        return receiptRepository.findAllByUser(user).stream()
                .map(ReceiptCheckResponse::from)
                .collect(Collectors.toList());
    }

    private boolean isAddressMatch(String receiptAddress, String storeAddress) {
        if (receiptAddress == null || storeAddress == null) {
            return false;
        }

        String normalizedReceiptAddress = normalizeAddress(receiptAddress);
        String normalizedStoreAddress = normalizeAddress(storeAddress);

        return normalizedReceiptAddress.contains(normalizedStoreAddress);
    }

    private String normalizeAddress(String address) {
        return address.replace("경북", "경상북도")
                      .replace("경남", "경상남도")
                      .replace("전북", "전라북도")
                      .replace("전남", "전라남도")
                      .replace("충북", "충청북도")
                      .replace("충남", "충청남도");
    }
}