package me.yeon.freship.store.infrastructure;

import me.yeon.freship.store.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {

}
