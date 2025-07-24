<!--
  웹 전용 로그인 페이지
  데스크톱/태블릿용 OAuth 소셜 로그인 + 테스트 로그인 제공
-->
<template>
  <div class="login-page">
    <!-- 페이지 헤더 -->
    <section class="page-header">
      <div class="header-container">
        <div class="header-content">
          <div class="header-icon">🍻</div>
          <h1 class="page-title">술하재밸 로그인</h1>
          <p class="page-subtitle">술 마실 때 재밌는 밸런스 게임에 참여해보세요!</p>
        </div>
      </div>
    </section>

    <!-- 메인 콘텐츠 -->
    <main class="main-content">
      <div class="container">
        <div class="login-container">
          <!-- 소셜 로그인 섹션 -->
          <div class="login-section">
            <div class="section-header">
              <div class="section-icon">🔐</div>
              <h2>소셜 로그인</h2>
              <p>간편하고 안전한 소셜 로그인을 이용해보세요</p>
            </div>
            
            <div class="social-buttons">
              <button @click="socialLogin('google')" class="social-btn google-btn" :disabled="loading">
                <span class="btn-icon">🔴</span>
                <span class="btn-text">Google로 로그인</span>
              </button>
              <button @click="socialLogin('kakao')" class="social-btn kakao-btn" :disabled="loading">
                <span class="btn-icon">💬</span>
                <span class="btn-text">카카오로 로그인</span>
              </button>
              <button @click="socialLogin('naver')" class="social-btn naver-btn" :disabled="loading">
                <span class="btn-icon">🟢</span>
                <span class="btn-text">네이버로 로그인</span>
              </button>
            </div>
          </div>
          
          <!-- 구분선 -->
          <div class="divider">
            <div class="divider-line"></div>
            <div class="divider-text">또는</div>
            <div class="divider-line"></div>
          </div>
          
          <!-- 테스트 로그인 섹션 -->
          <div class="test-section">
            <div class="section-header">
              <div class="section-icon">🧪</div>
              <h2>테스트 로그인</h2>
              <p>개발용 테스트 계정으로 빠르게 체험해보세요</p>
            </div>
            
            <div class="test-buttons">
              <button @click="testLogin('google_user1')" class="test-btn" :disabled="loading">
                <span class="test-icon">👤</span>
                <span class="test-info">
                  <span class="test-name">테스터1</span>
                  <span class="test-provider">Google 계정</span>
                </span>
              </button>
              <button @click="testLogin('kakao_user2')" class="test-btn" :disabled="loading">
                <span class="test-icon">👤</span>
                <span class="test-info">
                  <span class="test-name">테스터2</span>
                  <span class="test-provider">카카오 계정</span>
                </span>
              </button>
              <button @click="testLogin('naver_user3')" class="test-btn" :disabled="loading">
                <span class="test-icon">👤</span>
                <span class="test-info">
                  <span class="test-name">테스터3</span>
                  <span class="test-provider">네이버 계정</span>
                </span>
              </button>
            </div>
          </div>
          
          <!-- 에러 메시지 -->
          <div v-if="error" class="error-message">
            <span class="error-icon">⚠️</span>
            {{ error }}
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
/**
 * 술하재밸 로그인 페이지 로직
 */
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import axios from 'axios'

const router = useRouter()
const authStore = useAuthStore()

const loading = ref(false)
const error = ref('')


/**
 * 소셜 로그인 처리
 */
const socialLogin = (provider) => {
  loading.value = true
  error.value = ''
  
  // OAuth 로그인 URL로 이동
  window.location.href = `http://localhost:8080/oauth2/authorization/${provider}`
}

/**
 * 테스트 로그인 처리 (웹 API)
 */
const testLogin = async (username) => {
  loading.value = true
  error.value = ''

  try {
    const response = await axios.post('/api/web/auth/test-login', {
      username: username,
      password: 'test' // 테스트용 비밀번호
    })
    
    // Auth store를 통해 로그인 처리
    authStore.login(response.data.user, response.data.token)
    
    // 홈 페이지로 이동
    router.push('/')
    
  } catch (err) {
    console.error('테스트 로그인 실패:', err)
    error.value = '테스트 로그인에 실패했습니다. 다시 시도해주세요.'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* 로그인 페이지 - 완전한 웹사이트 스타일 */
.login-page {
  background: #ffffff;
  min-height: 100vh;
}

/* 페이지 헤더 */
.page-header {
  background: #f8fafc;
  padding: 80px 0;
  position: relative;
  display: flex;
  justify-content: center;
}

.page-header .header-container {
  background: linear-gradient(135deg, #FF6B35 0%, #F7931E 50%, #FFD23F 100%);
  border-radius: 30px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.15);
  position: relative;
  overflow: hidden;
}

.page-header .header-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: radial-gradient(ellipse at center, rgba(255,255,255,0.15) 0%, transparent 70%);
  pointer-events: none;
  z-index: 1;
}

.header-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 60px 80px;
  position: relative;
  z-index: 2;
  color: white;
}

.header-icon {
  font-size: 3rem;
  margin-bottom: 16px;
  display: block;
  filter: drop-shadow(0 4px 8px rgba(0,0,0,0.3));
}

