package com.drink.balancegame.repository;

import com.drink.balancegame.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
}