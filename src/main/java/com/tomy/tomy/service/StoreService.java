package com.tomy.tomy.service;

import com.tomy.tomy.domain.Store;
import com.tomy.tomy.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;

    @Transactional(readOnly = true)
    public Page<Store> getAllStores(Pageable pageable) {
        return storeRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Store> getStoreById(Long id) {
        return storeRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Store> searchStoresByName(String name) {
        return storeRepository.findByNameContainingIgnoreCase(name);
    }
}
