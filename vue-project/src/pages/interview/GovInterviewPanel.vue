<template>
  <div class="container py-4">
    <!-- Header -->
    <header class="d-flex align-items-center gap-3 mb-4">
      <i class="fas fa-user-shield fs-2 text-primary"></i>
      <h1 class="h3 fw-bold mb-0">공무원 면접 질문 뱅크</h1>
    </header>

    <!-- Trending Top‑5 -->
    <section class="mb-4">
      <h6 class="fw-semibold mb-2"><i class="fas fa-fire me-2 text-danger"></i>이번 주 인기 TOP 5</h6>
      <ul class="list-group list-group-horizontal-md overflow-auto flex-nowrap">
        <li
            v-for="t in trending"
            :key="t.id"
            class="list-group-item list-group-item-action rounded shadow-sm me-2 cursor-pointer min-width-200"
            @click="openQuestion(t)"
        >
          <div class="small text-truncate">{{ t.title }}</div>
          <span class="badge bg-secondary mt-1"><i class="fas fa-eye me-1"></i>{{ t.viewCount }}</span>
        </li>
      </ul>
    </section>

    <!-- Search & Filters -->
    <div class="row g-3 mb-4">
      <div class="col-md-8">
        <div class="input-group">
          <input
              v-model="query"
              @keyup.enter="search"
              type="search"
              class="form-control"
              placeholder="키워드·질문 입력 후 Enter"
          />
          <button class="btn btn-primary" @click="search">검색</button>
        </div>
      </div>
      <div class="col-md-4">
        <div class="d-flex flex-wrap gap-2">
          <span
              v-for="s in seriesList"
              :key="s"
              @click="toggleSeries(s)"
              :class="[
              'badge rounded-pill py-2 px-3 cursor-pointer',
              filters.series.includes(s) ? 'bg-primary text-white' : 'bg-light text-dark'
            ]"
          >
            {{ s }}
          </span>
        </div>
      </div>
    </div>

    <div class="row">
      <!-- Question List -->
      <main class="col-lg-8">
        <div v-if="loading" class="text-center py-5 text-muted">
          <div class="spinner-border" role="status"></div>
        </div>

        <div v-if="!loading && questions && questions.length > 0">
          <div
              v-for="q in questions"
              :key="q.id"
              class="card mb-3 shadow-sm hover-shadow cursor-pointer"
              @click="openQuestion(q)"
          >
            <div class="card-body">
              <h5 class="card-title mb-2 text-truncate-2">{{ q.question }}</h5>
              <p class="card-text small text-muted mb-1">카테고리: {{ q.category }} / {{ q.subCategory }}</p>
              <div class="d-flex justify-content-between text-muted small">
                <span><i class="fas fa-clock me-1"></i>{{ formatDate(q.createdAt) }}</span>
              </div>
            </div>
          </div>
        </div>

      </main>

      <!-- Sidebar -->
      <aside class="col-lg-4 d-none d-lg-block">
        <h6 class="fw-semibold mb-3">유사 질문 Top 3</h6>
        <ul class="list-group mb-4">
          <li
              v-for="rel in related.slice(0, 3)"
              :key="rel.id"
              class="list-group-item list-group-item-action"
              @click="openQuestion(rel)"
          >
            {{ rel.title }}
          </li>
        </ul>
      </aside>
    </div>

    <!-- Question Modal -->
    <div class="modal fade" tabindex="-1" ref="questionModal">
      <div class="modal-dialog modal-lg modal-dialog-scrollable">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ active?.title }}</h5>
            <button type="button" class="btn-close" @click="closeModal"></button>
          </div>
          <div class="modal-body">
            <div v-html="active?.body" class="mb-4"></div>
            <hr/>
            <h6 class="fw-bold mb-2">내 답변</h6>
            <textarea
                v-model="myAnswer"
                class="form-control mb-3"
                rows="4"
                placeholder="여기에 답변을 작성해 보세요…"
            ></textarea>
            <button class="btn btn-success" @click="saveAnswer" :disabled="saving">
              <span v-if="!saving">저장</span>
              <span v-else class="spinner-border spinner-border-sm"></span>
            </button>
            <hr class="my-4"/>
            <h6 class="fw-bold mb-2">모범 답안 / 해설</h6>
            <div v-html="active?.answerKey"></div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="closeModal">닫기</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, reactive, onMounted} from 'vue'
import axios from 'axios'
import {Modal} from 'bootstrap'

const seriesList = ['행정직', '세무직', '교정직', '경찰직', '소방직']

const query = ref('')
const loading = ref(false)
const questions = ref([])
const related = ref([])
const trending = ref([])
const active = ref(null)
const myAnswer = ref('')
const saving = ref(false)
const questionModal = ref(null)
let bsModal = null

const filters = reactive({
  series: ['행정직']
})

function toggleSeries(s) {
  filters.series = [s]
  search()
}

async function fetchTrending() {
  const {data} = await axios.get('/api/interview/gov/trending')
  trending.value = data
}

async function search() {
  loading.value = true
  try {
    const selected = filters.series[0] || '행정직'
    const {data} = await axios.get(`/api/interview/공무원/${selected}`, {
      params: {q: query.value}
    })
    console.log('응답 확인:', data)
    questions.value = data.interview || []
    related.value = data.related || []
  } finally {
    loading.value = false
  }
}

function openQuestion(q) {
  active.value = q
  myAnswer.value = ''
  loadMyAnswer(q.id)
  showModal()
}

function showModal() {
  if (!bsModal) bsModal = new Modal(questionModal.value)
  bsModal.show()
}

function closeModal() {
  bsModal.hide()
  active.value = null
}

function formatDate(dt) {
  return new Date(dt).toLocaleDateString()
}

async function loadMyAnswer(qid) {
  try {
    const {data} = await axios.get(`/api/interview/answer/${qid}`)
    myAnswer.value = data.answer || ''
  } catch {
  }
}

async function saveAnswer() {
  if (!active.value) return
  saving.value = true
  try {
    await axios.post('/api/interview/answer', {
      id: active.value.id,
      answer: myAnswer.value
    })
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  fetchTrending()
  search()
})
</script>

<style scoped>
.cursor-pointer {
  cursor: pointer;
}

.text-truncate-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.text-truncate-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.hover-shadow:hover {
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15);
}

.min-width-200 {
  min-width: 200px;
}
</style>
