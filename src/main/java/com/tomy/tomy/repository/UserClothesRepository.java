package com.tomy.tomy.repository;

import com.tomy.tomy.domain.Clothes;
import com.tomy.tomy.domain.Pet;
import com.tomy.tomy.domain.UserClothes;
import com.tomy.tomy.enums.ClothesStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserClothesRepository extends JpaRepository<UserClothes, Long> {
    Optional<UserClothes> findByPetAndClothes(Pet pet, Clothes clothes);
    Optional<UserClothes> findByPetAndClothesCategoryAndStatus(Pet pet, String category, ClothesStatus status);
    Optional<UserClothes> findByPetAndClothesAndStatus(Pet pet, Clothes clothes, ClothesStatus status);
    List<UserClothes> findByPetAndStatus(Pet pet, ClothesStatus status);
    List<UserClothes> findByPet(Pet pet);
}