package com.drink.balancegame.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 밸런스 게임 DTO
 * 밸런스 게임 정보 전송을 위한 데이터 객체
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceGameDto {
    
    /** 게임 ID */
    private Long id;
    
    /** 게임 제목 */
    private String title;
    
    /** 게임 설명 */
    private String description;
    
    /** 선택지 A */
    private String optionA;
    
    /** 선택지 B */
    private String optionB;
    
    /** 선택지 A 상세 설명 */
    private String optionADescription;
    
    /** 선택지 B 상세 설명 */
    private String optionBDescription;
    
    /** 조회수 */
    private Integer viewCount;
    
    /** 작성자 ID */
    private Long authorId;
    
    /** 작성자명 */
    private String authorUsername;
    
    /** 작성자 닉네임 */
    private String authorNickname;
    
    
    /** 생성일시 */
    private LocalDateTime createdAt;
    
    /** 수정일시 */
    private LocalDateTime updatedAt;
    
    /** 옵션 A 투표수 */
    private Long optionAVotes;
    
    /** 옵션 B 투표수 */
    private Long optionBVotes;
    
    /** 총 투표수 */
    private Long totalVotes;
    
    /** 댓글 수 */
    private Long commentCount;
    
    /** 추천 수 */
    private Long likeCount;
    
    /** 현재 사용자의 추천 여부 */
    private boolean isLiked;
    
    /** 현재 사용자의 투표 선택지 (로그인한 경우) */
    private String userVote;
}

