package me.yeon.freship.product.service;

import lombok.RequiredArgsConstructor;
import me.yeon.freship.common.domain.PageInfo;
import me.yeon.freship.common.domain.Response;
import me.yeon.freship.common.domain.constant.ErrorCode;
import me.yeon.freship.common.exception.ClientException;
import me.yeon.freship.member.domain.AuthMember;
import me.yeon.freship.member.domain.Member;
import me.yeon.freship.member.domain.MemberRole;
import me.yeon.freship.member.infrastructure.MemberRepository;
import me.yeon.freship.product.domain.Category;
import me.yeon.freship.product.domain.Product;
import me.yeon.freship.product.domain.ProductRequest;
import me.yeon.freship.product.domain.ProductResponse;
import me.yeon.freship.product.infrastructure.ProductRepository;
import me.yeon.freship.store.domain.Store;
import me.yeon.freship.store.infrastructure.StoreRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public ProductResponse saveProduct(AuthMember authMember, Long storeId, ProductRequest request) {
        Member member = findMemberByAuthMemberId(authMember);
        if (!member.getId().equals(storeId)) {
            throw new ClientException(ErrorCode.NOT_STORE_OWNER);
        }

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ClientException(ErrorCode.STORE_NOT_FOUND));

        Product product = new Product(
                store,
                request.getName(),
                request.getQuantity(),
                request.getStatus(),
                request.getCategory(),
                request.getPrice(),
                request.getImgUrl(),
                request.getDescription()
        );
        productRepository.save(product);

        return ProductResponse.fromEntity(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> findProducts(Category category, PageInfo pageInfo) {
        Pageable pageable = PageRequest.of(pageInfo.getPageNum(), pageInfo.getPageSize());

        Page<Product> products;
        if (category == null) {
            products = productRepository.findAll(pageable);
        } else {
            products = productRepository.findByCategory(category, pageable);
        }

        return products.map(ProductResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public ProductResponse findProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ClientException(ErrorCode.PRODUCT_NOT_FOUND));

        return ProductResponse.fromEntity(product);
    }

    @Transactional
    public ProductResponse updateProduct(AuthMember authMember, Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ClientException(ErrorCode.PRODUCT_NOT_FOUND));

        Member member = findMemberByAuthMemberId(authMember);
        if (!member.getId().equals(product.getStore().getMember().getId())) {
            throw new ClientException(ErrorCode.NOT_STORE_OWNER);
        }

        product.update(
                request.getName(),
                request.getQuantity(),
                request.getStatus(),
                request.getCategory(),
                request.getPrice(),
                request.getImgUrl(),
                request.getDescription()
        );

        return ProductResponse.fromEntity(product);
    }

    @Transactional
    public void deleteProduct(AuthMember authMember, Long id) {
        Member member = findMemberByAuthMemberId(authMember);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ClientException(ErrorCode.PRODUCT_NOT_FOUND));

        if (!member.getId().equals(product.getStore().getMember().getId())) {
            throw new ClientException(ErrorCode.NOT_STORE_OWNER);
        }

        productRepository.delete(product);
    }

    private Member findMemberByAuthMemberId(AuthMember authMember) {
        return memberRepository.findById(authMember.getId()).orElseThrow(() -> new ClientException(ErrorCode.NOT_FOUND_MEMBER));
    }
}
