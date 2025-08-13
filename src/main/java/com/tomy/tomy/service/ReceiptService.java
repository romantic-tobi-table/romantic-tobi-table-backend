package com.tomy.tomy.service;

import com.tomy.tomy.domain.Receipt;
import com.tomy.tomy.domain.Store;
import com.tomy.tomy.domain.User;
import com.tomy.tomy.enums.PointTransactionType;
import com.tomy.tomy.repository.ReceiptRepository;
import com.tomy.tomy.repository.StoreRepository;
import com.tomy.tomy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final PointService pointService; // Assuming PointService exists

    @Transactional
    public Receipt uploadReceipt(Long userId, String recognizedText, LocalDate recognizedDate, String ocrRawJson) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        // TODO: Implement actual OCR processing and store recognition
        // For now, mock store recognition
        Store store = storeRepository.findById(1L) // Placeholder for a recognized store
                .orElseThrow(() -> new IllegalArgumentException("Store not recognized."));

        // Check for duplicate receipt (storeName, date, userId)
        // This requires a custom query in ReceiptRepository
        // For now, skipping detailed duplicate check, but it's in the ERD unique constraint.

        Receipt receipt = new Receipt();
        receipt.setUser(user);
        receipt.setStore(store);
        receipt.setTotalPrice(10000); // Placeholder
        receipt.setRecognizedText(recognizedText);
        receipt.setRecognizedDate(recognizedDate);
        receipt.setVerified(false); // Will be verified in a separate step
        receipt.setOcrRawJson(ocrRawJson);
        receipt.setCreatedAt(LocalDateTime.now());

        return receiptRepository.save(receipt);
    }

    @Transactional
    public Receipt verifyReceiptAndEarnPoints(Long userId, String recognizedText, LocalDate recognizedDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        // TODO: Re-verify recognizedText and recognizedDate against actual store DB
        // For now, mock store recognition
        Store store = storeRepository.findById(1L) // Placeholder for a recognized store
                .orElseThrow(() -> new IllegalArgumentException("소상공인 가게로 인식되지 않았습니다."));

        // Find the uploaded receipt
        Receipt receipt = receiptRepository.findByUserAndStoreAndRecognizedDate(user, store, recognizedDate)
                .orElseThrow(() -> new IllegalArgumentException("Receipt not found."));

        if (receipt.getVerified()) {
            throw new IllegalArgumentException("이미 인증된 영수증입니다.");
        }

        // Calculate points (e.g., 1% of total price)
        int pointsEarned = (int) (receipt.getTotalPrice() * 0.01);

        // Earn points
        pointService.earnPoints(user, pointsEarned, PointTransactionType.RECEIPT_EARN, "Receipt verification", receipt.getId());

        receipt.setVerified(true);
        return receiptRepository.save(receipt);
    }

    @Transactional(readOnly = true)
    public List<Receipt> getUserReceipts(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        return receiptRepository.findByUser(user);
    }
}
