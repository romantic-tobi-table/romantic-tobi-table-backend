package com.tomy.tomy.repository;

import com.tomy.tomy.domain.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    Page<Store> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Optional<Store> findByName(String name);

}
