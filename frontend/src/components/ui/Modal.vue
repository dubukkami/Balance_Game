<!--
  재사용 가능한 모달 컴포넌트
  Blind 스타일 모달 UI를 제공
-->
<template>
  <Teleport to="body">
    <Transition name="modal" appear>
      <div v-if="modelValue" class="modal-overlay" @click="handleOverlayClick">
        <div 
          class="modal-container"
          :class="[
            `modal-${size}`,
            { 'modal-fullscreen': fullscreen }
          ]"
          @click.stop
        >
          <header v-if="title || $slots.header || closable" class="modal-header">
            <slot name="header">
              <h3 v-if="title" class="modal-title">{{ title }}</h3>
            </slot>
            
            <button 
              v-if="closable"
              class="modal-close"
              @click="close"
              :aria-label="closeLabel"
            >
              ✕
            </button>
          </header>
          
          <div class="modal-body" :class="{ 'no-padding': noPadding }">
            <slot />
          </div>
          
          <footer v-if="$slots.footer" class="modal-footer">
            <slot name="footer" />
          </footer>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
import { watch, nextTick, onMounted, onUnmounted } from 'vue'

/**
 * 모달 컴포넌트 Props
 */
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: ''
  },
  size: {
    type: String,
    default: 'md',
    validator: (value) => ['sm', 'md', 'lg', 'xl'].includes(value)
  },
  closable: {
    type: Boolean,
    default: true
  },
  closeOnOverlay: {
    type: Boolean,
    default: true
  },
  closeOnEscape: {
    type: Boolean,
    default: true
  },
  closeLabel: {
    type: String,
    default: '닫기'
  },
  fullscreen: {
    type: Boolean,
    default: false
  },
  noPadding: {
    type: Boolean,
    default: false
  }
})

/**
 * 이벤트 정의
 */
const emit = defineEmits(['update:modelValue', 'close', 'open'])

/**
 * 모달 열기
 */
const open = () => {
  emit('update:modelValue', true)
  emit('open')
}

/**
 * 모달 닫기
 */
const close = () => {
  emit('update:modelValue', false)
  emit('close')
}

/**
 * 오버레이 클릭 핸들러
 */
const handleOverlayClick = () => {
  if (props.closeOnOverlay) {
    close()
  }
}

/**
 * ESC 키 핸들러
 */
const handleEscape = (event) => {
  if (event.key === 'Escape' && props.closeOnEscape && props.modelValue) {
    close()
  }
}

/**
 * 바디 스크롤 제어
 */
watch(() => props.modelValue, (newValue) => {
  nextTick(() => {
    if (newValue) {
      document.body.style.overflow = 'hidden'
    } else {
      document.body.style.overflow = ''
    }
  })
})

/**
 * 컴포넌트 마운트/언마운트 시 이벤트 리스너
 */
onMounted(() => {
  document.addEventListener('keydown', handleEscape)
})

onUnmounted(() => {
  document.removeEventListener('keydown', handleEscape)
  document.body.style.overflow = ''
})
</script>

<style scoped>
@import '../../styles/variables.css';

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: var(--bg-overlay);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: var(--z-modal);
  padding: var(--space-4);
  backdrop-filter: blur(4px);
}

.modal-container {
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-xl);
  border: 1px solid var(--border-light);
  display: flex;
  flex-direction: column;
  max-height: 90vh;
  overflow: hidden;
  position: relative;
}

/* 크기 */
.modal-sm {
  width: 100%;
  max-width: 400px;
}

.modal-md {
  width: 100%;
  max-width: 600px;
}

.modal-lg {
  width: 100%;
  max-width: 800px;
}

.modal-xl {
  width: 100%;
  max-width: 1200px;
}

.modal-fullscreen {
  width: 100vw;
  height: 100vh;
  max-width: none;
  max-height: none;
  border-radius: 0;
  margin: 0;
}

/* 헤더 */
.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--space-5) var(--space-6);
  border-bottom: 1px solid var(--border-light);
  background: var(--bg-primary);
  border-radius: var(--radius-xl) var(--radius-xl) 0 0;
}

.modal-title {
  margin: 0;
  font-size: var(--text-xl);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  line-height: 1;
}

.modal-close {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border: none;
  background: transparent;
  color: var(--text-tertiary);
  cursor: pointer;
  border-radius: var(--radius-lg);
  transition: var(--transition-fast);
  font-size: var(--text-lg);
  line-height: 1;
  margin-left: var(--space-4);
}

.modal-close:hover {
  background: var(--bg-tertiary);
  color: var(--text-secondary);
}

/* 바디 */
.modal-body {
  flex: 1;
  padding: var(--space-6);
  overflow-y: auto;
}

.modal-body.no-padding {
  padding: 0;
}

/* 푸터 */
.modal-footer {
  padding: var(--space-4) var(--space-6);
  border-top: 1px solid var(--border-light);
  background: var(--bg-secondary);
  border-radius: 0 0 var(--radius-xl) var(--radius-xl);
  display: flex;
  gap: var(--space-3);
  justify-content: flex-end;
}

/* 애니메이션 */
.modal-enter-active,
.modal-leave-active {
  transition: all var(--transition-medium);
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.modal-enter-from .modal-container,
.modal-leave-to .modal-container {
  transform: scale(0.9) translateY(-20px);
}

/* 반응형 */
@media (max-width: 640px) {
  .modal-overlay {
    padding: var(--space-2);
  }
  
  .modal-container {
    max-height: 95vh;
  }
  
  .modal-header {
    padding: var(--space-4) var(--space-5);
  }
  
  .modal-body {
    padding: var(--space-5);
  }
  
  .modal-footer {
    padding: var(--space-3) var(--space-5);
    flex-direction: column-reverse;
  }
  
  .modal-title {
    font-size: var(--text-lg);
  }
  
  .modal-sm,
  .modal-md,
  .modal-lg,
  .modal-xl {
    max-width: none;
  }
}
</style>