package io.ssafy.p.i13c203.gameserver.auth.security;

import io.ssafy.p.i13c203.gameserver.domain.member.entity.Member;
import io.ssafy.p.i13c203.gameserver.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));

        return new CustomUserDetails(member);
    }

    @Cacheable(value = "users", key = "#memberId")
    public UserDetails loadUserByMemberId(Long memberId) throws UsernameNotFoundException {
        log.trace("회원 ID로 사용자 조회 시작 - 회원 ID: {} (인메모리 캐시 미스로 DB 조회)", memberId);

        try {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> {
                        log.trace("회원 조회 실패 - 존재하지 않는 회원 ID: {}", memberId);
                        return new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + memberId);
                    });

            log.trace("회원 조회 성공 - 회원 ID: {}, 이메일: {}, 역할: {}",
                    member.getId(), member.getEmail(), member.getRole());

            CustomUserDetails userDetails = new CustomUserDetails(member);
            log.trace("CustomUserDetails 생성 완료 - 권한: {} (인메모리 캐시에 저장됨)", userDetails.getAuthorities());

            return userDetails;
        } catch (Exception e) {
            log.trace("회원 조회 중 예외 발생 - 회원 ID: {}, 예외: {} - {}",
                    memberId, e.getClass().getSimpleName(), e.getMessage());
            throw e;
        }
    }
}