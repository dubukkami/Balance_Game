<!--
  모바일 게임 상세 페이지 컴포넌트
  밸런스 게임 상세 정보, 투표, 댓글 기능을 제공 (모바일 최적화)
-->
<template>
  <div class="game-detail-mobile">
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>게임을 불러오는 중...</p>
    </div>

    <div v-else-if="game" class="game-content-mobile">
      <!-- 게임 헤더 모바일 -->
      <div class="game-header-mobile">
        <h1 class="game-title-mobile">{{ game.title }}</h1>
        <div class="game-meta-mobile">
          <div class="meta-left">
            <span class="author">{{ game.authorUsername }}</span>
            <span class="date">{{ formatDate(game.createdAt) }}</span>
          </div>
          <div class="meta-right">
            <span class="view-count">👁️ {{ game.viewCount }}</span>
          </div>
        </div>
      </div>

      <!-- 게임 설명 모바일 -->
      <div v-if="game.description" class="game-description-mobile">
        <p>{{ game.description }}</p>
      </div>

      <!-- 투표 섹션 모바일 -->
      <div class="voting-section-mobile">
        <div class="vote-options-mobile">
          <div 
            class="vote-option-mobile"
            :class="{ 
              'selected': userVote === 'A',
              'voted': hasVoted
            }"
            @click="vote('A')"
          >
            <div class="option-content-mobile">
              <h3>{{ game.optionA }}</h3>
              <p v-if="game.optionADescription" class="option-desc">{{ game.optionADescription }}</p>
            </div>
            <div class="vote-stats-mobile">
              <div class="vote-info-mobile">
                <span class="vote-count-mobile">{{ game.optionAVotes }}표</span>
                <span class="vote-percentage-mobile">{{ getVotePercentage(game.optionAVotes) }}%</span>
              </div>
              <div class="vote-bar-mobile">
                <div 
                  class="vote-fill-a"
                  :style="{ width: getVotePercentage(game.optionAVotes) + '%' }"
                ></div>
              </div>
            </div>
          </div>

          <div class="vs-divider-mobile">VS</div>

          <div 
            class="vote-option-mobile"
            :class="{ 
              'selected': userVote === 'B',
              'voted': hasVoted
            }"
            @click="vote('B')"
          >
            <div class="option-content-mobile">
              <h3>{{ game.optionB }}</h3>
              <p v-if="game.optionBDescription" class="option-desc">{{ game.optionBDescription }}</p>
            </div>
            <div class="vote-stats-mobile">
              <div class="vote-info-mobile">
                <span class="vote-count-mobile">{{ game.optionBVotes }}표</span>
                <span class="vote-percentage-mobile">{{ getVotePercentage(game.optionBVotes) }}%</span>
              </div>
              <div class="vote-bar-mobile">
                <div 
                  class="vote-fill-b"
                  :style="{ width: getVotePercentage(game.optionBVotes) + '%' }"
                ></div>
              </div>
            </div>
          </div>
        </div>

        <div class="vote-summary-mobile">
          <p class="total-votes">총 {{ game.totalVotes }}명이 투표</p>
          <p v-if="!isLoggedIn" class="login-notice-mobile">
            <router-link to="/login">로그인</router-link>하여 투표 참여!
          </p>
          <p v-else-if="hasVoted" class="vote-info-mobile">
            다시 클릭하면 투표 취소 가능
          </p>
        </div>
      </div>

      <!-- 상호작용 바 -->
      <div class="interaction-bar">
        <button 
          @click="toggleGameLike"
          class="interaction-btn"
          :class="{ 'liked': game.isLiked }"
          :disabled="!isLoggedIn"
        >
          <span class="icon">❤️</span>
          <span class="count">{{ game.likeCount || 0 }}</span>
        </button>
        <div class="divider"></div>
        <div class="comment-count">
          <span class="icon">💬</span>
          <span class="count">{{ comments.length }}</span>
        </div>
        <div class="divider"></div>
        <button @click="shareGame" class="interaction-btn share-btn">
          <span class="icon">📤</span>
          <span class="text">공유</span>
        </button>
      </div>

      <!-- 댓글 섹션 모바일 -->
      <div class="comments-section-mobile">
        <h3 class="comments-title">💬 댓글 {{ comments.length }}</h3>
        
        <!-- 댓글 작성 폼 -->
        <div v-if="isLoggedIn" class="comment-form-mobile">
          <textarea 
            v-model="newComment"
            placeholder="댓글을 입력하세요..."
            class="comment-input-mobile"
            rows="3"
          ></textarea>
          <button 
            @click="submitComment"
            class="comment-submit-btn"
            :disabled="!newComment.trim() || submittingComment"
          >
            {{ submittingComment ? '작성 중...' : '댓글 작성' }}
          </button>
        </div>

        <!-- 댓글 목록 -->
        <div class="comments-list-mobile">
          <div 
            v-for="comment in comments" 
            :key="comment.id"
            class="comment-item-mobile"
          >
            <div class="comment-header-mobile">
              <strong class="comment-author">
                {{ comment.authorNickname || comment.authorUsername }}
              </strong>
              <span class="comment-date-mobile">{{ formatDate(comment.createdAt) }}</span>
            </div>
            <div class="comment-content-mobile">
              <p>{{ comment.content }}</p>
            </div>
            <div class="comment-actions-mobile">
              <button 
                @click="toggleCommentLike(comment.id)"
                class="action-btn"
                :class="{ 'liked': comment.isLiked }"
                :disabled="!isLoggedIn"
              >
                <span class="icon">❤️</span>
                <span class="count">{{ comment.likeCount || 0 }}</span>
              </button>
              <button 
                @click="toggleReplyForm(comment.id)"
                class="action-btn"
                v-if="isLoggedIn"
              >
                <span class="icon">💬</span>
                <span class="text">답글</span>
              </button>
            </div>
            
            <!-- 대댓글 작성 폼 -->
            <div v-if="showReplyForm[comment.id]" class="reply-form-mobile">
              <textarea 
                v-model="replyTexts[comment.id]"
                :placeholder="`${comment.authorNickname || comment.authorUsername}님에게 답글...`"
                class="reply-input-mobile"
                rows="2"
              ></textarea>
              <div class="reply-form-actions-mobile">
                <button 
                  @click="submitReply(comment.id)"
                  class="reply-submit-btn"
                  :disabled="!replyTexts[comment.id]?.trim() || submittingReply[comment.id]"
                >
                  {{ submittingReply[comment.id] ? '작성 중...' : '답글 작성' }}
                </button>
                <button 
                  @click="cancelReply(comment.id)"
                  class="reply-cancel-btn"
                >
                  취소
                </button>
              </div>
            </div>
            
            <!-- 대댓글 목록 -->
            <div v-if="comment.replies && comment.replies.length > 0" class="replies-list-mobile">
              <div 
                v-for="reply in comment.replies" 
                :key="reply.id"
                class="reply-item-mobile"
              >
                <div class="reply-header-mobile">
                  <strong class="reply-author">
                    {{ reply.authorNickname || reply.authorUsername }}
                  </strong>
                  <span class="reply-date-mobile">{{ formatDate(reply.createdAt) }}</span>
                </div>
                <div class="reply-content-mobile">
                  <p>{{ reply.content }}</p>
                </div>
                <div class="reply-actions-mobile">
                  <button 
                    @click="toggleCommentLike(reply.id)"
                    class="action-btn"
                    :class="{ 'liked': reply.isLiked }"
                    :disabled="!isLoggedIn"
                  >
                    <span class="icon">❤️</span>
                    <span class="count">{{ reply.likeCount || 0 }}</span>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div v-if="comments.length === 0" class="no-comments-mobile">
          <div class="empty-icon">💬</div>
          <p>첫 번째 댓글을 작성해보세요!</p>
        </div>
      </div>
    </div>

    <div v-else class="game-not-found-mobile">
      <div class="not-found-icon">🎮</div>
      <h2>게임을 찾을 수 없어요</h2>
      <router-link to="/games" class="back-btn">
        게임 목록으로 돌아가기
      </router-link>
    </div>

    <!-- 로그인 모달 -->
    <LoginModal 
      :isVisible="showLoginModal" 
      :message="loginModalMessage"
      @close="closeLoginModal"
    />

    <!-- 토스트 알림 -->
    <ToastNotification
      :isVisible="showToast"
      :message="toastMessage"
      :type="toastType"
      @close="closeToast"
    />
  </div>
