package com.drink.balancegame.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 댓글 생성 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateDto {
    
    /** 댓글 내용 */
    private String content;
    
    /** 익명 작성 여부 */
    private boolean isAnonymous;
    
    /** 밸런스 게임 ID */
    private Long balanceGameId;
    
    /** 부모 댓글 ID (대댓글 작성시) */
    private Long parentCommentId;
}