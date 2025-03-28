package me.yeon.freship.product.infrastructure;

import me.yeon.freship.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("select p from Product p join fetch p.store s join s.member where p.id=:productId")
    Optional<Product> findByIdWithStoreAndOwner(@Param("productId") Long id);
}
