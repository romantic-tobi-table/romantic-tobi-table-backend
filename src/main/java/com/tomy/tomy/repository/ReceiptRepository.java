package com.tomy.tomy.repository;

import com.tomy.tomy.domain.Receipt;
import com.tomy.tomy.domain.Store;
import com.tomy.tomy.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    Optional<Receipt> findByUserAndStoreAndRecognizedDate(User user, Store store, LocalDate recognizedDate);
    List<Receipt> findByUser(User user);
}