import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import axios from 'axios'

export const useAuthStore = defineStore('auth', () => {
  // 상태
  const user = ref(null)
  const token = ref(null)
  
  // 계산된 속성
  const isLoggedIn = computed(() => !!user.value && !!token.value)
  
  // 초기화 (로컬 스토리지에서 복원)
  const initAuth = () => {
    const savedToken = localStorage.getItem('token')
    const savedUser = localStorage.getItem('user')
    
    if (savedToken && savedUser) {
      try {
        token.value = savedToken
        user.value = JSON.parse(savedUser)
        
        // axios 헤더에 토큰 설정
        axios.defaults.headers.common['Authorization'] = `Bearer ${savedToken}`
      } catch (error) {
        console.error('인증 정보 복원 실패:', error)
        logout()
      }
    }
  }
  
  // 로그인
  const login = (userData, userToken) => {
    user.value = userData
    token.value = userToken
    
    // 로컬 스토리지에 저장
    localStorage.setItem('token', userToken)
    localStorage.setItem('user', JSON.stringify(userData))
    
    // axios 헤더에 토큰 설정
    axios.defaults.headers.common['Authorization'] = `Bearer ${userToken}`
  }
  
  // 로그아웃
  const logout = () => {
    user.value = null
    token.value = null
    
    // 로컬 스토리지에서 제거
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    
    // axios 헤더에서 토큰 제거
    delete axios.defaults.headers.common['Authorization']
  }
  
  // 사용자 정보 업데이트
  const updateUser = (newUserData) => {
    user.value = { ...user.value, ...newUserData }
    localStorage.setItem('user', JSON.stringify(user.value))
  }
  
  return {
    user,
    token,
    isLoggedIn,
    initAuth,
    login,
    logout,
    updateUser
  }
})