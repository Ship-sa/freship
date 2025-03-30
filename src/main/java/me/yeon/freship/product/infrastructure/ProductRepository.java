package me.yeon.freship.product.infrastructure;

import me.yeon.freship.product.domain.Category;
import me.yeon.freship.product.domain.Product;
import me.yeon.freship.store.domain.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {


    Page<Product> findByCategory(Category category, Pageable pageable);

    boolean existsByStoreAndName(Store store, String name);

    @Query("SELECT p FROM Product p WHERE p.name LIKE %:name%")
    Page<Product> searchByName(Pageable pageable, @Param("name") String name);

    @Query("SELECT p from Product p JOIN FETCH p.store s JOIN FETCH s.member WHERE p.id=:productId")
    Optional<Product> findByIdWithStoreAndOwner(@Param("productId") Long id);

    @Query("SELECT p FROM Product p WHERE p.id in :productIds")
    List<Product> findProductsByRank(List<Long> productIds);
}
