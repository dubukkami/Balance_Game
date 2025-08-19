# 🍻 술하재밸 - 밸런스 게임 커뮤니티

> Vue.js와 Spring Boot로 구현된 밸런스 게임 커뮤니티 플랫폼

🌐 **사이트 주소**: https://balancegame-delta.vercel.app/

## ✨ 주요 기능

- 🎮 밸런스 게임 생성/투표
- 💬 계층형 댓글 & 대댓글 시스템  
- 🔐 OAuth 소셜 로그인 (Google, Kakao, Naver)
- 📊 게임 정렬 (최신순, 인기순, 투표수순, 좋아요순)
- ❤️ 좋아요 & 북마크 시스템
- 📱 반응형 모바일 최적화
- 🔍 게임 검색 기능

## 🛠 기술 스택

**백엔드**
- Java 17, Spring Boot 3.2.1
- Spring Security, Spring Data JPA
- PostgreSQL, HikariCP
- JWT 인증, OAuth2

**프론트엔드**
- Vue.js 3, Composition API
- Vite, Pinia (상태관리)
- CSS3, 반응형 디자인

**배포 및 인프라**
- Render (백엔드 배포)
- Vercel (프론트엔드 배포)
- PostgreSQL (Neon DB)
- GitHub Actions (CI/CD)

## 🚀 빠른 시작

### 로컬 개발 환경
```bash
# 저장소 클론
git clone https://github.com/your-username/balance-game-community.git
cd balance-game-community

# 백엔드 실행
cd backend
./gradlew bootRun

# 프론트엔드 실행 (새 터미널)
cd frontend
npm install
npm run dev
```

### 🚀 Render 배포
**상세한 배포 가이드는 [RENDER_DEPLOYMENT.md](./RENDER_DEPLOYMENT.md)를 참고하세요.**

**로컬 개발 환경**
- 🖥️ 프론트엔드: http://localhost:3000
- 🔧 백엔드 API: http://localhost:8080
- 🗄️ H2 콘솔: http://localhost:8080/h2-console

## 📁 프로젝트 구조

```
balance-game-community/
├── 🖥️ backend/                    # Spring Boot API 서버
│   ├── src/main/java/com/drink/balancegame/
│   │   ├── 🎮 controller/         # REST API 컨트롤러
│   │   ├── 🏗️ entity/             # JPA 엔티티 (게임, 사용자, 투표 등)
│   │   ├── 🗄️ repository/         # 데이터 접근 계층
│   │   ├── 🔐 security/           # JWT & OAuth2 인증/인가
│   │   ├── 📋 dto/                # 데이터 전송 객체
│   │   ├── ⚙️ config/             # 설정 클래스
│   │   └── 🔧 service/            # 비즈니스 로직
│   ├── src/main/resources/
│   │   ├── application.yml        # 기본 설정
│   │   ├── application-local.yml  # 로컬 환경
│   │   └── application-prod.yml   # 운영 환경
│   └── build.gradle
├── 🌐 frontend/                   # Vue.js 클라이언트
│   ├── src/
│   │   ├── 🧩 components/         # 재사용 가능한 컴포넌트
│   │   │   ├── ui/               # 기본 UI 컴포넌트
│   │   │   └── LoginModal.vue    # 로그인 모달
│   │   ├── 📄 views/              # 페이지 컴포넌트
│   │   │   ├── Home.vue          # 메인 페이지
│   │   │   ├── GameList.vue      # 게임 목록
│   │   │   └── GameDetail.vue    # 게임 상세
│   │   ├── 📦 stores/             # Pinia 상태 관리
│   │   ├── 🛣️ router/             # Vue Router 설정
│   │   └── 🎨 styles/             # 전역 스타일
│   ├── package.json
│   └── vite.config.js
├── 🚀 .github/workflows/          # GitHub Actions CI/CD
├── 🐳 docker-compose.yml          # 로컬 개발용 Docker
└── 📚 README.md
```

## 📡 API 엔드포인트

### 🎮 게임 관리
```http
GET    /api/web/balance-games           # 게임 목록 (정렬: latest, popular, votes, likes)
GET    /api/web/balance-games/{id}      # 게임 상세 조회
POST   /api/web/balance-games           # 게임 생성 (인증 필요)
PUT    /api/web/balance-games/{id}      # 게임 수정 (인증 필요)
DELETE /api/web/balance-games/{id}      # 게임 삭제 (인증 필요)
POST   /api/web/balance-games/{id}/like # 좋아요 토글 (인증 필요)
```

### 🗳️ 투표 시스템
```http
GET  /api/votes/balance-games/{gameId}/stats  # 투표 통계 조회
POST /api/votes/balance-games/{gameId}        # 투표하기 (인증 필요)
GET  /api/votes/user/balance-games/{gameId}   # 사용자 투표 여부 확인
```

### 💬 댓글 시스템
```http
GET    /api/comments/game/{gameId}/list       # 댓글 목록 (계층형)
POST   /api/comments/game/{gameId}            # 댓글 작성 (인증 필요)
PUT    /api/comments/{commentId}              # 댓글 수정 (인증 필요)
DELETE /api/comments/{commentId}              # 댓글 삭제 (인증 필요)
```

### 👤 사용자 관리
```http
POST /api/auth/register                       # 일반 회원가입
POST /api/auth/login                          # 일반 로그인
GET  /oauth2/authorization/{provider}         # 소셜 로그인 (Google, Kakao, Naver)
GET  /api/web/users/profile                   # 프로필 조회 (인증 필요)
PUT  /api/web/users/profile                   # 프로필 수정 (인증 필요)
```

## 🚀 배포 및 운영

### 🌳 브랜치 전략
- `main` - 프로덕션 브랜치 (자동 배포)
- `develop` - 개발 브랜치
- `feature/*` - 기능 개발 브랜치

### 🏗️ 배포 파이프라인
1. **Render** (백엔드)
   - `main` 브랜치 자동 배포
   - PostgreSQL (Neon DB) 연결
   - 환경변수 기반 설정

2. **Vercel** (프론트엔드)
   - `main` 브랜치 자동 배포
   - SPA 최적화 빌드
   - CDN 캐싱

3. **GitHub Actions**
   - 자동 테스트 및 빌드
   - 코드 품질 검사

### 🔧 개발 워크플로우
```bash
# 새 기능 개발
git checkout main
git pull origin main
git checkout -b feature/new-feature

# 개발 및 커밋
git add .
git commit -m "feat: 새로운 기능 추가"
git push origin feature/new-feature

# Pull Request 생성 → main 브랜치로 병합 → 자동 배포
```

---

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 있습니다.

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request