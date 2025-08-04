package com.drink.balancegame.service;

import com.drink.balancegame.dto.BalanceGameDto;
import com.drink.balancegame.dto.CommentDto;
import com.drink.balancegame.dto.UserDto;
import com.drink.balancegame.dto.UserProfileDto;
import com.drink.balancegame.dto.VoteDto;
import com.drink.balancegame.entity.BalanceGame;
import com.drink.balancegame.entity.Comment;
import com.drink.balancegame.entity.Like;
import com.drink.balancegame.entity.User;
import com.drink.balancegame.entity.Vote;
import com.drink.balancegame.repository.CommentRepository;
import com.drink.balancegame.repository.LikeRepository;
import com.drink.balancegame.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
     * BalanceGame 엔티티를 BalanceGameDto로 변환 (N+1 방지를 위해 단일 게임용)
     * @param game 밸런스 게임 엔티티
     * @param userId 현재 사용자 ID
     * @return BalanceGameDto
     */
    public BalanceGameDto convertToBalanceGameDto(BalanceGame game, Long userId) {
        if (game == null) return null;
        return convertToBalanceGameDtos(List.of(game), userId).get(0);
    }
    
    /**
     * BalanceGame 리스트를 BalanceGameDto 리스트로 변환 (N+1 방지)
     * @param games 밸런스 게임 리스트
     * @param userId 현재 사용자 ID
     * @return BalanceGameDto 리스트
     */
    public List<BalanceGameDto> convertToBalanceGameDtos(List<BalanceGame> games, Long userId) {
        if (games == null || games.isEmpty()) return Collections.emptyList();
        
        List<Long> gameIds = games.stream().map(BalanceGame::getId).collect(Collectors.toList());
        
        // 배치로 한 번에 조회 - N+1 방지
        Map<Long, Long> likeCountMap = getLikeCountsByGameIds(gameIds);
        Map<Long, Long> commentCountMap = getCommentCountsByGameIds(gameIds);
        Map<Long, VoteStats> voteStatsMap = getVoteStatsByGameIds(gameIds);
        Map<Long, Boolean> userLikeMap = userId != null ? getUserLikesByGameIds(userId, gameIds) : Collections.emptyMap();
        Map<Long, String> userVoteMap = userId != null ? getUserVotesByGameIds(userId, gameIds) : Collections.emptyMap();
        
        return games.stream().map(game -> {
            Long gameId = game.getId();
            VoteStats voteStats = voteStatsMap.getOrDefault(gameId, new VoteStats(0L, 0L, 0L));
            
            return BalanceGameDto.builder()
                    .id(gameId)
                    .title(game.getTitle())
                    .description(game.getDescription())
                    .optionA(game.getOptionA())
                    .optionADescription(game.getOptionADescription())
                    .optionB(game.getOptionB())
                    .optionBDescription(game.getOptionBDescription())
                    .authorId(game.getAuthor() != null ? game.getAuthor().getId() : null)
                    .authorUsername(game.getAuthor() != null ? game.getAuthor().getUsername() : null)
                    .authorNickname(game.getAuthor() != null ? game.getAuthor().getNickname() : null)
                    .viewCount(game.getViewCount())
                    .createdAt(game.getCreatedAt())
                    .updatedAt(game.getUpdatedAt())
                    .likeCount(likeCountMap.getOrDefault(gameId, 0L))
                    .isLiked(userLikeMap.getOrDefault(gameId, false))
                    .optionAVotes(voteStats.getOptionACount())
                    .optionBVotes(voteStats.getOptionBCount())
                    .totalVotes(voteStats.getTotalCount())
                    .commentCount(commentCountMap.getOrDefault(gameId, 0L))
                    .userVote(userVoteMap.get(gameId))
                    .build();
        }).collect(Collectors.toList());
    }
    
    // 배치 조회 헬퍼 메서드들
    private Map<Long, Long> getLikeCountsByGameIds(List<Long> gameIds) {
        return likeRepository.countByBalanceGameIdIn(gameIds);
    }
    
    private Map<Long, Long> getCommentCountsByGameIds(List<Long> gameIds) {
        return commentRepository.countByBalanceGameIdIn(gameIds);
    }
    
    private Map<Long, VoteStats> getVoteStatsByGameIds(List<Long> gameIds) {
        return voteRepository.getVoteStatsByGameIds(gameIds);
    }
    
    private Map<Long, Boolean> getUserLikesByGameIds(Long userId, List<Long> gameIds) {
        List<Long> likedGameIds = likeRepository.findLikedGameIdsByUserIdAndGameIdIn(userId, gameIds);
        return likedGameIds.stream().collect(Collectors.toMap(
                gameId -> gameId,
                gameId -> true
        ));
    }
    
    private Map<Long, String> getUserVotesByGameIds(Long userId, List<Long> gameIds) {
        return voteRepository.findUserVotesByGameIds(userId, gameIds);
    }
    
    // VoteStats 내부 클래스
    public static class VoteStats {
        private final Long optionACount;
        private final Long optionBCount;
        private final Long totalCount;
        
        public VoteStats(Long optionACount, Long optionBCount, Long totalCount) {
            this.optionACount = optionACount;
            this.optionBCount = optionBCount;
            this.totalCount = totalCount;
        }
        
        public Long getOptionACount() { return optionACount; }
        public Long getOptionBCount() { return optionBCount; }
        public Long getTotalCount() { return totalCount; }
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
     * Comment 엔티티를 CommentDto로 변환 (대댓글 포함) - N+1 방지
     * @param comment 댓글 엔티티
     * @param userId 현재 사용자 ID
     * @return CommentDto (대댓글 포함)
     */
    public CommentDto convertToCommentDtoWithReplies(Comment comment, Long userId) {
        return convertToCommentDtosWithReplies(List.of(comment), userId).get(0);
    }
    
    /**
     * Comment 리스트를 CommentDto 리스트로 변환 (대댓글 포함, N+1 방지)
     * @param parentComments 부모 댓글 리스트
     * @param userId 현재 사용자 ID
     * @return CommentDto 리스트 (대댓글 포함)
     */
    public List<CommentDto> convertToCommentDtosWithReplies(List<Comment> parentComments, Long userId) {
        if (parentComments == null || parentComments.isEmpty()) return Collections.emptyList();
        
        List<Long> parentIds = parentComments.stream().map(Comment::getId).collect(Collectors.toList());
        
        // 모든 대댓글을 한 번에 조회 - N+1 방지
        List<Comment> allReplies = commentRepository.findByParentCommentIdIn(parentIds);
        Map<Long, List<Comment>> repliesMap = allReplies.stream()
                .collect(Collectors.groupingBy(comment -> comment.getParentComment().getId()));
        
        return parentComments.stream().map(parent -> {
            CommentDto dto = convertToCommentDto(parent, userId);
            if (dto != null) {
                List<Comment> replies = repliesMap.getOrDefault(parent.getId(), Collections.emptyList());
                List<CommentDto> replyDtos = replies.stream()
                        .map(reply -> convertToCommentDto(reply, userId))
                        .collect(Collectors.toList());
                dto.setReplies(replyDtos);
            }
            return dto;
        }).filter(Objects::nonNull).collect(Collectors.toList());
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