<template>
  <div class="container py-5">
    <div class="mb-4 text-center">
      <h2 class="fw-bold text-primary">📝 문제 풀기</h2>
      <p class="text-muted mb-0">한 문제씩 차근차근 해결해보세요!</p>
    </div>

    <div v-if="questions.length > 0" class="card question-card p-4 mb-4">
      <div class="d-flex justify-content-between align-items-center mb-3">
        <small class="text-muted fw-bold">{{ currentIndex + 1 }} / {{ questions.length }}</small>
        <div>
          <span class="badge bg-primary">{{ currentQuestion.category }}</span>
          <span class="badge bg-success ms-2">{{ currentQuestion.type }}</span>
        </div>
      </div>

      <!-- 문제 내용 -->
      <pre class="bg-light p-3 rounded">
        <code class="text-dark">{{ currentQuestion.questionText }}</code>
      </pre>

      <small class="text-danger fw-bold">난이도: {{ currentQuestion.difficulty ?? '-' }}</small>

      <textarea v-model="userAnswer" class="form-control mt-3" rows="3" placeholder="답안을 작성해보세요."></textarea>
      <button class="btn btn-primary mt-3" @click="toggleExplanation">정답 확인</button>

      <div v-if="showExplanation" class="explanation-area mt-4 border rounded p-3 bg-light">
        <h6 class="fw-bold">정답: <span>{{ currentQuestion.modelAnswer }}</span></h6>
        <p class="mb-0">해설: <span>{{ currentQuestion.explanation }}</span></p>
      </div>

      <!-- 이전 / 다음 문제 -->
      <div class="d-flex justify-content-between mt-4">
        <button class="btn btn-outline-secondary" @click="prevQuestion" :disabled="currentIndex === 0">← 이전 문제</button>
        <button class="btn btn-outline-primary" @click="nextQuestion" :disabled="currentIndex === questions.length - 1">다음 문제 →</button>
      </div>
    </div>
    <div v-else class="text-center text-muted">불러온 문제가 없습니다.</div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'

const questions = ref([])
const currentIndex = ref(0)
const userAnswer = ref('')
const showExplanation = ref(false)

const currentQuestion = computed(() => questions.value[currentIndex.value] || {})

function toggleExplanation() {
  showExplanation.value = !showExplanation.value
}

function nextQuestion() {
  if (currentIndex.value < questions.value.length - 1) {
    currentIndex.value++
    userAnswer.value = ''
    showExplanation.value = false
  }
}

function prevQuestion() {
  if (currentIndex.value > 0) {
    currentIndex.value--
    userAnswer.value = ''
    showExplanation.value = false
  }
}
onMounted(() => {
  try {
    const stored = localStorage.getItem('questions')
    if (stored && stored !== 'undefined') {
      questions.value = JSON.parse(stored)
    } else {
      console.warn('questions 없음 또는 undefined임')
      questions.value = []
    }
  } catch (e) {
    console.error('JSON 파싱 실패:', e)
    questions.value = []
  }
})

</script>

<style scoped>
body {
  font-family: 'Inter', sans-serif;
  background: linear-gradient(135deg, #f2f6fc, #e3ecfa 70%);
  min-height: 100vh;
}
.question-card {
  border: none;
  border-radius: 1rem;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
}
.custom-logo {
  font-size: 1.8rem;
  padding-left: 100px;
}
pre code {
  font-family: 'Courier New', monospace;
  font-size: 0.95rem;
}
pre {
  white-space: pre-wrap;
  max-height: 400px;
  overflow-x: auto;
}
</style>
