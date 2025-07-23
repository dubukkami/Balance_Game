package com.drink.balancegame.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 투표 응답 DTO
 * 투표 생성/변경/취소 결과를 담는 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteResponseDto {
    
    /**
     * 응답 메시지 ("vote_created", "vote_updated", "vote_cancelled")
     */
    private String message;
    
    /**
     * 투표 정보 (취소된 경우 null)
     */
    private VoteDto vote;
    
    /**
     * 성공 여부
     */
    private boolean success;
    
    public static VoteResponseDto created(VoteDto vote) {
        return VoteResponseDto.builder()
                .message("vote_created")
                .vote(vote)
                .success(true)
                .build();
    }
    
    public static VoteResponseDto updated(VoteDto vote) {
        return VoteResponseDto.builder()
                .message("vote_updated")
                .vote(vote)
                .success(true)
                .build();
    }
    
    public static VoteResponseDto cancelled() {
        return VoteResponseDto.builder()
                .message("vote_cancelled")
                .vote(null)
                .success(true)
                .build();
    }
}