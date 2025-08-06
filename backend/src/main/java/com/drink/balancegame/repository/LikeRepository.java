package com.drink.balancegame.repository;

import com.drink.balancegame.entity.BalanceGame;
import com.drink.balancegame.entity.Like;
import com.drink.balancegame.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
     * 특정 기간 이후의 밸런스 게임 추천 수 조회
     */
    @Query("SELECT COUNT(l) FROM Like l WHERE l.balanceGame.id = :balanceGameId AND l.createdAt > :after")
    Long countByBalanceGameIdAndCreatedAtAfter(@Param("balanceGameId") Long balanceGameId, @Param("after") java.time.LocalDateTime after);
    
    
    /**
     * 사용자의 모든 게임 좋아요 삭제 (성능 최적화)
     */
    @Modifying
    @Query("DELETE FROM Like l WHERE l.user.id = :userId AND l.balanceGame IS NOT NULL")
    void deleteByUserIdAndBalanceGameIsNotNull(@Param("userId") Long userId);
    
    /**
     * 사용자의 모든 댓글 좋아요 삭제 (성능 최적화)
     */
    @Modifying
    @Query("DELETE FROM Like l WHERE l.user.id = :userId AND l.comment IS NOT NULL")
    void deleteByUserIdAndCommentIsNotNull(@Param("userId") Long userId);
    
    /**
     * 여러 게임의 좋아요 수를 한번에 조회 (N+1 방지)
     */
    @Query("SELECT l.balanceGame.id, COUNT(l) FROM Like l WHERE l.balanceGame.id IN :gameIds GROUP BY l.balanceGame.id")
    List<Object[]> countByBalanceGameIdInRaw(@Param("gameIds") List<Long> gameIds);
    
    default Map<Long, Long> countByBalanceGameIdIn(List<Long> gameIds) {
        return countByBalanceGameIdInRaw(gameIds).stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));
    }
    
    /**
     * 사용자가 좋아요한 게임 ID 목록 조회 (N+1 방지)
     */
    @Query("SELECT l.balanceGame.id FROM Like l WHERE l.user.id = :userId AND l.balanceGame.id IN :gameIds")
    List<Long> findLikedGameIdsByUserIdAndGameIdIn(@Param("userId") Long userId, @Param("gameIds") List<Long> gameIds);
}