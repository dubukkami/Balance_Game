<!--
  홈 페이지 컴포넌트
  메인 랜딩 페이지와 인기 게임 목록을 표시
-->
<template>
  <div class="home">
    <div class="container">
      <!-- 헤로 섹션 -->
      <section class="hero">
        <h1 class="hero-title">밸런스 게임 커뮤니티</h1>
        <p class="hero-subtitle">
          어려운 선택, 재미있는 토론! 
          다양한 밸런스 게임에 참여하고 당신의 선택을 공유하세요.
        </p>
        <div class="hero-buttons">
          <UiButton size="lg" variant="primary" to="/games">
            밸런스 게임 둘러보기
          </UiButton>
          <UiButton size="lg" variant="secondary" to="/create">
            게임 만들기
          </UiButton>
        </div>
      </section>

      <!-- 통계 섹션 -->
      <section class="stats">
        <UiCard class="stat-card" variant="elevated">
          <div class="stat-number">{{ totalGames.toLocaleString() }}</div>
          <div class="stat-label">총 게임 수</div>
          <div class="stat-icon">🎮</div>
        </UiCard>
        <UiCard class="stat-card" variant="elevated">
          <div class="stat-number">{{ totalVotes.toLocaleString() }}</div>
          <div class="stat-label">총 투표 수</div>
          <div class="stat-icon">🗳️</div>
        </UiCard>
        <UiCard class="stat-card" variant="elevated">
          <div class="stat-number">{{ totalUsers.toLocaleString() }}</div>
          <div class="stat-label">총 사용자 수</div>
          <div class="stat-icon">👥</div>
        </UiCard>
      </section>

      <!-- 인기 게임 섹션 -->
      <section class="popular-games">
        <h2>인기 게임</h2>
        <div class="games-grid" v-if="popularGames.length > 0">
          <UiCard 
            v-for="game in popularGames" 
            :key="game.id"
            class="game-card"
            hover
            clickable
            @click="goToGame(game.id)"
          >
            <h3>{{ game.title }}</h3>
            <div class="game-options">
              <div class="option">
                <span class="option-text">{{ game.optionA }}</span>
                <span class="vote-count">{{ game.optionAVotes }}표</span>
              </div>
              <div class="vs">VS</div>
              <div class="option">
                <span class="option-text">{{ game.optionB }}</span>
                <span class="vote-count">{{ game.optionBVotes }}표</span>
              </div>
            </div>
            <div class="game-meta">
              <span>조회수: {{ game.viewCount }}</span>
              <span>댓글: {{ game.commentCount }}</span>
            </div>
          </UiCard>
        </div>
        <div v-else class="no-games">
          <p>아직 게임이 없습니다. 첫 번째 게임을 만들어보세요!</p>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
/**
 * 홈 페이지 컴포넌트 로직
 */
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { Card as UiCard, Button as UiButton } from '../components/ui'

const router = useRouter()

// 반응형 데이터
const popularGames = ref([])
const totalGames = ref(0)
const totalVotes = ref(0)
const totalUsers = ref(0)

/**
 * 게임 상세 페이지로 이동
 * @param {number} gameId - 게임 ID
 */
const goToGame = (gameId) => {
  router.push(`/game/${gameId}`)
}

/**
 * 인기 게임 목록 조회
 */
const fetchPopularGames = async () => {
  try {
    const response = await axios.get('/api/balance-games?sort=popular&size=6')
    popularGames.value = response.data.content
    totalGames.value = response.data.totalElements
    
    // 총 투표 수 계산
    totalVotes.value = popularGames.value.reduce((sum, game) => sum + game.totalVotes, 0)
  } catch (error) {
    console.error('인기 게임 조회 실패:', error)
  }
}

/**
 * 사용자 수 조회
 */
const fetchUserCount = async () => {
  try {
    const response = await axios.get('/api/users')
    totalUsers.value = response.data.length
  } catch (error) {
    console.error('사용자 수 조회 실패:', error)
  }
}

// 컴포넌트 마운트 시 데이터 로드
onMounted(() => {
  fetchPopularGames()
  fetchUserCount()
})
</script>

<style scoped>
@import '../styles/variables.css';

.home {
  padding: var(--space-8) 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 var(--space-4);
}

