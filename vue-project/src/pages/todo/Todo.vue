<!-- TodoStickyWall.vue (Trend Redesign + complete/delete 포함) -->
<template>
  <div class="page">
    <div class="wrap">
      <!-- 헤더 -->
      <header class="header">
        <div class="h-left">
          <div class="title">
            <span class="logo">🧩</span>
            <div>
              <h1>Todo 보드</h1>
              <p class="sub">
                오늘 {{ todayLabel }} · <strong>{{ inProgressCount }}</strong>개 진행 중 ·
                <strong>{{ completedCount }}</strong>개 완료
              </p>
            </div>
          </div>

          <!-- 진행률 -->
          <div class="progress">
            <div class="progress-top">
              <span>진행률</span>
              <strong>{{ progress }}%</strong>
            </div>
            <div class="bar-outer">
              <div class="bar-inner" :style="{ width: progress + '%' }" />
            </div>
          </div>
        </div>

        <div class="h-right">
          <button class="btn-primary" @click="$router.push('/todo/new')">
            <i class="fas fa-plus" />
            새 Todo
          </button>
        </div>
      </header>

      <!-- 컨트롤 바 -->
      <section class="controls">
        <div class="search">
          <i class="fas fa-search" />
          <input
              v-model.trim="q"
              class="search-input"
              type="text"
              placeholder="제목/설명 검색…"
          />
        </div>

        <div class="filters">
          <select v-model="statusFilter" class="select">
            <option value="ALL">전체</option>
            <option value="IN_PROGRESS">진행중</option>
            <option value="DONE">완료</option>
          </select>

          <select v-model="categoryFilter" class="select">
            <option value="ALL">모든 카테고리</option>
            <option v-for="c in categoryOptions" :key="c" :value="c">
              {{ c }}
            </option>
          </select>

          <select v-model="sortKey" class="select">
            <option value="NEW">최신순</option>
            <option value="PRIORITY">우선순위</option>
            <option value="TITLE">제목순</option>
          </select>
        </div>

        <div class="toggle">
          <button class="tbtn" :class="{ active: viewMode === 'grid' }" @click="viewMode='grid'">
            <i class="fas fa-th-large" />
          </button>
          <button class="tbtn" :class="{ active: viewMode === 'list' }" @click="viewMode='list'">
            <i class="fas fa-list" />
          </button>
        </div>
      </section>

      <!-- 로딩/에러 배너 -->
      <section v-if="loading || errorMsg" class="banner">
        <div v-if="loading" class="banner-card">불러오는 중…</div>
        <div v-else class="banner-card danger">
          {{ errorMsg }}
          <button class="retry" @click="fetchTodos">다시 시도</button>
        </div>
      </section>

      <!-- 빈 상태 -->
      <section v-if="!loading && !filteredTodos.length" class="empty">
        <div class="empty-card">
          <div class="empty-emoji">🪄</div>
          <h3>아직 Todo가 없어요</h3>
          <p>작게 하나만 만들어도 충분해요. 첫 Todo부터 시작해봅시다.</p>
          <button class="btn-primary" @click="$router.push('/todo/new')">
            새 Todo 만들기
          </button>
        </div>
      </section>

      <!-- 그리드(보드) -->
      <section v-else-if="viewMode === 'grid'" class="board">
        <article
            v-for="todo in filteredTodos"
            :key="todo.todo_id"
            class="card"
            :data-status="todo.status"
            :style="noteStyle(todo)"
        >
          <div class="card-top">
            <div class="meta">
              <span class="pill cat">{{ todo.category ?? '기타' }}</span>
              <span class="pill pri" :data-pri="todo.priority">P{{ todo.priority }}</span>
            </div>

            <button
                class="icon-btn"
                @click="completeTodo(todo)"
                :disabled="todo.status !== 'IN_PROGRESS' || busyIds.has(todo.todo_id)"
                title="완료"
            >
              <i v-if="!busyIds.has(todo.todo_id)" class="fas fa-check" />
              <i v-else class="fas fa-spinner fa-spin" />
            </button>
          </div>

          <h3 class="card-title">{{ todo.title }}</h3>
          <p class="card-desc">{{ todo.description || '설명 없음' }}</p>

          <div class="card-bottom">
            <div class="hintline">
              <span class="hintchip" :data-due="dueTag(todo.dueAt).tone">
                ⏰ {{ dueTag(todo.dueAt).label }}
              </span>
              <span class="hintchip">🔁 {{ todo.repeat ? repeatLabel(todo.repeat) : '반복 없음' }}</span>
            </div>

            <button
                class="icon-btn danger"
                @click="deleteTodo(todo.todo_id)"
                :disabled="busyIds.has(todo.todo_id)"
                title="삭제"
            >
              <i v-if="!busyIds.has(todo.todo_id)" class="fas fa-trash" />
              <i v-else class="fas fa-spinner fa-spin" />
            </button>
          </div>

          <div class="pin" />
        </article>

        <!-- 추가 카드 -->
        <button class="card add" @click="$router.push('/todo/new')">
          <div class="add-inner">
            <i class="fas fa-plus fa-2x" />
            <p>새 Todo 추가</p>
          </div>
        </button>
      </section>

      <!-- 리스트 -->
      <section v-else class="list">
        <div
            v-for="todo in filteredTodos"
            :key="todo.todo_id"
            class="row"
            :data-status="todo.status"
        >
          <div class="left">
            <div class="row-title">
              <span class="pill cat">{{ todo.category ?? '기타' }}</span>
              <span class="pill pri" :data-pri="todo.priority">P{{ todo.priority }}</span>
              <strong>{{ todo.title }}</strong>
            </div>
            <p class="row-desc">{{ todo.description || '설명 없음' }}</p>

            <div class="row-hints">
              <span class="hintchip">⏰ {{ todo.dueAt ? formatDue(todo.dueAt) : '마감 없음' }}</span>
              <span class="hintchip">🔁 {{ todo.repeat ? repeatLabel(todo.repeat) : '반복 없음' }}</span>
            </div>
          </div>

          <div class="right">
            <button
                class="btn-small"
                @click="completeTodo(todo)"
                :disabled="todo.status !== 'IN_PROGRESS' || busyIds.has(todo.todo_id)"
            >
              <span v-if="!busyIds.has(todo.todo_id)">완료</span>
              <span v-else><i class="fas fa-spinner fa-spin" /></span>
            </button>
            <button
                class="btn-small danger"
                @click="deleteTodo(todo.todo_id)"
                :disabled="busyIds.has(todo.todo_id)"
            >
              <span v-if="!busyIds.has(todo.todo_id)">삭제</span>
              <span v-else><i class="fas fa-spinner fa-spin" /></span>
            </button>
          </div>
        </div>
      </section>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import api from '@/utils/api'

