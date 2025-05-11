<template>
  <section class="search-result-wrap">
    <h2 class="fs-5 mb-4 d-flex align-items-center">
      <i class="fas fa-search me-2 text-primary"></i>
      '{{ keyword }}' 검색 결과
    </h2>

    <div v-if="results.length" class="result-list">
      <div
          v-for="item in results"
          :key="item.feedUID"
          class="card feed-card shadow-sm mb-3"
          @click="goDetail(item.feedUID)">
        <div class="card-body d-flex justify-content-between align-items-center">
          <div class="me-2 flex-grow-1">
            <h6 class="fw-bold mb-1 text-truncate">{{ item.title }}</h6>
            <small class="text-muted">
              {{ item.username }} · {{ formatTime(item.createdAt) }}
            </small>
          </div>
          <div class="d-flex align-items-center text-muted small gap-3 flex-shrink-0">
            <span><i class="bi bi-eye me-1"></i>{{ item.viewCount }}</span>
            <span><i class="bi bi-heart-fill text-danger me-1"></i>{{ item.likeCount }}</span>
          </div>
        </div>
      </div>
    </div>

    <p v-else class="text-muted">검색 결과가 없습니다.</p>
  </section>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '@/utils/api'

const route = useRoute()
const router = useRouter()
const keyword = ref(route.query.text || '')
const results = ref([])

onMounted(fetchSearch)
watch(() => route.query.text, () => {
  keyword.value = route.query.text
  fetchSearch()
})

async function fetchSearch() {
  if (!keyword.value) return
  const { data } = await api.get('/search/content', { params: { text: keyword.value } })
  results.value = data.data ?? []
}

function goDetail(id) {
  router.push({ name: 'feed-detail', params: { id } })
}

function formatTime(dateStr) {
  return new Date(dateStr).toLocaleString('ko-KR', { hour12: false })
}
</script>

<style scoped>
.search-result-wrap {
  max-width: 750px;
  margin: 0 auto;
  padding: 2rem 1rem;
}

.feed-card {
  cursor: pointer;
  transition: background 0.15s;
}
.feed-card:hover {
  background: #f8f9fa;
}
</style>
