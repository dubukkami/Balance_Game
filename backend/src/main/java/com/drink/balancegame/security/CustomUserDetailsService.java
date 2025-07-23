package com.drink.balancegame.security;

import com.drink.balancegame.entity.User;
import com.drink.balancegame.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

/**
 * 사용자 인증을 위한 커스텀 UserDetailsService
 * OAuth2 로그인과 일반 로그인을 모두 지원
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService extends DefaultOAuth2UserService implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        
        return UserPrincipal.create(user);
    }
    
    /**
     * ID로 사용자 조회
     * @param id 사용자 ID
     * @return UserDetails
     */
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
        
        return UserPrincipal.create(user);
    }
    
    /**
     * OAuth2 로그인 처리
     * @param userRequest OAuth2 사용자 요청
     * @return OAuth2User
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        
        try {
            return processOAuth2User(userRequest, oauth2User);
        } catch (Exception ex) {
            throw new OAuth2AuthenticationException("OAuth2 authentication failed");
        }
    }
    
    /**
     * OAuth2 사용자 정보 처리
     * @param userRequest OAuth2 사용자 요청
     * @param oauth2User OAuth2 사용자
     * @return UserPrincipal
     */
    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        OAuth2UserInfo oauth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                userRequest.getClientRegistration().getRegistrationId(), 
                oauth2User.getAttributes()
        );
        
        if (oauth2UserInfo.getEmail() == null || oauth2UserInfo.getEmail().isEmpty()) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }
        
        User user = userRepository.findByEmail(oauth2UserInfo.getEmail()).orElse(null);
        
        if (user != null) {
            // 기존 사용자 업데이트
            user = updateExistingUser(user, oauth2UserInfo);
        } else {
            // 새 사용자 생성
            user = registerNewUser(userRequest, oauth2UserInfo);
        }
        
        return UserPrincipal.create(user, oauth2User.getAttributes());
    }
    
    /**
     * 새 사용자 등록
     * @param userRequest OAuth2 사용자 요청
     * @param oauth2UserInfo OAuth2 사용자 정보
     * @return User
     */
    private User registerNewUser(OAuth2UserRequest userRequest, OAuth2UserInfo oauth2UserInfo) {
        User user = User.builder()
                .username(oauth2UserInfo.getId())
                .email(oauth2UserInfo.getEmail())
                .nickname(oauth2UserInfo.getName())
                .profileImageUrl(oauth2UserInfo.getImageUrl())
                .provider(User.Provider.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase()))
                .providerId(oauth2UserInfo.getId())
                .role(User.Role.USER)
                .build();
        
        return userRepository.save(user);
    }
    
    /**
     * 기존 사용자 업데이트
     * @param existingUser 기존 사용자
     * @param oauth2UserInfo OAuth2 사용자 정보
     * @return User
     */
    private User updateExistingUser(User existingUser, OAuth2UserInfo oauth2UserInfo) {
        existingUser.setNickname(oauth2UserInfo.getName());
        existingUser.setProfileImageUrl(oauth2UserInfo.getImageUrl());
        
        return userRepository.save(existingUser);
    }
}