const todos = ref([])
// const completedCount = ref(0)
const palette = ['#fff8b8', '#dff3f9', '#ffe0e4', '#ffe8d1', '#d1f7e1']

// UI state
const q = ref('')
const statusFilter = ref('ALL')
const categoryFilter = ref('ALL')
const sortKey = ref('NEW')
const viewMode = ref('grid')

const loading = ref(false)
const errorMsg = ref('')
const busyIds = ref(new Set()) // 진행중 id set

const todayLabel = computed(() => {
  const d = new Date()
  const mm = String(d.getMonth() + 1).padStart(2, '0')
  const dd = String(d.getDate()).padStart(2, '0')
  return `${mm}.${dd}`
})

const completedCount = computed(() =>
    todos.value.filter(t => t.status === 'DONE').length
)

const inProgressCount = computed(() =>
    todos.value.filter(t => t.status === 'IN_PROGRESS').length
)

const progress = computed(() =>
    todos.value.length ? Math.round((completedCount.value / todos.value.length) * 100) : 0
)

const categoryOptions = computed(() => {
  const set = new Set(todos.value.map(t => t.category).filter(Boolean))
  return Array.from(set)
})

async function fetchTodos () {
  loading.value = true
  errorMsg.value = ''
  try {
    const token = localStorage.getItem('token')
    const { data } = await api.get('/todos', {
      headers: { Authorization: 'Bearer ' + token }
    })
    todos.value = (data.todos ?? []).map(t => ({
      ...t,
      dueAt: t.dueDate,
      _color : palette[Math.floor(Math.random() * palette.length)],
      _rotate: (Math.random() * 4 - 2).toFixed(2)
    }))
    completedCount.value = data.completedCount || 0
  } catch (err) {
    errorMsg.value = normalizeErr(err)
  } finally {
    loading.value = false
  }
}
function dueTag (iso) {
  if (!iso) return { label: '마감 없음', tone: 'none' }

  const due = new Date(iso)
  const now = new Date()

  // 날짜만 비교(시간 제거)
  const dueDateOnly = new Date(due.getFullYear(), due.getMonth(), due.getDate())
  const nowDateOnly = new Date(now.getFullYear(), now.getMonth(), now.getDate())

  const diffMs = dueDateOnly - nowDateOnly
  const diffDays = Math.round(diffMs / (1000 * 60 * 60 * 24))

  const mm = String(due.getMonth() + 1).padStart(2,'0')
  const dd = String(due.getDate()).padStart(2,'0')

  if (diffDays < 0) return { label: `${mm}/${dd} · D+${Math.abs(diffDays)} (지남)`, tone: 'over' }
  if (diffDays === 0) return { label: `${mm}/${dd} · D-DAY`, tone: 'today' }
  return { label: `${mm}/${dd} · D-${diffDays}`, tone: diffDays <= 3 ? 'soon' : 'ok' }
}

