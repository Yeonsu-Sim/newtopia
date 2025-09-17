package io.ssafy.p.i13c203.gameserver.auth.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    private final SecretKey key;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtUtil(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access-token-expiration:900000}") long accessTokenExpiration, // 15분
            @Value("${jwt.refresh-token-expiration:604800000}") long refreshTokenExpiration // 7일
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public String generateAccessToken(Long memberId) {
        return generateToken(memberId, accessTokenExpiration);
    }

    public String generateRefreshToken(Long memberId) {
        return generateToken(memberId, refreshTokenExpiration);
    }

    private String generateToken(Long memberId, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(memberId.toString())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key, Jwts.SIG.HS512)
                .compact();
    }

    public Long getMemberIdFromToken(String token) {
        try {
            log.trace("토큰에서 회원 ID 추출 시작");
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String subject = claims.getSubject();
            log.trace("토큰 Subject 추출 성공: {}", subject);

            Long memberId = Long.parseLong(subject);
            log.trace("회원 ID 파싱 성공: {}", memberId);

            return memberId;
        } catch (Exception e) {
            log.trace("토큰에서 회원 ID 추출 실패 - 예외: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            throw e;
        }
    }

    public boolean validateToken(String token) {
        try {
            log.trace("JWT 토큰 검증 시작");
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            log.trace("JWT 토큰 검증 성공 - 토큰이 유효함");
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.trace("JWT 토큰 검증 실패 - 잘못된 서명: {}", e.getMessage());
            log.debug("Invalid JWT signature: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.trace("JWT 토큰 검증 실패 - 토큰 만료: {}", e.getMessage());
            log.debug("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.trace("JWT 토큰 검증 실패 - 지원되지 않는 토큰: {}", e.getMessage());
            log.debug("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.trace("JWT 토큰 검증 실패 - 빈 토큰: {}", e.getMessage());
            log.debug("JWT claims string is empty: {}", e.getMessage());
        }
        log.trace("JWT 토큰 검증 최종 결과: 무효");
        return false;
    }

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}