package com.drink.balancegame.controller;

import com.drink.balancegame.dto.VoteDto;
import com.drink.balancegame.dto.VoteCreateDto;
import com.drink.balancegame.dto.VoteResponseDto;
import com.drink.balancegame.dto.VoteStatsDto;
import com.drink.balancegame.entity.BalanceGame;
import com.drink.balancegame.entity.User;
import com.drink.balancegame.entity.Vote;
import com.drink.balancegame.repository.BalanceGameRepository;
import com.drink.balancegame.repository.UserRepository;
import com.drink.balancegame.repository.VoteRepository;
import com.drink.balancegame.service.DtoConversionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 투표 컨트롤러
 * 투표 관련 API 엔드포인트를 제공
 */
@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
public class VoteController {
    
    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final BalanceGameRepository balanceGameRepository;
    private final DtoConversionService dtoConversionService;
    
    /**
     * 특정 게임의 모든 투표 조회
     * @param balanceGameId 밸런스 게임 ID
     * @return 투표 목록
     */
    @GetMapping("/game/{balanceGameId}")
    @Transactional(readOnly = true)
    public ResponseEntity<List<VoteDto>> getVotesByGame(@PathVariable Long balanceGameId) {
        List<Vote> votes = voteRepository.findByBalanceGameId(balanceGameId);
        List<VoteDto> voteDtos = votes.stream()
                .map(dtoConversionService::convertToVoteDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(voteDtos);
    }
    
    /**
     * 특정 사용자의 모든 투표 조회
     * @param userId 사용자 ID
     * @return 투표 목록
     */
    @GetMapping("/user/{userId}")
    @Transactional(readOnly = true)
    public ResponseEntity<List<VoteDto>> getVotesByUser(@PathVariable Long userId) {
        List<Vote> votes = voteRepository.findByUserId(userId);
        List<VoteDto> voteDtos = votes.stream()
                .map(dtoConversionService::convertToVoteDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(voteDtos);
    }
    
    /**
     * 특정 사용자의 특정 게임에 대한 투표 조회
     * @param userId 사용자 ID
     * @param balanceGameId 밸런스 게임 ID
     * @return 투표 정보
     */
    @GetMapping("/user/{userId}/game/{balanceGameId}")
    @Transactional(readOnly = true)
    public ResponseEntity<VoteDto> getUserVoteForGame(
            @PathVariable Long userId,
            @PathVariable Long balanceGameId) {
        
        Optional<Vote> vote = voteRepository.findByUserIdAndBalanceGameId(userId, balanceGameId);
        
        if (vote.isPresent()) {
            VoteDto dto = dtoConversionService.convertToVoteDto(vote.get());
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 투표 생성 또는 변경/취소
     * @param createDto 투표 생성 정보
     * @param userId 투표할 사용자 ID (실제로는 인증된 사용자에서 가져와야 함)
     * @return 생성/수정된 투표 정보 또는 취소 메시지
     */
    @PostMapping
    public ResponseEntity<VoteResponseDto> createOrUpdateVote(
            @RequestBody VoteCreateDto createDto,
            @RequestParam Long userId) {
        
        try {
            // 사용자 존재 확인
            Optional<User> user = userRepository.findById(userId);
            if (user.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            // 밸런스 게임 존재 확인
            Optional<BalanceGame> balanceGame = balanceGameRepository.findById(createDto.getBalanceGameId());
            if (balanceGame.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            // 투표 옵션 유효성 검사
            if (!createDto.getSelectedOption().equals("A") && !createDto.getSelectedOption().equals("B")) {
                return ResponseEntity.badRequest().build();
            }
            
            // 이미 투표했는지 확인
            Optional<Vote> existingVote = voteRepository.findByUserIdAndBalanceGameId(userId, createDto.getBalanceGameId());
            
            if (existingVote.isPresent()) {
                Vote vote = existingVote.get();
                Vote.VoteOption newOption = Vote.VoteOption.valueOf(createDto.getSelectedOption());
                
                // 같은 옵션을 다시 선택하면 투표 취소
                if (vote.getSelectedOption() == newOption) {
                    voteRepository.delete(vote);
                    return ResponseEntity.ok(VoteResponseDto.cancelled());
                } else {
                    // 다른 옵션을 선택하면 투표 변경
                    vote.setSelectedOption(newOption);
                    Vote updatedVote = voteRepository.save(vote);
                    VoteDto dto = dtoConversionService.convertToVoteDto(updatedVote);
                    return ResponseEntity.ok(VoteResponseDto.updated(dto));
                }
            } else {
                // 새로운 투표 생성
                Vote vote = Vote.builder()
                        .selectedOption(Vote.VoteOption.valueOf(createDto.getSelectedOption()))
                        .user(user.get())
                        .balanceGame(balanceGame.get())
                        .build();
                
                Vote savedVote = voteRepository.save(vote);
                VoteDto dto = dtoConversionService.convertToVoteDto(savedVote);
                
                return ResponseEntity.ok(VoteResponseDto.created(dto));
            }
        } catch (Exception e) {
            // 로그 출력
            System.err.println("Vote creation/update error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 투표 수정
     * @param id 투표 ID
     * @param updateDto 수정 정보
     * @return 수정된 투표 정보
     */
    @PutMapping("/{id}")
    public ResponseEntity<VoteDto> updateVote(
            @PathVariable Long id,
            @RequestBody VoteCreateDto updateDto) {
        
        Optional<Vote> vote = voteRepository.findById(id);
        if (vote.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        // 투표 옵션 유효성 검사
        if (!updateDto.getSelectedOption().equals("A") && !updateDto.getSelectedOption().equals("B")) {
            return ResponseEntity.badRequest().build();
        }
        
        Vote existingVote = vote.get();
        existingVote.setSelectedOption(Vote.VoteOption.valueOf(updateDto.getSelectedOption()));
        
        Vote updatedVote = voteRepository.save(existingVote);
        VoteDto dto = dtoConversionService.convertToVoteDto(updatedVote);
        
        return ResponseEntity.ok(dto);
    }
    
    /**
     * 투표 삭제
     * @param id 투표 ID
     * @return 삭제 결과
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVote(@PathVariable Long id) {
        if (voteRepository.existsById(id)) {
            voteRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 특정 게임의 투표 통계 조회
     * @param balanceGameId 밸런스 게임 ID
     * @return 투표 통계
     */
    @GetMapping("/stats/{balanceGameId}")
    @Transactional(readOnly = true)
    public ResponseEntity<VoteStatsDto> getVoteStats(@PathVariable Long balanceGameId) {
        Long optionAVotes = voteRepository.countOptionAVotesByBalanceGameId(balanceGameId);
        Long optionBVotes = voteRepository.countOptionBVotesByBalanceGameId(balanceGameId);
        Long totalVotes = optionAVotes + optionBVotes;
        
        double optionAPercentage = totalVotes > 0 ? (double) optionAVotes / totalVotes * 100 : 0;
        double optionBPercentage = totalVotes > 0 ? (double) optionBVotes / totalVotes * 100 : 0;
        
        VoteStatsDto statsDto = VoteStatsDto.builder()
                .balanceGameId(balanceGameId)
                .optionAVotes(optionAVotes)
                .optionBVotes(optionBVotes)
                .totalVotes(totalVotes)
                .optionAPercentage(optionAPercentage)
                .optionBPercentage(optionBPercentage)
                .build();
        
        return ResponseEntity.ok(statsDto);
    }
    
}

