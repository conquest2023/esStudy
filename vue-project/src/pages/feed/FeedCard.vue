<template>
  <div
      class="feed-row"
      :class="{
      'feed-row--notice': notice,
      'feed-row--vote': isVoteCard
    }"
      v-lift
      @click="goToDetail"
  >
    <div class="feed-main">
      <!-- 제목 -->
      <div class="feed-title-line">
        <h6 class="feed-title text-truncate mb-0">
          <span v-if="notice">📢 </span>
          <span v-else-if="isVoteCard">🗳️ </span>
          {{ post.title }}
        </h6>

        <!-- 댓글 수 뱃지 -->
        <span
            v-if="!notice && !isVoteCard && commentCount"
            class="feed-badge-comment"
        >
          {{ commentCount }}
        </span>
      </div>

      <!-- 메타 정보 -->
      <div class="feed-meta">
        <span class="feed-meta__item">{{ post.username }}</span>
        <span class="feed-meta__dot">·</span>
        <span class="feed-meta__item">{{ time }}</span>

        <template v-if="!notice">
          <span class="feed-meta__dot">·</span>
          <span class="feed-meta__item">
            <i class="bi bi-eye me-1"></i>{{ viewCount }}
          </span>
          <span class="feed-meta__dot">·</span>
          <span class="feed-meta__item">
            <i class="bi bi-heart-fill me-1 text-like"></i>{{ likeCount }}
          </span>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'

const props = defineProps({
  post: { type: Object, required: true },
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
  if (isNaN(d.getTime())) return ''
  const ampm = d.getHours() >= 12 ? '오후' : '오전'
  const hh = (d.getHours() % 12) || 12
  const mm = d.getMinutes().toString().padStart(2, '0')
  return `${d.getMonth() + 1}.${d.getDate()}. ${ampm} ${hh}:${mm}`
})

// 스펙 호환
const likeCount = computed(() => props.post?.likeCount ?? 0)
const viewCount = computed(() => props.post?.viewCount ?? 0)

function goToDetail () {
  if (isVoteCard.value) {
    router.push({
      path: '/search/view/vote/detail',
      query: { id: props.post.id, page: props.page }
    })
    return
  }

  router.push({
    name: 'PostDetail',
    params: { id: props.post.id },
    query: { page: props.page }
  })
}
</script>

<style scoped>
/* 전체 한 줄(row) 스타일 */
.feed-row {
  padding: 0.75rem 0.4rem;
  border-bottom: 1px solid #f1f3f5;
  cursor: pointer;
  transition: background-color 0.15s ease, transform 0.04s ease;
  display: flex;
  align-items: center;
}

.feed-row:last-child {
  border-bottom: none;
}

.feed-row:hover {
  background: #f8fafc;
}

/* 공지 스타일 */
.feed-row--notice {
  background: #fff9e7;
  border-bottom-color: #f6e3b3;
}

.feed-row--notice:hover {
  background: #fff4cf;
}

/* 투표 카드 스타일 (살짝 강조) */
.feed-row--vote .feed-title {
  color: #15803d;
}

/* 안쪽 레이아웃 */
.feed-main {
  flex: 1;
  min-width: 0;
}

/* 제목 라인: 제목 + 댓글 뱃지 */
.feed-title-line {
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.feed-title {
  font-size: 0.95rem;
  font-weight: 600;
  letter-spacing: -0.01em;
}

/* 댓글 수 뱃지 */
.feed-badge-comment {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 1.4rem;
  padding: 0 0.25rem;
  font-size: 0.72rem;
  font-weight: 700;
  border-radius: 999px;
  background: #fee2e2;
  color: #b91c1c;
}

/* 메타 정보 라인 */
.feed-meta {
  margin-top: 0.15rem;
  font-size: 0.78rem;
  color: #6b7280;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 0.15rem;
}

.feed-meta__item {
  display: inline-flex;
  align-items: center;
}

.feed-meta__dot {
  color: #d1d5db;
}

/* 좋아요 색 */
.text-like {
  color: #ef4444 !important;
}

/* 모바일 대응 */
@media (max-width: 576px) {
  .feed-row {
    padding: 0.65rem 0.25rem;
  }

  .feed-title {
    font-size: 0.9rem;
  }

  .feed-meta {
    font-size: 0.74rem;
  }
}
</style>
