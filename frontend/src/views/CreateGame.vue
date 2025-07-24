<!--
  웹 전용 게임 생성 페이지 컴포넌트
  데스크톱/태블릿용 새로운 밸런스 게임 생성 기능을 제공
-->
<template>
  <div class="create-game-page">
    <!-- 페이지 헤더 -->
    <section class="page-header">
      <div class="header-container">
        <div class="header-content">
          <div class="header-icon">🍻</div>
          <h1 class="page-title">새로운 술하재밸 만들기</h1>
          <p class="page-subtitle">친구들과 함께할 재밌는 딜레마를 만들어보세요!</p>
        </div>
      </div>
    </section>

    <!-- 메인 콘텐츠 -->
    <main class="main-content">
      <div class="container">
        <div class="create-form-container">
          <div class="form-header">
            <div class="form-icon">✨</div>
            <h2>게임 정보 입력</h2>
            <p>모든 항목을 정확히 입력해주세요</p>
          </div>
          
          <form @submit.prevent="createGame">
            <div class="form-group">
              <label class="form-label">게임 제목</label>
              <input
                v-model="form.title"
                type="text"
                class="form-input"
                placeholder="게임 제목을 입력하세요"
                required
                maxlength="100"
              />
              <div class="char-count">{{ form.title.length }}/100</div>
            </div>

            <div class="form-group">
              <label class="form-label">게임 설명 (선택사항)</label>
              <textarea
                v-model="form.description"
                class="form-textarea"
                placeholder="게임에 대한 설명을 입력하세요"
                rows="3"
              ></textarea>
            </div>

            <!-- 선택지 설정 섹션 -->
            <div class="options-section">
              <div class="section-header">
                <div class="section-icon">⚖️</div>
                <h3>선택지 설정</h3>
                <p>두 가지 선택지를 입력하여 딜레마를 만들어보세요</p>
              </div>
              
              <div class="choices-container">
                <div class="choice-card choice-a">
                  <div class="choice-header">
                    <span class="choice-label">A</span>
                    <h4>첫 번째 선택지</h4>
                  </div>
                  <div class="form-group">
                    <input
                      v-model="form.optionA"
                      type="text"
                      class="form-input"
                      placeholder="선택지 A를 입력하세요"
                      required
                      maxlength="100"
                    />
                    <div class="char-count">{{ form.optionA.length }}/100</div>
                  </div>
                  <div class="form-group">
                    <textarea
                      v-model="form.optionADescription"
                      class="form-textarea"
                      placeholder="선택지 A에 대한 상세 설명 (선택사항)"
                      rows="3"
                    ></textarea>
                  </div>
                </div>

                <div class="vs-divider">
                  <span class="vs-text">VS</span>
                </div>

                <div class="choice-card choice-b">
                  <div class="choice-header">
                    <span class="choice-label">B</span>
                    <h4>두 번째 선택지</h4>
                  </div>
                  <div class="form-group">
                    <input
                      v-model="form.optionB"
                      type="text"
                      class="form-input"
                      placeholder="선택지 B를 입력하세요"
                      required
                      maxlength="100"
                    />
                    <div class="char-count">{{ form.optionB.length }}/100</div>
                  </div>
                  <div class="form-group">
                    <textarea
                      v-model="form.optionBDescription"
                      class="form-textarea"
                      placeholder="선택지 B에 대한 상세 설명 (선택사항)"
                      rows="3"
                    ></textarea>
                  </div>
                </div>
              </div>
            </div>

            <!-- 폼 액션 버튼 -->
            <div class="form-actions">
              <button 
                type="button"
                class="btn-secondary"
                @click="previewGame"
              >
                <span>👁️</span>
                미리보기
              </button>
              <button 
                type="submit"
                class="btn-primary"
                :disabled="!isFormValid || loading"
              >
                <span>{{ loading ? '⏳' : '🎮' }}</span>
                {{ loading ? '생성 중...' : '게임 생성' }}
              </button>
            </div>

            <!-- 에러 메시지 -->
            <div v-if="error" class="error-message">
              <span class="error-icon">⚠️</span>
              {{ error }}
            </div>
          </form>

          <!-- 미리보기 모달 -->
          <div v-if="showPreview" class="preview-modal">
            <div class="preview-overlay" @click="closePreview"></div>
            <div class="preview-content">
              <div class="preview-header">
                <h3>🎮 게임 미리보기</h3>
                <button class="close-btn" @click="closePreview">×</button>
              </div>
              <div class="preview-game">
                <div class="preview-title">{{ form.title }}</div>
                <div v-if="form.description" class="preview-description">{{ form.description }}</div>
                
                <div class="preview-choices">
                  <div class="preview-choice preview-choice-a">
                    <div class="preview-choice-header">
                      <span class="preview-choice-label">A</span>
                      <h5>{{ form.optionA }}</h5>
                    </div>
                    <p v-if="form.optionADescription" class="preview-choice-desc">{{ form.optionADescription }}</p>
                  </div>
                  
                  <div class="preview-vs">VS</div>
                  
                  <div class="preview-choice preview-choice-b">
                    <div class="preview-choice-header">
                      <span class="preview-choice-label">B</span>
                      <h5>{{ form.optionB }}</h5>
                    </div>
                    <p v-if="form.optionBDescription" class="preview-choice-desc">{{ form.optionBDescription }}</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
