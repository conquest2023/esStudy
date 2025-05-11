<template>
  <div class="container py-4">
    <!-- Todo Summary -->
    <div v-if="todos.length" class="card mb-3">
      <div class="card-body d-flex flex-column flex-md-row align-items-center justify-content-between">
        <div class="mb-2 mb-md-0">
          전체 Todo: {{ todos.length }}개 / 완료: {{ completedCount }}개
        </div>
        <div class="progress" style="width: 50%; min-width:200px;">
          <div
              class="progress-bar"
              role="progressbar"
              :style="{ width: progress + '%' }"
              :aria-valuenow="progress"
              aria-valuemin="0"
              aria-valuemax="100"
          >
            {{ progress }}%
          </div>
        </div>
      </div>
    </div>

    <!-- Todo Table -->
    <div class="card shadow-sm mb-4">
      <div class="card-header bg-white d-flex justify-content-between align-items-center">
        <strong>오늘의 할일</strong>
        <router-link to="/todo/new" class="btn btn-sm btn-primary">
          <i class="fas fa-plus"></i> 새 Todo 추가
        </router-link>
      </div>
      <div class="card-body p-0">
        <table class="table table-hover align-middle mb-0">
          <thead class="table-light">
          <tr>
            <th style="width: 40%;">제목</th>
            <th>상태</th>
            <th>우선순위</th>
            <th class="text-end">작업</th>
          </tr>
          </thead>
          <tbody>
          <template v-for="todo in todos" :key="todo.todo_id">
            <tr>
              <td>
                <a href="#" @click.prevent="toggleDetail(todo.todo_id)">
                  {{ todo.title }}
                </a>
              </td>
              <td>
                  <span class="badge badge-status" :data-status="todo.status">
                    {{ statusText(todo.status) }}
                  </span>
              </td>
              <td>
                  <span class="badge priority-badge" :data-priority="todo.priority">
                    P{{ todo.priority }}
                  </span>
              </td>
              <td class="text-end">
                <button
                    class="btn btn-sm btn-outline-success me-2"
                    @click="completeTodo(todo)"
                    :disabled="todo.status !== 'IN_PROGRESS'">
                  완료
                </button>
                <button class="btn btn-sm btn-outline-danger" @click="deleteTodo(todo.todo_id)">
                  삭제
                </button>
              </td>
            </tr>
            <tr v-if="showDetails[todo.todo_id]" class="todo-details">
              <td colspan="4" class="p-3 bg-light">
                <strong>설명:</strong> {{ todo.description || '설명 없음' }}<br>
                <strong>우선순위:</strong> {{ todo.priority }}
              </td>
            </tr>
          </template>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 프로젝트 섹션 -->
    <div class="card shadow-sm">
      <div class="card-header bg-white d-flex justify-content-between align-items-center">
        <strong>장기 목표 및 프로젝트</strong>
        <button class="btn btn-success" data-bs-toggle="modal" data-bs-target="#addProjectModal">
          <i class="fas fa-clock"></i> 프로젝트 추가
        </button>
      </div>
      <div class="card-body p-0">
        <div class="table-responsive">
          <table class="table table-hover align-middle mb-0">
            <thead class="table-light">
            <tr>
              <th style="width: 30%;">제목</th>
              <th>상태</th>
              <th>우선순위</th>
              <th>마감일</th>
              <th class="text-end">작업</th>
            </tr>
            </thead>
            <tbody id="project-table-body"></tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- D-Day 일정 섹션 -->
    <div class="card shadow-sm mt-5">
      <div class="card-header bg-white d-flex justify-content-between align-items-center">
        <strong>내 D-DAY 일정</strong>
        <button class="btn btn-success btn-sm" @click="showDDayModal = true">
          <i class="fas fa-clock"></i> D-DAY 추가
        </button>
      </div>
      <div class="card-body p-0">
        <div class="table-responsive">
          <table class="table table-hover align-middle mb-0">
            <thead class="table-light">
            <tr>
              <th>시험명</th>
              <th>시험일</th>
              <th>D-DAY</th>
              <th>목표</th>
              <th class="text-end">작업</th>
            </tr>
            </thead>
            <tbody>
            <tr v-if="dDays.length === 0">
              <td colspan="5" class="text-center text-muted py-4">등록된 D-DAY가 없습니다.</td>
            </tr>
            <tr v-for="dd in dDays" :key="dd.id">
              <td>{{ dd.examName }}</td>
              <td>{{ dd.examDate }}</td>
              <td>
                <strong>
                  {{ dd.dday === 0 ? '시험 당일!' : dd.dday < 0 ? '시험 지남' : 'D-' + dd.dday}}
                </strong>
              </td>
              <td>{{ dd.goal || '' }}</td>
              <td class="text-end">
                <button class="btn btn-sm btn-danger" @click="deleteDDay(dd.id)">삭제</button>
              </td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>
      <DDayModal :visible="showDDayModal" @close="showDDayModal = false" @refresh="fetchDDays" />

    </div>


    <!-- Kibana Chart -->
    <div class="card text-center shadow-sm p-4 mt-5">
      <h4 class="mb-3">📊 Todo 완료율 분석</h4>
      <iframe id="kibana-chart" height="600" width="800" :src="kibanaUrl"></iframe>
      <p class="mt-3 text-muted">최근 1주일 동안 완료된 Todo 추이</p>
    </div>
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
.badge-status[data-status='TODO'] {
  background-color: #6c757d;
}

.badge-status[data-status='IN_PROGRESS'] {
  background-color: #0d6efd;
}

.badge-status[data-status='DONE'] {
  background-color: #198754;
}

.priority-badge[data-priority='1'] {
  background-color: #dc3545;
}

.priority-badge[data-priority='2'] {
  background-color: #fd7e14;
}

.priority-badge[data-priority='3'] {
  background-color: #ffc107;
}

.priority-badge[data-priority='4'] {
  background-color: #20c997;
}

.priority-badge[data-priority='5'] {
  background-color: #0dcaf0;
}
</style>
