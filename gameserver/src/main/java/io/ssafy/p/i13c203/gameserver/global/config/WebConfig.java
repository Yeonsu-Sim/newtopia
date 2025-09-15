package io.ssafy.p.i13c203.gameserver.global.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Deprecated
@Component
// @RequiredArgsConstructor  // Spring Security 사용으로 주석 처리
class WebConfig implements WebMvcConfigurer {
//    private final MemberIdCookieInterceptor interceptor;
//    private final CurrentMemberIdArgumentResolver resolver;
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(interceptor).addPathPatterns("/api/**");
//    }
//
//    @Override
//    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
//        resolvers.add(resolver);
//    }
//
//    @Override
//    public void addCorsMappings(CorsRegistry reg) {
//        reg.addMapping("/api/**")
//                .allowedOrigins("http://localhost:5500")  // TODO: 임시 허용 수거
//                .allowedMethods("GET","POST","PUT","DELETE","OPTIONS","HEAD")
//                .allowCredentials(true)
//                .allowedHeaders("*");
//    }
}