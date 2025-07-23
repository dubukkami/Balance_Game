package com.drink.balancegame.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 사용자 엔티티
 * 밸런스 게임 커뮤니티의 사용자 정보를 저장
 */
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    /** 사용자 ID (Primary Key) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** 사용자명 (고유값) */
    @Column(unique = true, nullable = false)
    private String username;
    
    /** 비밀번호 (OAuth 사용자는 null 가능) */
    private String password;
    
    /** 이메일 (고유값) */
    @Column(unique = true, nullable = false)
    private String email;
    
    /** 닉네임 */
    private String nickname;
    
    /** 사용자 권한 (USER, ADMIN) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;
    
    /** OAuth 제공자 (GOOGLE, KAKAO, NAVER) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider;
    
    /** OAuth 제공자 ID */
    private String providerId;
    
    /** 프로필 이미지 URL */
    private String profileImageUrl;
    
    /** 자기소개 */
    @Column(length = 500)
    private String bio;
    
    /** 거주 지역 */
    @Column(length = 100)
    private String location;
    
    /** 개인 웹사이트 */
    @Column(length = 200)
    private String website;
    
    /** 생성일시 */
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    /** 수정일시 */
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    /** 사용자가 작성한 밸런스 게임 목록 */
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<BalanceGame> balanceGames;
    
    /** 사용자가 투표한 목록 */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Vote> votes;
    
    /** 사용자가 작성한 댓글 목록 */
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Comment> comments;
    
    /** 사용자 권한 열거형 */
    public enum Role {
        USER, ADMIN
    }
    
    /** OAuth 제공자 열거형 */
    public enum Provider {
        GOOGLE, KAKAO, NAVER
    }
}