.hero {
  text-align: center;
  padding: var(--space-20) var(--space-8);
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
  color: var(--text-white);
  border-radius: var(--radius-xl);
  margin-bottom: var(--space-12);
  box-shadow: var(--shadow-lg);
}

.hero-title {
  font-size: var(--text-4xl);
  margin-bottom: var(--space-4);
  font-weight: var(--font-bold);
  line-height: var(--leading-tight);
}

.hero-subtitle {
  font-size: var(--text-lg);
  margin-bottom: var(--space-8);
  opacity: 0.95;
  line-height: var(--leading-normal);
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.hero-buttons {
  display: flex;
  gap: var(--space-4);
  justify-content: center;
  flex-wrap: wrap;
}

.stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: var(--space-6);
  margin-bottom: var(--space-12);
}

.stat-card {
  text-align: center;
  padding: var(--space-8);
  position: relative;
  transition: var(--transition-fast);
}

.stat-number {
  font-size: var(--text-4xl);
  color: var(--primary-color);
  margin-bottom: var(--space-2);
  font-weight: var(--font-bold);
  line-height: 1;
}

.stat-label {
  font-size: var(--text-base);
  color: var(--text-secondary);
  font-weight: var(--font-medium);
  margin-bottom: var(--space-2);
}

.stat-icon {
  font-size: var(--text-2xl);
  opacity: 0.7;
  margin-top: var(--space-2);
}

.popular-games h2 {
  text-align: center;
  margin-bottom: var(--space-8);
  color: var(--text-primary);
  font-size: var(--text-2xl);
  font-weight: var(--font-semibold);
}

.games-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: var(--space-6);
}

.game-card {
  padding: var(--space-6);
}

.game-card h3 {
  margin-bottom: var(--space-4);
  color: var(--text-primary);
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  line-height: var(--leading-tight);
}

.game-options {
  display: flex;
  align-items: center;
  margin-bottom: var(--space-4);
  gap: var(--space-3);
}

.option {
  flex: 1;
  text-align: center;
  padding: var(--space-4);
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-light);
  transition: var(--transition-fast);
}

.option:hover {
  background: var(--bg-tertiary);
  box-shadow: var(--shadow-sm);
}

.option-text {
  display: block;
  font-weight: var(--font-medium);
  margin-bottom: var(--space-2);
  color: var(--text-primary);
  font-size: var(--text-sm);
}

.vote-count {
  color: var(--primary-color);
  font-size: var(--text-xs);
  font-weight: var(--font-semibold);
}

.vs {
  font-weight: var(--font-bold);
  color: var(--text-tertiary);
  background: var(--bg-primary);
  border-radius: var(--radius-full);
  padding: var(--space-2);
  min-width: 40px;
  text-align: center;
  box-shadow: var(--shadow-sm);
}

.game-meta {
  display: flex;
  justify-content: space-between;
  font-size: var(--text-xs);
  color: var(--text-tertiary);
  padding-top: var(--space-3);
  border-top: 1px solid var(--border-light);
}

.no-games {
  text-align: center;
  padding: var(--space-20);
  color: var(--text-secondary);
  background: var(--bg-secondary);
  border-radius: var(--radius-xl);
  border: 1px solid var(--border-light);
}

@media (max-width: 768px) {
  .container {
    padding: 0 var(--space-4);
  }
  
  .hero {
    padding: var(--space-16) var(--space-6);
    margin-bottom: var(--space-8);
  }
  
  .hero-title {
    font-size: var(--text-3xl);
  }
  
  .hero-subtitle {
    font-size: var(--text-base);
    margin-bottom: var(--space-6);
  }
  
  .hero-buttons {
    flex-direction: column;
    align-items: center;
    gap: var(--space-3);
  }
  
  .stats {
    grid-template-columns: 1fr;
    gap: var(--space-4);
    margin-bottom: var(--space-8);
  }
  
  .stat-card {
    padding: var(--space-6);
  }
  
  .stat-number {
    font-size: var(--text-3xl);
  }
  
  .games-grid {
    grid-template-columns: 1fr;
    gap: var(--space-4);
  }
  
  .game-options {
    flex-direction: column;
    gap: var(--space-2);
  }
  
  .vs {
    margin: var(--space-2) 0;
  }
}
</style>