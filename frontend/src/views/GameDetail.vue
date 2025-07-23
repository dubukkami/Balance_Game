<!--
  게임 상세 페이지 컴포넌트
  밸런스 게임 상세 정보, 투표, 댓글 기능을 제공
-->
<template>
  <div class="game-detail">
    <div class="container">
      <div v-if="loading" class="loading">
        <p>게임을 불러오는 중...</p>
      </div>

      <div v-else-if="game" class="game-content">
        <!-- 게임 헤더 -->
        <div class="game-header">
          <h1>{{ game.title }}</h1>
          <div class="game-meta">
            <span>작성자: {{ game.authorUsername }}</span>
            <span>{{ formatDate(game.createdAt) }}</span>
            <span>조회수: {{ game.viewCount }}</span>
          </div>
        </div>

        <!-- 게임 설명 -->
        <div v-if="game.description" class="game-description">
          <p>{{ game.description }}</p>
        </div>

        <!-- 게임 상호작용 섹션 -->
        <div class="game-interactions">
          <div class="like-section">
            <button 
              @click="toggleGameLike"
              class="like-btn"
              :class="{ 'liked': game.isLiked }"
              :disabled="!isLoggedIn"
            >
              <i class="icon">❤️</i>
              <span>좋아요</span>
              <span class="count">{{ game.likeCount || 0 }}</span>
            </button>
          </div>
        </div>

        <!-- 투표 섹션 -->
        <div class="voting-section">
          <div class="vote-options">
            <div 
              class="vote-option"
              :class="{ 
                'selected': userVote === 'A',
                'voted': hasVoted
              }"
              @click="vote('A')"
            >
              <div class="option-content">
                <h3>{{ game.optionA }}</h3>
                <p v-if="game.optionADescription">{{ game.optionADescription }}</p>
              </div>
              <div class="vote-stats">
                <div class="vote-count">{{ game.optionAVotes }}표</div>
                <div class="vote-percentage">
                  {{ getVotePercentage(game.optionAVotes) }}%
                </div>
                <div class="vote-bar">
                  <div 
                    class="vote-fill"
                    :style="{ width: getVotePercentage(game.optionAVotes) + '%' }"
                  ></div>
                </div>
              </div>
            </div>

            <div class="vs-divider">VS</div>

            <div 
              class="vote-option"
              :class="{ 
                'selected': userVote === 'B',
                'voted': hasVoted
              }"
              @click="vote('B')"
            >
              <div class="option-content">
                <h3>{{ game.optionB }}</h3>
                <p v-if="game.optionBDescription">{{ game.optionBDescription }}</p>
              </div>
              <div class="vote-stats">
                <div class="vote-count">{{ game.optionBVotes }}표</div>
                <div class="vote-percentage">
                  {{ getVotePercentage(game.optionBVotes) }}%
                </div>
                <div class="vote-bar">
                  <div 
                    class="vote-fill"
                    :style="{ width: getVotePercentage(game.optionBVotes) + '%' }"
                  ></div>
                </div>
              </div>
            </div>
          </div>

          <div class="vote-summary">
            <p>총 {{ game.totalVotes }}명이 투표했습니다</p>
            <p v-if="!isLoggedIn" class="login-notice">
              <router-link to="/login">로그인</router-link>하여 투표에 참여하세요!
            </p>
            <p v-else-if="hasVoted" class="vote-info">
              선택한 옵션을 다시 클릭하면 투표를 취소할 수 있습니다.
            </p>
          </div>
        </div>

        <!-- 댓글 섹션 -->
        <div class="comments-section">
          <h3>댓글 ({{ comments.length }})</h3>
          
          <!-- 댓글 작성 폼 -->
          <div v-if="isLoggedIn" class="comment-form">
            <textarea 
              v-model="newComment"
              placeholder="댓글을 입력하세요..."
              class="form-control"
              rows="3"
            ></textarea>
            <button 
              @click="submitComment"
              class="btn btn-primary"
              :disabled="!newComment.trim() || submittingComment"
            >
              {{ submittingComment ? '작성 중...' : '댓글 작성' }}
            </button>
          </div>

          <!-- 댓글 목록 -->
          <div class="comments-list">
            <div 
              v-for="comment in comments" 
              :key="comment.id"
              class="comment-item"
            >
              <div class="comment-header">
                <strong>
                  {{ comment.authorNickname || comment.authorUsername }}
                </strong>
                <span class="comment-date">{{ formatDate(comment.createdAt) }}</span>
              </div>
              <div class="comment-content">
                <p>{{ comment.content }}</p>
              </div>
              <div class="comment-actions">
                <button 
                  @click="toggleCommentLike(comment.id)"
                  class="like-btn small"
                  :class="{ 'liked': comment.isLiked }"
                  :disabled="!isLoggedIn"
                >
                  <i class="icon">❤️</i>
                  {{ comment.likeCount || 0 }}
                </button>
                <button 
                  @click="toggleReplyForm(comment.id)"
                  class="reply-btn small"
                  v-if="isLoggedIn"
                >
                  <i class="icon">💬</i>
                  답글
                </button>
              </div>
              
              <!-- 대댓글 작성 폼 -->
              <div v-if="showReplyForm[comment.id]" class="reply-form">
                <textarea 
                  v-model="replyTexts[comment.id]"
                  :placeholder="`${comment.authorNickname || comment.authorUsername}님에게 답글...`"
                  class="form-control small"
                  rows="2"
                ></textarea>
                <div class="reply-form-actions">
                  <button 
                    @click="submitReply(comment.id)"
                    class="btn btn-primary small"
                    :disabled="!replyTexts[comment.id]?.trim() || submittingReply[comment.id]"
                  >
                    {{ submittingReply[comment.id] ? '작성 중...' : '답글 작성' }}
                  </button>
                  <button 
                    @click="cancelReply(comment.id)"
                    class="btn btn-secondary small"
                  >
                    취소
                  </button>
                </div>
              </div>
              
              <!-- 대댓글 목록 -->
              <div v-if="comment.replies && comment.replies.length > 0" class="replies-list">
                <div 
                  v-for="reply in comment.replies" 
                  :key="reply.id"
                  class="reply-item"
                >
                  <div class="reply-header">
                    <strong>
                      {{ reply.authorNickname || reply.authorUsername }}
                    </strong>
                    <span class="reply-date">{{ formatDate(reply.createdAt) }}</span>
                  </div>
                  <div class="reply-content">
                    <p>{{ reply.content }}</p>
                  </div>
                  <div class="reply-actions">
                    <button 
                      @click="toggleCommentLike(reply.id)"
                      class="like-btn small"
                      :class="{ 'liked': reply.isLiked }"
                      :disabled="!isLoggedIn"
                    >
                      <i class="icon">❤️</i>
                      {{ reply.likeCount || 0 }}
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div v-if="comments.length === 0" class="no-comments">
            <p>첫 번째 댓글을 작성해보세요!</p>
          </div>
        </div>
      </div>

      <div v-else class="game-not-found">
        <h2>게임을 찾을 수 없습니다</h2>
        <router-link to="/games" class="btn btn-primary">
          게임 목록으로 돌아가기
        </router-link>
      </div>
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
 * 게임 상세 페이지 컴포넌트 로직
 */
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'
import LoginModal from '../components/LoginModal.vue'
import ToastNotification from '../components/ToastNotification.vue'

