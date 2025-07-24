<template>
  <div class="game-list-page">
    <!-- 페이지 헤더 -->
    <section class="page-header">
      <div class="header-container">
        <div class="header-content">
          <div class="header-icon">🍻</div>
          <h1 class="page-title">술하재밸 게임 목록</h1>
          <p class="page-subtitle">친구들과 함께 즐기는 모든 밸런스 게임!</p>
        </div>
      </div>
    </section>

    <!-- 메인 콘텐츠 -->
    <main class="main-content">
      <div class="container">

        <!-- 검색 및 필터 섹션 -->
        <section class="filters-section">
          <div class="filters-container">
            <div class="search-section">
              <div class="search-box">
                <input 
                  v-model="searchTerm"
                  type="text"
                  placeholder="게임 제목으로 검색..."
                  class="search-input"
                  @keyup.enter="searchGames"
                />
                <button @click="searchGames" class="search-btn">
                  <span>🔍</span>
                  검색
                </button>
              </div>
            </div>
            
            <div class="sort-section">
              <label class="sort-label">정렬:</label>
              <select v-model="sortBy" @change="changeSortBy(sortBy)" class="sort-select">
                <option v-for="option in sortOptions" :key="option.value" :value="option.value">
                  {{ option.label }}
                </option>
              </select>
            </div>
          </div>
        </section>

        <!-- 로딩 상태 -->
        <div v-if="loading" class="loading-section">
          <div class="loading-spinner"></div>
          <p class="loading-text">게임을 불러오는 중...</p>
        </div>

        <!-- 게임 목록 -->
        <section v-else-if="games.length > 0" class="games-section">
          <div class="games-grid">
            <div 
              v-for="game in games" 
              :key="game.id"
              class="game-item"
              @click="goToGame(game.id)"
            >
              <div class="game-content">
                <div class="game-header">
                  <h3 class="game-title">{{ game.title }}</h3>
                  <div class="game-meta">
                    <span class="meta-item">👀 {{ game.viewCount }}</span>
                    <span class="meta-item">🗳 {{ game.totalVotes }}</span>
                    <span class="meta-item">💬 {{ game.commentCount }}</span>
                  </div>
                </div>
                
                <div class="game-choices">
                  <div class="choice choice-a">
                    <div class="choice-text">{{ game.optionA }}</div>
                    <div class="choice-stats">
                      <div class="vote-bar">
                        <div class="vote-fill-a" :style="{ width: getVotePercentage(game.optionAVotes, game.totalVotes) + '%' }"></div>
                      </div>
                      <span class="vote-percent">{{ getVotePercentage(game.optionAVotes, game.totalVotes).toFixed(0) }}%</span>
                    </div>
                  </div>
                  
                  <div class="vs-separator">VS</div>
                  
                  <div class="choice choice-b">
                    <div class="choice-text">{{ game.optionB }}</div>
                    <div class="choice-stats">
                      <div class="vote-bar">
                        <div class="vote-fill-b" :style="{ width: getVotePercentage(game.optionBVotes, game.totalVotes) + '%' }"></div>
                      </div>
                      <span class="vote-percent">{{ getVotePercentage(game.optionBVotes, game.totalVotes).toFixed(0) }}%</span>
                    </div>
                  </div>
                </div>
                
                <div class="game-footer">
                  <div class="author-info">👤 {{ game.authorUsername }}</div>
                  <div class="date-info">📅 {{ formatDate(game.createdAt) }}</div>
                  <div class="like-info">❤️ {{ game.likeCount || 0 }}</div>
                </div>
              </div>
            </div>
          </div>
        </section>

        <!-- 빈 상태 -->
        <section v-else class="empty-section">
          <div class="empty-content">
            <div class="empty-icon">🎮</div>
            <h3 class="empty-title">게임이 없습니다</h3>
            <p class="empty-description">첫 번째 밸런스 게임을 만들어보세요!</p>
            <router-link to="/create" class="btn-create" v-if="authStore.isLoggedIn">
              게임 만들기
            </router-link>
          </div>
        </section>

        <!-- 더 보기 섹션 -->
        <section v-if="hasMore && games.length > 0" class="load-more-section">
          <button @click="loadMoreGames" class="load-more-btn" :disabled="loading">
            {{ loading ? '로딩 중...' : '더 보기' }}
          </button>
        </section>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
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
 * 게임 목록 조회 (웹 API)
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
    
    const response = await axios.get('/api/web/balance-games', { params })
    
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
 * 게임 검색 (웹 API)
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
    
    const response = await axios.get('/api/web/balance-games/search', { params })
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
  router.push(`/game/${gameId}`)
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
/* 게임 목록 페이지 - 완전한 웹사이트 스타일 */
.game-list-page {
  background: #ffffff;
  min-height: 100vh;
}

