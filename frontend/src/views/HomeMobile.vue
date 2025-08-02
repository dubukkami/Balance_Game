<!--
  모바일 전용 홈 페이지 컴포넌트
  모바일 앱 스타일의 "술하재밸" 테마 홈 페이지
-->
<template>
  <div class="home-mobile">
    <!-- 헤로 섹션 -->
    <section class="hero-mobile">
      <div class="hero-content">
        <div class="hero-emoji">🍺🆚🍻</div>
        <h1 class="hero-title-mobile">술하재밸</h1>
        <p class="hero-subtitle-mobile">
          술 마실 때 하면 재밌는<br>
          밸런스 게임 커뮤니티
        </p>
        <div class="hero-buttons-mobile">
          <router-link to="/mobile/games" class="hero-btn primary">
            게임 둘러보기
          </router-link>
          <router-link to="/mobile/create" class="hero-btn secondary" v-if="authStore.isLoggedIn">
            게임 만들기
          </router-link>
        </div>
      </div>
    </section>

    <!-- 통계 섹션 -->
    <section class="stats-mobile">
      <div class="stat-row">
        <div class="stat-item">
          <div class="stat-number">{{ totalGames.toLocaleString() }}</div>
          <div class="stat-label">🎮 게임</div>
        </div>
        <div class="stat-item">
          <div class="stat-number">{{ totalVotes.toLocaleString() }}</div>
          <div class="stat-label">🗳️ 투표</div>
        </div>
        <div class="stat-item">
          <div class="stat-number">{{ totalUsers.toLocaleString() }}</div>
          <div class="stat-label">👥 사용자</div>
        </div>
      </div>
    </section>

    <!-- 인기 게임 섹션 -->
    <section class="popular-games-mobile">
      <div class="section-header">
        <h2 class="section-title">🔥 인기 게임</h2>
        <router-link to="/mobile/games" class="see-all-btn">전체보기</router-link>
      </div>
      
      <!-- 베스트 탭 메뉴 (모바일) -->
      <div class="best-tabs-mobile">
        <button 
          v-for="tab in bestTabs" 
          :key="tab.key"
          @click="activeTab = tab.key; fetchBestGames()"
          :class="['tab-button-mobile', { active: activeTab === tab.key }]"
        >
          {{ tab.icon }}
          <span>{{ tab.label }}</span>
        </button>
      </div>
      
      <div class="games-list-mobile" v-if="popularGames.length > 0">
        <div 
          v-for="game in popularGames.slice(0, 4)" 
          :key="game.id"
          class="game-card-mobile"
          @click="goToGame(game.id)"
        >
          <div class="game-header-mobile">
            <h3 class="game-title-mobile">{{ game.title }}</h3>
            <div class="game-stats-mobile">
              <span class="stat-mobile">
                <span class="stat-icon-mobile">👁️</span>
                <span class="stat-value-mobile">{{ game.viewCount }}</span>
              </span>
              <span class="stat-mobile">
                <span class="stat-icon-mobile">🗳️</span>
                <span class="stat-value-mobile">{{ game.totalVotes }}</span>
              </span>
            </div>
          </div>
          
          <div class="game-options-mobile">
            <div class="option-mobile option-a">
              <div class="option-content-mobile">
                <span class="option-label">{{ game.optionA }}</span>
                <span class="vote-percent">{{ getVotePercentage(game.optionAVotes, game.totalVotes).toFixed(0) }}%</span>
              </div>
              <div class="vote-bar-mobile">
                <div class="vote-fill-a" :style="{ width: getVotePercentage(game.optionAVotes, game.totalVotes) + '%' }"></div>
              </div>
            </div>
            
            <div class="vs-mobile">VS</div>
            
            <div class="option-mobile option-b">
              <div class="option-content-mobile">
                <span class="option-label">{{ game.optionB }}</span>
                <span class="vote-percent">{{ getVotePercentage(game.optionBVotes, game.totalVotes).toFixed(0) }}%</span>
              </div>
              <div class="vote-bar-mobile">
                <div class="vote-fill-b" :style="{ width: getVotePercentage(game.optionBVotes, game.totalVotes) + '%' }"></div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div v-else class="no-games-mobile">
        <div class="empty-icon-mobile">🎮</div>
        <p class="empty-text-mobile">아직 게임이 없어요</p>
        <router-link to="/mobile/create" class="create-first-btn" v-if="authStore.isLoggedIn">
          첫 게임 만들기
        </router-link>
      </div>
    </section>
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
const popularGames = ref([])
const totalGames = ref(0)
const totalVotes = ref(0)
const totalUsers = ref(0)
const activeTab = ref('daily')

