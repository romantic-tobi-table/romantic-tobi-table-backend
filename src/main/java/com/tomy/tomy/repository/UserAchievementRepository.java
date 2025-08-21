package com.tomy.tomy.repository;

import com.tomy.tomy.domain.Achievement;
import com.tomy.tomy.domain.User;
import com.tomy.tomy.domain.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {
    List<UserAchievement> findByUser(User user);
    Optional<UserAchievement> findByUserAndAchievement(User user, Achievement achievement);
    Optional<UserAchievement> findTopByUserOrderByLastUpdatedAtDesc(User user);
}