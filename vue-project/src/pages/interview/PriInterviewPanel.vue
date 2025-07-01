<template>
  <div class="container py-4">
    <!-- Header -->
    <header class="d-flex align-items-center gap-3 mb-4">
      <i class="fas fa-building fs-2 text-success"></i>
      <h1 class="h3 fw-bold mb-0">사기업 면접 질문 뱅크</h1>
    </header>

    <!-- 🔥 이번 주 인기 TOP 5 -->
    <section class="mb-4">
      <div class="d-flex justify-content-between align-items-center mb-2">
        <h6 class="fw-semibold mb-0">
          <i class="fas fa-fire me-2 text-danger"></i>이번 주 인기 TOP 5
        </h6>
        <button
            class="btn btn-sm btn-outline-secondary"
            @click.stop="toggleTrending"
            style="z-index:1; position: relative;"
        >
          <i :class="isTrendingOpen ? 'fas fa-chevron-up' : 'fas fa-chevron-down'"></i>
        </button>
      </div>

      <transition name="fade">
        <ul v-show="isTrendingOpen" class="list-group">
          <li
              v-for="(t, i) in trending"
              :key="t.id"
              class="list-group-item list-group-item-action d-flex justify-content-between align-items-start py-3 px-3 hover-shadow-sm cursor-pointer"
              @click="openQuestion(t)"
          >
            <div class="me-3">
              <div class="fw-bold mb-1 text-truncate">{{ i + 1 }}. {{ t.question }}</div>
              <small class="text-muted">카테고리: {{ t.category }} / {{ t.subCategory }}</small>
            </div>
          </li>
        </ul>
      </transition>
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
      <main class="col-lg-8">
        <div v-if="loading" class="text-center py-5 text-muted">
          <div class="spinner-border" role="status"></div>
        </div>

        <div v-if="!loading && questions && questions.length > 0">
          <div
              v-for="q in questions"
              :key="q.id"
              class="card mb-3 shadow-sm hover-shadow cursor-pointer"
              @click="handleQuestionClick(q)"
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

      <div class="d-flex justify-content-center my-4" v-if="totalPages > 1">
        <button class="btn btn-outline-secondary me-2" @click="prevPage" :disabled="page === 0">이전</button>
        <span class="align-self-center">Page {{ page + 1 }} / {{ totalPages }}</span>
        <button class="btn btn-outline-secondary ms-2" @click="nextPage" :disabled="page + 1 >= totalPages">다음</button>
      </div>
    </div>

    <!-- Question Modal -->
    <div class="modal fade" tabindex="-1" ref="questionModal">
      <div class="modal-dialog modal-lg modal-dialog-scrollable">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ active?.question }}</h5>
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
            <p class="text-muted small mb-1" v-if="active?.bestUsername">
              <i class="fas fa-user me-1"></i>{{ active.bestUsername }}님의 답변
            </p>
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
import { ref, reactive, onMounted } from 'vue'
import axios from 'axios'
import { Modal } from 'bootstrap'

// const seriesList = ['인성', '기술', '조직문화']
const filters = reactive({ series: ['사기업'] })

const query = ref('')
const loading = ref(false)
const questions = ref([])
const trending = ref([])
const related = ref([])
const totalPages = ref(0)
const totalElements = ref(0)
const page = ref(0)
const size = ref(10)

const active = ref(null)
const myAnswer = ref('')
const saving = ref(false)
const questionModal = ref(null)
const token = localStorage.getItem("token")
const isTrendingOpen = ref(true)
let bsModal = null

function toggleTrending() {
  isTrendingOpen.value = !isTrendingOpen.value
}

async function searchByQuery() {
  page.value = 0
  const keyword = query.value.trim()
  const { data } = await axios.get(`/api/search/interview/${encodeURIComponent(keyword)}`)
  questions.value = data.searchResult || []
  totalPages.value = 1
  totalElements.value = questions.value.length

  try {
    await axios.post('/api/interview/log', {
      eventType: 'search',
      targetId: null,
      query: keyword,
      category: '일반',
      // subCategory: filters.series[0] || null,
      subCategory: "사기업"
    }, {
      headers: {
        Authorization: token ? `Bearer ${token}` : undefined
      }
    })
  } catch (e) {
    console.warn('검색 로그 저장 실패:', e)
  }
}