/* 페이지 헤더 */
.page-header {
  background: #f8fafc;
  padding: 80px 0;
  position: relative;
  display: flex;
  justify-content: center;
}

.page-header .header-container {
  background: linear-gradient(135deg, #FF6B35 0%, #F7931E 50%, #FFD23F 100%);
  border-radius: 30px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.15);
  position: relative;
  overflow: hidden;
}

.page-header .header-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: radial-gradient(ellipse at center, rgba(255,255,255,0.15) 0%, transparent 70%);
  pointer-events: none;
  z-index: 1;
}

.header-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 60px 80px;
  position: relative;
  z-index: 2;
  color: white;
}

.header-content {
  text-align: center;
}

.header-icon {
  font-size: 3rem;
  margin-bottom: 16px;
  display: block;
  filter: drop-shadow(0 4px 8px rgba(0,0,0,0.3));
}

.page-title {
  font-size: 2.2rem;
  font-weight: 900;
  line-height: 1.2;
  margin-bottom: 16px;
  text-shadow: 0 4px 15px rgba(0,0,0,0.4);
  color: white;
}

.page-subtitle {
  font-size: 1.1rem;
  font-weight: 500;
  opacity: 0.95;
  line-height: 1.4;
  max-width: 500px;
  margin: 0 auto;
}

/* 메인 콘텐츠 */
.main-content {
  padding: 60px 0;
  background: #f8fafc;
}

.container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 40px;
}

/* 필터 섹션 */
.filters-section {
  margin-bottom: 50px;
}

.filters-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  padding: 30px;
  border-radius: 15px;
  box-shadow: 0 5px 25px rgba(0,0,0,0.08);
  border: 1px solid #e2e8f0;
}

.search-section {
  flex: 1;
  max-width: 500px;
}

.search-box {
  display: flex;
  gap: 12px;
  align-items: center;
}

.search-input {
  flex: 1;
  padding: 12px 18px;
  border: 2px solid #e2e8f0;
  border-radius: 25px;
  font-size: 1rem;
  background: #f8fafc;
  transition: all 0.3s ease;
}

.search-input:focus {
  outline: none;
  border-color: #FF6B35;
  background: white;
  box-shadow: 0 0 0 3px rgba(255, 107, 53, 0.1);
}

.search-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: linear-gradient(135deg, #FF6B35, #F7931E);
  color: white;
  border: none;
  border-radius: 25px;
  font-weight: 600;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(255, 107, 53, 0.3);
}

.search-btn:hover {
  background: linear-gradient(135deg, #E55A2B, #E8860D);
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(255, 107, 53, 0.4);
}

.search-btn span {
  font-size: 1.1rem;
}

.sort-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.sort-label {
  font-weight: 600;
  color: #4a5568;
  font-size: 1rem;
}

.sort-select {
  padding: 10px 16px;
  border: 2px solid #e2e8f0;
  border-radius: 10px;
  font-size: 1rem;
  background: white;
  cursor: pointer;
  transition: all 0.3s ease;
  min-width: 120px;
}

.sort-select:focus {
  outline: none;
  border-color: #FF6B35;
  box-shadow: 0 0 0 3px rgba(255, 107, 53, 0.1);
}

/* 로딩 섹션 */
.loading-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 40px;
  background: white;
  border-radius: 15px;
  border: 1px solid #e2e8f0;
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 4px solid #f1f5f9;
  border-top: 4px solid #FF6B35;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 20px;
}

.loading-text {
  font-size: 1.1rem;
  color: #64748b;
  font-weight: 500;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 게임 목록 섹션 */
.games-section {
  margin-bottom: 60px;
}

.games-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 30px;
}

.game-item {
  background: white;
  border-radius: 15px;
  box-shadow: 0 5px 25px rgba(0,0,0,0.08);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  border: 1px solid #e2e8f0;
  overflow: hidden;
}

.game-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 50px rgba(0,0,0,0.15);
  border-color: #FF6B35;
}

.game-content {
  padding: 24px;
}

/* 게임 헤더 */
.game-header {
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e2e8f0;
}

.game-title {
  font-size: 1.2rem;
  font-weight: 600;
  color: #1a202c;
  margin-bottom: 12px;
  line-height: 1.3;
}

.game-meta {
  display: flex;
  gap: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 0.9rem;
  color: #64748b;
  font-weight: 500;
  background: #f1f5f9;
  padding: 5px 12px;
  border-radius: 20px;
}

