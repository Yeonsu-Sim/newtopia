package io.ssafy.p.i13c203.gameserver.global.config;

import io.ssafy.p.i13c203.gameserver.auth.CurrentMemberIdArgumentResolver;
import io.ssafy.p.i13c203.gameserver.auth.interceptor.MemberIdCookieInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Component
@RequiredArgsConstructor
class WebConfig implements WebMvcConfigurer {

    private final MemberIdCookieInterceptor interceptor;
    private final CurrentMemberIdArgumentResolver resolver;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor).addPathPatterns("/api/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(resolver);
    }

    @Override
    public void addCorsMappings(CorsRegistry reg) {
        reg.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000")  // TODO: 임시 허용 수거
                .allowedMethods("GET","POST","PUT","DELETE","OPTIONS","HEAD")
                .allowCredentials(true)
                .allowedHeaders("*");
    }
}