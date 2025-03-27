package me.yeon.freship.orders.infrastructure;

import me.yeon.freship.orders.domain.CustomerOrderInfo;
import me.yeon.freship.orders.domain.OwnerOrderInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderRepositoryCustom {

    Page<CustomerOrderInfo> findAllByCustomer(Pageable pageable);

    Page<OwnerOrderInfo> findAllByOwner(Pageable pageable);
}

