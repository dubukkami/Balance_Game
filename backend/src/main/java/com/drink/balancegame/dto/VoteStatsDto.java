package com.drink.balancegame.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 투표 통계 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoteStatsDto {
    
    /** 밸런스 게임 ID */
    private Long balanceGameId;
    
    /** 옵션 A 투표수 */
    private Long optionAVotes;
    
    /** 옵션 B 투표수 */
    private Long optionBVotes;
    
    /** 총 투표수 */
    private Long totalVotes;
    
    /** 옵션 A 투표 비율 (%) */
    private Double optionAPercentage;
    
    /** 옵션 B 투표 비율 (%) */
    private Double optionBPercentage;
}