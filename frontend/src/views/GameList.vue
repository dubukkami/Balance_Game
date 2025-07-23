<!--
  게임 목록 페이지 컴포넌트
  모든 밸런스 게임을 페이징과 정렬 기능으로 표시
-->
<template>
  <div class="game-list">
    <div class="container">
      <h1>밸런스 게임 목록</h1>
      
      <!-- 필터 및 정렬 옵션 -->
      <div class="filters">
        <div class="search-box">
          <input 
            v-model="searchTerm"
            type="text"
            placeholder="게임 제목 검색..."
            class="form-control"
            @keyup.enter="searchGames"
          />
          <button @click="searchGames" class="btn btn-primary">
            검색
          </button>
        </div>
        
        <div class="sort-options">
          <label>정렬:</label>
          <select v-model="sortBy" @change="fetchGames" class="form-control">
            <option value="latest">최신순</option>
            <option value="popular">인기순</option>
            <option value="votes">투표순</option>
          </select>
        </div>
      </div>

      <!-- 게임 목록 -->
      <div v-if="loading" class="loading">
        <p>게임을 불러오는 중...</p>
      </div>

      <div v-else-if="games.length > 0" class="games-container">
        <div 
          v-for="game in games" 
          :key="game.id"
          class="game-item"
          @click="goToGame(game.id)"
        >
          <div class="game-header">
            <h3>{{ game.title }}</h3>
            <div class="game-stats">
              <span class="stat">
                👁️ {{ game.viewCount }}
              </span>
              <span class="stat">
                🗳️ {{ game.totalVotes }}
              </span>
              <span class="stat">
                💬 {{ game.commentCount }}
              </span>
            </div>
          </div>

          <div class="game-description" v-if="game.description">
            <p>{{ game.description }}</p>
          </div>

          <div class="game-options">
            <div class="option option-a">
              <div class="option-content">
                <h4>{{ game.optionA }}</h4>
                <p v-if="game.optionADescription">{{ game.optionADescription }}</p>
              </div>
              <div class="vote-stats">
                <span class="vote-count">{{ game.optionAVotes }}표</span>
                <div class="vote-bar">
                  <div 
                    class="vote-fill"
                    :style="{ width: getVotePercentage(game.optionAVotes, game.totalVotes) + '%' }"
                  ></div>
                </div>
              </div>
            </div>

            <div class="vs-divider">VS</div>

            <div class="option option-b">
              <div class="option-content">
                <h4>{{ game.optionB }}</h4>
                <p v-if="game.optionBDescription">{{ game.optionBDescription }}</p>
              </div>
              <div class="vote-stats">
                <span class="vote-count">{{ game.optionBVotes }}표</span>
                <div class="vote-bar">
                  <div 
                    class="vote-fill"
                    :style="{ width: getVotePercentage(game.optionBVotes, game.totalVotes) + '%' }"
                  ></div>
                </div>
              </div>
            </div>
          </div>

          <div class="game-meta">
            <span>작성자: {{ game.authorUsername }}</span>
            <span>{{ formatDate(game.createdAt) }}</span>
          </div>
          
          <div class="game-stats">
            <span>조회수: {{ game.viewCount }}</span>
            <span>댓글: {{ game.commentCount }}</span>
            <span>추천: {{ game.likeCount || 0 }}</span>
          </div>
        </div>

        <!-- 페이지네이션 -->
        <div class="pagination" v-if="totalPages > 1">
          <button 
            @click="goToPage(currentPage - 1)"
            :disabled="currentPage === 0"
            class="btn btn-secondary"
          >
            이전
          </button>
          
          <span class="page-info">
            {{ currentPage + 1 }} / {{ totalPages }}
          </span>
          
          <button 
            @click="goToPage(currentPage + 1)"
            :disabled="currentPage === totalPages - 1"
            class="btn btn-secondary"
          >
            다음
          </button>
        </div>
      </div>

      <div v-else class="no-games">
        <p>게임이 없습니다.</p>
        <router-link to="/create" class="btn btn-primary">
          첫 번째 게임 만들기
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * 게임 목록 페이지 컴포넌트 로직
 */
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()

// 반응형 데이터
const games = ref([])
const loading = ref(false)
const currentPage = ref(0)
const totalPages = ref(0)
const sortBy = ref('latest')
const searchTerm = ref('')

/**
 * 게임 목록 조회
 */
