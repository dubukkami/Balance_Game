package com.drink.balancegame.service;

import com.drink.balancegame.entity.BalanceGame;
import com.drink.balancegame.entity.Comment;
import com.drink.balancegame.entity.User;
import com.drink.balancegame.repository.BalanceGameRepository;
import com.drink.balancegame.repository.CommentRepository;
import com.drink.balancegame.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

/**
 * 공통 유효성 검사 서비스
 * 엔티티 존재 확인 및 데이터 유효성 검사를 담당
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ValidationService {
    
    private final UserRepository userRepository;
    private final BalanceGameRepository balanceGameRepository;
    private final CommentRepository commentRepository;
    
    /**
     * 사용자 존재 확인
     * @param userId 사용자 ID
     * @return User 엔티티
     * @throws EntityNotFoundException 사용자가 존재하지 않을 때
     */
    public User validateUserExists(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다.");
        }
        
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 사용자 - 사용자 ID: {}", userId);
                    return new EntityNotFoundException("사용자를 찾을 수 없습니다. ID: " + userId);
                });
    }
    
    /**
     * 밸런스 게임 존재 확인
     * @param gameId 게임 ID
     * @return BalanceGame 엔티티
     * @throws EntityNotFoundException 게임이 존재하지 않을 때
     */
    public BalanceGame validateBalanceGameExists(Long gameId) {
        if (gameId == null) {
            throw new IllegalArgumentException("게임 ID는 필수입니다.");
        }
        
        return balanceGameRepository.findById(gameId)
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 게임 - 게임 ID: {}", gameId);
                    return new EntityNotFoundException("게임을 찾을 수 없습니다. ID: " + gameId);
                });
    }
    
    /**
     * 댓글 존재 확인
     * @param commentId 댓글 ID
     * @return Comment 엔티티
     * @throws EntityNotFoundException 댓글이 존재하지 않을 때
     */
    public Comment validateCommentExists(Long commentId) {
        if (commentId == null) {
            throw new IllegalArgumentException("댓글 ID는 필수입니다.");
        }
        
        return commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 댓글 - 댓글 ID: {}", commentId);
                    return new EntityNotFoundException("댓글을 찾을 수 없습니다. ID: " + commentId);
                });
    }
    
    /**
     * 댓글 내용 유효성 검사
     * @param content 댓글 내용
     * @throws IllegalArgumentException 내용이 유효하지 않을 때
     */
    public void validateCommentContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("댓글 내용은 필수입니다.");
        }
        
        if (content.length() > 1000) {
            throw new IllegalArgumentException("댓글은 1000자를 초과할 수 없습니다.");
        }
    }
    
    /**
     * 투표 옵션 유효성 검사
     * @param selectedOption 선택된 옵션
     * @throws IllegalArgumentException 옵션이 유효하지 않을 때
     */
    public void validateVoteOption(String selectedOption) {
        if (selectedOption == null || selectedOption.trim().isEmpty()) {
            throw new IllegalArgumentException("투표 옵션은 필수입니다.");
        }
        
        if (!"A".equals(selectedOption) && !"B".equals(selectedOption)) {
            throw new IllegalArgumentException("유효하지 않은 투표 옵션입니다. A 또는 B만 가능합니다.");
        }
    }
    
    /**
     * 사용자 권한 확인 (작성자인지 확인)
     * @param entityAuthorId 엔티티 작성자 ID
     * @param requestUserId 요청 사용자 ID
     * @throws IllegalArgumentException 권한이 없을 때
     */
    public void validateAuthorPermission(Long entityAuthorId, Long requestUserId) {
        if (entityAuthorId == null || requestUserId == null) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다.");
        }
        
        if (!entityAuthorId.equals(requestUserId)) {
            log.warn("권한 없는 접근 시도 - 작성자 ID: {}, 요청자 ID: {}", entityAuthorId, requestUserId);
            throw new IllegalArgumentException("해당 작업을 수행할 권한이 없습니다.");
        }
    }
    
    /**
     * 페이지 매개변수 유효성 검사
     * @param page 페이지 번호
     * @param size 페이지 크기
     */
    public void validatePageParameters(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("페이지 번호는 0 이상이어야 합니다.");
        }
        
        if (size <= 0) {
            throw new IllegalArgumentException("페이지 크기는 1 이상이어야 합니다.");
        }
        
        if (size > 100) {
            throw new IllegalArgumentException("페이지 크기는 100을 초과할 수 없습니다.");
        }
    }
    
    /**
     * 이메일 형식 유효성 검사
     * @param email 이메일 주소
     * @throws IllegalArgumentException 이메일 형식이 유효하지 않을 때
     */
    public void validateEmailFormat(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("이메일은 필수입니다.");
        }
        
        String emailRegex = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$";
        if (!email.matches(emailRegex)) {
            throw new IllegalArgumentException("유효하지 않은 이메일 형식입니다.");
        }
    }
    
    /**
     * 사용자명 유효성 검사
     * @param username 사용자명
     * @throws IllegalArgumentException 사용자명이 유효하지 않을 때
     */
    public void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("사용자명은 필수입니다.");
        }
        
        if (username.length() < 3 || username.length() > 20) {
            throw new IllegalArgumentException("사용자명은 3-20자 사이여야 합니다.");
        }
        
        // 영문, 숫자, 언더스코어만 허용
        String usernameRegex = "^[a-zA-Z0-9_]+$";
        if (!username.matches(usernameRegex)) {
            throw new IllegalArgumentException("사용자명은 영문, 숫자, 언더스코어만 사용할 수 있습니다.");
        }
    }
    
    /**
     * 닉네임 유효성 검사
     * @param nickname 닉네임
     * @throws IllegalArgumentException 닉네임이 유효하지 않을 때
     */
    public void validateNickname(String nickname) {
        if (nickname == null || nickname.trim().isEmpty()) {
            return; // 닉네임은 선택사항
        }
        
        if (nickname.length() > 50) {
            throw new IllegalArgumentException("닉네임은 50자를 초과할 수 없습니다.");
        }
    }
}