const route = useRoute()
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
  return date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 컴포넌트 마운트 시 데이터 로드
onMounted(() => {
  fetchGame()
  fetchComments()
})
</script>

<style scoped>
@import '../styles/variables.css';

.game-detail {
  padding: var(--space-8) 0;
}

.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 var(--space-4);
}

.loading {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--space-20);
  color: var(--text-secondary);
}

.game-header {
  text-align: center;
  margin-bottom: var(--space-8);
  padding: var(--space-6);
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-sm);
}

.game-header h1 {
  color: var(--text-primary);
  margin-bottom: var(--space-4);
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
}

.game-meta {
  display: flex;
  justify-content: center;
  gap: var(--space-8);
  color: var(--text-secondary);
  font-size: var(--text-sm);
}

.game-description {
  background: var(--bg-tertiary);
  padding: var(--space-6);
  border-radius: var(--radius-xl);
  margin-bottom: var(--space-8);
  text-align: center;
  border: 1px solid var(--border-light);
}

.voting-section {
  background: var(--bg-primary);
  padding: var(--space-8);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-md);
  margin-bottom: var(--space-8);
  border: 1px solid var(--border-light);
}

.vote-options {
  display: flex;
  gap: var(--space-8);
  align-items: center;
  margin-bottom: var(--space-8);
}

