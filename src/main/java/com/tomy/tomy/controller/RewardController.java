package com.tomy.tomy.controller;

import com.tomy.tomy.dto.ErrorResponse;
import com.tomy.tomy.dto.RedeemRewardRequest;
import com.tomy.tomy.dto.RedeemRewardResponse;
import com.tomy.tomy.dto.RewardResponse;
import com.tomy.tomy.dto.GreetingRewardResponse;
import com.tomy.tomy.domain.UserReward;
import com.tomy.tomy.service.RewardService;
import com.tomy.tomy.service.PointService;
import com.tomy.tomy.security.JwtTokenProvider;
import com.tomy.tomy.repository.UserRepository;
import com.tomy.tomy.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> getUserRewards(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            Long userId = getUserIdFromAuthorization(authorizationHeader);
            List<UserReward> userRewards = rewardService.getUserRewards(userId);
            List<RewardResponse> response = userRewards.stream()
                    .map(ur -> new RewardResponse(ur.getReward().getId(), ur.getReward().getRewardName(), ur.getReward().getCostPoint(), ur.getUsed()))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("리워드 목록 조회 실패."));
        }
    }

    @PostMapping("/{rewardId}/redeem")
    public ResponseEntity<?> redeemReward(@RequestHeader("Authorization") String authorizationHeader,
                                          @PathVariable Long rewardId,
                                          @RequestBody(required = false) RedeemRewardRequest request) {
        try {
            Long userId = getUserIdFromAuthorization(authorizationHeader);
            UserReward userReward = rewardService.redeemReward(userId, rewardId);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found."));
            int remainingPoint = pointService.getCurrentPoints(user);

            return ResponseEntity.ok(Map.of(
                    "id", userReward.getReward().getId(),                // 리워드 마스터 ID (6001 등)
                    "rewardName", userReward.getReward().getRewardName(),
                    "point", userReward.getReward().getCostPoint(),
                    "used", userReward.getUsed(),
                    "remainingPoint", remainingPoint,                    // 잔액
                    "userRewardId", userReward.getId()                   // 발급된 내 리워드 PK
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("리워드 사용 실패."));
        }
    }

    @PostMapping("/greeting")
    public ResponseEntity<?> greetUser(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            Long userId = getUserIdFromAuthorization(authorizationHeader);
            int remainingPoint = rewardService.greetUser(userId);
            return ResponseEntity.ok(new GreetingRewardResponse("인사 포인트 보상 완료.", remainingPoint));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("인사 포인트 보상 오류."));
        }
    }
}
