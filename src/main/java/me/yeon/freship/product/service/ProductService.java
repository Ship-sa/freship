package me.yeon.freship.product.service;

import lombok.RequiredArgsConstructor;
import me.yeon.freship.common.domain.PageInfo;
import me.yeon.freship.common.domain.Response;
import me.yeon.freship.common.domain.constant.ErrorCode;
import me.yeon.freship.common.exception.ClientException;
import me.yeon.freship.member.domain.AuthMember;
import me.yeon.freship.member.domain.Member;
import me.yeon.freship.common.utils.RedisUtils;
import me.yeon.freship.product.domain.ProductSearchResponse;
import org.springframework.cache.annotation.Cacheable;
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

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final MemberRepository memberRepository;
    private final RedisUtils redisUtils;

    @Transactional
    public Response<ProductResponse> saveProduct(AuthMember authMember, Long storeId, ProductRequest request) {
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

        return Response.of(ProductResponse.fromEntity(product));
    }

    @Transactional
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

    public Response<ProductResponse> findProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ClientException(ErrorCode.PRODUCT_NOT_FOUND));

        return Response.of(ProductResponse.fromEntity(product));
    }

    @Transactional
    public Response<ProductResponse> updateProduct(AuthMember authMember, Long id, ProductRequest request) {
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

        return Response.of(ProductResponse.fromEntity(product));
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

    // 캐싱 적용하지 않은 검색 (v1)
    public List<ProductSearchResponse> searchProductsWithoutCache(String name, int pageNum, int pageSize, Long userId) {
        return searchProducts(name, pageNum, pageSize, userId);
    }

    // 캐싱 적용한 검색 (v2)
    @Cacheable(cacheNames = "search", key = "#name + 'page:' + #pageNum + 'size:' + #pageSize")
    public List<ProductSearchResponse> searchProductsWithCache(String name, int pageNum, int pageSize, Long userId) {
        return searchProducts(name, pageNum, pageSize, userId);
    }

    // 인기 검색어 조회
    public List<String> findProductsByPopularSearch() {
        return redisUtils.getTopSearchKeywords();
    }

    private List<ProductSearchResponse> searchProducts(String productName, int pageNum, int pageSize, Long userId) {

        memberRepository.findById(userId).orElseThrow(() -> new ClientException(ErrorCode.NOT_FOUND_MEMBER));

        redisUtils.saveSearchHistory(userId, productName);

        Page<Product> productPage = getProductPage(productName, pageNum, pageSize);
        PageInfo pageInfo = getPageInfo(productName, pageNum, pageSize);

        return productPage.getContent()
                .stream()
                .map(product -> ProductSearchResponse.fromEntity(product, pageInfo))
                .toList();
    }

    private PageInfo getPageInfo(String productName, int pageNum, int pageSize) {
        Page<Product> productPage = getProductPage(productName, pageNum, pageSize);

        return PageInfo.builder()
                .pageNum(pageNum)
                .pageSize(pageSize)
                .totalElement(productPage.getTotalElements())
                .totalPage(productPage.getTotalPages())
                .build();
    }

    private Page<Product> getProductPage(String productName, int pageNum, int pageSize) {
        PageRequest pageable = PageRequest.of(pageNum - 1, pageSize);
        return productRepository.searchByName(pageable, productName);
    }

    private Member findMemberByAuthMemberId(AuthMember authMember) {
        return memberRepository.findById(authMember.getId()).orElseThrow(() -> new ClientException(ErrorCode.NOT_FOUND_MEMBER));
    }
}
