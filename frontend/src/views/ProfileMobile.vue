<!--
  모바일 프로필 컴포넌트
  사용자 프로필 정보 관리 (모바일 최적화)
-->
<template>
  <div class="profile-mobile">
    <div class="profile-container-mobile">
      <!-- 헤더 -->
      <div class="header-mobile">
        <h1>내 정보</h1>
      </div>

      <!-- 프로필 정보 -->
      <div class="profile-section-mobile">
        <div class="avatar-section-mobile">
          <div class="avatar-wrapper-mobile">
            <img 
              v-if="form.profileImageUrl" 
              :src="form.profileImageUrl" 
              :alt="form.nickname"
              class="avatar-mobile"
            />
            <div v-else class="avatar-default-mobile">
              {{ form.nickname?.charAt(0) || '?' }}
            </div>
            
            <button class="avatar-edit-mobile" @click="triggerFileInput">
              <span>📷</span>
            </button>
            
            <input 
              ref="fileInput"
              type="file"
              accept="image/*"
              @change="handleFileChange"
              class="file-input"
            />
          </div>
          
          <div class="user-info-mobile">
            <h2>{{ form.nickname || '사용자' }}</h2>
            <p>{{ getProviderName(user?.provider) }} 로그인</p>
          </div>
        </div>
      </div>

      <!-- 활동 통계 -->
      <div class="stats-section-mobile">
        <h3>🏆 활동 통계</h3>
        <div class="stats-grid-mobile">
          <div class="stat-card-mobile">
            <div class="stat-icon-mobile">🎮</div>
            <div class="stat-number-mobile">{{ userStats.gamesCreated }}</div>
            <div class="stat-label-mobile">만든 게임</div>
          </div>
          
          <div class="stat-card-mobile">
            <div class="stat-icon-mobile">🗳️</div>
            <div class="stat-number-mobile">{{ userStats.totalVotes }}</div>
            <div class="stat-label-mobile">총 투표</div>
          </div>
          
          <div class="stat-card-mobile">
            <div class="stat-icon-mobile">💬</div>
            <div class="stat-number-mobile">{{ userStats.commentsCount }}</div>
            <div class="stat-label-mobile">댓글</div>
          </div>
          
          <div class="stat-card-mobile">
            <div class="stat-icon-mobile">❤️</div>
            <div class="stat-number-mobile">{{ userStats.likesReceived }}</div>
            <div class="stat-label-mobile">좋아요</div>
          </div>
        </div>
      </div>

      <!-- 기본 정보 폼 -->
      <div class="form-section-mobile">
        <h3>📝 기본 정보</h3>
        <form @submit.prevent="updateProfile">
          <div class="input-group-mobile">
            <label>닉네임</label>
            <input
              v-model="form.nickname"
              type="text"
              placeholder="사용할 닉네임을 입력하세요"
              maxlength="20"
              required
            />
            <div class="char-count-mobile">{{ form.nickname.length }}/20</div>
          </div>

          <div class="input-group-mobile">
            <label>자기소개</label>
            <textarea
              v-model="form.bio"
              placeholder="간단한 자기소개를 입력하세요"
              rows="3"
              maxlength="200"
            ></textarea>
            <div class="char-count-mobile">{{ form.bio.length }}/200</div>
          </div>

          <div class="input-group-mobile">
            <label>지역</label>
            <input
              v-model="form.location"
              type="text"
              placeholder="거주 지역을 입력하세요"
              maxlength="50"
            />
          </div>

          <div class="input-group-mobile">
            <label>웹사이트</label>
            <input
              v-model="form.website"
              type="text"
              placeholder="개인 웹사이트나 블로그 주소"
              maxlength="100"
            />
          </div>

          <div class="form-buttons-mobile">
            <button 
              type="button"
              class="btn-reset-mobile"
              @click="resetForm"
            >
              취소
            </button>
            
            <button 
              type="submit"
              class="btn-save-mobile"
              :disabled="loading"
            >
              {{ loading ? '저장 중...' : '저장하기' }}
            </button>
          </div>
        </form>
      </div>

      <!-- 계정 정보 -->
      <div class="account-section-mobile">
        <h3>⚙️ 계정 정보</h3>
        <div class="account-items-mobile">
          <div class="account-item-mobile">
            <span class="account-label-mobile">이메일</span>
            <span class="account-value-mobile">{{ user?.email || '이메일 없음' }}</span>
          </div>
          
          <div class="account-item-mobile">
            <span class="account-label-mobile">가입일</span>
            <span class="account-value-mobile">{{ formatDate(user?.createdAt) }}</span>
          </div>
          
          <div class="account-item-mobile">
            <span class="account-label-mobile">로그인 방식</span>
            <span class="account-value-mobile">{{ getProviderName(user?.provider) }}</span>
          </div>
        </div>
      </div>

      <!-- 메시지 -->
      <div v-if="message" class="message-mobile" :class="messageType">
        {{ message }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import axios from 'axios'

const router = useRouter()
const authStore = useAuthStore()

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
/* 모바일 뷰 스타일 */
.profile-mobile {
  background: #f5f5f5;
  min-height: 100vh;
}

.profile-container-mobile {
  background: #ffffff;
}

.header-mobile {
  background: #ffd93d;
  padding: 16px;
  text-align: center;
  border-bottom: 1px solid #e1e5e9;
}

.header-mobile h1 {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.profile-section-mobile {
  background: #ffffff;
  padding: 20px 16px;
  border-bottom: 8px solid #f5f5f5;
}

.avatar-section-mobile {
  display: flex;
  align-items: center;
  gap: 16px;
}

.avatar-wrapper-mobile {
  position: relative;
}

.avatar-mobile {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.avatar-default-mobile {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #ffd93d, #ff6b6b);
  color: #333;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  font-weight: 700;
  border: 3px solid #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.avatar-edit-mobile {
  position: absolute;
  bottom: -2px;
  right: -2px;
  width: 28px;
  height: 28px;
  background: #ffd93d;
  border: 2px solid #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.2s;
}

.avatar-edit-mobile:hover {
  transform: scale(1.1);
}

.file-input {
  display: none;
}

.user-info-mobile h2 {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0 0 4px 0;
}

.user-info-mobile p {
  font-size: 13px;
  color: #666;
  margin: 0;
}

.stats-section-mobile {
  background: #ffffff;
  padding: 20px 16px;
  border-bottom: 8px solid #f5f5f5;
}

.stats-section-mobile h3 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 16px 0;
}

.stats-grid-mobile {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.stat-card-mobile {
  background: #f8f9fa;
  padding: 16px;
  border-radius: 12px;
  text-align: center;
  border: 1px solid #e1e5e9;
}

.stat-icon-mobile {
  font-size: 20px;
  margin-bottom: 8px;
}

.stat-number-mobile {
  font-size: 20px;
  font-weight: 700;
  color: #ffd93d;
  margin-bottom: 4px;
}

.stat-label-mobile {
  font-size: 12px;
  color: #666;
  font-weight: 500;
}

.form-section-mobile {
  background: #ffffff;
  padding: 20px 16px;
  border-bottom: 8px solid #f5f5f5;
}

.form-section-mobile h3 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 16px 0;
}

.input-group-mobile {
  margin-bottom: 16px;
}

.input-group-mobile label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 6px;
}

.input-group-mobile input,
.input-group-mobile textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #e1e5e9;
  border-radius: 8px;
  font-size: 14px;
  background: #f8f9fa;
  transition: all 0.2s;
  box-sizing: border-box;
}

