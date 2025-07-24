/**
 * Vue 3 애플리케이션 진입점
 * 라우터, 상태 관리, 전역 설정 등을 초기화
 */
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import './styles/global.css'
import axios from 'axios'

// API 기본 URL 설정
axios.defaults.baseURL = import.meta.env.VITE_API_URL || 'http://localhost:8080'

// 전역 에러 핸들러 - API 에러가 JavaScript 실행을 중단하지 않도록
axios.interceptors.response.use(
  response => response,
  error => {
    return Promise.reject(error)
  }
)

const app = createApp(App)

// Pinia 상태 관리 설정
app.use(createPinia())

// Vue Router 설정
app.use(router)

// 앱 마운트
app.mount('#app')