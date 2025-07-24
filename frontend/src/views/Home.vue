<!--
  웹 전용 홈 페이지 컴포넌트
  데스크톱/태블릿용 메인 랜딩 페이지와 인기 게임 목록을 표시
-->
<template>
  <div class="home-page">
    <!-- 히어로 배너 -->
    <section class="hero-banner">
      <div class="hero-container">
        <div class="hero-text">
          <div class="hero-icon">🍻</div>
          <h1 class="hero-title">술하재밸</h1>
          <p class="hero-subtitle">술 마실 때 하면 재밌는 밸런스 게임!</p>
          <p class="hero-description">
            친구들과 한잔하며 즐기는 딜레마! 
            지금까지 <strong>{{ totalVotes.toLocaleString() }}번</strong>의 선택이 이뤄졌어요 🥂
          </p>
          <div class="hero-buttons">
            <router-link to="/games" class="btn-main">🎮 게임 시작</router-link>
            <router-link to="/create" class="btn-outline" v-if="authStore.isLoggedIn">✨ 게임 만들기</router-link>
          </div>
        </div>
        <div class="hero-visual">
          <div class="drink-battle">
            <div class="drink-left">🍺</div>
            <div class="vs-text">VS</div>
            <div class="drink-right">🍻</div>
          </div>
        </div>
      </div>
    </section>

    <!-- 메인 콘텐츠 영역 -->
    <main class="main-content">
      <div class="container">
        <!-- 인기 게임 섹션 -->
        <section class="popular-games-section">
          <div class="section-header">
            <h2>🍻 지금 HOT한 게임</h2>
            <router-link to="/games" class="view-all-link">전체 보기 →</router-link>
          </div>
          
          <div v-if="popularGames.length > 0" class="games-container">
            <div 
              v-for="game in popularGames" 
              :key="game.id"
              class="game-item"
              @click="goToGame(game.id)"
            >
              <div class="game-content">
                <h3 class="game-title">{{ game.title }}</h3>
                <div class="game-meta">
                  <span class="meta-item">👀 {{ game.viewCount }}</span>
                  <span class="meta-item">🗳 {{ game.totalVotes }}</span>
                  <span class="meta-item">💬 {{ game.commentCount }}</span>
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
              </div>
            </div>
          </div>
          
          <div v-else class="empty-games">
            <div class="empty-icon">🎮</div>
            <h3>아직 게임이 없습니다</h3>
            <p>첫 번째 밸런스 게임을 만들어보세요!</p>
            <router-link to="/create" class="btn-main" v-if="authStore.isLoggedIn">첫 게임 만들기</router-link>
          </div>
        </section>

        <!-- 커뮤니티 통계 및 정보 -->
        <section class="community-stats">
          <div class="stats-grid">
            <div class="stat-card">
              <div class="stat-icon">🎯</div>
              <div class="stat-content">
                <h3>{{ totalGames.toLocaleString() }}</h3>
                <p>총 게임 수</p>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon">🗳️</div>
              <div class="stat-content">
                <h3>{{ totalVotes.toLocaleString() }}</h3>
                <p>총 투표 수</p>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon">👥</div>
              <div class="stat-content">
                <h3>{{ totalUsers.toLocaleString() }}</h3>
                <p>커뮤니티 멤버</p>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon">🏆</div>
              <div class="stat-content">
                <h3>매일</h3>
                <p>새로운 딜레마</p>
              </div>
            </div>
          </div>
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
 * 인기 게임 목록 조회 (웹 API)
 */
const fetchPopularGames = async () => {
  try {
    const response = await axios.get('/api/web/balance-games?sort=popular&size=6')
    popularGames.value = response.data.content
    totalGames.value = response.data.totalElements
    
    // 총 투표 수 계산
    totalVotes.value = popularGames.value.reduce((sum, game) => sum + game.totalVotes, 0)
  } catch (error) {
    console.error('인기 게임 조회 실패:', error)
    // API 실패 시 기본 더미 데이터로 페이지 표시
    totalVotes.value = 1234
    totalGames.value = 0
    popularGames.value = []
  }
}

