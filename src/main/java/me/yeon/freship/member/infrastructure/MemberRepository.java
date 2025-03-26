package me.yeon.freship.member.infrastructure;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import me.yeon.freship.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);

    Optional<Member> findByEmail(String email);
}