.vote-option {
  flex: 1;
  padding: var(--space-8);
  background: var(--bg-tertiary);
  border-radius: var(--radius-xl);
  cursor: pointer;
  transition: var(--transition-medium);
  border: 3px solid transparent;
}

.vote-option:hover {
  background: var(--bg-secondary);
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
}

.vote-option.selected {
  border-color: var(--primary-color);
  background: rgba(45, 95, 193, 0.1);
  box-shadow: var(--shadow-md);
}

.vote-option.voted {
  cursor: default;
}

.option-content h3 {
  margin-bottom: var(--space-4);
  color: var(--text-primary);
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
}

.option-content p {
  color: var(--text-secondary);
  font-size: var(--text-sm);
}

.vote-stats {
  margin-top: var(--space-6);
}

.vote-count {
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  color: var(--primary-color);
}

.vote-percentage {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: var(--space-2) 0;
}

.vote-bar {
  width: 100%;
  height: 8px;
  background: var(--border-medium);
  border-radius: var(--radius-sm);
  overflow: hidden;
}

.vote-fill {
  height: 100%;
  background: linear-gradient(90deg, var(--primary-color), var(--secondary-color));
  transition: width var(--transition-slow);
}

.vs-divider {
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
  color: var(--text-tertiary);
  text-align: center;
  min-width: 80px;
  background: var(--bg-primary);
  border-radius: var(--radius-full);
  padding: var(--space-3);
  box-shadow: var(--shadow-sm);
}

.vote-summary {
  text-align: center;
  color: var(--text-secondary);
}

.login-notice {
  margin-top: var(--space-4);
}

.login-notice a {
  color: var(--primary-color);
  text-decoration: none;
  font-weight: var(--font-medium);
}

.vote-info {
  margin-top: var(--space-2);
  color: var(--text-tertiary);
  font-size: var(--text-sm);
  font-style: italic;
}

.comments-section {
  background: var(--bg-primary);
  padding: var(--space-8);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-md);
  border: 1px solid var(--border-light);
}

.comments-section h3 {
  margin-bottom: var(--space-6);
  color: var(--text-primary);
  font-size: var(--text-xl);
  font-weight: var(--font-semibold);
}

.comment-form {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
  margin-bottom: var(--space-8);
}

.comment-form textarea {
  resize: vertical;
  min-height: 100px;
}

.comment-form button {
  align-self: flex-end;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.comment-item {
  padding: var(--space-5);
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-light);
  transition: var(--transition-fast);
}

.comment-item:hover {
  box-shadow: var(--shadow-sm);
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-2);
}

.comment-header strong {
  color: var(--text-primary);
  font-weight: var(--font-medium);
}

.comment-date {
  color: var(--text-tertiary);
  font-size: var(--text-xs);
}

.comment-content p {
  color: var(--text-primary);
  margin: 0;
  line-height: var(--leading-normal);
}

.no-comments {
  text-align: center;
  color: var(--text-secondary);
  padding: var(--space-8);
}

.game-not-found {
  text-align: center;
  padding: var(--space-20);
}

.game-not-found h2 {
  color: var(--text-primary);
  margin-bottom: var(--space-4);
}

