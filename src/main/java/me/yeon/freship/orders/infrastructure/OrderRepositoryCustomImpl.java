package me.yeon.freship.orders.infrastructure;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import me.yeon.freship.orders.domain.CustomerOrderInfo;
import me.yeon.freship.orders.domain.OwnerOrderInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static me.yeon.freship.orders.domain.QOrder.order;

public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {

    private final EntityManager em;
    private JPAQueryFactory queryFactory;

    public OrderRepositoryCustomImpl(EntityManager em) {
        this.em = em;
        queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<CustomerOrderInfo> findAllByCustomer(Pageable pageable) {

        List<CustomerOrderInfo> results = queryFactory
                .select(order)
                .from(order)
                .orderBy(order.createdAt.desc())
                .fetch()
                .stream()
                .map(CustomerOrderInfo::fromOrder)
                .toList();

        JPAQuery<Long> countQuery = queryFactory
                .select(order.count())
                .from(order);

        return PageableExecutionUtils.getPage(results, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<OwnerOrderInfo> findAllByOwner(Pageable pageable) {

        List<OwnerOrderInfo> results = queryFactory
                .select(order)
                .from(order)
                .orderBy(order.createdAt.desc())
                .fetch()
                .stream()
                .map(OwnerOrderInfo::fromOrder)
                .toList();

        JPAQuery<Long> countQuery = queryFactory
                .select(order.count())
                .from(order);

        return PageableExecutionUtils.getPage(results, pageable, countQuery::fetchOne);
    }
}
