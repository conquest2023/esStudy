<template>
  <nav class="pagination-container my-4">
    <div class="pagination-grid">

      <div class="nav-side left">
        <button class="p-btn icon" :disabled="page === 0" @click="change(0)">
          <i class="fas fa-angle-double-left"></i>
        </button>
        <button class="p-btn text d-none d-md-block" :disabled="page === 0" @click="change(page - 1)">
          이전
        </button>
        <button class="p-btn icon d-md-none" :disabled="page === 0" @click="change(page - 1)">
          <i class="fas fa-angle-left"></i>
        </button>
      </div>

      <div class="num-center">
        <button
            v-for="p in visiblePages"
            :key="p"
            class="num-btn"
            :class="{ active: p === page }"
            @click="change(p)"
        >
          {{ p + 1 }}
        </button>
      </div>

      <div class="nav-side right">
        <button class="p-btn text d-none d-md-block" :disabled="page >= totalPages - 1" @click="change(page + 1)">
          다음
        </button>
        <button class="p-btn icon d-md-none" :disabled="page >= totalPages - 1" @click="change(page + 1)">
          <i class="fas fa-angle-right"></i>
        </button>
        <button class="p-btn icon" :disabled="page >= totalPages - 1" @click="change(totalPages - 1)">
          <i class="fas fa-angle-double-right"></i>
        </button>
      </div>

    </div>
  </nav>
</template>

<script setup>
import { computed } from 'vue'
const props = defineProps({
  page: { type: Number, required: true },
  totalPages: { type: Number, required: true }
})
const emit = defineEmits(['change'])

const visiblePages = computed(() => {
  const current = props.page
  const total = props.totalPages
  // 모바일에서는 현재 페이지 기준 좌우 1개씩(총 3개)만 보여줌으로써 공간 확보
  const windowSize = window.innerWidth < 576 ? 1 : 2

  let start = Math.max(0, current - windowSize)
  let end = Math.min(total - 1, current + windowSize)

  // 보정 로직
  if (start === 0) end = Math.min(total - 1, windowSize * 2)
  if (end === total - 1) start = Math.max(0, total - (windowSize * 2) - 1)

  const pages = []
  for (let i = start; i <= end; i++) pages.push(i)
  return pages
})

function change(newPage) {
  if (newPage >= 0 && newPage < props.totalPages) emit('change', newPage)
}
</script>

<style scoped>
.pagination-container {
  width: 100%;
  display: flex;
  justify-content: center;
  padding: 0 10px;
}

.pagination-grid {
  display: grid;
  grid-template-columns: auto 1fr auto;
  align-items: center;
  gap: 8px;
  width: 100%;
  max-width: 500px;
}

.nav-side {
  display: flex;
  gap: 4px;
}

.num-center {
  display: flex;
  justify-content: center;
  gap: 4px;
  overflow: hidden; /* 숫자가 넘쳐도 화살표를 밀지 않음 */
}

/* 기본 버튼 스타일 */
.p-btn, .num-btn {
  border: 1px solid #e5e8eb;
  background: #fff;
  color: #4e5968;
  border-radius: 8px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.9rem;
  transition: 0.2s;
  cursor: pointer;
}

.p-btn:disabled {
  opacity: 0.3;
  cursor: not-allowed;
  background: #f2f4f6;
}

.p-btn.icon { min-width: 36px; }
.p-btn.text { padding: 0 12px; font-weight: 600; }

.num-btn {
  min-width: 36px;
  border: none;
}

.num-btn.active {
  background: #0d6efd;
  color: #fff;
  font-weight: 700;
}

/* 모바일 전용 미세 조정 */
@media (max-width: 576px) {
  .pagination-grid { gap: 4px; }
  .p-btn, .num-btn {
    height: 32px;
    min-width: 32px;
    font-size: 0.8rem;
  }
}
</style>