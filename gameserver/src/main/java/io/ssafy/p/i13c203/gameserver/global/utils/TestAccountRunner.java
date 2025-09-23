package io.ssafy.p.i13c203.gameserver.global.utils;

import io.ssafy.p.i13c203.gameserver.domain.member.dto.request.SignupRequestDto;
import io.ssafy.p.i13c203.gameserver.domain.member.repository.MemberRepository;
import io.ssafy.p.i13c203.gameserver.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
@Profile({"local","docker"})
public class TestAccountRunner implements CommandLineRunner {

    private final MemberService memberService;
    @Override
    public void run(String... args) throws Exception {

//
//        private String email;
//        private String password;
//        private String nickname;

        SignupRequestDto signupRequestDto = new SignupRequestDto();
        signupRequestDto.setEmail("ssafy@ssafy.com");
        signupRequestDto.setPassword("ssafy123");
        signupRequestDto.setNickname("감자탕의신");
        memberService.signup(signupRequestDto);

        SignupRequestDto signupRequestDto1 = new SignupRequestDto();
        signupRequestDto1.setEmail("ssafy1@ssafy.com");
        signupRequestDto1.setPassword("ssafy123");
        signupRequestDto1.setNickname("치킨은국룰");
        memberService.signup(signupRequestDto1);


        SignupRequestDto signupRequestDto2 = new SignupRequestDto();
        signupRequestDto2.setEmail("ssafy2@ssafy.com");
        signupRequestDto2.setPassword("ssafy123");
        signupRequestDto2.setNickname("버그제조기");
        memberService.signup(signupRequestDto2);

        SignupRequestDto signupRequestDto3 = new SignupRequestDto();
        signupRequestDto3.setEmail("ssafy3@ssafy.com");
        signupRequestDto3.setPassword("ssafy123");
        signupRequestDto3.setNickname("코딩하다잠듦");
        memberService.signup(signupRequestDto3);

        SignupRequestDto signupRequestDto4 = new SignupRequestDto();
        signupRequestDto4.setEmail("ssafy4@ssafy.com");
        signupRequestDto4.setPassword("ssafy123");
        signupRequestDto4.setNickname("라면물맨");
        memberService.signup(signupRequestDto4);
        SignupRequestDto signupRequestDto5 = new SignupRequestDto();
        signupRequestDto5.setEmail("ssafy5@ssafy.com");
        signupRequestDto5.setPassword("ssafy123");
        signupRequestDto5.setNickname("실수의달인");
        memberService.signup(signupRequestDto5);

        SignupRequestDto signupRequestDto6 = new SignupRequestDto();
        signupRequestDto6.setEmail("ssafy6@ssafy.com");
        signupRequestDto6.setPassword("ssafy123");
        signupRequestDto6.setNickname("집가고싶다왕");
        memberService.signup(signupRequestDto6);

        memberService.singupAdmin();

    }
}
