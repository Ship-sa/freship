package me.yeon.freship.orders.infrastructure;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import me.yeon.freship.orders.domain.CustomerOrderInfo;
import me.yeon.freship.orders.domain.Order;
import me.yeon.freship.orders.domain.OwnerOrderInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Optional;

import static me.yeon.freship.member.domain.QMember.member;
import static me.yeon.freship.orders.domain.QOrder.order;
import static me.yeon.freship.product.domain.QProduct.product;
import static me.yeon.freship.store.domain.QStore.store;

public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {

    private final EntityManager em;
    private JPAQueryFactory queryFactory;

    public OrderRepositoryCustomImpl(EntityManager em) {
        this.em = em;
        queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<CustomerOrderInfo> findAllByCustomer(Pageable pageable, Long memberId) {

        List<CustomerOrderInfo> results = queryFactory
                .selectFrom(order)
                .join(order.member, member)
                .where(member.id.eq(memberId))
                .orderBy(order.createdAt.desc())
                .fetch()
                .stream()
                .map(CustomerOrderInfo::fromOrder)
                .toList();

        JPAQuery<Long> countQuery = queryFactory
                .select(order.count())
                .from(order)
                .join(order.member, member)
                .where(member.id.eq(memberId));

        return PageableExecutionUtils.getPage(results, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<OwnerOrderInfo> findAllByOwner(Pageable pageable, Long ownerId) {

        List<OwnerOrderInfo> results = queryFactory
                .selectFrom(order)
                .join(product).on(order.productId.eq(product.id))
                .join(product.store, store)
                .join(store.member, member)
                .where(member.id.eq(ownerId))
                .fetch()
                .stream()
                .map(OwnerOrderInfo::fromOrder)
                .toList();

        JPAQuery<Long> countQuery = queryFactory
                .select(order.count())
                .from(order)
                .join(product).on(order.productId.eq(product.id))
                .join(product.store, store)
                .join(store.member, member)
                .where(member.id.eq(ownerId));

        return PageableExecutionUtils.getPage(results, pageable, countQuery::fetchOne);
    }

    @Override
    public Optional<Order> findByIdAndOwnerId(Long id, Long ownerId) {

        Order oneOrder = queryFactory
                .selectFrom(order)
                .join(product).on(order.productId.eq(product.id))
                .join(product.store, store)
                .join(store.member, member)
                .where(order.id.eq(id), member.id.eq(ownerId))
                .fetchOne();
        return Optional.ofNullable(oneOrder);
    }
}
