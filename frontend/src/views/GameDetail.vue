<!--
  게임 상세 페이지 컴포넌트
  밸런스 게임 상세 정보, 투표, 댓글 기능을 제공
-->
<template>
  <div class="game-detail">
    <!-- 데스크톱 뷰 -->
    <div v-if="!isMobile" class="desktop-view">
      <div class="container">
        <div v-if="loading" class="loading">
          <div class="loading-spinner"></div>
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
                      class="vote-fill vote-fill-a"
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
                      class="vote-fill vote-fill-b"
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
    </div>

    <!-- 모바일 뷰 -->
    <div v-else class="mobile-view">
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

// 모바일 여부 판단
const isMobile = computed(() => {
  if (typeof window === 'undefined') return false
  return window.innerWidth <= 768
})

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
@import '../styles/variables.css';

/* 데스크톱 뷰 스타일 */
.desktop-view {
  padding: var(--space-8) 0;
}

.desktop-view .container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 var(--space-4);
}

.desktop-view .loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--space-20);
  color: var(--text-secondary);
}

.desktop-view .loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid var(--primary-color);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: var(--space-4);
}

.desktop-view .game-header {
  text-align: center;
  margin-bottom: var(--space-8);
  padding: var(--space-6);
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-sm);
}

.desktop-view .game-header h1 {
  color: var(--text-primary);
  margin-bottom: var(--space-4);
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
}

.desktop-view .game-meta {
  display: flex;
  justify-content: center;
  gap: var(--space-8);
  color: var(--text-secondary);
  font-size: var(--text-sm);
}

.desktop-view .game-description {
  background: var(--bg-tertiary);
  padding: var(--space-6);
  border-radius: var(--radius-xl);
  margin-bottom: var(--space-8);
  text-align: center;
  border: 1px solid var(--border-light);
}

.desktop-view .voting-section {
  background: var(--bg-primary);
  padding: var(--space-8);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-md);
  margin-bottom: var(--space-8);
  border: 1px solid var(--border-light);
}

.desktop-view .vote-options {
  display: flex;
  gap: var(--space-8);
  align-items: center;
  margin-bottom: var(--space-8);
}

.desktop-view .vote-option {
  flex: 1;
  padding: var(--space-8);
  background: var(--bg-tertiary);
  border-radius: var(--radius-xl);
  cursor: pointer;
  transition: var(--transition-medium);
  border: 3px solid transparent;
}

.desktop-view .vote-option:hover {
  background: var(--bg-secondary);
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
}

.desktop-view .vote-option.selected {
  border-color: var(--primary-color);
  background: rgba(45, 95, 193, 0.1);
  box-shadow: var(--shadow-md);
}

.desktop-view .vote-option.voted {
  cursor: default;
}

.desktop-view .option-content h3 {
  margin-bottom: var(--space-4);
  color: var(--text-primary);
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
}

.desktop-view .option-content p {
  color: var(--text-secondary);
  font-size: var(--text-sm);
}

.desktop-view .vote-stats {
  margin-top: var(--space-6);
}

.desktop-view .vote-count {
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  color: var(--primary-color);
}

.desktop-view .vote-percentage {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: var(--space-2) 0;
}

.desktop-view .vote-bar {
  width: 100%;
  height: 8px;
  background: var(--border-medium);
  border-radius: var(--radius-sm);
  overflow: hidden;
}

.vote-fill {
  height: 100%;
  transition: width var(--transition-slow);
}

.vote-fill-a {
  background: linear-gradient(90deg, #ff6b6b, #ff8e8e);
}

.vote-fill-b {
  background: linear-gradient(90deg, #4ecdc4, #6bcf9f);
}

.desktop-view .vs-divider {
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

.desktop-view .vote-summary {
  text-align: center;
  color: var(--text-secondary);
}

.desktop-view .login-notice {
  margin-top: var(--space-4);
}

.desktop-view .login-notice a {
  color: var(--primary-color);
  text-decoration: none;
  font-weight: var(--font-medium);
}

.desktop-view .vote-info {
  margin-top: var(--space-2);
  color: var(--text-tertiary);
  font-size: var(--text-sm);
  font-style: italic;
}

.desktop-view .comments-section {
  background: var(--bg-primary);
  padding: var(--space-8);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-md);
  border: 1px solid var(--border-light);
}

.desktop-view .comments-section h3 {
  margin-bottom: var(--space-6);
  color: var(--text-primary);
  font-size: var(--text-xl);
  font-weight: var(--font-semibold);
}

.desktop-view .comment-form {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
  margin-bottom: var(--space-8);
}

.desktop-view .comment-form textarea {
  resize: vertical;
  min-height: 100px;
}

.desktop-view .comment-form button {
  align-self: flex-end;
}

.desktop-view .comments-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.desktop-view .comment-item {
  padding: var(--space-5);
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-light);
  transition: var(--transition-fast);
}

.desktop-view .comment-item:hover {
  box-shadow: var(--shadow-sm);
}

.desktop-view .comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-2);
}

.desktop-view .comment-header strong {
  color: var(--text-primary);
  font-weight: var(--font-medium);
}

.desktop-view .comment-date {
  color: var(--text-tertiary);
  font-size: var(--text-xs);
}

.desktop-view .comment-content p {
  color: var(--text-primary);
  margin: 0;
  line-height: var(--leading-normal);
}

.desktop-view .no-comments {
  text-align: center;
  color: var(--text-secondary);
  padding: var(--space-8);
}

.desktop-view .game-not-found {
  text-align: center;
  padding: var(--space-20);
}

