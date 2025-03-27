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

    public Product(String name, Integer quantity, Status status, Category category, Integer price, String imgUrl,
                   String description) {
        this.name = name;
        this.quantity = quantity;
        this.status = status;
        this.category = category;
        this.price = price;
        this.imgUrl = imgUrl;
        this.description = description;
    }

    public void decreaseQuantity(int amount) {
        this.quantity -= amount;
    }

    public void increaseQuantity(int amount) {
        this.quantity += amount;
    }

}
