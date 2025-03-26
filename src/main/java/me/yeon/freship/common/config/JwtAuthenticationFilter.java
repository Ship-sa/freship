package me.yeon.freship.common.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.yeon.freship.common.domain.constant.ErrorCode;
import me.yeon.freship.common.exception.ClientException;
import me.yeon.freship.common.exception.ServerException;
import me.yeon.freship.member.domain.AuthMember;
import me.yeon.freship.member.domain.MemberRole;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest httpRequest,
            @NonNull HttpServletResponse httpResponse,
            @NonNull FilterChain chain
    ) throws ServletException, IOException {
        String authorizationHeader = httpRequest.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwt = jwtUtil.substringToken(authorizationHeader);
            try {
                Claims claims = jwtUtil.extractClaims(jwt);

                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    setAuthentication(claims);
                }
            } catch (SecurityException | MalformedJwtException e) {
                throw new ClientException(ErrorCode.INVALID_JWT);
            } catch (ExpiredJwtException e) {
                throw new ClientException(ErrorCode.EXPIRED_JWT);
            } catch (UnsupportedJwtException e) {
                throw new ClientException(ErrorCode.UNSUPPORTED_JWT);
            } catch (Exception e) {
                throw new ServerException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }
        chain.doFilter(httpRequest, httpResponse);
    }

    private void setAuthentication(Claims claims) {
        Long memberId = Long.valueOf(claims.getSubject());
        String email = claims.get("email", String.class);
        MemberRole memberRole = MemberRole.of(claims.get("memberRole", String.class));

        AuthMember authMember = new AuthMember(memberId, email, memberRole);
        JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(authMember);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
