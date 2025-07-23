<!--
  술하재밸 - 술 마실 때 하면 재밌는 밸런스 게임
-->
<template>
  <div id="app">
    <!-- 블라인드 스타일 헤더 -->
    <header class="header">
      <div class="container">
        <div class="header-left">
          <router-link to="/" class="brand-link">
            <div class="brand-logo">
              <span class="brand-emoji">🍻</span>
              <div class="brand-text">
                <span class="brand-name">술하재밸</span>
                <span class="brand-desc">술 마실 때 하면 재밌는 밸런스 게임</span>
              </div>
            </div>
          </router-link>
        </div>
        
        <div class="header-right">
          <nav class="nav-menu">
            <router-link to="/" class="nav-link">
              <i class="nav-icon">🏠</i>
              <span>홈</span>
            </router-link>
            <router-link to="/games" class="nav-link">
              <i class="nav-icon">⚖️</i>
              <span>밸런스</span>
            </router-link>
            <router-link to="/create" class="nav-link" v-if="authStore.isLoggedIn">
              <i class="nav-icon">✍️</i>
              <span>만들기</span>
            </router-link>
          </nav>
          
          <!-- 사용자 영역 -->
          <div class="user-area">
            <div v-if="authStore.isLoggedIn" class="user-profile">
              <router-link to="/profile" class="user-info">
                <img 
                  v-if="authStore.user?.profileImageUrl" 
                  :src="authStore.user.profileImageUrl" 
                  :alt="authStore.user.nickname"
                  class="user-avatar"
                />
                <div v-else class="user-avatar-default">
                  {{ authStore.user?.nickname?.charAt(0) || '?' }}
                </div>
                <div class="user-details">
                  <span class="user-nickname">{{ authStore.user?.nickname }}</span>
                  <span class="user-provider">{{ getProviderName(authStore.user?.provider) }}</span>
                </div>
              </router-link>
              <button @click="handleLogout" class="logout-btn">
                <i class="logout-icon">🚪</i>
                <span>로그아웃</span>
              </button>
            </div>
            
            <router-link to="/login" class="login-btn" v-else>
              <i class="login-icon">👤</i>
              <span>로그인</span>
            </router-link>
          </div>
        </div>
      </div>
    </header>

    <!-- 메인 컨텐츠 -->
    <main class="main-content">
      <router-view />
    </main>

    <!-- 푸터 -->
    <footer class="footer">
      <div class="container">
        <div class="footer-content">
          <div class="footer-logo">
            <span class="footer-emoji">🍻</span>
            <span class="footer-text">술하재밸</span>
          </div>
          <p class="footer-desc">술자리가 더 재밌어지는 밸런스 게임 커뮤니티</p>
          <p class="footer-copyright">&copy; 2024 술하재밸. All rights reserved.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
/**
 * 술하재밸 메인 애플리케이션 컴포넌트 로직
 */
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from './stores/auth'

const router = useRouter()
const authStore = useAuthStore()

onMounted(() => {
  console.log('술하재밸 시작! 🍻')
  // 인증 상태 초기화
  authStore.initAuth()
})

/**
 * 로그아웃 처리
 */
const handleLogout = () => {
  authStore.logout()
  router.push('/')
}

/**
 * Provider 이름 변환
 */
const getProviderName = (provider) => {
  const providerMap = {
    'GOOGLE': '구글',
    'KAKAO': '카카오',
    'NAVER': '네이버'
  }
  return providerMap[provider] || provider
}
</script>

<style scoped>
@import './styles/variables.css';
@import './styles/global.css';

/* 블라인드 스타일 헤더 */
.header {
  background: var(--bg-primary);
  border-bottom: 1px solid var(--border-light);
  padding: var(--space-4) 0;
  position: sticky;
  top: 0;
  z-index: var(--z-sticky);
  box-shadow: var(--shadow-sm);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 var(--space-4);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  flex: 1;
}

.brand-link {
  text-decoration: none;
  color: inherit;
}

.brand-logo {
  display: flex;
  align-items: center;
  gap: 0.8rem;
}

.brand-emoji {
  font-size: 1.8rem;
  animation: bounce 2s infinite;
}

.brand-text {
  display: flex;
  flex-direction: column;
  gap: 0.2rem;
}

.brand-name {
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  letter-spacing: -0.5px;
}

.brand-desc {
  font-size: var(--text-xs);
  color: var(--text-tertiary);
  font-weight: var(--font-normal);
  line-height: var(--leading-tight);
}