const noteStyle = todo => ({
  background: todo._color,
  transform : `rotate(${todo._rotate}deg)`
})

const filteredTodos = computed(() => {
  let arr = [...todos.value]

  // status
  if (statusFilter.value !== 'ALL') {
    arr = arr.filter(t => t.status === statusFilter.value)
  }
  // category
  if (categoryFilter.value !== 'ALL') {
    arr = arr.filter(t => (t.category ?? '기타') === categoryFilter.value)
  }
  // search
  if (q.value) {
    const s = q.value.toLowerCase()
    arr = arr.filter(t =>
        (t.title ?? '').toLowerCase().includes(s) ||
        (t.description ?? '').toLowerCase().includes(s)
    )
  }

  // sort
  if (sortKey.value === 'PRIORITY') {
    arr.sort((a, b) => (a.priority ?? 3) - (b.priority ?? 3))
  } else if (sortKey.value === 'TITLE') {
    arr.sort((a, b) => String(a.title ?? '').localeCompare(String(b.title ?? ''), 'ko'))
  } else {
    arr.sort((a, b) => new Date(b.createdAt ?? 0) - new Date(a.createdAt ?? 0))
  }

  return arr
})

function formatDue (iso) {
  const d = new Date(iso)
  const mm = String(d.getMonth() + 1).padStart(2,'0')
  const dd = String(d.getDate()).padStart(2,'0')
  const hh = String(d.getHours()).padStart(2,'0')
  const mi = String(d.getMinutes()).padStart(2,'0')
  return `${mm}/${dd} ${hh}:${mi}`
}

function repeatLabel (repeat) {
  if (!repeat?.type) return '반복 없음'
  if (repeat.type === 'DAILY') return '매일'
  if (repeat.type === 'MONTHLY') return '매월'
  if (repeat.type === 'WEEKLY') return `매주(${(repeat.weekdays ?? []).join(',')})`
  return repeat.type
}