const fetchGames = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: 10,
      sort: sortBy.value
    }
    
    const response = await axios.get('/api/balance-games', { params })
    games.value = response.data.content
    totalPages.value = response.data.totalPages
  } catch (error) {
    console.error('게임 목록 조회 실패:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 게임 검색
 */
const searchGames = async () => {
  if (!searchTerm.value.trim()) {
    fetchGames()
    return
  }
  
  loading.value = true
  try {
    const params = {
      title: searchTerm.value,
      page: 0,
      size: 10
    }
    
    const response = await axios.get('/api/balance-games/search', { params })
    games.value = response.data.content
    totalPages.value = response.data.totalPages
    currentPage.value = 0
  } catch (error) {
    console.error('게임 검색 실패:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 페이지 이동
 */
const goToPage = (page) => {
  if (page >= 0 && page < totalPages.value) {
    currentPage.value = page
    fetchGames()
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
  return new Date(dateString).toLocaleDateString('ko-KR')
}

// 컴포넌트 마운트 시 데이터 로드
onMounted(() => {
  fetchGames()
})
</script>

<style scoped>
@import '../styles/variables.css';

.game-list {
  padding: var(--space-8) 0;
}

.game-list h1 {
  text-align: center;
  margin-bottom: var(--space-8);
  color: var(--text-primary);
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
}

.filters {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-8);
  gap: var(--space-4);
  padding: var(--space-6);
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-light);
}

.search-box {
  display: flex;
  gap: var(--space-2);
  flex: 1;
  max-width: 400px;
}

.sort-options {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.sort-options label {
  font-weight: var(--font-medium);
  color: var(--text-secondary);
  font-size: var(--text-sm);
}

.sort-options select {
  width: 120px;
}

.loading {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--space-20);
  color: var(--text-secondary);
}

.games-container {
  display: grid;
  grid-template-columns: 1fr;
  gap: var(--space-6);
}

.game-item {
  background: var(--bg-primary);
  padding: var(--space-6);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-sm);
  cursor: pointer;
  transition: var(--transition-fast);
  border: 1px solid var(--border-light);
}

.game-item:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
  border-color: var(--border-medium);
}

.game-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-4);
}

.game-header h3 {
  color: var(--text-primary);
  margin: 0;
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
}

.game-stats {
  display: flex;
  gap: var(--space-4);
}

.stat {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  display: flex;
  align-items: center;
  gap: var(--space-1);
}

.game-description {
  margin-bottom: var(--space-6);
  color: var(--text-secondary);
  background: var(--bg-tertiary);
  padding: var(--space-3);
  border-radius: var(--radius-lg);
  border-left: 3px solid var(--primary-color);
}

.game-options {
  display: flex;
  align-items: center;
  gap: var(--space-8);
  margin-bottom: var(--space-4);
}

.option {
  flex: 1;
  padding: var(--space-5);
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-light);
  transition: var(--transition-fast);
}

.option:hover {
  background: var(--bg-tertiary);
  box-shadow: var(--shadow-sm);
}

.option-content h4 {
  margin-bottom: var(--space-2);
  color: var(--text-primary);
  font-size: var(--text-base);
  font-weight: var(--font-medium);
}

.option-content p {
  color: var(--text-secondary);
  font-size: var(--text-sm);
}

.vote-stats {
  margin-top: var(--space-4);
}

.vote-count {
  font-weight: var(--font-semibold);
  color: var(--primary-color);
  font-size: var(--text-sm);
}

.vote-bar {
  width: 100%;
  height: 4px;
  background: var(--border-medium);
  border-radius: var(--radius-sm);
  margin-top: var(--space-2);
}

.vote-fill {
  height: 100%;
  background: var(--primary-color);
  border-radius: var(--radius-sm);
  transition: width var(--transition-medium);
}

.vs-divider {
  font-weight: var(--font-bold);
  color: var(--text-tertiary);
  font-size: var(--text-lg);
  background: var(--bg-primary);
  border-radius: var(--radius-full);
  padding: var(--space-2);
  min-width: 60px;
  text-align: center;
}

.game-meta {
  display: flex;
  justify-content: space-between;
  font-size: var(--text-sm);
  color: var(--text-secondary);
  padding-top: var(--space-4);
  border-top: 1px solid var(--border-light);
}

.game-meta + .game-stats {
  display: flex;
  gap: var(--space-4);
  font-size: var(--text-xs);
  color: var(--text-tertiary);
  margin-top: var(--space-2);
  padding-top: var(--space-2);
  border-top: 1px solid var(--border-light);
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: var(--space-4);
  margin-top: var(--space-8);
  padding: var(--space-6);
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-sm);
}

.page-info {
  font-weight: var(--font-medium);
  color: var(--text-primary);
  padding: 0 var(--space-4);
}

.no-games {
  text-align: center;
  padding: var(--space-20);
  color: var(--text-secondary);
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-light);
}

@media (max-width: 768px) {
  .filters {
    flex-direction: column;
  }
  
  .search-box {
    max-width: 100%;
  }
  
  .game-options {
    flex-direction: column;
    gap: 1rem;
  }
  
  .vs-divider {
    align-self: center;
  }
  
  .game-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }
  
  .game-stats {
    flex-wrap: wrap;
  }
}
</style>