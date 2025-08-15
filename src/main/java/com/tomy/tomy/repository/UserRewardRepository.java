package com.tomy.tomy.repository;

import com.tomy.tomy.domain.Reward;
import com.tomy.tomy.domain.User;
import com.tomy.tomy.domain.UserReward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRewardRepository extends JpaRepository<UserReward, Long> {
    List<UserReward> findByUser(User user);
    Optional<UserReward> findByUserAndReward(User user, Reward reward);
}