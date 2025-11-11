<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/utils/api'

const router = useRouter()
const notifications = ref([])

function formatDate(dateStr) {
  const date = new Date(dateStr)
  return `${date.getMonth() + 1}.${date.getDate()} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}
onMounted(async () => {
  try {
    const {data} = await api.get('/notifications/all', {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('token')}`
      }
    })
    notifications.value = data
  } catch (e) {
    console.error('알림 불러오기 실패', e)
  }
})

</script>

<template>
  <div class="container py-4">
    <h3 class="fw-bold mb-4">전체 알림</h3>

    <ul class="list-group shadow-sm rounded">
      <li v-for="n in notifications" :key="n.id"
          class="list-group-item d-flex justify-content-between align-items-start">
        <router-link :to="'/search/view/feed/id/' + n.feedUID" class="text-decoration-none me-3">
          {{ n.message }}
        </router-link>
        <div class="small text-muted mt-1">{{ formatDate(n.createdAt) }}</div>
      </li>
      <li v-if="!notifications.length" class="list-group-item text-center text-muted">
        도착한 알림이 없습니다
      </li>
    </ul>

    <button class="btn btn-outline-secondary mt-4" @click="router.back()">← 돌아가기</button>
  </div>
</template>

<style scoped>
.list-group-item { border:none; border-bottom:1px solid #eee; }
</style>
