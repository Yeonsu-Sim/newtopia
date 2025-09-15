package io.ssafy.p.i13c203.gameserver.auth.oauth;

/**
 * OAuth2 통합 인터페이스
 * 향후 Google, Kakao, Naver 등 다양한 OAuth Provider 지원을 위한 인터페이스
 */
public interface OAuth2UserService {

    /**
     * OAuth2 제공자 이름
     * @return "google", "kakao", "naver" 등
     */
    String getProviderName();

    /**
     * OAuth2 로그인 처리
     * @param authorizationCode OAuth2 인증 코드
     * @return OAuth2 사용자 정보
     */
    Object processLogin(String authorizationCode);

    /**
     * 사용자 정보 조회 (토큰으로부터)
     * @param accessToken OAuth2 액세스 토큰
     * @return OAuth2 사용자 정보
     */
    Object getUserInfo(String accessToken);
}