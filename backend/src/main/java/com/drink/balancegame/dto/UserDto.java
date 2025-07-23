package com.drink.balancegame.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자 DTO
 * 사용자 정보 전송을 위한 데이터 객체
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    
    /** 사용자 ID */
    private Long id;
    
    /** 사용자명 */
    private String username;
    
    /** 이메일 */
    private String email;
    
    /** 닉네임 */
    private String nickname;
    
    /** 생성일시 */
    private LocalDateTime createdAt;
    
    /** 수정일시 */
    private LocalDateTime updatedAt;
    
    /** 프로필 이미지 URL */
    private String profileImageUrl;
    
    /** 사용자 권한 */
    private String role;
    
    /** OAuth 제공자 */
    private String provider;
}

/**
 * 사용자 회원가입 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class UserRegistrationDto {
    
    /** 사용자명 */
    private String username;
    
    /** 비밀번호 */
    private String password;
    
    /** 이메일 */
    private String email;
    
    /** 닉네임 */
    private String nickname;
}

/**
 * 사용자 로그인 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class UserLoginDto {
    
    /** 사용자명 */
    private String username;
    
    /** 비밀번호 */
    private String password;
}

/**
 * 사용자 프로필 수정 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class UserUpdateDto {
    
    /** 이메일 */
    private String email;
    
    /** 닉네임 */
    private String nickname;
    
    /** 새 비밀번호 */
    private String newPassword;
    
    /** 현재 비밀번호 */
    private String currentPassword;
}