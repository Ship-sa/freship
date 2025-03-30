package me.yeon.freship.product.infrastructure;

import me.yeon.freship.product.domain.Category;
import me.yeon.freship.product.domain.Product;
import me.yeon.freship.store.domain.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {


    Page<Product> findByCategory(Category category, Pageable pageable);

    boolean existsByStoreAndName(Store store, String name);

    @Query("SELECT p FROM Product p WHERE p.id in :productIds")
    List<Product> findProductsByRank(List<Long> productIds);
}
