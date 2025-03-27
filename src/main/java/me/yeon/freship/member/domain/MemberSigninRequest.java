package me.yeon.freship.member.domain;

import lombok.Getter;

@Getter
public class MemberSigninRequest {

    private String email;
    private String password;
}