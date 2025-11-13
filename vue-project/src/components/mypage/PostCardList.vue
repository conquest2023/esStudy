<template>
  <div class="row g-3">
    <div v-if="!items.length" class="text-center text-muted w-100">
      작성된 항목이 없습니다.
    </div>
    <div v-for="post in items" :key="post.id" class="col-md-6 col-lg-4">
      <RouterLink :to="`/post/${post.id}`" class="text-decoration-none text-dark">
        <div class="card shadow-sm rounded-4 overflow-hidden position-relative hover-effect">
          <div
              class="feed-img position-relative"
              :style="{
              background: `url(${post.imageURL || 'https://source.unsplash.com/300x200/?abstract'}) center/cover no-repeat`,
              height: '180px'
            }"
          >
            <div class="overlay d-flex align-items-center justify-content-center">
              <button class="btn btn-light btn-sm">상세보기</button>
            </div>
          </div>
          <div class="card-body p-3">
            <h5 class="card-title text-truncate">{{ post.title || '제목 없음' }}</h5>
            <p class="card-text text-muted text-truncate">{{ post.description || '내용 없음' }}</p>
            <span class="badge bg-primary">{{ post.category || '카테고리 없음' }}</span>
          </div>
          <div class="card-footer d-flex justify-content-between align-items-center bg-light">
            <small class="text-muted">{{ formatDate(post.createdAt) }}</small>
            <div>
              <span class="text-muted me-2">
                <i class="fas fa-heart text-danger"></i> {{ post.likeCount || 0 }}
              </span>
              <span class="text-muted">
                <i class="fas fa-eye"></i> {{ post.viewCount || 0 }}
              </span>
            </div>
          </div>
        </div>
      </RouterLink>
    </div>
  </div>
</template>

<script setup>
import { RouterLink } from 'vue-router'

const props = defineProps({
  items: Array
})

function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const month = date.getMonth() + 1
  const day = date.getDate()
  let hour = date.getHours()
  const minute = date.getMinutes()
  const period = hour >= 12 ? '오후' : '오전'
  hour = hour % 12 || 12
  return `${month}.${day}. ${period} ${hour}:${minute.toString().padStart(2, '0')}`
}
</script>

<style scoped>
.feed-img {
  position: relative;
  overflow: hidden;
  border-radius: 10px 10px 0 0;
}
.feed-img .overlay {
  position: absolute;
  top: 0; left: 0;
  width: 100%; height: 100%;
  background: rgba(0, 0, 0, 0.4);
  opacity: 0;
  transition: opacity 0.3s ease-in-out;
}
.feed-img:hover .overlay {
  opacity: 1;
}
.card {
  transition: transform 0.3s ease-in-out;
}
.card:hover {
  transform: translateY(-5px);
}
</style>
