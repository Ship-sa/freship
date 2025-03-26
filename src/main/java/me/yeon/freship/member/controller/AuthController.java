package me.yeon.freship.member.controller;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.yeon.freship.member.domain.*;
import me.yeon.freship.member.service.AuthService;
import me.yeon.freship.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/signup")
    public ResponseEntity<MemberSignupResponse> signup(@Valid @RequestBody MemberSignupRequest memberSignupRequest) {
        return ResponseEntity.ok(authService.signup(memberSignupRequest));
    }

    @PostMapping("/auth/signin")
    public ResponseEntity<String> signin(@Valid @RequestBody MemberSigninRequest memberSigninRequest) {
        String bearerToken = authService.signin(memberSigninRequest);
        return ResponseEntity.ok(bearerToken);
    }
}
