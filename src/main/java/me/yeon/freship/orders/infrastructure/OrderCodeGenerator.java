package me.yeon.freship.orders.infrastructure;

import lombok.RequiredArgsConstructor;
import me.yeon.freship.product.domain.Category;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class OrderCodeGenerator {

    private final OrderPolicyStringGenerator orderPolicyStringGenerator;

    public String create(Category category, LocalDate now) {
        StringBuilder sb = new StringBuilder();

        sb.append(now.format(DateTimeFormatter.ofPattern("yyMMdd")));
        sb.append(getCategoryCode(category));
        sb.append(orderPolicyStringGenerator.create());

        return sb.toString();
    }

    private String getCategoryCode(Category category) {
        return switch (category) {
            case MEAT, SEAFOOD -> "1";
            case VEGETABLES, FRUITS -> "2";
            case SNACKS -> "3";
        };
    }
}