.header-right {
  display: flex;
  align-items: center;
  gap: var(--space-8);
}

.nav-menu {
  display: flex;
  gap: var(--space-6);
}

.nav-link {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  text-decoration: none;
  color: var(--text-primary);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  padding: var(--space-2) var(--space-3);
  border-radius: var(--radius-lg);
  transition: var(--transition-fast);
  position: relative;
}

.nav-link:hover,
.nav-link.router-link-active {
  color: var(--primary-color);
  background: var(--bg-tertiary);
}

.nav-link.router-link-active::after {
  content: '';
  position: absolute;
  bottom: calc(-1 * var(--space-4));
  left: 50%;
  transform: translateX(-50%);
  width: 20px;
  height: 2px;
  background: var(--primary-color);
}

.nav-icon {
  font-size: 1rem;
  font-style: normal;
}

.user-area {
  display: flex;
  align-items: center;
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 0.5rem 0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-2) var(--space-4);
  background: var(--bg-secondary);
  border-radius: var(--radius-full);
  border: 1px solid var(--border-light);
  text-decoration: none;
  color: inherit;
  transition: var(--transition-fast);
  cursor: pointer;
}

.user-info:hover {
  background: var(--bg-tertiary);
  border-color: var(--border-medium);
  transform: translateY(-1px);
  box-shadow: var(--shadow-sm);
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #fff;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.user-avatar-default {
  width: 32px;
  height: 32px;
  border-radius: var(--radius-full);
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
  color: var(--text-white);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: var(--font-semibold);
  font-size: var(--text-sm);
  box-shadow: var(--shadow-sm);
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 0.1rem;
}

.user-nickname {
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.user-provider {
  font-size: var(--text-xs);
  color: var(--text-tertiary);
  font-weight: var(--font-normal);
}

.logout-btn {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-2) var(--space-3);
  background: var(--bg-primary);
  border: 1px solid var(--error-color);
  border-radius: var(--radius-lg);
  color: var(--error-color);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  cursor: pointer;
  transition: var(--transition-fast);
}

.logout-btn:hover {
  background: var(--error-color);
  color: var(--text-white);
}

.logout-icon {
  font-size: 0.9rem;
  font-style: normal;
}

.login-btn {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-3) var(--space-5);
  background: var(--primary-color);
  color: var(--text-white);
  text-decoration: none;
  border-radius: var(--radius-lg);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  transition: var(--transition-fast);
}

.login-btn:hover {
  background: var(--primary-hover);
  transform: translateY(-1px);
}

.login-icon {
  font-size: 1rem;
  font-style: normal;
}

.main-content {
  min-height: calc(100vh - 120px);
  background: var(--bg-secondary);
  flex: 1;
}

.footer {
  background: var(--gray-900);
  color: var(--text-white);
  padding: var(--space-8) 0;
  border-top: 1px solid var(--gray-800);
}

.footer-content {
  text-align: center;
}

.footer-logo {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.footer-emoji {
  font-size: 1.5rem;
}

.footer-text {
  font-size: 1.2rem;
  font-weight: 600;
}

.footer-desc {
  color: var(--gray-400);
  margin: 0 0 var(--space-4) 0;
  font-size: var(--text-sm);
}

.footer-copyright {
  color: var(--gray-500);
  margin: 0;
  font-size: var(--text-xs);
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-3px);
  }
  60% {
    transform: translateY(-1px);
  }
}

@media (max-width: 768px) {
  .header {
    padding: 0.6rem 0;
  }
  
  .container {
    flex-direction: column;
    gap: 1rem;
    align-items: stretch;
  }
  
  .header-left {
    text-align: center;
  }
  
  .brand-name {
    font-size: 1.2rem;
  }
  
  .brand-desc {
    font-size: 0.7rem;
  }
  
  .header-right {
    flex-direction: column;
    gap: 1rem;
    align-items: center;
  }
  
  .nav-menu {
    justify-content: center;
    gap: 1rem;
  }
  
  .nav-link {
    font-size: 0.8rem;
    padding: 0.4rem 0.6rem;
  }
  
  .user-info {
    padding: 0.4rem 0.8rem;
  }
  
  .user-nickname {
    max-width: 60px;
    font-size: 0.8rem;
  }
  
  .user-avatar,
  .user-avatar-default {
    width: 28px;
    height: 28px;
  }
  
  .logout-btn {
    padding: 0.4rem 0.6rem;
    font-size: 0.8rem;
  }
  
  .login-btn {
    padding: 0.5rem 1rem;
    font-size: 0.85rem;
  }
}

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
</style>