// 공통 에러 메시지
function normalizeErr (err) {
  // 서버가 { message } 또는 { error/message } 등을 줄 때 최대한 뽑아오기
  const msg =
      err?.response?.data?.message ||
      err?.response?.data?.error ||
      err?.message ||
      '요청 처리 중 오류가 발생했습니다.'
  return String(msg)
}

function addBusy (id) {
  const next = new Set(busyIds.value)
  next.add(id)
  busyIds.value = next
}
function removeBusy (id) {
  const next = new Set(busyIds.value)
  next.delete(id)
  busyIds.value = next
}


async function completeTodo (todo) {
  const id = todo.todo_id
  if (!id || todo.status !== 'IN_PROGRESS') return

  const prevStatus = todo.status
  todo.status = 'DONE'

  addBusy(id)

  try {
    const token = localStorage.getItem('token')

    await api.put(`/todo/${id}/complete`, null, {
      headers: { Authorization: 'Bearer ' + token }
    })

    completedCount.value = todos.value.filter(t => t.status === 'DONE').length
  } catch (err) {
    // rollback
    todo.status = prevStatus
    alert('완료 처리 실패: ' + normalizeErr(err))
  } finally {
    removeBusy(id)
  }
}

async function deleteTodo (id) {
  if (!id) return
  const ok = confirm('정말 삭제할까요?')
  if (!ok) return

  addBusy(id)

  const snapshot = [...todos.value]
  todos.value = todos.value.filter(t => t.todo_id !== id)

  try {
    const token = localStorage.getItem('token')

    await api.delete(`/todo/${id}`, {
      headers: { Authorization: 'Bearer ' + token }
    })


    completedCount.value = todos.value.filter(t => t.status === 'DONE').length
  } catch (err) {
    todos.value = snapshot
    alert('삭제 실패: ' + normalizeErr(err))
  } finally {
    removeBusy(id)
  }
}

onMounted(fetchTodos)
</script>

<style scoped>
/* === 배경 === */
.page {
  min-height: 100vh;
  padding: 110px 16px 90px;
  background: radial-gradient(circle at 30% 10%, #dfe8d8 0%, #cfdac7 100%);
  font-family: 'Pretendard','Inter',sans-serif;
}

.wrap {
  max-width: 1280px;
  margin: 0 auto;
  background: rgba(255,255,255,.92);
  border-radius: 32px;
  box-shadow: 0 22px 60px rgba(0,0,0,.12);
  padding: 26px;
}

/* === Header === */
.header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  padding: 8px 6px 18px;
  border-bottom: 1px solid rgba(0,0,0,.06);
}

.h-left { display: flex; gap: 18px; align-items: center; flex-wrap: wrap; }
.title { display: flex; gap: 12px; align-items: center; }
.logo { width: 44px; height: 44px; border-radius: 14px;
  display: grid; place-items: center;
  background: rgba(37,99,235,.12);
  font-size: 20px;
}
.title h1 { margin: 0; font-size: 22px; font-weight: 900; }
.sub { margin: 6px 0 0; color: rgba(0,0,0,.55); font-weight: 700; font-size: 13px; }

.progress {
  min-width: 220px;
  background: rgba(0,0,0,.03);
  border: 1px solid rgba(0,0,0,.06);
  padding: 12px 14px;
  border-radius: 16px;
}

.progress-top {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 10px;
  font-weight: 900;
  margin-bottom: 10px;
}
.pct {
  font-variant-numeric: tabular-nums;
}
.progress-top span { color: rgba(0,0,0,.55); }
.progress-top strong { color: rgba(0,0,0,.85); }

.bar-outer {
  position: relative;
  height: 10px;
  border-radius: 999px;
  background: rgba(0,0,0,.10);
  overflow: hidden;
  line-height: 0;
  font-size: 0;
}

.bar-inner {
  height: 100%;
  border-radius: 999px;
  background: #2563eb;
  width: 0%;
  transition: width .25s ease;
}


