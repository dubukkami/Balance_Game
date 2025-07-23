package com.drink.balancegame.dto;

import com.drink.balancegame.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자 프로필 정보 전송 객체 (DTO)
 * 사용자 프로필 정보를 클라이언트에 전송하기 위한 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {
    /** 사용자 ID */
    private Long id;
    
    /** 사용자명 */
    private String username;
    
    /** 이메일 */
    private String email;
    
    /** 닉네임 */
    private String nickname;
    
    /** 프로필 이미지 URL */
    private String profileImageUrl;
    
    /** 자기소개 */
    private String bio;
    
    /** 거주 지역 */
    private String location;
    
    /** 개인 웹사이트 */
    private String website;
    
    /** OAuth 제공자 */
    private User.Provider provider;
    
    /** 가입일시 */
    private LocalDateTime createdAt;
}