package io.ssafy.p.i13c203.gameserver.domain.member.dto.response;

import io.ssafy.p.i13c203.gameserver.domain.member.entity.Gender;
import io.ssafy.p.i13c203.gameserver.domain.member.entity.Member;
import io.ssafy.p.i13c203.gameserver.domain.member.entity.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponseDto {
    
    private Long id;
    private String email;
    private String nickname;
    private Integer age;
    private Gender gender;
    private Role role;
    
    public static LoginResponseDto from(Member member) {
        return LoginResponseDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .age(member.getAge())
                .gender(member.getGender())
                .role(member.getRole())
                .build();
    }
}