// 베스트 탭 메뉴
const bestTabs = ref([
  { key: 'daily', label: '일간', icon: '🔥' },
  { key: 'weekly', label: '주간', icon: '📈' },
  { key: 'monthly', label: '월간', icon: '👑' },
  { key: 'all', label: '전체', icon: '💎' }
])

/**
 * 게임 상세 페이지로 이동
 * @param {number} gameId - 게임 ID
 */
const goToGame = (gameId) => {
  router.push(`/mobile/game/${gameId}`)
}

/**
 * 베스트 게임 목록 조회 (탭별 - 모바일)
 */
const fetchBestGames = async () => {
  try {
    // 베스트 정렬과 기간 파라미터 설정
    const response = await axios.get('/api/mobile/balance-games', {
      params: {
        sort: 'best',
        period: activeTab.value,
        size: 6
      }
    })
    popularGames.value = response.data.content
    totalGames.value = response.data.totalElements
    
    // 총 투표 수 계산
    totalVotes.value = popularGames.value.reduce((sum, game) => sum + game.totalVotes, 0)
  } catch (error) {
    console.error('베스트 게임 조회 실패:', error)
    // API 실패 시 기본값
    totalVotes.value = 1234
    totalGames.value = 0
    popularGames.value = []
  }
}

/**
 * 인기 게임 목록 조회 (모바일 API) - 하위 호환성
 */
const fetchPopularGames = async () => {
  await fetchBestGames()
}

/**
 * 사용자 수 조회 (모바일 API)
 */
