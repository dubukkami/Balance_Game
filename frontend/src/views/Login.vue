<!--
  술하재밸 로그인 페이지
  OAuth 소셜 로그인 + 테스트 로그인 제공
-->
<template>
  <div class="login">
    <div class="container">
      <div class="login-card">
        <div class="login-header">
          <h1>
            <span class="login-emoji">🍻</span>
            술하재밸 로그인
          </h1>
          <p class="login-subtitle">소셜 로그인으로 간편하게 시작하세요!</p>
        </div>
        
        <!-- 소셜 로그인 버튼들 -->
        <div class="social-login">
          <button 
            @click="socialLogin('google')"
            class="social-btn google-btn"
            :disabled="loading"
          >
            <span class="social-icon">🔍</span>
            Google로 시작하기
          </button>
          
          <button 
            @click="socialLogin('kakao')"
            class="social-btn kakao-btn"
            :disabled="loading"
          >
            <span class="social-icon">💬</span>
            카카오로 시작하기
          </button>
          
          <button 
            @click="socialLogin('naver')"
            class="social-btn naver-btn"
            :disabled="loading"
          >
            <span class="social-icon">🟢</span>
            네이버로 시작하기
          </button>
        </div>

        <!-- 구분선 -->
        <div class="divider">
          <span>또는</span>
        </div>

        <!-- 테스트 로그인 -->
        <div class="test-login">
          <h3>🧪 테스트 로그인</h3>
          <p class="test-description">개발용 테스트 계정으로 빠르게 체험해보세요!</p>
          
          <div class="test-accounts">
            <button 
              @click="testLogin('google_user1')"
              class="test-btn"
              :disabled="loading"
            >
              <span class="test-icon">👤</span>
              테스터1 (구글)
            </button>
            
            <button 
              @click="testLogin('kakao_user2')"
              class="test-btn"
              :disabled="loading"
            >
              <span class="test-icon">💬</span>
              테스터2 (카카오)
            </button>
            
            <button 
              @click="testLogin('naver_user3')"
              class="test-btn"
              :disabled="loading"
            >
              <span class="test-icon">🟢</span>
              테스터3 (네이버)
            </button>
          </div>
        </div>

        <div v-if="error" class="error-message">
          {{ error }}
        </div>

        <div class="login-footer">
          <p>🎮 술자리가 더 재밌어지는 밸런스 게임! 🎮</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * 술하재밸 로그인 페이지 로직
 */
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
 * 테스트 로그인 처리
 */
const testLogin = async (username) => {
  loading.value = true
  error.value = ''

  try {
    const response = await axios.post('/api/auth/test-login', {
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
/* 블라인드 스타일 로그인 페이지 */
.login {
  min-height: 70vh;
  display: flex;
  align-items: center;
  padding: 2rem 0;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.container {
  max-width: 500px;
  margin: 0 auto;
  padding: 0 1rem;
}

.login-card {
  background: white;
  padding: 2.5rem;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.login-header h1 {
  margin: 0 0 1rem 0;
  color: #2c3e50;
  font-size: 2rem;
  font-weight: 700;
}

.login-emoji {
  font-size: 2.5rem;
  margin-right: 0.5rem;
  animation: bounce 2s infinite;
}

.login-subtitle {
  color: #7f8c8d;
  margin: 0 0 2rem 0;
  font-size: 1.1rem;
}

.social-login {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-bottom: 2rem;
}

.social-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.8rem;
  padding: 1rem 1.5rem;
  border: none;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  text-decoration: none;
}

.social-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.google-btn {
  background: #4285f4;
  color: white;
}

.google-btn:hover:not(:disabled) {
  background: #357ae8;
  transform: translateY(-2px);
}

.kakao-btn {
  background: #fee500;
  color: #3c1e1e;
}

.kakao-btn:hover:not(:disabled) {
  background: #fada0a;
  transform: translateY(-2px);
}

.naver-btn {
  background: #03c75a;
  color: white;
}

.naver-btn:hover:not(:disabled) {
  background: #02b34a;
  transform: translateY(-2px);
}

.social-icon {
  font-size: 1.3rem;
}

.divider {
  position: relative;
  margin: 2rem 0;
  text-align: center;
  color: #95a5a6;
}

.divider::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 1px;
  background: #ecf0f1;
}

.divider span {
  background: white;
  padding: 0 1rem;
  font-weight: 500;
}

.test-login {
  background: #f8f9fa;
  padding: 1.5rem;
  border-radius: 15px;
  margin-bottom: 1.5rem;
}

.test-login h3 {
  margin: 0 0 0.5rem 0;
  color: #2c3e50;
  font-size: 1.2rem;
}

.test-description {
  color: #7f8c8d;
  margin: 0 0 1.5rem 0;
  font-size: 0.9rem;
}

.test-accounts {
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
}

.test-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 0.8rem 1rem;
  border: 2px solid #e9ecef;
  border-radius: 10px;
  background: white;
  color: #495057;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.test-btn:hover:not(:disabled) {
  border-color: #667eea;
  background: #f8f9ff;
  transform: translateY(-1px);
}

.test-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.test-icon {
  font-size: 1.1rem;
}

.error-message {
  background: #ffe6e6;
  color: #d63384;
  padding: 1rem;
  border-radius: 10px;
  margin-bottom: 1rem;
  border: 1px solid #f5c6cb;
}

.login-footer {
  margin-top: 1.5rem;
  padding-top: 1.5rem;
  border-top: 1px solid #ecf0f1;
  color: #95a5a6;
  font-size: 0.9rem;
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

@media (max-width: 768px) {
  .login-card {
    margin: 0 1rem;
    padding: 2rem;
  }
  
  .login-header h1 {
    font-size: 1.7rem;
  }
  
  .login-emoji {
    font-size: 2rem;
  }
  
  .social-btn {
    padding: 0.9rem 1.2rem;
    font-size: 0.95rem;
  }
  
  .test-accounts {
    gap: 0.6rem;
  }
  
  .test-btn {
    padding: 0.7rem 0.8rem;
    font-size: 0.9rem;
  }
}
</style>