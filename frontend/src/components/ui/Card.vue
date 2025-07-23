<!--
  재사용 가능한 카드 컴포넌트
  Blind 스타일 카드 UI를 제공
-->
<template>
  <div 
    class="card" 
    :class="[
      variant,
      { 
        'card-hover': hover,
        'card-clickable': clickable,
        'card-bordered': bordered 
      }
    ]"
    @click="handleClick"
  >
    <header v-if="$slots.header" class="card-header">
      <slot name="header" />
    </header>
    
    <div class="card-body" :class="{ 'no-padding': noPadding }">
      <slot />
    </div>
    
    <footer v-if="$slots.footer" class="card-footer">
      <slot name="footer" />
    </footer>
  </div>
</template>

<script setup>
/**
 * 카드 컴포넌트 Props
 */
defineProps({
  variant: {
    type: String,
    default: 'default',
    validator: (value) => ['default', 'elevated', 'outlined'].includes(value)
  },
  hover: {
    type: Boolean,
    default: false
  },
  clickable: {
    type: Boolean,
    default: false
  },
  bordered: {
    type: Boolean,
    default: true
  },
  noPadding: {
    type: Boolean,
    default: false
  }
})

/**
 * 이벤트 정의
 */
const emit = defineEmits(['click'])

/**
 * 클릭 핸들러
 */
const handleClick = (event) => {
  emit('click', event)
}
</script>

<style scoped>
@import '../../styles/variables.css';

.card {
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  transition: var(--transition-fast);
  position: relative;
}

/* 기본 카드 */
.card.default {
  box-shadow: var(--shadow-sm);
}

/* 높은 elevation 카드 */
.card.elevated {
  box-shadow: var(--shadow-lg);
}

/* 테두리 카드 */
.card.outlined {
  border: 1px solid var(--border-medium);
  box-shadow: none;
}

/* 테두리 */
.card-bordered {
  border: 1px solid var(--border-light);
}

/* 호버 효과 */
.card-hover:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
}

/* 클릭 가능한 카드 */
.card-clickable {
  cursor: pointer;
  transition: var(--transition-fast);
}

.card-clickable:hover {
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

.card-clickable:active {
  transform: translateY(0);
}

/* 카드 헤더 */
.card-header {
  padding: var(--space-5) var(--space-6);
  border-bottom: 1px solid var(--border-light);
  border-radius: var(--radius-xl) var(--radius-xl) 0 0;
}

/* 카드 바디 */
.card-body {
  padding: var(--space-6);
}

.card-body.no-padding {
  padding: 0;
}

/* 카드 푸터 */
.card-footer {
  padding: var(--space-4) var(--space-6);
  border-top: 1px solid var(--border-light);
  background: var(--bg-secondary);
  border-radius: 0 0 var(--radius-xl) var(--radius-xl);
}

/* 반응형 */
@media (max-width: 640px) {
  .card-header {
    padding: var(--space-4) var(--space-5);
  }
  
  .card-body {
    padding: var(--space-5);
  }
  
  .card-footer {
    padding: var(--space-3) var(--space-5);
  }
}
</style>