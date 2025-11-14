<template>
  <ul class="list-group">
    <li v-if="!items.length" class="list-group-item text-center text-muted">
      작성된 댓글이 없습니다.
    </li>
    <li v-for="comment in items" :key="comment.postId" class="list-group-item">
      <div class="comment-item">
        <div><strong>댓글 내용:</strong> {{ comment.content || '내용 없음' }}</div>
        <div><strong>작성일자:</strong> {{ formatDate(comment.createdAt) }}</div>
        <div><strong>좋아요:</strong> {{ comment.likeCount || 0 }}개</div>
        <div class="mt-2">
          <RouterLink
              class="btn btn-sm btn-primary"
              :to="`/post/${comment.postId}`">
            게시물 보러 가기
          </RouterLink>
        </div>
      </div>
    </li>
  </ul>
</template>

<script setup>
import { RouterLink } from 'vue-router'

const props = defineProps({
  items: Array
})

function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('ko-KR')
}
</script>

<style scoped>
.comment-item {
  padding: 10px;
  border-bottom: 1px solid #ddd;
}
</style>
