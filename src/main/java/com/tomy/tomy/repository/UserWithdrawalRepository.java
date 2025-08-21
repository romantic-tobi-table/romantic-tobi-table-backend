package com.tomy.tomy.repository;

import com.tomy.tomy.domain.UserWithdrawal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserWithdrawalRepository extends JpaRepository<UserWithdrawal, Long> {
}
