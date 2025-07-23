package com.drink.balancegame.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 투표 생성 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoteCreateDto {
    
    /** 선택된 옵션 (A 또는 B) */
    private String selectedOption;
    
    /** 밸런스 게임 ID */
    private Long balanceGameId;
}