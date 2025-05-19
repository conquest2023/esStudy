<template>
  <div class="container my-5">
    <!-- 헤더 -->
    <div class="text-center mb-5">
      <h1 class="fw-bold">자격증 자료</h1>
      <p class="text-muted">관심 있는 자격증을 클릭하여 상세 정보를 확인하세요</p>
    </div>

    <!-- 카드 리스트 -->
    <div class="row g-4">
      <div class="col-12 col-sm-6 col-lg-4" v-for="cert in paginatedData" :key="cert.title">
        <div class="card h-100 shadow-sm">
          <div class="card-body d-flex flex-column">
            <RouterLink
                :to="{ path: '/certificate/detail', query: { text: cert.title } }"
                class="stretched-link text-decoration-none"
            >
              {{ cert.title }}
            </RouterLink>
<!--            </h5>-->
          </div>
        </div>
      </div>
    </div>

    <!-- 페이지네이션 -->
    <nav class="d-flex justify-content-center mt-4">
      <ul class="pagination">
        <li class="page-item">
          <button class="page-link" @click="prevPage" :disabled="page === 0">이전</button>
        </li>
        <li class="page-item disabled">
          <span class="page-link">{{ page + 1 }}</span>
        </li>
        <li class="page-item">
          <button class="page-link" @click="nextPage" :disabled="isLastPage">다음</button>
        </li>
      </ul>
    </nav>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useUserStore } from '@/stores/user'
const store = useUserStore()
const page = ref(0)
const size = 10
const mockData = [
  { title: "SQLD" }, { title: "정보처리기사" }, { title: "한국사" }, { title: "리눅스마스터2급" },
  { title: "빅데이터 분석기사" }, { title: "오픽" }, { title: "토익" }, { title: "컴활 2급" },
  { title: "정보처리산업기사" }, { title: "네트워크관리사" }
]

const paginatedData = computed(() => {
  const start = page.value * size
  return mockData.slice(start, start + size)
})

const isLastPage = computed(() => {
  return (page.value + 1) * size >= mockData.length
})

function prevPage() {
  if (page.value > 0) page.value--
}

function nextPage() {
  if (!isLastPage.value) page.value++
}
</script>

<style scoped>
.card {
  transition: transform 0.2s, box-shadow 0.2s;
}
.container {
  max-width: 1200px;
  margin: 50px auto;
  padding-top: 80px;
}
.card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
}
</style>
