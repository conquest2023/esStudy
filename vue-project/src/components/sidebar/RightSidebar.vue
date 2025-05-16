<template>
  <button v-if="collapsed" class="sidebar-fab d-inline-flex" @click="collapsed = false" aria-label="사이드바 열기">
    <i class="fas fa-bars"></i>
  </button>


  <!-- ▣ Sidebar Drawer -->
  <transition name="fade-slide">
    <aside v-if="!collapsed" class="sidebar-drawer glass shadow-lg">
      <!-- ▣ Drawer Header -->
      <header class="d-flex justify-content-between align-items-center mb-3">
        <!-- Section Nav -->
        <nav class="nav nav-pills gap-2 flex-grow-1">
          <button
              v-for="s in sections"
              :key="s.id"
              class="nav-link d-flex align-items-center gap-1"
              :class="{ active: currentSection === s.id }"
              @click="currentSection = s.id"
              :title="s.title"
          >
            <i :class="s.icon"></i>
            <span class="d-none d-lg-inline">{{ s.title }}</span>
          </button>
        </nav>
        <button class="btn btn-sm btn-light ms-2" @click="collapsed = true">
          <i class="fas fa-times"></i>
        </button>
      </header>

      <!-- ▣ Dashboard: 방문자·Top Writers -->
      <section v-show="currentSection === 'dashboard'" class="mb-4">
        <!-- Visitor Stats Card -->
        <div class="card mb-3">
          <div class="card-body p-3">
            <h6 class="fw-bold mb-2"><i class="fas fa-chart-line me-1"></i> 방문자 통계</h6>
            <div class="d-flex justify-content-between small text-muted">
              <div>
                <span class="fw-bold">{{ visitorStats.active }}</span> 현재 접속
              </div>
              <div>
                <span class="fw-bold">{{ visitorStats.today }}</span> 오늘
              </div>
              <div>
                <span class="fw-bold">{{ visitorStats.total }}</span> 누적
              </div>
            </div>
          </div>
        </div>

        <!-- Top Writers Card -->
        <div class="card">
          <div class="card-body p-3">
            <h6 class="fw-bold mb-2"><i class="fas fa-crown me-1"></i> Top 5 기여자</h6>
            <div v-if="!topWriters.length" class="text-muted small text-center">데이터가 없습니다</div>
            <div v-for="(w, idx) in topWriters" :key="w.username" class="d-flex justify-content-between align-items-center small py-1">
              <span>{{ rankIcon(idx) }} {{ w.username }}</span>
              <span class="text-muted">{{ w.viewCount }}점</span>
            </div>
          </div>
        </div>
      </section>

      <!-- ▣ Tasks: D-Day & Todo -->
      <section v-show="currentSection === 'tasks'" class="mb-4">
        <!-- D-Day -->
        <div class="card mb-3">
          <div class="card-body p-3">
            <h6 class="fw-bold mb-2"><i class="fas fa-flag-checkered me-1"></i> D-Day 목록</h6>
            <ul class="list-group list-group-flush">
              <li v-for="d in dDayList" :key="d.id" class="list-group-item d-flex justify-content-between align-items-center px-0 py-1">
                <span>{{ d.examName }}</span>
                <span class="badge bg-danger">D-{{ d.dday }}</span>
              </li>
              <li v-if="!dDayList.length" class="list-group-item text-muted px-0 py-1">D-Day 일정이 없습니다</li>
            </ul>
          </div>
        </div>

        <!-- Todo -->
        <div class="card">
          <div class="card-body p-3">
            <h6 class="fw-bold mb-2"><i class="fas fa-list-check me-1"></i> 오늘의 Todo</h6>
            <div class="progress mb-2" :title="todoProgress + '%'">
              <div
                  class="progress-bar bg-info progress-bar-striped progress-bar-animated"
                  role="progressbar"
                  :style="{ width: todoProgress + '%' }"
                  :aria-valuenow="todoProgress"
                  aria-valuemin="0"
                  aria-valuemax="100"
              >
                {{ todoProgress }}%
              </div>
            </div>
            <ul class="list-group list-group-flush">
              <li v-for="t in todoList.slice(0, 3)" :key="t.id" class="list-group-item d-flex justify-content-between align-items-center px-0 py-1">
                <span>{{ statusIcon(t.status) }} {{ t.title }}</span>
                <span class="badge bg-secondary">{{ t.priority }}</span>
              </li>
              <li v-if="!todoList.length" class="list-group-item text-muted px-0 py-1">오늘의 Todo가 없습니다</li>
            </ul>
          </div>
        </div>
      </section>

      <section v-show="currentSection === 'calendar'" class="mb-4 text-center">
        <div class="card">
          <div class="card-body p-3">
            <h6 class="fw-bold mb-2"><i class="far fa-calendar-alt me-1"></i> 내 캘린더</h6>
            <div class="text-muted small">(캘린더 컴포넌트 자리)</div>
          </div>
        </div>
      </section>
    </aside>
  </transition>
