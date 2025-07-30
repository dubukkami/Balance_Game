<!--
  마이페이지 컴포넌트 (데스크톱 전용)
  사용자 프로필 정보 관리
-->
<template>
  <div class="profile">
    <!-- 데스크톱 뷰 -->
    <div class="desktop-view">
      <div class="container">
        <div class="profile-card">
          <div class="profile-header">
            <h1>마이페이지</h1>
          </div>

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
                  <span class="icon">📷</span>
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
                  <label class="form-label">닉네임</label>
                  <input
                    v-model="form.nickname"
                    type="text"
                    class="form-input"
                    placeholder="사용할 닉네임을 입력하세요"
                    maxlength="20"
                    required
                  />
                  <div class="char-count">{{ form.nickname.length }}/20</div>
                </div>

                <div class="form-group">
                  <label class="form-label">자기소개</label>
                  <textarea
                    v-model="form.bio"
                    class="form-textarea"
                    placeholder="간단한 자기소개를 입력하세요"
                    rows="4"
                    maxlength="200"
                  ></textarea>
                  <div class="char-count">{{ form.bio.length }}/200</div>
                </div>

                <div class="form-group">
                  <label class="form-label">지역</label>
                  <input
                    v-model="form.location"
                    type="text"
                    class="form-input"
                    placeholder="거주 지역을 입력하세요"
                    maxlength="50"
                  />
                </div>

                <div class="form-group">
                  <label class="form-label">웹사이트</label>
                  <input
                    v-model="form.website"
                    type="text"
                    class="form-input"
                    placeholder="개인 웹사이트나 블로그 주소"
                    maxlength="100"
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
                <button 
                  type="button"
                  class="btn btn-secondary"
                  @click="resetForm"
                >
                  취소
                </button>
                
                <button 
                  type="submit"
                  class="btn btn-primary"
                  :disabled="loading"
                >
                  {{ loading ? '저장 중...' : '저장하기' }}
                </button>
              </div>
            </form>

            <!-- 에러/성공 메시지 -->
            <div v-if="message" class="message" :class="messageType">
              {{ message }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

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

/* 데스크톱 뷰 스타일 */
.desktop-view {
  padding: var(--space-8) 0;
  min-height: calc(100vh - 120px);
}

.desktop-view .container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 var(--space-4);
}

.desktop-view .profile-card {
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-lg);
  border: 1px solid var(--border-light);
}

.desktop-view .profile-header {
  padding: var(--space-6) var(--space-8) var(--space-4) var(--space-8);
  border-bottom: 1px solid var(--border-light);
}

.desktop-view .profile-header h1 {
  margin: 0;
  color: var(--text-primary);
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
}

.desktop-view .profile-content {
  padding: var(--space-8);
}

/* 프로필 아바타 섹션 */
.desktop-view .profile-avatar-section {
  display: flex;
  align-items: center;
  gap: var(--space-6);
  margin-bottom: var(--space-8);
  padding-bottom: var(--space-6);
  border-bottom: 1px solid var(--border-light);
}

.desktop-view .avatar-container {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-3);
}

.desktop-view .profile-avatar {
  width: 120px;
  height: 120px;
  border-radius: var(--radius-full);
  object-fit: cover;
  border: 4px solid var(--border-light);
  box-shadow: var(--shadow-md);
}

