package me.yeon.freship.store.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.yeon.freship.common.domain.PageInfo;
import me.yeon.freship.common.domain.Response;
import me.yeon.freship.member.domain.AuthMember;
import me.yeon.freship.store.domain.StoreRequest;
import me.yeon.freship.store.domain.StoreResponse;
import me.yeon.freship.store.domain.UpdateStoreRequest;
import me.yeon.freship.store.service.StoreService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stores")
public class StoreController {

    private final StoreService storeService;

    @PostMapping
    public ResponseEntity<Response<StoreResponse>> saveStore(
            @AuthenticationPrincipal AuthMember authMember,
            @Valid @RequestBody StoreRequest request
    ) {
        Response<StoreResponse> response = storeService.saveStore(authMember, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Response<List<StoreResponse>>> findStores() {
        return ResponseEntity.ok(storeService.findStores());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<StoreResponse>> findStoreById(@PathVariable Long id) {
        return ResponseEntity.ok(storeService.findStoreById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response<StoreResponse>> updateStore(
            @AuthenticationPrincipal AuthMember authMember,
            @PathVariable Long id,
            @RequestBody UpdateStoreRequest request
    ) {
        return ResponseEntity.ok(storeService.updateStore(authMember, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStore(
            @AuthenticationPrincipal AuthMember authMember,
            @PathVariable Long id
    ) {
        storeService.deleteStore(authMember, id);
        return ResponseEntity.noContent().build();
    }
}
