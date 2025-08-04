package com.drink.balancegame.repository;

import com.drink.balancegame.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.drink.balancegame.service.DtoConversionService;

/**
 * 투표 리포지토리
 * 투표 정보에 대한 데이터베이스 접근을 담당
 */
@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    
    /**
     * 특정 사용자의 특정 게임에 대한 투표 조회
     * @param userId 사용자 ID
     * @param balanceGameId 밸런스 게임 ID
     * @return 투표 정보
     */
    Optional<Vote> findByUserIdAndBalanceGameId(Long userId, Long balanceGameId);
    
    /**
     * 특정 게임의 모든 투표 조회
     * @param balanceGameId 밸런스 게임 ID
     * @return 투표 목록
     */
    List<Vote> findByBalanceGameId(Long balanceGameId);
    
    /**
     * 특정 사용자의 모든 투표 조회
     * @param userId 사용자 ID
     * @return 투표 목록
     */
    List<Vote> findByUserId(Long userId);
    
    /**
     * 특정 게임의 옵션 A 투표 수 조회
     * @param balanceGameId 밸런스 게임 ID
     * @return 옵션 A 투표 수
     */
    @Query("SELECT COUNT(v) FROM Vote v WHERE v.balanceGame.id = :balanceGameId AND v.selectedOption = com.drink.balancegame.entity.Vote$VoteOption.A")
    Long countOptionAVotesByBalanceGameId(@Param("balanceGameId") Long balanceGameId);
    
    /**
     * 특정 게임의 옵션 B 투표 수 조회
     * @param balanceGameId 밸런스 게임 ID
     * @return 옵션 B 투표 수
     */
    @Query("SELECT COUNT(v) FROM Vote v WHERE v.balanceGame.id = :balanceGameId AND v.selectedOption = com.drink.balancegame.entity.Vote$VoteOption.B")
    Long countOptionBVotesByBalanceGameId(@Param("balanceGameId") Long balanceGameId);
    
    /**
     * 특정 게임의 총 투표 수 조회
     * @param balanceGameId 밸런스 게임 ID
     * @return 총 투표 수
     */
    Long countByBalanceGameId(Long balanceGameId);
    
    /**
     * 특정 사용자의 총 투표 수 조회
     * @param userId 사용자 ID
     * @return 총 투표 수
     */
    Long countByUserId(Long userId);
    
    /**
     * 여러 게임의 투표 통계를 한번에 조회 (N+1 방지)
     */
    @Query("""
        SELECT v.balanceGame.id,
               SUM(CASE WHEN v.selectedOption = com.drink.balancegame.entity.Vote$VoteOption.A THEN 1 ELSE 0 END),
               SUM(CASE WHEN v.selectedOption = com.drink.balancegame.entity.Vote$VoteOption.B THEN 1 ELSE 0 END),
               COUNT(v)
        FROM Vote v 
        WHERE v.balanceGame.id IN :gameIds 
        GROUP BY v.balanceGame.id
        """)
    List<Object[]> getVoteStatsByGameIdsRaw(@Param("gameIds") List<Long> gameIds);
    
    default Map<Long, DtoConversionService.VoteStats> getVoteStatsByGameIds(List<Long> gameIds) {
        if (gameIds == null || gameIds.isEmpty()) {
            return Map.of();
        }
        Map<Long, DtoConversionService.VoteStats> result = new HashMap<>();
        List<Object[]> rawStats = getVoteStatsByGameIdsRaw(gameIds);
        for (Object[] row : rawStats) {
            Long gameId = (Long) row[0];
            Long optionACount = (Long) row[1];
            Long optionBCount = (Long) row[2];
            Long totalCount = (Long) row[3];
            result.put(gameId, new DtoConversionService.VoteStats(optionACount, optionBCount, totalCount));
        }
        return result;
    }
    
    /**
     * 사용자의 투표 정보를 여러 게임에 대해 한번에 조회 (N+1 방지)
     */
    @Query("SELECT v.balanceGame.id, v.selectedOption FROM Vote v WHERE v.user.id = :userId AND v.balanceGame.id IN :gameIds")
    List<Object[]> findUserVotesByGameIdsRaw(@Param("userId") Long userId, @Param("gameIds") List<Long> gameIds);
    
    default Map<Long, String> findUserVotesByGameIds(Long userId, List<Long> gameIds) {
        if (gameIds == null || gameIds.isEmpty()) {
            return Map.of();
        }
        return findUserVotesByGameIdsRaw(userId, gameIds).stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> row[1].toString(),
                        (existing, replacement) -> existing
                ));
    }
}