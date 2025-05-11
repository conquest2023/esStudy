<!-- src/components/sidebar/RightSidebar.vue -->
<template>
  <!-- ▣ 토글 버튼 (항상 화면 우측에 고정) -->
<!--  <button-->
<!--      class="toggle-btn btn btn-light shadow-sm d-flex align-items-center justify-content-center"-->
<!--      @click="collapsed = !collapsed"-->
<!--      :title="collapsed ? '펼치기' : '접기'"-->
<!--  >-->
<!--    <i :class="collapsed ? 'fas fa-chevron-left' : 'fas fa-chevron-right'"></i>-->
<!--  </button>-->

  <!-- ▣ 실제 사이드바 -->
  <!-- ▣ 열기 버튼 (북마크 스타일로 개선) -->
  <button
      v-if="collapsed"
      class="toggle-bookmark-btn"
      @click="collapsed = false"
      title="사이드바 열기">
    📑
  </button>

  <transition name="slide">

    <aside v-if="!collapsed" class="right-sidebar shadow-sm">

      <button
          class="btn btn-sm btn-outline-secondary w-100 mb-2"
          @click="collapsed = true">
        사이드바 접기
      </button>
      <ul class="nav nav-tabs card-header-tabs small">
        <li v-for="t in tabs" :key="t.id" class="nav-item">
          <button
              class="nav-link"
              :class="{ active: currentTab === t.id }"
              @click="currentTab = t.id"
          >
            {{ t.label }}
          </button>
        </li>
      </ul>

      <!-- ▣ 탭 컨텐츠 -->
      <div class="tab-content small p-2">

        <!-- ▷ Todo / D‑Day -->
        <section v-show="currentTab === 'todo'">
          <h6 class="fw-bold">D‑Day 일정</h6>

          <ul class="list-group small mb-3">
            <li v-for="d in dDayList" :key="d.id"
                class="list-group-item d-flex justify-content-between align-items-center">
              <span>{{ d.examName }}</span>
              <span class="badge bg-danger">D‑{{ d.dday }}</span>
            </li>
            <li v-if="!dDayList.length" class="list-group-item text-muted">D‑Day 일정이 없습니다</li>
          </ul>

          <h6 class="fw-bold">오늘의 Todo</h6>

          <div class="progress mb-2" :title="todoProgress+'%'">
            <div class="progress-bar bg-info"
                 role="progressbar"
                 :style="{ width: todoProgress + '%' }"
                 :aria-valuenow="todoProgress" aria-valuemin="0" aria-valuemax="100">
              {{ todoProgress }}%
            </div>
          </div>

          <ul class="list-group small">
            <li v-for="t in todoList.slice(0,3)" :key="t.id"
                class="list-group-item d-flex justify-content-between align-items-center">
              <span>{{ statusIcon(t.status) }} {{ t.title }}</span>
              <span class="badge bg-secondary">{{ t.priority }}</span>
            </li>
            <li v-if="!todoList.length" class="list-group-item text-muted">오늘의 Todo가 없습니다</li>
          </ul>
        </section>

        <!-- ▷ 캘린더 (원하면 나중에 구현) -->
        <section v-show="currentTab === 'calendar'">
          <h6 class="fw-bold">📅 내 캘린더</h6>
          <!-- <MiniCalendar :events="calendarEvents" /> -->
        </section>
      </div>

      <!-- ▣ 방문자 · Top 5 -->
      <div class="accordion mt-3">
        <div class="accordion-item">
          <h2 class="accordion-header">
            <button class="accordion-button collapsed" data-bs-toggle="collapse" data-bs-target="#visitor">
              👥 방문자 통계
            </button>
          </h2>
          <div id="visitor" class="accordion-collapse collapse show">
            <div class="accordion-body small">
              <p>현재 접속자: {{ visitorStats.active }}</p>
              <p>오늘 방문자: {{ visitorStats.today }}</p>
              <p>누적 방문자: {{ visitorStats.total }}</p>
            </div>
          </div>
        </div>

        <div class="accordion-item">
          <h2 class="accordion-header">
            <button class="accordion-button collapsed" data-bs-toggle="collapse" data-bs-target="#top">
              🏆 Top 5 기여자
            </button>
          </h2>
          <div id="top" class="accordion-collapse collapse">
            <div class="accordion-body small">
              <div v-if="!topWriters.length" class="text-muted text-center">데이터가 없습니다</div>
              <div v-for="(w,idx) in topWriters" :key="w.username"
                   class="d-flex justify-content-between align-items-center py-1">
                <span>{{ rankIcon(idx) }} {{ w.username }}</span>
                <small class="text-muted">{{ w.viewCount }}점</small>
              </div>
            </div>
          </div>
        </div>
      </div>

    </aside>
  </transition>
</template>

<script setup>
import { ref } from 'vue'
import { useSidebarData } from '@/composables/useSidebarData'

/* ▶︎ 데이터 로딩 composable */
const {
  dDayList,
  todoList,
  todoProgress,
  visitorStats,
  topWriters
} = useSidebarData()

/* ▶︎ 사이드바 접힘 상태 */
const collapsed = ref(false)

/* ▶︎ 로컬 탭 */
const tabs = [
  { id:'todo',     label:'📋 Todo & D‑Day' },
  { id:'calendar', label:'📅 캘린더' }
]
const currentTab = ref('todo')

/* ▶︎ 헬퍼 */
function statusIcon (s) { return s==='DONE' ? '✅': s==='IN_PROGRESS'?'⏳':'📝' }
function rankIcon   (i) { return ['👑','🥇','🥈','🥉'][i] || `${i+1}.` }
</script>

<style scoped>
.right-sidebar {
  position: fixed;
  top: 80px;
  right: 20px;
  width: 260px;
  max-height: calc(100vh - 90px);
  overflow-y: auto;
  background: #fff;
  border-radius: 8px;
  padding: 10px;
  z-index: 1030;
  box-shadow: 0 2px 6px rgba(0,0,0,.08);
}

/* ───────── 토글 버튼 */
.toggle-bookmark-btn {
  position: fixed;
  top: 130px;
  right: 12px;
  z-index: 1040;
  background: #fff;
  border: 1px solid #dee2e6;
  border-radius: 6px 0 0 6px;
  padding: 6px 10px;
  font-size: 1.2rem;
  cursor: pointer;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  transition: background 0.2s ease;
}

.toggle-bookmark-btn:hover {
  background: #f1f3f5;
}


/* ✅ 모바일 환경에도 토글버튼만 보이게 하고 사이드바는 숨김 */
@media (max-width: 991.98px) {
  .right-sidebar {
    display: block !important;
  }
}


</style>
