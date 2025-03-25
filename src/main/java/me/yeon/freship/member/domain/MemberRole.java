package me.yeon.freship.member.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.yeon.freship.common.domain.constant.ErrorCode;
import me.yeon.freship.common.exception.ClientException;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum MemberRole {
    ROLE_MEMBER(Authority.MEMBER),
    ROLE_OWNER(Authority.OWNER);

    private final String memberRole;

    public static MemberRole of(String memberRole) {
        return Arrays.stream(MemberRole.values())
                .filter(r -> r.name().equalsIgnoreCase(memberRole))
                .findFirst()
                .orElseThrow(() -> new ClientException(ErrorCode.INVALID_MEMBER_ROLE));
    }

    public static class Authority {
        public static final String MEMBER = "ROLE_MEMBER";
        public static final String OWNER = "ROLE_OWNER";
    }
}