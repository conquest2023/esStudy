<template>
  <button
      v-if="collapsed"
      class="insight-fab shadow-lg"
      @click="collapsed = false"
      aria-label="인사이트 열기"
  >
    <i class="fas fa-chart-pie"></i>
  </button>

  <Teleport to="body">
    <Transition name="fade">
      <div v-if="!collapsed" class="sheet-backdrop" @click="collapsed = true"></div>
    </Transition>

    <Transition name="slide-up-drawer">
      <aside v-if="!collapsed" class="insight-drawer shadow-lg">

        <div class="sheet-handle-wrap" @click="collapsed = true">
          <div class="sheet-handle"></div>
        </div>

        <header class="drawer-header d-flex justify-content-between align-items-center mb-4">
          <h4 class="fw-bold mb-0 text-dark">
            <i class="fas fa-layer-group text-primary me-2"></i> Workly Insights
          </h4>
          <button class="btn-close-soft" @click="collapsed = true">
            <i class="fas fa-times"></i>
          </button>
        </header>

        <div class="modern-segmented-control mb-4">
          <button
              v-for="s in sections"
              :key="s.id"
              class="seg-btn"
              :class="{ active: currentSection === s.id }"
              @click="currentSection = s.id"
          >
            <i :class="s.icon" class="me-1"></i> {{ s.title }}
          </button>
        </div>

        <div class="drawer-scroll-area">
          <section v-show="currentSection === 'dashboard'" class="section-content">

            <div class="modern-insight-card mb-3">
              <h6 class="card-title-soft"><i class="fas fa-chart-line text-success me-2"></i> 방문자 통계</h6>
              <div class="d-flex justify-content-between text-center mt-3">
                <div class="stat-box flex-fill me-2">
                  <span class="stat-label">현재 접속</span>
                  <strong class="stat-value text-primary">{{ activeUsers }}</strong>
                </div>
                <div class="stat-box flex-fill">
                  <span class="stat-label">오늘 방문</span>
                  <strong class="stat-value text-success">{{ todayUsers }}</strong>
                </div>
              </div>
            </div>

            <div class="modern-insight-card mb-3">
              <h6 class="card-title-soft"><i class="fas fa-crown text-warning me-2"></i> 명예의 전당 Top 5</h6>
              <div v-if="!topWriters.length" class="empty-state">데이터가 없습니다</div>
              <ul v-else class="rank-list">
                <li v-for="(w, idx) in topWriters" :key="w.username" class="rank-item">
                  <span class="rank-badge">{{ rankIcon(idx) }}</span>
                  <span class="fw-semibold text-dark">{{ w.username }}</span>
                </li>
              </ul>
            </div>

            <div class="modern-insight-card">
              <h6 class="card-title-soft"><i class="fas fa-fire text-danger me-2"></i> 이번 주 베스트 Top 5</h6>
              <div v-if="!topRecentWriters.length" class="empty-state">데이터가 없습니다</div>
              <ul v-else class="rank-list">
                <li v-for="(w, idx) in topRecentWriters" :key="w.username" class="rank-item">
                  <span class="rank-badge">{{ recentRankIcon(idx) }}</span>
                  <span class="fw-semibold text-dark">{{ w.username }}</span>
                </li>
              </ul>
            </div>
          </section>

          <section v-show="currentSection === 'tasks'" class="section-content">

            <div class="modern-insight-card mb-3">
              <h6 class="card-title-soft"><i class="fas fa-flag-checkered text-danger me-2"></i> 다가오는 D-Day</h6>
              <ul class="task-list mt-2">
                <li v-for="d in dDayList" :key="d.id" class="task-item">
                  <span class="fw-medium text-dark">{{ d.examName }}</span>
                  <span class="badge-dday">D-{{ d.dday }}</span>
                </li>
                <li v-if="!dDayList.length" class="empty-state mt-2">등록된 D-Day가 없습니다</li>
              </ul>
            </div>

            <div class="modern-insight-card">
              <h6 class="card-title-soft d-flex justify-content-between">
                <span><i class="fas fa-list-check text-primary me-2"></i> 오늘의 Todo</span>
                <span class="text-primary fw-bold">{{ todoProgress }}%</span>
              </h6>

              <div class="progress-soft my-3">
                <div
                    class="progress-fill"
                    :style="{ width: todoProgress + '%' }"
                ></div>
              </div>

              <ul class="task-list">
                <li v-for="t in todoList.slice(0, 3)" :key="t.id" class="task-item">
                  <span class="d-flex align-items-center gap-2">
                    <span class="status-icon">{{ statusIcon(t.status) }}</span>
                    <span class="fw-medium text-dark text-truncate" style="max-width: 180px;">{{ t.title }}</span>
                  </span>
                  <span class="badge-priority">{{ t.priority }}</span>
                </li>
                <li v-if="!todoList.length" class="empty-state mt-2">오늘의 Todo가 없습니다</li>
              </ul>
            </div>
          </section>

          <section v-show="currentSection === 'calendar'" class="section-content text-center py-5">
            <div class="empty-state-large">
              <i class="far fa-calendar-alt text-muted mb-3"></i>
              <p class="text-muted fw-semibold">캘린더 준비 중입니다</p>
            </div>
          </section>
        </div>
      </aside>
    </Transition>
  </Teleport>
