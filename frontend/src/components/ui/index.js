/**
 * UI 컴포넌트 모듈 내보내기
 * 재사용 가능한 UI 컴포넌트들을 중앙에서 관리
 */

// 기본 UI 컴포넌트들
export { default as Card } from './Card.vue'
export { default as Button } from './Button.vue'
export { default as Input } from './Input.vue'
export { default as Modal } from './Modal.vue'

/**
 * 전역 컴포넌트 등록을 위한 플러그인
 * Vue app에서 app.use(UIComponents)로 사용
 */
export const UIComponents = {
  install(app) {
    // 컴포넌트들을 전역으로 등록
    app.component('UiCard', () => import('./Card.vue'))
    app.component('UiButton', () => import('./Button.vue'))
    app.component('UiInput', () => import('./Input.vue'))
    app.component('UiModal', () => import('./Modal.vue'))
  }
}

/**
 * 개별 컴포넌트 등록을 위한 헬퍼
 */
export const registerUIComponents = (app, components = []) => {
  const componentMap = {
    Card: () => import('./Card.vue'),
    Button: () => import('./Button.vue'),
    Input: () => import('./Input.vue'),
    Modal: () => import('./Modal.vue')
  }

  components.forEach(name => {
    if (componentMap[name]) {
      app.component(`Ui${name}`, componentMap[name])
    }
  })
}