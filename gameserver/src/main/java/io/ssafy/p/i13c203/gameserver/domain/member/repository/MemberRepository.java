package io.ssafy.p.i13c203.gameserver.domain.member.repository;


import io.ssafy.p.i13c203.gameserver.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    
    boolean existsByEmail(String email);
    
    boolean existsByNickname(String nickname);

    Optional<Member> findByEmail(String email);
}
