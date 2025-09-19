package io.ssafy.p.i13c203.gameserver.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.ssafy.p.i13c203.gameserver.auth.filter.JsonAuthenticationFilter;
import io.ssafy.p.i13c203.gameserver.auth.filter.JwtCookieAuthenticationFilter;
import io.ssafy.p.i13c203.gameserver.auth.handler.CustomAccessDeniedHandler;
import io.ssafy.p.i13c203.gameserver.auth.handler.CustomAuthenticationFailureHandler;
import io.ssafy.p.i13c203.gameserver.auth.handler.CustomAuthenticationSuccessHandler;
import io.ssafy.p.i13c203.gameserver.auth.handler.CustomLogoutHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.core.env.Environment;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtCookieAuthenticationFilter jwtCookieAuthenticationFilter;
    private final CustomAuthenticationSuccessHandler successHandler;
    private final CustomAuthenticationFailureHandler failureHandler;
    private final CustomLogoutHandler logoutHandler;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final ObjectMapper objectMapper;
    private final Environment environment;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public JsonAuthenticationFilter jsonAuthenticationFilter(AuthenticationManager authenticationManager) {
        JsonAuthenticationFilter filter = new JsonAuthenticationFilter(objectMapper);
        filter.setAuthenticationManager(authenticationManager);
        filter.setAuthenticationSuccessHandler(successHandler);
        filter.setAuthenticationFailureHandler(failureHandler);
        return filter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF 비활성화 (JWT + 쿠키 방식에서는 불필요)
            .csrf(AbstractHttpConfigurer::disable)
            .cors(AbstractHttpConfigurer::disable)  // CORS 비활성화
//            .cors(cors -> cors.configurationSource(corsConfigurationSource()))  // CORS 활성화
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/games/**").authenticated()
                        .requestMatchers("/api/v1/game-results/**").authenticated()

                        .requestMatchers(HttpMethod.GET, "/api/v1/ranking/me").authenticated()

                        .requestMatchers(HttpMethod.POST, "/api/v1/files").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/files").authenticated()

                        .anyRequest().permitAll()
                )

            // 로그아웃 필터 설정
            .logout(logout -> logout
                .logoutUrl("/api/v1/auth/logout")
                .addLogoutHandler(logoutHandler)
                .logoutSuccessHandler((request, response, authentication) -> {
                    response.setStatus(200);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"success\":true,\"message\":\"로그아웃에 성공했습니다\",\"data\":null}");
                })
                .permitAll()
            )

            // JWT 쿠키 인증 필터 설정
            .addFilterBefore(jwtCookieAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

            // JSON 로그인 필터 설정
            .addFilterAt(jsonAuthenticationFilter(authenticationManager(null)), UsernamePasswordAuthenticationFilter.class)

            // 커스텀 AccessDeniedHandler 설정
            .exceptionHandling(exceptions -> exceptions
                .accessDeniedHandler(accessDeniedHandler)
            );

        return http.build();
    }

    /**
     * CORS 설정 빈 (현재 비활성화됨)
     * 프록시 사용으로 CORS 불필요하지만, 필요시 활성화 가능
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        log.debug("active profiles: {}",Arrays.toString(environment.getActiveProfiles()));

        if (Arrays.asList(environment.getActiveProfiles()).contains("prod")) {
            configuration.setAllowedOrigins(Arrays.asList(
                "https://j13c203.p.ssafy.io"
            ));
        } else {
            configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:5173",
                "http://localhost:5500" 
            ));
        }

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}