.input-group-mobile input:focus,
.input-group-mobile textarea:focus {
  outline: none;
  border-color: #ffd93d;
  background: #ffffff;
}

.input-group-mobile textarea {
  resize: vertical;
  min-height: 80px;
}

.char-count-mobile {
  text-align: right;
  font-size: 11px;
  color: #999;
  margin-top: 4px;
}

.form-buttons-mobile {
  display: flex;
  gap: 8px;
  margin-top: 20px;
}

.btn-reset-mobile {
  flex: 1;
  padding: 12px;
  background: #f8f9fa;
  border: 1px solid #e1e5e9;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-reset-mobile:hover {
  background: #e9ecef;
}

.btn-save-mobile {
  flex: 2;
  padding: 12px;
  background: #ffd93d;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #333;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-save-mobile:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(255, 217, 61, 0.3);
}

.btn-save-mobile:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.account-section-mobile {
  background: #ffffff;
  padding: 20px 16px;
}

.account-section-mobile h3 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 16px 0;
}

.account-items-mobile {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.account-item-mobile {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e1e5e9;
}

.account-label-mobile {
  font-size: 13px;
  font-weight: 600;
  color: #666;
}

.account-value-mobile {
  font-size: 13px;
  color: #333;
  font-weight: 500;
}

.message-mobile {
  margin: 16px;
  padding: 12px;
  border-radius: 8px;
  font-size: 14px;
  text-align: center;
  font-weight: 500;
}

.message-mobile.success {
  background: rgba(0, 200, 81, 0.1);
  color: #00c851;
  border: 1px solid rgba(0, 200, 81, 0.2);
}

.message-mobile.error {
  background: rgba(255, 68, 68, 0.1);
  color: #d32f2f;
  border: 1px solid rgba(255, 68, 68, 0.2);
}
</style>