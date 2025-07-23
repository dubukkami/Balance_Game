package com.drink.balancegame.security;

import com.drink.balancegame.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Spring Security UserDetails와 OAuth2User 인터페이스를 구현한 사용자 정보 클래스
 */
@AllArgsConstructor
@Getter
public class UserPrincipal implements OAuth2User, UserDetails {
    
    private Long id;
    private String email;
    private String username;
    private String nickname;
    private String profileImageUrl;
    private Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;
    
    /**
     * User 엔티티로부터 UserPrincipal 생성
     * @param user User 엔티티
     * @return UserPrincipal
     */
    public static UserPrincipal create(User user) {
        Collection<GrantedAuthority> authorities = Collections.singleton(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );
        
        return new UserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getNickname(),
                user.getProfileImageUrl(),
                authorities,
                null
        );
    }
    
    /**
     * User 엔티티와 OAuth2 속성으로부터 UserPrincipal 생성
     * @param user User 엔티티
     * @param attributes OAuth2 속성
     * @return UserPrincipal
     */
    public static UserPrincipal create(User user, Map<String, Object> attributes) {
        UserPrincipal userPrincipal = create(user);
        return new UserPrincipal(
                userPrincipal.getId(),
                userPrincipal.getEmail(),
                userPrincipal.getUsername(),
                userPrincipal.getNickname(),
                userPrincipal.getProfileImageUrl(),
                userPrincipal.getAuthorities(),
                attributes
        );
    }
    
    // UserDetails 인터페이스 구현
    @Override
    public String getPassword() {
        return null; // OAuth2 로그인에서는 비밀번호 사용 안함
    }
    
    @Override
    public String getUsername() {
        return username;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return true;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    
    // OAuth2User 인터페이스 구현
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
    
    @Override
    public String getName() {
        return String.valueOf(id);
    }
}