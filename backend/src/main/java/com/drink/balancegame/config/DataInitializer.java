package com.drink.balancegame.config;

import com.drink.balancegame.entity.BalanceGame;
import com.drink.balancegame.entity.User;
import com.drink.balancegame.entity.UserRole;
import com.drink.balancegame.entity.Vote;
import com.drink.balancegame.repository.BalanceGameRepository;
import com.drink.balancegame.repository.UserRepository;
import com.drink.balancegame.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 애플리케이션 시작 시 테스트 데이터 초기화
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final BalanceGameRepository balanceGameRepository;
    private final VoteRepository voteRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        // 테스트 사용자가 이미 존재하는지 확인
        if (userRepository.findByUsername("testuser").isEmpty()) {
            // 테스트 사용자 생성
            User testUser = User.builder()
                    .username("testuser")
                    .email("test@example.com")
                    .password(passwordEncoder.encode("password"))
                    .role(UserRole.USER)
                    .provider(User.Provider.GOOGLE)
                    .build();
            
            userRepository.save(testUser);
            log.info("테스트 사용자 생성 완료: username=testuser, password=password");
        }
        
        // 관리자 사용자가 이미 존재하는지 확인
        if (userRepository.findByUsername("admin").isEmpty()) {
            // 관리자 사용자 생성
            User adminUser = User.builder()
                    .username("admin")
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("admin"))
                    .role(UserRole.ADMIN)
                    .provider(User.Provider.GOOGLE)
                    .build();
            
            userRepository.save(adminUser);
            log.info("관리자 사용자 생성 완료: username=admin, password=admin");
        }
        
        // 웹 테스트 사용자
        if (userRepository.findByUsername("webuser").isEmpty()) {
            User webUser = User.builder()
                    .username("webuser")
                    .email("web@example.com")
                    .password(passwordEncoder.encode("webpass"))
                    .role(UserRole.USER)
                    .provider(User.Provider.GOOGLE)
                    .build();
            
            userRepository.save(webUser);
            log.info("웹 테스트 사용자 생성 완료: username=webuser, password=webpass");
        }
        
        // 모바일 테스트 사용자
        if (userRepository.findByUsername("mobileuser").isEmpty()) {
            User mobileUser = User.builder()
                    .username("mobileuser")
                    .email("mobile@example.com")
                    .password(passwordEncoder.encode("mobilepass"))
                    .role(UserRole.USER)
                    .provider(User.Provider.GOOGLE)
                    .build();
            
            userRepository.save(mobileUser);
            log.info("모바일 테스트 사용자 생성 완료: username=mobileuser, password=mobilepass");
        }
        
        // 프론트엔드 테스트 로그인용 사용자들
        if (userRepository.findByUsername("google_user1").isEmpty()) {
            User googleUser1 = User.builder()
                    .username("google_user1")
                    .email("google_user1@example.com")
                    .password(passwordEncoder.encode("test"))
                    .role(UserRole.USER)
                    .provider(User.Provider.GOOGLE)
                    .build();
            
            userRepository.save(googleUser1);
            log.info("구글 테스트 사용자1 생성 완료: username=google_user1, password=test");
        }
        
        if (userRepository.findByUsername("kakao_user2").isEmpty()) {
            User kakaoUser2 = User.builder()
                    .username("kakao_user2")
                    .email("kakao_user2@example.com")
                    .password(passwordEncoder.encode("test"))
                    .role(UserRole.USER)
                    .provider(User.Provider.KAKAO)
                    .build();
            
            userRepository.save(kakaoUser2);
            log.info("카카오 테스트 사용자2 생성 완료: username=kakao_user2, password=test");
        }
        
        if (userRepository.findByUsername("naver_user3").isEmpty()) {
            User naverUser3 = User.builder()
                    .username("naver_user3")
                    .email("naver_user3@example.com")
                    .password(passwordEncoder.encode("test"))
                    .role(UserRole.USER)
                    .provider(User.Provider.NAVER)
                    .build();
            
            userRepository.save(naverUser3);
            log.info("네이버 테스트 사용자3 생성 완료: username=naver_user3, password=test");
        }
        
        // 초기 밸런스 게임 데이터 생성
        createInitialBalanceGames();
        
        log.info("데이터 초기화 완료. 총 사용자 수: {}, 총 게임 수: {}", 
                userRepository.count(), balanceGameRepository.count());
    }
    
    /**
     * 초기 밸런스 게임 데이터 생성
     */
    private void createInitialBalanceGames() {
        if (balanceGameRepository.count() > 0) {
            log.info("이미 밸런스 게임 데이터가 존재합니다. 스킵합니다.");
            return;
        }
        
        User admin = userRepository.findByUsername("admin").orElse(null);
        User testUser = userRepository.findByUsername("testuser").orElse(null);
        
        if (admin == null || testUser == null) {
            log.warn("관리자 또는 테스트 사용자를 찾을 수 없습니다. 게임 데이터 생성을 스킵합니다.");
            return;
        }
        
        // 게임 1: 고전적인 딜레마
        BalanceGame game1 = BalanceGame.builder()
                .title("치킨 vs 피자")
                .description("영원한 고민! 둘 중 하나만 평생 먹을 수 있다면?")
                .optionA("치킨")
                .optionB("피자")
                .optionADescription("바삭한 껍질과 촉촉한 속살의 치킨!")
                .optionBDescription("다양한 토핑과 쫄깃한 도우의 피자!")
                .viewCount(150)
                .author(admin)
                .build();
        balanceGameRepository.save(game1);
        
        // 게임 2: 여행 관련
        BalanceGame game2 = BalanceGame.builder()
                .title("바다 vs 산")
                .description("휴가를 간다면 어디로 갈래요?")
                .optionA("바다")
                .optionB("산")
                .optionADescription("시원한 바다와 해변에서의 여유로운 시간")
                .optionBDescription("맑은 공기와 아름다운 자연 속에서의 힐링")
                .viewCount(89)
                .author(testUser)
                .build();
        balanceGameRepository.save(game2);
        
        // 게임 3: 계절 선택
        BalanceGame game3 = BalanceGame.builder()
                .title("여름 vs 겨울")
                .description("일년 내내 한 계절만 있다면?")
                .optionA("영원한 여름")
                .optionB("영원한 겨울")
                .optionADescription("따뜻한 햇살과 시원한 바다, 그리고 아이스크림!")
                .optionBDescription("눈 내리는 풍경과 따뜻한 코코아, 그리고 포근한 담요")
                .viewCount(203)
                .author(admin)
                .build();
        balanceGameRepository.save(game3);
        
        // 게임 4: 능력 선택
        BalanceGame game4 = BalanceGame.builder()
                .title("투명인간 vs 하늘을 나는 능력")
                .description("초능력을 하나 가질 수 있다면?")
                .optionA("투명인간이 되는 능력")
                .optionB("하늘을 나는 능력")
                .optionADescription("누구에게도 들키지 않고 어디든 갈 수 있어요")
                .optionBDescription("교통체증도 없고 새처럼 자유롭게 날 수 있어요")
                .viewCount(317)
                .author(testUser)
                .build();
        balanceGameRepository.save(game4);
        
        // 게임 5: 시간 선택
        BalanceGame game5 = BalanceGame.builder()
                .title("과거로 가기 vs 미래로 가기")
                .description("시간여행이 가능하다면?")
                .optionA("과거로 시간여행")
                .optionB("미래로 시간여행")
                .optionADescription("후회했던 일들을 바로잡을 수 있어요")
                .optionBDescription("미래의 기술과 변화된 세상을 볼 수 있어요")
                .viewCount(178)
                .author(admin)
                .build();
        balanceGameRepository.save(game5);
        
        // 게임 6: 돈 vs 시간
        BalanceGame game6 = BalanceGame.builder()
                .title("무한한 돈 vs 무한한 시간")
                .description("둘 중 하나를 선택할 수 있다면?")
                .optionA("무한한 돈")
                .optionB("무한한 시간")
                .optionADescription("돈 걱정 없이 원하는 모든 것을 살 수 있어요")
                .optionBDescription("시간에 쫓기지 않고 여유롭게 살 수 있어요")
                .viewCount(256)
                .author(testUser)
                .build();
        balanceGameRepository.save(game6);
        
        log.info("초기 밸런스 게임 6개 생성 완료");
        
        // 투표 데이터 생성
        createInitialVotes(game1, game2, game3, game4, game5, game6, admin, testUser);
    }
    
    /**
     * 초기 투표 데이터 생성
     */
    private void createInitialVotes(BalanceGame game1, BalanceGame game2, BalanceGame game3,
                                   BalanceGame game4, BalanceGame game5, BalanceGame game6,
                                   User admin, User testUser) {
        
        // 게임 1 투표 (치킨 vs 피자) - 치킨 승리
        voteRepository.save(Vote.builder().balanceGame(game1).user(admin).selectedOption(Vote.VoteOption.A).build());
        voteRepository.save(Vote.builder().balanceGame(game1).user(testUser).selectedOption(Vote.VoteOption.A).build());
        
        // 게임 2 투표 (바다 vs 산) - 바다 승리  
        voteRepository.save(Vote.builder().balanceGame(game2).user(admin).selectedOption(Vote.VoteOption.A).build());
        voteRepository.save(Vote.builder().balanceGame(game2).user(testUser).selectedOption(Vote.VoteOption.B).build());
        
        // 게임 3 투표 (여름 vs 겨울) - 겨울 승리
        voteRepository.save(Vote.builder().balanceGame(game3).user(admin).selectedOption(Vote.VoteOption.B).build());
        voteRepository.save(Vote.builder().balanceGame(game3).user(testUser).selectedOption(Vote.VoteOption.B).build());
        
        // 게임 4 투표 (투명인간 vs 날기) - 날기 승리
        voteRepository.save(Vote.builder().balanceGame(game4).user(admin).selectedOption(Vote.VoteOption.B).build());
        voteRepository.save(Vote.builder().balanceGame(game4).user(testUser).selectedOption(Vote.VoteOption.A).build());
        
        // 게임 5 투표 (과거 vs 미래) - 과거 승리
        voteRepository.save(Vote.builder().balanceGame(game5).user(admin).selectedOption(Vote.VoteOption.A).build());
        voteRepository.save(Vote.builder().balanceGame(game5).user(testUser).selectedOption(Vote.VoteOption.A).build());
        
        // 게임 6 투표 (돈 vs 시간) - 시간 승리
        voteRepository.save(Vote.builder().balanceGame(game6).user(admin).selectedOption(Vote.VoteOption.B).build());
        voteRepository.save(Vote.builder().balanceGame(game6).user(testUser).selectedOption(Vote.VoteOption.B).build());
        
        log.info("초기 투표 데이터 12개 생성 완료");
    }
}