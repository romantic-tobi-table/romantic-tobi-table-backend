package com.tomy.tomy.service;

import com.tomy.tomy.domain.Achievement;
import com.tomy.tomy.domain.AchievementMilestone;
import com.tomy.tomy.domain.User;
import com.tomy.tomy.domain.UserAchievement;
import com.tomy.tomy.domain.UserAchievementStatus;
import com.tomy.tomy.domain.UserProgress;
import com.tomy.tomy.dto.AchievementResponse;
import com.tomy.tomy.dto.AchievementUpdateRequest;
import com.tomy.tomy.dto.UserAchievementSummaryResponse;
import com.tomy.tomy.repository.AchievementRepository;
import com.tomy.tomy.repository.AchievementMilestoneRepository;
import com.tomy.tomy.repository.UserAchievementRepository;
import com.tomy.tomy.repository.UserAchievementStatusRepository;
import com.tomy.tomy.repository.UserProgressRepository;
import com.tomy.tomy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository; // Keep for now, might remove later
    private final UserRepository userRepository;
    private final UserProgressRepository userProgressRepository;
    private final AchievementMilestoneRepository achievementMilestoneRepository;
    private final UserAchievementStatusRepository userAchievementStatusRepository;
    private final PointService pointService;

    @Transactional(readOnly = true)
    public UserAchievementSummaryResponse getUserAchievements(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        UserProgress userProgress = userProgressRepository.findByUserId(userId)
                .orElseGet(() -> new UserProgress(null, user, 0, null, 0, 0, LocalDateTime.now()));

        List<UserAchievementStatus> userAchievementStatuses = userAchievementStatusRepository.findByUser(user);
        Map<String, Boolean> specificAchievements = new HashMap<>();
        for (UserAchievementStatus status : userAchievementStatuses) {
            specificAchievements.put(status.getAchievementMilestone().getName(), status.getIsAchieved());
        }

        return new UserAchievementSummaryResponse(
                userProgress.getAttendanceSequence(),
                userProgress.getFeedingCount(),
                userProgress.getReceiptCount(),
                userProgress.getLastAttendedAt() != null ? userProgress.getLastAttendedAt().toString() : null,
                specificAchievements
        );
    }

    @Transactional
    public AchievementResponse updateUserAchievementProgress(Long userId, AchievementUpdateRequest.AchievementType type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        UserProgress userProgress = userProgressRepository.findByUserId(userId)
                .orElseGet(() -> new UserProgress(null, user, 0, null, 0, 0, LocalDateTime.now()));

        LocalDate today = LocalDate.now();

        switch (type) {
            case ATTENDANCE:
                if (userProgress.getLastAttendedAt() == null || !userProgress.getLastAttendedAt().plusDays(1).isEqual(today)) {
                    // Not consecutive or first attendance
                    userProgress.setAttendanceSequence(1);
                } else {
                    // Consecutive attendance
                    userProgress.setAttendanceSequence(userProgress.getAttendanceSequence() + 1);
                }
                userProgress.setLastAttendedAt(today);
                break;
            case FEEDING:
                userProgress.setFeedingCount(userProgress.getFeedingCount() + 1);
                break;
            case RECEIPT:
                userProgress.setReceiptCount(userProgress.getReceiptCount() + 1);
                break;
            default:
                throw new IllegalArgumentException("Unknown achievement type: " + type);
        }

        userProgress.setLastUpdated(LocalDateTime.now());
        userProgressRepository.save(userProgress);

        // Update specific achievement statuses
        updateSpecificAchievementStatuses(user, userProgress, type);

        return AchievementResponse.from(userProgress);
    }

    private void updateSpecificAchievementStatuses(User user, UserProgress userProgress, AchievementUpdateRequest.AchievementType updatedType) {
        List<AchievementMilestone> milestones = achievementMilestoneRepository.findByType(AchievementMilestone.AchievementType.valueOf(updatedType.name()));

        for (AchievementMilestone milestone : milestones) {
            boolean isMet = false;
            switch (milestone.getType()) {
                case ATTENDANCE:
                    isMet = userProgress.getAttendanceSequence() >= milestone.getMilestoneValue();
                    break;
                case FEEDING:
                    isMet = userProgress.getFeedingCount() >= milestone.getMilestoneValue();
                    break;
                case RECEIPT:
                    isMet = userProgress.getReceiptCount() >= milestone.getMilestoneValue();
                    break;
            }

            if (isMet) {
                Optional<UserAchievementStatus> existingStatus = userAchievementStatusRepository.findByUserAndAchievementMilestone(user, milestone);
                if (existingStatus.isEmpty() || !existingStatus.get().getIsAchieved()) {
                    UserAchievementStatus status = existingStatus.orElseGet(UserAchievementStatus::new);
                    status.setUser(user);
                    status.setAchievementMilestone(milestone);
                    status.setIsAchieved(true);
                    userAchievementStatusRepository.save(status);

                    // Award points for achieving the milestone
                    awardPointsForMilestone(user, milestone);
                }
            }
        }
    }

    private void awardPointsForMilestone(User user, AchievementMilestone milestone) {
        long pointsToAward = 0;
        System.out.println("Attempting to award points for milestone: " + milestone.getName()); // Debug log
        switch (milestone.getName()) {
            // Consecutive Attendance
            case "3일 연속 출석":
                pointsToAward = 50;
                break;
            case "7일 연속 출석":
                pointsToAward = 100;
                break;
            case "15일 연속 출석":
                pointsToAward = 150;
                break;
            case "30일 연속 출석":
                pointsToAward = 200;
                break;
            // Feeding
            case "밥 주기 5번":
                pointsToAward = 50;
                break;
            case "밥 주기 10번":
                pointsToAward = 100;
                break;
            case "밥 주기 15번":
                pointsToAward = 150;
                break;
            // Receipt Verification
            case "영수증 인증 5번":
                pointsToAward = 50;
                break;
            case "영수증 인증 10번":
                pointsToAward = 100;
                break;
            case "영수증 인증 15번":
                pointsToAward = 150;
                break;
        }

        System.out.println("Points to award for " + milestone.getName() + ": " + pointsToAward); // Debug log

        if (pointsToAward > 0) {
            pointService.earnPoints(user, pointsToAward, com.tomy.tomy.enums.PointTransactionType.ACHIEVEMENT_REWARD, "Achievement: " + milestone.getName(), milestone.getId());
            System.out.println("Points awarded successfully for: " + milestone.getName()); // Debug log
        } else {
            System.out.println("No points awarded for: " + milestone.getName() + " (pointsToAward was 0)"); // Debug log
        }
    }

    // The old recordAchievementProgress method is no longer needed for the new achievement system
    // It can be removed or refactored if still used elsewhere.
    @Transactional
    public UserAchievement recordAchievementProgress(Long userId, String achievementCode, int progressIncrease) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        Achievement achievement = achievementRepository.findByCode(achievementCode)
                .orElseThrow(() -> new IllegalArgumentException("Achievement not found with code: " + achievementCode));

        Optional<UserAchievement> existingUserAchievement = userAchievementRepository.findByUserAndAchievement(user, achievement);

        UserAchievement userAchievement;
        if (existingUserAchievement.isPresent()) {
            userAchievement = existingUserAchievement.get();
            userAchievement.setProgress(userAchievement.getProgress() + progressIncrease);
        } else {
            userAchievement = new UserAchievement();
            userAchievement.setUser(user);
            userAchievement.setAchievement(achievement);
            userAchievement.setProgress(progressIncrease);
        }
        userAchievement.setLastUpdatedAt(LocalDateTime.now());
        return userAchievementRepository.save(userAchievement);
    }

    @Transactional(readOnly = true)
    public boolean areAllAttendanceAchievementsCompleted(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        List<AchievementMilestone> attendanceMilestones = achievementMilestoneRepository.findByType(AchievementMilestone.AchievementType.ATTENDANCE);
        for (AchievementMilestone milestone : attendanceMilestones) {
            Optional<UserAchievementStatus> status = userAchievementStatusRepository.findByUserAndAchievementMilestone(user, milestone);
            if (status.isEmpty() || !status.get().getIsAchieved()) {
                return false;
            }
        }
        return true;
    }

    @Transactional(readOnly = true)
    public boolean areAllFeedingAchievementsCompleted(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        List<AchievementMilestone> feedingMilestones = achievementMilestoneRepository.findByType(AchievementMilestone.AchievementType.FEEDING);
        for (AchievementMilestone milestone : feedingMilestones) {
            Optional<UserAchievementStatus> status = userAchievementStatusRepository.findByUserAndAchievementMilestone(user, milestone);
            if (status.isEmpty() || !status.get().getIsAchieved()) {
                return false;
            }
        }
        return true;
    }

    @Transactional(readOnly = true)
    public boolean areAllReceiptAchievementsCompleted(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        List<AchievementMilestone> receiptMilestones = achievementMilestoneRepository.findByType(AchievementMilestone.AchievementType.RECEIPT);
        for (AchievementMilestone milestone : receiptMilestones) {
            Optional<UserAchievementStatus> status = userAchievementStatusRepository.findByUserAndAchievementMilestone(user, milestone);
            if (status.isEmpty() || !status.get().getIsAchieved()) {
                return false;
            }
        }
        return true;
    }

    @Transactional(readOnly = true)
    public boolean areAllAchievementsCompleted(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        List<AchievementMilestone> allMilestones = achievementMilestoneRepository.findAll(); // Get all milestones
        for (AchievementMilestone milestone : allMilestones) {
            Optional<UserAchievementStatus> status = userAchievementStatusRepository.findByUserAndAchievementMilestone(user, milestone);
            if (status.isEmpty() || !status.get().getIsAchieved()) {
                return false;
            }
        }
        return true;
    }
}
