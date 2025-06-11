<template>
  <div class="card daily-questions-card">
    <div class="card-header d-flex justify-content-between align-items-center">
      <div class="fw-bold fs-5">
        <i class="fas fa-calendar-alt me-2 text-primary"></i>하루 3문제
      </div>
      <button class="btn btn-light btn-sm rounded-pill shadow-sm" @click="expanded = !expanded">
        {{ expanded ? '접기' : '펼치기' }}
        <i :class="expanded ? 'fas fa-chevron-up' : 'fas fa-chevron-down'" class="ms-1"></i>
      </button>
    </div>

    <transition name="fade">
      <div v-if="expanded" class="card-body">
        <!-- 카테고리 선택 -->
        <div class="mb-4">
          <h6 class="text-muted fw-semibold mb-2">카테고리</h6>
          <div class="d-flex flex-wrap gap-2">
            <button
                v-for="cat in Object.keys(categories)"
                :key="cat"
                class="btn btn-outline-primary rounded-pill px-3 py-1"
                :class="{ active: selectedCategory === cat }"
                @click="selectCategory(cat)"
            >
              {{ cat }}
            </button>
          </div>
        </div>

        <!-- 세부 카테고리 선택 -->
        <div v-if="subcategories.length" class="mb-4">
          <h6 class="text-muted fw-semibold mb-2">세부 카테고리</h6>
          <div class="d-flex flex-wrap gap-2">
            <button
                v-for="sub in subcategories"
                :key="sub"
                class="btn btn-outline-secondary rounded-pill px-3 py-1"
                :class="{ active: selectedSub === sub }"
                @click="selectSub(sub)"
            >
              {{ sub }}
            </button>
          </div>
        </div>

        <!-- 문제 리스트 -->
        <div v-if="questions.length">
          <h6 class="fw-bold mb-3">📌 오늘의 문제</h6>
          <ul class="list-group list-group-flush">
            <li
                v-for="(q, i) in questions"
                :key="q.id || i"
                class="list-group-item px-3 py-2 border-0"
            >
              <div class="fw-semibold mb-2 d-flex justify-content-between align-items-center">
                <span>
                  <i class="fas fa-question-circle text-primary me-2"></i>
                  {{ i + 1 }}. {{ q.question }}
                </span>

                <!-- 북마크 -->
                <button
                    class="btn btn-sm"
                    :class="bookmarks[i] ? 'btn-warning' : 'btn-outline-secondary'"
                    @click="toggleBookmark(i)"
                >
                  <i :class="bookmarks[i] ? 'fas fa-star' : 'far fa-star'"></i>
                </button>
              </div>

              <!-- 보기 출력 (라디오 버튼) -->
              <ul class="mb-2 ps-3 small">
                <li v-for="(choice, idx) in parseChoices(q.choices)" :key="idx">
                  <label class="d-flex align-items-center gap-2">
                    <input
                        type="radio"
                        :name="'question-' + i"
                        :value="mapIndexToNumber(idx)"
                        v-model="userAnswers[i]"
                    />
                    {{ idx + 1 }}. {{ choice }}
                  </label>
                </li>
              </ul>

              <!-- 정답 제출 -->
              <div class="mt-2">
                <button class="btn btn-sm btn-primary" @click="submitAnswer(i, q)">
                  정답 제출
                </button>

                <div v-if="results[i] !== null" class="mt-1">
                  <span class="fw-bold" :class="results[i] ? 'text-success' : 'text-danger'">
                    {{ results[i] ? '정답입니다!' : '틀렸습니다.' }}
                  </span>
                </div>

                <div v-if="showAnswer[i]" class="mt-1 text-muted small">
                  <span class="fw-bold">정답:</span> {{ q.answer }}
                </div>
              </div>
            </li>
          </ul>
        </div>

        <div v-else-if="selectedCategory || selectedSub" class="text-muted small">
          문제가 없습니다.
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import api from '@/utils/api'

const expanded = ref(false)
const selectedCategory = ref('')
const selectedSub = ref('')
const questions = ref([])
const showAnswer = ref([])
const bookmarks = ref([])
const userAnswers = ref([])
const results = ref([])

const categories = {
  '공무원': ['경찰', '일반행정'],
  '토익': [],
  '정처기': ['실기']
}

