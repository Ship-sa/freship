package me.yeon.freship.payment.infrastructure;

import me.yeon.freship.payment.domain.PayHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentHistoryRepository extends JpaRepository<PayHistory, Long> {
}
