<!--
  OAuth2 로그인 리다이렉트 처리 컴포넌트 (모바일)
-->
<template>
  <div class="mobile-oauth-redirect">
    <div class="mobile-container">
      <div class="mobile-loading-card">
        <div class="mobile-loading-spinner">🍻</div>
        <h2>로그인 처리 중...</h2>
        <p>잠시만 기다려주세요!</p>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * OAuth2 리다이렉트 처리 로직 (모바일)
 */
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

onMounted(() => {
  handleOAuth2Redirect()
})

/**
 * OAuth2 리다이렉트 처리
 */
const handleOAuth2Redirect = async () => {
  try {
    // URL에서 토큰 추출
    const urlParams = new URLSearchParams(window.location.search)
    const token = urlParams.get('token')
    const error = urlParams.get('error')
    
    // 에러가 있는 경우
    if (error) {
      console.error('OAuth2 로그인 에러:', error)
      router.push('/login?error=' + error)
      return
    }
    
    // 토큰이 없는 경우
    if (!token) {
      console.error('토큰이 없습니다.')
      router.push('/login?error=no_token')
      return
    }
    
    // 토큰을 사용하여 사용자 정보 조회
    const response = await fetch('/api/users/me', {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    
    if (response.ok) {
      const userData = await response.json()
      
      // Auth store를 통해 로그인 처리
      authStore.login(userData, token)
      
      // 홈 페이지로 이동
      router.push('/')
    } else {
      throw new Error('사용자 정보 조회 실패')
    }
  } catch (error) {
    console.error('사용자 정보 조회 실패:', error)
    router.push('/login?error=user_info_failed')
  }
}
</script>

<style scoped>
.mobile-oauth-redirect {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 1rem;
}

.mobile-container {
  width: 100%;
  max-width: 320px;
}

.mobile-loading-card {
  background: white;
  padding: 2rem 1.5rem;
  border-radius: 16px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.mobile-loading-spinner {
  font-size: 3rem;
  margin-bottom: 1rem;
  animation: spin 2s linear infinite;
}

.mobile-loading-card h2 {
  margin: 0 0 0.8rem 0;
  color: #2c3e50;
  font-size: 1.3rem;
}

.mobile-loading-card p {
  margin: 0;
  color: #7f8c8d;
  font-size: 0.9rem;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>