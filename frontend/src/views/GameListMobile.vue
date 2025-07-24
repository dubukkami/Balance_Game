<!--
  모바일 전용 게임 목록 페이지 컴포넌트
  "술하재밸" 테마의 모바일 앱 스타일 게임 목록
-->
<template>
  <div class="game-list-mobile">
    <!-- 검색 및 필터 -->
    <div class="filter-section">
      <div class="search-container">
        <input 
          v-model="searchTerm"
          type="text"
          placeholder="게임 검색..."
          class="search-input"
          @keyup.enter="searchGames"
        />
        <button @click="searchGames" class="search-btn">🔍</button>
      </div>
      
      <div class="sort-tabs">
        <button 
          v-for="option in sortOptions" 
          :key="option.value"
          @click="changeSortBy(option.value)"
          :class="['sort-tab', { active: sortBy === option.value }]"
        >
          {{ option.label }}
        </button>
      </div>
    </div>

    <!-- 로딩 -->
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>게임을 불러오는 중...</p>
    </div>

    <!-- 게임 목록 -->
    <div v-else-if="games.length > 0" class="games-list">
      <div 
        v-for="game in games" 
        :key="game.id"
        class="game-card"
        @click="goToGame(game.id)"
      >
        <!-- 게임 헤더 -->
        <div class="card-header">
          <h3 class="game-title">{{ game.title }}</h3>
          <div class="stats-row">
            <span class="stat-item">
              <span class="stat-icon">👁️</span>
              <span class="stat-value">{{ game.viewCount }}</span>
            </span>
            <span class="stat-item">
              <span class="stat-icon">🗳️</span>
              <span class="stat-value">{{ game.totalVotes }}</span>
            </span>
            <span class="stat-item">
              <span class="stat-icon">💬</span>
              <span class="stat-value">{{ game.commentCount }}</span>
            </span>
          </div>
        </div>

        <!-- 게임 옵션 (가로형 컴팩트) -->
        <div class="game-options-compact">
          <div class="option-compact option-a">
            <div class="option-content-compact">
              <span class="option-label">{{ game.optionA }}</span>
              <span class="vote-percent">{{ getVotePercentage(game.optionAVotes, game.totalVotes).toFixed(0) }}%</span>
            </div>
            <div class="vote-bar-compact">
              <div 
                class="vote-fill-a"
                :style="{ width: getVotePercentage(game.optionAVotes, game.totalVotes) + '%' }"
              ></div>
            </div>
          </div>

          <div class="vs-divider-compact">VS</div>

          <div class="option-compact option-b">
            <div class="option-content-compact">
              <span class="option-label">{{ game.optionB }}</span>
              <span class="vote-percent">{{ getVotePercentage(game.optionBVotes, game.totalVotes).toFixed(0) }}%</span>
            </div>
            <div class="vote-bar-compact">
              <div 
                class="vote-fill-b"
                :style="{ width: getVotePercentage(game.optionBVotes, game.totalVotes) + '%' }"
              ></div>
            </div>
          </div>
        </div>

        <!-- 게임 메타 정보 -->
        <div class="card-footer">
          <div class="author-info">
            <span class="author-name">{{ game.authorUsername }}</span>
            <span class="created-date">{{ formatDate(game.createdAt) }}</span>
          </div>
          <div class="like-info">
            <span class="like-icon">❤️</span>
            <span class="like-count">{{ game.likeCount || 0 }}</span>
          </div>
        </div>
      </div>

      <!-- 무한 스크롤 로딩 -->
      <div v-if="hasMore" class="load-more" @click="loadMoreGames">
        <button class="load-more-btn">더 보기</button>
      </div>
    </div>

    <!-- 빈 상태 -->
    <div v-else class="empty-state">
      <div class="empty-icon">🎮</div>
      <h3>게임이 없어요</h3>
      <p>첫 번째 밸런스 게임을 만들어보세요!</p>
      <router-link to="/mobile/create" class="create-btn" v-if="authStore.isLoggedIn">
        게임 만들기
      </router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import axios from 'axios'

const router = useRouter()
const authStore = useAuthStore()

// 반응형 데이터
const games = ref([])
const loading = ref(false)
const currentPage = ref(0)
const sortBy = ref('latest')
const searchTerm = ref('')
const hasMore = ref(true)

// 정렬 옵션
const sortOptions = [
  { value: 'latest', label: '최신순' },
  { value: 'popular', label: '인기순' },
  { value: 'votes', label: '투표순' }
]

/**
 * 게임 목록 조회 (모바일 API)
 */
