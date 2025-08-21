package com.tomy.tomy.controller;

import com.tomy.tomy.dto.ErrorResponse;
import com.tomy.tomy.dto.StoreResponse;
import com.tomy.tomy.dto.StoreSearchResponse;
import com.tomy.tomy.domain.Store;
import com.tomy.tomy.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @GetMapping
    public ResponseEntity<Page<StoreResponse>> getAllStores(@ParameterObject @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Store> stores = storeService.getAllStores(pageable);
        Page<StoreResponse> response = stores.map(store -> new StoreResponse(store.getId(), store.getName(), store.getAddress()));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStoreById(@PathVariable Long id) {
        Optional<Store> store = storeService.getStoreById(id);
        if (store.isPresent()) {
            Store s = store.get();
            return ResponseEntity.ok(new StoreResponse(s.getId(), s.getName(), s.getAddress()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("해당 ID의 가게가 존재하지 않습니다."));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Page<StoreSearchResponse>> searchStoresByName(@RequestParam String name, @ParameterObject @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Store> stores = storeService.searchStoresByName(name, pageable);
        Page<StoreSearchResponse> response = stores.map(store -> new StoreSearchResponse(store.getId(), store.getName(), store.getAddress()));
        return ResponseEntity.ok(response);
    }
}
