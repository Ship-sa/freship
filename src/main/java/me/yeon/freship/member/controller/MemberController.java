package me.yeon.freship.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.yeon.freship.member.domain.*;
import me.yeon.freship.member.infrastructure.MemberRepository;
import me.yeon.freship.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members")
    public ResponseEntity<MemberfindResponse> findMember(@AuthenticationPrincipal AuthMember authMember) {
        return ResponseEntity.ok(memberService.findMember(authMember));
    }

    @PatchMapping("/members")
    public ResponseEntity<MemberUpdateResponse> updateMember(
            @AuthenticationPrincipal AuthMember authMember,
            @Valid @RequestBody MemberUpdateRequest memberUpdateRequest) {
        return ResponseEntity.ok(memberService.updateMember(authMember, memberUpdateRequest));
    }

    @PatchMapping("/members/password")
    public void updatePassword(
            @AuthenticationPrincipal AuthMember authMember,
            @Valid @RequestBody MemberPasswordUpdateRequest memberPasswordUpdateRequest) {
        memberService.updatePassword(authMember, memberPasswordUpdateRequest);
    }

    @DeleteMapping("/members")
    public void deleteMember(@AuthenticationPrincipal AuthMember authMember) {
        memberService.deleteMember(authMember);
    }
}