const fetchUserCount = async () => {
  try {
    const response = await axios.get('/api/mobile/users')
    totalUsers.value = response.data.length
  } catch (error) {
    console.error('사용자 수 조회 실패:', error)
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
/* 모바일 뷰 스타일 */
.home-mobile {
  background: #f5f5f5;
  min-height: 100vh;
}

/* 헤로 섹션 */
.hero-mobile {
  background: linear-gradient(135deg, #FF6B35 0%, #F7931E 50%, #FFD23F 100%);
  color: #333;
  padding: 24px 16px 32px 16px;
  margin-bottom: 16px;
}

.hero-content {
  text-align: center;
  max-width: 480px;
  margin: 0 auto;
}

.hero-emoji {
  font-size: 32px;
  margin-bottom: 8px;
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-10px);
  }
  60% {
    transform: translateY(-5px);
  }
}

.hero-title-mobile {
  font-size: 24px;
  font-weight: 700;
  margin: 0 0 8px 0;
  letter-spacing: -0.5px;
}

.hero-subtitle-mobile {
  font-size: 14px;
  line-height: 1.4;
  margin: 0 0 20px 0;
  opacity: 0.8;
}

.hero-buttons-mobile {
  display: flex;
  gap: 8px;
  justify-content: center;
  flex-wrap: wrap;
}

.hero-btn {
  padding: 10px 20px;
  border-radius: 20px;
  text-decoration: none;
  font-size: 13px;
  font-weight: 600;
  transition: all 0.2s;
  border: none;
  cursor: pointer;
}

.hero-btn.primary {
  background: #ffffff;
  color: #333;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.hero-btn.primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.hero-btn.secondary {
  background: rgba(255, 255, 255, 0.2);
  color: #333;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.hero-btn.secondary:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-1px);
}

/* 통계 섹션 */
.stats-mobile {
  background: #ffffff;
  margin: 0 12px 16px 12px;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.stat-row {
  display: flex;
  justify-content: space-around;
  align-items: center;
}

.stat-item {
  text-align: center;
  flex: 1;
}

.stat-number {
  font-size: 20px;
  font-weight: 700;
  color: #FF6B35;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 11px;
  color: #666;
  font-weight: 500;
}

/* 인기 게임 섹션 */
.popular-games-mobile {
  padding: 0 12px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding: 0 4px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.see-all-btn {
  font-size: 12px;
  color: #666;
  text-decoration: none;
  padding: 4px 8px;
  border-radius: 8px;
  transition: background 0.2s;
}

.see-all-btn:hover {
  background: #f8f9fa;
}

/* 베스트 탭 스타일 (모바일) */
.best-tabs-mobile {
  display: flex;
  gap: 6px;
  margin: 12px 0 16px 0;
  padding: 6px;
  background: #f8fafc;
  border-radius: 10px;
  overflow-x: auto;
  scrollbar-width: none;
  -ms-overflow-style: none;
}

.best-tabs-mobile::-webkit-scrollbar {
  display: none;
}

.tab-button-mobile {
  flex: 1;
  min-width: 70px;
  padding: 8px 12px;
  border: none;
  border-radius: 6px;
  background: transparent;
  color: #64748b;
  font-weight: 500;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
}

.tab-button-mobile span {
  font-size: 10px;
}

.tab-button-mobile:hover {
  background: #e2e8f0;
  color: #475569;
}

.tab-button-mobile.active {
  background: linear-gradient(135deg, #FF6B35 0%, #F7931E 50%, #FFD23F 100%);
  color: white;
  box-shadow: 0 2px 6px rgba(255, 107, 53, 0.3);
}

.tab-button-mobile.active:hover {
  background: linear-gradient(135deg, #FF6B35 0%, #F7931E 50%, #FFD23F 100%);
  transform: translateY(-1px);
  box-shadow: 0 3px 8px rgba(255, 107, 53, 0.4);
}

/* 게임 리스트 */
.games-list-mobile {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.game-card-mobile {
  background: #ffffff;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: transform 0.2s;
}

.game-card-mobile:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.game-header-mobile {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.game-title-mobile {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin: 0;
  line-height: 1.3;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-right: 8px;
}

.game-stats-mobile {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

.stat-mobile {
  display: flex;
  align-items: center;
  gap: 2px;
  font-size: 11px;
  color: #666;
}

.stat-icon-mobile {
  font-size: 12px;
}

.stat-value-mobile {
  font-weight: 500;
}

/* 게임 옵션 - 모바일 최적화 */
.game-options-mobile {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
}

.option-mobile {
  flex: 1;
  min-width: 0;
}

.option-content-mobile {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.option-label {
  font-size: 12px;
  font-weight: 500;
  color: #333;
  line-height: 1.2;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 80px;
}

.vote-percent {
  font-size: 10px;
  font-weight: 600;
  color: #666;
  margin-left: 4px;
}

.vote-bar-mobile {
  height: 4px;
  background: #e9ecef;
  border-radius: 2px;
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

.vs-mobile {
  font-size: 9px;
  font-weight: 700;
  color: #999;
  background: #ffffff;
  border-radius: 50%;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  border: 1px solid #e9ecef;
}

/* 빈 상태 */
.no-games-mobile {
  text-align: center;
  padding: 40px 20px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.empty-icon-mobile {
  font-size: 32px;
  margin-bottom: 12px;
}

.empty-text-mobile {
  font-size: 14px;
  color: #666;
  margin: 0 0 16px 0;
}

.create-first-btn {
  display: inline-block;
  padding: 10px 20px;
  background: #FF6B35;
  color: #333;
  border-radius: 20px;
  text-decoration: none;
  font-size: 13px;
  font-weight: 600;
  transition: transform 0.2s;
}

.create-first-btn:hover {
  transform: translateY(-1px);
}
</style>