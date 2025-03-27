package me.yeon.freship.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.yeon.freship.common.domain.Response;
import me.yeon.freship.member.domain.*;
import me.yeon.freship.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<Response<MemberfindResponse>> findMember(@AuthenticationPrincipal AuthMember authMember) {
        MemberfindResponse memberfindResponse = memberService.findMember(authMember);
        return ResponseEntity.ok().body(Response.of(memberfindResponse));
    }

    @PatchMapping
    public ResponseEntity<Response<MemberUpdateResponse>> updateMember(
            @AuthenticationPrincipal AuthMember authMember,
            @Valid @RequestBody MemberUpdateRequest memberUpdateRequest) {
        MemberUpdateResponse memberUpdateResponse = memberService.updateMember(authMember, memberUpdateRequest);
        return ResponseEntity.ok().body(Response.of(memberUpdateResponse));
    }

    @PatchMapping("/password")
    public void updatePassword(
            @AuthenticationPrincipal AuthMember authMember,
            @Valid @RequestBody MemberPasswordUpdateRequest memberPasswordUpdateRequest) {
        memberService.updatePassword(authMember, memberPasswordUpdateRequest);
    }

    @DeleteMapping
    public void deleteMember(@AuthenticationPrincipal AuthMember authMember) {
        memberService.deleteMember(authMember);
    }
}