<!--
  모바일 전용 게임 생성 페이지 컴포넌트
  "술하재밸" 테마의 모바일 앱 스타일 게임 생성 기능을 제공
-->
<template>
  <div class="create-game-mobile">
    <div class="create-form-mobile">
      <div class="header-mobile">
        <h1>게임 만들기</h1>
      </div>
      
      <form @submit.prevent="createGame" class="form-mobile">
        <!-- 게임 제목 -->
        <div class="form-section-mobile">
          <label class="form-label-mobile">게임 제목</label>
          <input
            v-model="form.title"
            type="text"
            class="form-input-mobile"
            placeholder="게임 제목을 입력하세요"
            required
            maxlength="100"
          />
          <div class="char-count-mobile">{{ form.title.length }}/100</div>
        </div>

        <!-- 게임 설명 -->
        <div class="form-section-mobile">
          <label class="form-label-mobile">게임 설명 (선택사항)</label>
          <textarea
            v-model="form.description"
            class="form-textarea-mobile"
            placeholder="게임에 대한 설명을 입력하세요"
            rows="3"
          ></textarea>
        </div>

        <!-- 선택지 A -->
        <div class="option-section-mobile">
          <div class="option-header-mobile">
            <span class="option-badge-a">A</span>
            <h3>첫 번째 선택지</h3>
          </div>
          <div class="option-content-mobile">
            <input
              v-model="form.optionA"
              type="text"
              class="option-input-mobile"
              placeholder="선택지 A를 입력하세요"
              required
              maxlength="100"
            />
            <div class="char-count-mobile">{{ form.optionA.length }}/100</div>
            <textarea
              v-model="form.optionADescription"
              class="option-textarea-mobile"
              placeholder="상세 설명 (선택사항)"
              rows="2"
            ></textarea>
          </div>
        </div>

        <!-- VS 구분자 -->
        <div class="vs-divider-mobile">
          <span>VS</span>
        </div>

        <!-- 선택지 B -->
        <div class="option-section-mobile">
          <div class="option-header-mobile">
            <span class="option-badge-b">B</span>
            <h3>두 번째 선택지</h3>
          </div>
          <div class="option-content-mobile">
            <input
              v-model="form.optionB"
              type="text"
              class="option-input-mobile"
              placeholder="선택지 B를 입력하세요"
              required
              maxlength="100"
            />
            <div class="char-count-mobile">{{ form.optionB.length }}/100</div>
            <textarea
              v-model="form.optionBDescription"
              class="option-textarea-mobile"
              placeholder="상세 설명 (선택사항)"
              rows="2"
            ></textarea>
          </div>
        </div>

        <!-- 액션 버튼 -->
        <div class="actions-mobile">
          <button 
            type="button"
            class="preview-btn-mobile"
            @click="previewGame"
          >
            미리보기
          </button>
          <button 
            type="submit"
            class="submit-btn-mobile"
            :disabled="!isFormValid || loading"
          >
            {{ loading ? '생성 중...' : '게임 생성' }}
          </button>
        </div>

        <div v-if="error" class="error-message-mobile">
          {{ error }}
        </div>
      </form>

      <!-- 미리보기 모달 모바일 -->
      <div v-if="showPreview" class="preview-modal-mobile">
        <div class="preview-overlay-mobile" @click="closePreview"></div>
        <div class="preview-content-mobile">
          <div class="preview-header-mobile">
            <h3>게임 미리보기</h3>
            <button class="close-btn-mobile" @click="closePreview">×</button>
          </div>
          <div class="preview-body-mobile">
            <h4 class="preview-title-mobile">{{ form.title }}</h4>
            <p v-if="form.description" class="preview-desc-mobile">{{ form.description }}</p>
            
            <div class="preview-options-mobile">
              <div class="preview-option-mobile option-a-preview">
                <div class="preview-option-header">
                  <span class="option-badge-a">A</span>
                  <h5>{{ form.optionA }}</h5>
                </div>
                <p v-if="form.optionADescription" class="preview-option-desc">{{ form.optionADescription }}</p>
              </div>
              
              <div class="preview-vs-mobile">VS</div>
              
              <div class="preview-option-mobile option-b-preview">
                <div class="preview-option-header">
                  <span class="option-badge-b">B</span>
                  <h5>{{ form.optionB }}</h5>
                </div>
                <p v-if="form.optionBDescription" class="preview-option-desc">{{ form.optionBDescription }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()

// 반응형 데이터
const form = reactive({
  title: '',
  description: '',
  optionA: '',
  optionADescription: '',
  optionB: '',
  optionBDescription: '',
})

const loading = ref(false)
const error = ref('')
const showPreview = ref(false)

/**
 * 폼 유효성 검사
 */
const isFormValid = computed(() => {
  return form.title.trim() && 
         form.optionA.trim() && 
         form.optionB.trim()
})

/**
 * 게임 미리보기
 */
const previewGame = () => {
  if (!isFormValid.value) {
    error.value = '필수 항목을 모두 입력해주세요.'
    return
  }
  showPreview.value = true
}

/**
 * 미리보기 닫기
 */
const closePreview = () => {
  showPreview.value = false
}

/**
 * 게임 생성 (모바일 API)
 */
const createGame = async () => {
  loading.value = true
  error.value = ''

  try {
    // 현재 로그인한 사용자 정보 가져오기
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    if (!user.id) {
      error.value = '로그인이 필요합니다.'
      router.push('/mobile/login')
      return
    }

    const gameData = {
      title: form.title,
      description: form.description,
      optionA: form.optionA,
      optionADescription: form.optionADescription,
      optionB: form.optionB,
      optionBDescription: form.optionBDescription,
    }

    const response = await axios.post('/api/mobile/balance-games', gameData, {
      params: { authorId: user.id }
    })
    
    // 생성된 게임 페이지로 이동
    router.push(`/mobile/game/${response.data.id}`)
    
  } catch (err) {
    console.error('게임 생성 실패:', err)
    
    if (err.response?.status === 400) {
      error.value = '입력한 정보를 다시 확인해주세요.'
    } else {
      error.value = '게임 생성 중 오류가 발생했습니다. 다시 시도해주세요.'
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* 모바일 뷰 스타일 */
.create-game-mobile {
  background: #f5f5f5;
  min-height: 100vh;
}

.create-form-mobile {
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

.form-mobile {
  padding: 0;
}

.form-section-mobile {
  background: #ffffff;
  padding: 16px;
  border-bottom: 1px solid #e1e5e9;
}

.form-label-mobile {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.form-input-mobile,
.form-textarea-mobile {
  width: 100%;
  padding: 12px;
  border: 1px solid #e1e5e9;
  border-radius: 8px;
  font-size: 14px;
  background: #f8f9fa;
  transition: all 0.2s;
}

.form-input-mobile:focus,
.form-textarea-mobile:focus {
  outline: none;
  border-color: #ffd93d;
  background: #ffffff;
}

.form-textarea-mobile {
  resize: vertical;
  min-height: 80px;
}

.char-count-mobile {
  text-align: right;
  font-size: 11px;
  color: #999;
  margin-top: 4px;
}

.option-section-mobile {
  background: #ffffff;
  padding: 16px;
  margin-bottom: 8px;
  border-bottom: 1px solid #e1e5e9;
}

.option-header-mobile {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.option-badge-a,
.option-badge-b {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 700;
  color: #ffffff;
}

.option-badge-a {
  background: linear-gradient(135deg, #ff6b6b, #ff8e8e);
}

.option-badge-b {
  background: linear-gradient(135deg, #4ecdc4, #6bcf9f);
}

.option-header-mobile h3 {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.option-input-mobile,
.option-textarea-mobile {
  width: 100%;
  padding: 12px;
  border: 1px solid #e1e5e9;
  border-radius: 8px;
  font-size: 14px;
  background: #f8f9fa;
  margin-bottom: 8px;
  transition: all 0.2s;
}

.option-input-mobile:focus,
.option-textarea-mobile:focus {
  outline: none;
  border-color: #ffd93d;
  background: #ffffff;
}

.option-textarea-mobile {
  resize: vertical;
  min-height: 60px;
}

.vs-divider-mobile {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 16px;
  background: #f8f9fa;
}

.vs-divider-mobile span {
  background: #ffffff;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 700;
  color: #666;
  border: 1px solid #e1e5e9;
}

.actions-mobile {
  background: #ffffff;
  padding: 16px;
  display: flex;
  gap: 8px;
}

.preview-btn-mobile {
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

.preview-btn-mobile:hover {
  background: #e9ecef;
}

.submit-btn-mobile {
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

.submit-btn-mobile:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(255, 217, 61, 0.3);
}

.submit-btn-mobile:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.error-message-mobile {
  background: rgba(255, 68, 68, 0.1);
  color: #d32f2f;
  padding: 12px 16px;
  margin: 0 16px 16px 16px;
  border-radius: 8px;
  font-size: 13px;
  border: 1px solid rgba(255, 68, 68, 0.2);
}

/* 미리보기 모달 모바일 */
.preview-modal-mobile {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1000;
}

.preview-overlay-mobile {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
}

.preview-content-mobile {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: #ffffff;
  border-radius: 16px 16px 0 0;
  max-height: 80vh;
  overflow-y: auto;
}

.preview-header-mobile {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #e1e5e9;
  background: #ffd93d;
  border-radius: 16px 16px 0 0;
}

.preview-header-mobile h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.close-btn-mobile {
  background: none;
  border: none;
  font-size: 20px;
  color: #333;
  cursor: pointer;
  padding: 4px;
  border-radius: 50%;
  transition: all 0.2s;
}

.close-btn-mobile:hover {
  background: rgba(0, 0, 0, 0.1);
}

.preview-body-mobile {
  padding: 16px;
}

.preview-title-mobile {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  text-align: center;
  margin: 0 0 8px 0;
}

.preview-desc-mobile {
  font-size: 14px;
  color: #666;
  text-align: center;
  margin: 0 0 16px 0;
}

.preview-options-mobile {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.preview-option-mobile {
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e1e5e9;
}

.option-a-preview {
  border-left: 4px solid #ff6b6b;
}

.option-b-preview {
  border-left: 4px solid #4ecdc4;
}

.preview-option-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.preview-option-header h5 {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.preview-option-desc {
  font-size: 13px;
  color: #666;
  margin: 0;
  margin-left: 32px;
}

.preview-vs-mobile {
  text-align: center;
  font-size: 12px;
  font-weight: 700;
  color: #999;
  padding: 8px 0;
}
</style>