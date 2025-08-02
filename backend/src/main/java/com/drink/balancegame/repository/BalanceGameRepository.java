package com.drink.balancegame.repository;

import com.drink.balancegame.entity.BalanceGame;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 밸런스 게임 리포지토리
 * 밸런스 게임 정보에 대한 데이터베이스 접근을 담당
 */
@Repository
public interface BalanceGameRepository extends JpaRepository<BalanceGame, Long> {
    
    /**
     * 작성자별 밸런스 게임 조회
     * @param authorId 작성자 ID
     * @param pageable 페이징 정보
     * @return 페이징된 밸런스 게임 목록
     */
    Page<BalanceGame> findByAuthorId(Long authorId, Pageable pageable);
    
    /**
     * 제목으로 밸런스 게임 검색
     * @param title 제목 키워드
     * @param pageable 페이징 정보
     * @return 페이징된 밸런스 게임 목록
     */
    Page<BalanceGame> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    
    /**
     * 조회수 기준 인기 게임 조회
     * @param pageable 페이징 정보
     * @return 페이징된 밸런스 게임 목록
     */
    Page<BalanceGame> findByOrderByViewCountDesc(Pageable pageable);
    
    /**
     * 최신 게임 조회
     * @param pageable 페이징 정보
     * @return 페이징된 밸런스 게임 목록
     */
    Page<BalanceGame> findByOrderByCreatedAtDesc(Pageable pageable);
    
    /**
     * 투표 수가 많은 게임 조회 (JOIN 쿼리)
     * @param pageable 페이징 정보
     * @return 페이징된 밸런스 게임 목록
     */
    @Query("SELECT bg FROM BalanceGame bg LEFT JOIN bg.votes v GROUP BY bg.id ORDER BY COUNT(v) DESC")
    Page<BalanceGame> findByOrderByVoteCountDesc(Pageable pageable);
    
    /**
     * 특정 작성자가 작성한 게임 수 조회
     * @param authorId 작성자 ID
     * @return 게임 수
     */
    Long countByAuthorId(Long authorId);
    
    /**
     * 게임 목록과 모든 통계를 한 번에 조회 (N+1 쿼리 해결) - 최신순
     * @param pageable 페이징 정보
     * @return 통계 정보가 포함된 게임 목록
     */
    @Query("""
        SELECT bg, 
               COALESCE(likes.likeCount, 0) as likeCount,
               COALESCE(votes.optionACount, 0) as optionACount, 
               COALESCE(votes.optionBCount, 0) as optionBCount,
               COALESCE(comments.commentCount, 0) as commentCount
        FROM BalanceGame bg
        LEFT JOIN (
            SELECT l.balanceGame.id as gameId, COUNT(l) as likeCount 
            FROM Like l 
            GROUP BY l.balanceGame.id
        ) likes ON bg.id = likes.gameId
        LEFT JOIN (
            SELECT v.balanceGame.id as gameId,
                   SUM(CASE WHEN v.selectedOption = 'A' THEN 1 ELSE 0 END) as optionACount,
                   SUM(CASE WHEN v.selectedOption = 'B' THEN 1 ELSE 0 END) as optionBCount
            FROM Vote v 
            GROUP BY v.balanceGame.id
        ) votes ON bg.id = votes.gameId
        LEFT JOIN (
            SELECT c.balanceGame.id as gameId, COUNT(c) as commentCount 
            FROM Comment c 
            GROUP BY c.balanceGame.id
        ) comments ON bg.id = comments.gameId
        ORDER BY bg.createdAt DESC
        """)
    Page<Object[]> findAllWithStats(Pageable pageable);
    
    /**
     * 게임 목록과 모든 통계를 한 번에 조회 (N+1 쿼리 해결) - 인기순 (조회수)
     * @param pageable 페이징 정보
     * @return 통계 정보가 포함된 게임 목록
     */
    @Query("""
        SELECT bg, 
               COALESCE(likes.likeCount, 0) as likeCount,
               COALESCE(votes.optionACount, 0) as optionACount, 
               COALESCE(votes.optionBCount, 0) as optionBCount,
               COALESCE(comments.commentCount, 0) as commentCount
        FROM BalanceGame bg
        LEFT JOIN (
            SELECT l.balanceGame.id as gameId, COUNT(l) as likeCount 
            FROM Like l 
            GROUP BY l.balanceGame.id
        ) likes ON bg.id = likes.gameId
        LEFT JOIN (
            SELECT v.balanceGame.id as gameId,
                   SUM(CASE WHEN v.selectedOption = 'A' THEN 1 ELSE 0 END) as optionACount,
                   SUM(CASE WHEN v.selectedOption = 'B' THEN 1 ELSE 0 END) as optionBCount
            FROM Vote v 
            GROUP BY v.balanceGame.id
        ) votes ON bg.id = votes.gameId
        LEFT JOIN (
            SELECT c.balanceGame.id as gameId, COUNT(c) as commentCount 
            FROM Comment c 
            GROUP BY c.balanceGame.id
        ) comments ON bg.id = comments.gameId
        ORDER BY bg.viewCount DESC
        """)
    Page<Object[]> findAllWithStatsOrderByViewCount(Pageable pageable);
    
