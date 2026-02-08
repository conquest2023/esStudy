<template>
  <div class="d-flex gap-2 mt-3">
    <button class="btn btn-outline-secondary px-3 fw-bold" @click="$emit('prev')">
      <i class="fa-solid fa-chevron-left me-1"></i> 이전
    </button>

    <button
        v-if="!submitted"
        class="btn btn-dark flex-grow-1 fw-black py-2"
        :disabled="!canSubmit"
        @click="$emit('submit')"
    >
      제출하기
    </button>

    <button
        v-else
        class="btn btn-primary flex-grow-1 fw-black py-2"
        @click="$emit('next')"
    >
      다음 문제 <i class="fa-solid fa-chevron-right ms-1"></i>
    </button>

    <button
        class="btn px-3 fw-bold"
        :class="wrongNoteSaved ? 'btn-outline-success' : 'btn-outline-danger'"
        @click="$emit('toggleWrongNote')"
        :disabled="!submitted && !canSubmit"
        title="제출 후 오답 저장 추천"
    >
      <i class="fa-solid fa-book-bookmark"></i>
    </button>
  </div>
</template>

<script setup>
defineProps({
  canSubmit: Boolean,
  submitted: Boolean,
  wrongNoteSaved: Boolean,
})
defineEmits(['submit', 'next', 'prev', 'toggleWrongNote'])
</script>

<style scoped>
.fw-black { font-weight: 800; }
</style>