</template>

<script setup>
/**
 * 모바일 게임 상세 페이지 컴포넌트 로직
 */
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import axios from 'axios'
import LoginModal from '../components/LoginModal.vue'
import ToastNotification from '../components/ToastNotification.vue'

const route = useRoute()
const authStore = useAuthStore()
const gameId = route.params.id

// 반응형 데이터
const game = ref(null)
const comments = ref([])
const userVote = ref('')
const loading = ref(true)
const newComment = ref('')
const submittingComment = ref(false)

// 대댓글 관련 데이터
const showReplyForm = ref({})
const replyTexts = ref({})
const submittingReply = ref({})

// 로그인 모달 관련 데이터
const showLoginModal = ref(false)
const loginModalMessage = ref('')

// 토스트 알림 관련 데이터
const showToast = ref(false)
const toastMessage = ref('')
const toastType = ref('info')

/**
 * 로그인 상태 확인
 */
const isLoggedIn = computed(() => {
  const user = localStorage.getItem('user')
  return user && user !== 'null'
})

/**
 * 현재 사용자 정보
 */
const currentUser = computed(() => {
  try {
    return JSON.parse(localStorage.getItem('user') || '{}')
  } catch {
    return {}
  }
})

/**
 * 투표 여부 확인
 */
const hasVoted = computed(() => {
  return userVote.value !== ''
})

