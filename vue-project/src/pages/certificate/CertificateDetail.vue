<template>
  <div class="container my-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h2 class="mb-0">{{ pageTitle }}</h2>
      <RouterLink to="/certificate/data" class="btn btn-outline-primary">목록으로 돌아가기</RouterLink>
    </div>

    <div class="row" v-if="loading">
      <div class="col-12 text-muted text-center">자료를 불러오는 중...</div>
    </div>

    <div class="row" v-else-if="contentList.length === 0">
      <div class="col-12 text-muted text-center">자료가 없습니다.</div>
    </div>

    <div class="row g-4" v-else>
      <div class="col-md-4" v-for="item in contentList" :key="item.feedUID">
        <div class="card h-100 mb-4">
          <div class="card-body">
            <h5 class="card-title">{{ item.title }}</h5>
            <p class="card-text text-muted">
              <RouterLink
                  :to="`/search/view/feed/id/${item.feedUID}`"
                  class="text-decoration-none text-muted"
              >
                {{ truncate(item.description || '설명 없음', 10) }}
              </RouterLink>
            </p>
            <p class="card-text text-secondary small">
              조회수: {{ item.viewCount }} / 좋아요: {{ item.likeCount }}<br />
              작성일: {{ item.createdAt?.substring(0, 10) }}
            </p>
            <RouterLink
                :to="`/search/view/feed/id/${item.feedUID}`"
                class="btn btn-sm btn-outline-primary"
            >
              상세보기
            </RouterLink>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const text = route.query.text || ''
const pageTitle = ref(`"${text}" 관련 자료 로딩 중...`)
const contentList = ref([])
const loading = ref(true)

function truncate(str, len) {
  return str.length > len ? str.substring(0, len) + '...' : str
}

onMounted(async () => {
  if (!text) {
    pageTitle.value = '잘못된 접근입니다.'
    loading.value = false
    return
  }

  try {
    const { data } = await axios.get('/search/content', { params: { text } })
    contentList.value = data.data || []
    pageTitle.value = `"${text}" 관련 자료 (${contentList.value.length}개)`
  } catch (err) {
    pageTitle.value = '데이터를 불러오는 중 오류 발생'
  } finally {
    loading.value = false
  }
})
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
