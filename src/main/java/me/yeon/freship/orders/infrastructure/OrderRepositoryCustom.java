package me.yeon.freship.orders.infrastructure;

import me.yeon.freship.orders.domain.CustomerOrderInfo;
import me.yeon.freship.orders.domain.Order;
import me.yeon.freship.orders.domain.OwnerOrderInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface OrderRepositoryCustom {

    Page<CustomerOrderInfo> findAllByCustomer(Pageable pageable);

    Page<OwnerOrderInfo> findAllByOwner(Pageable pageable, Long memberId);

    Optional<Order> findByIdAndOwnerId(Long id, Long ownerId);
}

