package me.yeon.freship.product.infrastructure;

import me.yeon.freship.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryQuery {
}