/* 게임 선택지 */
.game-choices {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  gap: 20px;
  align-items: center;
  margin-bottom: 20px;
}

.choice {
  background: #f8fafc;
  border-radius: 10px;
  padding: 16px;
  border: 2px solid transparent;
  transition: all 0.3s ease;
}

.choice:hover {
  border-color: #e2e8f0;
  background: #f1f5f9;
}

.choice-a:hover {
  border-color: #ff6b6b;
  background: rgba(255, 107, 107, 0.05);
}

.choice-b:hover {
  border-color: #4ecdc4;
  background: rgba(78, 205, 196, 0.05);
}

.choice-text {
  font-weight: 600;
  color: #2d3748;
  margin-bottom: 12px;
  line-height: 1.4;
  font-size: 1rem;
}

.choice-stats {
  display: flex;
  align-items: center;
  gap: 10px;
}

.vote-bar {
  flex: 1;
  height: 8px;
  background: #e2e8f0;
  border-radius: 4px;
  overflow: hidden;
}

.vote-fill-a {
  height: 100%;
  background: linear-gradient(90deg, #ff6b6b, #ff8e8e);
  transition: width 0.5s ease;
  border-radius: 4px;
}

.vote-fill-b {
  height: 100%;
  background: linear-gradient(90deg, #4ecdc4, #6bcf9f);
  transition: width 0.5s ease;
  border-radius: 4px;
}

.vote-percent {
  font-weight: 700;
  color: #4a5568;
  font-size: 0.9rem;
  min-width: 35px;
  text-align: right;
}

.vs-separator {
  background: linear-gradient(135deg, #FF6B35, #F7931E);
  color: white;
  padding: 10px 15px;
  border-radius: 25px;
  font-weight: 800;
  font-size: 0.9rem;
  box-shadow: 0 3px 15px rgba(255, 107, 53, 0.4);
}

/* 게임 푸터 */
.game-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #e2e8f0;
  font-size: 0.85rem;
  color: #64748b;
}

.author-info,
.date-info,
.like-info {
  display: flex;
  align-items: center;
  gap: 5px;
}

/* 빈 상태 섹션 */
.empty-section {
  background: white;
  border-radius: 15px;
  border: 2px dashed #cbd5e0;
  padding: 80px 40px;
}

.empty-content {
  text-align: center;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 24px;
}

.empty-title {
  font-size: 1.5rem;
  color: #2d3748;
  margin-bottom: 12px;
  font-weight: 600;
}

.empty-description {
  color: #64748b;
  margin-bottom: 30px;
  font-size: 1.1rem;
}

.btn-create {
  display: inline-block;
  padding: 12px 24px;
  background: linear-gradient(135deg, #FF6B35, #F7931E);
  color: white;
  text-decoration: none;
  border-radius: 25px;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(255, 107, 53, 0.3);
}

.btn-create:hover {
  background: linear-gradient(135deg, #E55A2B, #E8860D);
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(255, 107, 53, 0.4);
}

/* 더 보기 섹션 */
.load-more-section {
  text-align: center;
  padding: 40px 0;
}

.load-more-btn {
  padding: 14px 32px;
  background: white;
  border: 2px solid #e2e8f0;
  border-radius: 25px;
  color: #4a5568;
  font-weight: 600;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
}

.load-more-btn:hover:not(:disabled) {
  background: #f8fafc;
  border-color: #FF6B35;
  color: #FF6B35;
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.1);
}

.load-more-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 반응형 디자인 */
@media (max-width: 1400px) {
  .header-container,
  .container {
    max-width: 1200px;
    padding: 0 40px;
  }
  
  .games-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 25px;
  }
}

@media (max-width: 1024px) {
  .header-container,
  .container {
    padding: 0 30px;
  }
  
  .page-title {
    font-size: 2rem;
  }
  
  .games-grid {
    grid-template-columns: 1fr;
  }
  
  .filters-container {
    flex-direction: column;
    gap: 20px;
    align-items: stretch;
  }
  
  .search-section {
    max-width: none;
  }
}

@media (max-width: 768px) {
  .page-header {
    padding: 40px 0;
  }
  
  .header-container,
  .container {
    padding: 0 20px;
  }
  
  .page-title {
    font-size: 1.8rem;
  }
  
  .page-subtitle {
    font-size: 1rem;
  }
  
  .game-choices {
    grid-template-columns: 1fr;
    gap: 15px;
  }
  
  .vs-separator {
    order: -1;
    align-self: center;
  }
  
  .game-footer {
    flex-direction: column;
    gap: 8px;
    align-items: center;
  }
}
</style>