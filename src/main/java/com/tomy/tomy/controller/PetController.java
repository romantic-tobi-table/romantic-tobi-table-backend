package com.tomy.tomy.controller;

import com.tomy.tomy.dto.*;
import com.tomy.tomy.domain.Pet;
import com.tomy.tomy.service.PetService;
import com.tomy.tomy.service.PointService;
import com.tomy.tomy.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pet")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;
    private final PointService pointService;
    private final JwtTokenProvider jwtTokenProvider; // New dependency

    @GetMapping
    public ResponseEntity<?> getPetInfo(@RequestHeader("Authorization") String authorizationHeader) {
        String userId = getUserIdFromAuthorizationHeader(authorizationHeader); // Changed to String

        try {
            Pet pet = petService.getPetInfo(userId); // Pass String userId
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
        String userId = getUserIdFromAuthorizationHeader(authorizationHeader); // Changed to String

        try {
            Pet pet = petService.feedPet(userId, request.getPoint()); // Pass String userId
            return ResponseEntity.ok(new FeedPetResponse("밥을 먹였습니다.", pet.getLevel(), pet.getCurrentPoint()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/levelup")
    public ResponseEntity<?> levelUpPet(@RequestHeader("Authorization") String authorizationHeader,
                                        @RequestBody LevelUpRequest request) {
        String userId = getUserIdFromAuthorizationHeader(authorizationHeader); // Changed to String

        try {
            Pet pet = petService.levelUpPet(userId, request.getExceededexp()); // Pass String userId
            return ResponseEntity.ok(new LevelUpResponse("레벨업 하였습니다.", pet.getLevel(), pet.getExp()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }

    // Helper method to extract userId (String) from JWT token
    private String getUserIdFromAuthorizationHeader(String authorizationHeader) { // Changed return type to String
        String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
        return jwtTokenProvider.getUserIdFromJWT(token); // Returns String userId
    }
}
