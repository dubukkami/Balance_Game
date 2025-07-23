# 밸런스 게임 커뮤니티

Vue.js와 Spring Boot로 구현된 밸런스 게임 커뮤니티 플랫폼

## 주요 기능

- 밸런스 게임 생성/투표
- 댓글 & 대댓글 시스템  
- OAuth 소셜 로그인 (Google, Kakao, Naver)
- 게임 정렬 (최신순, 인기순, 투표수순)
- 좋아요 시스템

## 기술 스택

**백엔드**: Java 17, Spring Boot 3.2.1, Spring Security, JPA, PostgreSQL  
**프론트엔드**: Vue.js 3, Vite, Pinia  
**배포**: Railway (백엔드), Vercel (프론트엔드), Neon PostgreSQL

## 빠른 시작

```bash
# 저장소 클론
git clone <repository-url>
cd balance-game-community

# 백엔드 실행
cd backend
./gradlew bootRun

# 프론트엔드 실행 (새 터미널)
cd frontend
npm install
npm run dev
```

- 프론트엔드: http://localhost:3000
- 백엔드 API: http://localhost:8080
- H2 콘솔: http://localhost:8080/h2-console

## 프로젝트 구조

```
├── backend/           # Spring Boot
│   ├── src/main/java/com/drink/balancegame/
│   │   ├── controller/    # REST API
│   │   ├── entity/        # JPA 엔티티
│   │   ├── repository/    # 데이터 접근
│   │   ├── security/      # 인증/인가
│   │   └── service/       # 비즈니스 로직
│   └── build.gradle
├── frontend/          # Vue.js
│   ├── src/
│   │   ├── components/    # Vue 컴포넌트
│   │   ├── views/         # 페이지
│   │   └── stores/        # 상태 관리
│   └── package.json
└── .github/workflows/ # CI/CD
```

## API 엔드포인트

### 게임
- `GET /api/balance-games` - 게임 목록 (sort: latest, popular, votes)
- `GET /api/balance-games/{id}` - 게임 상세
- `POST /api/balance-games` - 게임 생성 (인증)
- `POST /api/balance-games/{id}/like` - 좋아요 (인증)

### 투표
- `GET /api/votes/balance-games/{gameId}/stats` - 투표 통계
- `POST /api/votes/balance-games/{gameId}` - 투표하기 (인증)

### 댓글
- `GET /api/comments/game/{gameId}/list` - 댓글 목록
- `POST /api/comments/game/{gameId}` - 댓글 작성 (인증)

### 사용자
- `POST /api/users/register` - 회원가입
- `POST /api/users/login` - 로그인
- `GET /oauth2/authorization/{provider}` - 소셜 로그인

## 배포

### 브랜치 전략
- `develop` - 개발 브랜치 (기본)
- `main` - 프로덕션 브랜치 (develop → main 병합 시 자동 배포)

### 배포 설정
1. **Railway** (백엔드): main 브랜치 연결, `/backend` 디렉토리
2. **Vercel** (프론트엔드): main 브랜치 연결, `/frontend` 디렉토리  
3. **Neon PostgreSQL**: 프로덕션 데이터베이스

## 개발 워크플로우

```bash
# 기능 개발
git checkout develop
git checkout -b feature/new-feature
# 개발 작업...
git commit -m "feat: Add new feature"
git push origin feature/new-feature

# PR 생성: feature → develop
# develop → main 병합 시 자동 배포
```