</template>

<script setup>
import { ref, watch, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useSidebarData } from '@/composables/useSidebarData'

/* ▣ 상태 */
const collapsed = ref(localStorage.getItem('sidebarCollapsed') === 'true')
watch(collapsed, v => localStorage.setItem('sidebarCollapsed', v.toString()))

const sections = [
  { id: 'dashboard', title: '대시보드', icon: 'fas fa-chart-pie' },
  { id: 'tasks', title: '작업', icon: 'fas fa-list-check' },
  { id: 'calendar', title: '캘린더', icon: 'far fa-calendar-alt' },
]
const currentSection = ref('dashboard')

const {
  dDayList,
  todoList,
  todoProgress,
  visitorStats,
  topWriters,
  fetchSidebarData,
} = useSidebarData()

/* ▣ 메인 페이지에서만 데이터 로드 */
const route = useRoute()
const isFeedMain = computed(() => route.path === '/')

onMounted(() => {
  if (isFeedMain.value) fetchSidebarData()
})

/* ▣ 헬퍼 */
const statusIcon = s => (s === 'DONE' ? '✅' : s === 'IN_PROGRESS' ? '⏳' : '📝')
const rankIcon = i => ['👑', '🥇', '🥈', '🥉'][i] || `${i + 1}.`
</script>

<style scoped>
/*****************
* Layout & Glass *
*****************/
.sidebar-drawer {
  position: fixed;
  top: 70px;
  right: 20px;
  width: 320px;
  max-height: calc(100vh - 90px);
  overflow-y: auto;
  padding: 20px 18px 28px;
  border-radius: 1.25rem;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(14px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  z-index: 1040;
}

/*********
* Mobile *
*********/
@media (max-width: 992px) {
  .sidebar-drawer {
    width: 100%;
    left: 0;
    right: 0;
    bottom: 0;
    top: auto;
    max-height: 85vh;
    border-radius: 1.25rem 1.25rem 0 0;
  }
}

/***********
* FAB btn  *
***********/
.sidebar-fab {
  position: fixed;
  bottom: 550px;
  right: 24px;
  width: 56px;
  height: 56px;
  border: none;
  border-radius: 50%;
  background: var(--bs-primary);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.25rem;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
  cursor: pointer;
  transition: transform 0.2s ease, opacity 0.2s ease;
  z-index: 1050;
}
.sidebar-fab:hover {
  transform: scale(1.08);
}

/****************
* Transitions   *
****************/
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: opacity 0.25s ease, transform 0.25s ease;
}
.fade-slide-enter-from,
.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(20px);
}

/* Nav pills */
.nav-pills .nav-link {
  border-radius: 50rem !important;
  font-size: 0.875rem;
  padding: 6px 12px;
}
.nav-pills .nav-link.active {
  background: var(--bs-primary);
  color: #fff;
}
</style>
