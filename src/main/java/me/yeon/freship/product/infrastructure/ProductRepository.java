package me.yeon.freship.product.infrastructure;

import me.yeon.freship.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {
    @Query("SELECT p FROM Product p WHERE p.id in :productIds")
    List<Product> findProductsByRank(List<Long> productIds);
}