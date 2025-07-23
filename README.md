# 밸런스 게임 커뮤니티 🎮

Vue.js와 Spring Boot로 구현된 밸런스 게임 커뮤니티 플랫폼입니다.

## 🚀 주요 기능

- **밸런스 게임 생성**: 사용자가 직접 A vs B 형태의 밸런스 게임을 만들 수 있습니다
- **투표 시스템**: 다른 사용자들이 만든 게임에 투표할 수 있습니다
- **댓글 시스템**: 각 게임에 댓글을 달아 의견을 공유할 수 있습니다
- **사용자 관리**: 회원가입, 로그인, 프로필 관리 기능을 제공합니다
- **게임 관리**: 게임 목록, 검색, 정렬 기능을 제공합니다

## 🛠️ 기술 스택

### 백엔드
- **Java 17**
- **Spring Boot 3.1.0**
- **Spring Data JPA**
- **H2 Database**
- **Gradle**

### 프론트엔드
- **Vue.js 3**
- **Vue Router 4**
- **Pinia** (상태 관리)
- **Axios** (HTTP 클라이언트)
- **Vite** (빌드 도구)

### 인프라
- **Docker & Docker Compose**
- **Nginx** (프론트엔드 서버)

## 📋 시스템 요구사항

- **Docker**: 20.10+
- **Docker Compose**: 2.0+
- **Git**: 2.30+

## 🏃 빠른 시작

### 1. 저장소 클론
```bash
git clone <repository-url>
cd balance-game-community
```

### 2. 프로덕션 환경 실행
```bash
# 스크립트 실행
./start.sh

# 또는 직접 Docker Compose 실행
docker-compose up -d
```

### 3. 개발 환경 실행
```bash
# 개발 환경 스크립트 실행
./start-dev.sh

# 또는 직접 Docker Compose 실행
docker-compose -f docker-compose.dev.yml up -d
```

### 4. 애플리케이션 접속
- **프론트엔드**: http://localhost:3000
- **백엔드 API**: http://localhost:8080
- **H2 콘솔**: http://localhost:8080/h2-console

## 📁 프로젝트 구조

```
balance-game-community/
├── backend/                    # Spring Boot 백엔드
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/example/balancegame/
│   │       │       ├── controller/     # REST API 컨트롤러
│   │       │       ├── entity/         # JPA 엔티티
│   │       │       ├── repository/     # 데이터 접근 계층
│   │       │       └── dto/            # 데이터 전송 객체
│   │       └── resources/
│   │           └── application.yml     # 설정 파일
│   ├── build.gradle           # Gradle 빌드 설정
│   ├── Dockerfile            # 프로덕션 Docker 이미지
│   └── Dockerfile.dev        # 개발 환경 Docker 이미지
├── frontend/                  # Vue.js 프론트엔드
│   ├── src/
│   │   ├── components/       # Vue 컴포넌트
│   │   ├── views/           # 페이지 컴포넌트
│   │   ├── router/          # 라우터 설정
│   │   └── assets/          # 정적 자산
│   ├── package.json         # NPM 의존성
│   ├── vite.config.js       # Vite 설정
│   ├── Dockerfile           # 프로덕션 Docker 이미지
│   └── nginx.conf           # Nginx 설정
├── docker-compose.yml        # 프로덕션 Docker Compose
├── docker-compose.dev.yml    # 개발 환경 Docker Compose
├── start.sh                 # 프로덕션 시작 스크립트
├── start-dev.sh             # 개발 환경 시작 스크립트
└── README.md                # 프로젝트 문서
```

## 🔧 API 엔드포인트

### 사용자 관리
- `POST /api/users/register` - 회원가입
- `POST /api/users/login` - 로그인
- `GET /api/users/{id}` - 사용자 정보 조회
- `PUT /api/users/{id}` - 사용자 정보 수정

### 밸런스 게임
- `GET /api/balance-games` - 게임 목록 조회
- `GET /api/balance-games/{id}` - 게임 상세 조회
- `POST /api/balance-games` - 게임 생성
- `PUT /api/balance-games/{id}` - 게임 수정
- `DELETE /api/balance-games/{id}` - 게임 삭제
- `GET /api/balance-games/search` - 게임 검색

### 투표
- `GET /api/votes/game/{gameId}` - 게임별 투표 조회
- `POST /api/votes` - 투표 생성
- `PUT /api/votes/{id}` - 투표 수정
- `GET /api/votes/stats/{gameId}` - 투표 통계 조회

### 댓글
- `GET /api/comments/game/{gameId}` - 게임별 댓글 조회
- `POST /api/comments` - 댓글 생성
- `PUT /api/comments/{id}` - 댓글 수정
- `DELETE /api/comments/{id}` - 댓글 삭제

## 🗄️ 데이터베이스 스키마

### Users (사용자)
- `id` (PK)
- `username` (유니크)
- `email` (유니크)
- `password`
- `nickname`
- `created_at`
- `updated_at`

### Balance_Games (밸런스 게임)
- `id` (PK)
- `title`
- `description`
- `option_a`
- `option_b`
- `option_a_description`
- `option_b_description`
- `view_count`
- `author_id` (FK)
- `created_at`
- `updated_at`

### Votes (투표)
- `id` (PK)
- `selected_option` (A/B)
- `user_id` (FK)
- `balance_game_id` (FK)
- `created_at`

### Comments (댓글)
- `id` (PK)
- `content`
- `author_id` (FK)
- `balance_game_id` (FK)
- `created_at`
- `updated_at`

## 🧪 개발 환경 설정

### 백엔드 개발
```bash
cd backend
./gradlew bootRun
```

### 프론트엔드 개발
```bash
cd frontend
npm install
npm run dev
```

## 🐛 문제 해결

### 컨테이너 재시작
```bash
docker-compose down
docker-compose up -d
```

### 이미지 재빌드
```bash
docker-compose build --no-cache
```

### 로그 확인
```bash
docker-compose logs -f
```

### 데이터베이스 초기화
H2 데이터베이스는 인메모리 방식으로 애플리케이션 재시작 시 자동으로 초기화됩니다.

## 🤝 기여하기

1. 저장소를 포크합니다
2. 기능 브랜치를 생성합니다 (`git checkout -b feature/amazing-feature`)
3. 변경사항을 커밋합니다 (`git commit -m 'Add amazing feature'`)
4. 브랜치에 푸시합니다 (`git push origin feature/amazing-feature`)
5. Pull Request를 생성합니다

## 📝 라이선스

이 프로젝트는 MIT 라이선스를 따릅니다.

## 📧 문의

문의사항이 있으시면 이슈를 생성해주세요.

---

⭐ 이 프로젝트가 도움이 되었다면 별표를 눌러주세요!