package me.yeon.freship.member.infrastructure;

import me.yeon.freship.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