/**
 * 사용자 수 조회 (웹 API)
 */
const fetchUserCount = async () => {
  try {
    const response = await axios.get('/api/web/users')
    totalUsers.value = response.data.length
  } catch (error) {
    console.error('사용자 수 조회 실패:', error)
    // API 실패 시 기본값
    totalUsers.value = 42
  }
}

/**
 * 투표 비율 계산
 */
const getVotePercentage = (votes, total) => {
  return total > 0 ? (votes / total) * 100 : 0
}

// 컴포넌트 마운트 시 데이터 로드
onMounted(() => {
  fetchPopularGames()
  fetchUserCount()
})
</script>

<style scoped>
/* 홈 페이지 - 완전한 웹사이트 스타일 */
.home-page {
  background: #ffffff;
  min-height: 100vh;
}

/* 히어로 배너 - 술하재밸 컨셉 */
.hero-banner {
  background: #f8fafc;
  padding: 80px 0;
  position: relative;
  display: flex;
  justify-content: center;
}

.hero-banner .hero-container {
  background: linear-gradient(135deg, #FF6B35 0%, #F7931E 50%, #FFD23F 100%);
  border-radius: 30px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.15);
  position: relative;
  overflow: hidden;
}

.hero-banner .hero-container::before {
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

.hero-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 80px;
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 60px;
  align-items: center;
  position: relative;
  z-index: 2;
  color: white;
}

.hero-text {
  text-align: left;
}

.hero-icon {
  font-size: 3rem;
  margin-bottom: 16px;
  display: block;
  filter: drop-shadow(0 4px 8px rgba(0,0,0,0.3));
}

.hero-title {
  font-size: 2.5rem;
  font-weight: 900;
  line-height: 1.2;
  margin-bottom: 16px;
  text-shadow: 0 4px 15px rgba(0,0,0,0.4);
  color: white;
}

.hero-subtitle {
  font-size: 1.2rem;
  font-weight: 500;
  margin-bottom: 16px;
  opacity: 0.95;
  line-height: 1.4;
}

.hero-description {
  font-size: 1rem;
  line-height: 1.6;
  margin-bottom: 24px;
  opacity: 0.9;
  max-width: 400px;
}

.hero-description strong {
  color: #FFE066;
  font-weight: 700;
  text-shadow: 0 2px 4px rgba(0,0,0,0.3);
}

.hero-buttons {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.btn-main {
  background: linear-gradient(135deg, #FF4081, #E91E63);
  color: white;
  padding: 14px 28px;
  border-radius: 25px;
  text-decoration: none;
  font-weight: 700;
  font-size: 1rem;
  box-shadow: 0 6px 20px rgba(255, 64, 129, 0.4);
  transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  border: none;
  cursor: pointer;
}

.btn-main:hover {
  transform: translateY(-3px);
  box-shadow: 0 10px 30px rgba(255, 64, 129, 0.6);
  background: linear-gradient(135deg, #F50057, #C2185B);
}

.btn-outline {
  background: rgba(255, 255, 255, 0.1);
  color: white;
  padding: 14px 28px;
  border: 2px solid rgba(255, 255, 255, 0.8);
  border-radius: 25px;
  text-decoration: none;
  font-weight: 600;
  font-size: 1rem;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
}

.btn-outline:hover {
  background: rgba(255, 255, 255, 0.2);
  border-color: white;
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 255, 255, 0.3);
}

/* 히어로 비주얼 - 맥주 vs 소주 컨셉 */
.hero-visual {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
}

.drink-battle {
  display: flex;
  align-items: center;
  gap: 30px;
  animation: float 3s ease-in-out infinite;
}

.drink-left, .drink-right {
  font-size: 3rem;
  filter: drop-shadow(0 8px 20px rgba(0,0,0,0.4));
  animation: sway 2s ease-in-out infinite alternate;
}

.drink-left {
  animation-delay: 0s;
}

.drink-right {
  animation-delay: 0.5s;
}

.vs-text {
  font-size: 1.5rem;
  font-weight: 900;
  color: white;
  text-shadow: 0 4px 8px rgba(0,0,0,0.5);
  background: rgba(255, 255, 255, 0.1);
  padding: 8px 16px;
  border-radius: 20px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(10px);
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-8px); }
}

