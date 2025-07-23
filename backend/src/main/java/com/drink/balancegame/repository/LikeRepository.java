package com.drink.balancegame.repository;

import com.drink.balancegame.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 추천 레포지토리
 */
@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    
    /**
     * 밸런스 게임 추천 수 조회
     */
    @Query("SELECT COUNT(l) FROM Like l WHERE l.balanceGame.id = :balanceGameId")
    Long countByBalanceGameId(@Param("balanceGameId") Long balanceGameId);
    
    /**
     * 댓글 추천 수 조회
     */
    @Query("SELECT COUNT(l) FROM Like l WHERE l.comment.id = :commentId")
    Long countByCommentId(@Param("commentId") Long commentId);
    
    /**
     * 사용자가 특정 밸런스 게임을 추천했는지 확인
     */
    @Query("SELECT l FROM Like l WHERE l.user.id = :userId AND l.balanceGame.id = :balanceGameId")
    Optional<Like> findByUserIdAndBalanceGameId(@Param("userId") Long userId, @Param("balanceGameId") Long balanceGameId);
    
    /**
     * 사용자가 특정 댓글을 추천했는지 확인
     */
    @Query("SELECT l FROM Like l WHERE l.user.id = :userId AND l.comment.id = :commentId")
    Optional<Like> findByUserIdAndCommentId(@Param("userId") Long userId, @Param("commentId") Long commentId);
    
    /**
     * 사용자의 밸런스 게임 추천 삭제
     */
    void deleteByUserIdAndBalanceGameId(Long userId, Long balanceGameId);
    
    /**
     * 사용자의 댓글 추천 삭제
     */
    void deleteByUserIdAndCommentId(Long userId, Long commentId);
}