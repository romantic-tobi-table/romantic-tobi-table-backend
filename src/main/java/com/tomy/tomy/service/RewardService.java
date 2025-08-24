package com.tomy.tomy.service;

import com.tomy.tomy.domain.Reward;
import com.tomy.tomy.domain.User;
import com.tomy.tomy.domain.UserReward;
import com.tomy.tomy.domain.GreetingLog;
import com.tomy.tomy.dto.AchievementUpdateRequest;
import com.tomy.tomy.enums.PointTransactionType;
import com.tomy.tomy.repository.RewardRepository;
import com.tomy.tomy.repository.UserRepository;
import com.tomy.tomy.repository.UserRewardRepository;
import com.tomy.tomy.repository.GreetingLogRepository;
import com.tomy.tomy.repository.UserAchievementStatusRepository;
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
    private final AchievementService achievementService;
    private final UserAchievementStatusRepository userAchievementStatusRepository;

    @Transactional(readOnly = true)
    public List<UserReward> getUserRewards(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        return userRewardRepository.findByUser(user);
    }

    @Transactional
    public UserReward redeemGifticonByAchievement(Long userId, String rewardName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        // Check achievement status
        boolean canRedeem = false;
        Long rewardValue = 0L;
        String achievementType = ""; // Not strictly needed for redemption, but good for logging/tracking

        switch (rewardName) {
            case "ATTENDANCE_5000":
                // Check if all 4 attendance achievements are completed
                if (achievementService.areAllAttendanceAchievementsCompleted(user.getId())) {
                    canRedeem = true;
                    rewardValue = 5000L;
                    achievementType = "ATTENDANCE";
                }
                break;
            case "FEEDING_5000":
                // Check if all 3 feeding achievements are completed
                if (achievementService.areAllFeedingAchievementsCompleted(user.getId())) {
                    canRedeem = true;
                    rewardValue = 5000L;
                    achievementType = "FEEDING";
                }
                break;
            case "RECEIPT_5000":
                // Check if all 3 receipt achievements are completed
                if (achievementService.areAllReceiptAchievementsCompleted(user.getId())) {
                    canRedeem = true;
                    rewardValue = 5000L;
                    achievementType = "RECEIPT";
                }
                break;
            case "ALL_ACHIEVEMENTS_10000":
                // Check if all achievements are completed
                if (achievementService.areAllAchievementsCompleted(user.getId())) {
                    canRedeem = true;
                    rewardValue = 10000L;
                    achievementType = "ALL";
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid reward name: " + rewardName);
        }

        if (!canRedeem) {
            throw new IllegalArgumentException("업적 달성 조건이 충족되지 않았습니다.");
        }

        // Prevent duplicate redemption for the same achievement type
        Optional<UserReward> existingUserReward = userRewardRepository.findByUserAndRewardName(user, rewardName);
        if (existingUserReward.isPresent()) {
            throw new IllegalArgumentException("이미 해당 업적으로 기프티콘을 받으셨습니다.");
        }

        UserReward userReward = new UserReward();
        userReward.setUser(user);
        userReward.setReward(null); // For achievement-based rewards, no direct Reward entity
        userReward.setRewardName(rewardName); // Store the reward name directly
        userReward.setValue(rewardValue.intValue()); // Set the value of the gifticon
        userReward.setUsed(false); // Not used yet, just issued
        userReward.setIssuedAt(LocalDateTime.now());
        userReward.setCode("GENERATED_GIFTICON_CODE_FOR_" + rewardName); // Generate a unique code

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

        // Award greeting points (e.g., 10 points)
        int greetingPoints = 10;
        pointService.earnPoints(user, greetingPoints, PointTransactionType.GREETING_REWARD, "Daily greeting", null);

        GreetingLog greetingLog = new GreetingLog();
        greetingLog.setUser(user);
        greetingLog.setGreetedDate(today);
        greetingLog.setPointAwarded(greetingPoints);
        greetingLog.setCreatedAt(LocalDateTime.now());
        greetingLogRepository.save(greetingLog);
        achievementService.updateUserAchievementProgress(userId, AchievementUpdateRequest.AchievementType.ATTENDANCE);


        return pointService.getCurrentPoints(user);
    }
}
