package me.yeon.freship.store.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.yeon.freship.common.domain.BaseEntity;
import me.yeon.freship.member.domain.Member;

@Entity
@Table(name = "store")
@Getter
@NoArgsConstructor
public class Store extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String bizRegNum;

    @Column(nullable = false)
    private String address;

    public Store(Member member, String name, String bizRegNum, String address) {
        this.member = member;
        this.name = name;
        this.bizRegNum = bizRegNum;
        this.address = address;
    }

    public void update(String name, String address) {
        if (name != null) this.name = name;
        if (address != null) this.address = address;
    }

}
