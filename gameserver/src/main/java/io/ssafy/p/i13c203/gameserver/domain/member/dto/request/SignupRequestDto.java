package io.ssafy.p.i13c203.gameserver.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class SignupRequestDto {
    
    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    @Schema(example = "test@test.test")
    private String email;
    
    @NotBlank(message = "비밀번호는 필수입니다")
    @Schema(example = "test@test.test")
    private String password;
    
    @NotBlank(message = "닉네임은 필수입니다")
    @Schema(example = "내왕국에서나가")
    private String nickname;
}