const fetchGames = async (reset = true) => {
  if (loading.value) return
  
  loading.value = true
  try {
    const params = {
      page: reset ? 0 : currentPage.value,
      size: 10,
      sort: sortBy.value
    }
    
    const response = await axios.get('/api/mobile/balance-games', { params })
    
    if (reset) {
      games.value = response.data.content
      currentPage.value = 0
    } else {
      games.value.push(...response.data.content)
    }
    
    hasMore.value = !response.data.last
    currentPage.value = response.data.number
  } catch (error) {
    console.error('게임 목록 조회 실패:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 정렬 변경
 */
const changeSortBy = (newSort) => {
  sortBy.value = newSort
  fetchGames(true)
}

/**
 * 더 많은 게임 로드
 */
const loadMoreGames = () => {
  if (hasMore.value && !loading.value) {
    currentPage.value += 1
    fetchGames(false)
  }
}

/**
 * 게임 검색 (모바일 API)
 */
const searchGames = async () => {
  if (!searchTerm.value.trim()) {
    fetchGames(true)
    return
  }
  
  loading.value = true
  try {
    const params = {
      title: searchTerm.value,
      page: 0,
      size: 10
    }
    
    const response = await axios.get('/api/mobile/balance-games/search', { params })
    games.value = response.data.content
    hasMore.value = !response.data.last
    currentPage.value = 0
  } catch (error) {
    console.error('게임 검색 실패:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 게임 상세 페이지로 이동
 */
const goToGame = (gameId) => {
  router.push(`/mobile/game/${gameId}`)
}

/**
 * 투표 비율 계산
 */
const getVotePercentage = (votes, total) => {
  return total > 0 ? (votes / total) * 100 : 0
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
  fetchGames(true)
})
</script>

<style scoped>
/* 모바일 뷰 스타일 */
.game-list-mobile {
  background: #f5f5f5;
  min-height: 100vh;
}

/* 필터 섹션 */
.filter-section {
  background: #ffffff;
  padding: 16px;
  border-bottom: 1px solid #e1e5e9;
  position: sticky;
  top: 60px;
  z-index: 10;
}

.search-container {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.search-input {
  flex: 1;
  padding: 10px 12px;
  border: 1px solid #e1e5e9;
  border-radius: 20px;
  background: #f8f9fa;
  font-size: 14px;
  outline: none;
}

.search-input:focus {
  border-color: #ffd93d;
  background: #ffffff;
}

.search-btn {
  width: 40px;
  height: 40px;
  border: none;
  background: #ffd93d;
  border-radius: 50%;
  font-size: 16px;
  cursor: pointer;
  transition: transform 0.2s;
}

.search-btn:hover {
  transform: scale(1.05);
}

.sort-tabs {
  display: flex;
  gap: 4px;
}

.sort-tab {
  flex: 1;
  padding: 8px 16px;
  border: none;
  background: #f8f9fa;
  color: #666;
  border-radius: 16px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.sort-tab.active {
  background: #ffd93d;
  color: #333;
}

.sort-tab:hover:not(.active) {
  background: #e9ecef;
}

/* 로딩 */
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

/* 게임 리스트 */
.games-list {
  padding: 0 12px 12px 12px;
}

.game-card {
  background: #ffffff;
  margin-bottom: 8px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: transform 0.2s;
}

.game-card:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

/* 카드 헤더 */
.card-header {
  padding: 12px 16px 8px 16px;
}

.game-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin: 0 0 6px 0;
  line-height: 1.3;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.stats-row {
  display: flex;
  gap: 12px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #666;
}

.stat-icon {
  font-size: 13px;
}

.stat-value {
  font-weight: 500;
}

/* 게임 옵션 - 컴팩트 가로형 */
.game-options-compact {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: #f8f9fa;
  margin: 0 16px;
  border-radius: 8px;
}

.option-compact {
  flex: 1;
  min-width: 0;
}

.option-content-compact {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.option-label {
  font-size: 13px;
  font-weight: 500;
  color: #333;
  line-height: 1.2;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 100px;
}

.vote-percent {
  font-size: 11px;
  font-weight: 600;
  color: #666;
  margin-left: 4px;
}

.vote-bar-compact {
  height: 6px;
  background: #e9ecef;
  border-radius: 3px;
  overflow: hidden;
}

.vote-fill-a {
  height: 100%;
  background: linear-gradient(90deg, #ff6b6b, #ff8e8e);
  border-radius: 3px;
  transition: width 0.3s ease;
}

.vote-fill-b {
  height: 100%;
  background: linear-gradient(90deg, #4ecdc4, #6bcf9f);
  border-radius: 3px;
  transition: width 0.3s ease;
}

.vs-divider-compact {
  font-size: 10px;
  font-weight: 700;
  color: #999;
  background: #ffffff;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  border: 1px solid #e9ecef;
}

/* 카드 푸터 */
.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 16px 10px 16px;
  background: #fafafa;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.author-name {
  font-size: 11px;
  font-weight: 500;
  color: #333;
}

.created-date {
  font-size: 10px;
  color: #999;
}

.like-info {
  display: flex;
  align-items: center;
  gap: 3px;
}

.like-icon {
  font-size: 11px;
}

.like-count {
  font-size: 11px;
  font-weight: 500;
  color: #666;
}

/* 더 보기 버튼 */
.load-more {
  padding: 20px;
  text-align: center;
}

.load-more-btn {
  padding: 12px 32px;
  background: #ffffff;
  border: 1px solid #e1e5e9;
  border-radius: 20px;
  color: #666;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.load-more-btn:hover {
  background: #ffd93d;
  border-color: #ffd93d;
  color: #333;
}

/* 빈 상태 */
.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: #666;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-state h3 {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 8px 0;
  color: #333;
}

.empty-state p {
  font-size: 14px;
  color: #999;
  margin: 0 0 24px 0;
}

.create-btn {
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

.create-btn:hover {
  transform: translateY(-1px);
}
</style>