/* buttons */
.btn-primary {
  border: none;
  background: #2563eb;
  color: #fff;
  font-weight: 900;
  padding: 12px 14px;
  border-radius: 14px;
  cursor: pointer;
  box-shadow: 0 10px 22px rgba(37,99,235,.22);
  transition: .15s ease;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}
.btn-primary:hover { transform: translateY(-1px); }

/* === Controls === */
.controls {
  margin-top: 18px;
  display: grid;
  grid-template-columns: 1.3fr 1.6fr auto;
  gap: 12px;
  align-items: center;
}

.search {
  display: flex;
  align-items: center;
  gap: 10px;
  background: #fff;
  border: 1px solid rgba(0,0,0,.10);
  border-radius: 14px;
  padding: 10px 12px;
}
.search i { color: rgba(0,0,0,.45); }
.search-input {
  border: none;
  outline: none;
  width: 100%;
  font-size: 14px;
}

.filters { display: flex; gap: 10px; flex-wrap: wrap; justify-content: flex-end; }
.select {
  border: 1px solid rgba(0,0,0,.10);
  border-radius: 14px;
  padding: 10px 12px;
  background: #fff;
  font-weight: 800;
  font-size: 13px;
}

.toggle { display: flex; gap: 8px; justify-content: flex-end; }
.tbtn {
  width: 42px; height: 42px;
  border-radius: 14px;
  border: 1px solid rgba(0,0,0,.10);
  background: #fff;
  cursor: pointer;
  display: grid; place-items: center;
  transition: .15s ease;
  color: rgba(0,0,0,.55);
}
.tbtn.active {
  border-color: rgba(37,99,235,.35);
  background: rgba(37,99,235,.10);
  color: rgba(0,0,0,.80);
}

/* banner */
.banner { margin-top: 14px; }
.banner-card {
  background: rgba(0,0,0,.04);
  border: 1px solid rgba(0,0,0,.08);
  border-radius: 16px;
  padding: 12px 14px;
  font-weight: 800;
  color: rgba(0,0,0,.70);
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}
.banner-card.danger {
  background: rgba(239,68,68,.08);
  border-color: rgba(239,68,68,.18);
}
.retry {
  border: none;
  background: rgba(0,0,0,.08);
  border-radius: 12px;
  padding: 8px 10px;
  cursor: pointer;
  font-weight: 900;
}

/* === Empty === */
.empty { padding: 40px 6px 10px; }
.empty-card {
  border: 1px dashed rgba(0,0,0,.18);
  border-radius: 22px;
  background: rgba(255,255,255,.7);
  padding: 26px;
  text-align: center;
}
.empty-emoji { font-size: 32px; }
.empty-card h3 { margin: 12px 0 6px; font-weight: 900; }
.empty-card p { margin: 0 0 14px; color: rgba(0,0,0,.55); font-weight: 700; }

/* === Grid board === */
.board {
  margin-top: 16px;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 18px;
}

