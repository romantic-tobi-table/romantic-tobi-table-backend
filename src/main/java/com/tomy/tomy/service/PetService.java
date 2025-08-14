package com.tomy.tomy.service;

import com.tomy.tomy.domain.Pet;
import com.tomy.tomy.domain.User;
import com.tomy.tomy.enums.PointTransactionType;
import com.tomy.tomy.repository.PetRepository;
import com.tomy.tomy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final PointService pointService;

    // Helper method to get the current authenticated user
    private User getCurrentUser() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with userId: " + userId));
    }

    @Transactional(readOnly = true)
    public Pet getPetInfo() {
        User user = getCurrentUser();
        return petRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Pet not found for user."));
    }

    @Transactional
    public Pet feedPet(int pointsToSpend) {
        User user = getCurrentUser();
        Pet pet = petRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Pet not found for user."));

        pointService.spendPoints(user, pointsToSpend, PointTransactionType.FEED_SPEND, "Pet feeding", pet.getId());

        pet.setExp(pet.getExp() + pointsToSpend);
        pet.setUpdatedAt(LocalDateTime.now());

        if (pet.getExp() >= (pet.getLevel() * 100)) {
            pet.setLevel(pet.getLevel() + 1);
        }

        return petRepository.save(pet);
    }

    @Transactional
    public Pet levelUpPet(int exceededExp) {
        User user = getCurrentUser();
        Pet pet = petRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Pet not found for user."));

        pet.setLevel(pet.getLevel() + 1);
        pet.setExp(exceededExp);
        pet.setUpdatedAt(LocalDateTime.now());

        return petRepository.save(pet);
    }
}