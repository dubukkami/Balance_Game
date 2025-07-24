/**
 * Vue Router 설정
 * 웹/모바일 완전 분리된 라우팅 규칙을 정의
 */
import { createRouter, createWebHistory } from 'vue-router'

// 웹 전용 컴포넌트
import Home from '../views/Home.vue'
import GameList from '../views/GameList.vue'
import CreateGame from '../views/CreateGame.vue'
import Login from '../views/Login.vue'

// 모바일 전용 컴포넌트
import HomeMobile from '../views/HomeMobile.vue'
import GameListMobile from '../views/GameListMobile.vue'
import CreateGameMobile from '../views/CreateGameMobile.vue'
import LoginMobile from '../views/LoginMobile.vue'

// 공통 컴포넌트
import GameDetail from '../views/GameDetail.vue'
import Register from '../views/Register.vue'
import OAuth2Redirect from '../views/OAuth2Redirect.vue'
import Profile from '../views/Profile.vue'

const routes = [
  // 🌐 웹 전용 라우트 (데스크톱/태블릿)
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: { title: '홈', platform: 'web' }
  },
  {
    path: '/games',
    name: 'GameList',
    component: GameList,
    meta: { title: '게임 목록', platform: 'web' }
  },
  {
    path: '/create',
    name: 'CreateGame',
    component: CreateGame,
    meta: { title: '게임 만들기', platform: 'web' }
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { title: '로그인', platform: 'web' }
  },

  // 📱 모바일 전용 라우트
  {
    path: '/mobile',
    name: 'HomeMobile',
    component: HomeMobile,
    meta: { title: '술하재밸', platform: 'mobile' }
  },
  {
    path: '/mobile/games',
    name: 'GameListMobile',
    component: GameListMobile,
    meta: { title: '게임 목록', platform: 'mobile' }
  },
  {
    path: '/mobile/create',
    name: 'CreateGameMobile',
    component: CreateGameMobile,
    meta: { title: '게임 만들기', platform: 'mobile' }
  },
  {
    path: '/mobile/login',
    name: 'LoginMobile',
    component: LoginMobile,
    meta: { title: '로그인', platform: 'mobile' }
  },

  // 🔄 공통 라우트 (웹/모바일 공용)
  {
    path: '/game/:id',
    name: 'GameDetail',
    component: GameDetail,
    meta: { title: '게임 상세', platform: 'common' },
    props: true
  },
  {
    path: '/mobile/game/:id',
    name: 'GameDetailMobile',
    component: GameDetail,
    meta: { title: '게임 상세', platform: 'mobile' },
    props: true
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
    meta: { title: '회원가입', platform: 'common' }
  },
  {
    path: '/oauth2/redirect',
    name: 'OAuth2Redirect',
    component: OAuth2Redirect,
    meta: { title: '로그인 처리중', platform: 'common' }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: Profile,
    meta: { title: '마이페이지', requiresAuth: true, platform: 'common' }
  },
  {
    path: '/mobile/profile',
    name: 'ProfileMobile',
    component: Profile,
    meta: { title: '마이페이지', requiresAuth: true, platform: 'mobile' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 모바일 기기 감지 함수
const isMobileDevice = () => {
  const userAgent = navigator.userAgent.toLowerCase()
  
  // 터치 기능과 화면 크기를 함께 고려
  const isTouchDevice = 'ontouchstart' in window || navigator.maxTouchPoints > 0
  const isSmallScreen = window.innerWidth <= 768
  
  // User-Agent 기반 감지 (더 포괄적인 패턴)
  const mobilePattern = /android|webos|iphone|ipad|ipod|blackberry|iemobile|opera mini|mobile|phone|tablet/i
  const isMobileUA = mobilePattern.test(userAgent)
  
  // Safari on iOS (최신 iOS는 User-Agent에 iPhone이 없을 수 있음)
  const isIOSSafari = /safari/i.test(userAgent) && /mobile/i.test(userAgent)
  
  // Chrome on Android
  const isAndroidChrome = /android/i.test(userAgent) && /chrome/i.test(userAgent)
  
  console.log('모바일 감지:', {
    userAgent,
    isTouchDevice,
    isSmallScreen,
    isMobileUA,
    isIOSSafari,
    isAndroidChrome,
    result: isMobileUA || isIOSSafari || isAndroidChrome || (isTouchDevice && isSmallScreen)
  })
  
  return isMobileUA || isIOSSafari || isAndroidChrome || (isTouchDevice && isSmallScreen)
}

// 라우트 변경 시 모바일 리다이렉트 및 페이지 제목 업데이트
router.beforeEach((to, from, next) => {
  const platform = to.meta.platform
  const title = to.meta.title
  
  // 무한 리다이렉트 방지: from과 to가 같으면 리다이렉트 중단
  if (from.path === to.path) {
    return next()
  }
  
  // 모바일 기기에서 웹 경로로 접속하면 모바일 경로로 리다이렉트
  if (isMobileDevice() && platform === 'web') {
    const mobileRoutes = {
      '/': '/mobile',
      '/games': '/mobile/games', 
      '/create': '/mobile/create',
      '/login': '/mobile/login'
    }
    
    const mobilePath = mobileRoutes[to.path]
    if (mobilePath && from.path !== mobilePath) {
      return next(mobilePath)
    }
  }
  
  // 데스크톱에서 모바일 경로로 접속하면 웹 경로로 리다이렉트 (선택사항)
  if (!isMobileDevice() && platform === 'mobile') {
    const webRoutes = {
      '/mobile': '/',
      '/mobile/games': '/games',
      '/mobile/create': '/create', 
      '/mobile/login': '/login'
    }
    
    const webPath = webRoutes[to.path]
    if (webPath && from.path !== webPath) {
      return next(webPath)
    }
  }
  
  // 페이지 제목 설정
  if (platform === 'mobile') {
    document.title = title ? `${title} - 술하재밸` : '술하재밸'
  } else {
    document.title = title ? `${title} - 밸런스 게임 커뮤니티` : '밸런스 게임 커뮤니티'
  }
  
  next()
})

export default router