package com.drink.balancegame.security;

import com.drink.balancegame.entity.User;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import java.util.Map;

/**
 * OAuth2 제공자별 사용자 정보 팩토리 클래스
 */
public class OAuth2UserInfoFactory {
    
    /**
     * OAuth2 제공자에 따라 적절한 OAuth2UserInfo 객체 생성
     * @param registrationId OAuth2 제공자 등록 ID
     * @param attributes OAuth2 사용자 속성
     * @return OAuth2UserInfo 객체
     */
    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        switch (registrationId.toLowerCase()) {
            case "google":
                return new GoogleOAuth2UserInfo(attributes);
            case "kakao":
                return new KakaoOAuth2UserInfo(attributes);
            case "naver":
                return new NaverOAuth2UserInfo(attributes);
            default:
                throw new OAuth2AuthenticationException("Sorry! Login with " + registrationId + " is not supported yet.");
        }
    }
}