package com.tomy.tomy.repository;

import com.tomy.tomy.domain.UserAchievementStatus;
import com.tomy.tomy.domain.User;
import com.tomy.tomy.domain.AchievementMilestone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAchievementStatusRepository extends JpaRepository<UserAchievementStatus, Long> {
    List<UserAchievementStatus> findByUser(User user);
    Optional<UserAchievementStatus> findByUserAndAchievementMilestone(User user, AchievementMilestone achievementMilestone);
}
