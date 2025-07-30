<!--
  SNS 공유 버튼 컴포넌트
  카카오톡, 인스타그램, 트위터, 링크 복사 기능 제공
-->
<template>
  <div class="share-container">
    <!-- 공유 토글 버튼 -->
    <button @click="toggleShare" class="share-toggle-btn" title="공유하기">
      <svg class="share-icon" viewBox="0 0 24 24" fill="currentColor">
        <path d="M18 16.08c-.76 0-1.44.3-1.96.77L8.91 12.7c.05-.23.09-.46.09-.7s-.04-.47-.09-.7l7.05-4.11c.54.5 1.25.81 2.04.81 1.66 0 3-1.34 3-3s-1.34-3-3-3-3 1.34-3 3c0 .24.04.47.09.7L8.04 9.81C7.5 9.31 6.79 9 6 9c-1.66 0-3 1.34-3 3s1.34 3 3 3c.79 0 1.5-.31 2.04-.81l7.12 4.16c-.05.21-.08.43-.08.65 0 1.61 1.31 2.92 2.92 2.92s2.92-1.31 2.92-2.92-1.31-2.92-2.92-2.92z"/>
      </svg>
    </button>

    <!-- 공유 옵션들 -->
    <transition name="share-dropdown">
      <div v-if="showShareOptions" class="share-dropdown">
        <div class="share-options">
          <!-- 카카오톡 공유 -->
          <button @click="shareToKakao" class="share-option kakao" title="카카오톡으로 공유">
            <svg viewBox="0 0 24 24" fill="currentColor">
              <path d="M12 3C6.477 3 2 6.477 2 11.5c0 3.518 2.246 6.5 5.5 7.5v2.5l3-2c.5.1 1 .1 1.5.1 5.523 0 10-3.477 10-8.5S17.523 3 12 3z"/>
            </svg>
          </button>
          
          <!-- 인스타그램 공유 -->
          <button @click="shareToInstagram" class="share-option instagram" title="인스타그램으로 공유">
            <svg viewBox="0 0 24 24" fill="currentColor">
              <path d="M12 2.163c3.204 0 3.584.012 4.85.07 3.252.148 4.771 1.691 4.919 4.919.058 1.265.069 1.645.069 4.849 0 3.205-.012 3.584-.069 4.849-.149 3.225-1.664 4.771-4.919 4.919-1.266.058-1.644.07-4.85.07-3.204 0-3.584-.012-4.849-.07-3.26-.149-4.771-1.699-4.919-4.92-.058-1.265-.07-1.644-.07-4.849 0-3.204.013-3.583.07-4.849.149-3.227 1.664-4.771 4.919-4.919 1.266-.057 1.645-.069 4.849-.069zm0-2.163c-3.259 0-3.667.014-4.947.072-4.358.2-6.78 2.618-6.98 6.98-.059 1.281-.073 1.689-.073 4.948 0 3.259.014 3.668.072 4.948.2 4.358 2.618 6.78 6.98 6.98 1.281.058 1.689.072 4.948.072 3.259 0 3.668-.014 4.948-.072 4.354-.2 6.782-2.618 6.979-6.98.059-1.28.073-1.689.073-4.948 0-3.259-.014-3.667-.072-4.947-.196-4.354-2.617-6.78-6.979-6.98-1.281-.059-1.69-.073-4.949-.073zm0 5.838c-3.403 0-6.162 2.759-6.162 6.162s2.759 6.163 6.162 6.163 6.162-2.759 6.162-6.163c0-3.403-2.759-6.162-6.162-6.162zm0 10.162c-2.209 0-4-1.79-4-4 0-2.209 1.791-4 4-4s4 1.791 4 4c0 2.21-1.791 4-4 4zm6.406-11.845c-.796 0-1.441.645-1.441 1.44s.645 1.44 1.441 1.44c.795 0 1.439-.645 1.439-1.44s-.644-1.44-1.439-1.44z"/>
            </svg>
          </button>
          
          <!-- 트위터 공유 -->
          <button @click="shareToTwitter" class="share-option twitter" title="트위터로 공유">
            <svg viewBox="0 0 24 24" fill="currentColor">
              <path d="M23.953 4.57a10 10 0 01-2.825.775 4.958 4.958 0 002.163-2.723c-.951.555-2.005.959-3.127 1.184a4.92 4.92 0 00-8.384 4.482C7.69 8.095 4.067 6.13 1.64 3.162a4.822 4.822 0 00-.666 2.475c0 1.71.87 3.213 2.188 4.096a4.904 4.904 0 01-2.228-.616v.06a4.923 4.923 0 003.946 4.827 4.996 4.996 0 01-2.212.085 4.936 4.936 0 004.604 3.417 9.867 9.867 0 01-6.102 2.105c-.39 0-.779-.023-1.17-.067a13.995 13.995 0 007.557 2.209c9.053 0 13.998-7.496 13.998-13.985 0-.21 0-.42-.015-.63A9.935 9.935 0 0024 4.59z"/>
            </svg>
          </button>
          
          <!-- 링크 복사 -->
          <button @click="copyLink" class="share-option link" title="링크 복사">
            <svg viewBox="0 0 24 24" fill="currentColor">
              <path d="M16 1H4c-1.1 0-2 .9-2 2v14h2V3h12V1zm3 4H8c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h11c1.1 0 2-.9 2-2V7c0-1.1-.9-2-2-2zm0 16H8V7h11v14z"/>
            </svg>
          </button>
        </div>
      </div>
    </transition>
    
    <!-- 복사 완료 메시지 -->
    <transition name="fade">
      <div v-if="showCopyMessage" class="copy-message">
        링크가 복사되었습니다!
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const props = defineProps({
  game: {
    type: Object,
    required: true
  }
})

