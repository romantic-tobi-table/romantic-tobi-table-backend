package com.tomy.tomy.controller;

import com.tomy.tomy.dto.*;
import com.tomy.tomy.domain.Clothes;
import com.tomy.tomy.domain.UserClothes;
import com.tomy.tomy.service.ClothesService;
import com.tomy.tomy.security.JwtTokenProvider;
import com.tomy.tomy.repository.UserRepository;
import com.tomy.tomy.domain.User;
import com.tomy.tomy.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clothes")
@RequiredArgsConstructor
public class ClothesController {

    private final ClothesService clothesService;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PointService pointService;


    @GetMapping
    public ResponseEntity<List<ClothesResponse>> getAllClothes() {
        List<Clothes> clothes = clothesService.getAllClothes();
        List<ClothesResponse> response = clothes.stream()
                .map(c -> new ClothesResponse(c.getId(), c.getName(), c.getPrice(), c.getCategory(), c.getImageUrl(), c.getDescription()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getClothesById(@PathVariable Long id) {
        Optional<Clothes> clothes = clothesService.getClothesById(id);
        if (clothes.isPresent()) {
            Clothes c = clothes.get();
            return ResponseEntity.ok(new ClothesResponse(c.getId(), c.getName(), c.getPrice(), c.getCategory(), c.getImageUrl(), c.getDescription()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("옷을 찾을 수 없습니다."));
        }
    }

    @PostMapping("/pet/dress/{id}")
    public ResponseEntity<?> purchaseAndEquipClothes(@RequestHeader("Authorization") String authorizationHeader,
                                                     @PathVariable("id") Long clothesId) {
        // TODO: Extract userId from JWT token in authorizationHeader

        try {
            Long userId = getUserIdFromAuthorization(authorizationHeader);

            UserClothes result = clothesService.purchaseAndEquipClothes(userId, clothesId);

            // TODO: Get remaining points from PointService
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found."));
            int remainingPoint = pointService.getCurrentPoints(user);

            return ResponseEntity.ok(new PurchaseEquipClothesResponse("옷을 구매하고 착용했습니다.", remainingPoint));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("옷 착용 실패."));
        }
    }

    @PostMapping("/pet/undress/{id}")
    public ResponseEntity<?> unequipClothes(@RequestHeader("Authorization") String authorizationHeader,
                                            @PathVariable("id") Long clothesId) {
        // TODO: Extract userId from JWT token in authorizationHeader
        Long userId = getUserIdFromAuthorization(authorizationHeader);


        try {
            UserClothes userClothes = clothesService.unequipClothes(userId, clothesId);
            return ResponseEntity.ok(new AuthResponse("착용 해제 완료")); // Reusing AuthResponse for message
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("옷 해제 실패."));
        }
    }

    @GetMapping("/pet/appearance")
    public ResponseEntity<?> getEquippedClothes(@RequestHeader("Authorization") String authorizationHeader) {
        // TODO: Extract userId from JWT token in authorizationHeader
        Long userId = getUserIdFromAuthorization(authorizationHeader);


        try {
            List<UserClothes> equippedClothes = clothesService.getEquippedClothes(userId);
            List<EquippedClothesResponse> response = equippedClothes.stream()
                    .map(uc -> new EquippedClothesResponse(uc.getClothes().getId(), uc.getClothes().getName(), uc.getClothes().getCategory(), uc.getClothes().getImageUrl()))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }

    private Long getUserIdFromAuthorization(String authorizationHeader) {
        String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
        if (!jwtTokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("Invalid or expired token.");
        }
        String username = jwtTokenProvider.getUserIdFromJWT(token);
        User user = userRepository.findByUserId(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        return user.getId();
    }
}