.card {
  position: relative;
  height: 230px;
  border-radius: 20px;
  padding: 16px;
  box-shadow: 0 10px 24px rgba(0,0,0,.10);
  transition: transform .18s ease, box-shadow .18s ease;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.card:hover { transform: translateY(-6px) rotate(0deg); box-shadow: 0 16px 34px rgba(0,0,0,.14); }
.card[data-status="DONE"] { opacity: .60; filter: grayscale(.15); }

.card-top { display: flex; justify-content: space-between; align-items: center; gap: 10px; }
.meta { display: flex; gap: 8px; flex-wrap: wrap; }
.pill {
  font-size: 12px;
  font-weight: 900;
  padding: 6px 10px;
  border-radius: 999px;
  background: rgba(0,0,0,.12);
  color: rgba(0,0,0,.78);
}
.hintchip[data-due="over"]   { border-color: rgba(239,68,68,.35); }
.hintchip[data-due="today"]  { border-color: rgba(245,158,11,.40); }
.hintchip[data-due="soon"]   { border-color: rgba(245,158,11,.25); }
.hintchip[data-due="ok"]     { border-color: rgba(34,197,94,.22); }
.hintchip[data-due="none"]   { opacity: .9; }

.pill.cat { background: rgba(255,255,255,.7); border: 1px solid rgba(0,0,0,.10); }
.pill.pri[data-pri="1"] { background: rgba(239,68,68,.18); }
.pill.pri[data-pri="2"] { background: rgba(245,158,11,.18); }
.pill.pri[data-pri="3"] { background: rgba(0,0,0,.12); }
.pill.pri[data-pri="4"] { background: rgba(59,130,246,.14); }
.pill.pri[data-pri="5"] { background: rgba(34,197,94,.14); }

.card-title { margin: 0; font-size: 16px; font-weight: 900; }
.card-desc {
  margin: 0;
  font-size: 13px;
  color: rgba(0,0,0,.62);
  line-height: 1.45;
  white-space: pre-wrap;
  word-break: break-word;
  flex: 1;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 4;
  -webkit-box-orient: vertical;
}

.card-bottom { display: flex; justify-content: space-between; align-items: center; gap: 10px; }
.hintline { display: flex; gap: 8px; flex-wrap: wrap; }
.hintchip {
  font-size: 12px;
  font-weight: 800;
  padding: 6px 10px;
  border-radius: 999px;
  background: rgba(255,255,255,.65);
  border: 1px solid rgba(0,0,0,.10);
  color: rgba(0,0,0,.72);
}

.icon-btn {
  width: 38px; height: 38px;
  border-radius: 14px;
  border: 1px solid rgba(0,0,0,.12);
  background: rgba(255,255,255,.65);
  cursor: pointer;
  display: grid; place-items: center;
  transition: .15s ease;
}
.icon-btn:hover { transform: translateY(-1px); }
.icon-btn:disabled { opacity: .45; cursor: not-allowed; transform: none; }
.icon-btn.danger { border-color: rgba(239,68,68,.22); }

.pin {
  position: absolute;
  top: 12px;
  left: 50%;
  transform: translateX(-50%);
  width: 16px; height: 16px;
  border-radius: 50%;
  background: #111827;
  border: 4px solid rgba(255,255,255,.92);
  box-shadow: 0 2px 10px rgba(0,0,0,.25);
}


.card.add {
  border: 2px dashed rgba(0,0,0,.20);
  background: rgba(255,255,255,.6);
  box-shadow: none;
  cursor: pointer;
}
.add-inner {
  height: 100%;
  display: grid;
  place-items: center;
  gap: 8px;
  color: rgba(0,0,0,.55);
  font-weight: 900;
}
.card.add:hover { background: rgba(255,255,255,.82); }


.list { margin-top: 16px; display: flex; flex-direction: column; gap: 12px; }
.row {
  display: flex;
  justify-content: space-between;
  gap: 14px;
  padding: 14px 16px;
  border-radius: 18px;
  border: 1px solid rgba(0,0,0,.08);
  background: rgba(255,255,255,.85);
}
.row[data-status="DONE"] { opacity: .62; }
.row-title { display: flex; align-items: center; flex-wrap: wrap; gap: 8px; }
.row-desc { margin: 8px 0 0; color: rgba(0,0,0,.62); font-size: 13px; }
.row-hints { margin-top: 10px; display: flex; gap: 8px; flex-wrap: wrap; }

.right { display: flex; gap: 8px; align-items: center; }
.btn-small {
  border: 1px solid rgba(0,0,0,.12);
  background: #fff;
  border-radius: 12px;
  padding: 10px 12px;
  font-weight: 900;
  cursor: pointer;
}
.btn-small:disabled { opacity: .45; cursor: not-allowed; }
.btn-small.danger { border-color: rgba(239,68,68,.22); }


@media (max-width: 980px) {
  .controls { grid-template-columns: 1fr; }
  .filters { justify-content: flex-start; }
  .toggle { justify-content: flex-start; }
}
</style>
