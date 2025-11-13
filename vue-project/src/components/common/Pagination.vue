<template>
  <nav class="d-flex justify-content-center my-4" aria-label="Page navigation">
    <ul class="pagination">

      <!-- 이전 버튼 -->
      <li class="page-item" :class="{ disabled: page === 0 }">
        <button class="page-link" @click="change(page - 1)" :disabled="page === 0">
          이전
        </button>
      </li>

      <!-- 페이지 번호 -->
      <li
          v-for="p in pages"
          :key="p"
          class="page-item"
          :class="{ active: p === page }"
      >
        <button class="page-link" @click="change(p)">
          {{ p + 1 }}
        </button>
      </li>

      <!-- 다음 버튼 -->
      <li class="page-item" :class="{ disabled: page >= totalPages - 1 }">
        <button class="page-link" @click="change(page + 1)" :disabled="page >= totalPages - 1">
          다음
        </button>
      </li>

    </ul>
  </nav>
</template>

<script setup>
import { computed } from 'vue'

// props
const props = defineProps({
  page: { type: Number, required: true },
  totalPages: { type: Number, required: true }
})

// emit
const emit = defineEmits(['change'])

// 페이지 번호 뿌리기
const pages = computed(() => {
  const arr = []
  for (let i = 0; i < props.totalPages; i++) {
    arr.push(i)
  }
  return arr
})

function change(newPage) {
  if (newPage < 0 || newPage >= props.totalPages) return
  emit('change', newPage)
}
</script>

<style scoped>
.page-link {
  cursor: pointer;
}
</style>
