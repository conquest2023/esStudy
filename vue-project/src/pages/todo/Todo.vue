<template>
  <div class="board-wrap container">

    <!-- ▣ 진행률 카드 -->
    <div v-if="todos.length" class="summary-card">
      <p>
        전체 Todo <strong>{{ todos.length }}</strong>개 /
        완료 <strong>{{ completedCount }}</strong>개
      </p>
      <div class="progress-bar-outer">
        <div class="progress-bar-inner" :style="{ width: progress + '%' }">
          {{ progress }}%
        </div>
      </div>
    </div>

    <!-- ▣ 스티키 월 (Todo 카드) -->
    <section class="sticky-wall">
      <article
          v-for="todo in todos"
          :key="todo.todo_id"
          class="sticky-note"
          :data-status="todo.status"
          :data-priority="todo.priority"
      >
        <header class="note-header">
          <h6>{{ todo.title }}</h6>
        </header>

        <p class="note-desc">
          {{ todo.description || '설명 없음' }}
        </p>

        <footer class="note-footer">
          <span class="badge priority">P{{ todo.priority }}</span>
          <div class="actions">
            <button
                class="btn-icon done"
                @click="completeTodo(todo)"
                :disabled="todo.status !== 'IN_PROGRESS'"
            >
              <i class="fas fa-check"></i>
            </button>
            <button class="btn-icon del" @click="deleteTodo(todo.todo_id)">
              <i class="fas fa-trash"></i>
            </button>
          </div>
        </footer>
      </article>

      <!-- ▣ 새 카드 추가 버튼 -->
      <button
          class="sticky-note add-note"
          @click="$router.push('/todo/new')"
      >
        <i class="fas fa-plus fa-2x"></i>
      </button>
    </section>

    <!-- ▣ 장기 목표 · D-DAY · Kibana 영역은 그대로 두면 됩니다 -->
    <slot></slot>
  </div>
</template>

<script setup>
import {ref, onMounted,computed} from 'vue'
import api from '@/utils/api'
import DDayModal from '@/components/DDayModal.vue'

const todos = ref([])
const completedCount = ref(0)
const showDetails = ref({})
const kibanaUrl = ref('')
const dDays = ref([])
const showDDayModal = ref(false)
async function fetchDDays() {
  try {
    const token = localStorage.getItem('token')
    const { data } = await api.get('/day', {
      headers: { Authorization: 'Bearer ' + token }
    })
    dDays.value = data.D_Day || []
  } catch (err) {
    console.error('D-DAY 목록 불러오기 실패:', err)
  }
}

async function deleteDDay(id) {
  const token = localStorage.getItem('token')
  if (!confirm('정말 삭제하시겠습니까?')) return
  try {
    await api.post(`/day/delete?id=${id}`, {
      headers: { Authorization: 'Bearer ' + token }
    })
    alert('삭제 완료!')
    fetchDDays()
  } catch (err) {
    console.error('삭제 중 오류 발생:', err)
    alert('오류 발생: ' + err.message)
  }
}

onMounted(() => {
  fetchDDays()
})
const progress = computed(() => {
  if (!todos.value.length) return 0
  return Math.round((completedCount.value / todos.value.length) * 100)
})

function statusText(status) {
  return status === 'IN_PROGRESS' ? '진행 중' : status === 'DONE' ? '완료됨' : status
}

function toggleDetail(id) {
  showDetails.value[id] = !showDetails.value[id]
}

async function fetchTodos() {
  const token = localStorage.getItem('token')
  const {data} = await api.get('/search/todo', {
    headers: {Authorization: 'Bearer ' + token}
  })
  todos.value = data.todos
  completedCount.value = data.completedCount || 0
}

async function completeTodo(todo) {
  if (todo.status !== 'IN_PROGRESS') {
    alert('진행 중인 Todo만 완료할 수 있습니다!')
    return
  }
  if (!confirm('정말 완료 처리하시겠습니까?')) return

  await api.put(`/todo/status/${todo.todo_id}`)
  alert('✅ Todo가 완료되었습니다!')
  fetchTodos()
}

async function deleteTodo(id) {
  if (!confirm('정말 삭제하시겠습니까?')) return
  await api.post(`/todo/delete/${id}`)
  alert('삭제되었습니다!')
  fetchTodos()
}

