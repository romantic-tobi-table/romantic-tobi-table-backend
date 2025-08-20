package com.tomy.tomy.service;

import com.tomy.tomy.domain.Reward;
import com.tomy.tomy.domain.User;
import com.tomy.tomy.domain.UserReward;
import com.tomy.tomy.domain.GreetingLog;
import com.tomy.tomy.enums.PointTransactionType;
import com.tomy.tomy.repository.RewardRepository;
import com.tomy.tomy.repository.UserRepository;
import com.tomy.tomy.repository.UserRewardRepository;
import com.tomy.tomy.repository.GreetingLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class RewardService {

    private final RewardRepository rewardRepository;
    private final UserRewardRepository userRewardRepository;
    private final UserRepository userRepository;
    private final PointService pointService;
    private final GreetingLogRepository greetingLogRepository;

    @Transactional(readOnly = true)
    public List<UserReward> getUserRewards(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        return userRewardRepository.findByUser(user);
    }

    @Transactional
    public UserReward redeemReward(Long userId, Long rewardId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new IllegalArgumentException("Reward not found."));

        // Check if reward is active and in stock (if applicable)
        if (!reward.getIsActive()) {
            throw new IllegalArgumentException("비활성화된 리워드입니다.");
        }
        if (reward.getStock() != null && reward.getStock() <= 0) {
            throw new IllegalArgumentException("리워드 재고가 부족합니다.");
        }

//        // Check if user already redeemed this specific reward and it's not reusable
//        Optional<UserReward> existingUserReward = userRewardRepository.findByUserAndReward(user, reward);
//        if (existingUserReward.isPresent() && existingUserReward.get().getUsed()) {
//            throw new IllegalArgumentException("이미 사용한 리워드입니다.");
//        }

        // Spend points
        pointService.spendPoints(user, reward.getCostPoint(), PointTransactionType.REWARD_REDEEM, "Reward redemption", reward.getId());

        // Decrease stock if applicable
        if (reward.getStock() != null) {
            reward.setStock(reward.getStock() - 1);
            rewardRepository.save(reward);
        }

        UserReward userReward = new UserReward();
        userReward.setUser(user);
        userReward.setReward(reward);
        userReward.setUsed(true); // Mark as used upon redemption
        userReward.setIssuedAt(LocalDateTime.now());
        userReward.setUsedAt(LocalDateTime.now()); // Mark used_at as now

        // TODO: Generate actual gifticon code if applicable
        userReward.setCode("GENERATED_GIFTICON_CODE");

        return userRewardRepository.save(userReward);
    }

    @Transactional
    public Long greetUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        Optional<GreetingLog> existingGreeting = greetingLogRepository.findByUserAndGreetedDate(user, today);

        if (existingGreeting.isPresent()) {
            throw new IllegalArgumentException("이미 인사를 하였습니다.");
        }

        // Award greeting points (e.g., 100 points)
        int greetingPoints = 100;
        pointService.earnPoints(user, greetingPoints, PointTransactionType.GREETING_REWARD, "Daily greeting", null);

        GreetingLog greetingLog = new GreetingLog();
        greetingLog.setUser(user);
        greetingLog.setGreetedDate(today);
        greetingLog.setPointAwarded(greetingPoints);
        greetingLog.setCreatedAt(LocalDateTime.now());
        greetingLogRepository.save(greetingLog);

        return pointService.getCurrentPoints(user);
    }
}