/* 게임 상호작용 섹션 */
.game-interactions {
  margin: var(--space-6) 0;
  padding: var(--space-4);
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-light);
}

.like-section {
  display: flex;
  justify-content: center;
}

.like-btn {
  display: inline-flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-3) var(--space-6);
  background: var(--bg-primary);
  border: 2px solid var(--border-medium);
  border-radius: var(--radius-full);
  cursor: pointer;
  transition: var(--transition-fast);
  font-size: var(--text-base);
  font-weight: var(--font-medium);
}

.like-btn .count {
  background: var(--bg-tertiary);
  padding: var(--space-1) var(--space-2);
  border-radius: var(--radius-full);
  font-size: var(--text-sm);
  margin-left: var(--space-1);
}

.like-btn:hover {
  background: var(--bg-tertiary);
  transform: translateY(-1px);
  box-shadow: var(--shadow-sm);
}

.like-btn.liked {
  background: var(--accent-color);
  color: var(--text-white);
  border-color: var(--accent-color);
}

.like-btn.liked .count {
  background: rgba(255, 255, 255, 0.2);
  color: var(--text-white);
}

.like-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.like-btn.small {
  padding: var(--space-1) var(--space-3);
  font-size: var(--text-xs);
}

.comment-actions {
  margin-top: var(--space-2);
  padding-top: var(--space-2);
  border-top: 1px solid var(--border-light);
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

/* 답글 버튼 스타일 */
.reply-btn {
  display: inline-flex;
  align-items: center;
  gap: var(--space-1);
  padding: var(--space-1) var(--space-3);
  background: transparent;
  border: 1px solid var(--border-medium);
  border-radius: var(--radius-full);
  cursor: pointer;
  transition: var(--transition-fast);
  font-size: var(--text-xs);
  color: var(--text-secondary);
}

.reply-btn:hover {
  background: var(--bg-tertiary);
  color: var(--text-primary);
}

/* 대댓글 작성 폼 */
.reply-form {
  margin-top: var(--space-3);
  padding: var(--space-3);
  background: var(--bg-tertiary);
  border-radius: var(--radius-md);
  border-left: 3px solid var(--accent-color);
}

.reply-form .form-control.small {
  font-size: var(--text-sm);
  padding: var(--space-2);
  margin-bottom: var(--space-2);
}

.reply-form-actions {
  display: flex;
  gap: var(--space-2);
}

.btn.small {
  padding: var(--space-1) var(--space-3);
  font-size: var(--text-xs);
}

/* 대댓글 목록 */
.replies-list {
  margin-top: var(--space-4);
  margin-left: var(--space-6);
  border-left: 2px solid var(--border-light);
  padding-left: var(--space-4);
}

.reply-item {
  background: var(--bg-tertiary);
  border-radius: var(--radius-md);
  padding: var(--space-3);
  margin-bottom: var(--space-3);
}

.reply-item:last-child {
  margin-bottom: 0;
}

.reply-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-2);
}

.reply-header strong {
  color: var(--text-primary);
  font-weight: var(--font-medium);
  font-size: var(--text-sm);
}

.reply-date {
  color: var(--text-tertiary);
  font-size: var(--text-xs);
}

.reply-content p {
  color: var(--text-primary);
  margin: 0;
  line-height: var(--leading-normal);
  font-size: var(--text-sm);
}

.reply-actions {
  margin-top: var(--space-2);
  padding-top: var(--space-2);
  border-top: 1px solid var(--border-light);
}

@media (max-width: 768px) {
  .game-meta {
    flex-direction: column;
    gap: 0.5rem;
  }
  
  .vote-options {
    flex-direction: column;
    gap: 1rem;
  }
  
  .vs-divider {
    transform: rotate(90deg);
    margin: 1rem 0;
  }
  
  .vote-option {
    padding: 1.5rem;
  }
  
  .comment-form button {
    align-self: stretch;
  }
}
</style>