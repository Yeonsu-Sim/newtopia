package io.ssafy.p.i13c203.gameserver.domain.member.service;

import io.ssafy.p.i13c203.gameserver.domain.member.dto.request.LoginRequestDto;
import io.ssafy.p.i13c203.gameserver.domain.member.dto.request.SignupRequestDto;
import io.ssafy.p.i13c203.gameserver.domain.member.dto.response.LoginResponseDto;
import io.ssafy.p.i13c203.gameserver.domain.member.dto.response.SignupResponseDto;
import io.ssafy.p.i13c203.gameserver.domain.member.entity.Member;
import io.ssafy.p.i13c203.gameserver.domain.member.entity.Role;
import io.ssafy.p.i13c203.gameserver.domain.member.exception.DuplicatedEmailException;
import io.ssafy.p.i13c203.gameserver.domain.member.exception.DuplicatedNicknameException;
import io.ssafy.p.i13c203.gameserver.domain.member.exception.InvalidPasswordException;
import io.ssafy.p.i13c203.gameserver.domain.member.exception.MemberNotFoundException;
import io.ssafy.p.i13c203.gameserver.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class MemberService {
    
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    
    public SignupResponseDto signup(SignupRequestDto request) {
        // 이메일 중복 체크
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new DuplicatedEmailException();
        }
        
        // 닉네임 중복 체크
        if (memberRepository.existsByNickname(request.getNickname())) {
            throw new DuplicatedNicknameException();
        }
        
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        
        // Member 엔티티 생성
        Member member = Member.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .nickname(request.getNickname())
                .age(null) // 기본값 null
                .gender(null) // 기본값 null
                .role(Role.MEMBER) // 기본값 MEMBER
                .isDeleted(false) // 기본값 false
                .isNewsletterSubscribed(false) // 기본값 false
                .build();
        
        // 저장
        Member savedMember = memberRepository.save(member);
        
        log.info("회원가입 완료: email={}, nickname={}", savedMember.getEmail(), savedMember.getNickname());
        
        return SignupResponseDto.from(savedMember);
    }
    
    public LoginResponseDto login(LoginRequestDto request) {
        // 이메일로 회원 조회
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 이메일입니다."));
        
        // 삭제된 회원 체크
        if (member.getIsDeleted()) {
            throw new MemberNotFoundException("탈퇴한 회원입니다.");
        }
        
        // 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new InvalidPasswordException("비밀번호가 일치하지 않습니다.");
        }
        
        log.info("로그인 성공: email={}, nickname={}", member.getEmail(), member.getNickname());
        
        return LoginResponseDto.from(member);
    }
}
