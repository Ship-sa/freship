package me.yeon.freship.store.service;

import lombok.RequiredArgsConstructor;
import me.yeon.freship.common.domain.PageInfo;
import me.yeon.freship.common.domain.Response;
import me.yeon.freship.common.domain.constant.ErrorCode;
import me.yeon.freship.common.exception.ClientException;
import me.yeon.freship.member.domain.AuthMember;
import me.yeon.freship.member.domain.Member;
import me.yeon.freship.member.domain.MemberRole;
import me.yeon.freship.member.infrastructure.MemberRepository;
import me.yeon.freship.store.domain.Store;
import me.yeon.freship.store.domain.StoreRequest;
import me.yeon.freship.store.domain.StoreResponse;
import me.yeon.freship.store.domain.UpdateStoreRequest;
import me.yeon.freship.store.infrastructure.StoreRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Response<StoreResponse> saveStore(AuthMember authMember, StoreRequest request) {
        Member member = memberRepository.findById(authMember.getId())
                .orElseThrow(() -> new ClientException(ErrorCode.NOT_FOUND_MEMBER));

        if (member.getMemberRole() != MemberRole.ROLE_OWNER) {
            throw new ClientException(ErrorCode.NOT_OWNER_AUTHORITY);
        }

        Store store = new Store(
                member,
                request.getName(),
                request.getBizRegNum(),
                request.getAddress()
        );
        storeRepository.save(store);

        return Response.of(StoreResponse.fromEntity(store));
    }

    @Transactional
    public Response<List<StoreResponse>> findStores() {
        return Response.of(storeRepository.findAll().stream()
                .map(StoreResponse::fromEntity)
                .collect(Collectors.toList()));
    }

    @Transactional(readOnly = true)
    public Response<StoreResponse> findStoreById(Long id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new ClientException(ErrorCode.STORE_NOT_FOUND));
        return Response.of(StoreResponse.fromEntity(store));
    }

    @Transactional
    public Response<StoreResponse> updateStore(AuthMember authMember, Long id, UpdateStoreRequest request) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new ClientException(ErrorCode.STORE_NOT_FOUND));

        if (!store.getMember().getId().equals(authMember.getId())) {
            throw new ClientException(ErrorCode.NOT_STORE_OWNER);
        }

        store.update(request.getName(), request.getAddress());
        return Response.of(StoreResponse.fromEntity(store));
    }

    @Transactional
    public void deleteStore(AuthMember authMember, Long id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new ClientException(ErrorCode.STORE_NOT_FOUND));

        if (!store.getMember().getId().equals(authMember.getId())) {
            throw new ClientException(ErrorCode.NOT_STORE_OWNER);
        }

        storeRepository.delete(store);
    }
}
