<!--
  모바일 전용 로그인 페이지
  "술하재밸" 테마의 모바일 앱 스타일 로그인 페이지
-->
<template>
  <div class="login-mobile">
    <div class="login-container-mobile">
      <!-- 헤더 -->
      <div class="header-mobile">
        <div class="brand-mobile">
          <span class="brand-emoji">🍻</span>
          <h1>술하재밸</h1>
        </div>
        <p class="welcome-text">어서오세요!</p>
      </div>

      <!-- 소셜 로그인 섹션 -->
      <div class="social-section-mobile">
        <h2>간편 로그인</h2>
        <div class="social-buttons-mobile">
          <button 
            @click="socialLogin('google')"
            class="social-btn-mobile google-mobile"
            :disabled="loading"
          >
            <span class="social-icon-mobile">🔍</span>
            <span class="social-text-mobile">Google</span>
          </button>
          
          <button 
            @click="socialLogin('kakao')"
            class="social-btn-mobile kakao-mobile"
            :disabled="loading"
          >
            <span class="social-icon-mobile">💬</span>
            <span class="social-text-mobile">카카오</span>
          </button>
          
          <button 
            @click="socialLogin('naver')"
            class="social-btn-mobile naver-mobile"
            :disabled="loading"
          >
            <span class="social-icon-mobile">🟢</span>
            <span class="social-text-mobile">네이버</span>
          </button>
        </div>
      </div>

      <!-- 테스트 로그인 섹션 -->
      <div class="test-section-mobile">
        <div class="test-header-mobile">
          <span class="test-emoji">🧪</span>
          <h3>테스트 계정</h3>
        </div>
        <p class="test-desc-mobile">개발용 계정으로 빠르게 체험!</p>
        
        <div class="test-buttons-mobile">
          <button 
            @click="testLogin('google_user1')"
            class="test-btn-mobile"
            :disabled="loading"
          >
            <span class="test-badge google-badge">G</span>
            테스터1
          </button>
          
          <button 
            @click="testLogin('kakao_user2')"
            class="test-btn-mobile"
            :disabled="loading"
          >
            <span class="test-badge kakao-badge">K</span>
            테스터2
          </button>
          
          <button 
            @click="testLogin('naver_user3')"
            class="test-btn-mobile"
            :disabled="loading"
          >
            <span class="test-badge naver-badge">N</span>
            테스터3
          </button>
        </div>
      </div>

      <!-- 에러 메시지 -->
      <div v-if="error" class="error-mobile">
        {{ error }}
      </div>

      <!-- 푸터 -->
      <div class="footer-mobile">
        <p>🎮 더 재밌는 술자리 🎮</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
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
 * 테스트 로그인 처리 (모바일 API)
 */
const testLogin = async (username) => {
  loading.value = true
  error.value = ''

  try {
    const response = await axios.post('/api/mobile/auth/test-login', {
      username: username,
      password: 'test' // 테스트용 비밀번호
    })
    
    // Auth store를 통해 로그인 처리
    authStore.login(response.data.user, response.data.token)
    
    // 모바일 홈 페이지로 이동
    router.push('/mobile')
    
  } catch (err) {
    console.error('테스트 로그인 실패:', err)
    error.value = '테스트 로그인에 실패했습니다. 다시 시도해주세요.'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* 모바일 뷰 스타일 */
.login-mobile {
  background: #f5f5f5;
  min-height: 100vh;
}

.login-container-mobile {
  background: #ffffff;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header-mobile {
  background: linear-gradient(135deg, #ffd93d 0%, #ff6b6b 100%);
  padding: 24px 16px 32px 16px;
  text-align: center;
  color: #333;
}

.brand-mobile {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-bottom: 8px;
}

.brand-emoji {
  font-size: 28px;
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-10px);
  }
  60% {
    transform: translateY(-5px);
  }
}

.brand-mobile h1 {
  font-size: 24px;
  font-weight: 700;
  margin: 0;
  letter-spacing: -0.5px;
}

.welcome-text {
  font-size: 14px;
  margin: 0;
  opacity: 0.8;
}

.social-section-mobile {
  padding: 24px 16px;
  background: #ffffff;
}

.social-section-mobile h2 {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0 0 16px 0;
  text-align: center;
}

.social-buttons-mobile {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.social-btn-mobile {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 16px;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.social-btn-mobile:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.google-mobile {
  background: #4285f4;
  color: #ffffff;
}

.google-mobile:hover:not(:disabled) {
  background: #357ae8;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(66, 133, 244, 0.3);
}

.kakao-mobile {
  background: #fee500;
  color: #3c1e1e;
}

.kakao-mobile:hover:not(:disabled) {
  background: #fada0a;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(254, 229, 0, 0.3);
}

.naver-mobile {
  background: #03c75a;
  color: #ffffff;
}

.naver-mobile:hover:not(:disabled) {
  background: #02b34a;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(3, 199, 90, 0.3);
}

.social-icon-mobile {
  font-size: 20px;
}

.social-text-mobile {
  font-size: 16px;
}

.test-section-mobile {
  background: #f8f9fa;
  padding: 20px 16px;
  margin: 8px 16px;
  border-radius: 12px;
}

.test-header-mobile {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-bottom: 8px;
}

.test-emoji {
  font-size: 20px;
}

.test-header-mobile h3 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.test-desc-mobile {
  text-align: center;
  font-size: 13px;
  color: #666;
  margin: 0 0 16px 0;
}

.test-buttons-mobile {
  display: flex;
  gap: 8px;
}

.test-btn-mobile {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding: 12px 8px;
  border: 1px solid #e1e5e9;
  border-radius: 8px;
  background: #ffffff;
  cursor: pointer;
  transition: all 0.2s;
}

.test-btn-mobile:hover:not(:disabled) {
  border-color: #ffd93d;
  background: #fffef7;
  transform: translateY(-1px);
}

.test-btn-mobile:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.test-badge {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  font-weight: 700;
  color: #ffffff;
}

.google-badge {
  background: #4285f4;
}

.kakao-badge {
  background: #fee500;
  color: #3c1e1e;
}

.naver-badge {
  background: #03c75a;
}

.test-btn-mobile span:last-child {
  font-size: 12px;
  font-weight: 600;
  color: #333;
}

.error-mobile {
  background: rgba(255, 68, 68, 0.1);
  color: #d32f2f;
  padding: 12px 16px;
  margin: 16px;
  border-radius: 8px;
  font-size: 14px;
  border: 1px solid rgba(255, 68, 68, 0.2);
}

.footer-mobile {
  margin-top: auto;
  padding: 20px 16px;
  text-align: center;
  background: #f8f9fa;
  border-top: 1px solid #e1e5e9;
}

.footer-mobile p {
  font-size: 13px;
  color: #666;
  margin: 0;
}
</style>