package com.drink.balancegame.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 댓글 DTO
 * 댓글 정보 전송을 위한 데이터 객체
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    
    /** 댓글 ID */
    private Long id;
    
    /** 댓글 내용 */
    private String content;
    
    /** 생성일시 */
    private LocalDateTime createdAt;
    
    /** 수정일시 */
    private LocalDateTime updatedAt;
    
    /** 작성자 ID */
    private Long authorId;
    
    /** 작성자명 */
    private String authorUsername;
    
    /** 작성자 닉네임 */
    private String authorNickname;
    
    /** 부모 댓글 ID (대댓글인 경우) */
    private Long parentCommentId;
    
    /** 댓글 깊이 (0: 원댓글, 1: 대댓글) */
    private Integer depth;
    
    /** 대댓글 목록 */
    private List<CommentDto> replies;
    
    /** 밸런스 게임 ID */
    private Long balanceGameId;
    
    /** 게임 제목 */
    private String gameTitle;
    
    /** 추천 수 */
    private Long likeCount;
    
    /** 현재 사용자의 추천 여부 */
    private boolean isLiked;
}

