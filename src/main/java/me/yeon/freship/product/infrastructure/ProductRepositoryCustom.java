package me.yeon.freship.product.infrastructure;

import me.yeon.freship.product.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {

    Page<Product> searchByName(Pageable pageable, String name);
}
