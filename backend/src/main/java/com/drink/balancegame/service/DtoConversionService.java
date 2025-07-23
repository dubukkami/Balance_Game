package com.drink.balancegame.service;

import com.drink.balancegame.dto.*;
import com.drink.balancegame.entity.*;
import com.drink.balancegame.repository.CommentRepository;
import com.drink.balancegame.repository.LikeRepository;
import com.drink.balancegame.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO 변환 서비스
 * 엔티티를 DTO로 변환하는 공통 로직을 제공
 * 중복된 변환 코드를 제거하고 일관성을 보장
 */
@Service
@RequiredArgsConstructor
public class DtoConversionService {
    
    private final VoteRepository voteRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    
    /**
     * User 엔티티를 UserDto로 변환
     * @param user 사용자 엔티티
     * @return UserDto
     */
    public UserDto convertToUserDto(User user) {
        if (user == null) return null;
        
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .role(user.getRole().name())
                .provider(user.getProvider().name())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
    
    /**
     * User 엔티티를 UserProfileDto로 변환
     * @param user 사용자 엔티티
     * @return UserProfileDto
     */
    public UserProfileDto convertToUserProfileDto(User user) {
        if (user == null) return null;
        
        return UserProfileDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .bio(user.getBio())
                .location(user.getLocation())
                .website(user.getWebsite())
                .provider(user.getProvider())
                .createdAt(user.getCreatedAt())
                .build();
    }
    
    /**
     * BalanceGame 엔티티를 BalanceGameDto로 변환
     * @param game 밸런스 게임 엔티티
     * @param userId 현재 사용자 ID (좋아요 및 투표 상태 확인용)
     * @return BalanceGameDto
     */
    public BalanceGameDto convertToBalanceGameDto(BalanceGame game, Long userId) {
        if (game == null) return null;
        
        // 좋아요 정보 조회
        Long likeCount = likeRepository.countByBalanceGameId(game.getId());
        boolean isLiked = userId != null && 
            likeRepository.findByUserIdAndBalanceGameId(userId, game.getId()).isPresent();
        
        // 투표 정보 조회
        Long optionAVotes = voteRepository.countOptionAVotesByBalanceGameId(game.getId());
        Long optionBVotes = voteRepository.countOptionBVotesByBalanceGameId(game.getId());
        Long totalVotes = voteRepository.countByBalanceGameId(game.getId());
        
        // 사용자 투표 상태 확인
        final String[] userVote = {null};
        if (userId != null) {
            voteRepository.findByUserIdAndBalanceGameId(userId, game.getId())
                .ifPresent(vote -> userVote[0] = vote.getSelectedOption().name());
        }
        
        // 댓글 수 조회
        Long commentCount = commentRepository.countByBalanceGameId(game.getId());
        
        return BalanceGameDto.builder()
                .id(game.getId())
                .title(game.getTitle())
                .description(game.getDescription())
                .optionA(game.getOptionA())
                .optionADescription(game.getOptionADescription())
                .optionB(game.getOptionB())
                .optionBDescription(game.getOptionBDescription())
                .authorId(game.getAuthor().getId())
                .authorUsername(game.getAuthor().getUsername())
                .authorNickname(game.getAuthor().getNickname())
                .viewCount(game.getViewCount())
                .createdAt(game.getCreatedAt())
                .updatedAt(game.getUpdatedAt())
                .likeCount(likeCount)
                .isLiked(isLiked)
                .optionAVotes(optionAVotes)
                .optionBVotes(optionBVotes)
                .totalVotes(totalVotes)
                .commentCount(commentCount)
                .userVote(userVote[0])
                .build();
    }
    
    /**
     * Comment 엔티티를 CommentDto로 변환
     * @param comment 댓글 엔티티
     * @param userId 현재 사용자 ID (좋아요 상태 확인용)
     * @return CommentDto
     */
    public CommentDto convertToCommentDto(Comment comment, Long userId) {
        if (comment == null) return null;
        
        // 좋아요 정보 조회
        Long likeCount = likeRepository.countByCommentId(comment.getId());
        boolean isLiked = userId != null && 
            likeRepository.findByUserIdAndCommentId(userId, comment.getId()).isPresent();
        
        return CommentDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .authorId(comment.getAuthor().getId())
                .authorUsername(comment.getAuthor().getUsername())
                .authorNickname(comment.getAuthor().getNickname())
                .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null)
                .depth(comment.getDepth())
                .balanceGameId(comment.getBalanceGame().getId())
                .gameTitle(comment.getBalanceGame().getTitle())
                .likeCount(likeCount)
                .isLiked(isLiked)
                .build();
    }
    
    /**
     * Comment 엔티티를 CommentDto로 변환 (대댓글 포함)
     * @param comment 댓글 엔티티
     * @param userId 현재 사용자 ID
     * @return CommentDto (대댓글 포함)
     */
    public CommentDto convertToCommentDtoWithReplies(Comment comment, Long userId) {
        CommentDto dto = convertToCommentDto(comment, userId);
        if (dto == null) return null;
        
        // 대댓글 조회 및 변환
        List<Comment> replies = commentRepository.findByParentCommentIdOrderByCreatedAtAsc(comment.getId());
        List<CommentDto> replyDtos = replies.stream()
                .map(reply -> convertToCommentDto(reply, userId))
                .collect(Collectors.toList());
        
        dto.setReplies(replyDtos);
        return dto;
    }
    
    /**
     * Vote 엔티티를 VoteDto로 변환
     * @param vote 투표 엔티티
     * @return VoteDto
     */
    public VoteDto convertToVoteDto(Vote vote) {
        if (vote == null) return null;
        
        return VoteDto.builder()
                .id(vote.getId())
                .selectedOption(vote.getSelectedOption().name())
                .createdAt(vote.getCreatedAt())
                .userId(vote.getUser().getId())
                .userUsername(vote.getUser().getUsername())
                .userNickname(vote.getUser().getNickname())
                .balanceGameId(vote.getBalanceGame().getId())
                .gameTitle(vote.getBalanceGame().getTitle())
                .build();
    }
}