package com.drink.balancegame.security;

import java.util.Map;

/**
 * OAuth2 사용자 정보를 추상화한 인터페이스
 */
public abstract class OAuth2UserInfo {
    
    protected Map<String, Object> attributes;
    
    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
    
    public Map<String, Object> getAttributes() {
        return attributes;
    }
    
    public abstract String getId();
    public abstract String getName();
    public abstract String getEmail();
    public abstract String getImageUrl();
}