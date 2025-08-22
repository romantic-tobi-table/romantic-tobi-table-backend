package com.tomy.tomy.service;

import com.tomy.tomy.domain.Clothes;
import com.tomy.tomy.domain.Pet;
import com.tomy.tomy.domain.User;
import com.tomy.tomy.domain.UserClothes;
import com.tomy.tomy.dto.ClothesResponse;
import com.tomy.tomy.enums.ClothesStatus;
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
import java.util.stream.Collectors;

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
            UserClothes uc = existingUserClothes.get();
            if (uc.getStatus() == ClothesStatus.EQUIPPED) {
                throw new IllegalArgumentException("이미 착용 중인 옷입니다.");
            }

            List<UserClothes> equippedClothes = userClothesRepository.findByPetAndStatus(pet, ClothesStatus.EQUIPPED);
            for (UserClothes currentlyEquipped : equippedClothes) {
                currentlyEquipped.setStatus(ClothesStatus.OWNED);
                userClothesRepository.save(currentlyEquipped);
            }
            uc.setStatus(ClothesStatus.EQUIPPED);
            return userClothesRepository.save(uc);
        }

        // Spend points to purchase
        pointService.spendPoints(user, clothes.getPrice(), PointTransactionType.CLOTHES_BUY, "Clothes purchase", clothes.getId());

        // Unequip all currently equipped clothes before equipping new one
        List<UserClothes> equippedClothes = userClothesRepository.findByPetAndStatus(pet, ClothesStatus.EQUIPPED);
        for (UserClothes currentlyEquipped : equippedClothes) {
            currentlyEquipped.setStatus(ClothesStatus.OWNED);
            userClothesRepository.save(currentlyEquipped);
        }

        // Equip the new clothes
        UserClothes userClothes = new UserClothes();
        userClothes.setPet(pet);
        userClothes.setClothes(clothes);
        userClothes.setStatus(ClothesStatus.EQUIPPED);
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

        UserClothes userClothes = userClothesRepository
                .findByPetAndClothesAndStatus(pet, clothes, ClothesStatus.EQUIPPED)
                .orElseThrow(() -> new IllegalArgumentException("현재 착용 중이지 않은 옷입니다."));

        userClothes.setStatus(ClothesStatus.OWNED);
        return userClothesRepository.save(userClothes);
    }

    @Transactional(readOnly = true)
    public Optional<UserClothes> getEquippedClothes(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        Pet pet = petRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Pet not found for user."));
        return userClothesRepository.findByPetAndStatus(pet, ClothesStatus.EQUIPPED).stream().findFirst();
    }

    @Transactional(readOnly = true)
    public List<ClothesResponse> getAllClothesWithStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        Pet pet = petRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Pet not found for user."));

        List<Clothes> all = clothesRepository.findAll();
        var statusByClothesId = userClothesRepository.findByPet(pet).stream()
                .collect(Collectors.toMap(uc -> uc.getClothes().getId(), UserClothes::getStatus));

        return all.stream().map(c ->
            new ClothesResponse(
                c.getId(),
                c.getName(),
                c.getPrice(),
                c.getCategory(),
                c.getImageUrl(),
                c.getDescription(),
                statusByClothesId.getOrDefault(c.getId(), ClothesStatus.NOT_OWNED)
            )
        ).toList();

    }

    @Transactional(readOnly = true)
    public Optional<ClothesResponse> getClothesById(Long userId, Long clothesId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        Pet pet = petRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Pet not found for user."));

        Clothes c = clothesRepository.findById(clothesId)
                .orElseThrow(() -> new IllegalArgumentException("Clothes not found."));

        ClothesStatus status = userClothesRepository.findByPetAndClothes(pet, c)
                .map(UserClothes::getStatus)
                .orElse(ClothesStatus.NOT_OWNED);

        return Optional.of(new ClothesResponse(
                c.getId(), c.getName(), c.getPrice(), c.getCategory(),
                c.getImageUrl(), c.getDescription(), status
        ));
    }

}
