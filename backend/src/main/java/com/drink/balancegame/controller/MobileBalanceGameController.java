package com.drink.balancegame.controller;

import com.drink.balancegame.dto.BalanceGameDto;
import com.drink.balancegame.dto.BalanceGameCreateDto;
import com.drink.balancegame.dto.BalanceGameUpdateDto;
import com.drink.balancegame.entity.BalanceGame;
import com.drink.balancegame.entity.Like;
import com.drink.balancegame.entity.User;
import com.drink.balancegame.repository.BalanceGameRepository;
import com.drink.balancegame.repository.LikeRepository;
import com.drink.balancegame.repository.UserRepository;
import com.drink.balancegame.repository.VoteRepository;
import com.drink.balancegame.repository.CommentRepository;
import com.drink.balancegame.service.DtoConversionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * 모바일 전용 밸런스 게임 컨트롤러
 * 모바일 플랫폼용 밸런스 게임 관련 API 엔드포인트를 제공
 */
@RestController
@RequestMapping("/api/mobile/balance-games")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MobileBalanceGameController {
    
    private final BalanceGameRepository balanceGameRepository;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final DtoConversionService dtoConversionService;
    
    /**
     * 모든 밸런스 게임 조회 (페이징) - N+1 쿼리 최적화 버전
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @param sort 정렬 기준 (latest, popular, votes, best)
     * @param period 기간 (daily, weekly, monthly, all) - sort가 best일 때만 사용
     * @return 페이징된 밸런스 게임 목록
     */
    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<Page<BalanceGameDto>> getAllBalanceGames(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(defaultValue = "all") String period) {
        
        try {
            Pageable pageable = createPageable(page, size, sort);
            
            // N+1 쿼리 문제 해결: 모든 정렬 타입에 대해 통계와 함께 한 번에 조회
            Page<Object[]> results;
            switch (sort) {
                case "popular":
                    results = balanceGameRepository.findAllWithStatsOrderByViewCount(pageable);
                    break;
                case "votes":
                    results = balanceGameRepository.findAllWithStatsOrderByVoteCount(pageable);
                    break;
                case "best":
                    if ("all".equals(period)) {
                        results = balanceGameRepository.findAllWithStatsOrderByLikes(pageable);
                        break; // 일반 컨버터 사용
                    } else {
                        results = balanceGameRepository.findAllWithStatsOrderByLikesByPeriod(pageable, period);
                        Page<BalanceGameDto> periodDtos = results.map(this::convertToBalanceGameDtoFromStatsWithPeriod);
                        return ResponseEntity.ok(periodDtos);
                    }
                default: // "latest"
                    results = balanceGameRepository.findAllWithStats(pageable);
                    break;
            }
            
            Page<BalanceGameDto> balanceGameDtos = results.map(this::convertToBalanceGameDtoFromStats);
            return ResponseEntity.ok(balanceGameDtos);
        } catch (Exception e) {
            System.err.println("Mobile Balance game list error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 특정 밸런스 게임 조회 (조회수 증가)
     * @param id 밸런스 게임 ID
     * @return 밸런스 게임 정보
     */
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<BalanceGameDto> getBalanceGame(@PathVariable Long id) {
        Optional<BalanceGame> balanceGame = balanceGameRepository.findById(id);
        
        if (balanceGame.isPresent()) {
            BalanceGame game = balanceGame.get();
            // 조회수 증가
            game.setViewCount(game.getViewCount() + 1);
            balanceGameRepository.save(game);
            
            BalanceGameDto dto = dtoConversionService.convertToBalanceGameDto(game, null);
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 특정 밸런스 게임 정보 조회 (조회수 증가 없이)
     * @param id 밸런스 게임 ID
     * @return 밸런스 게임 정보
     */
    @GetMapping("/{id}/info")
    @Transactional(readOnly = true)
    public ResponseEntity<BalanceGameDto> getBalanceGameInfo(@PathVariable Long id) {
        Optional<BalanceGame> balanceGame = balanceGameRepository.findById(id);
        
        if (balanceGame.isPresent()) {
            BalanceGameDto dto = dtoConversionService.convertToBalanceGameDto(balanceGame.get(), null);
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 밸런스 게임 생성
     * @param createDto 게임 생성 정보
     * @param authorId 작성자 ID (실제로는 인증된 사용자에서 가져와야 함)
     * @return 생성된 밸런스 게임 정보
     */
    @PostMapping
    public ResponseEntity<BalanceGameDto> createBalanceGame(
            @RequestBody BalanceGameCreateDto createDto,
            @RequestParam Long authorId) {
        
        Optional<User> author = userRepository.findById(authorId);
        if (author.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        BalanceGame balanceGame = BalanceGame.builder()
                .title(createDto.getTitle())
                .description(createDto.getDescription())
                .optionA(createDto.getOptionA())
                .optionB(createDto.getOptionB())
                .optionADescription(createDto.getOptionADescription())
                .optionBDescription(createDto.getOptionBDescription())
                .author(author.get())
                .viewCount(0)
                .build();
        
        BalanceGame savedGame = balanceGameRepository.save(balanceGame);
        BalanceGameDto dto = dtoConversionService.convertToBalanceGameDto(savedGame, authorId);
        
        return ResponseEntity.ok(dto);
    }
    
    /**
     * 밸런스 게임 수정
     * @param id 게임 ID
     * @param updateDto 수정 정보
     * @return 수정된 밸런스 게임 정보
     */
    @PutMapping("/{id}")
    public ResponseEntity<BalanceGameDto> updateBalanceGame(
            @PathVariable Long id,
            @RequestBody BalanceGameUpdateDto updateDto) {
        
        Optional<BalanceGame> balanceGame = balanceGameRepository.findById(id);
        if (balanceGame.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        BalanceGame game = balanceGame.get();
        if (updateDto.getTitle() != null) {
            game.setTitle(updateDto.getTitle());
        }
        if (updateDto.getDescription() != null) {
            game.setDescription(updateDto.getDescription());
        }
        
        BalanceGame updatedGame = balanceGameRepository.save(game);
        BalanceGameDto dto = dtoConversionService.convertToBalanceGameDto(updatedGame, null);
        
        return ResponseEntity.ok(dto);
    }
    
    /**
     * 밸런스 게임 삭제
     * @param id 게임 ID
     * @return 삭제 결과
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBalanceGame(@PathVariable Long id) {
        if (balanceGameRepository.existsById(id)) {
            balanceGameRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 제목으로 밸런스 게임 검색
     * @param title 검색할 제목
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 검색 결과
     */
    @GetMapping("/search")
    @Transactional(readOnly = true)
    public ResponseEntity<Page<BalanceGameDto>> searchBalanceGames(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<BalanceGame> balanceGames = balanceGameRepository.findByTitleContainingIgnoreCase(title, pageable);
        
        Page<BalanceGameDto> balanceGameDtos = balanceGames.map(game -> dtoConversionService.convertToBalanceGameDto(game, null));
        return ResponseEntity.ok(balanceGameDtos);
    }
    
    /**
     * 정렬 타입에 따른 Pageable 생성
     */
    private Pageable createPageable(int page, int size, String sort) {
        Sort sortBy;
        switch (sort) {
            case "popular":
                sortBy = Sort.by("viewCount").descending();
                break;
            case "votes":
                // 투표수 기준 정렬은 별도 쿼리 필요
                sortBy = Sort.by("createdAt").descending();
                break;
            default:
                sortBy = Sort.by("createdAt").descending();
        }
        return PageRequest.of(page, size, sortBy);
    }
    
    
    /**
     * 통계 정보가 포함된 Object[] 배열을 BalanceGameDto로 변환
     * N+1 쿼리 문제 해결용 메서드
     */
    private BalanceGameDto convertToBalanceGameDtoFromStats(Object[] row) {
        BalanceGame game = (BalanceGame) row[0];
        Long likeCount = ((Number) row[1]).longValue();
        Long optionACount = ((Number) row[2]).longValue(); 
        Long optionBCount = ((Number) row[3]).longValue();
        Long commentCount = ((Number) row[4]).longValue();
        Long totalVotes = optionACount + optionBCount;
        
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
                .isLiked(false) // 로그인하지 않은 상태로 처리
                .optionAVotes(optionACount)
                .optionBVotes(optionBCount)
                .totalVotes(totalVotes)
                .userVote(null) // 로그인하지 않은 상태로 처리
                .commentCount(commentCount)
                .build();
    }
    
    /**
     * 기간별 통계 정보가 포함된 Object[] 배열을 BalanceGameDto로 변환
     */
    private BalanceGameDto convertToBalanceGameDtoFromStatsWithPeriod(Object[] row) {
        BalanceGame game = (BalanceGame) row[0];
        Long totalLikeCount = ((Number) row[1]).longValue();
        Long optionACount = ((Number) row[2]).longValue(); 
        Long optionBCount = ((Number) row[3]).longValue();
        Long commentCount = ((Number) row[4]).longValue();
        Long periodLikeCount = ((Number) row[5]).longValue(); // 기간별 좋아요
        Long totalVotes = optionACount + optionBCount;
        
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
                .likeCount(totalLikeCount) // 전체 좋아요 수 표시
                .isLiked(false)
                .optionAVotes(optionACount)
                .optionBVotes(optionBCount)
                .totalVotes(totalVotes)
                .userVote(null)
                .commentCount(commentCount)
                .build();
    }
    
    /**
     * 밸런스 게임 추천/추천취소
     * @param id 게임 ID
     * @param userId 사용자 ID
     * @return 추천 결과
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<String> toggleLike(
            @PathVariable Long id,
            @RequestParam Long userId) {
        
        Optional<User> user = userRepository.findById(userId);
        Optional<BalanceGame> balanceGame = balanceGameRepository.findById(id);
        
        if (user.isEmpty() || balanceGame.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        Optional<Like> existingLike = likeRepository.findByUserIdAndBalanceGameId(userId, id);
        
        if (existingLike.isPresent()) {
            // 추천 취소
            likeRepository.delete(existingLike.get());
            return ResponseEntity.ok("unliked");
        } else {
            // 추천 추가
            Like like = Like.builder()
                    .user(user.get())
                    .balanceGame(balanceGame.get())
                    .build();
            likeRepository.save(like);
            return ResponseEntity.ok("liked");
        }
    }
    
}