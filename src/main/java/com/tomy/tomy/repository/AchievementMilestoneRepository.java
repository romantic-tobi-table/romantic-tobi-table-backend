package com.tomy.tomy.repository;

import com.tomy.tomy.domain.AchievementMilestone;
import com.tomy.tomy.domain.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AchievementMilestoneRepository extends JpaRepository<AchievementMilestone, Long> {
    List<AchievementMilestone> findByAchievement(Achievement achievement);
    List<AchievementMilestone> findByType(AchievementMilestone.AchievementType type);
}