.header-content {
  text-align: center;
}

.page-title {
  font-size: 2.4rem;
  font-weight: 800;
  line-height: 1.2;
  margin-bottom: 16px;
  text-shadow: 0 4px 20px rgba(0,0,0,0.3);
  background: linear-gradient(45deg, #ffffff, #e8f4fd);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.page-subtitle {
  font-size: 1.1rem;
  font-weight: 400;
  opacity: 0.95;
  line-height: 1.4;
  max-width: 600px;
  margin: 0 auto;
}

/* 메인 콘텐츠 */
.main-content {
  padding: 60px 0;
  background: #f8fafc;
}

.container {
  max-width: 600px;
  margin: 0 auto;
  padding: 0 60px;
}

.login-container {
  background: white;
  border-radius: 20px;
  box-shadow: 0 10px 40px rgba(0,0,0,0.1);
  border: 1px solid #e2e8f0;
  overflow: hidden;
}

/* 섹션 헤더 */
.section-header {
  text-align: center;
  margin-bottom: 30px;
}

.section-icon {
  font-size: 2.5rem;
  margin-bottom: 16px;
  display: block;
}

.section-header h2 {
  font-size: 1.6rem;
  font-weight: 700;
  color: #1a202c;
  margin-bottom: 8px;
}

.section-header p {
  color: #64748b;
  font-size: 1rem;
  margin: 0;
}

/* 로그인 섹션 */
.login-section {
  padding: 40px;
  border-bottom: 1px solid #e2e8f0;
}

.social-buttons {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.social-btn {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 24px;
  border: none;
  border-radius: 12px;
  font-weight: 600;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s ease;
  font-family: inherit;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
}

.social-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.15);
}

.social-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.btn-icon {
  font-size: 1.2rem;
}

.btn-text {
  flex: 1;
  text-align: center;
}

.google-btn {
  background: linear-gradient(135deg, #4285f4, #34a853);
  color: white;
}

.google-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #3367d6, #2e7d32);
}

.kakao-btn {
  background: linear-gradient(135deg, #fee500, #fdd835);
  color: #333;
}

.kakao-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #fbc02d, #f57f17);
}

.naver-btn {
  background: linear-gradient(135deg, #03c75a, #00c853);
  color: white;
}

.naver-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #00a152, #2e7d32);
}

/* 구분선 */
.divider {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 30px 40px;
}

.divider-line {
  flex: 1;
  height: 1px;
  background: #e2e8f0;
}

.divider-text {
  color: #64748b;
  font-weight: 600;
  font-size: 0.9rem;
  background: white;
  padding: 0 16px;
}

/* 테스트 섹션 */
.test-section {
  padding: 40px;
}

.test-buttons {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.test-btn {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 24px;
  background: #f8fafc;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-family: inherit;
}

.test-btn:hover:not(:disabled) {
  background: #f1f5f9;
  border-color: #cbd5e0;
  transform: translateY(-2px);
  box-shadow: 0 5px 20px rgba(0,0,0,0.1);
}

.test-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.test-icon {
  font-size: 1.5rem;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #e2e8f0;
  border-radius: 50%;
}

.test-info {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  flex: 1;
}

.test-name {
  font-weight: 600;
  color: #1a202c;
  font-size: 1rem;
  margin-bottom: 4px;
}

.test-provider {
  font-size: 0.85rem;
  color: #64748b;
}

/* 에러 메시지 */
.error-message {
  display: flex;
  align-items: center;
  gap: 10px;
  background: linear-gradient(135deg, #fee2e2, #fecaca);
  color: #dc2626;
  padding: 16px 24px;
  margin: 20px 40px 40px;
  border-radius: 12px;
  border: 2px solid #fca5a5;
  font-size: 0.95rem;
  font-weight: 500;
}

.error-icon {
  font-size: 1.2rem;
}

/* 반응형 디자인 */
@media (max-width: 1024px) {
  .header-container,
  .container {
    padding: 0 30px;
  }
  
  .page-title {
    font-size: 2rem;
  }
}

@media (max-width: 768px) {
  .page-header {
    padding: 40px 0;
  }
  
  .header-container,
  .container {
    padding: 0 20px;
  }
  
  .page-title {
    font-size: 1.8rem;
  }
  
  .page-subtitle {
    font-size: 1rem;
  }
  
  .login-container {
    border-radius: 16px;
  }
  
  .login-section,
  .test-section {
    padding: 30px 20px;
  }
  
  .divider {
    padding: 20px;
  }
  
  .error-message {
    margin: 20px 20px 30px;
  }
  
  .social-btn,
  .test-btn {
    padding: 14px 20px;
  }
  
  .btn-text {
    font-size: 0.95rem;
  }
}

@media (max-width: 480px) {
  .container {
    max-width: none;
  }
  
  .social-buttons,
  .test-buttons {
    gap: 12px;
  }
  
  .social-btn,
  .test-btn {
    padding: 12px 16px;
  }
  
  .test-info {
    gap: 2px;
  }
  
  .test-name {
    font-size: 0.95rem;
  }
  
  .test-provider {
    font-size: 0.8rem;
  }
}
</style>