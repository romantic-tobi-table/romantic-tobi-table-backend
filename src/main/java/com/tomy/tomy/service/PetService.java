package com.tomy.tomy.service;

import com.tomy.tomy.domain.Pet;
import com.tomy.tomy.domain.User;
import com.tomy.tomy.enums.PointTransactionType;
import com.tomy.tomy.repository.PetRepository;
import com.tomy.tomy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final PointService pointService; // Dependency

    @Transactional(readOnly = true)
    public Pet getPetInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        return petRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Pet not found for user."));
    }

    @Transactional
    public Pet feedPet(Long userId, int pointsToSpend) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        Pet pet = petRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Pet not found for user."));

        pointService.spendPoints(user, pointsToSpend, PointTransactionType.FEED_SPEND, "Pet feeding", pet.getId());

        // Logic for pet experience and leveling up
        pet.setExp(pet.getExp() + pointsToSpend); // Example: points spent contribute to exp
        pet.setUpdatedAt(LocalDateTime.now());

        // Simple leveling up logic (can be more complex)
        if (pet.getExp() >= (pet.getLevel() * 100)) { // Example: 100 exp per level
            pet.setLevel(pet.getLevel() + 1);
            // Handle exceeded exp for next level if needed
        }

        return petRepository.save(pet);
    }

    @Transactional
    public Pet levelUpPet(Long userId, int exceededExp) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        Pet pet = petRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Pet not found for user."));

        pet.setLevel(pet.getLevel() + 1);
        pet.setExp(exceededExp); // Set remaining exp for the new level
        pet.setUpdatedAt(LocalDateTime.now());

        return petRepository.save(pet);
    }
}