/**
 * 게임 정보 조회
 */
const fetchGame = async () => {
  try {
    const response = await axios.get(`/api/balance-games/${gameId}`)
    game.value = response.data
    
    // 사용자 투표 확인
    if (isLoggedIn.value) {
      await checkUserVote()
    }
  } catch (error) {
    console.error('게임 조회 실패:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 사용자 투표 확인
 */
const checkUserVote = async () => {
  if (!isLoggedIn.value) return
  
  try {
    const response = await axios.get(`/api/votes/user/${currentUser.value.id}/game/${gameId}`)
    userVote.value = response.data.selectedOption
  } catch (error) {
    // 투표하지 않은 경우 404 에러 발생
    if (error.response?.status !== 404) {
      console.error('사용자 투표 확인 실패:', error)
    }
  }
}

/**
 * 댓글 목록 조회 (대댓글 포함)
 */
const fetchComments = async () => {
  try {
    const params = {}
    if (isLoggedIn.value) {
      params.userId = currentUser.value.id
    }
    
    const response = await axios.get(`/api/comments/game/${gameId}/list`, { params })
    comments.value = response.data
  } catch (error) {
    console.error('댓글 조회 실패:', error)
  }
}

/**
 * 로그인 모달 표시
 */
const showLoginPrompt = (message) => {
  loginModalMessage.value = message
  showLoginModal.value = true
}

/**
 * 로그인 모달 닫기
 */
const closeLoginModal = () => {
  showLoginModal.value = false
}

/**
 * 토스트 알림 표시
 */
const showToastMessage = (message, type = 'info') => {
  toastMessage.value = message
  toastType.value = type
  showToast.value = true
}

/**
 * 토스트 알림 닫기
 */
const closeToast = () => {
  showToast.value = false
}

/**
 * 투표 처리
 */
const vote = async (option) => {
  if (!isLoggedIn.value) {
    showLoginPrompt('투표에 참여하려면 로그인이 필요합니다.')
    return
  }

  try {
    const voteData = {
      selectedOption: option,
      balanceGameId: parseInt(gameId)
    }

    const response = await axios.post('/api/votes', voteData, {
      params: { userId: currentUser.value.id }
    })

    // 응답에 따른 처리
    const responseMessage = response.data.message
    
    if (responseMessage === 'vote_cancelled') {
      userVote.value = ''
      showToastMessage('투표가 취소되었습니다.', 'info')
    } else if (responseMessage === 'vote_updated') {
      userVote.value = option
      showToastMessage('투표가 변경되었습니다.', 'success')
    } else if (responseMessage === 'vote_created') {
      userVote.value = option
      showToastMessage('투표가 완료되었습니다.', 'success')
    }

    // 투표 후 게임 정보 새로고침
    await fetchGame()
    
  } catch (error) {
    console.error('투표 실패:', error)
    showToastMessage('투표 중 오류가 발생했습니다.', 'error')
  }
}

/**
 * 게임 정보 새로고침 (조회수 증가 없이)
 */
const refreshGameInfo = async () => {
  try {
    const response = await axios.get(`/api/balance-games/${gameId}/info`)
    game.value = response.data
  } catch (error) {
    console.error('게임 정보 새로고침 실패:', error)
  }
}

/**
 * 게임 추천/추천취소
 */
const toggleGameLike = async () => {
  if (!isLoggedIn.value) {
    showLoginPrompt('게임에 좋아요를 누르려면 로그인이 필요합니다.')
    return
  }

  try {
    await axios.post(`/api/balance-games/${gameId}/like`, null, {
      params: { userId: currentUser.value.id }
    })

    // 게임 정보 새로고침 (조회수 증가 없이)
    await refreshGameInfo()
    
  } catch (error) {
    console.error('추천 처리 실패:', error)
    showToastMessage('추천 처리 중 오류가 발생했습니다.', 'error')
  }
}

/**
 * 게임 공유
 */
const shareGame = async () => {
  try {
    if (navigator.share) {
      // 모바일 네이티브 공유 API 사용
      await navigator.share({
        title: game.value.title,
        text: `${game.value.optionA} VS ${game.value.optionB} - 당신의 선택은?`,
        url: window.location.href
      })
    } else {
      // 폴백: 링크 복사
      await navigator.clipboard.writeText(window.location.href)
      showToastMessage('링크가 복사되었습니다!', 'success')
    }
  } catch (error) {
    console.error('공유 실패:', error)
    // 에러 시 링크 복사 시도
    try {
      await navigator.clipboard.writeText(window.location.href)
      showToastMessage('링크가 복사되었습니다!', 'success')
    } catch (clipboardError) {
      showToastMessage('공유에 실패했습니다.', 'error')
    }
  }
}

/**
 * 댓글 추천/추천취소
 */
const toggleCommentLike = async (commentId) => {
  if (!isLoggedIn.value) {
    showLoginPrompt('댓글에 좋아요를 누르려면 로그인이 필요합니다.')
    return
  }

  try {
    await axios.post(`/api/comments/${commentId}/like`, null, {
      params: { userId: currentUser.value.id }
    })

    // 댓글 목록 새로고침
    await fetchComments()
    
  } catch (error) {
    console.error('댓글 추천 처리 실패:', error)
    showToastMessage('추천 처리 중 오류가 발생했습니다.', 'error')
  }
}

/**
 * 댓글 작성
 */
const submitComment = async () => {
  if (!newComment.value.trim()) return

  submittingComment.value = true
  
  try {
    const commentData = {
      content: newComment.value,
      balanceGameId: parseInt(gameId),
    }

    await axios.post('/api/comments', commentData, {
      params: { authorId: currentUser.value.id }
    })

    newComment.value = ''
    showToastMessage('댓글이 작성되었습니다.', 'success')
    await fetchComments()
    
  } catch (error) {
    console.error('댓글 작성 실패:', error)
    showToastMessage('댓글 작성 중 오류가 발생했습니다.', 'error')
  } finally {
    submittingComment.value = false
  }
}

/**
 * 대댓글 폼 표시/숨김 토글
 */
const toggleReplyForm = (commentId) => {
  showReplyForm.value[commentId] = !showReplyForm.value[commentId]
  if (!showReplyForm.value[commentId]) {
    replyTexts.value[commentId] = ''
  }
}

/**
 * 대댓글 취소
 */
const cancelReply = (commentId) => {
  showReplyForm.value[commentId] = false
  replyTexts.value[commentId] = ''
}

/**
 * 대댓글 작성
 */
const submitReply = async (parentCommentId) => {
  const replyText = replyTexts.value[parentCommentId]
  if (!replyText?.trim()) return

  submittingReply.value[parentCommentId] = true
  
  try {
    const replyData = {
      content: replyText,
      balanceGameId: parseInt(gameId),
      parentCommentId: parentCommentId
    }

    await axios.post('/api/comments', replyData, {
      params: { authorId: currentUser.value.id }
    })

    replyTexts.value[parentCommentId] = ''
    showReplyForm.value[parentCommentId] = false
    showToastMessage('답글이 작성되었습니다.', 'success')
    await fetchComments()
    
  } catch (error) {
    console.error('답글 작성 실패:', error)
    showToastMessage('답글 작성 중 오류가 발생했습니다.', 'error')
  } finally {
    submittingReply.value[parentCommentId] = false
  }
}

/**
 * 투표 비율 계산
 */
const getVotePercentage = (votes) => {
  if (!game.value || game.value.totalVotes === 0) return 0
  return Math.round((votes / game.value.totalVotes) * 100)
}

/**
 * 날짜 포맷팅
 */
const formatDate = (dateString) => {
  const date = new Date(dateString)
  const now = new Date()
  const diffTime = Math.abs(now - date)
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
  
  if (diffDays === 1) return '오늘'
  if (diffDays === 2) return '어제'
  if (diffDays <= 7) return `${diffDays}일 전`
  return date.toLocaleDateString('ko-KR', { month: 'short', day: 'numeric' })
}

// 컴포넌트 마운트 시 데이터 로드
onMounted(() => {
  fetchGame()
  fetchComments()
})
</script>

<style scoped>
/* 모바일 게임 상세 페이지 스타일 */
.game-detail-mobile {
  background: #f5f5f5;
  min-height: 100vh;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #666;
}

.loading-spinner {
  width: 32px;
  height: 32px;
  border: 3px solid #f3f3f3;
  border-top: 3px solid #ffd93d;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 게임 헤더 모바일 */
.game-header-mobile {
  background: #ffffff;
  padding: 16px;
  border-bottom: 1px solid #e1e5e9;
}

.game-title-mobile {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px 0;
  line-height: 1.3;
}

.game-meta-mobile {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #666;
}

.meta-left {
  display: flex;
  gap: 8px;
}

/* 게임 설명 모바일 */
.game-description-mobile {
  background: #ffffff;
  padding: 16px;
  margin-bottom: 8px;
  font-size: 14px;
  color: #333;
  border-bottom: 1px solid #e1e5e9;
}

/* 투표 섹션 모바일 - 원본 GameDetail.vue 모바일 뷰와 동일 */
.voting-section-mobile {
  background: #ffffff;
  padding: 16px;
  margin-bottom: 8px;
  border-bottom: 1px solid #e1e5e9;
}

.vote-options-mobile {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 16px;
}

.vote-option-mobile {
  padding: 16px;
  background: #f8f9fa;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  border: 2px solid transparent;
}

.vote-option-mobile:hover {
  background: #e9ecef;
}

.vote-option-mobile.selected {
  border-color: #ffd93d;
  background: rgba(255, 217, 61, 0.1);
}

.option-content-mobile h3 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 4px 0;
}

.option-desc {
  font-size: 13px;
  color: #666;
  margin: 4px 0 0 0;
}

.vote-stats-mobile {
  margin-top: 12px;
}

.vote-info-mobile {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.vote-count-mobile {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.vote-percentage-mobile {
  font-size: 18px;
  font-weight: 700;
  color: #ffd93d;
}

.vote-bar-mobile {
  height: 6px;
  background: #e9ecef;
  border-radius: 3px;
  overflow: hidden;
}

.vote-fill-a {
  height: 100%;
  background: linear-gradient(90deg, #ff6b6b, #ff8e8e);
  border-radius: 2px;
  transition: width 0.3s ease;
}

.vote-fill-b {
  height: 100%;
  background: linear-gradient(90deg, #4ecdc4, #6bcf9f);
  border-radius: 2px;
  transition: width 0.3s ease;
}

.vs-divider-mobile {
  text-align: center;
  font-size: 12px;
  font-weight: 700;
  color: #999;
  background: #ffffff;
  border-radius: 50%;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
  border: 1px solid #e9ecef;
}

.vote-summary-mobile {
  text-align: center;
  font-size: 14px;
  color: #666;
}

.total-votes {
  margin: 0 0 8px 0;
  font-weight: 500;
}

.login-notice-mobile {
  margin: 0;
  font-size: 12px;
}

.login-notice-mobile a {
  color: #ffd93d;
  text-decoration: none;
  font-weight: 600;
}

.vote-summary-mobile .vote-info-mobile {
  margin: 0;
  font-size: 12px;
  color: #999;
}

/* 상호작용 바 */
.interaction-bar {
  background: #ffffff;
  padding: 12px 16px;
  display: flex;
  align-items: center;
  border-bottom: 1px solid #e1e5e9;
}

.interaction-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  background: #f8f9fa;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 14px;
}

.interaction-btn:hover {
  background: #e9ecef;
}

.interaction-btn.liked {
  background: #ffd93d;
  color: #333;
}

.interaction-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.divider {
  width: 1px;
  height: 20px;
  background: #e1e5e9;
  margin: 0 12px;
}

.comment-count {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #666;
}

/* 댓글 섹션 모바일 */
.comments-section-mobile {
  background: #ffffff;
  padding: 16px;
}

.comments-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 16px 0;
}

.comment-form-mobile {
  margin-bottom: 20px;
}

.comment-input-mobile {
  width: 100%;
  padding: 12px;
  border: 1px solid #e1e5e9;
  border-radius: 8px;
  background: #f8f9fa;
  font-size: 14px;
  resize: vertical;
  outline: none;
  margin-bottom: 8px;
  box-sizing: border-box;
}

.comment-input-mobile:focus {
  border-color: #ffd93d;
  background: #ffffff;
}

.comment-submit-btn {
  padding: 10px 16px;
  background: #ffd93d;
  color: #333;
  border: none;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.comment-submit-btn:hover {
  transform: translateY(-1px);
}

.comment-submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 댓글 목록 모바일 */
.comments-list-mobile {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.comment-item-mobile {
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
}

.comment-header-mobile {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.comment-author {
  font-size: 13px;
  font-weight: 600;
  color: #333;
}

.comment-date-mobile {
  font-size: 10px;
  color: #999;
}

.comment-content-mobile p {
  font-size: 14px;
  color: #333;
  line-height: 1.4;
  margin: 0;
}

.comment-actions-mobile {
  display: flex;
  gap: 8px;
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px solid #e1e5e9;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  background: none;
  border: 1px solid #e1e5e9;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 11px;
  color: #666;
}

.action-btn:hover {
  background: #f8f9fa;
}

.action-btn.liked {
  background: #ffd93d;
  border-color: #ffd93d;
  color: #333;
}

.action-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 대댓글 폼 모바일 */
.reply-form-mobile {
  margin-top: 8px;
  padding: 8px;
  background: var(--bg-primary);
  border-radius: 6px;
  border-left: 3px solid var(--accent-yellow);
}

.reply-input-mobile {
  width: 100%;
  padding: 8px;
  border: 1px solid var(--border-light);
  border-radius: 6px;
  background: var(--bg-secondary);
  font-size: 13px;
  resize: vertical;
  outline: none;
  margin-bottom: 6px;
  box-sizing: border-box;
}

.reply-input-mobile:focus {
  border-color: var(--accent-yellow);
  background: var(--bg-primary);
}

.reply-form-actions-mobile {
  display: flex;
  gap: 6px;
}

.reply-submit-btn {
  padding: 6px 12px;
  background: var(--accent-yellow);
  color: var(--text-primary);
  border: none;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 600;
  cursor: pointer;
}

.reply-submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.reply-cancel-btn {
  padding: 6px 12px;
  background: var(--gray-200);
  color: var(--text-secondary);
  border: none;
  border-radius: 12px;
  font-size: 11px;
  cursor: pointer;
}

/* 대댓글 목록 모바일 */
.replies-list-mobile {
  margin-top: 8px;
  margin-left: 12px;
  border-left: 2px solid var(--border-light);
  padding-left: 8px;
}

.reply-item-mobile {
  background: var(--bg-primary);
  border-radius: 6px;
  padding: 8px;
  margin-bottom: 6px;
}

.reply-header-mobile {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.reply-author {
  font-size: 12px;
  font-weight: 600;
  color: var(--text-primary);
}

.reply-date-mobile {
  font-size: 9px;
  color: var(--text-tertiary);
}

.reply-content-mobile p {
  font-size: 13px;
  color: var(--text-primary);
  line-height: 1.3;
  margin: 0;
}

.reply-actions-mobile {
  margin-top: 6px;
  padding-top: 6px;
  border-top: 1px solid var(--border-light);
}

/* 빈 상태 모바일 */
.no-comments-mobile {
  text-align: center;
  padding: 40px 20px;
  color: var(--text-secondary);
}

.empty-icon {
  font-size: 32px;
  margin-bottom: 12px;
}

.no-comments-mobile p {
  font-size: 14px;
  margin: 0;
}

/* 게임 없음 모바일 */
.game-not-found-mobile {
  text-align: center;
  padding: 60px 20px;
  color: var(--text-secondary);
}

.not-found-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.game-not-found-mobile h2 {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 16px 0;
}

.back-btn {
  display: inline-block;
  padding: 12px 24px;
  background: var(--accent-yellow);
  color: var(--text-primary);
  border-radius: 20px;
  text-decoration: none;
  font-size: 14px;
  font-weight: 600;
  transition: transform 0.2s;
}

.back-btn:hover {
  transform: translateY(-1px);
}
</style>