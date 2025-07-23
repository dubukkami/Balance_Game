<!--
  회원가입 페이지 컴포넌트
  새로운 사용자 계정 생성 기능을 제공
-->
<template>
  <div class="register">
    <div class="container">
      <div class="register-form">
        <h1>회원가입</h1>
        
        <form @submit.prevent="register">
          <div class="form-group">
            <label for="username">사용자명 *</label>
            <input 
              id="username"
              v-model="form.username"
              type="text"
              class="form-control"
              placeholder="사용자명을 입력하세요"
              required
              @blur="checkUsername"
            />
            <div v-if="usernameError" class="field-error">
              {{ usernameError }}
            </div>
          </div>

          <div class="form-group">
            <label for="email">이메일 *</label>
            <input 
              id="email"
              v-model="form.email"
              type="email"
              class="form-control"
              placeholder="이메일을 입력하세요"
              required
              @blur="checkEmail"
            />
            <div v-if="emailError" class="field-error">
              {{ emailError }}
            </div>
          </div>

          <div class="form-group">
            <label for="password">비밀번호 *</label>
            <input 
              id="password"
              v-model="form.password"
              type="password"
              class="form-control"
              placeholder="비밀번호를 입력하세요"
              required
              @input="validatePassword"
            />
            <div v-if="passwordError" class="field-error">
              {{ passwordError }}
            </div>
          </div>

          <div class="form-group">
            <label for="confirmPassword">비밀번호 확인 *</label>
            <input 
              id="confirmPassword"
              v-model="form.confirmPassword"
              type="password"
              class="form-control"
              placeholder="비밀번호를 다시 입력하세요"
              required
              @input="validateConfirmPassword"
            />
            <div v-if="confirmPasswordError" class="field-error">
              {{ confirmPasswordError }}
            </div>
          </div>

          <div class="form-group">
            <label for="nickname">닉네임</label>
            <input 
              id="nickname"
              v-model="form.nickname"
              type="text"
              class="form-control"
              placeholder="닉네임을 입력하세요 (선택사항)"
            />
          </div>

          <div class="form-group">
            <button 
              type="submit"
              class="btn btn-primary btn-block"
              :disabled="loading || !isFormValid"
            >
              {{ loading ? '가입 중...' : '회원가입' }}
            </button>
          </div>

          <div v-if="error" class="error-message">
            {{ error }}
          </div>
        </form>

        <div class="register-footer">
          <p>
            이미 회원이신가요?
            <router-link to="/login" class="login-link">
              로그인
            </router-link>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * 회원가입 페이지 컴포넌트 로직
 */
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()

// 반응형 데이터
const form = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  nickname: ''
})

const loading = ref(false)
const error = ref('')
const usernameError = ref('')
const emailError = ref('')
const passwordError = ref('')
const confirmPasswordError = ref('')

/**
 * 폼 유효성 검사
 */
const isFormValid = computed(() => {
  return form.username && 
         form.email && 
         form.password && 
         form.confirmPassword &&
         !usernameError.value &&
         !emailError.value &&
         !passwordError.value &&
         !confirmPasswordError.value
})

/**
 * 사용자명 중복 확인
 */
const checkUsername = async () => {
  if (!form.username) {
    usernameError.value = ''
    return
  }

  try {
    const response = await axios.get(`/api/users/check-username/${form.username}`)
    if (response.data) {
      usernameError.value = '이미 사용 중인 사용자명입니다.'
    } else {
      usernameError.value = ''
    }
  } catch (error) {
    console.error('사용자명 확인 오류:', error)
    usernameError.value = '사용자명 확인 중 오류가 발생했습니다.'
  }
}

/**
 * 이메일 중복 확인
 */
const checkEmail = async () => {
  if (!form.email) {
    emailError.value = ''
    return
  }

  try {
    const response = await axios.get(`/api/users/check-email/${form.email}`)
    if (response.data) {
      emailError.value = '이미 사용 중인 이메일입니다.'
    } else {
      emailError.value = ''
    }
  } catch (error) {
    console.error('이메일 확인 오류:', error)
    emailError.value = '이메일 확인 중 오류가 발생했습니다.'
  }
}

/**
 * 비밀번호 유효성 검사
 */
const validatePassword = () => {
  if (!form.password) {
    passwordError.value = ''
    return
  }

  if (form.password.length < 6) {
    passwordError.value = '비밀번호는 6자 이상이어야 합니다.'
  } else {
    passwordError.value = ''
  }
  
  // 비밀번호 변경 시 확인 비밀번호도 재검사
  if (form.confirmPassword) {
    validateConfirmPassword()
  }
}

/**
 * 비밀번호 확인 유효성 검사
 */
const validateConfirmPassword = () => {
  if (!form.confirmPassword) {
    confirmPasswordError.value = ''
    return
  }

  if (form.password !== form.confirmPassword) {
    confirmPasswordError.value = '비밀번호가 일치하지 않습니다.'
  } else {
    confirmPasswordError.value = ''
  }
}

/**
 * 회원가입 처리
 */
const register = async () => {
  loading.value = true
  error.value = ''

  try {
    const registerData = {
      username: form.username,
      email: form.email,
      password: form.password,
      nickname: form.nickname || form.username
    }

    const response = await axios.post('/api/users/register', registerData)
    
    // 회원가입 성공 시 자동 로그인
    localStorage.setItem('user', JSON.stringify(response.data))
    
    // 홈 페이지로 이동
    router.push('/')
    
  } catch (err) {
    console.error('회원가입 실패:', err)
    
    if (err.response?.status === 400) {
      error.value = '입력한 정보를 다시 확인해주세요.'
    } else {
      error.value = '회원가입 중 오류가 발생했습니다. 다시 시도해주세요.'
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register {
  min-height: 60vh;
  display: flex;
  align-items: center;
  padding: 2rem 0;
}

.register-form {
  max-width: 500px;
  margin: 0 auto;
  background: white;
  padding: 2rem;
  border-radius: 10px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.register-form h1 {
  text-align: center;
  margin-bottom: 2rem;
  color: #333;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: bold;
  color: #333;
}

.btn-block {
  width: 100%;
}

.error-message {
  background: #f8d7da;
  color: #721c24;
  padding: 0.75rem;
  border-radius: 4px;
  margin-top: 1rem;
  text-align: center;
}

.field-error {
  color: #dc3545;
  font-size: 0.875rem;
  margin-top: 0.25rem;
}

.register-footer {
  margin-top: 2rem;
  text-align: center;
  color: #666;
}

.login-link {
  color: #667eea;
  text-decoration: none;
  font-weight: bold;
}

.login-link:hover {
  text-decoration: underline;
}

@media (max-width: 768px) {
  .register-form {
    margin: 0 1rem;
  }
}
</style>