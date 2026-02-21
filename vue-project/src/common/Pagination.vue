<template>
  <nav v-if="totalPages > 1" class="my-5 d-flex justify-content-center">
    <ul class="modern-pagination">
      <li class="page-item" :class="{ disabled: page === 0 }" @click="emitPage(0)">
        <span class="page-link"><i class="fas fa-angle-double-left"></i></span>
      </li>

      <li v-if="startPage > 0" class="page-item" @click="emitPage(startPage - 1)">
        <span class="page-link"><i class="fas fa-angle-left"></i></span>
      </li>

      <li
          v-for="p in visiblePages" :key="p"
          class="page-item"
          :class="{ active: p === page }"
          @click="emitPage(p)"
      >
        <span class="page-link">{{ p + 1 }}</span>
      </li>

      <li v-if="endPage < totalPages - 1" class="page-item" @click="emitPage(endPage + 1)">
        <span class="page-link"><i class="fas fa-angle-right"></i></span>
      </li>

      <li class="page-item" :class="{ disabled: page === totalPages - 1 }" @click="emitPage(totalPages - 1)">
        <span class="page-link"><i class="fas fa-angle-double-right"></i></span>
      </li>
    </ul>
  </nav>
</template>

<script setup>
// 기존 로직과 동일 (수정 불필요)
import {computed} from 'vue'

const props = defineProps({
  page: Number,
  totalPages: Number
})
const emit = defineEmits(['change'])

const MAX = 10
const startPage = computed(() => Math.floor(props.page / MAX) * MAX)
const endPage = computed(() => Math.min(props.totalPages - 1, startPage.value + MAX - 1))
const visiblePages = computed(() => Array.from({length: endPage.value - startPage.value + 1}, (_, i) => startPage.value + i))

function emitPage(p) {
  if (p !== props.page && p >= 0 && p < props.totalPages) {
    emit('change', p)
  }
}
</script>

<style scoped>
/* 모던 독립형 페이지네이션 */
.modern-pagination {
  display: flex;
  gap: 6px; /* 버튼 사이 여백 */
  list-style: none;
  padding: 0;
  margin: 0;
  align-items: center;
}

.page-item {
  cursor: pointer;
}

.page-link {
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 36px;
  height: 36px;
  padding: 0 8px;
  background: transparent;
  border: none;
  border-radius: 10px; /* 부드러운 둥근 네모 */
  color: #64748b;
  font-size: 0.95rem;
  font-weight: 500;
  transition: all 0.2s ease;
}

/* 호버 시 부드러운 회색 배경 */
.page-item:not(.disabled):not(.active):hover .page-link {
  background: #f1f5f9;
  color: #0f172a;
}

/* 활성화된 현재 페이지 */
.page-item.active .page-link {
  background: #2563eb;
  color: #ffffff;
  font-weight: 700;
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3); /* 살짝 떠보이는 효과 */
  transform: translateY(-1px);
}

/* 비활성화 상태 */
.page-item.disabled {
  cursor: not-allowed;
  opacity: 0.4;
}

.page-item.disabled .page-link {
  pointer-events: none;
}

/* 폰트어썸 아이콘 크기 조정 */
.page-link i {
  font-size: 0.8rem;
}
</style>