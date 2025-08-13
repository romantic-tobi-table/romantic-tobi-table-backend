package com.tomy.tomy.repository;

import com.tomy.tomy.domain.UserProgress;
import com.tomy.tomy.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {
    Optional<UserProgress> findByUser(User user);
}
