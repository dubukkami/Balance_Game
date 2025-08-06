package com.drink.balancegame.service;

import com.drink.balancegame.entity.BalanceGame;
import com.drink.balancegame.entity.Comment;
import com.drink.balancegame.entity.Like;
import com.drink.balancegame.entity.User;
import com.drink.balancegame.repository.LikeRepository;
import com.drink.balancegame.service.ValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 좋아요 관련 비즈니스 로직 서비스
 * 게임 및 댓글의 좋아요/좋아요 취소 기능을 처리
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class LikeService {
    
    private final LikeRepository likeRepository;
    private final ValidationService validationService;
    
    /**
     * 게임 좋아요 토글 (좋아요/좋아요 취소)
     * @param userId 사용자 ID
     * @param balanceGameId 밸런스 게임 ID
     * @return "liked" 또는 "unliked"
     */
    @Transactional
    public String toggleGameLike(Long userId, Long balanceGameId) {
        log.debug("게임 좋아요 토글 - 사용자 ID: {}, 게임 ID: {}", userId, balanceGameId);
        
        // 사용자 및 게임 존재 확인
        User user = validationService.validateUserExists(userId);
        BalanceGame balanceGame = validationService.validateBalanceGameExists(balanceGameId);
        
        // 기존 좋아요 확인
        Optional<Like> existingLike = likeRepository.findByUserIdAndBalanceGameId(userId, balanceGameId);
        
        if (existingLike.isPresent()) {
            // 좋아요 취소
            likeRepository.delete(existingLike.get());
            log.info("게임 좋아요 취소 - 사용자 ID: {}, 게임 ID: {}", userId, balanceGameId);
            return "unliked";
        } else {
            // 좋아요 추가
            Like like = Like.builder()
                    .user(user)
                    .balanceGame(balanceGame)
                    .build();
            likeRepository.save(like);
            log.info("게임 좋아요 추가 - 사용자 ID: {}, 게임 ID: {}", userId, balanceGameId);
            return "liked";
        }
    }
    
    /**
     * 댓글 좋아요 토글 (좋아요/좋아요 취소)
     * @param userId 사용자 ID
     * @param commentId 댓글 ID
     * @return "liked" 또는 "unliked"
     */
    @Transactional
    public String toggleCommentLike(Long userId, Long commentId) {
        log.debug("댓글 좋아요 토글 - 사용자 ID: {}, 댓글 ID: {}", userId, commentId);
        
        // 사용자 및 댓글 존재 확인
        User user = validationService.validateUserExists(userId);
        Comment comment = validationService.validateCommentExists(commentId);
        
        // 기존 좋아요 확인
        Optional<Like> existingLike = likeRepository.findByUserIdAndCommentId(userId, commentId);
        
        if (existingLike.isPresent()) {
            // 좋아요 취소
            likeRepository.delete(existingLike.get());
            log.info("댓글 좋아요 취소 - 사용자 ID: {}, 댓글 ID: {}", userId, commentId);
            return "unliked";
        } else {
            // 좋아요 추가
            Like like = Like.builder()
                    .user(user)
                    .comment(comment)
                    .build();
            likeRepository.save(like);
            log.info("댓글 좋아요 추가 - 사용자 ID: {}, 댓글 ID: {}", userId, commentId);
            return "liked";
        }
    }
    
    /**
     * 게임의 좋아요 수 조회
     * @param balanceGameId 밸런스 게임 ID
     * @return 좋아요 수
     */
    public Long getGameLikeCount(Long balanceGameId) {
        log.debug("게임 좋아요 수 조회 - 게임 ID: {}", balanceGameId);
        return likeRepository.countByBalanceGameId(balanceGameId);
    }
    
    /**
     * 댓글의 좋아요 수 조회
     * @param commentId 댓글 ID
     * @return 좋아요 수
     */
    public Long getCommentLikeCount(Long commentId) {
        log.debug("댓글 좋아요 수 조회 - 댓글 ID: {}", commentId);
        return likeRepository.countByCommentId(commentId);
    }
    
    /**
     * 사용자가 특정 게임에 좋아요를 눌렀는지 확인
     * @param userId 사용자 ID
     * @param balanceGameId 밸런스 게임 ID
     * @return 좋아요 여부
     */
    public boolean isGameLikedByUser(Long userId, Long balanceGameId) {
        if (userId == null || balanceGameId == null) {
            return false;
        }
        
        log.debug("게임 좋아요 여부 확인 - 사용자 ID: {}, 게임 ID: {}", userId, balanceGameId);
        return likeRepository.findByUserIdAndBalanceGameId(userId, balanceGameId).isPresent();
    }
    
    /**
     * 사용자가 특정 댓글에 좋아요를 눌렀는지 확인
     * @param userId 사용자 ID
     * @param commentId 댓글 ID
     * @return 좋아요 여부
     */
    public boolean isCommentLikedByUser(Long userId, Long commentId) {
        if (userId == null || commentId == null) {
            return false;
        }
        
        log.debug("댓글 좋아요 여부 확인 - 사용자 ID: {}, 댓글 ID: {}", userId, commentId);
        return likeRepository.findByUserIdAndCommentId(userId, commentId).isPresent();
    }
    
    /**
     * 사용자의 모든 게임 좋아요 취소 (사용자 탈퇴시 사용)
     * @param userId 사용자 ID
     */
    @Transactional
    public void removeAllUserGameLikes(Long userId) {
        log.debug("사용자의 모든 게임 좋아요 삭제 - 사용자 ID: {}", userId);
        
        // 사용자 존재 확인
        validationService.validateUserExists(userId);
        
        // 사용자의 모든 게임 좋아요 삭제 (최적화된 쿼리 사용)
        likeRepository.deleteByUserIdAndBalanceGameIsNotNull(userId);
        
        log.info("사용자의 모든 게임 좋아요 삭제 완료 - 사용자 ID: {}", userId);
    }
    
    /**
     * 사용자의 모든 댓글 좋아요 취소 (사용자 탈퇴시 사용)
     * @param userId 사용자 ID
     */
    @Transactional
    public void removeAllUserCommentLikes(Long userId) {
        log.debug("사용자의 모든 댓글 좋아요 삭제 - 사용자 ID: {}", userId);
        
        // 사용자 존재 확인
        validationService.validateUserExists(userId);
        
        // 사용자의 모든 댓글 좋아요 삭제 (최적화된 쿼리 사용)
        likeRepository.deleteByUserIdAndCommentIsNotNull(userId);
        
        log.info("사용자의 모든 댓글 좋아요 삭제 완료 - 사용자 ID: {}", userId);
    }
}