const subcategories = computed(() =>
    selectedCategory.value ? categories[selectedCategory.value] : []
)

function toggleBookmark(index) {
  const question = questions.value[index]
  const isBookmarked = bookmarks.value[index]

  if (isBookmarked) {
    bookmarks.value[index] = false
  } else {
    api
        .post('/daily/bookmark', {
          questionId: question.id,
          category: selectedSub.value || selectedCategory.value
        })
        .then(() => {
          bookmarks.value[index] = true
        })
        .catch(err => {
          console.error('북마크 실패:', err)
        })
  }
}

function selectCategory(cat) {
  selectedCategory.value = cat
  selectedSub.value = categories[cat][0] || ''
  loadQuestions()
}

function selectSub(sub) {
  selectedSub.value = sub
  loadQuestions()
}

watch(selectedSub, () => {
  if (selectedSub.value) loadQuestions()
})
function mapIndexToNumber(index) {
  const map = ['①', '②', '③', '④', '⑤', '⑥']
  return map[index] || ''
}

function parseChoices(raw) {
  if (!raw) return []
  if (Array.isArray(raw)) return raw
  if (typeof raw === 'string' && raw.trim().startsWith('[')) {
    try {
      return JSON.parse(raw)
    } catch {}
  }
  return typeof raw === 'string' ? raw.split('||') : []
}

async function loadQuestions() {
  const target = selectedSub.value || selectedCategory.value
  if (!target) return

  let endpoint = ''
  let responseKey = ''

  if (selectedCategory.value === '토익') {
    endpoint = '/toeic'
    responseKey = 'toeic'
  } else if (selectedCategory.value === '공무원') {
    endpoint = selectedSub.value === '경찰' ? '/police' : '/civil'
    responseKey = selectedSub.value === '경찰' ? 'police' : 'civil'
  } else {
    endpoint = '/daily'
    responseKey = 'questions'
  }

  try {
    const { data } = await api.get(endpoint, { params: { category: target } })
    questions.value = data[responseKey] ?? []
    showAnswer.value = new Array(questions.value.length).fill(false)
    bookmarks.value = new Array(questions.value.length).fill(false)
    userAnswers.value = new Array(questions.value.length).fill(null)
    results.value = new Array(questions.value.length).fill(null)
  } catch (err) {
    console.error('문제 불러오기 실패:', err)
    questions.value = []
    showAnswer.value = []
    bookmarks.value = []
    userAnswers.value = []
    results.value = []
  }
}

async function submitAnswer(index, question) {
  const token = localStorage.getItem('token')

  if (!token) {
    alert('로그인이 필요합니다.')
    window.location.href = '/login'
    return
  }

  const userAnswerSymbol = userAnswers.value[index] // ex: "①"
  const choices = parseChoices(question.choices)   // 보기 배열
  const correctAnswerIndex = choices.findIndex(c =>
      c.trim() === question.answer.trim()
  )
  const correctAnswerSymbol = mapIndexToNumber(correctAnswerIndex)
  if (!userAnswerSymbol) {
    alert('보기를 선택해주세요!')
    return
  }

  try {
    const { data } = await api.post(
        '/check/daily',
        {
          category: selectedSub.value || selectedCategory.value,
          matter: question.question,
          answer: userAnswerSymbol,
          correct: correctAnswerSymbol
        },
        {
          headers: {
            Authorization: `Bearer ${token}`
          }
        }
    )

    results.value[index] = data.ok === true
    showAnswer.value[index] = true // ✅ 정답 자동 표시

  } catch (err) {

    if (err.response?.data?.message === '이미 문제를 제출하셨습니다') {
      alert('이미 제출한 문제입니다.')
    } else if (err.response?.status === 401) {
      alert('세션이 만료되었습니다. 다시 로그인해주세요.')
      window.location.href = '/login'
    } else {
      alert('정답 제출 중 오류가 발생했습니다.')
    }
  }
}
</script>

<style scoped>
.daily-questions-card {
  border-radius: 1rem;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.btn.active {
  background-color: #0d6efd !important;
  color: white !important;
  border-color: #0d6efd !important;
}

.fade-enter-active,
.fade-leave-active {
  transition: all 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
