package com.tomy.tomy.controller;

import com.tomy.tomy.dto.AchievementResponse;
import com.tomy.tomy.dto.ErrorResponse;
import com.tomy.tomy.dto.AchievementUpdateRequest;
import com.tomy.tomy.dto.UserAchievementSummaryResponse;
import com.tomy.tomy.service.AchievementService;
import com.tomy.tomy.security.JwtTokenProvider;
import com.tomy.tomy.repository.UserRepository;
import com.tomy.tomy.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/achievements")
@RequiredArgsConstructor
public class AchievementController {

    private final AchievementService achievementService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getUserAchievements(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7); // Remove "Bearer " prefix

        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Invalid or expired token."));
        }

        String username = jwtTokenProvider.getUserIdFromJWT(token);
        Optional<User> userOptional = userRepository.findByUserId(username);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("User not found."));
        }

        Long userId = userOptional.get().getId();

        try {
            UserAchievementSummaryResponse response = achievementService.getUserAchievements(userId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("업적 조회 실패."));
        }
    }

    @PostMapping
    public ResponseEntity<?> updateAchievementProgress(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody AchievementUpdateRequest request) {
        String token = authorizationHeader.substring(7); // Remove "Bearer " prefix

        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Invalid or expired token."));
        }

        String username = jwtTokenProvider.getUserIdFromJWT(token);
        Optional<User> userOptional = userRepository.findByUserId(username);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("User not found."));
        }

        Long userId = userOptional.get().getId();

        try {
            AchievementResponse updatedProgress = achievementService.updateUserAchievementProgress(userId, request.getType());
            return ResponseEntity.ok(updatedProgress);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("업적 진행 상황 업데이트 실패."));
        }
    }
}


