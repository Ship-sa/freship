package me.yeon.freship.member.service;

import lombok.RequiredArgsConstructor;
import me.yeon.freship.common.config.JwtUtil;
import me.yeon.freship.common.domain.constant.ErrorCode;
import me.yeon.freship.common.exception.ClientException;
import me.yeon.freship.member.domain.*;
import me.yeon.freship.member.infrastructure.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public MemberSignupResponse signup(MemberSignupRequest memberSignupRequest) {

        if (memberRepository.existsByEmail(memberSignupRequest.getEmail())) {
            throw new ClientException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        String encodedPassword = passwordEncoder.encode(memberSignupRequest.getPassword());

        Member member = new Member(
                memberSignupRequest.getEmail(),
                encodedPassword,
                memberSignupRequest.getName(),
                memberSignupRequest.getPhone(),
                MemberRole.of(memberSignupRequest.getMemberRole()));

        Member savedMember = memberRepository.save(member);

        return MemberSignupResponse.builder()
                .id(savedMember.getId())
                .email(savedMember.getEmail())
                .name(savedMember.getName())
                .memberRole(String.valueOf(savedMember.getMemberRole()))
                .createdAt(savedMember.getCreatedAt())
                .build();

    }

    @Transactional
    public String signin(MemberSigninRequest memberSigninRequest) {
        Member findMember = memberRepository.findByEmail(memberSigninRequest.getEmail()).orElseThrow(() -> new ClientException(ErrorCode.NOT_FOUND_MEMBER));

        if (!passwordEncoder.matches(memberSigninRequest.getPassword(), findMember.getPassword())) {
            throw new ClientException(ErrorCode.INVALID_PASSWORD);
        }

        return jwtUtil.createToken(findMember.getId(), findMember.getEmail(), findMember.getMemberRole());

    }
}
