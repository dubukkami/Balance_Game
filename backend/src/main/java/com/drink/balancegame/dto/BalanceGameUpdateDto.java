package com.drink.balancegame.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 밸런스 게임 수정 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceGameUpdateDto {
    
    /** 게임 제목 */
    private String title;
    
    /** 게임 설명 */
    private String description;
}