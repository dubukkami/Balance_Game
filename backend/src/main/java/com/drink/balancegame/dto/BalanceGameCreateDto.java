package com.drink.balancegame.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 밸런스 게임 생성 DTO
 * 밸런스 게임 생성을 위한 데이터 객체
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceGameCreateDto {
    
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
}