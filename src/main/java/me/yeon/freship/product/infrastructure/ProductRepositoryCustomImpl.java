//package me.yeon.freship.product.infrastructure;
//
//import com.querydsl.core.types.dsl.BooleanExpression;
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import lombok.RequiredArgsConstructor;
//import me.yeon.freship.product.domain.Product;
//import me.yeon.freship.product.domain.QProduct;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//
//import java.util.List;
//
//@RequiredArgsConstructor
//public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {
//
//    private final JPAQueryFactory queryFactory;
//
//    @Override
//    public Page<Product> searchByName(Pageable pageable, String name) {
//        QProduct product = QProduct.product;
//
//        BooleanExpression condition = product.name.like("%" + name + "%");
//
//        List<Product> results = queryFactory.selectFrom(product)
//                .where(condition)
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetch();
//
//        long total = queryFactory.selectFrom(product)
//                .where(condition)
//                .fetch().size();
//
//        return new PageImpl<>(results, pageable, total);
//    }
//}
