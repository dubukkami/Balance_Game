<!--
  마이페이지 컴포넌트
  사용자 프로필 정보 관리
-->
<template>
  <div class="profile">
    <div class="container">
      <UiCard class="profile-card" variant="elevated">
        <template #header>
          <h1>마이페이지</h1>
        </template>

        <div class="profile-content">
          <!-- 프로필 사진 섹션 -->
          <div class="profile-avatar-section">
            <div class="avatar-container">
              <img 
                v-if="form.profileImageUrl" 
                :src="form.profileImageUrl" 
                :alt="form.nickname"
                class="profile-avatar"
              />
              <div v-else class="profile-avatar-default">
                {{ form.nickname?.charAt(0) || '?' }}
              </div>
              
              <button class="avatar-edit-btn" @click="triggerFileInput">
                <i class="icon">📷</i>
                <span>변경</span>
              </button>
              
              <input 
                ref="fileInput"
                type="file"
                accept="image/*"
                @change="handleFileChange"
                class="file-input"
              />
            </div>
            
            <div class="avatar-info">
              <h3>{{ form.nickname || '사용자' }}</h3>
              <p class="provider-info">{{ getProviderName(user?.provider) }} 로그인</p>
            </div>
          </div>

          <!-- 프로필 정보 폼 -->
          <form @submit.prevent="updateProfile" class="profile-form">
            <div class="form-section">
              <h3>기본 정보</h3>
              
              <div class="form-group">
                <UiInput
                  v-model="form.nickname"
                  label="닉네임"
                  placeholder="사용할 닉네임을 입력하세요"
                  :maxlength="20"
                  show-count
                  required
                />
              </div>

              <div class="form-group">
                <UiInput
                  v-model="form.bio"
                  type="textarea"
                  label="자기소개"
                  placeholder="간단한 자기소개를 입력하세요"
                  :rows="4"
                  :maxlength="200"
                  show-count
                />
              </div>

              <div class="form-group">
                <UiInput
                  v-model="form.location"
                  label="지역"
                  placeholder="거주 지역을 입력하세요"
                  :maxlength="50"
                />
              </div>

              <div class="form-group">
                <UiInput
                  v-model="form.website"
                  label="웹사이트"
                  placeholder="개인 웹사이트나 블로그 주소"
                  :maxlength="100"
                />
              </div>
            </div>

            <div class="form-section">
              <h3>계정 정보</h3>
              
              <div class="readonly-info">
                <div class="info-item">
                  <label>이메일</label>
                  <span>{{ user?.email || '이메일 없음' }}</span>
                </div>
                
                <div class="info-item">
                  <label>가입일</label>
                  <span>{{ formatDate(user?.createdAt) }}</span>
                </div>
                
                <div class="info-item">
                  <label>로그인 방식</label>
                  <span>{{ getProviderName(user?.provider) }}</span>
                </div>
              </div>
            </div>

            <!-- 활동 통계 -->
            <div class="form-section">
              <h3>활동 통계</h3>
              
              <div class="stats-grid">
                <div class="stat-item">
                  <div class="stat-number">{{ userStats.gamesCreated }}</div>
                  <div class="stat-label">만든 게임</div>
                </div>
                
                <div class="stat-item">
                  <div class="stat-number">{{ userStats.totalVotes }}</div>
                  <div class="stat-label">총 투표</div>
                </div>
                
                <div class="stat-item">
                  <div class="stat-number">{{ userStats.commentsCount }}</div>
                  <div class="stat-label">댓글 수</div>
                </div>
                
                <div class="stat-item">
                  <div class="stat-number">{{ userStats.likesReceived }}</div>
                  <div class="stat-label">받은 좋아요</div>
                </div>
              </div>
            </div>

            <div class="form-actions">
              <UiButton 
                variant="secondary" 
                @click="resetForm"
                type="button"
              >
                취소
              </UiButton>
              
              <UiButton 
                variant="primary"
                type="submit"
                :loading="loading"
                loading-text="저장 중..."
              >
                저장하기
              </UiButton>
            </div>
          </form>

          <!-- 에러/성공 메시지 -->
          <div v-if="message" class="message" :class="messageType">
            {{ message }}
          </div>
        </div>
      </UiCard>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { Card as UiCard, Button as UiButton, Input as UiInput } from '../components/ui'

const router = useRouter()

// 반응형 데이터
const fileInput = ref(null)
const loading = ref(false)
const message = ref('')
const messageType = ref('success')

const form = reactive({
  nickname: '',
  bio: '',
  location: '',
  website: '',
  profileImageUrl: ''
})

const userStats = reactive({
  gamesCreated: 0,
  totalVotes: 0,
  commentsCount: 0,
  likesReceived: 0
})

