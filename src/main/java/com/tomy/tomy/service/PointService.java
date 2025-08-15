package com.tomy.tomy.service;

import com.tomy.tomy.domain.Pet;
import com.tomy.tomy.domain.PointTransaction;
import com.tomy.tomy.domain.User;
import com.tomy.tomy.enums.PointTransactionType;
import com.tomy.tomy.repository.PetRepository;
import com.tomy.tomy.repository.PointTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointTransactionRepository pointTransactionRepository;
    private final PetRepository petRepository; // Pet holds current_point

    @Transactional
    public void earnPoints(User user, int amount, PointTransactionType type, String refTable, Long refId) {
        Pet pet = petRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Pet not found for user."));

        int newBalance = pet.getCurrentPoint() + amount;
        pet.setCurrentPoint(newBalance);
        pet.setUpdatedAt(LocalDateTime.now());
        petRepository.save(pet);

        PointTransaction transaction = new PointTransaction();
        transaction.setUser(user);
        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setBalanceAfter(newBalance);
        transaction.setRefTable(refTable);
        transaction.setRefId(refId);
        transaction.setCreatedAt(LocalDateTime.now());
        pointTransactionRepository.save(transaction);
    }

    @Transactional
    public void spendPoints(User user, int amount, PointTransactionType type, String refTable, Long refId) {
        Pet pet = petRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Pet not found for user."));

        if (pet.getCurrentPoint() < amount) {
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }

        int newBalance = pet.getCurrentPoint() - amount;
        pet.setCurrentPoint(newBalance);
        pet.setUpdatedAt(LocalDateTime.now());
        petRepository.save(pet);

        PointTransaction transaction = new PointTransaction();
        transaction.setUser(user);
        transaction.setType(type);
        transaction.setAmount(-amount); // Record as negative for spending
        transaction.setBalanceAfter(newBalance);
        transaction.setRefTable(refTable);
        transaction.setRefId(refId);
        transaction.setCreatedAt(LocalDateTime.now());
        pointTransactionRepository.save(transaction);
    }

    @Transactional(readOnly = true)
    public int getCurrentPoints(User user) {
        Pet pet = petRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Pet not found for user."));
        return pet.getCurrentPoint();
    }
}