    /**
     * 게임 목록과 모든 통계를 한 번에 조회 (N+1 쿼리 해결) - 투표수순
     * @param pageable 페이징 정보
     * @return 통계 정보가 포함된 게임 목록
     */
    @Query("""
        SELECT bg, 
               COALESCE(likes.likeCount, 0) as likeCount,
               COALESCE(votes.optionACount, 0) as optionACount, 
               COALESCE(votes.optionBCount, 0) as optionBCount,
               COALESCE(comments.commentCount, 0) as commentCount
        FROM BalanceGame bg
        LEFT JOIN (
            SELECT l.balanceGame.id as gameId, COUNT(l) as likeCount 
            FROM Like l 
            GROUP BY l.balanceGame.id
        ) likes ON bg.id = likes.gameId
        LEFT JOIN (
            SELECT v.balanceGame.id as gameId,
                   SUM(CASE WHEN v.selectedOption = 'A' THEN 1 ELSE 0 END) as optionACount,
                   SUM(CASE WHEN v.selectedOption = 'B' THEN 1 ELSE 0 END) as optionBCount
            FROM Vote v 
            GROUP BY v.balanceGame.id
        ) votes ON bg.id = votes.gameId
        LEFT JOIN (
            SELECT c.balanceGame.id as gameId, COUNT(c) as commentCount 
            FROM Comment c 
            GROUP BY c.balanceGame.id
        ) comments ON bg.id = comments.gameId
        ORDER BY (COALESCE(votes.optionACount, 0) + COALESCE(votes.optionBCount, 0)) DESC
        """)
    Page<Object[]> findAllWithStatsOrderByVoteCount(Pageable pageable);
    
    /**
     * 게임 목록과 모든 통계를 한 번에 조회 (N+1 쿼리 해결) - 좋아요수순
     * @param pageable 페이징 정보
     * @return 통계 정보가 포함된 게임 목록
     */
    @Query("""
        SELECT bg, 
               COALESCE(likes.likeCount, 0) as likeCount,
               COALESCE(votes.optionACount, 0) as optionACount, 
               COALESCE(votes.optionBCount, 0) as optionBCount,
               COALESCE(comments.commentCount, 0) as commentCount
        FROM BalanceGame bg
        LEFT JOIN (
            SELECT l.balanceGame.id as gameId, COUNT(l) as likeCount 
            FROM Like l 
            GROUP BY l.balanceGame.id
        ) likes ON bg.id = likes.gameId
        LEFT JOIN (
            SELECT v.balanceGame.id as gameId,
                   SUM(CASE WHEN v.selectedOption = 'A' THEN 1 ELSE 0 END) as optionACount,
                   SUM(CASE WHEN v.selectedOption = 'B' THEN 1 ELSE 0 END) as optionBCount
            FROM Vote v 
            GROUP BY v.balanceGame.id
        ) votes ON bg.id = votes.gameId
        LEFT JOIN (
            SELECT c.balanceGame.id as gameId, COUNT(c) as commentCount 
            FROM Comment c 
            GROUP BY c.balanceGame.id
        ) comments ON bg.id = comments.gameId
        ORDER BY COALESCE(likes.likeCount, 0) DESC
        """)
    Page<Object[]> findAllWithStatsOrderByLikes(Pageable pageable);
    
    /**
     * 게임 목록과 모든 통계를 한 번에 조회 (N+1 쿼리 해결) - 기간별 좋아요수순
     * @param pageable 페이징 정보
     * @param startDate 시작 날짜
     * @return 통계 정보가 포함된 게임 목록
     */
    @Query("""
        SELECT bg, 
               COALESCE((SELECT COUNT(l) FROM Like l WHERE l.balanceGame.id = bg.id), 0) as totalLikeCount,
               COALESCE((SELECT SUM(CASE WHEN v.selectedOption = 'A' THEN 1 ELSE 0 END) FROM Vote v WHERE v.balanceGame.id = bg.id), 0) as optionACount,
               COALESCE((SELECT SUM(CASE WHEN v.selectedOption = 'B' THEN 1 ELSE 0 END) FROM Vote v WHERE v.balanceGame.id = bg.id), 0) as optionBCount,
               COALESCE((SELECT COUNT(c) FROM Comment c WHERE c.balanceGame.id = bg.id), 0) as commentCount,
               COALESCE((SELECT COUNT(l) FROM Like l WHERE l.balanceGame.id = bg.id AND l.createdAt >= :startDate), 0) as periodLikeCount
        FROM BalanceGame bg
        ORDER BY COALESCE((SELECT COUNT(l) FROM Like l WHERE l.balanceGame.id = bg.id AND l.createdAt >= :startDate), 0) DESC, bg.createdAt DESC
        """)
    Page<Object[]> findAllWithStatsOrderByLikesWithPeriod(Pageable pageable, @Param("startDate") LocalDateTime startDate);
    
    /**
     * 게임 목록과 모든 통계를 한 번에 조회 (N+1 쿼리 해결) - 기간별 좋아요수순 (period 파라미터 버전)
     * @param pageable 페이징 정보
     * @param period 기간 (daily, weekly, monthly, all)
     * @return 통계 정보가 포함된 게임 목록
     */
    default Page<Object[]> findAllWithStatsOrderByLikesByPeriod(Pageable pageable, String period) {
        switch (period) {
            case "daily":
                return findAllWithStatsOrderByLikesWithPeriod(pageable, LocalDateTime.now().minusHours(24));
            case "weekly":
                return findAllWithStatsOrderByLikesWithPeriod(pageable, LocalDateTime.now().minusHours(168)); // 7*24
            case "monthly":
                return findAllWithStatsOrderByLikesWithPeriod(pageable, LocalDateTime.now().minusHours(720)); // 30*24
            default: // "all"의 경우
                return findAllWithStatsOrderByLikes(pageable);
        }
    }
}