/**
 * 현재 사용자 정보
 */
const user = computed(() => {
  try {
    return JSON.parse(localStorage.getItem('user') || '{}')
  } catch {
    return {}
  }
})

/**
 * 로그인 상태 확인
 */
const isLoggedIn = computed(() => {
  return user.value && user.value.id
})

/**
 * Provider 이름 변환
 */
const getProviderName = (provider) => {
  const providerMap = {
    'GOOGLE': '구글',
    'KAKAO': '카카오',
    'NAVER': '네이버'
  }
  return providerMap[provider] || provider || '알 수 없음'
}

/**
 * 날짜 포맷팅
 */
const formatDate = (dateString) => {
  if (!dateString) return '정보 없음'
  return new Date(dateString).toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

/**
 * 파일 입력 트리거
 */
const triggerFileInput = () => {
  fileInput.value?.click()
}

/**
 * 파일 변경 처리
 */
const handleFileChange = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  // 파일 크기 체크 (5MB)
  if (file.size > 5 * 1024 * 1024) {
    showMessage('파일 크기는 5MB 이하여야 합니다.', 'error')
    return
  }

  // 이미지 파일 체크
  if (!file.type.startsWith('image/')) {
    showMessage('이미지 파일만 업로드할 수 있습니다.', 'error')
    return
  }

  try {
    // 프리뷰를 위해 임시로 표시
    const reader = new FileReader()
    reader.onload = (e) => {
      form.profileImageUrl = e.target.result
    }
    reader.readAsDataURL(file)

    // 실제 업로드는 추후 구현 (현재는 로컬 프리뷰만)
    showMessage('프로필 사진이 변경되었습니다. 저장 버튼을 눌러 완료하세요.', 'success')
  } catch (error) {
    console.error('파일 처리 실패:', error)
    showMessage('파일 처리 중 오류가 발생했습니다.', 'error')
  }
}

/**
 * 사용자 정보 조회
 */
const fetchUserProfile = async () => {
  try {
    // 사용자 기본 정보 설정
    form.nickname = user.value.nickname || ''
    form.profileImageUrl = user.value.profileImageUrl || ''
    
    // 추가 프로필 정보는 API가 준비되면 조회
    // const response = await axios.get(`/api/users/${user.value.id}/profile`)
    // Object.assign(form, response.data)
    
  } catch (error) {
    console.error('사용자 정보 조회 실패:', error)
    showMessage('사용자 정보를 불러오는데 실패했습니다.', 'error')
  }
}

/**
 * 사용자 통계 조회
 */
const fetchUserStats = async () => {
  try {
    // 사용자가 만든 게임 수
    const gamesResponse = await axios.get('/api/balance-games', {
      params: { authorId: user.value.id }
    })
    userStats.gamesCreated = gamesResponse.data.totalElements || 0

    // 기타 통계는 API가 준비되면 조회
    userStats.totalVotes = Math.floor(Math.random() * 100) // 임시 데이터
    userStats.commentsCount = Math.floor(Math.random() * 50) // 임시 데이터
    userStats.likesReceived = Math.floor(Math.random() * 200) // 임시 데이터
    
  } catch (error) {
    console.error('사용자 통계 조회 실패:', error)
  }
}

/**
 * 프로필 업데이트
 */
const updateProfile = async () => {
  loading.value = true
  
  try {
    // 현재는 로컬 스토리지만 업데이트 (실제 API는 추후 구현)
    const updatedUser = {
      ...user.value,
      nickname: form.nickname,
      profileImageUrl: form.profileImageUrl
    }
    
    localStorage.setItem('user', JSON.stringify(updatedUser))
    
    // 실제 API 호출 (추후 구현)
    // await axios.put(`/api/users/${user.value.id}`, {
    //   nickname: form.nickname,
    //   bio: form.bio,
    //   location: form.location,
    //   website: form.website,
    //   profileImageUrl: form.profileImageUrl
    // })
    
    showMessage('프로필이 성공적으로 업데이트되었습니다!', 'success')
    
  } catch (error) {
    console.error('프로필 업데이트 실패:', error)
    showMessage('프로필 업데이트 중 오류가 발생했습니다.', 'error')
  } finally {
    loading.value = false
  }
}

/**
 * 폼 리셋
 */
const resetForm = () => {
  fetchUserProfile()
  showMessage('변경사항이 취소되었습니다.', 'success')
}

/**
 * 메시지 표시
 */
const showMessage = (text, type = 'success') => {
  message.value = text
  messageType.value = type
  
  setTimeout(() => {
    message.value = ''
  }, 3000)
}