@keyframes sway {
  0% { transform: rotate(-5deg) scale(1); }
  100% { transform: rotate(5deg) scale(1.05); }
}

/* 메인 콘텐츠 */
.main-content {
  padding: 50px 0;
  background: #f8fafc;
}

.container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 40px;
}

/* 인기 게임 섹션 */
.popular-games-section {
  margin-bottom: 80px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 50px;
  padding-bottom: 20px;
  border-bottom: 3px solid #e2e8f0;
}

.section-header h2 {
  font-size: 2rem;
  font-weight: 700;
  color: #1a202c;
  margin: 0;
}

.view-all-link {
  color: #FF6B35;
  text-decoration: none;
  font-weight: 600;
  font-size: 1rem;
  padding: 8px 16px;
  border: 2px solid #FF6B35;
  border-radius: 20px;
  transition: all 0.3s ease;
}

.view-all-link:hover {
  background: #FF6B35;
  color: white;
  transform: translateX(5px);
  box-shadow: 0 4px 15px rgba(255, 107, 53, 0.3);
}

/* 게임 컨테이너 */
.games-container {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 40px;
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
  margin-bottom: 20px;
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

/* 빈 게임 상태 */
.empty-games {
  text-align: center;
  padding: 80px 40px;
  background: white;
  border-radius: 15px;
  border: 2px dashed #cbd5e0;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 20px;
}

.empty-games h3 {
  font-size: 1.5rem;
  color: #2d3748;
  margin-bottom: 10px;
}

.empty-games p {
  color: #64748b;
  margin-bottom: 30px;
  font-size: 1.1rem;
}

/* 커뮤니티 통계 */
.community-stats {
  background: white;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.05);
  border: 1px solid #e2e8f0;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 32px;
}

.stat-card {
  text-align: center;
  padding: 24px 16px;
  border-radius: 12px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border: 1px solid #e2e8f0;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 35px rgba(0,0,0,0.1);
  border-color: #FF6B35;
}

.stat-icon {
  font-size: 2.4rem;
  margin-bottom: 12px;
  display: block;
}

.stat-content h3 {
  font-size: 2rem;
  font-weight: 800;
  color: #FF6B35;
  margin-bottom: 6px;
}

.stat-content p {
  font-size: 0.95rem;
  color: #64748b;
  font-weight: 500;
  margin: 0;
}

/* 반응형 디자인 */
@media (max-width: 1200px) {
  .hero-container,
  .container {
    max-width: 900px;
    padding: 0 30px;
  }
  
  .games-container {
    grid-template-columns: repeat(2, 1fr);
    gap: 25px;
  }
  
  .stats-grid {
    grid-template-columns: repeat(4, 1fr);
    gap: 25px;
  }
}

@media (max-width: 1024px) {
  .hero-container {
    grid-template-columns: 1fr;
    gap: 40px;
    text-align: center;
    padding: 0 30px;
  }
  
  .container {
    padding: 0 30px;
  }
  
  .hero-title {
    font-size: 2.8rem;
  }
  
  .games-container {
    grid-template-columns: 1fr;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .hero-banner {
    padding: 60px 0;
  }
  
  .hero-container,
  .container {
    padding: 0 20px;
  }
  
  .hero-title {
    font-size: 2.2rem;
  }
  
  .hero-subtitle {
    font-size: 1.2rem;
  }
  
  .section-header {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }
  
  .section-header h2 {
    font-size: 2rem;
  }
  
  .game-choices {
    grid-template-columns: 1fr;
    gap: 15px;
  }
  
  .vs-separator {
    order: -1;
    align-self: center;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .hero-buttons {
    flex-direction: column;
    align-items: center;
  }
  
  .btn-main,
  .btn-outline {
    width: 100%;
    max-width: 300px;
  }
}
</style>