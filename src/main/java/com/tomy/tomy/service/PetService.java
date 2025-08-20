package com.tomy.tomy.service;

import com.tomy.tomy.domain.Pet;
import com.tomy.tomy.domain.User;
import com.tomy.tomy.dto.AchievementUpdateRequest;
import com.tomy.tomy.enums.PointTransactionType;
import com.tomy.tomy.repository.PetRepository;
import com.tomy.tomy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final PointService pointService; // Dependency
    private final AchievementService achievementService;


    private static final int MAX_LEVEL = 5;
    private static final Map<Integer, Integer> LEVEL_EXP_MAP = Map.of(
            1, 300,
            2, 600,
            3, 900,
            4, 1200,
            5, 1500
    );

    @Transactional
    public Pet createPet(User user) {
        Pet pet = new Pet();
        pet.setUser(user);
        pet.setLevel(1);
        pet.setCurrentPoint(0L);
        pet.setExp(0);
        pet.setUpdatedAt(LocalDateTime.now());
        return petRepository.save(pet);
    }

    @Transactional(readOnly = true)
    public Pet getPetInfo(String userId) { // Changed parameter type to String
        User user = userRepository.findByUserId(userId) // Find user by String userId
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        return petRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Pet not found for user."));
    }

    @Transactional
    public Pet feedPet(String userId, int pointsToSpend) { // Changed parameter type to String
        User user = userRepository.findByUserId(userId) // Find user by String userId
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        Pet pet = petRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Pet not found for user."));

        pointService.spendPoints(user, pointsToSpend, PointTransactionType.FEED_SPEND, "Pet feeding", pet.getId());

        pet.setExp(pet.getExp() + pointsToSpend);
        pet.setUpdatedAt(LocalDateTime.now());

        // New level-up logic with carry-over and max level cap
        while (pet.getLevel() < MAX_LEVEL) {
            int requiredExp = LEVEL_EXP_MAP.get(pet.getLevel());
            if (pet.getExp() >= requiredExp) {
                pet.setExp(pet.getExp() - requiredExp);
                pet.setLevel(pet.getLevel() + 1);
            } else {
                break; // Not enough experience for the next level
            }
        }
        achievementService.updateUserAchievementProgress(user.getId(), AchievementUpdateRequest.AchievementType.FEEDING);
        return petRepository.save(pet);
    }
}
