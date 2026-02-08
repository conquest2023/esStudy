<template>
  <div class="mt-3 p-3 border rounded">
    <div class="d-flex justify-content-between align-items-center mb-2">
      <div class="fw-semibold">
        <span v-if="isCorrect" class="badge text-bg-success me-2">정답</span>
        <span v-else class="badge text-bg-danger me-2">오답</span>
        해설
      </div>
      <div class="text-muted small">내답: {{ myAns }} / 정답: {{ ans }}</div>
    </div>

    <div class="text-muted small mb-2">포인트</div>
    <div style="white-space: pre-wrap;">{{ explanation }}</div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  question: Object,
  isCorrect: Boolean,
  selectedIndex: Number,
  answerIndex: Number,
})

const options = computed(() => props.question.content?.questions?.[0]?.options ?? [])
const explanation = computed(() => props.question.content?.questions?.[0]?.explanation ?? '해설이 없습니다.')

const myAns = computed(() => props.selectedIndex != null ? options.value[props.selectedIndex] : '-')
const ans = computed(() => props.answerIndex != null ? options.value[props.answerIndex] : '-')
</script>
