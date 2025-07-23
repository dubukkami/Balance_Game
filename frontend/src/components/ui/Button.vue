<!--
  재사용 가능한 버튼 컴포넌트
  Blind 스타일 버튼 UI를 제공
-->
<template>
  <component
    :is="tag"
    class="btn"
    :class="[
      `btn-${variant}`,
      `btn-${size}`,
      {
        'btn-loading': loading,
        'btn-block': block,
        'btn-icon': icon && !$slots.default
      }
    ]"
    :disabled="disabled || loading"
    :to="to"
    :href="href"
    @click="handleClick"
  >
    <span v-if="loading" class="btn-spinner">
      <div class="spinner"></div>
    </span>
    
    <span v-if="icon && !loading" class="btn-icon-wrapper">
      {{ icon }}
    </span>
    
    <span v-if="$slots.default && !loading" class="btn-content">
      <slot />
    </span>
    
    <span v-if="$slots.default && loading" class="btn-content-loading">
      {{ loadingText || 'Loading...' }}
    </span>
  </component>
</template>

<script setup>
import { computed } from 'vue'

/**
 * 버튼 컴포넌트 Props
 */
const props = defineProps({
  variant: {
    type: String,
    default: 'primary',
    validator: (value) => ['primary', 'secondary', 'ghost', 'danger', 'success'].includes(value)
  },
  size: {
    type: String,
    default: 'md',
    validator: (value) => ['sm', 'md', 'lg'].includes(value)
  },
  disabled: {
    type: Boolean,
    default: false
  },
  loading: {
    type: Boolean,
    default: false
  },
  loadingText: {
    type: String,
    default: ''
  },
  block: {
    type: Boolean,
    default: false
  },
  icon: {
    type: String,
    default: ''
  },
  to: {
    type: [String, Object],
    default: null
  },
  href: {
    type: String,
    default: null
  }
})

/**
 * 이벤트 정의
 */
const emit = defineEmits(['click'])

/**
 * 태그 계산
 */
const tag = computed(() => {
  if (props.to) return 'router-link'
  if (props.href) return 'a'
  return 'button'
})

/**
 * 클릭 핸들러
 */
const handleClick = (event) => {
  if (!props.disabled && !props.loading) {
    emit('click', event)
  }
}
</script>

<style scoped>
@import '../../styles/variables.css';

.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-2);
  font-family: var(--font-family-primary);
  font-weight: var(--font-medium);
  border: 1px solid transparent;
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: var(--transition-fast);
  text-decoration: none;
  white-space: nowrap;
  position: relative;
  outline: none;
}

.btn:focus {
  box-shadow: 0 0 0 3px rgba(45, 95, 193, 0.2);
}

/* 크기 - Blind Style */
.btn-sm {
  padding: var(--space-2) var(--space-3);
  font-size: var(--text-xs);
  min-height: 32px;
  line-height: var(--leading-tight);
}

.btn-md {
  padding: var(--space-3) var(--space-4);
  font-size: var(--text-sm);  /* 13px - 블라인드 버튼 크기 */
  min-height: 36px;
  line-height: var(--leading-tight);
}

.btn-lg {
  padding: var(--space-4) var(--space-6);
  font-size: var(--text-base);  /* 14px */
  min-height: 44px;
  line-height: var(--leading-tight);
}

/* 변형 */
.btn-primary {
  background: var(--primary-color);
  color: var(--text-white);
  border-color: var(--primary-color);
}

.btn-primary:hover:not(:disabled) {
  background: var(--primary-hover);
  border-color: var(--primary-hover);
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

.btn-secondary {
  background: var(--bg-primary);
  color: var(--text-primary);
  border-color: var(--border-medium);
}

.btn-secondary:hover:not(:disabled) {
  background: var(--bg-tertiary);
  border-color: var(--border-dark);
  transform: translateY(-1px);
  box-shadow: var(--shadow-sm);
}

.btn-ghost {
  background: transparent;
  color: var(--text-secondary);
  border-color: transparent;
}

.btn-ghost:hover:not(:disabled) {
  background: var(--bg-tertiary);
  color: var(--text-primary);
}

.btn-danger {
  background: var(--error-color);
  color: var(--text-white);
  border-color: var(--error-color);
}

.btn-danger:hover:not(:disabled) {
  background: #cc3a3a;
  border-color: #cc3a3a;
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

.btn-success {
  background: var(--success-color);
  color: var(--text-white);
  border-color: var(--success-color);
}

.btn-success:hover:not(:disabled) {
  background: #00a043;
  border-color: #00a043;
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

/* 상태 */
.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.btn-loading {
  pointer-events: none;
}

.btn-block {
  width: 100%;
}

.btn-icon {
  aspect-ratio: 1;
  padding: var(--space-2);
}

/* 스피너 */
.btn-spinner {
  display: flex;
  align-items: center;
  justify-content: center;
}

.spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top: 2px solid currentColor;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.btn-icon-wrapper {
  font-style: normal;
  line-height: 1;
}

.btn-content,
.btn-content-loading {
  line-height: 1;
}

/* 반응형 */
@media (max-width: 640px) {
  .btn-lg {
    padding: var(--space-3) var(--space-5);
    font-size: var(--text-sm);
    min-height: 44px;
  }
}
</style>