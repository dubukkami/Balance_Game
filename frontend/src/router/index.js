/**
 * Vue Router 설정
 * 애플리케이션의 라우팅 규칙을 정의
 */
import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import GameList from '../views/GameList.vue'
import GameDetail from '../views/GameDetail.vue'
import CreateGame from '../views/CreateGame.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import OAuth2Redirect from '../views/OAuth2Redirect.vue'
import Profile from '../views/Profile.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: { title: '홈' }
  },
  {
    path: '/games',
    name: 'GameList',
    component: GameList,
    meta: { title: '게임 목록' }
  },
  {
    path: '/game/:id',
    name: 'GameDetail',
    component: GameDetail,
    meta: { title: '게임 상세' },
    props: true
  },
  {
    path: '/create',
    name: 'CreateGame',
    component: CreateGame,
    meta: { title: '게임 만들기' }
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { title: '로그인' }
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
    meta: { title: '회원가입' }
  },
  {
    path: '/oauth2/redirect',
    name: 'OAuth2Redirect',
    component: OAuth2Redirect,
    meta: { title: '로그인 처리중' }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: Profile,
    meta: { title: '마이페이지', requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 라우트 변경 시 페이지 제목 업데이트
router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 술하재밸` : '술하재밸'
  next()
})

export default router