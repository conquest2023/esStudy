<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const notifications = ref([])

onMounted(() => {
  notifications.value = JSON.parse(localStorage.getItem('notifications') ?? '[]')
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
        <small class="text-muted">{{ new Date(n.id).toLocaleString() }}</small>
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
