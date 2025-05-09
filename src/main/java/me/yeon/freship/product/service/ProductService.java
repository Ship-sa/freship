package me.yeon.freship.product.service;

import lombok.RequiredArgsConstructor;
import me.yeon.freship.common.domain.constant.ErrorCode;
import me.yeon.freship.common.exception.ClientException;
import me.yeon.freship.member.domain.AuthMember;
import me.yeon.freship.member.domain.Member;
import me.yeon.freship.member.infrastructure.MemberRepository;
import me.yeon.freship.product.domain.*;
import me.yeon.freship.product.infrastructure.ProductRepository;
import me.yeon.freship.store.domain.Store;
import me.yeon.freship.store.infrastructure.StoreRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final MemberRepository memberRepository;
    private final ProductRedisUtils productRedisUtils;
    private final ImgService imgService;

    @Transactional
    public ProductResponse saveProduct(AuthMember authMember, Long storeId, ProductRequest request) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ClientException(ErrorCode.STORE_NOT_FOUND));

        Member member = findMemberByAuthMemberId(authMember);
        if (!member.getId().equals(store.getMember().getId())) {
            throw new ClientException(ErrorCode.NOT_STORE_OWNER);
        }

        if (productRepository.existsByStoreAndName(store, request.getName())) {
            throw new ClientException(ErrorCode.PRODUCT_NAME_ALREADY_EXISTS);
        }

        Product product = new Product(
                store,
                request.getName(),
                request.getQuantity(),
                request.getStatus(),
                request.getCategory(),
                request.getPrice(),
                request.getDescription()
        );
        productRepository.save(product);

        return ProductResponse.fromEntity(product);
    }

    @Transactional
    public String uploadProductImage(AuthMember authMember, Long id, MultipartFile imgFile) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ClientException(ErrorCode.PRODUCT_NOT_FOUND));

        Member member = findMemberByAuthMemberId(authMember);
        if (!member.getId().equals(product.getStore().getMember().getId())) {
            throw new ClientException(ErrorCode.NOT_STORE_OWNER);
        }

        String imgUrl = imgService.saveFile(imgFile);
        product.updateImageUrl(imgUrl);
        return imgUrl;
    }

    public Page<ProductResponse> findProducts(Category category, int pageNum, int pageSize) {
        Pageable pageRequest = PageRequest.of(pageNum - 1, pageSize);

        Page<Product> products = (category == null)
                ? productRepository.findAll(pageRequest)
                : productRepository.findByCategory(category, pageRequest);

        return products.map(ProductResponse::fromEntity);
    }

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

        if (product.getImgUrl() != null) {
            String fileName = product.getImgUrl().substring(product.getImgUrl().lastIndexOf("/") + 1);
            imgService.deleteImage(fileName);
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
        return productRedisUtils.getTopSearchKeywords();
    }

    private List<ProductSearchResponse> searchProducts(String productName, int pageNum, int pageSize, Long userId) {

        memberRepository.findById(userId).orElseThrow(() -> new ClientException(ErrorCode.NOT_FOUND_MEMBER));

        productRedisUtils.saveSearchHistory(userId, productName);

        Page<Product> productPage = getProductPage(productName, pageNum, pageSize);

        return productPage.getContent().stream().map(ProductSearchResponse::fromEntity).toList();
    }

    // 캐시에 조회수를 저장한 단건 상품 조회
    @Transactional
    public ProductReadCountResponse findProductWithReadCount(Long id, Long userId) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ClientException(ErrorCode.PRODUCT_NOT_FOUND));
        Long readCount = findReadCount(product.getId(), userId);
        ProductReadCountResponse productReadCountResponse =
                ProductReadCountResponse.builder()
                        .id(product.getId())
                        .readCount(readCount)
                        .name(product.getName())
                        .quantity(product.getQuantity())
                        .category(product.getCategory())
                        .price(product.getPrice())
                        .status(product.getStatus())
                        .price(product.getPrice())
                        .imgUrl(product.getImgUrl())
                        .description(product.getDescription())
                        .build();
        return productReadCountResponse;
    }

    // 어뷰징 검증, 24시간 이내에 방문했다면 기존 조회수 조회, 아니라면 조회수 증가
    public Long findReadCount(Long productId, Long userId) {
        Boolean isNotViewed = productRedisUtils.isNotViewed(productId, userId);
        if (Boolean.TRUE.equals(isNotViewed)) {
            return addReadCount(productId);
        }
        return productRedisUtils.getReadCount(productId);
    }

    // 조회수가 없는 경우엔 1로 초기화, 존재하는 경우엔 조회수를 1만큼 증가
    public Long addReadCount(Long productId) {
        if (productRedisUtils.notExistsReadCount(productId)) {
            productRedisUtils.setReadCount(productId);
            return 1L;
        }
        return productRedisUtils.addReadCount(productId);
    }

    // 조회수 기준 상위 10개의 상품 리스트 조회하기
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "rank", key = "'products:rank'", cacheManager = "productCacheManager")
    public List<ProductRankResponse> findProductsByReadCount() {
        List<Long> idList = productRedisUtils.findProductIds();
        List<Product> products = productRepository.findProductsByRank(idList);

        // 순위별로 정렬
        products.sort(Comparator.comparingInt(p -> idList.indexOf(p.getId())));

        List<ProductRankResponse> readCountResponses = ProductRankResponse.toProductRankResponseList(products);
        return readCountResponses;
    }

    @Transactional
    public Product decreaseQuantity(Long productId, int orderAmount) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ClientException(ErrorCode.PRODUCT_NOT_FOUND));
        if (product.getQuantity() - orderAmount < 0) {
            throw new ClientException(ErrorCode.LACK_OF_QUANTITY);
        }
        product.decreaseQuantity(orderAmount);

        return product;
    }

    private Page<Product> getProductPage(String productName, int pageNum, int pageSize) {
        PageRequest pageable = PageRequest.of(pageNum - 1, pageSize);
        return productRepository.searchByName(pageable, productName);
    }

    private Member findMemberByAuthMemberId(AuthMember authMember) {
        return memberRepository.findById(authMember.getId()).orElseThrow(() -> new ClientException(ErrorCode.NOT_FOUND_MEMBER));
    }
}
