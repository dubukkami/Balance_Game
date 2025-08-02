package com.drink.balancegame.config;

import com.drink.balancegame.entity.BalanceGame;
import com.drink.balancegame.entity.Comment;
import com.drink.balancegame.entity.Like;
import com.drink.balancegame.entity.User;
import com.drink.balancegame.entity.UserRole;
import com.drink.balancegame.entity.Vote;
import com.drink.balancegame.repository.BalanceGameRepository;
import com.drink.balancegame.repository.CommentRepository;
import com.drink.balancegame.repository.LikeRepository;
import com.drink.balancegame.repository.UserRepository;
import com.drink.balancegame.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 애플리케이션 시작 시 테스트 데이터 초기화
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {
    
    private final UserRepository userRepository;
    private final BalanceGameRepository balanceGameRepository;
    private final VoteRepository voteRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final PasswordEncoder passwordEncoder;
    
    private final Random random = new Random();
    
    @EventListener(ApplicationReadyEvent.class)
    public void initializeData() {
        try {
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
        
        // 추가 테스트 사용자들 생성 (기간별 베스트를 위해 8명으로 증가)
        List<User> additionalUsers = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            String username = "user" + i;
            if (userRepository.findByUsername(username).isEmpty()) {
                User user = User.builder()
                        .username(username)
                        .email(username + "@example.com")
                        .password(passwordEncoder.encode("password"))
                        .role(UserRole.USER)
                        .provider(User.Provider.values()[random.nextInt(User.Provider.values().length)])
                        .build();
                
                additionalUsers.add(userRepository.save(user));
                log.info("추가 테스트 사용자 생성 완료: username={}", username);
            }
        }
        
        // 초기 밸런스 게임 데이터 생성
        createInitialBalanceGames();
        
            log.info("데이터 초기화 완료. 총 사용자 수: {}, 총 게임 수: {}", 
                    userRepository.count(), balanceGameRepository.count());
        } catch (Exception e) {
            log.warn("데이터 초기화 중 오류 발생: {}", e.getMessage());
            log.info("테이블이 아직 생성되지 않았을 수 있습니다. 나중에 다시 시도하세요.");
        }
    }
    
    /**
     * 초기 밸런스 게임 데이터 생성
     */
    private void createInitialBalanceGames() {
        // 기존 데이터 삭제 (테스트를 위해)
        log.info("모든 데이터를 삭제하고 새로 생성합니다.");
        likeRepository.deleteAll();
        commentRepository.deleteAll();
        voteRepository.deleteAll();
        balanceGameRepository.deleteAll();
        
        User admin = userRepository.findByUsername("admin").orElse(null);
        User testUser = userRepository.findByUsername("testuser").orElse(null);
        
        if (admin == null || testUser == null) {
            log.warn("관리자 또는 테스트 사용자를 찾을 수 없습니다. 게임 데이터 생성을 스킵합니다.");
            return;
        }
        
        // 모든 사용자 목록 가져오기
        List<User> allUsers = userRepository.findAll();
        
        // 게임 1: 고전적인 딜레마 (1주일 전 생성 - 인기 많음)
        BalanceGame game1 = BalanceGame.builder()
                .title("치킨 vs 피자")
                .description("영원한 고민! 둘 중 하나만 평생 먹을 수 있다면?")
                .optionA("치킨")
                .optionB("피자")
                .optionADescription("바삭한 껍질과 촉촉한 속살의 치킨!")
                .optionBDescription("다양한 토핑과 쫄깃한 도우의 피자!")
                .viewCount(1523)
                .author(admin)
                .build();
        game1.setCreatedAt(LocalDateTime.now().minusDays(7));
        balanceGameRepository.save(game1);
        
        // 게임 2: 여행 관련 (3일 전 생성)
        BalanceGame game2 = BalanceGame.builder()
                .title("바다 vs 산")
                .description("휴가를 간다면 어디로 갈래요?")
                .optionA("바다")
                .optionB("산")
                .optionADescription("시원한 바다와 해변에서의 여유로운 시간")
                .optionBDescription("맑은 공기와 아름다운 자연 속에서의 힐링")
                .viewCount(892)
                .author(testUser)
                .build();
        game2.setCreatedAt(LocalDateTime.now().minusDays(3));
        balanceGameRepository.save(game2);
        
        // 게임 3: 계절 선택 (2주 전 생성 - 오래된 인기 게임)
        BalanceGame game3 = BalanceGame.builder()
                .title("여름 vs 겨울")
                .description("일년 내내 한 계절만 있다면?")
                .optionA("영원한 여름")
                .optionB("영원한 겨울")
                .optionADescription("따뜻한 햇살과 시원한 바다, 그리고 아이스크림!")
                .optionBDescription("눈 내리는 풍경과 따뜻한 코코아, 그리고 포근한 담요")
                .viewCount(2038)
                .author(admin)
                .build();
        game3.setCreatedAt(LocalDateTime.now().minusDays(14));
        balanceGameRepository.save(game3);
        
        // 게임 4: 능력 선택 (오늘 생성 - 최신 인기)
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
        game4.setCreatedAt(LocalDateTime.now().minusHours(3));
        balanceGameRepository.save(game4);
        
        // 게임 5: 시간 선택 (1일 전 생성)
        BalanceGame game5 = BalanceGame.builder()
                .title("과거로 가기 vs 미래로 가기")
                .description("시간여행이 가능하다면?")
                .optionA("과거로 시간여행")
                .optionB("미래로 시간여행")
                .optionADescription("후회했던 일들을 바로잡을 수 있어요")
                .optionBDescription("미래의 기술과 변화된 세상을 볼 수 있어요")
                .viewCount(678)
                .author(admin)
                .build();
        game5.setCreatedAt(LocalDateTime.now().minusDays(1));
        balanceGameRepository.save(game5);
        
        // 게임 6: 돈 vs 시간 (5일 전 생성)
        BalanceGame game6 = BalanceGame.builder()
                .title("무한한 돈 vs 무한한 시간")
                .description("둘 중 하나를 선택할 수 있다면?")
                .optionA("무한한 돈")
                .optionB("무한한 시간")
                .optionADescription("돈 걱정 없이 원하는 모든 것을 살 수 있어요")
                .optionBDescription("시간에 쫓기지 않고 여유롭게 살 수 있어요")
                .viewCount(1256)
                .author(testUser)
                .build();
        game6.setCreatedAt(LocalDateTime.now().minusDays(5));
        balanceGameRepository.save(game6);
        
        // 추가 게임들 생성
        List<BalanceGame> additionalGames = createAdditionalGames(allUsers);
        
        log.info("초기 밸런스 게임 {} 개 생성 완료", 6 + additionalGames.size());
        
        // 투표 및 댓글 데이터 생성
        List<BalanceGame> allGames = new ArrayList<>();
        allGames.add(game1);
        allGames.add(game2);
        allGames.add(game3);
        allGames.add(game4);
        allGames.add(game5);
        allGames.add(game6);
        allGames.addAll(additionalGames);
        
        createInitialVotesAndComments(allGames, allUsers);
    }
    
    /**
     * 추가 게임들 생성
     */
    private List<BalanceGame> createAdditionalGames(List<User> users) {
        List<BalanceGame> games = new ArrayList<>();
        
        String[][] gameData = {
            // title, description, optionA, optionB, optionADesc, optionBDesc, daysAgo, viewCount
            {"아침형 인간 vs 저녁형 인간", "당신의 생활 패턴은?", "아침형 인간", "저녁형 인간", 
             "새벽에 일찍 일어나서 활동하는 스타일", "밤늦게까지 활동하는 올빼미 스타일", "2", "456"},
            {"혼자 여행 vs 함께 여행", "여행 스타일은?", "혼자 여행", "친구/가족과 여행",
             "자유롭고 여유로운 나만의 여행", "함께 나누는 즐거움이 있는 여행", "4", "789"},
            {"커피 vs 차", "카페인 섭취 선호도", "커피", "차(녹차, 홍차 등)",
             "진한 커피의 향과 맛", "은은하고 건강한 차의 매력", "6", "234"},
            {"강아지 vs 고양이", "반려동물 선택", "강아지", "고양이",
             "충성스럽고 활발한 강아지", "도도하지만 매력적인 고양이", "1", "1890"},
            {"독서 vs 영화", "여가시간 활용", "책 읽기", "영화 보기",
             "상상력을 자극하는 독서의 매력", "시각적 즐거움과 편안함", "8", "567"},
            {"아파트 vs 단독주택", "살고 싶은 집", "편리한 아파트", "정원이 있는 단독주택",
             "편의시설과 보안이 좋은 아파트", "프라이버시와 정원이 있는 단독주택", "10", "1234"},
            {"버스 vs 지하철", "대중교통 선호도", "버스", "지하철",
             "바깥 풍경을 보며 이동하는 버스", "빠르고 정확한 지하철", "12", "345"},
            {"소고기 vs 돼지고기", "고기 선호도", "소고기", "돼지고기",
             "부드럽고 고소한 소고기", "다양한 부위와 요리가 가능한 돼지고기", "15", "987"}
        };
        
        for (String[] data : gameData) {
            User randomUser = users.get(random.nextInt(users.size()));
            BalanceGame game = BalanceGame.builder()
                    .title(data[0])
                    .description(data[1])
                    .optionA(data[2])
                    .optionB(data[3])
                    .optionADescription(data[4])
                    .optionBDescription(data[5])
                    .viewCount(Integer.parseInt(data[7]))
                    .author(randomUser)
                    .build();
            game.setCreatedAt(LocalDateTime.now().minusDays(Integer.parseInt(data[6])));
            games.add(balanceGameRepository.save(game));
        }
        
        return games;
    }
    
    /**
     * 투표 및 댓글 데이터 생성
     */
    private void createInitialVotesAndComments(List<BalanceGame> games, List<User> users) {
        String[] comments = {
            "저는 A가 더 좋은 것 같아요!",
            "B가 훨씬 나은 선택이죠 ㅋㅋ",
            "고민이 되네요... 둘 다 좋은데",
            "이건 당연히 A죠!!",
            "B 선택한 사람들 이해가 안가요 ㅎㅎ",
            "저는 항상 A파입니다",
            "B가 더 현실적인 것 같아요",
            "재밌는 주제네요!",
            "친구들이랑 이거 가지고 한참 얘기했어요",
            "저는 상황에 따라 다를 것 같아요",
            "A 선택하신 분들 손!",
            "B가 진리입니다",
            "이런 밸런스 게임 너무 좋아요",
            "어렵네요 진짜 ㅠㅠ",
            "저는 평생 A만 선택할래요",
            "B의 매력을 모르시는군요",
            "둘 다 못 버려요 ㅋㅋㅋ",
            "이건 취향차이인 것 같아요",
            "통계가 재밌네요!",
            "다수의 선택이 항상 옳은건 아니죠"
        };
        
        for (BalanceGame game : games) {
            // 각 게임마다 랜덤한 수의 투표 생성 (3-6개로 최소화)
            int voteCount = 3 + random.nextInt(4);
            for (int i = 0; i < voteCount && i < users.size(); i++) {
                Vote vote = Vote.builder()
                        .balanceGame(game)
                        .user(users.get(i))
                        .selectedOption(random.nextBoolean() ? Vote.VoteOption.A : Vote.VoteOption.B)
                        .build();
                voteRepository.save(vote);
            }
            
            // 각 게임마다 랜덤한 수의 댓글 생성 (0-2개로 최소화)
            int commentCount = random.nextInt(3);
            for (int i = 0; i < commentCount; i++) {
                User randomUser = users.get(random.nextInt(users.size()));
                Comment comment = Comment.builder()
                        .content(comments[random.nextInt(comments.length)])
                        .balanceGame(game)
                        .author(randomUser)
                        .build();
                
                // 댓글 작성 시간을 게임 생성 후 랜덤하게 설정
                long gameCreatedHours = game.getCreatedAt().until(LocalDateTime.now(), java.time.temporal.ChronoUnit.HOURS);
                if (gameCreatedHours > 0) {
                    comment.setCreatedAt(game.getCreatedAt().plusHours(random.nextInt((int) gameCreatedHours)));
                }
                
                commentRepository.save(comment);
            }
            
            // 게임별로 기간에 따른 좋아요 생성
            createLikesForGame(game, users);
        }
        
        log.info("투표, 댓글, 좋아요 데이터 생성 완료");
    }
    
    /**
     * 게임별로 기간에 따른 좋아요 생성
     */
    private void createLikesForGame(BalanceGame game, List<User> users) {
        long gameCreatedDays = game.getCreatedAt().until(LocalDateTime.now(), java.time.temporal.ChronoUnit.DAYS);
        
        // 게임 생성 시기에 따라 좋아요 패턴 다르게 설정
        int totalLikes = 0;
        int dailyLikes = 0;
        int weeklyLikes = 0;
        int monthlyLikes = 0;
        
        // 각 게임별로 명확하게 다른 패턴 설정
        String gameTitle = game.getTitle();
        
        if (gameTitle.contains("치킨 vs 피자")) {
            // 일간 베스트 1위 - 오늘 많은 좋아요
            totalLikes = 10;
            dailyLikes = 8;  // 오늘 8개
            weeklyLikes = 8; 
            monthlyLikes = 10;
        } else if (gameTitle.contains("투명인간")) {
            // 일간 베스트 2위 - 오늘 생성된 인기 게임
            totalLikes = 7;
            dailyLikes = 6;  // 오늘 6개
            weeklyLikes = 7;
            monthlyLikes = 7;
        } else if (gameTitle.contains("바다 vs 산")) {
            // 주간 베스트 1위 - 이번 주 인기
            totalLikes = 12;
            dailyLikes = 1;   // 오늘 1개
            weeklyLikes = 10; // 이번주 10개
            monthlyLikes = 12;
        } else if (gameTitle.contains("과거로 가기")) {
            // 주간 베스트 2위
            totalLikes = 9;
            dailyLikes = 0;   // 오늘 0개
            weeklyLikes = 8;  // 이번주 8개
            monthlyLikes = 9;
        } else if (gameTitle.contains("여름 vs 겨울")) {
            // 월간 베스트 1위 - 이번 달 꾸준한 인기
            totalLikes = 15;
            dailyLikes = 0;   // 오늘 0개
            weeklyLikes = 2;  // 이번주 2개
            monthlyLikes = 14; // 이번달 14개
        } else if (gameTitle.contains("무한한 돈")) {
            // 월간 베스트 2위
            totalLikes = 13;
            dailyLikes = 0;
            weeklyLikes = 1;
            monthlyLikes = 11;
        } else if (gameTitle.contains("강아지 vs 고양이")) {
            // 전체 베스트 1위 - 오래된 인기 게임
            totalLikes = 20;
            dailyLikes = 0;
            weeklyLikes = 0;
            monthlyLikes = 3;
        } else {
            // 나머지 게임들은 적은 좋아요
            totalLikes = 1 + random.nextInt(3);
            dailyLikes = random.nextInt(2);
            weeklyLikes = dailyLikes;
            monthlyLikes = totalLikes;
        }
        
        // 실제 좋아요 데이터 생성
        createLikesWithTimeDistribution(game, users, totalLikes, dailyLikes, weeklyLikes, monthlyLikes);
    }
    
    /**
     * 시간 분포를 고려한 좋아요 생성 - 확실한 버전
     */
    private void createLikesWithTimeDistribution(BalanceGame game, List<User> users, 
                                               int totalLikes, int dailyLikes, int weeklyLikes, int monthlyLikes) {
        LocalDateTime now = LocalDateTime.now();
        
        if (users.isEmpty()) return;
        
        int userIndex = 0;
        String gameTitle = game.getTitle();
        
        // 치킨 게임 - 일간 베스트 (최근 24시간에만 많은 좋아요)
        if (gameTitle.contains("치킨")) {
            for (int i = 0; i < 8; i++) {
                if (userIndex >= users.size()) userIndex = 0;
                createLike(users.get(userIndex++), game, now.minusHours(i + 1));
            }
            log.info("치킨 게임 일간 좋아요 8개 생성");
            return;
        }
        
        // 바다 게임 - 주간 베스트 (7일 내 많음, 오늘은 적음)
        if (gameTitle.contains("바다")) {
            // 오늘 1개
            if (userIndex >= users.size()) userIndex = 0;
            createLike(users.get(userIndex++), game, now.minusHours(2));
            
            // 1-6일 전 10개
            for (int i = 0; i < 10; i++) {
                if (userIndex >= users.size()) userIndex = 0;
                LocalDateTime likeTime = now.minusHours(25 + (i * 12)); // 25~145시간 전
                createLike(users.get(userIndex++), game, likeTime);
            }
            log.info("바다 게임 주간 좋아요 11개 생성 (일간 1개, 주간 전체 11개)");
            return;
        }
        
        // 여름 게임 - 월간 베스트 (30일 내 많음, 최근 7일은 적음)
        if (gameTitle.contains("여름")) {
            // 8일 전에 2개
            for (int i = 0; i < 2; i++) {
                if (userIndex >= users.size()) userIndex = 0;
                createLike(users.get(userIndex++), game, now.minusHours(170 + (i * 12)));
            }
            
            // 10-30일 전에 15개
            for (int i = 0; i < 15; i++) {
                if (userIndex >= users.size()) userIndex = 0;
                LocalDateTime likeTime = now.minusHours(240 + (i * 24)); // 10-30일 전 분산
                createLike(users.get(userIndex++), game, likeTime);
            }
            log.info("여름 게임 월간 좋아요 17개 생성 (일간 0개, 주간 2개, 월간 전체 17개)");
            return;
        }
        
        // 나머지 게임들은 적은 좋아요
        for (int i = 0; i < 1 && userIndex < users.size(); i++) {
            createLike(users.get(userIndex++), game, now.minusDays(random.nextInt(10)));
        }
    }
    
    /**
     * 좋아요 생성 헬퍼 메서드
     */
    private void createLike(User user, BalanceGame game, LocalDateTime likeTime) {
        // 중복 좋아요 방지
        if (likeRepository.findByUserIdAndBalanceGameId(user.getId(), game.getId()).isEmpty()) {
            Like like = new Like();
            like.setUser(user);
            like.setBalanceGame(game);
            like.setCreatedAt(likeTime);
            likeRepository.save(like);
            
            log.info("Like 생성: {} - {} - {}", game.getTitle(), user.getUsername(), likeTime);
        }
    }
}