.desktop-view .profile-avatar-default {
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

.desktop-view .avatar-edit-btn {
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

.desktop-view .avatar-edit-btn:hover {
  background: var(--bg-tertiary);
  color: var(--text-primary);
  box-shadow: var(--shadow-sm);
}

.file-input {
  display: none;
}

.desktop-view .avatar-info h3 {
  margin: 0 0 var(--space-2) 0;
  color: var(--text-primary);
  font-size: var(--text-xl);
  font-weight: var(--font-semibold);
}

.desktop-view .provider-info {
  color: var(--text-tertiary);
  font-size: var(--text-sm);
  margin: 0;
}

/* 프로필 폼 */
.desktop-view .profile-form {
  display: flex;
  flex-direction: column;
  gap: var(--space-8);
}

.desktop-view .form-section {
  display: flex;
  flex-direction: column;
  gap: var(--space-6);
}

.desktop-view .form-section h3 {
  color: var(--text-primary);
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  margin: 0;
  padding-bottom: var(--space-3);
  border-bottom: 1px solid var(--border-light);
}

.desktop-view .form-group {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.desktop-view .form-label {
  font-weight: var(--font-medium);
  color: var(--text-primary);
  font-size: var(--text-sm);
}

.desktop-view .form-input,
.desktop-view .form-textarea {
  padding: var(--space-3);
  border: 1px solid var(--border-light);
  border-radius: var(--radius-md);
  font-size: var(--text-base);
  background: var(--bg-primary);
  color: var(--text-primary);
  transition: var(--transition-fast);
}

.desktop-view .form-input:focus,
.desktop-view .form-textarea:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(45, 95, 193, 0.1);
}

.desktop-view .form-textarea {
  resize: vertical;
  min-height: 100px;
}

.desktop-view .char-count {
  text-align: right;
  font-size: var(--text-xs);
  color: var(--text-tertiary);
}

/* 읽기 전용 정보 */
.desktop-view .readonly-info {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.desktop-view .info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-3) var(--space-4);
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-light);
}

.desktop-view .info-item label {
  font-weight: var(--font-medium);
  color: var(--text-secondary);
  font-size: var(--text-sm);
}

.desktop-view .info-item span {
  color: var(--text-primary);
  font-size: var(--text-sm);
}

/* 통계 그리드 */
.desktop-view .stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: var(--space-4);
}

.desktop-view .stat-item {
  text-align: center;
  padding: var(--space-4);
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-light);
  transition: var(--transition-fast);
}

.desktop-view .stat-item:hover {
  box-shadow: var(--shadow-sm);
}

.desktop-view .stat-number {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: var(--primary-color);
  margin-bottom: var(--space-1);
}

.desktop-view .stat-label {
  font-size: var(--text-xs);
  color: var(--text-tertiary);
  font-weight: var(--font-medium);
}

/* 폼 액션 */
.desktop-view .form-actions {
  display: flex;
  gap: var(--space-3);
  justify-content: flex-end;
  padding-top: var(--space-6);
  border-top: 1px solid var(--border-light);
}

.desktop-view .btn {
  padding: var(--space-3) var(--space-6);
  border-radius: var(--radius-md);
  font-weight: var(--font-medium);
  cursor: pointer;
  transition: var(--transition-fast);
  border: none;
  font-size: var(--text-base);
}

.desktop-view .btn-primary {
  background: var(--primary-color);
  color: var(--text-white);
}

.desktop-view .btn-primary:hover:not(:disabled) {
  background: var(--secondary-color);
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

.desktop-view .btn-secondary {
  background: var(--bg-tertiary);
  color: var(--text-primary);
  border: 1px solid var(--border-medium);
}

.desktop-view .btn-secondary:hover {
  background: var(--bg-secondary);
  transform: translateY(-1px);
  box-shadow: var(--shadow-sm);
}

.desktop-view .btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 메시지 */
.desktop-view .message {
  margin-top: var(--space-4);
  padding: var(--space-3) var(--space-4);
  border-radius: var(--radius-lg);
  font-size: var(--text-sm);
  text-align: center;
  font-weight: var(--font-medium);
}

.desktop-view .message.success {
  background: rgba(0, 200, 81, 0.1);
  color: var(--success-color);
  border: 1px solid rgba(0, 200, 81, 0.2);
}

.desktop-view .message.error {
  background: rgba(255, 68, 68, 0.1);
  color: var(--error-color);
  border: 1px solid rgba(255, 68, 68, 0.2);
}

</style>