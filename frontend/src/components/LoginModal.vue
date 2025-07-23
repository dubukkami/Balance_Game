<!--
  로그인 유도 모달 컴포넌트
  비로그인 사용자가 로그인이 필요한 기능 이용 시 표시
-->
<template>
  <div v-if="isVisible" class="modal-overlay" @click="closeModal">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h3>로그인이 필요합니다</h3>
        <button class="close-btn" @click="closeModal">×</button>
      </div>
      
      <div class="modal-body">
        <div class="login-icon">
          <i class="icon">🔐</i>
        </div>
        <p class="message">{{ message }}</p>
        <p class="sub-message">로그인하고 더 많은 기능을 이용해보세요!</p>
      </div>
      
      <div class="modal-footer">
        <button class="btn btn-secondary" @click="closeModal">
          취소
        </button>
        <button class="btn btn-primary" @click="goToLogin">
          로그인하러 가기
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const props = defineProps({
  isVisible: {
    type: Boolean,
    default: false
  },
  message: {
    type: String,
    default: '이 기능을 사용하려면 로그인이 필요합니다.'
  }
})

const emit = defineEmits(['close'])

const closeModal = () => {
  emit('close')
}

const goToLogin = () => {
  emit('close')
  router.push('/login')
}
</script>

<style scoped>
@import '../styles/variables.css';

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(3px);
}

.modal-content {
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-xl);
  width: 90%;
  max-width: 450px;
  max-height: 90vh;
  overflow: hidden;
  border: 1px solid var(--border-light);
  animation: modalAppear 0.3s ease-out;
}

@keyframes modalAppear {
  from {
    opacity: 0;
    transform: scale(0.9) translateY(-20px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-6);
  border-bottom: 1px solid var(--border-light);
  background: var(--bg-secondary);
}

.modal-header h3 {
  margin: 0;
  color: var(--text-primary);
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
}

.close-btn {
  background: none;
  border: none;
  font-size: var(--text-2xl);
  color: var(--text-secondary);
  cursor: pointer;
  padding: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-full);
  transition: var(--transition-fast);
}

.close-btn:hover {
  background: var(--bg-tertiary);
  color: var(--text-primary);
}

.modal-body {
  padding: var(--space-8);
  text-align: center;
}

.login-icon {
  margin-bottom: var(--space-4);
}

.login-icon .icon {
  font-size: var(--text-4xl);
  opacity: 0.8;
}

.message {
  color: var(--text-primary);
  font-size: var(--text-base);
  font-weight: var(--font-medium);
  margin: 0 0 var(--space-2) 0;
  line-height: var(--leading-relaxed);
}

.sub-message {
  color: var(--text-secondary);
  font-size: var(--text-sm);
  margin: 0;
  line-height: var(--leading-normal);
}

.modal-footer {
  display: flex;
  gap: var(--space-3);
  padding: var(--space-6);
  border-top: 1px solid var(--border-light);
  background: var(--bg-secondary);
}

.btn {
  flex: 1;
  padding: var(--space-3) var(--space-4);
  border-radius: var(--radius-lg);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  cursor: pointer;
  transition: var(--transition-fast);
  border: 1px solid transparent;
}

.btn-secondary {
  background: var(--bg-primary);
  color: var(--text-secondary);
  border-color: var(--border-medium);
}

.btn-secondary:hover {
  background: var(--bg-tertiary);
  color: var(--text-primary);
}

.btn-primary {
  background: var(--primary-color);
  color: var(--text-white);
}

.btn-primary:hover {
  background: var(--primary-dark);
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

@media (max-width: 480px) {
  .modal-content {
    margin: var(--space-4);
  }
  
  .modal-header,
  .modal-body,
  .modal-footer {
    padding: var(--space-4);
  }
  
  .modal-footer {
    flex-direction: column;
  }
}
</style>