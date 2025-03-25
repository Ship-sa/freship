package me.yeon.freship.store.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.yeon.freship.common.domain.BaseEntity;

@Entity
@Table(name = "store")
@Getter
@NoArgsConstructor
public class Store extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String bizRegNum;

    @Column(nullable = false)
    private String address;

    public Store(String name, String bizRegNum, String address) {
        this.name = name;
        this.bizRegNum = bizRegNum;
        this.address = address;
    }

}
