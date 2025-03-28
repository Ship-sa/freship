package me.yeon.freship.product.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.yeon.freship.common.domain.BaseEntity;
import me.yeon.freship.store.domain.Store;

@Entity
@Table(name = "product")
@Getter
@NoArgsConstructor
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(nullable = false)
    private Integer price;

    private String imgUrl;

    private String description;

    public Product(Store store, String name, Integer quantity, Status status, Category category, Integer price, String imgUrl, String description) {
        this.store = store;
        this.name = name;
        this.quantity = quantity;
        this.status = status;
        this.category = category;
        this.price = price;
        this.imgUrl = imgUrl;
        this.description = description;
    }

    public void update(String name, Integer quantity, Status status, Category category, Integer price, String imgUrl, String description) {
        if (name != null) this.name = name;
        if (quantity != null && quantity >= 0) this.quantity = quantity;
        if (status != null) this.status = status;
        if (category != null) this.category = category;
        if (price != null && price >= 0) this.price = price;
        if (imgUrl != null) this.imgUrl = imgUrl;
        if (description != null) this.description = description;
    }

}
