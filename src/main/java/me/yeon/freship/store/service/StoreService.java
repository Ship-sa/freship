package me.yeon.freship.store.service;

import lombok.RequiredArgsConstructor;
import me.yeon.freship.common.domain.Response;
import me.yeon.freship.common.domain.constant.ErrorCode;
import me.yeon.freship.common.exception.ClientException;
import me.yeon.freship.member.domain.Member;
import me.yeon.freship.member.infrastructure.MemberRepository;
import me.yeon.freship.store.domain.Store;
import me.yeon.freship.store.domain.StoreRequest;
import me.yeon.freship.store.domain.StoreResponse;
import me.yeon.freship.store.domain.UpdateStoreRequest;
import me.yeon.freship.store.infrastructure.StoreRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Response<StoreResponse> saveStore(StoreRequest request) {
        // member 존재여부, 권한 체크 로직 추가 필요
        Member member = memberRepository.findById(2L)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

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
    public Response<Page<StoreResponse>> findStores() {
        // 가게 리스트 조회 Response를 따로 두는게 좋겠지 싶어서 일단 비워뒀읍니당
        return null;
    }

    @Transactional(readOnly = true)
    public Response<StoreResponse> findStoreById(Long id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new ClientException(ErrorCode.STORE_NOT_FOUND));
        return Response.of(StoreResponse.fromEntity(store));
    }

    @Transactional
    public Response<StoreResponse> updateStore(Long id, UpdateStoreRequest request) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new ClientException(ErrorCode.STORE_NOT_FOUND));
        store.update(request.getName(), request.getAddress());
        return Response.of(StoreResponse.fromEntity(store));
    }

    @Transactional
    public void deleteStore(Long id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new ClientException(ErrorCode.STORE_NOT_FOUND));
        // 권한 체크 로직 필요
        // 또한 가게를 soft delete할 것인지, hard delete할 것인지? <- 일단 OneToOne이라 hard delete로 구현
        storeRepository.delete(store);
    }
}
