<!--
  OAuth2 로그인 리다이렉트 처리 컴포넌트
-->
<template>
  <div class="oauth-redirect">
    <div class="container">
      <div class="loading-card">
        <div class="loading-spinner">🍻</div>
        <h2>로그인 처리 중...</h2>
        <p>잠시만 기다려주세요!</p>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * OAuth2 리다이렉트 처리 로직
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
const handleOAuth2Redirect = () => {
  const urlParams = new URLSearchParams(window.location.search)
  const token = urlParams.get('token')
  const error = urlParams.get('error')
  
  if (error) {
    console.error('OAuth2 로그인 실패:', error)
    router.push('/login?error=oauth_failed')
    return
  }
  
  if (token) {
    // 토큰으로 사용자 정보 가져오기
    fetchUserInfo(token)
  } else {
    console.error('토큰이 없습니다.')
    router.push('/login?error=no_token')
  }
}

/**
 * 사용자 정보 가져오기
 */
const fetchUserInfo = async (token) => {
  try {
    const response = await fetch('/api/auth/me', {
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
.oauth-redirect {
  min-height: 70vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.container {
  max-width: 400px;
  margin: 0 auto;
  padding: 0 1rem;
}

.loading-card {
  background: white;
  padding: 3rem 2rem;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.loading-spinner {
  font-size: 4rem;
  margin-bottom: 1rem;
  animation: spin 2s linear infinite;
}

.loading-card h2 {
  margin: 0 0 1rem 0;
  color: #2c3e50;
}

.loading-card p {
  margin: 0;
  color: #7f8c8d;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>