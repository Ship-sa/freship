package me.yeon.freship.member.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.yeon.freship.common.domain.BaseEntity;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "members")
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String name;

    private String phone;

    private String address;

    @Enumerated(EnumType.STRING)
    private MemberRole memberRole;

    public Member(String email, String password, String name, String phone, String address, MemberRole memberRole) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.memberRole = memberRole;
    }

    public Member(String email, String password, String name, String phone, MemberRole memberRole) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.memberRole = memberRole;
    }

    private Member(Long id, String email, MemberRole memberRole) {
        this.id = id;
        this.email = email;
        this.memberRole = memberRole;
    }

    public void update(String email, String name, String phone, String address) {
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    public void updatePasssword(String password) {
        this.password = password;
    }
}