</template>

<script setup>
import {ref, computed, onMounted, watch} from 'vue'
import {useRoute} from 'vue-router'
import {storeToRefs} from 'pinia'
import {useSidebarStore} from '@/stores/sidebar'

const collapsed = ref(localStorage.getItem('sidebarCollapsed') === 'true')
const currentSection = ref('dashboard')

// 누락되었던 탭 데이터 복구
const sections = [
  {id: 'dashboard', title: '대시보드', icon: 'fas fa-chart-pie'},
  {id: 'tasks', title: '할 일', icon: 'fas fa-tasks'},
  {id: 'calendar', title: '일정', icon: 'far fa-calendar-alt'}
]

watch(collapsed, v => localStorage.setItem('sidebarCollapsed', String(v)))

const sb = useSidebarStore()
const {dDayList, todoList, todoProgress, visitorStats, topWriters, topRecentWriters} = storeToRefs(sb)

const activeUsers = computed(() => visitorStats.value.active)
const todayUsers = computed(() => visitorStats.value.today)
const totalUsers = computed(() => visitorStats.value.total)

let loadedOnce = false
onMounted(() => {
  if (!loadedOnce) {
    sb.loadLive()
    loadedOnce = true
  }
})

const statusIcon = s => (s === 'DONE' ? '✅' : s === 'IN_PROGRESS' ? '⏳' : '📝')
const rankIcon = i => ['👑', '🥇', '🥈', '🥉', '🏅'][i] || `${i + 1}.`
const recentRankIcon = i => `${i + 1}. 🔥`
</script>

<style scoped>
/* ===============================
   1. 플로팅 액션 버튼 (FAB)
================================= */
.insight-fab {
  position: fixed;
  bottom: 100px; /* 기존 550px에서 모바일에 적합한 하단으로 변경 */
  right: 20px;
  width: 56px;
  height: 56px;
  border-radius: 50%;
  border: none;
  background: linear-gradient(135deg, #1e293b, #0f172a);
  color: #fff;
  font-size: 1.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.4);
  cursor: pointer;
  transition: transform 0.2s cubic-bezier(0.34, 1.56, 0.64, 1);
  z-index: 1030;
}

.insight-fab:active {
  transform: scale(0.9);
}

/* ===============================
   2. 바텀 시트 배경 (Dim) & 패널
================================= */
.sheet-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(2px);
  z-index: 2000;
}

.insight-drawer {
  position: fixed;
  z-index: 2010;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  display: flex;
  flex-direction: column;
}

/* 🍎 모바일: 바텀 시트 형태 */
@media (max-width: 992px) {
  .insight-drawer {
    bottom: 0;
    left: 0;
    right: 0;
    height: 85vh; /* 화면의 85% 차지 */
    border-radius: 24px 24px 0 0;
    padding: 0 20px;
    padding-bottom: env(safe-area-inset-bottom, 20px);
  }
}

