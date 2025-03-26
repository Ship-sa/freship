package me.yeon.freship.member.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class MemberSignupRequest {

    @NotBlank(message = "이메일은 필수 값입니다.")
    @Email(regexp = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$", message = "올바른 이메일 형식을 입력해 주세요.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 값입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8글자 이상입니다.")
    private String password;

    @NotBlank(message = "이름은 필수 값입니다.")
    private String name;

    @NotBlank(message = "폰번호는 필수 값입니다.")
    private String phone;

    @NotBlank(message = "역할은 필수 값입니다.")
    private String memberRole;
}
