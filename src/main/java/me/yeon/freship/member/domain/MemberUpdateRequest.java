package me.yeon.freship.member.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class MemberUpdateRequest {

    @Email(regexp = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$", message = "올바른 이메일 형식을 입력해 주세요.")
    private String email;

    private String name;

    private String phone;

    private String address;

}
