<template>
  <ul class="list-group">
    <li v-for="(q, i) in items" :key="q.id || i" class="list-group-item">
      <div class="fw-semibold mb-2">
        {{ i + 1 }}. {{ q.question }}
      </div>

      <ul class="mb-2 ps-3 small">
        <li v-for="(choice, idx) in parseChoices(q.choices)" :key="idx">
          {{ idx + 1 }}. {{ choice }}
        </li>
      </ul>

      <button class="btn btn-sm btn-outline-primary mb-2" @click="toggleAnswer(i)">
        {{ showAnswers[i] ? '정답 숨기기' : '정답 보기' }}
      </button>

      <div v-if="showAnswers[i]" class="text-muted small">
        <span class="fw-bold">정답:</span> {{ q.answer }}
      </div>
    </li>
  </ul>
</template>

<script setup>
import { reactive } from 'vue'

const props = defineProps({ items: Array })

const showAnswers = reactive({})

// 정답 보기 토글
function toggleAnswer(index) {
  showAnswers[index] = !showAnswers[index]
}

function parseChoices(raw) {
  if (!raw) return []
  try {
    return typeof raw === 'string' ? JSON.parse(raw) : raw
  } catch {
    return []
  }
}
</script>
