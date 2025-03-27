package me.yeon.freship.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.yeon.freship.common.domain.Response;
import me.yeon.freship.member.domain.*;
import me.yeon.freship.member.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<Response<MemberSignupResponse>> signup(@Valid @RequestBody MemberSignupRequest memberSignupRequest) {
        MemberSignupResponse memberSignupResponse = authService.signup(memberSignupRequest);
        return ResponseEntity.ok().body(Response.of(memberSignupResponse));
    }

    @PostMapping("/signin")
    public ResponseEntity<Response<String>> signin(@Valid @RequestBody MemberSigninRequest memberSigninRequest) {
        String bearerToken = authService.signin(memberSigninRequest);
        return ResponseEntity.ok().body(Response.of(bearerToken));
    }
}