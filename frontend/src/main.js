/**
 * Vue 3 애플리케이션 진입점
 * 라우터, 상태 관리, 전역 설정 등을 초기화
 */
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import './styles/global.css'

const app = createApp(App)

// Pinia 상태 관리 설정
app.use(createPinia())

// Vue Router 설정
app.use(router)

// 앱 마운트
app.mount('#app')