/* 💻 데스크탑: 우측 사이드 패널 형태 */
@media (min-width: 993px) {
  .insight-drawer {
    top: 80px;
    right: 20px;
    width: 360px;
    max-height: calc(100vh - 100px);
    border-radius: 20px;
    border: 1px solid rgba(255, 255, 255, 0.5);
    padding: 24px;
  }

  .sheet-handle-wrap {
    display: none;
  }

  /* 데스크탑에선 손잡이 숨김 */
}

/* 손잡이 (Handle) */
.sheet-handle-wrap {
  width: 100%;
  padding: 12px 0;
  display: flex;
  justify-content: center;
  cursor: pointer;
}

.sheet-handle {
  width: 40px;
  height: 5px;
  background: #cbd5e1;
  border-radius: 10px;
}

/* 닫기 버튼 */
.btn-close-soft {
  background: #f1f5f9;
  border: none;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  color: #64748b;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 스크롤 영역 */
.drawer-scroll-area {
  flex: 1;
  overflow-y: auto;
  padding-bottom: 20px;
  /* 스크롤바 숨기기 */
  -ms-overflow-style: none;
  scrollbar-width: none;
}

.drawer-scroll-area::-webkit-scrollbar {
  display: none;
}

/* ===============================
   3. iOS 스타일 세그먼트 컨트롤 탭
================================= */
.modern-segmented-control {
  display: flex;
  background: #f1f5f9;
  border-radius: 12px;
  padding: 4px;
}

.seg-btn {
  flex: 1;
  border: none;
  background: transparent;
  padding: 8px 0;
  font-size: 0.9rem;
  font-weight: 600;
  color: #64748b;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.seg-btn.active {
  background: #ffffff;
  color: #0f172a;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
}

/* ===============================
   4. 내부 카드 및 리스트 UI
================================= */
.modern-insight-card {
  background: #ffffff;
  border: 1px solid #f1f5f9;
  border-radius: 16px;
  padding: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.02);
}

.card-title-soft {
  font-weight: 700;
  color: #334155;
  font-size: 1rem;
  margin: 0;
}

/* 통계 박스 */
.stat-box {
  background: #f8fafc;
  padding: 12px;
  border-radius: 12px;
}

.stat-label {
  display: block;
  font-size: 0.8rem;
  color: #64748b;
  font-weight: 600;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 1.4rem;
  font-weight: 800;
}

/* 랭킹 & 할 일 리스트 */
.rank-list, .task-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.rank-item, .task-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f1f5f9;
}

.rank-item:last-child, .task-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.rank-badge {
  font-size: 1.1rem;
  width: 28px;
  display: inline-block;
}

.badge-dday {
  background: #fee2e2;
  color: #ef4444;
  padding: 4px 8px;
  border-radius: 8px;
  font-size: 0.75rem;
  font-weight: 700;
}

.badge-priority {
  background: #e2e8f0;
  color: #475569;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 600;
}

/* 프로그레스 바 */
.progress-soft {
  height: 10px;
  background: #e2e8f0;
  border-radius: 10px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #60a5fa, #2563eb);
  border-radius: 10px;
  transition: width 0.4s ease;
}

.empty-state {
  text-align: center;
  font-size: 0.85rem;
  color: #94a3b8;
  padding: 10px 0;
}

.empty-state-large i {
  font-size: 3rem;
}

/* ===============================
   5. 트랜지션 애니메이션
================================= */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from, .fade-leave-to {
  opacity: 0;
}

.slide-up-drawer-enter-active, .slide-up-drawer-leave-active {
  transition: transform 0.4s cubic-bezier(0.2, 0.8, 0.2, 1);
}

@media (max-width: 992px) {
  .slide-up-drawer-enter-from, .slide-up-drawer-leave-to {
    transform: translateY(100%);
  }
}

@media (min-width: 993px) {
  .slide-up-drawer-enter-from, .slide-up-drawer-leave-to {
    transform: translateX(50px);
    opacity: 0;
  }
}
</style>