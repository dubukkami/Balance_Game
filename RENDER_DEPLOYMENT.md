# 🚀 Render 배포 가이드

## 📋 사전 준비

### 1. GitHub 레포지토리 준비
- 코드가 GitHub에 푸시되어 있어야 함
- `render.yaml` 파일이 루트에 위치해야 함

### 2. Render 계정 생성
- [Render.com](https://render.com) 회원가입
- GitHub 계정 연결

---

## 🗄️ 데이터베이스 설정

### 1. PostgreSQL 데이터베이스 생성
```
1. Render 대시보드 → "New +" → "PostgreSQL"
2. 설정:
   - Name: balance-game-db
   - Database Name: balance_game_db
   - User: balance_game_user
   - Region: Singapore
   - Plan: Free
```

### 2. 데이터베이스 정보 확인
```
생성 후 External Database URL을 복사해둘 것
예시: postgres://username:password@hostname:port/database_name
```

---

## 🌐 백엔드 웹 서비스 배포

### 1. 새 웹 서비스 생성
```
1. Render 대시보드 → "New +" → "Web Service"
2. GitHub 레포지토리 연결
3. 설정:
   - Name: balance-game-backend
   - Environment: Docker
   - Branch: main
   - Region: Singapore
   - Plan: Free
```

### 2. 필수 환경변수 설정

**데이터베이스 연결:**
```
DATABASE_URL = [PostgreSQL External URL]
SPRING_PROFILES_ACTIVE = prod
```

**JWT 보안:**
```
JWT_SECRET = [32자리 이상 랜덤 문자열]
```

**CORS 설정:**
```
CORS_ALLOWED_ORIGINS = https://balancegame-delta.vercel.app,http://localhost:3000
```

**OAuth 소셜 로그인 (각각 설정 필요):**

**Google OAuth:**
```
GOOGLE_CLIENT_ID = [Google Console에서 발급]
GOOGLE_CLIENT_SECRET = [Google Console에서 발급]
```

**Kakao OAuth:**
```
KAKAO_CLIENT_ID = [Kakao Developers에서 발급]
KAKAO_CLIENT_SECRET = [Kakao Developers에서 발급]
```

**Naver OAuth:**
```
NAVER_CLIENT_ID = [Naver Developers에서 발급]
NAVER_CLIENT_SECRET = [Naver Developers에서 발급]
```

### 3. Health Check 설정
```
Health Check Path: /actuator/health
```

---

## 🔑 OAuth 클라이언트 설정 가이드

### 1. Google OAuth 설정
```
1. Google Cloud Console 접속
2. API 및 서비스 → 사용자 인증 정보
3. OAuth 2.0 클라이언트 ID 생성
4. 승인된 리디렉션 URI 추가:
   - https://your-render-app.onrender.com/login/oauth2/code/google
   - http://localhost:8080/login/oauth2/code/google (개발용)
```

### 2. Kakao OAuth 설정
```
1. Kakao Developers 접속
2. 애플리케이션 추가
3. 제품 설정 → 카카오 로그인
4. Redirect URI 설정:
   - https://your-render-app.onrender.com/login/oauth2/code/kakao
   - http://localhost:8080/login/oauth2/code/kakao (개발용)
```

### 3. Naver OAuth 설정
```
1. Naver Developers 접속
2. 애플리케이션 등록
3. API 설정 → 네아로 (네이버 아이디로 로그인)
4. Callback URL 설정:
   - https://your-render-app.onrender.com/login/oauth2/code/naver
   - http://localhost:8080/login/oauth2/code/naver (개발용)
```

---

## 🚀 배포 과정

### 1. 자동 배포
```
1. GitHub에 코드 푸시
2. Render가 자동으로 빌드 및 배포 시작
3. 로그에서 빌드 상태 확인
```

### 2. 배포 확인
```
1. 배포 완료 후 제공된 URL로 접속
2. https://your-app-name.onrender.com/actuator/health 에서 상태 확인
3. API 엔드포인트 테스트
```

### 3. 프론트엔드 환경변수 업데이트
```
Vercel에서 VITE_API_BASE_URL을 새 Render URL로 업데이트:
VITE_API_BASE_URL = https://your-render-app.onrender.com
```

---

## ⚠️ 주의사항

### 1. Free 플랜 제한
- **메모리**: 512MB
- **CPU**: 공유
- **비활성화**: 15분 후 자동 슬립 (첫 요청 시 재시작에 시간 소요)
- **빌드 시간**: 월 500분 제한

### 2. Cold Start 대응
- 첫 번째 요청 시 응답이 느릴 수 있음 (슬립 상태에서 깨어나는 시간)
- 주기적인 Health Check로 서버를 깨워둘 수 있음

### 3. 환경변수 보안
- JWT_SECRET은 반드시 강력한 랜덤 문자열 사용
- OAuth 클라이언트 시크릿은 절대 노출 금지

---

## 🛠️ 트러블슈팅

### 1. 빌드 실패
```
- Docker 빌드 로그 확인
- Dockerfile 경로 및 문법 검증
- 의존성 문제 확인
```

### 2. 데이터베이스 연결 실패
```
- DATABASE_URL 환경변수 확인
- PostgreSQL 서비스 상태 확인
- 방화벽/보안 그룹 설정 확인
```

### 3. OAuth 로그인 실패
```
- Redirect URI 정확성 확인
- Client ID/Secret 환경변수 확인
- CORS 설정 확인
```

### 4. 메모리 부족 (OOM)
```
- JVM 힙 메모리 설정 조정 (-Xmx400m)
- 불필요한 의존성 제거
- 코드 최적화
```

---

## 📊 모니터링

### 1. 로그 확인
```
Render 대시보드 → 서비스 → Logs 탭에서 실시간 로그 확인
```

### 2. 메트릭 확인
```
Render 대시보드 → 서비스 → Metrics 탭에서 CPU/메모리 사용률 확인
```

### 3. Health Check
```
https://your-render-app.onrender.com/actuator/health
정기적으로 서버 상태 확인
```