/**
 * 게임 생성 페이지 컴포넌트 로직
 */
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
 * 게임 생성 (웹 API)
 */
const createGame = async () => {
  loading.value = true
  error.value = ''

  try {
    // 현재 로그인한 사용자 정보 가져오기
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    if (!user.id) {
      error.value = '로그인이 필요합니다.'
      router.push('/login')
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

    const response = await axios.post('/api/web/balance-games', gameData, {
      params: { authorId: user.id }
    })
    
    // 생성된 게임 페이지로 이동
    router.push(`/game/${response.data.id}`)
    
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
/* 게임 생성 페이지 - 완전한 웹사이트 스타일 */
.create-game-page {
  background: #ffffff;
  min-height: 100vh;
}

/* 페이지 헤더 */
.page-header {
  background: #f8fafc;
  padding: 80px 0;
  position: relative;
  display: flex;
  justify-content: center;
}

.page-header .header-container {
  background: linear-gradient(135deg, #FF6B35 0%, #F7931E 50%, #FFD23F 100%);
  border-radius: 30px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.15);
  position: relative;
  overflow: hidden;
}

.page-header .header-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: radial-gradient(ellipse at center, rgba(255,255,255,0.15) 0%, transparent 70%);
  pointer-events: none;
  z-index: 1;
}

.header-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 60px 80px;
  position: relative;
  z-index: 2;
  color: white;
}

.header-icon {
  font-size: 3rem;
  margin-bottom: 16px;
  display: block;
  filter: drop-shadow(0 4px 8px rgba(0,0,0,0.3));
}

.header-content {
  text-align: center;
}

.page-title {
  font-size: 2.4rem;
  font-weight: 800;
  line-height: 1.2;
  margin-bottom: 16px;
  text-shadow: 0 4px 20px rgba(0,0,0,0.3);
  background: linear-gradient(45deg, #ffffff, #e8f4fd);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.page-subtitle {
  font-size: 1.1rem;
  font-weight: 400;
  opacity: 0.95;
  line-height: 1.4;
  max-width: 600px;
  margin: 0 auto;
}

/* 메인 콘텐츠 */
.main-content {
  padding: 60px 0;
  background: #f8fafc;
}

.container {
  max-width: 900px;
  margin: 0 auto;
  padding: 0 60px;
}

.create-form-container {
  background: white;
  border-radius: 20px;
  box-shadow: 0 10px 40px rgba(0,0,0,0.1);
  border: 1px solid #e2e8f0;
  overflow: hidden;
}

/* 폼 헤더 */
.form-header {
  text-align: center;
  padding: 40px 40px 30px;
  background: linear-gradient(135deg, #f8fafc 0%, #ffffff 100%);
  border-bottom: 1px solid #e2e8f0;
}

.form-icon {
  font-size: 3rem;
  margin-bottom: 16px;
  display: block;
}

.form-header h2 {
  font-size: 1.8rem;
  font-weight: 700;
  color: #1a202c;
  margin-bottom: 8px;
}

.form-header p {
  color: #64748b;
  font-size: 1rem;
  margin: 0;
}

/* 폼 스타일 */
form {
  padding: 40px;
}

.form-group {
  margin-bottom: 30px;
}

.form-label {
  display: block;
  margin-bottom: 10px;
  color: #374151;
  font-weight: 600;
  font-size: 1rem;
}

.form-input,
.form-textarea {
  width: 100%;
  padding: 15px 20px;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  font-size: 1rem;
  background: #f8fafc;
  color: #374151;
  transition: all 0.3s ease;
  font-family: inherit;
}

.form-input:focus,
.form-textarea:focus {
  outline: none;
  border-color: #667eea;
  background: white;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-textarea {
  resize: vertical;
  min-height: 100px;
  line-height: 1.6;
}

.char-count {
  text-align: right;
  font-size: 0.85rem;
  color: #9ca3af;
  margin-top: 8px;
  font-weight: 500;
}

/* 선택지 섹션 */
.options-section {
  margin: 40px 0;
  padding: 40px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-radius: 16px;
  border: 1px solid #e2e8f0;
}

.section-header {
  text-align: center;
  margin-bottom: 40px;
}

.section-icon {
  font-size: 2.5rem;
  margin-bottom: 16px;
  display: block;
}

.section-header h3 {
  font-size: 1.8rem;
  font-weight: 700;
  color: #1a202c;
  margin-bottom: 8px;
}

.section-header p {
  color: #64748b;
  font-size: 1rem;
  margin: 0;
}

.choices-container {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  gap: 30px;
  align-items: start;
}

.choice-card {
  background: white;
  border-radius: 16px;
  padding: 30px;
  border: 2px solid transparent;
  box-shadow: 0 5px 25px rgba(0,0,0,0.08);
  transition: all 0.3s ease;
}

.choice-card:hover {
  border-color: #e2e8f0;
  box-shadow: 0 10px 40px rgba(0,0,0,0.12);
  transform: translateY(-2px);
}

.choice-a:hover {
  border-color: #ff6b6b;
  box-shadow: 0 10px 40px rgba(255, 107, 107, 0.2);
}

.choice-b:hover {
  border-color: #4ecdc4;
  box-shadow: 0 10px 40px rgba(78, 205, 196, 0.2);
}

.choice-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
}

.choice-label {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  font-weight: 800;
  font-size: 1.2rem;
  color: white;
  flex-shrink: 0;
}

.choice-a .choice-label {
  background: linear-gradient(135deg, #ff6b6b, #ff8e8e);
}

.choice-b .choice-label {
  background: linear-gradient(135deg, #4ecdc4, #6bcf9f);
}

.choice-header h4 {
  font-size: 1.2rem;
  font-weight: 600;
  color: #1a202c;
  margin: 0;
}

.vs-divider {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 60px;
}

.vs-text {
  background: #667eea;
  color: white;
  padding: 15px 20px;
  border-radius: 25px;
  font-weight: 800;
  font-size: 1.1rem;
  box-shadow: 0 5px 20px rgba(102, 126, 234, 0.3);
}

/* 폼 액션 버튼 */
.form-actions {
  display: flex;
  gap: 20px;
  justify-content: center;
  margin-top: 50px;
  padding-top: 40px;
  border-top: 2px solid #e2e8f0;
}

.btn-primary,
.btn-secondary {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 16px 32px;
  border-radius: 25px;
  font-weight: 600;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
  font-family: inherit;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.btn-primary:hover:not(:disabled) {
  background: linear-gradient(135deg, #5a6fd8, #6a42a0);
  transform: translateY(-2px);
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.5);
}

.btn-secondary {
  background: white;
  color: #64748b;
  border: 2px solid #e2e8f0;
  box-shadow: 0 4px 15px rgba(0,0,0,0.05);
}

.btn-secondary:hover {
  background: #f8fafc;
  border-color: #cbd5e0;
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.1);
}

.btn-primary:disabled,
.btn-secondary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

/* 에러 메시지 */
.error-message {
  display: flex;
  align-items: center;
  gap: 10px;
  background: linear-gradient(135deg, #fee2e2, #fecaca);
  color: #dc2626;
  padding: 16px 20px;
  border-radius: 12px;
  margin-top: 20px;
  text-align: center;
  border: 2px solid #fca5a5;
  font-size: 0.95rem;
  font-weight: 500;
  justify-content: center;
}

.error-icon {
  font-size: 1.2rem;
}

/* 미리보기 모달 */
.preview-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.preview-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(5px);
}

.preview-content {
  position: relative;
  background: white;
  border-radius: 20px;
  max-width: 700px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  border: 1px solid #e2e8f0;
}

/* 미리보기 헤더 */
.preview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30px 30px 20px;
  border-bottom: 2px solid #e2e8f0;
}

.preview-header h3 {
  margin: 0;
  color: #1a202c;
  font-size: 1.4rem;
  font-weight: 700;
}

.close-btn {
  background: #f8fafc;
  border: 2px solid #e2e8f0;
  font-size: 1.5rem;
  color: #64748b;
  cursor: pointer;
  padding: 8px;
  border-radius: 50%;
  transition: all 0.3s ease;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-btn:hover {
  background: #f1f5f9;
  color: #374151;
  border-color: #cbd5e0;
  transform: scale(1.1);
}

/* 미리보기 게임 내용 */
.preview-game {
  padding: 30px;
}

.preview-title {
  text-align: center;
  margin-bottom: 20px;
  color: #1a202c;
  font-size: 1.4rem;
  font-weight: 700;
}

.preview-description {
  text-align: center;
  color: #64748b;
  margin-bottom: 30px;
  line-height: 1.6;
  font-size: 1rem;
  padding: 16px;
  background: #f8fafc;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.preview-choices {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  gap: 20px;
  align-items: center;
}

.preview-choice {
  background: #f8fafc;
  border-radius: 16px;
  padding: 24px;
  border: 2px solid transparent;
  transition: all 0.3s ease;
}

.preview-choice-a {
  border-color: rgba(255, 107, 107, 0.3);
  background: linear-gradient(135deg, rgba(255, 107, 107, 0.05), rgba(255, 142, 142, 0.05));
}

.preview-choice-b {
  border-color: rgba(78, 205, 196, 0.3);
  background: linear-gradient(135deg, rgba(78, 205, 196, 0.05), rgba(107, 207, 159, 0.05));
}

.preview-choice-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.preview-choice-label {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  font-weight: 800;
  color: white;
  font-size: 1rem;
}

.preview-choice-a .preview-choice-label {
  background: linear-gradient(135deg, #ff6b6b, #ff8e8e);
}

.preview-choice-b .preview-choice-label {
  background: linear-gradient(135deg, #4ecdc4, #6bcf9f);
}

.preview-choice-header h5 {
  margin: 0;
  color: #1a202c;
  font-size: 1.1rem;
  font-weight: 600;
}

.preview-choice-desc {
  color: #64748b;
  font-size: 0.9rem;
  margin: 0;
  line-height: 1.5;
}

.preview-vs {
  background: #667eea;
  color: white;
  padding: 12px 16px;
  border-radius: 20px;
  font-weight: 800;
  font-size: 1rem;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
  text-align: center;
}

/* 반응형 디자인 */
@media (max-width: 1024px) {
  .header-container,
  .container {
    padding: 0 30px;
  }
  
  .page-title {
    font-size: 2rem;
  }
  
  .choices-container {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .vs-divider {
    margin-top: 0;
    order: -1;
  }
}

@media (max-width: 768px) {
  .page-header {
    padding: 40px 0;
  }
  
  .header-container,
  .container {
    padding: 0 20px;
  }
  
  .page-title {
    font-size: 1.8rem;
  }
  
  .page-subtitle {
    font-size: 1rem;
  }
  
  .create-form-container {
    border-radius: 16px;
  }
  
  form {
    padding: 30px 20px;
  }
  
  .options-section {
    padding: 30px 20px;
  }
  
  .choice-card {
    padding: 20px;
  }
  
  .form-actions {
    flex-direction: column;
    align-items: stretch;
  }
  
  .btn-primary,
  .btn-secondary {
    justify-content: center;
  }
  
  .preview-choices {
    grid-template-columns: 1fr;
    gap: 15px;
  }
  
  .preview-vs {
    order: -1;
    align-self: center;
  }
}
</style>