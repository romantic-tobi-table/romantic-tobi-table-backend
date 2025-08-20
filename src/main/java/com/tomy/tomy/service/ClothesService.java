package com.tomy.tomy.service;

import com.tomy.tomy.domain.Clothes;
import com.tomy.tomy.domain.Pet;
import com.tomy.tomy.domain.User;
import com.tomy.tomy.domain.UserClothes;
import com.tomy.tomy.enums.PointTransactionType;
import com.tomy.tomy.repository.ClothesRepository;
import com.tomy.tomy.repository.PetRepository;
import com.tomy.tomy.repository.UserClothesRepository;
import com.tomy.tomy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ClothesService {

    private final ClothesRepository clothesRepository;
    private final UserClothesRepository userClothesRepository;
    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final PointService pointService;

    @Transactional(readOnly = true)
    public List<Clothes> getAllClothes() {
        return clothesRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Clothes> getClothesById(Long id) {
        return clothesRepository.findById(id);
    }

    @Transactional
    public UserClothes purchaseAndEquipClothes(Long userId, Long clothesId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        Pet pet = petRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Pet not found for user."));
        Clothes clothes = clothesRepository.findById(clothesId)
                .orElseThrow(() -> new IllegalArgumentException("Clothes not found."));

        Set<String> ALLOWED = Set.of("HEAD", "EYE", "FACE");
        if (!ALLOWED.contains(clothes.getCategory())) {
            throw new IllegalArgumentException("지원하지 않는 카테고리입니다: " + clothes.getCategory());
        }

        // Check if already purchased
        Optional<UserClothes> existingUserClothes = userClothesRepository.findByPetAndClothes(pet, clothes);
        if (existingUserClothes.isPresent()) {
            // If already purchased, just equip it
            UserClothes uc = existingUserClothes.get();
            if (!uc.getIsEquipped()) {
                // Unequip current clothes of the same category
                userClothesRepository.findByPetAndClothesCategoryAndIsEquipped(pet, clothes.getCategory(), true)
                        .ifPresent(currentEquipped -> {
                            currentEquipped.setIsEquipped(false);
                            userClothesRepository.save(currentEquipped);
                        });
                uc.setIsEquipped(true);
                return userClothesRepository.save(uc);
            } else {
                throw new IllegalArgumentException("이미 착용 중인 옷입니다.");
            }
        }

        // Spend points to purchase
        pointService.spendPoints(user, clothes.getPrice(), PointTransactionType.CLOTHES_BUY, "Clothes purchase", clothes.getId());

        // Unequip current clothes of the same category before equipping new one
        userClothesRepository.findByPetAndClothesCategoryAndIsEquipped(pet, clothes.getCategory(), true)
                .ifPresent(currentEquipped -> {
                    currentEquipped.setIsEquipped(false);
                    userClothesRepository.save(currentEquipped);
                });

        UserClothes userClothes = new UserClothes();
        userClothes.setPet(pet);
        userClothes.setClothes(clothes);
        userClothes.setIsEquipped(true); // Equip immediately after purchase
        userClothes.setPurchasedAt(LocalDateTime.now());

        return userClothesRepository.save(userClothes);
    }

    @Transactional
    public UserClothes unequipClothes(Long userId, Long clothesId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        Pet pet = petRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Pet not found for user."));
        Clothes clothes = clothesRepository.findById(clothesId)
                .orElseThrow(() -> new IllegalArgumentException("Clothes not found."));

        UserClothes userClothes = userClothesRepository.findByPetAndClothesAndIsEquipped(pet, clothes, true)
                .orElseThrow(() -> new IllegalArgumentException("현재 착용 중이지 않은 옷입니다."));

        userClothes.setIsEquipped(false);
        return userClothesRepository.save(userClothes);
    }

    @Transactional(readOnly = true)
    public List<UserClothes> getEquippedClothes(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        Pet pet = petRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Pet not found for user."));
        return userClothesRepository.findByPetAndIsEquipped(pet, true);
    }
}
