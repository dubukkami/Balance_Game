package com.drink.balancegame.config;

import com.drink.balancegame.entity.BalanceGame;
import com.drink.balancegame.entity.Comment;
import com.drink.balancegame.entity.User;
import com.drink.balancegame.entity.Vote;
import com.drink.balancegame.repository.BalanceGameRepository;
import com.drink.balancegame.repository.CommentRepository;
import com.drink.balancegame.repository.UserRepository;
import com.drink.balancegame.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 애플리케이션 시작 시 초기 데이터를 생성하는 컴포넌트
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final BalanceGameRepository balanceGameRepository;
    private final VoteRepository voteRepository;
    private final CommentRepository commentRepository;
    
    @Override
    public void run(String... args) throws Exception {
        // 데이터가 이미 존재하는지 확인
        if (userRepository.count() > 0) {
            return; // 이미 데이터가 있으면 초기화하지 않음
        }
        
        // 기본 사용자 생성 (OAuth 사용자로 설정)
        User user1 = User.builder()
                .username("google_user1")
                .email("user1@example.com")
                .nickname("테스터1")
                .role(User.Role.USER)
                .provider(User.Provider.GOOGLE)
                .providerId("google_123456789")
                .profileImageUrl("https://example.com/profile1.jpg")
                .build();
        
        User user2 = User.builder()
                .username("kakao_user2")
                .email("user2@example.com")
                .nickname("테스터2")
                .role(User.Role.USER)
                .provider(User.Provider.KAKAO)
                .providerId("kakao_987654321")
                .profileImageUrl("https://example.com/profile2.jpg")
                .build();
        
        User user3 = User.builder()
                .username("naver_user3")
                .email("user3@example.com")
                .nickname("테스터3")
                .role(User.Role.USER)
                .provider(User.Provider.NAVER)
                .providerId("naver_555666777")
                .profileImageUrl("https://example.com/profile3.jpg")
                .build();
        
        User admin = User.builder()
                .username("admin_google")
                .email("admin@example.com")
                .nickname("관리자")
                .role(User.Role.ADMIN)
                .provider(User.Provider.GOOGLE)
                .providerId("google_admin123")
                .profileImageUrl("https://example.com/admin.jpg")
                .build();
        
        // 사용자 저장
        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);
        user3 = userRepository.save(user3);
        admin = userRepository.save(admin);
        
        // 밸런스 게임 생성
        BalanceGame game1 = BalanceGame.builder()
                .title("치킨 vs 피자")
                .description("영원한 고민! 당신의 선택은?")
                .optionA("치킨")
                .optionB("피자")
                .optionADescription("바삭바삭한 치킨의 매력! 다양한 맛과 소스로 질리지 않는다.")
                .optionBDescription("이탈리아의 정통 요리 피자! 다양한 토핑과 치즈의 조화가 완벽하다.")
                .viewCount(1250)
                .author(user1)
                .build();
        
        BalanceGame game2 = BalanceGame.builder()
                .title("아이폰 vs 갤럭시")
                .description("스마트폰의 양대 산맥! 어떤 것을 선택하시겠습니까?")
                .optionA("아이폰")
                .optionB("갤럭시")
                .optionADescription("iOS의 완성도와 애플 생태계의 연동성이 뛰어나다.")
                .optionBDescription("안드로이드의 자유로움과 다양한 기능, 합리적인 가격이 매력적이다.")
                .viewCount(2180)
                .author(user2)
                .build();
        
        BalanceGame game3 = BalanceGame.builder()
                .title("여름 vs 겨울")
                .description("계절의 선택! 당신이 더 좋아하는 계절은?")
                .optionA("여름")
                .optionB("겨울")
                .optionADescription("시원한 바다와 수영장, 긴 낮과 휴가의 계절!")
                .optionBDescription("하얀 눈과 스키, 따뜻한 실내와 코코아의 계절!")
                .viewCount(890)
                .author(user3)
                .build();
        
        BalanceGame game4 = BalanceGame.builder()
                .title("커피 vs 차")
                .description("음료의 선택! 당신의 픽은?")
                .optionA("커피")
                .optionB("차")
                .optionADescription("진한 향과 카페인으로 하루를 시작하는 커피!")
                .optionBDescription("다양한 종류와 건강한 효능을 가진 차!")
                .viewCount(1120)
                .author(user1)
                .build();
        
        BalanceGame game5 = BalanceGame.builder()
                .title("고양이 vs 강아지")
                .description("반려동물의 선택! 어떤 것을 더 좋아하시나요?")
                .optionA("고양이")
                .optionB("강아지")
                .optionADescription("독립적이고 우아한 매력의 고양이! 조용하고 관리가 쉽다.")
                .optionBDescription("충성스럽고 활발한 매력의 강아지! 산책과 놀이를 함께할 수 있다.")
                .viewCount(1680)
                .author(user2)
                .build();
        
        // 밸런스 게임 저장
        game1 = balanceGameRepository.save(game1);
        game2 = balanceGameRepository.save(game2);
        game3 = balanceGameRepository.save(game3);
        game4 = balanceGameRepository.save(game4);
        game5 = balanceGameRepository.save(game5);
        
        // 투표 생성
        Vote vote1 = Vote.builder()
                .selectedOption(Vote.VoteOption.A)
                .user(user2)
                .balanceGame(game1)
                .build();
        
        Vote vote2 = Vote.builder()
                .selectedOption(Vote.VoteOption.B)
                .user(user3)
                .balanceGame(game1)
                .build();
        
        Vote vote3 = Vote.builder()
                .selectedOption(Vote.VoteOption.A)
                .user(user1)
                .balanceGame(game2)
                .build();
        
        Vote vote4 = Vote.builder()
                .selectedOption(Vote.VoteOption.B)
                .user(user3)
                .balanceGame(game2)
                .build();
        
        Vote vote5 = Vote.builder()
                .selectedOption(Vote.VoteOption.A)
                .user(user1)
                .balanceGame(game3)
                .build();
        
        Vote vote6 = Vote.builder()
                .selectedOption(Vote.VoteOption.B)
                .user(user2)
                .balanceGame(game3)
                .build();
        
        Vote vote7 = Vote.builder()
                .selectedOption(Vote.VoteOption.A)
                .user(user3)
                .balanceGame(game4)
                .build();
        
        Vote vote8 = Vote.builder()
                .selectedOption(Vote.VoteOption.B)
                .user(user1)
                .balanceGame(game5)
                .build();
        
        Vote vote9 = Vote.builder()
                .selectedOption(Vote.VoteOption.A)
                .user(user2)
                .balanceGame(game5)
                .build();
        
        // 투표 저장
        voteRepository.save(vote1);
        voteRepository.save(vote2);
        voteRepository.save(vote3);
        voteRepository.save(vote4);
        voteRepository.save(vote5);
        voteRepository.save(vote6);
        voteRepository.save(vote7);
        voteRepository.save(vote8);
        voteRepository.save(vote9);
        
        // 댓글 생성
        Comment comment1 = Comment.builder()
                .content("치킨이 진리죠! 특히 양념치킨은 못 참아요 ㅠㅠ")
                .author(user2)
                .balanceGame(game1)
                .build();
        
        Comment comment2 = Comment.builder()
                .content("피자도 맛있지만 치킨만 한 게 없어요!")
                .author(user3)
                .balanceGame(game1)
                .build();
        
        Comment comment3 = Comment.builder()
                .content("아이폰 사용자인데 정말 만족합니다!")
                .author(user1)
                .balanceGame(game2)
                .build();
        
        Comment comment4 = Comment.builder()
                .content("여름 바다 휴가가 최고죠!")
                .author(user2)
                .balanceGame(game3)
                .build();
        
        Comment comment5 = Comment.builder()
                .content("커피 없으면 하루가 시작 안 돼요")
                .author(user3)
                .balanceGame(game4)
                .build();
        
        // 댓글 저장
        commentRepository.save(comment1);
        commentRepository.save(comment2);
        commentRepository.save(comment3);
        commentRepository.save(comment4);
        commentRepository.save(comment5);
        
        System.out.println("초기 데이터 생성 완료!");
    }
}