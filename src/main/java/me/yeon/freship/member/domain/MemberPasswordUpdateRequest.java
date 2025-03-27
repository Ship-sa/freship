package me.yeon.freship.member.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class MemberPasswordUpdateRequest {

    @NotBlank(message = "비밀번호는 필수 값입니다.")
    private String oldPassword;

    @NotBlank(message = "비밀번호는 필수 값입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8글자 이상입니다.")
    private String newPassword;
}