const showCopyMessage = ref(false)
const showShareOptions = ref(false)

// 공유 옵션 토글
const toggleShare = () => {
  showShareOptions.value = !showShareOptions.value
}

// 외부 클릭시 닫기
const handleClickOutside = (event) => {
  if (!event.target.closest('.share-container')) {
    showShareOptions.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

// 컴포넌트 언마운트시 이벤트 리스너 제거
import { onUnmounted } from 'vue'
onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})

// 공유할 URL과 텍스트 생성
const getShareUrl = () => {
  return `${window.location.origin}/games/${props.game.id}`
}

const getShareText = () => {
  const optionAVotes = props.game.optionAVotes || 0
  const optionBVotes = props.game.optionBVotes || 0
  const totalVotes = optionAVotes + optionBVotes
  
  return `🍻 술하재밸 - 밸런스 게임

"${props.game.title}"

🔴 ${props.game.optionA} (${totalVotes > 0 ? Math.round(optionAVotes / totalVotes * 100) : 50}%)
🔵 ${props.game.optionB} (${totalVotes > 0 ? Math.round(optionBVotes / totalVotes * 100) : 50}%)

당신의 선택은?`
}

// 카카오 SDK 초기화
onMounted(() => {
  // 카카오 SDK 로드
  if (!window.Kakao) {
    const script = document.createElement('script')
    script.src = 'https://developers.kakao.com/sdk/js/kakao.js'
    script.onload = () => {
      if (window.Kakao && !window.Kakao.isInitialized()) {
        // 카카오 앱 키 설정 (환경변수로 관리)
        window.Kakao.init(import.meta.env.VITE_KAKAO_JS_KEY || 'your-kakao-js-key')
      }
    }
    document.head.appendChild(script)
  }
})

// 카카오톡 공유
const shareToKakao = () => {
  if (!window.Kakao) {
    alert('카카오톡 공유 기능을 사용할 수 없습니다.')
    return
  }
  
  showShareOptions.value = false
  window.Kakao.Share.sendDefault({
    objectType: 'feed',
    content: {
      title: props.game.title,
      description: `🔴 ${props.game.optionA} VS 🔵 ${props.game.optionB}`,
      imageUrl: 'https://balancegame-delta.vercel.app/images/og-image.png', // OG 이미지 경로
      link: {
        mobileWebUrl: getShareUrl(),
        webUrl: getShareUrl(),
      },
    },
    buttons: [
      {
        title: '게임 참여하기',
        link: {
          mobileWebUrl: getShareUrl(),
          webUrl: getShareUrl(),
        },
      },
    ],
  })
}

