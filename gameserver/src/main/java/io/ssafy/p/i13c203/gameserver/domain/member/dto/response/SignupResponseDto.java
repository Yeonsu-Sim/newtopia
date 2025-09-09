package io.ssafy.p.i13c203.gameserver.domain.member.dto.response;

import io.ssafy.p.i13c203.gameserver.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SignupResponseDto {
    
    private Long id;
    private String email;
    private String nickname;
    private LocalDateTime createdAt;
    
    public static SignupResponseDto from(Member member) {
        return SignupResponseDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .createdAt(member.getCreatedAt())
                .build();
    }
}