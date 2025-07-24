<!--
  토스트 알림 컴포넌트
  성공, 정보, 경고, 에러 메시지를 예쁘게 표시
-->
<template>
  <div v-if="isVisible" class="toast-container" :class="type">
    <div class="toast-content">
      <div class="toast-icon">
        <i class="icon">{{ getIcon() }}</i>
      </div>
      <div class="toast-message">
        <p class="message-text">{{ message }}</p>
      </div>
      <button class="close-btn" @click="closeToast">×</button>
    </div>
    <div class="toast-progress" :class="{ 'animate': isVisible }"></div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'

const props = defineProps({
  isVisible: {
    type: Boolean,
    default: false
  },
  message: {
    type: String,
    required: true
  },
  type: {
    type: String,
    default: 'info', // 'success', 'info', 'warning', 'error'
    validator: (value) => ['success', 'info', 'warning', 'error'].includes(value)
  },
  duration: {
    type: Number,
    default: 3000
  }
})

const emit = defineEmits(['close'])

let timeoutId = null

const getIcon = () => {
  const icons = {
    success: '✅',
    info: 'ℹ️',
    warning: '⚠️',
    error: '❌'
  }
  return icons[props.type] || icons.info
}

const closeToast = () => {
  if (timeoutId) {
    clearTimeout(timeoutId)
    timeoutId = null
  }
  emit('close')
}

watch(() => props.isVisible, (newValue) => {
  if (newValue) {
    timeoutId = setTimeout(() => {
      closeToast()
    }, props.duration)
  }
})
</script>

<style scoped>
@import '../styles/variables.css';

.toast-container {
  position: fixed;
  bottom: 20px;
  right: 20px;
  z-index: 9999;
  min-width: 320px;
  max-width: 400px;
  background: var(--bg-primary);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-xl);
  border: 1px solid var(--border-light);
  overflow: hidden;
  animation: slideInRight 0.3s ease-out;
}

@keyframes slideInRight {
  from {
    opacity: 0;
    transform: translateX(100%);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.toast-content {
  display: flex;
  align-items: center;
  padding: var(--space-4);
  gap: var(--space-3);
}

.toast-icon {
  flex-shrink: 0;
}

.toast-icon .icon {
  font-size: var(--text-xl);
}

.toast-message {
  flex: 1;
}

.message-text {
  margin: 0;
  color: var(--text-primary);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  line-height: var(--leading-normal);
}

.close-btn {
  background: none;
  border: none;
  font-size: var(--text-xl);
  color: var(--text-tertiary);
  cursor: pointer;
  padding: 0;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-full);
  transition: var(--transition-fast);
  flex-shrink: 0;
}

.close-btn:hover {
  background: var(--bg-tertiary);
  color: var(--text-primary);
}

.toast-progress {
  height: 3px;
  background: var(--primary-color);
  width: 0;
  transition: none;
}

.toast-progress.animate {
  animation: progressBar 3s linear;
}

@keyframes progressBar {
  from {
    width: 100%;
  }
  to {
    width: 0;
  }
}

/* 타입별 스타일 */
.toast-container.success {
  border-left: 4px solid #10b981;
}

.toast-container.success .toast-progress {
  background: #10b981;
}

.toast-container.info {
  border-left: 4px solid var(--primary-color);
}

.toast-container.info .toast-progress {
  background: var(--primary-color);
}

.toast-container.warning {
  border-left: 4px solid #f59e0b;
}

.toast-container.warning .toast-progress {
  background: #f59e0b;
}

.toast-container.error {
  border-left: 4px solid #ef4444;
}

.toast-container.error .toast-progress {
  background: #ef4444;
}

@media (max-width: 480px) {
  .toast-container {
    position: fixed;
    bottom: 80px;
    left: 50%;
    right: auto;
    top: auto;
    transform: translateX(-50%);
    min-width: 280px;
    max-width: 90vw;
    animation: slideInBottom 0.3s ease-out;
  }
  
  @keyframes slideInBottom {
    from {
      opacity: 0;
      transform: translateX(-50%) translateY(100%);
    }
    to {
      opacity: 1;
      transform: translateX(-50%) translateY(0);
    }
  }
}
</style>