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
/* 1) 전체 배경 & 보드 */
body {
  background: radial-gradient(circle at 20% 20%, #dfe8d8 0%, #cbd6c5 100%);
  min-height: 100vh;
  font-family: 'Pretendard', 'Inter', sans-serif;
}
.board-wrap {
  max-width: 1280px;
  margin: 0 auto;
  padding: 72px 40px 120px;
  background: #fff;
  border-radius: 32px;
  box-shadow: 0 20px 60px rgba(0,0,0,.12);
}

/* 2) 진행률 카드 */
.summary-card {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0,0,0,.06);
  padding: 1.2rem 1.6rem;
  margin-bottom: 2.5rem;
  display: flex; justify-content: space-between; align-items: center;
  font-size: 1rem;
}
.progress-bar-outer{
  flex:0 0 50%; height:8px; border-radius:99px; background:#e4e8ee;
}
.progress-bar-inner{
  height:100%; border-radius:99px; background:#3b82f6;
  font-size:.75rem; color:#fff; display:flex; justify-content:flex-end;
  align-items:center; padding-right:.4rem; transition:width .3s ease;
}

/* 3) 스티키 월 */
.sticky-wall{
  display:grid;
  grid-template-columns:repeat(auto-fill,minmax(260px,1fr));
  gap:2rem;
}

/* NOTE 베이스 */
.sticky-note{
  position:relative;
  padding:1.4rem 1.6rem 1.2rem;
  border-radius:18px;
  box-shadow:0 6px 14px rgba(0,0,0,.08);
  display:flex; flex-direction:column; justify-content:space-between;
  overflow:hidden;
  transition:transform .2s ease, box-shadow .2s ease;
}
/* 작은 랜덤 기울기 연출 */
.sticky-note:nth-child(5n){ transform:rotate(-1.8deg); }
.sticky-note:nth-child(5n+2){ transform:rotate(1.2deg); }
.sticky-note:nth-child(5n+4){ transform:rotate(-0.6deg); }
.sticky-note:hover{
  transform:translateY(-6px) rotate(0deg);
  box-shadow:0 10px 24px rgba(0,0,0,.14);
}
/* 스티키 색상 */
:root{
  --note-yellow:#fff8b8;
  --note-blue:#dff3f9;
  --note-pink:#ffe0e4;
  --note-orange:#ffe8d1;
  --note-green:#d1f7e1;
}
[data-priority='1']{background:var(--note-pink);}
[data-priority='2']{background:var(--note-orange);}
[data-priority='3']{background:var(--note-yellow);}
[data-priority='4']{background:var(--note-blue);}
[data-priority='5']{background:var(--note-green);}
/* DONE 흐림 */
[data-status='DONE']{opacity:.55;}

/* 헤더 & 본문 */
.note-header h6{margin:0 0 .6rem;font-weight:700;font-size:1.05rem;}
.note-desc{font-size:.9rem;line-height:1.45;white-space:pre-wrap;}

/* 핀(PIN) 데코 */
.sticky-note::before{
  content:'';
  position:absolute;
  top:12px; left:50%; transform:translateX(-50%);
  width:18px; height:18px; border-radius:50%;
  background:#343a40; border:4px solid #fff;
  box-shadow:0 2px 6px rgba(0,0,0,.25);
}

/* 푸터 */
.note-footer{margin-top:1rem;display:flex;justify-content:space-between;align-items:center;}
.badge.priority{
  background:rgba(0,0,0,.18);color:#fff;font-size:.7rem;padding:.25rem .7rem;border-radius:99px;
}
.actions .btn-icon{border:none;background:none;font-size:1.1rem;cursor:pointer;margin-left:.3rem;}
.btn-icon.done{color:#16a34a;}
.btn-icon.del{color:#ef4444;}

/* 4) “+” 카드 */
.add-note{
  border:3px dashed #c3cad5;
  color:#64748b;background:#f7f9fc;
  align-items:center;justify-content:center;display:flex;
  transition:background .2s ease,border-color .2s ease;
}
.add-note:hover{
  background:#ebf0f9;border-color:#94a3b8;
}

/* 5) 모바일 대응 */
@media(max-width:768px){
  .board-wrap{padding:56px 20px 120px;}
  .summary-card{flex-direction:column;gap:1rem;font-size:.95rem;}
}

</style>