// 인스타그램 공유 (웹사이트로 이동)
const shareToInstagram = () => {
  showShareOptions.value = false
  // 인스타그램 웹사이트로 이동
  window.open('https://www.instagram.com/', '_blank')
}

// 트위터 공유
const shareToTwitter = () => {
  const url = encodeURIComponent(getShareUrl())
  const text = encodeURIComponent(getShareText())
  showShareOptions.value = false
  window.open(
    `https://twitter.com/intent/tweet?url=${url}&text=${text}`,
    'twitter-share',
    'width=600,height=400'
  )
}

// 링크 복사
const copyLink = async () => {
  try {
    await navigator.clipboard.writeText(getShareUrl())
    showShareOptions.value = false
    showCopyMessage.value = true
    setTimeout(() => {
      showCopyMessage.value = false
    }, 2000)
  } catch (err) {
    // 폴백: 구형 브라우저 지원
    const textArea = document.createElement('textarea')
    textArea.value = getShareUrl()
    textArea.style.position = 'fixed'
    textArea.style.left = '-999999px'
    document.body.appendChild(textArea)
    textArea.select()
    document.execCommand('copy')
    document.body.removeChild(textArea)
    
    showShareOptions.value = false
    showCopyMessage.value = true
    setTimeout(() => {
      showCopyMessage.value = false
    }, 2000)
  }
}
</script>

<style scoped>
.share-container {
  position: relative;
  display: inline-block;
}

.share-toggle-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border: none;
  border-radius: 50%;
  background: var(--bg-secondary, #f8f9fa);
  color: var(--text-secondary, #666);
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.share-toggle-btn:hover {
  background: var(--primary-color, #2d5fc1);
  color: white;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(45, 95, 193, 0.3);
}

.share-icon {
  width: 18px;
  height: 18px;
}

.share-dropdown {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 0.5rem;
  z-index: 1000;
}

.share-options {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  padding: 0.75rem;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  border: 1px solid var(--border-light, #e9ecef);
  min-width: 48px;
}

.share-option {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border: none;
  border-radius: 8px;
  background: var(--bg-tertiary, #f8f9fa);
  cursor: pointer;
  transition: all 0.2s ease;
}

.share-option:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.share-option svg {
  width: 18px;
  height: 18px;
}

/* 각 SNS별 색상 */
.share-option.kakao {
  color: #391B1B;
}

.share-option.kakao:hover {
  background: #FEE500;
  color: #391B1B;
}

.share-option.instagram {
  color: #E4405F;
}

.share-option.instagram:hover {
  background: linear-gradient(45deg, #f09433 0%,#e6683c 25%,#dc2743 50%,#cc2366 75%,#bc1888 100%);
  color: white;
}

.share-option.twitter {
  color: #1DA1F2;
}

.share-option.twitter:hover {
  background: #1DA1F2;
  color: white;
}

.share-option.link {
  color: #6c757d;
}

.share-option.link:hover {
  background: #6c757d;
  color: white;
}

/* 복사 완료 메시지 */
.copy-message {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: rgba(0, 0, 0, 0.85);
  color: white;
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  font-size: 0.85rem;
  white-space: nowrap;
  z-index: 9999;
  pointer-events: none;
}

/* 트랜지션 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.share-dropdown-enter-active {
  transition: all 0.2s ease;
}

.share-dropdown-leave-active {
  transition: all 0.15s ease;
}

.share-dropdown-enter-from {
  opacity: 0;
  transform: translateY(-10px) scale(0.95);
}

.share-dropdown-leave-to {
  opacity: 0;
  transform: translateY(-5px) scale(0.98);
}

/* 모바일 반응형 */
@media (max-width: 768px) {
  .share-dropdown {
    right: -8px;
  }
  
  .share-options {
    padding: 0.5rem;
    gap: 0.4rem;
  }
  
  .share-option {
    width: 32px;
    height: 32px;
  }
  
  .share-option svg {
    width: 16px;
    height: 16px;
  }
}

/* 초소형 화면 */
@media (max-width: 360px) {
  .share-toggle-btn {
    width: 36px;
    height: 36px;
  }
  
  .share-icon {
    width: 16px;
    height: 16px;
  }
  
  .share-option {
    width: 30px;
    height: 30px;
  }
  
  .share-option svg {
    width: 14px;
    height: 14px;
  }
}
</style>