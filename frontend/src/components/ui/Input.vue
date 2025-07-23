<!--
  재사용 가능한 입력 컴포넌트
  Blind 스타일 입력 필드 UI를 제공
-->
<template>
  <div class="input-wrapper" :class="{ 'input-wrapper-error': error }">
    <label v-if="label" :for="inputId" class="input-label">
      {{ label }}
      <span v-if="required" class="input-required">*</span>
    </label>
    
    <div class="input-container">
      <span v-if="icon" class="input-icon">
        {{ icon }}
      </span>
      
      <component
        :is="type === 'textarea' ? 'textarea' : 'input'"
        :id="inputId"
        :type="type === 'textarea' ? undefined : type"
        :value="modelValue"
        :placeholder="placeholder"
        :disabled="disabled"
        :readonly="readonly"
        :maxlength="maxlength"
        :rows="rows"
        class="input-field"
        :class="{
          'input-field-icon': icon,
          'input-field-error': error
        }"
        @input="handleInput"
        @blur="handleBlur"
        @focus="handleFocus"
      />
      
      <span v-if="clearable && modelValue" class="input-clear" @click="clearInput">
        ✕
      </span>
    </div>
    
    <div v-if="error || hint || maxlength" class="input-footer">
      <span v-if="error" class="input-error">{{ error }}</span>
      <span v-else-if="hint" class="input-hint">{{ hint }}</span>
      
      <span v-if="maxlength && showCount" class="input-count">
        {{ (modelValue || '').length }} / {{ maxlength }}
      </span>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'

/**
 * 입력 컴포넌트 Props
 */
const props = defineProps({
  modelValue: {
    type: [String, Number],
    default: ''
  },
  type: {
    type: String,
    default: 'text',
    validator: (value) => ['text', 'email', 'password', 'number', 'tel', 'url', 'search', 'textarea'].includes(value)
  },
  label: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: ''
  },
  hint: {
    type: String,
    default: ''
  },
  error: {
    type: String,
    default: ''
  },
  icon: {
    type: String,
    default: ''
  },
  disabled: {
    type: Boolean,
    default: false
  },
  readonly: {
    type: Boolean,
    default: false
  },
  required: {
    type: Boolean,
    default: false
  },
  clearable: {
    type: Boolean,
    default: false
  },
  maxlength: {
    type: Number,
    default: null
  },
  showCount: {
    type: Boolean,
    default: false
  },
  rows: {
    type: Number,
    default: 3
  }
})

/**
 * 이벤트 정의
 */
const emit = defineEmits(['update:modelValue', 'blur', 'focus'])

/**
 * 유니크 ID 생성
 */
const inputId = computed(() => `input-${Math.random().toString(36).substr(2, 9)}`)

/**
 * 입력 핸들러
 */
const handleInput = (event) => {
  emit('update:modelValue', event.target.value)
}

/**
 * 블러 핸들러
 */
const handleBlur = (event) => {
  emit('blur', event)
}

/**
 * 포커스 핸들러
 */
const handleFocus = (event) => {
  emit('focus', event)
}

/**
 * 입력 값 클리어
 */
const clearInput = () => {
  emit('update:modelValue', '')
}
</script>

<style scoped>
@import '../../styles/variables.css';

.input-wrapper {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.input-wrapper-error {
  --input-border-color: var(--error-color);
  --input-focus-color: var(--error-color);
}

.input-label {
  font-size: var(--text-base);  /* 14px - 블라인드 스타일 */
  font-weight: var(--font-medium);
  color: var(--text-secondary);
  line-height: 1;
}

.input-required {
  color: var(--error-color);
  margin-left: var(--space-1);
}

.input-container {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: var(--space-3);
  z-index: 1;
  color: var(--text-tertiary);
  font-style: normal;
  pointer-events: none;
}

.input-field {
  width: 100%;
  padding: var(--space-3) var(--space-4);
  font-size: var(--text-base);  /* 14px - 블라인드 스타일 */
  border: 1px solid var(--input-border-color, var(--border-medium));
  border-radius: var(--radius-lg);
  background: var(--bg-primary);
  color: var(--text-primary);
  transition: var(--transition-fast);
  outline: none;
  font-family: var(--font-family-primary);
  line-height: var(--leading-normal);
}

.input-field::placeholder {
  color: var(--text-tertiary);
}

.input-field:focus {
  border-color: var(--input-focus-color, var(--primary-color));
  box-shadow: 0 0 0 3px rgba(45, 95, 193, 0.1);
}

.input-field:disabled {
  background: var(--bg-tertiary);
  color: var(--text-tertiary);
  cursor: not-allowed;
}

.input-field:readonly {
  background: var(--bg-secondary);
}

.input-field-icon {
  padding-left: calc(var(--space-10) + var(--space-2));
}

.input-field-error {
  border-color: var(--error-color);
}

.input-field-error:focus {
  box-shadow: 0 0 0 3px rgba(255, 68, 68, 0.1);
}

.input-clear {
  position: absolute;
  right: var(--space-3);
  color: var(--text-tertiary);
  cursor: pointer;
  padding: var(--space-1);
  border-radius: var(--radius-sm);
  transition: var(--transition-fast);
  font-size: var(--text-xs);
  line-height: 1;
}

.input-clear:hover {
  color: var(--text-secondary);
  background: var(--bg-tertiary);
}

textarea.input-field {
  resize: vertical;
  min-height: calc(var(--space-12) + var(--space-6));
}

.input-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--space-2);
}

.input-error {
  font-size: var(--text-xs);
  color: var(--error-color);
  line-height: 1;
}

.input-hint {
  font-size: var(--text-xs);
  color: var(--text-tertiary);
  line-height: 1;
}

.input-count {
  font-size: var(--text-xs);
  color: var(--text-tertiary);
  line-height: 1;
  margin-left: auto;
}

/* 반응형 */
@media (max-width: 640px) {
  .input-field {
    padding: var(--space-3);
  }
  
  .input-field-icon {
    padding-left: calc(var(--space-8) + var(--space-3));
  }
  
  .input-icon {
    left: var(--space-3);
  }
}
</style>