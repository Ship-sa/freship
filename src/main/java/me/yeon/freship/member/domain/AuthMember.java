package me.yeon.freship.member.domain;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

@Getter
public class AuthMember {

    private final Long Id;
    private final String email;
    private final Collection<? extends GrantedAuthority> authorities;

    public AuthMember(Long Id, String email, MemberRole memberRole) {
        this.Id = Id;
        this.email = email;
        this.authorities = List.of(new SimpleGrantedAuthority(memberRole.name()));
    }
}
