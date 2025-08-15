package com.tomy.tomy.repository;

import com.tomy.tomy.domain.GreetingLog;
import com.tomy.tomy.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface GreetingLogRepository extends JpaRepository<GreetingLog, Long> {
    Optional<GreetingLog> findByUserAndGreetedDate(User user, LocalDate greetedDate);
}