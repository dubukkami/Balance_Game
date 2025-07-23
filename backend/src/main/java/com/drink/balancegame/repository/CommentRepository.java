package com.drink.balancegame.repository;

import com.drink.balancegame.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 댓글 리포지토리
 * 댓글 정보에 대한 데이터베이스 접근을 담당
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    /**
     * 특정 게임의 댓글 조회 (페이징)
     * @param balanceGameId 밸런스 게임 ID
     * @param pageable 페이징 정보
     * @return 페이징된 댓글 목록
     */
    Page<Comment> findByBalanceGameId(Long balanceGameId, Pageable pageable);
    
    /**
     * 특정 게임의 댓글 조회 (최신순)
     * @param balanceGameId 밸런스 게임 ID
     * @return 댓글 목록
     */
    List<Comment> findByBalanceGameIdOrderByCreatedAtDesc(Long balanceGameId);
    
    /**
     * 특정 사용자의 댓글 조회
     * @param authorId 작성자 ID
     * @param pageable 페이징 정보
     * @return 페이징된 댓글 목록
     */
    Page<Comment> findByAuthorId(Long authorId, Pageable pageable);
    
    /**
     * 특정 게임의 댓글 수 조회
     * @param balanceGameId 밸런스 게임 ID
     * @return 댓글 수
     */
    Long countByBalanceGameId(Long balanceGameId);
    
    /**
     * 특정 작성자의 댓글 수 조회
     * @param authorId 작성자 ID
     * @return 댓글 수
     */
    Long countByAuthorId(Long authorId);
    
    /**
     * 특정 게임의 최상위 댓글만 조회 (대댓글 제외)
     * @param balanceGameId 밸런스 게임 ID
     * @param pageable 페이징 정보
     * @return 페이징된 최상위 댓글 목록
     */
    Page<Comment> findByBalanceGameIdAndParentCommentIsNull(Long balanceGameId, Pageable pageable);
    
    /**
     * 특정 댓글의 대댓글 조회
     * @param parentCommentId 부모 댓글 ID
     * @return 대댓글 목록
     */
    List<Comment> findByParentCommentIdOrderByCreatedAtAsc(Long parentCommentId);
    
    /**
     * 특정 게임의 최상위 댓글만 조회 (최신순, 대댓글 제외)
     * @param balanceGameId 밸런스 게임 ID
     * @return 최상위 댓글 목록
     */
    List<Comment> findByBalanceGameIdAndParentCommentIsNullOrderByCreatedAtDesc(Long balanceGameId);
}