package com.drink.balancegame.controller;

import com.drink.balancegame.dto.UserProfileDto;
import com.drink.balancegame.dto.UserProfileUpdateDto;
import com.drink.balancegame.dto.UserStatsDto;
import com.drink.balancegame.entity.User;
import com.drink.balancegame.repository.UserRepository;
import com.drink.balancegame.repository.BalanceGameRepository;
import com.drink.balancegame.repository.VoteRepository;
import com.drink.balancegame.repository.CommentRepository;
import com.drink.balancegame.service.DtoConversionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 관리 컨트롤러
 * 사용자 프로필 관련 API 엔드포인트를 제공
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserRepository userRepository;
    private final BalanceGameRepository balanceGameRepository;
    private final VoteRepository voteRepository;
    private final CommentRepository commentRepository;
    private final DtoConversionService dtoConversionService;
    
    /**
     * 모든 사용자 조회
     * @return 사용자 목록
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }
    
    /**
     * 특정 사용자 조회
     * @param id 사용자 ID
     * @return 사용자 정보
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileDto> getUser(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        
        if (user.isPresent()) {
            UserProfileDto userProfile = dtoConversionService.convertToUserProfileDto(user.get());
            return ResponseEntity.ok(userProfile);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 사용자 프로필 업데이트
     * @param id 사용자 ID
     * @param updateDto 업데이트할 프로필 정보
     * @return 업데이트된 사용자 정보
     */
    @PutMapping("/{id}/profile")
    public ResponseEntity<UserProfileDto> updateUserProfile(
            @PathVariable Long id,
            @RequestBody UserProfileUpdateDto updateDto) {
        
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        User user = userOpt.get();
        
        // 프로필 정보 업데이트
        if (updateDto.getNickname() != null) {
            user.setNickname(updateDto.getNickname());
        }
        if (updateDto.getBio() != null) {
            user.setBio(updateDto.getBio());
        }
        if (updateDto.getLocation() != null) {
            user.setLocation(updateDto.getLocation());
        }
        if (updateDto.getWebsite() != null) {
            user.setWebsite(updateDto.getWebsite());
        }
        if (updateDto.getProfileImageUrl() != null) {
            user.setProfileImageUrl(updateDto.getProfileImageUrl());
        }
        
        User updatedUser = userRepository.save(user);
        UserProfileDto userProfile = dtoConversionService.convertToUserProfileDto(updatedUser);
        
        return ResponseEntity.ok(userProfile);
    }
    
    /**
     * 사용자 활동 통계 조회
     * @param id 사용자 ID
     * @return 사용자 활동 통계
     */
    @GetMapping("/{id}/stats")
    public ResponseEntity<UserStatsDto> getUserStats(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        // 통계 계산
        Long gamesCreated = balanceGameRepository.countByAuthorId(id);
        Long totalVotes = voteRepository.countByUserId(id);
        Long commentsCount = commentRepository.countByAuthorId(id);
        
        // 받은 좋아요 수는 추후 구현
        Long likesReceived = 0L;
        
        UserStatsDto stats = UserStatsDto.builder()
                .gamesCreated(gamesCreated)
                .totalVotes(totalVotes)
                .commentsCount(commentsCount)
                .likesReceived(likesReceived)
                .build();
        
        return ResponseEntity.ok(stats);
    }
    
}

