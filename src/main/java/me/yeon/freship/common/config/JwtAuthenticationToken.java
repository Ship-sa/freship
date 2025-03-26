package me.yeon.freship.common.config;

import me.yeon.freship.member.domain.AuthMember;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final AuthMember authMember;

    public JwtAuthenticationToken(AuthMember authMember) {
        super(authMember.getAuthorities());
        this.authMember = authMember;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return authMember;
    }
}
