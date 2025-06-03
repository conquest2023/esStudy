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

        <!-- 서브 카테고리 선택 -->
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
        <!-- 문제 리스트 -->
        <div v-if="questions.length">
          <h6 class="fw-bold mb-3">📌 오늘의 문제</h6>
          <ul class="list-group list-group-flush">
            <li
                v-for="(q, i) in questions"
                :key="q.id || i"
                class="list-group-item px-3 py-2 border-0"
            >
              <div class="fw-semibold mb-2">
                <i class="fas fa-question-circle text-primary me-2"></i>
                {{ i + 1 }}. {{ q.question }}
              </div>

              <!-- ✅ 보기 출력: 배열을 줄마다 1~4번 붙여서 보여줌 -->
              <ul class="mb-2 ps-3 small">
                <li
                    v-for="(choice, idx) in parseChoices(q.choices)"
                    :key="idx">
                  {{ idx + 1 }}. {{ choice }}
                </li>
              </ul>


              <div class="text-muted small">
<!--                <span class="fw-bold">정답:</span> {{ q.answer }}-->
              </div>
            </li>
          </ul>
        </div>


        <!-- 선택됐는데 질문이 없을 경우 -->
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

const categories = {
  '공무원': ['경찰', '일반행정'],
  '토익': [],
  '정처기': ['실기']
}

const subcategories = computed(() =>
    selectedCategory.value ? categories[selectedCategory.value] : []
)

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
function parseChoices(raw) {
  if (!raw) return []
  try {
    return typeof raw === 'string' ? JSON.parse(raw) : raw
  } catch {
    return []
  }
}

async function loadQuestions() {
  const target = selectedSub.value || selectedCategory.value
  if (!target) return
  try {
    const { data } = await api.get('/toeic', {
      params: { category: target }
    })
    questions.value = data.toeic ?? []
    console.log(questions.value)
  } catch (err) {
    console.error('문제 불러오기 실패:', err)
    questions.value = []
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

.fade-enter-active, .fade-leave-active {
  transition: all 0.3s ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
