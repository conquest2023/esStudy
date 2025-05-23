<!-- src/components/RecommendBox.vue -->
<template>
  <aside class="recommend-box card shadow-sm p-3 mb-3">
    <h6 class="fw-bold mb-3">🌟 추천 글</h6>
    <div v-if="feeds.length">
      <article v-for="feed in feeds" :key="feed.feedUID" class="mb-2">
        <router-link :to="`/search/view/feed/id/${feed.feedUID}`"
                     class="text-dark text-decoration-none">
          <p class="mb-0 fw-semibold text-truncate">{{ feed.title }}</p>
          <small class="text-muted">{{ feed.username }} · {{ formatDate(feed.createdAt) }}</small>
        </router-link>
      </article>
    </div>
    <div v-else class="text-muted small text-center">추천 글 없음</div>
  </aside>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '@/utils/api'

const feeds = ref([])

onMounted(async () => {
  try {
    const { data } = await api.get('/search/view/feed/recommend')
    feeds.value = data.recommend ?? []
  } catch (e) {
    console.error('추천 글 로딩 실패', e)
  }
})

function formatDate(dateStr) {
  const d = new Date(dateStr)
  return `${d.getMonth() + 1}.${d.getDate()}`
}
</script>

<style scoped>
.recommend-box {
  width: 240px;
}
</style>
