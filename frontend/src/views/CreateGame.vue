<!--
  게임 생성 페이지 컴포넌트
  새로운 밸런스 게임 생성 기능을 제공
-->
<template>
  <div class="create-game">
    <div class="container">
      <UiCard class="create-form" variant="elevated">
        <h1>밸런스 게임 만들기</h1>
        
        <form @submit.prevent="createGame">
          <div class="form-group">
            <UiInput
              v-model="form.title"
              label="게임 제목"
              placeholder="게임 제목을 입력하세요"
              required
              :maxlength="100"
              show-count
            />
          </div>

          <div class="form-group">
            <UiInput
              v-model="form.description"
              type="textarea"
              label="게임 설명"
              placeholder="게임에 대한 설명을 입력하세요 (선택사항)"
              :rows="3"
            />
          </div>

          <div class="options-section">
            <h3>선택지 설정</h3>
            
            <div class="option-group">
              <UiCard class="option-card" variant="outlined">
                <h4>선택지 A</h4>
                <div class="form-group">
                  <UiInput
                    v-model="form.optionA"
                    placeholder="선택지 A를 입력하세요"
                    required
                    :maxlength="100"
                    show-count
                  />
                </div>
                <div class="form-group">
                  <UiInput
                    v-model="form.optionADescription"
                    type="textarea"
                    placeholder="선택지 A에 대한 상세 설명 (선택사항)"
                    :rows="3"
                  />
                </div>
              </UiCard>

              <div class="vs-divider">VS</div>

              <UiCard class="option-card" variant="outlined">
                <h4>선택지 B</h4>
                <div class="form-group">
                  <UiInput
                    v-model="form.optionB"
                    placeholder="선택지 B를 입력하세요"
                    required
                    :maxlength="100"
                    show-count
                  />
                </div>
                <div class="form-group">
                  <UiInput
                    v-model="form.optionBDescription"
                    type="textarea"
                    placeholder="선택지 B에 대한 상세 설명 (선택사항)"
                    :rows="3"
                  />
                </div>
              </UiCard>
            </div>
          </div>


          <div class="form-actions">
            <UiButton 
              variant="secondary" 
              @click="previewGame"
              type="button"
            >
              미리보기
            </UiButton>
            <UiButton 
              variant="primary"
              type="submit"
              :disabled="!isFormValid"
              :loading="loading"
              loading-text="생성 중..."
            >
              게임 생성
            </UiButton>
          </div>

          <div v-if="error" class="error-message">
            {{ error }}
          </div>
        </form>

        <!-- 미리보기 모달 -->
        <UiModal 
          v-model="showPreview"
          title="게임 미리보기"
          size="lg"
        >
          <div class="preview-game">
            <h4>{{ form.title }}</h4>
            <p v-if="form.description">{{ form.description }}</p>
            
            <div class="preview-options">
              <UiCard class="preview-option" variant="outlined">
                <h5>{{ form.optionA }}</h5>
                <p v-if="form.optionADescription">{{ form.optionADescription }}</p>
              </UiCard>
              <div class="preview-vs">VS</div>
              <UiCard class="preview-option" variant="outlined">
                <h5>{{ form.optionB }}</h5>
                <p v-if="form.optionBDescription">{{ form.optionBDescription }}</p>
              </UiCard>
            </div>
          </div>
          
          <template #footer>
            <UiButton variant="secondary" @click="closePreview">
              닫기
            </UiButton>
          </template>
        </UiModal>
      </UiCard>
    </div>
  </div>
</template>

<script setup>
/**
 * 게임 생성 페이지 컴포넌트 로직
 */
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { Card as UiCard, Button as UiButton, Input as UiInput, Modal as UiModal } from '../components/ui'

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
 * 게임 생성
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

    const response = await axios.post('/api/balance-games', gameData, {
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
@import '../styles/variables.css';

.create-game {
  padding: var(--space-8) 0;
}

.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 var(--space-4);
}

.create-form {
  padding: var(--space-8);
}

.create-form h1 {
  text-align: center;
  margin-bottom: var(--space-8);
  color: var(--text-primary);
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
}

.form-group {
  margin-bottom: var(--space-6);
}

.options-section {
  margin: var(--space-8) 0;
  padding: var(--space-6);
  background: var(--bg-secondary);
  border-radius: var(--radius-xl);
  border: 1px solid var(--border-light);
}

.options-section h3 {
  text-align: center;
  margin-bottom: var(--space-6);
  color: var(--text-primary);
  font-size: var(--text-xl);
  font-weight: var(--font-semibold);
}

.option-group {
  display: flex;
  gap: var(--space-8);
  align-items: center;
}

.option-card {
  flex: 1;
  padding: var(--space-6);
}

.option-card h4 {
  margin-bottom: var(--space-4);
  color: var(--text-primary);
  text-align: center;
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
}

.vs-divider {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: var(--text-tertiary);
  text-align: center;
  min-width: 80px;
  background: var(--bg-primary);
  border-radius: var(--radius-full);
  padding: var(--space-3);
  box-shadow: var(--shadow-sm);
}

.form-actions {
  display: flex;
  gap: var(--space-4);
  justify-content: center;
  margin-top: var(--space-8);
  padding-top: var(--space-6);
  border-top: 1px solid var(--border-light);
}

.error-message {
  background: rgba(255, 68, 68, 0.1);
  color: var(--error-color);
  padding: var(--space-3);
  border-radius: var(--radius-lg);
  margin-top: var(--space-4);
  text-align: center;
  border: 1px solid rgba(255, 68, 68, 0.2);
  font-size: var(--text-sm);
}



.preview-game {
  margin-bottom: var(--space-6);
}

.preview-game h4 {
  text-align: center;
  margin-bottom: var(--space-4);
  color: var(--text-primary);
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
}

.preview-game p {
  text-align: center;
  color: var(--text-secondary);
  margin-bottom: var(--space-6);
}

.preview-options {
  display: flex;
  gap: var(--space-4);
  align-items: center;
}

.preview-option {
  flex: 1;
  text-align: center;
}

.preview-option h5 {
  margin-bottom: var(--space-2);
  color: var(--text-primary);
  font-size: var(--text-base);
  font-weight: var(--font-medium);
}

.preview-option p {
  color: var(--text-secondary);
  font-size: var(--text-sm);
  margin: 0;
}

.preview-vs {
  font-weight: var(--font-bold);
  color: var(--text-tertiary);
  background: var(--bg-primary);
  border-radius: var(--radius-full);
  padding: var(--space-2);
  min-width: 60px;
  text-align: center;
}

@media (max-width: 768px) {
  .create-form {
    margin: 0 1rem;
  }
  
  .option-group {
    flex-direction: column;
    gap: 1rem;
  }
  
  .vs-divider {
    transform: rotate(90deg);
    margin: 1rem 0;
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .preview-options {
    flex-direction: column;
  }
  
  .preview-vs {
    margin: 1rem 0;
  }
}
</style>