function buildKibanaUrl(userId) {
  const g = {
    filters: [],
    refreshInterval: {pause: true, value: 0},
    time: {from: 'now-7d', to: 'now+5d'}
  }

  const a = {
    dataView: 'd62d2338-65d8-4f24-8a25-5ff361054759',
    filters: [
      {
        $state: {store: 'appState'},
        meta: {index: 'd62d2338-65d8-4f24-8a25-5ff361054759', key: 'userId', value: userId},
        query: {match_phrase: {userId}}
      }
    ],
    query: {language: 'kuery', query: `userId : "${userId}"`},
    linked: false,
    vis: {
      aggs: [
        {id: '1', params: {field: 'completionRate'}, schema: 'metric', type: 'avg'},
        {
          id: '2',
          params: {field: 'timestamp', interval: 'auto', min_doc_count: 1},
          schema: 'segment',
          type: 'date_histogram'
        }
      ],
      params: {type: 'line', addLegend: true, seriesParams: [{data: {id: '1'}, type: 'line'}]}
    }
  }

  const gRison = window.rison.encode(g)
  const aRison = window.rison.encode(a)
  return `https://kibana.montkim.com/app/visualize#/create?embed=true&type=line&indexPattern=84096079-8960-4e0d-a98c-6a5fd89ff035&_g=${encodeURIComponent(gRison)}&_a=${encodeURIComponent(aRison)}`
}

onMounted(async () => {
  await fetchTodos()

  const {data} = await api.get('/user/id', {
    headers: {Authorization: 'Bearer ' + localStorage.getItem('token')}
  })
  kibanaUrl.value = buildKibanaUrl(data.userId)
})
</script>

<style scoped>
.container {
  max-width: 1200px;
  margin: 50px auto;
  padding-top: 100px;
}
:root {
  --note-yellow: #fff8b8;
  --note-blue:   #dff3f9;
  --note-pink:   #ffe0e4;
  --note-orange: #ffe8d1;
  --note-green:  #d1f7e1;
  --radius: 14px;
  --shadow: 0 4px 14px rgba(0,0,0,.08);
}

/* ───────── 레이아웃 ───────── */
.board-wrap {
  padding-top: 100px;
  max-width: 1280px;
  margin: 0 auto;
}

.summary-card {
  background: #fff;
  border-radius: var(--radius);
  box-shadow: var(--shadow);
  padding: 1.25rem 1.5rem;
  margin-bottom: 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.progress-bar-outer {
  flex: 0 0 45%;
  height: 10px;
  background: #e9ecef;
  border-radius: 5px;
}

.progress-bar-inner {
  background: #0d6efd;
  height: 100%;
  border-radius: 5px;
  font-size: 0.75rem;
  line-height: 1.1rem;
  color: #fff;
  text-align: right;
  padding-right: .25rem;
}

/* ───────── 스티키 월 ───────── */
.sticky-wall {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 1.5rem;
}

.sticky-note {
  position: relative;
  padding: 1.25rem 1.5rem 1rem;
  border-radius: var(--radius);
  box-shadow: var(--shadow);
  overflow: hidden;
  background: var(--note-yellow);           /* 기본 색 */
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  transition: transform .15s ease, box-shadow .15s ease;
}

.sticky-note:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 18px rgba(0,0,0,.12);
}

/* ▣ 상태별 투명도 */
.sticky-note[data-status='DONE']    { opacity: .55; }
.sticky-note[data-status='TODO']    { opacity: 1;   }
.sticky-note[data-status='IN_PROGRESS'] { opacity: 1; }

/* ▣ 우선순위별 색상 */
.sticky-note[data-priority='1'] { background: var(--note-pink);   }
.sticky-note[data-priority='2'] { background: var(--note-orange); }
.sticky-note[data-priority='3'] { background: var(--note-yellow); }
.sticky-note[data-priority='4'] { background: var(--note-blue);   }
.sticky-note[data-priority='5'] { background: var(--note-green);  }

.note-header h6 {
  font-weight: 700;
  margin-bottom: .5rem;
}

.note-desc {
  font-size: .875rem;
  min-height: 60px;
  white-space: pre-wrap;
  word-break: break-word;
}

.note-footer {
  margin-top: 1rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.badge.priority {
  background: rgba(0,0,0,.15);
  border-radius: 99px;
  padding: .2rem .6rem;
  font-size: .75rem;
}

.actions .btn-icon {
  border: none;
  background: transparent;
  margin-left: .25rem;
  cursor: pointer;
  font-size: 1rem;
}

.actions .btn-icon.done { color: #198754; }
.actions .btn-icon.del  { color: #dc3545; }

/* ▣ “+” 추가 카드 */
.add-note {
  border: 2px dashed #cbd5e1;
  color: #6c757d;
  background: #f8f9fa;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background .15s;
}
.add-note:hover {
  background: #e9ecef;
}

/* ───────── 반응형 보완 ───────── */
@media (max-width: 768px) {
  .summary-card { flex-direction: column; gap: 1rem; }
}
</style>