.desktop-view .game-not-found h2 {
  color: var(--text-primary);
  margin-bottom: var(--space-4);
}

/* 게임 상호작용 섹션 */
.desktop-view .game-interactions {
  margin: var(--space-6) 0;
  padding: var(--space-4);
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-light);
}

.desktop-view .like-section {
  display: flex;
  justify-content: center;
}

.desktop-view .like-btn {
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

.desktop-view .like-btn .count {
  background: var(--bg-tertiary);
  padding: var(--space-1) var(--space-2);
  border-radius: var(--radius-full);
  font-size: var(--text-sm);
  margin-left: var(--space-1);
}

.desktop-view .like-btn:hover {
  background: var(--bg-tertiary);
  transform: translateY(-1px);
  box-shadow: var(--shadow-sm);
}

.desktop-view .like-btn.liked {
  background: var(--accent-color);
  color: var(--text-white);
  border-color: var(--accent-color);
}

.desktop-view .like-btn.liked .count {
  background: rgba(255, 255, 255, 0.2);
  color: var(--text-white);
}

.desktop-view .like-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.desktop-view .like-btn.small {
  padding: var(--space-1) var(--space-3);
  font-size: var(--text-xs);
}

.desktop-view .comment-actions {
  margin-top: var(--space-2);
  padding-top: var(--space-2);
  border-top: 1px solid var(--border-light);
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

/* 답글 버튼 스타일 */
.desktop-view .reply-btn {
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

.desktop-view .reply-btn:hover {
  background: var(--bg-tertiary);
  color: var(--text-primary);
}

/* 대댓글 작성 폼 */
.desktop-view .reply-form {
  margin-top: var(--space-3);
  padding: var(--space-3);
  background: var(--bg-tertiary);
  border-radius: var(--radius-md);
  border-left: 3px solid var(--accent-color);
}

.desktop-view .reply-form .form-control.small {
  font-size: var(--text-sm);
  padding: var(--space-2);
  margin-bottom: var(--space-2);
}

.desktop-view .reply-form-actions {
  display: flex;
  gap: var(--space-2);
}

.desktop-view .btn.small {
  padding: var(--space-1) var(--space-3);
  font-size: var(--text-xs);
}

/* 대댓글 목록 */
.desktop-view .replies-list {
  margin-top: var(--space-4);
  margin-left: var(--space-6);
  border-left: 2px solid var(--border-light);
  padding-left: var(--space-4);
}

.desktop-view .reply-item {
  background: var(--bg-tertiary);
  border-radius: var(--radius-md);
  padding: var(--space-3);
  margin-bottom: var(--space-3);
}

.desktop-view .reply-item:last-child {
  margin-bottom: 0;
}

.desktop-view .reply-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-2);
}

.desktop-view .reply-header strong {
  color: var(--text-primary);
  font-weight: var(--font-medium);
  font-size: var(--text-sm);
}

.desktop-view .reply-date {
  color: var(--text-tertiary);
  font-size: var(--text-xs);
}

.desktop-view .reply-content p {
  color: var(--text-primary);
  margin: 0;
  line-height: var(--leading-normal);
  font-size: var(--text-sm);
}

.desktop-view .reply-actions {
  margin-top: var(--space-2);
  padding-top: var(--space-2);
  border-top: 1px solid var(--border-light);
}

/* 모바일 뷰 스타일 */
.mobile-view {
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

.mobile-view .loading-spinner {
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

/* 투표 섹션 모바일 */
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

.vote-info-mobile {
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

/* 대댓글 폼 모바일 */
.reply-form-mobile {
  margin-top: 8px;
  padding: 8px;
  background: #ffffff;
  border-radius: 6px;
  border-left: 3px solid #ffd93d;
}

.reply-input-mobile {
  width: 100%;
  padding: 8px;
  border: 1px solid #e1e5e9;
  border-radius: 6px;
  background: #f8f9fa;
  font-size: 13px;
  resize: vertical;
  outline: none;
  margin-bottom: 6px;
}

.reply-input-mobile:focus {
  border-color: #ffd93d;
  background: #ffffff;
}

.reply-form-actions-mobile {
  display: flex;
  gap: 6px;
}

.reply-submit-btn {
  padding: 6px 12px;
  background: #ffd93d;
  color: #333;
  border: none;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 600;
  cursor: pointer;
}

.reply-cancel-btn {
  padding: 6px 12px;
  background: #e9ecef;
  color: #666;
  border: none;
  border-radius: 12px;
  font-size: 11px;
  cursor: pointer;
}

/* 대댓글 목록 모바일 */
.replies-list-mobile {
  margin-top: 8px;
  margin-left: 12px;
  border-left: 2px solid #e1e5e9;
  padding-left: 8px;
}

.reply-item-mobile {
  background: #ffffff;
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
  color: #333;
}

.reply-date-mobile {
  font-size: 9px;
  color: #999;
}

.reply-content-mobile p {
  font-size: 13px;
  color: #333;
  line-height: 1.3;
  margin: 0;
}

.reply-actions-mobile {
  margin-top: 6px;
  padding-top: 6px;
  border-top: 1px solid #e1e5e9;
}

/* 빈 상태 모바일 */
.no-comments-mobile {
  text-align: center;
  padding: 40px 20px;
  color: #666;
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
  color: #666;
}

.not-found-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.game-not-found-mobile h2 {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0 0 16px 0;
}

.back-btn {
  display: inline-block;
  padding: 12px 24px;
  background: #ffd93d;
  color: #333;
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