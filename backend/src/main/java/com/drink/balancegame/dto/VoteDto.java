package com.drink.balancegame.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 투표 DTO
 * 투표 정보 전송을 위한 데이터 객체
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoteDto {
    
    /** 투표 ID */
    private Long id;
    
    /** 선택된 옵션 (A 또는 B) */
    private String selectedOption;
    
    /** 투표 일시 */
    private LocalDateTime createdAt;
    
    /** 투표한 사용자 ID */
    private Long userId;
    
    /** 투표한 사용자명 */
    private String userUsername;
    
    /** 투표한 사용자 닉네임 */
    private String userNickname;
    
    /** 밸런스 게임 ID */
    private Long balanceGameId;
    
    /** 게임 제목 */
    private String gameTitle;
}

