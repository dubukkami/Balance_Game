<!--
  밸런스 게임 커뮤니티 - 웹/모바일 플랫폼별 레이아웃
-->
<template>
  <div id="app">
    <!-- 웹 헤더 (데스크톱/태블릿) -->
    <header v-if="isWebPlatform" class="web-header">
      <div class="web-header-content">
        <div class="web-brand">
          <router-link to="/" class="brand-link">
            <span class="brand-icon">🍻</span>
            <span class="brand-title">술하재밸</span>
          </router-link>
        </div>
        
        <nav class="web-nav">
          <router-link to="/" class="nav-item">홈</router-link>
          <router-link to="/games" class="nav-item">게임 목록</router-link>
          <router-link to="/create" class="nav-item" v-if="authStore.isLoggedIn">게임 만들기</router-link>
        </nav>
        
        <div class="web-user-section">
          <div v-if="authStore.isLoggedIn" class="user-profile-web">
            <router-link to="/profile" class="user-link-web">
              <img 
                v-if="authStore.user?.profileImageUrl" 
                :src="authStore.user.profileImageUrl" 
                :alt="authStore.user.nickname"
                class="user-avatar-web"
              />
              <div v-else class="user-avatar-default-web">
                {{ authStore.user?.nickname?.charAt(0) || '?' }}
              </div>
              <span class="user-name">{{ authStore.user?.nickname }}</span>
            </router-link>
            <button @click="handleLogout" class="logout-btn-web">
              로그아웃
            </button>
          </div>
          
          <router-link to="/login" class="login-btn-web" v-else>
            로그인
          </router-link>
        </div>
      </div>
    </header>

    <!-- 모바일 헤더 -->
    <header v-if="isMobilePlatform" class="mobile-header">
      <div class="header-content">
        <div class="brand-section">
          <span class="brand-emoji">🍻</span>
          <span class="brand-name">술하재밸</span>
        </div>
        
        <!-- 로그인/프로필 영역 -->
        <div class="user-section">
          <div v-if="authStore.isLoggedIn" class="user-profile-mobile">
            <router-link :to="getMobileProfilePath()" class="user-link">
              <img 
                v-if="authStore.user?.profileImageUrl" 
                :src="authStore.user.profileImageUrl" 
                :alt="authStore.user.nickname"
                class="user-avatar-mobile"
              />
              <div v-else class="user-avatar-default-mobile">
                {{ authStore.user?.nickname?.charAt(0) || '?' }}
              </div>
            </router-link>
            <button @click="handleLogout" class="logout-btn-mobile">
              <span>🚪</span>
            </button>
          </div>
          
          <router-link to="/mobile/login" class="login-btn-mobile" v-else>
            <span>👤</span>
          </router-link>
        </div>
      </div>
    </header>

    <!-- 메인 컨텐츠 -->
    <main :class="['main-content', { 'web-content': isWebPlatform, 'mobile-content': isMobilePlatform }]">
      <router-view />
    </main>

    <!-- 하단 탭 네비게이션 (모바일 전용) -->
    <nav v-if="isMobilePlatform" class="bottom-nav">
      <router-link to="/mobile" class="tab-item">
        <div class="tab-icon">🏠</div>
        <span class="tab-label">홈</span>
      </router-link>
      
      <router-link to="/mobile/games" class="tab-item">
        <div class="tab-icon">⚖️</div>
        <span class="tab-label">밸런스</span>
      </router-link>
      
      <router-link to="/mobile/create" class="tab-item" v-if="authStore.isLoggedIn">
        <div class="tab-icon">✍️</div>
        <span class="tab-label">만들기</span>
      </router-link>
      
      <router-link to="/mobile/profile" class="tab-item" v-if="authStore.isLoggedIn">
        <div class="tab-icon">👤</div>
        <span class="tab-label">내정보</span>
      </router-link>
      
      <router-link to="/mobile/login" class="tab-item" v-else>
        <div class="tab-icon">👤</div>
        <span class="tab-label">로그인</span>
      </router-link>
    </nav>
  </div>
</template>

<script setup>
/**
 * 밸런스 게임 커뮤니티 메인 애플리케이션 컴포넌트 로직
 * 웹/모바일 플랫폼별 레이아웃 분리
 */
import { onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from './stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

/**
 * 플랫폼 감지
 */
const isWebPlatform = computed(() => {
  return route.meta?.platform === 'web' || route.meta?.platform === 'common'
})

const isMobilePlatform = computed(() => {
  return route.meta?.platform === 'mobile'
})

/**
 * 모바일 프로필 경로 반환
 */
const getMobileProfilePath = () => {
  return '/mobile/profile'
}

onMounted(() => {
  console.log('밸런스 게임 커뮤니티 시작! ⚖️')
  // 인증 상태 초기화
  authStore.initAuth()
  
  // Railway Sleep 방지를 위한 Keep-Alive
  startKeepAlive()
})

/**
 * Railway Sleep 방지를 위한 Keep-Alive 설정
 * 5분마다 백엔드에 ping 요청을 보내서 서버가 잠들지 않도록 함
 */
const startKeepAlive = () => {
  // 개발 환경에서는 실행하지 않음
  if (import.meta.env.DEV) {
    console.log('개발 환경: Keep-Alive 비활성화')
    return
  }
  
  const keepAliveInterval = 5 * 60 * 1000 // 5분
  
  const pingServer = async () => {
    try {
      const response = await fetch('/api/heartbeat', {
        method: 'GET',
        headers: {
          'Cache-Control': 'no-cache'
        }
      })
      
      if (response.ok) {
        console.log('✅ Keep-Alive: 서버 연결 유지됨')
      } else {
        console.warn('⚠️ Keep-Alive: 서버 응답 이상', response.status)
      }
    } catch (error) {
      console.warn('⚠️ Keep-Alive: 서버 연결 실패', error.message)
    }
  }
  
  // 초기 ping (30초 후)
  setTimeout(pingServer, 30000)
  
  // 정기적 ping (5분마다)
  setInterval(pingServer, keepAliveInterval)
  
  console.log('🔄 Keep-Alive 활성화: 5분마다 서버 ping')
}

/**
 * 로그아웃 처리
 */
const handleLogout = () => {
  authStore.logout()
  
  // 플랫폼에 따라 다른 홈으로 이동
  if (isMobilePlatform.value) {
    router.push('/mobile')
  } else {
    router.push('/')
  }
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

/* 앱 전체 레이아웃 */
#app {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: #f5f5f5;
}

/* ===========================================
   웹 헤더 스타일 (데스크톱/태블릿)
   =========================================== */
.web-header {
  background: #ffffff;
  border-bottom: 1px solid #e1e5e9;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.web-header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  height: 64px;
}

.web-brand .brand-link {
  display: flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
  color: #333;
}

.brand-icon {
  font-size: 24px;
}

.brand-title {
  font-size: 20px;
  font-weight: 700;
  color: #333;
}

.web-nav {
  display: flex;
  gap: 32px;
}

.web-nav .nav-item {
  color: #666;
  text-decoration: none;
  font-weight: 500;
  padding: 8px 16px;
  border-radius: 8px;
  transition: all 0.2s;
}

.web-nav .nav-item:hover {
  background: #f8f9fa;
  color: #333;
}

.web-nav .nav-item.router-link-active {
  background: var(--primary-color, #007bff);
  color: white;
}

.web-user-section {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-profile-web {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-link-web {
  display: flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
  color: #333;
  padding: 6px 12px;
  border-radius: 8px;
  transition: background 0.2s;
}

.user-link-web:hover {
  background: #f8f9fa;
}

.user-avatar-web {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
}

.user-avatar-default-web {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, #007bff, #0056b3);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 14px;
}

.user-name {
  font-weight: 500;
  color: #333;
}

.logout-btn-web,
.login-btn-web {
  padding: 8px 16px;
  border: 1px solid #e1e5e9;
  background: #ffffff;
  color: #666;
  text-decoration: none;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.logout-btn-web:hover,
.login-btn-web:hover {
  background: #f8f9fa;
  border-color: #d1d5db;
}

.login-btn-web {
  background: var(--primary-color, #007bff);
  color: white;
  border-color: var(--primary-color, #007bff);
}

.login-btn-web:hover {
  background: var(--secondary-color, #0056b3);
  border-color: var(--secondary-color, #0056b3);
}

/* 웹 메인 컨텐츠 */
.web-content {
  flex: 1;
  min-height: calc(100vh - 64px);
}

/* ===========================================
   모바일 헤더/네비게이션 스타일
   =========================================== */
.mobile-header {
  background: #ffffff;
  border-bottom: 1px solid #e1e5e9;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  max-width: 480px;
  margin: 0 auto;
}

.brand-section {
  display: flex;
  align-items: center;
  gap: 8px;
}

.brand-emoji {
  font-size: 20px;
}

.brand-name {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  letter-spacing: -0.3px;
}

/* 사용자 섹션 */
.user-section {
  display: flex;
  align-items: center;
}

.user-profile-mobile {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-link {
  text-decoration: none;
}

.user-avatar-mobile {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #fff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.2);
}

.user-avatar-default-mobile {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, #ff6b6b, #4ecdc4);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 14px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.2);
}

.logout-btn-mobile {
  background: none;
  border: none;
  padding: 4px;
  cursor: pointer;
  font-size: 16px;
  opacity: 0.7;
  transition: opacity 0.2s;
}

.logout-btn-mobile:hover {
  opacity: 1;
}

.login-btn-mobile {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  background: #ffd93d;
  border-radius: 50%;
  text-decoration: none;
  font-size: 16px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s;
}

.login-btn-mobile:hover {
  transform: scale(1.05);
}

/* 메인 컨텐츠 - 공통 */
.main-content {
  flex: 1;
}

/* 모바일 메인 컨텐츠 */
.mobile-content {
  padding-bottom: 70px; /* 하단 탭 공간 확보 */
  min-height: calc(100vh - 140px);
}

/* 하단 탭 네비게이션 (카카오톡 스타일) */
.bottom-nav {
  position: fixed;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 100%;
  max-width: 480px;
  background: #ffffff;
  border-top: 1px solid #e1e5e9;
  display: flex;
  justify-content: space-around;
  align-items: center;
  padding: 8px 0 12px 0;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.1);
  z-index: 100;
}

.tab-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  text-decoration: none;
  color: #999;
  padding: 4px 8px;
  border-radius: 8px;
  transition: all 0.2s ease;
  min-width: 48px;
}

.tab-item:hover {
  background: #f8f9fa;
  transform: translateY(-1px);
}

.tab-item.router-link-active {
  color: #ffd93d;
}

.tab-item.router-link-active .tab-icon {
  transform: scale(1.1);
}

.tab-icon {
  font-size: 20px;
  font-style: normal;
  transition: transform 0.2s ease;
}

.tab-label {
  font-size: 10px;
  font-weight: 500;
  line-height: 1;
  letter-spacing: -0.2px;
}

/* 반응형 디자인 */
@media (max-width: 480px) {
  .header-content {
    padding: 10px 12px;
  }
  
  .brand-name {
    font-size: 16px;
  }
  
  .brand-emoji {
    font-size: 18px;
  }
  
  .user-avatar-mobile,
  .user-avatar-default-mobile {
    width: 28px;
    height: 28px;
  }
  
  .login-btn-mobile {
    width: 32px;
    height: 32px;
    font-size: 14px;
  }
  
  .bottom-nav {
    padding: 6px 0 10px 0;
  }
  
  .tab-icon {
    font-size: 18px;
  }
  
  .tab-label {
    font-size: 9px;
  }
  
  .main-content {
    padding-bottom: 65px;
  }
}

/* 모바일 전용 스타일 (모바일 플랫폼에서만 적용) */
@media (min-width: 481px) {
  .mobile-header,
  .bottom-nav {
    max-width: 480px;
    margin: 0 auto;
  }
  
  .mobile-content {
    max-width: 480px;
    margin: 0 auto;
    box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
  }
}

/* 모바일 플랫폼에서만 적용되는 큰 화면 스타일 */
@media (min-width: 768px) {
  .mobile-content {
    background: #f5f5f5;
    margin-top: 20px;
    margin-bottom: 20px;
    border-radius: 12px;
    overflow: hidden;
    padding-bottom: 0;
  }
  
  .mobile-header {
    border-radius: 12px 12px 0 0;
  }
  
  .bottom-nav {
    border-radius: 0 0 12px 12px;
    position: relative;
    bottom: auto;
    transform: none;
    margin: 0 auto;
  }
}</style>