package com.tomy.tomy.controller;

import com.tomy.tomy.dto.AchievementRewardStatusResponse;
import com.tomy.tomy.dto.ErrorResponse;
import com.tomy.tomy.dto.RedeemAchievementRewardRequest;
import com.tomy.tomy.dto.RewardResponse;
import com.tomy.tomy.dto.GreetingRewardResponse;
import com.tomy.tomy.domain.UserReward;
import com.tomy.tomy.service.RewardService;
import com.tomy.tomy.service.PointService;
import com.tomy.tomy.security.JwtTokenProvider;
import com.tomy.tomy.repository.UserRepository;
import com.tomy.tomy.repository.UserRewardRepository;
import com.tomy.tomy.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Map;

@RestController
@RequestMapping("/api/rewards")
@RequiredArgsConstructor
public class RewardController {

    private final RewardService rewardService;
    private final PointService pointService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final UserRewardRepository userRewardRepository; // Added for checking redemption status

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

    @GetMapping
    public ResponseEntity<?> getAvailableAchievementRewards(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            Long userId = getUserIdFromAuthorization(authorizationHeader);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found."));

            List<AchievementRewardStatusResponse> availableRewards = new ArrayList<>();

            // Define the 4 achievement reward types
            Map<String, Integer> rewardTypes = Map.of(
                    "ATTENDANCE_5000", 5000,
                    "FEEDING_5000", 5000,
                    "RECEIPT_5000", 5000,
                    "ALL_ACHIEVEMENTS_10000", 10000
            );

            for (Map.Entry<String, Integer> entry : rewardTypes.entrySet()) {
                String rewardName = entry.getKey();
                Integer value = entry.getValue();
                boolean redeemed = userRewardRepository.findByUserAndRewardName(user, rewardName).isPresent();
                availableRewards.add(new AchievementRewardStatusResponse(rewardName, value, redeemed));
            }

            return ResponseEntity.ok(availableRewards);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace(); // Log the stack trace for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("사용 가능한 업적 리워드 조회 실패: " + e.getMessage()));
        }
    }

    @PostMapping("/redeemByAchievement")
    public ResponseEntity<?> redeemGifticonByAchievement(@RequestHeader("Authorization") String authorizationHeader,
                                                         @RequestBody RedeemAchievementRewardRequest request) {
        try {
            Long userId = getUserIdFromAuthorization(authorizationHeader);
            UserReward userReward = rewardService.redeemGifticonByAchievement(userId, request.getRewardName());

            return ResponseEntity.ok(Map.of(
                    "rewardName", userReward.getRewardName(),
                    "value", userReward.getValue(),
                    "userRewardId", userReward.getId()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace(); // 서버 로그에 스택 트레이스 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("업적 기반 기프티콘 사용 실패: " + e.getMessage()));
        }
    }

    @PostMapping("/greeting")
    public ResponseEntity<?> greetUser(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            Long userId = getUserIdFromAuthorization(authorizationHeader);
            Long remainingPoint = rewardService.greetUser(userId);
            return ResponseEntity.ok(new GreetingRewardResponse("인사 포인트 보상 완료.", remainingPoint));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("인사 포인트 보상 오류."));
        }
    }
}