// 컴포넌트 마운트 시
onMounted(() => {
  if (!isLoggedIn.value) {
    router.push('/login')
    return
  }
  
  fetchUserProfile()
  fetchUserStats()
})
</script>

<style scoped>
@import '../styles/variables.css';

.profile {
  padding: var(--space-8) 0;
  min-height: calc(100vh - 120px);
}

.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 var(--space-4);
}

.profile-card {
  margin-bottom: var(--space-8);
}

.profile-content {
  padding: var(--space-6);
}

/* 프로필 아바타 섹션 */
.profile-avatar-section {
  display: flex;
  align-items: center;
  gap: var(--space-6);
  margin-bottom: var(--space-8);
  padding-bottom: var(--space-6);
  border-bottom: 1px solid var(--border-light);
}

.avatar-container {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-3);
}

.profile-avatar {
  width: 120px;
  height: 120px;
  border-radius: var(--radius-full);
  object-fit: cover;
  border: 4px solid var(--border-light);
  box-shadow: var(--shadow-md);
}

.profile-avatar-default {
  width: 120px;
  height: 120px;
  border-radius: var(--radius-full);
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
  color: var(--text-white);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: var(--text-4xl);
  font-weight: var(--font-bold);
  border: 4px solid var(--border-light);
  box-shadow: var(--shadow-md);
}

.avatar-edit-btn {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-2) var(--space-3);
  background: var(--bg-secondary);
  border: 1px solid var(--border-medium);
  border-radius: var(--radius-lg);
  color: var(--text-secondary);
  cursor: pointer;
  transition: var(--transition-fast);
  font-size: var(--text-sm);
}

.avatar-edit-btn:hover {
  background: var(--bg-tertiary);
  color: var(--text-primary);
  box-shadow: var(--shadow-sm);
}

.file-input {
  display: none;
}

.avatar-info h3 {
  margin: 0 0 var(--space-2) 0;
  color: var(--text-primary);
  font-size: var(--text-xl);
  font-weight: var(--font-semibold);
}

.provider-info {
  color: var(--text-tertiary);
  font-size: var(--text-sm);
  margin: 0;
}

/* 프로필 폼 */
.profile-form {
  display: flex;
  flex-direction: column;
  gap: var(--space-8);
}

.form-section {
  display: flex;
  flex-direction: column;
  gap: var(--space-6);
}

.form-section h3 {
  color: var(--text-primary);
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  margin: 0;
  padding-bottom: var(--space-3);
  border-bottom: 1px solid var(--border-light);
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

/* 읽기 전용 정보 */
.readonly-info {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-3) var(--space-4);
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-light);
}

.info-item label {
  font-weight: var(--font-medium);
  color: var(--text-secondary);
  font-size: var(--text-sm);
}

.info-item span {
  color: var(--text-primary);
  font-size: var(--text-sm);
}

/* 통계 그리드 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: var(--space-4);
}

.stat-item {
  text-align: center;
  padding: var(--space-4);
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-light);
  transition: var(--transition-fast);
}

.stat-item:hover {
  box-shadow: var(--shadow-sm);
}

.stat-number {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: var(--primary-color);
  margin-bottom: var(--space-1);
}

.stat-label {
  font-size: var(--text-xs);
  color: var(--text-tertiary);
  font-weight: var(--font-medium);
}

/* 폼 액션 */
.form-actions {
  display: flex;
  gap: var(--space-3);
  justify-content: flex-end;
  padding-top: var(--space-6);
  border-top: 1px solid var(--border-light);
}

/* 메시지 */
.message {
  margin-top: var(--space-4);
  padding: var(--space-3) var(--space-4);
  border-radius: var(--radius-lg);
  font-size: var(--text-sm);
  text-align: center;
  font-weight: var(--font-medium);
}

.message.success {
  background: rgba(0, 200, 81, 0.1);
  color: var(--success-color);
  border: 1px solid rgba(0, 200, 81, 0.2);
}

.message.error {
  background: rgba(255, 68, 68, 0.1);
  color: var(--error-color);
  border: 1px solid rgba(255, 68, 68, 0.2);
}

/* 반응형 */
@media (max-width: 768px) {
  .container {
    padding: 0 var(--space-4);
  }

  .profile-avatar-section {
    flex-direction: column;
    text-align: center;
    gap: var(--space-4);
  }

  .profile-avatar,
  .profile-avatar-default {
    width: 100px;
    height: 100px;
  }

  .profile-avatar-default {
    font-size: var(--text-3xl);
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: var(--space-3);
  }

  .form-actions {
    flex-direction: column-reverse;
  }

  .info-item {
    flex-direction: column;
    gap: var(--space-2);
    text-align: center;
  }
}
</style>