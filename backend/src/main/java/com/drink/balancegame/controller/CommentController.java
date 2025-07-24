package com.drink.balancegame.controller;

import com.drink.balancegame.dto.CommentDto;
import com.drink.balancegame.dto.CommentCreateDto;
import com.drink.balancegame.dto.CommentUpdateDto;
import com.drink.balancegame.entity.BalanceGame;
import com.drink.balancegame.entity.Comment;
import com.drink.balancegame.entity.Like;
import com.drink.balancegame.entity.User;
import com.drink.balancegame.repository.BalanceGameRepository;
import com.drink.balancegame.repository.CommentRepository;
import com.drink.balancegame.repository.LikeRepository;
import com.drink.balancegame.repository.UserRepository;
import com.drink.balancegame.service.DtoConversionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 댓글 컨트롤러
 * 댓글 관련 API 엔드포인트를 제공
 */
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BalanceGameRepository balanceGameRepository;
    private final LikeRepository likeRepository;
    private final DtoConversionService dtoConversionService;
    
    /**
     * 특정 게임의 최상위 댓글 조회 (페이징, 대댓글 포함)
     * @param balanceGameId 밸런스 게임 ID
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 페이징된 최상위 댓글 목록 (대댓글 포함)
     */
    @GetMapping("/game/{balanceGameId}")
    public ResponseEntity<Page<CommentDto>> getCommentsByGame(
            @PathVariable Long balanceGameId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long userId) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Comment> comments = commentRepository.findByBalanceGameIdAndParentCommentIsNull(balanceGameId, pageable);
        
        Page<CommentDto> commentDtos = comments.map(comment -> dtoConversionService.convertToCommentDtoWithReplies(comment, userId));
        return ResponseEntity.ok(commentDtos);
    }
    
    /**
     * 특정 게임의 최상위 댓글 조회 (리스트, 대댓글 포함)
     * @param balanceGameId 밸런스 게임 ID
     * @return 최상위 댓글 목록 (대댓글 포함)
     */
    @GetMapping("/game/{balanceGameId}/list")
    public ResponseEntity<List<CommentDto>> getCommentsByGameList(
            @PathVariable Long balanceGameId,
            @RequestParam(required = false) Long userId) {
        List<Comment> comments = commentRepository.findByBalanceGameIdAndParentCommentIsNullOrderByCreatedAtDesc(balanceGameId);
        List<CommentDto> commentDtos = comments.stream()
                .map(comment -> dtoConversionService.convertToCommentDtoWithReplies(comment, userId))
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(commentDtos);
    }
    
    /**
     * 특정 댓글의 대댓글 목록 조회
     * @param parentCommentId 부모 댓글 ID
     * @return 대댓글 목록
     */
    @GetMapping("/{parentCommentId}/replies")
    public ResponseEntity<List<CommentDto>> getRepliesByParentComment(
            @PathVariable Long parentCommentId,
            @RequestParam(required = false) Long userId) {
        List<Comment> replies = commentRepository.findByParentCommentIdOrderByCreatedAtAsc(parentCommentId);
        List<CommentDto> replyDtos = replies.stream()
                .map(reply -> dtoConversionService.convertToCommentDto(reply, userId))
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(replyDtos);
    }
    
    /**
     * 특정 사용자의 모든 댓글 조회
     * @param userId 사용자 ID
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 페이징된 댓글 목록
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<CommentDto>> getCommentsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Comment> comments = commentRepository.findByAuthorId(userId, pageable);
        
        Page<CommentDto> commentDtos = comments.map(comment -> dtoConversionService.convertToCommentDto(comment, null));
        return ResponseEntity.ok(commentDtos);
    }
    
    /**
     * 특정 댓글 조회
     * @param id 댓글 ID
     * @return 댓글 정보
     */
    @GetMapping("/{id}")
    public ResponseEntity<CommentDto> getComment(@PathVariable Long id) {
        Optional<Comment> comment = commentRepository.findById(id);
        
        if (comment.isPresent()) {
            CommentDto dto = dtoConversionService.convertToCommentDto(comment.get(), null);
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 댓글 생성
     * @param createDto 댓글 생성 정보
     * @param authorId 작성자 ID (실제로는 인증된 사용자에서 가져와야 함)
     * @return 생성된 댓글 정보
     */
    @PostMapping
    public ResponseEntity<CommentDto> createComment(
            @RequestBody CommentCreateDto createDto,
            @RequestParam Long authorId) {
        
        // 작성자 존재 확인
        Optional<User> author = userRepository.findById(authorId);
        if (author.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        // 밸런스 게임 존재 확인
        Optional<BalanceGame> balanceGame = balanceGameRepository.findById(createDto.getBalanceGameId());
        if (balanceGame.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        // 댓글 내용 유효성 검사
        if (createDto.getContent() == null || createDto.getContent().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        Comment.CommentBuilder commentBuilder = Comment.builder()
                .content(createDto.getContent())
                .author(author.get())
                .balanceGame(balanceGame.get());
        
        // 대댓글인 경우 부모 댓글 설정
        if (createDto.getParentCommentId() != null) {
            Optional<Comment> parentComment = commentRepository.findById(createDto.getParentCommentId());
            if (parentComment.isPresent()) {
                commentBuilder.parentComment(parentComment.get())
                        .depth(1); // 대댓글은 깊이 1로 고정
            }
        }
        
        Comment comment = commentBuilder.build();
        
        Comment savedComment = commentRepository.save(comment);
        CommentDto dto = dtoConversionService.convertToCommentDto(savedComment, null);
        
        return ResponseEntity.ok(dto);
    }
    
    /**
     * 댓글 수정
     * @param id 댓글 ID
     * @param updateDto 수정 정보
     * @return 수정된 댓글 정보
     */
    @PutMapping("/{id}")
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable Long id,
            @RequestBody CommentUpdateDto updateDto) {
        
        Optional<Comment> comment = commentRepository.findById(id);
        if (comment.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        // 댓글 내용 유효성 검사
        if (updateDto.getContent() == null || updateDto.getContent().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        Comment existingComment = comment.get();
        existingComment.setContent(updateDto.getContent());
        
        Comment updatedComment = commentRepository.save(existingComment);
        CommentDto dto = dtoConversionService.convertToCommentDto(updatedComment, null);
        
        return ResponseEntity.ok(dto);
    }
    
    /**
     * 댓글 삭제
     * @param id 댓글 ID
     * @return 삭제 결과
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        if (commentRepository.existsById(id)) {
            commentRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 특정 게임의 댓글 수 조회
     * @param balanceGameId 밸런스 게임 ID
     * @return 댓글 수
     */
    @GetMapping("/count/game/{balanceGameId}")
    public ResponseEntity<Long> getCommentCount(@PathVariable Long balanceGameId) {
        Long count = commentRepository.countByBalanceGameId(balanceGameId);
        return ResponseEntity.ok(count);
    }
    
    /**
     * 댓글 추천/추천취소
     * @param id 댓글 ID
     * @param userId 사용자 ID
     * @return 추천 결과
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<String> toggleLike(
            @PathVariable Long id,
            @RequestParam Long userId) {
        
        Optional<User> user = userRepository.findById(userId);
        Optional<Comment> comment = commentRepository.findById(id);
        
        if (user.isEmpty() || comment.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        Optional<Like> existingLike = likeRepository.findByUserIdAndCommentId(userId, id);
        
        if (existingLike.isPresent()) {
            // 추천 취소
            likeRepository.delete(existingLike.get());
            return ResponseEntity.ok("unliked");
        } else {
            // 추천 추가
            Like like = Like.builder()
                    .user(user.get())
                    .comment(comment.get())
                    .build();
            likeRepository.save(like);
            return ResponseEntity.ok("liked");
        }
    }
    
}

