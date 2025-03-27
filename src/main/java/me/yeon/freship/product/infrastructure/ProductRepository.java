package me.yeon.freship.product.infrastructure;

import me.yeon.freship.product.domain.Category;
import me.yeon.freship.product.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

    Page<Product> findByCategory(Category category, Pageable pageable);

}