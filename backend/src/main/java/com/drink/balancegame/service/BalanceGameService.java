package com.drink.balancegame.service;

import com.drink.balancegame.dto.BalanceGameCreateDto;
import com.drink.balancegame.dto.BalanceGameDto;
import com.drink.balancegame.dto.BalanceGameUpdateDto;
import com.drink.balancegame.entity.BalanceGame;
import com.drink.balancegame.entity.User;
import com.drink.balancegame.repository.BalanceGameRepository;
import com.drink.balancegame.repository.UserRepository;
import com.drink.balancegame.service.ValidationService;
import com.drink.balancegame.service.DtoConversionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 밸런스 게임 비즈니스 로직 서비스
 * 밸런스 게임 생성, 조회, 수정, 삭제 및 관련 비즈니스 로직을 처리
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class BalanceGameService {
    
    private final BalanceGameRepository balanceGameRepository;
    private final UserRepository userRepository;
    private final DtoConversionService dtoConversionService;
    private final ValidationService validationService;
    
    /**
     * 모든 밸런스 게임 조회 (페이징)
     * @param pageable 페이징 정보
     * @param userId 현재 사용자 ID (선택사항)
     * @return 페이징된 밸런스 게임 목록
     */
    public Page<BalanceGameDto> getAllGames(Pageable pageable, Long userId) {
        log.debug("모든 밸런스 게임 조회 - 페이지: {}, 사용자 ID: {}", pageable, userId);
        
        Page<BalanceGame> games = balanceGameRepository.findAll(pageable);
        return games.map(game -> dtoConversionService.convertToBalanceGameDto(game, userId));
    }
    
    /**
     * 인기 게임 조회 (조회수 기준)
     * @param pageable 페이징 정보
     * @param userId 현재 사용자 ID (선택사항)
     * @return 인기 게임 목록
     */
    public Page<BalanceGameDto> getPopularGames(Pageable pageable, Long userId) {
        log.debug("인기 게임 조회 - 페이지: {}, 사용자 ID: {}", pageable, userId);
        
        Page<BalanceGame> games = balanceGameRepository.findByOrderByViewCountDesc(pageable);
        return games.map(game -> dtoConversionService.convertToBalanceGameDto(game, userId));
    }
    
    /**
     * 최신 게임 조회
     * @param pageable 페이징 정보
     * @param userId 현재 사용자 ID (선택사항)
     * @return 최신 게임 목록
     */
    public Page<BalanceGameDto> getLatestGames(Pageable pageable, Long userId) {
        log.debug("최신 게임 조회 - 페이지: {}, 사용자 ID: {}", pageable, userId);
        
        Page<BalanceGame> games = balanceGameRepository.findByOrderByCreatedAtDesc(pageable);
        return games.map(game -> dtoConversionService.convertToBalanceGameDto(game, userId));
    }
    
    /**
     * 투표수 기준 인기 게임 조회
     * @param pageable 페이징 정보
     * @param userId 현재 사용자 ID (선택사항)
     * @return 투표수 기준 인기 게임 목록
     */
    public Page<BalanceGameDto> getGamesByVoteCount(Pageable pageable, Long userId) {
        log.debug("투표수 기준 게임 조회 - 페이지: {}, 사용자 ID: {}", pageable, userId);
        
        Page<BalanceGame> games = balanceGameRepository.findByOrderByVoteCountDesc(pageable);
        return games.map(game -> dtoConversionService.convertToBalanceGameDto(game, userId));
    }
    
    /**
     * 기간별 베스트 게임 조회 (좋아요 기준)
     * @param period 기간 (daily, weekly, monthly, all)
     * @param pageable 페이징 정보
     * @param userId 현재 사용자 ID (선택사항)
     * @return 기간별 베스트 게임 목록
     */
    public Page<BalanceGameDto> getBestGamesByPeriod(String period, Pageable pageable, Long userId) {
        log.debug("기간별 베스트 게임 조회 - 기간: {}, 페이지: {}, 사용자 ID: {}", period, pageable, userId);
        
        Page<Object[]> results = balanceGameRepository.findAllWithStatsOrderByLikesByPeriod(pageable, period);
        return results.map(this::convertToBalanceGameDtoFromStats);
    }
    
    /**
     * 통계 정보가 포함된 Object[] 배열을 BalanceGameDto로 변환
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
     * 제목으로 게임 검색
     * @param title 검색할 제목
     * @param pageable 페이징 정보
     * @param userId 현재 사용자 ID (선택사항)
     * @return 검색된 게임 목록
     */
    public Page<BalanceGameDto> searchGamesByTitle(String title, Pageable pageable, Long userId) {
        log.debug("게임 제목 검색 - 키워드: {}, 페이지: {}, 사용자 ID: {}", title, pageable, userId);
        
        Page<BalanceGame> games = balanceGameRepository.findByTitleContainingIgnoreCase(title, pageable);
        return games.map(game -> dtoConversionService.convertToBalanceGameDto(game, userId));
    }
    
    /**
     * 특정 작성자의 게임 조회
     * @param authorId 작성자 ID
     * @param pageable 페이징 정보
     * @param userId 현재 사용자 ID (선택사항)
     * @return 작성자의 게임 목록
     */
    public Page<BalanceGameDto> getGamesByAuthor(Long authorId, Pageable pageable, Long userId) {
        log.debug("작성자별 게임 조회 - 작성자 ID: {}, 페이지: {}, 사용자 ID: {}", authorId, pageable, userId);
        
        // 작성자 존재 확인
        validationService.validateUserExists(authorId);
        
        Page<BalanceGame> games = balanceGameRepository.findByAuthorId(authorId, pageable);
        return games.map(game -> dtoConversionService.convertToBalanceGameDto(game, userId));
    }
    
    /**
     * 특정 게임 조회 (조회수 증가)
     * @param gameId 게임 ID
     * @param userId 현재 사용자 ID (선택사항)
     * @return 게임 정보
     */
    @Transactional
    public Optional<BalanceGameDto> getGameById(Long gameId, Long userId) {
        log.debug("게임 조회 (조회수 증가) - 게임 ID: {}, 사용자 ID: {}", gameId, userId);
        
        Optional<BalanceGame> gameOpt = balanceGameRepository.findById(gameId);
        if (gameOpt.isPresent()) {
            BalanceGame game = gameOpt.get();
            // 조회수 증가
            game.setViewCount(game.getViewCount() + 1);
            balanceGameRepository.save(game);
            
            return Optional.of(dtoConversionService.convertToBalanceGameDto(game, userId));
        }
        return Optional.empty();
    }
    
    /**
     * 게임 정보만 조회 (조회수 증가 없음)
     * @param gameId 게임 ID
     * @param userId 현재 사용자 ID (선택사항)
     * @return 게임 정보
     */
    public Optional<BalanceGameDto> getGameInfo(Long gameId, Long userId) {
        log.debug("게임 정보 조회 - 게임 ID: {}, 사용자 ID: {}", gameId, userId);
        
        Optional<BalanceGame> gameOpt = balanceGameRepository.findById(gameId);
        return gameOpt.map(game -> dtoConversionService.convertToBalanceGameDto(game, userId));
    }
    
    /**
     * 새 밸런스 게임 생성
     * @param createDto 게임 생성 정보
     * @param authorId 작성자 ID
     * @return 생성된 게임 정보
     */
    @Transactional
    public BalanceGameDto createGame(BalanceGameCreateDto createDto, Long authorId) {
        log.debug("새 게임 생성 - 제목: {}, 작성자 ID: {}", createDto.getTitle(), authorId);
        
        // 작성자 존재 확인
        User author = validationService.validateUserExists(authorId);
        
        // 게임 정보 유효성 검사
        validateGameContent(createDto.getTitle(), createDto.getOptionA(), createDto.getOptionB());
        
        BalanceGame game = BalanceGame.builder()
                .title(createDto.getTitle())
                .description(createDto.getDescription())
                .optionA(createDto.getOptionA())
                .optionB(createDto.getOptionB())
                .optionADescription(createDto.getOptionADescription())
                .optionBDescription(createDto.getOptionBDescription())
                .author(author)
                .viewCount(0)
                .build();
        
        BalanceGame savedGame = balanceGameRepository.save(game);
        log.info("새 게임 생성 완료 - 게임 ID: {}, 제목: {}", savedGame.getId(), savedGame.getTitle());
        
        return dtoConversionService.convertToBalanceGameDto(savedGame, authorId);
    }
    
    /**
     * 게임 정보 수정
     * @param gameId 게임 ID
     * @param updateDto 수정 정보
     * @param userId 요청 사용자 ID
     * @return 수정된 게임 정보
     */
    @Transactional
    public Optional<BalanceGameDto> updateGame(Long gameId, BalanceGameUpdateDto updateDto, Long userId) {
        log.debug("게임 수정 - 게임 ID: {}, 사용자 ID: {}", gameId, userId);
        
        Optional<BalanceGame> gameOpt = balanceGameRepository.findById(gameId);
        if (gameOpt.isEmpty()) {
            log.warn("존재하지 않는 게임 수정 시도 - 게임 ID: {}", gameId);
            return Optional.empty();
        }
        
        BalanceGame game = gameOpt.get();
        
        // 작성자 권한 확인
        if (!game.getAuthor().getId().equals(userId)) {
            log.warn("게임 수정 권한 없음 - 게임 ID: {}, 요청 사용자 ID: {}, 작성자 ID: {}", 
                    gameId, userId, game.getAuthor().getId());
            throw new IllegalArgumentException("게임을 수정할 권한이 없습니다.");
        }
        
        // 수정 내용 적용
        if (updateDto.getTitle() != null && !updateDto.getTitle().trim().isEmpty()) {
            game.setTitle(updateDto.getTitle());
        }
        if (updateDto.getDescription() != null) {
            game.setDescription(updateDto.getDescription());
        }
        
        BalanceGame updatedGame = balanceGameRepository.save(game);
        log.info("게임 수정 완료 - 게임 ID: {}", gameId);
        
        return Optional.of(dtoConversionService.convertToBalanceGameDto(updatedGame, userId));
    }
    
    /**
     * 게임 삭제
     * @param gameId 게임 ID
     * @param userId 요청 사용자 ID
     * @return 삭제 성공 여부
     */
    @Transactional
    public boolean deleteGame(Long gameId, Long userId) {
        log.debug("게임 삭제 - 게임 ID: {}, 사용자 ID: {}", gameId, userId);
        
        Optional<BalanceGame> gameOpt = balanceGameRepository.findById(gameId);
        if (gameOpt.isEmpty()) {
            log.warn("존재하지 않는 게임 삭제 시도 - 게임 ID: {}", gameId);
            return false;
        }
        
        BalanceGame game = gameOpt.get();
        
        // 작성자 권한 확인
        if (!game.getAuthor().getId().equals(userId)) {
            log.warn("게임 삭제 권한 없음 - 게임 ID: {}, 요청 사용자 ID: {}, 작성자 ID: {}", 
                    gameId, userId, game.getAuthor().getId());
            throw new IllegalArgumentException("게임을 삭제할 권한이 없습니다.");
        }
        
        balanceGameRepository.delete(game);
        log.info("게임 삭제 완료 - 게임 ID: {}", gameId);
        
        return true;
    }
    
    /**
     * 게임 내용 유효성 검사
     * @param title 제목
     * @param optionA 옵션 A
     * @param optionB 옵션 B
     */
    private void validateGameContent(String title, String optionA, String optionB) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("게임 제목은 필수입니다.");
        }
        if (optionA == null || optionA.trim().isEmpty()) {
            throw new IllegalArgumentException("선택지 A는 필수입니다.");
        }
        if (optionB == null || optionB.trim().isEmpty()) {
            throw new IllegalArgumentException("선택지 B는 필수입니다.");
        }
        if (title.length() > 200) {
            throw new IllegalArgumentException("제목은 200자를 초과할 수 없습니다.");
        }
        if (optionA.length() > 100 || optionB.length() > 100) {
            throw new IllegalArgumentException("선택지는 100자를 초과할 수 없습니다.");
        }
    }
}