package me.yeon.freship.orders.infrastructure;

import me.yeon.freship.orders.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {

    @Query("select o from Order o join fetch o.member where o.id = :orderId")
    Optional<Order> findByIdWithMember(@Param("orderId") Long id);

    @Query("select o from Order o join fetch o.member where o.orderCode=:orderCode")
    Optional<Order> findByOrderCode(@Param("orderCode") String orderCode);

}
