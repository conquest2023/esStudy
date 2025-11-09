<template>
  <div class="card feed-card" v-lift :class="notice ? 'notice' : ''" @click="goToDetail">
    <div class="card-body d-flex justify-content-between align-items-center">
      <div class="me-2 flex-grow-1">
        <h6 class="fw-bold mb-1 text-truncate">
          <span v-if="notice">📢&nbsp;</span>
          <span v-else-if="isVoteCard">🗳️&nbsp;</span>
          {{ post.title }}
          <span v-if="!notice && !isVoteCard && commentCount" class="text-danger fw-bold ms-1">[{{ commentCount }}]</span>
        </h6>

        <small class="text-muted">
          {{ post.username }} · {{ time }}
        </small>
      </div>

      <div v-if="!notice && !isVoteCard" class="d-flex align-items-center meta-stats small gap-3 flex-shrink-0">
        <span><i class="bi bi-eye me-1"></i>{{ viewCount }}</span>
        <span><i class="bi bi-heart-fill text-danger me-1"></i>{{ likeCount }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'

const props = defineProps({
  post: { type: Object, required: true }, // 목록/상세 공통 객체
  posts: Array,
  notice: { type: Boolean, default: false },
  isVote: { type: Boolean, default: false },
  commentCount: { type: Number, default: 0 },
  page: { type: Number, default: 1 }
})

const router = useRouter()

// 목록에서 투표카드 판별: id 없으면 투표 카드로 간주 (기존 로직 유지)
const isVoteCard = computed(() => !props.notice && !props.post?.id)


const time = computed(() => {
  const raw = props.post?.createdAt
  if (!raw) return ''
  const d = new Date(raw)
  if (isNaN(d.getTime())) return '' // 잘못된 날짜 문자열 방어
  const ampm = d.getHours() >= 12 ? '오후' : '오전'
  const hh = (d.getHours() % 12) || 12
  const mm = d.getMinutes().toString().padStart(2, '0')
  return `${d.getMonth() + 1}.${d.getDate()}. ${ampm} ${hh}:${mm}`
})

// 스펙 호환: imageURL/imageUrl, like/view 기본값 방어
const imageURL = computed(() => props.post?.imageURL ?? props.post?.imageUrl ?? null)
const likeCount = computed(() => props.post?.likeCount ?? 0)
const viewCount = computed(() => props.post?.viewCount ?? 0)

// function goToDetail() {
//   router.push({
//     path: isVoteCard.value ? '/search/view/vote/detail' : `/post/${props.post.id}`,
//     query: isVoteCard.value ? {id: props.post.id, page: props.page} : {page: props.page}
//   })
// }
function goToDetail () {
  router.push({
    name: 'PostDetail', // 라우터에 name 등록돼 있어야 함
    params: { id: props.post.id },
    query: { page: props.page }
  })
}
</script>

<style scoped>
.feed-card {
  border: none;
  box-shadow: var(--elev-1, 0 1px 3px rgba(0, 0, 0, .08));
  border-radius: var(--radius-lg);
  cursor: pointer;
  font-weight: 500;
  background: var(--c-surface, #fff);
  color: var(--c-text)
}

.feed-card .text-muted {
  color: var(--c-text-muted)
}

.feed-card .meta-stats {
  color: var(--c-meta-stats)
}

.feed-card.notice {
  border: 2px solid #f0ad4e
}

@media (max-width: 576px) {
  .feed-card .card-body > .me-2 {
    max-width: 75%
  }
}
</style>
