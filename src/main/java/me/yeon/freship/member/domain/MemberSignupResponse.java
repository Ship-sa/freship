package me.yeon.freship.member.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MemberSignupResponse {

    private final Long id;
    private final String email;
    private final String name;
    private final String memberRole;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createdAt;

    public MemberSignupResponse(Long id, String email, String name, String memberRole, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.memberRole = memberRole;
        this.createdAt = createdAt;
    }
}