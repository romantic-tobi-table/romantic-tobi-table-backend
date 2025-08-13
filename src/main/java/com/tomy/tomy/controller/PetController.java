package com.tomy.tomy.controller;

import com.tomy.tomy.dto.*;
import com.tomy.tomy.domain.Pet;
import com.tomy.tomy.service.PetService;
import com.tomy.tomy.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pet")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;
    private final PointService pointService; // To get current points for response

    @GetMapping
    public ResponseEntity<?> getPetInfo(@RequestHeader("Authorization") String authorizationHeader) {
        // TODO: Extract userId from JWT token in authorizationHeader
        Long userId = 1L; // Placeholder

        try {
            Pet pet = petService.getPetInfo(userId);
            return ResponseEntity.ok(new PetInfoResponse(pet.getLevel(), pet.getCurrentPoint(), pet.getExp()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("펫 정보 조회 실패."));
        }
    }

    @PostMapping("/feed")
    public ResponseEntity<?> feedPet(@RequestHeader("Authorization") String authorizationHeader,
                                     @RequestBody FeedPetRequest request) {
        // TODO: Extract userId from JWT token in authorizationHeader
        Long userId = 1L; // Placeholder

        try {
            Pet pet = petService.feedPet(userId, request.getPoint());
            return ResponseEntity.ok(new FeedPetResponse("밥을 먹였습니다.", pet.getLevel(), pet.getCurrentPoint()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/levelup")
    public ResponseEntity<?> levelUpPet(@RequestHeader("Authorization") String authorizationHeader,
                                        @RequestBody LevelUpRequest request) {
        // TODO: Extract userId from JWT token in authorizationHeader
        Long userId = 1L; // Placeholder

        try {
            Pet pet = petService.levelUpPet(userId, request.getExceededexp());
            return ResponseEntity.ok(new LevelUpResponse("레벨업 하였습니다.", pet.getLevel(), pet.getExp()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("레벨업 실패."));
        }
    }
}
