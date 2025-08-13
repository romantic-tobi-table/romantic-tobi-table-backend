package com.tomy.tomy.repository;

import com.tomy.tomy.domain.Clothes;
import com.tomy.tomy.domain.Pet;
import com.tomy.tomy.domain.UserClothes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserClothesRepository extends JpaRepository<UserClothes, Long> {
    Optional<UserClothes> findByPetAndClothes(Pet pet, Clothes clothes);
    Optional<UserClothes> findByPetAndClothesCategoryAndIsEquipped(Pet pet, String category, Boolean isEquipped);
    Optional<UserClothes> findByPetAndClothesAndIsEquipped(Pet pet, Clothes clothes, Boolean isEquipped);
    List<UserClothes> findByPetAndIsEquipped(Pet pet, Boolean isEquipped);
}