async function saveAnswer() {
  if (!myAnswer.value || myAnswer.value.trim() === '') {
    alert('답변을 입력해주세요.')
    return
  }

  saving.value = true
  try {
    const token = localStorage.getItem('token')
    if (!token) {
      alert('로그인이 필요합니다.')
      return
    }

    const dto = {
      questionId: active.value?.id,
      title: active.value?.question,
      answer: myAnswer.value
    }

    const res = await axios.post('/api/save/interview/question', dto, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })

    if (res.data.success) {
      alert(res.data.message || '답변이 저장되었습니다.')
      closeModal()
      myAnswer.value = ''
    } else {
      alert('답변 저장에 실패했습니다.')
    }
  } catch (err) {
    console.error(err)
    alert('서버 오류가 발생했습니다.')
  } finally {
    saving.value = false
  }
}

async function fetchBestAnswer() {
  if (!active.value?.id) return
  try {
    const res = await axios.get('/api/interview/best/answer', {
      headers: {
        'X-Question-Ids': active.value.id
      }
    })
    const answerList = res.data
    if (Array.isArray(answerList) && answerList.length > 0) {
      const best = answerList[0]
      active.value.answerKey = best.answer
      active.value.bestUsername = best.username || '익명'
    } else {
      active.value.answerKey = '<p class="text-muted">베스트 답변이 아직 없습니다.</p>'
      active.value.bestUsername = null
    }
  } catch (e) {
    console.warn('베스트 답변 불러오기 실패:', e)
    active.value.answerKey = '<p class="text-muted">불러오는 중 오류가 발생했습니다.</p>'
    active.value.bestUsername = null
  }
}

async function searchBySeries() {
  const selected = filters.series[0] || '인성'
  const { data } = await axios.get(`/api/interview/일반/${selected}`, {
    params: { page: page.value, size: size.value }
  })
  questions.value = data.interview || []
  totalPages.value = data.totalPages
  totalElements.value = data.totalElements
}

async function search() {
  loading.value = true
  try {
    if (query.value && query.value.trim() !== '') {
      await searchByQuery()
    } else {
      await searchBySeries()
    }
  } finally {
    loading.value = false
  }
}

function toggleSeries(s) {
  filters.series = [s]
  query.value = ''
  page.value = 0
  search()
}

function nextPage() {
  if (page.value < totalPages.value - 1) {
    page.value++
    searchBySeries()
  }
}

function prevPage() {
  if (page.value > 0) {
    page.value--
    searchBySeries()
  }
}

async function fetchTrending() {
  const subCategory = filters.series[0] || '인성'
  try {
    const { data } = await axios.get(`/api/interview/aggregation/${subCategory}`)
    trending.value = data.aggregation || []
  } catch (e) {
    console.warn('TOP5 조회 실패:', e)
  }
}

async function logInterviewClick(question) {
  const token = localStorage.getItem('token') || null
  const logData = {
    eventType: 'click',
    userId: null,
    username: null,
    targetId: question.id,
    query: null,
    category: question.category || '사기업',
    subCategory: question.subCategory || null,
  }
  try {
    await axios.post('/api/interview/log', logData, {
      headers: token ? { Authorization: `Bearer ${token}` } : {}
    })
  } catch (e) {
    console.warn('로그 저장 실패:', e.message)
  }
}

function handleQuestionClick(q) {
  openQuestion(q)
  fetchBestAnswer()
  logInterviewClick(q)
}

function openQuestion(q) {
  active.value = q
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

const formatDate = dt => new Date(dt).toLocaleDateString()

onMounted(() => {
  fetchTrending()
  searchBySeries()
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

.hover-shadow:hover {
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15);
}
</style>
