package me.yeon.freship.member.service;

import lombok.RequiredArgsConstructor;
import me.yeon.freship.common.domain.constant.ErrorCode;
import me.yeon.freship.common.exception.ClientException;
import me.yeon.freship.member.domain.*;
import me.yeon.freship.member.infrastructure.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public MemberfindResponse findMember(AuthMember authMember) {

        Member findMember = findMemberByAuthMemberId(authMember);

        return MemberfindResponse.builder()
                .id(findMember.getId())
                .email(findMember.getEmail())
                .name(findMember.getName())
                .phone(findMember.getPhone())
                .address(findMember.getAddress())
                .build();
    }

    @Transactional
    public MemberUpdateResponse updateMember(AuthMember authMember, MemberUpdateRequest memberUpdateRequest) {

        Member findMember = findMemberByAuthMemberId(authMember);

        if (!findMember.getEmail().equals(memberUpdateRequest.getEmail()) && memberRepository.existsByEmail(memberUpdateRequest.getEmail())) {
            throw new ClientException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        findMember.updateMember(memberUpdateRequest.getEmail(), memberUpdateRequest.getName(), memberUpdateRequest.getPhone(), memberUpdateRequest.getAddress());

        Member savedMember = memberRepository.save(findMember);

        return MemberUpdateResponse.builder()
                .id(savedMember.getId())
                .email(savedMember.getEmail())
                .name(savedMember.getName())
                .phone(savedMember.getPhone())
                .address(savedMember.getAddress())
                .build();
    }

    @Transactional
    public void updatePassword(AuthMember authMember, MemberPasswordUpdateRequest memberPasswordUpdateRequest) {

        Member findMember = findMemberByAuthMemberId(authMember);

        if (!passwordEncoder.matches(memberPasswordUpdateRequest.getOldPassword(), findMember.getPassword())) {
            throw new ClientException(ErrorCode.INVALID_PASSWORD);
        }

        if (passwordEncoder.matches(memberPasswordUpdateRequest.getNewPassword(), findMember.getPassword())) {
            throw new ClientException(ErrorCode.SAME_AS_OLD_PASSWORD);
        }

        findMember.updatePasssword(passwordEncoder.encode(memberPasswordUpdateRequest.getNewPassword()));
    }

    @Transactional
    public void deleteMember(AuthMember authMember) {

        Member findMember = findMemberByAuthMemberId(authMember);

        memberRepository.deleteById(findMember.getId());
    }

    private Member findMemberByAuthMemberId(AuthMember authMember) {
        return memberRepository.findById(authMember.getId()).orElseThrow(() -> new ClientException(ErrorCode.NOT_FOUND_MEMBER));
    }
}