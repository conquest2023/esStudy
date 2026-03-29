<template>
  <div
      class="feed-item"
      :class="{ 'feed-item--notice': notice, 'feed-item--vote': isVoteCard }"
      v-lift
      @click="goToDetail"
  >
    <div class="feed-top">
      <div class="feed-badges">
        <span v-if="notice" class="chip chip--notice">공지</span>
        <span v-else-if="isVoteCard" class="chip chip--vote">투표</span>
        <span v-else-if="post.category" class="chip" :style="categoryStyle">{{ post.category }}</span>
        <span v-if="!notice && isNew" class="chip chip--new">NEW</span>
      </div>
    </div>

    <h6 class="feed-title">{{ post.title }}</h6>

    <div class="feed-footer">
      <div class="footer-left">
        <span v-if="userRankIndex !== -1" class="rank-icon">{{ rankIcon(userRankIndex) }}</span>
        <span v-if="isHotUser(post.username)" class="hot-icon">🔥</span>
        <span class="author">{{ post.username }}</span>
        <span class="dot">·</span>
        <span class="time">{{ time }}</span>
      </div>

      <div v-if="!notice" class="footer-right">
        <span class="stat">
          <i class="bi bi-eye"></i>
          {{ viewCount }}
        </span>
        <span class="stat stat--like">
          <i class="bi bi-heart"></i>
          {{ likeCount }}
        </span>
        <span v-if="commentCount" class="stat stat--comment">
          <i class="bi bi-chat"></i>
          {{ commentCount }}
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRankIcon } from '@/composables/useRankIcon.js'
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useSidebarStore } from '@/stores/sidebar.js'
import { storeToRefs } from 'pinia'

const props = defineProps({
  post:         { type: Object,  required: true },
  posts:        Array,
  notice:       { type: Boolean, default: false },
  isVote:       { type: Boolean, default: false },
  likeCount:    { type: Number,  default: null },
  commentCount: { type: Number,  default: 0 },
  page:         { type: Number,  default: 1 }
})

const CATEGORY_COLORS = {
  '자유':   { bg: '#f1f5f9', color: '#475569' },
  '자격증': { bg: '#f3e8ff', color: '#7c3aed' },
  '문제':   { bg: '#fff7ed', color: '#c2410c' },
  '기술':   { bg: '#eff6ff', color: '#1d4ed8' },
  '취업':   { bg: '#f0fdf4', color: '#15803d' },
  'Q/A':    { bg: '#fefce8', color: '#a16207' },
  '자료':   { bg: '#e0f2fe', color: '#0369a1' },
  '고민':   { bg: '#fdf2f8', color: '#be185d' },
}

const categoryStyle = computed(() => {
  const c = CATEGORY_COLORS[props.post.category]
  return c ? { background: c.bg, color: c.color } : { background: '#f1f5f9', color: '#475569' }
})

const NEW_THRESHOLD_MS = 6 * 60 * 60 * 1000
const isNew = computed(() => {
  const postCreatedTime    = new Date(props.post.createdAt).getTime()
  const latestCommentTime  = props.post.latestCommentTime ? new Date(props.post.latestCommentTime).getTime() : 0
  const now = Date.now()
  return (now - postCreatedTime) < NEW_THRESHOLD_MS ||
      (props.commentCount > 0 && (now - latestCommentTime) < NEW_THRESHOLD_MS)
})

const { rankIcon } = useRankIcon()
const router       = useRouter()
const isVoteCard   = computed(() => props.post?.category === '투표')

const sb = useSidebarStore()
const { topWriters, topRecentWriters } = storeToRefs(sb)

const userRankIndex = computed(() => {
  if (!topWriters.value || !props.post.username) return -1
  return topWriters.value.findIndex(w => w.username === props.post.username)
})

const topRecentSet = computed(() =>
    new Set((topRecentWriters.value ?? []).slice(0, 5).map(w => w.username))
)
function isHotUser(username) {
  return username ? topRecentSet.value.has(username) : false
}

const time = computed(() => {
  const raw = props.post?.createdAt
  if (!raw) return ''
  const d = new Date(raw)
  if (isNaN(d.getTime())) return ''
  const now = new Date()
  const diffMs = now - d
  const diffMin = Math.floor(diffMs / 60000)
  if (diffMin < 1)  return '방금'
  if (diffMin < 60) return `${diffMin}분 전`
  const diffH = Math.floor(diffMin / 60)
  if (diffH < 24)   return `${diffH}시간 전`
  return `${d.getMonth() + 1}.${d.getDate()}.`
})

const likeCount  = computed(() => props.likeCount ?? props.post?.likeCount ?? 0)
const viewCount  = computed(() => props.post?.viewCount ?? 0)

function goToDetail() {
  router.push({ name: 'PostDetail', params: { id: props.post.id }, query: { page: props.page } })
}
</script>

<style scoped>
.feed-item {
  background: #fff;
  border: 1px solid #f1f5f9;
  border-radius: 14px;
  padding: 1.1rem 1.4rem;
  margin-bottom: 0.6rem;
  cursor: pointer;
  transition: border-color 0.18s ease, box-shadow 0.18s ease, background 0.18s ease;
}

.feed-item:hover {
  border-color: #e2e8f0;
  background: #fafbfc;
  box-shadow: 0 4px 16px rgba(0,0,0,0.06);
}

.feed-item--notice {
  border-left: 3px solid #3b82f6;
  background: #fafcff;
}

/* 뱃지 영역 */
.feed-top {
  margin-bottom: 0.45rem;
}
.feed-badges {
  display: flex;
  align-items: center;
  gap: 0.35rem;
  flex-wrap: wrap;
}

.chip {
  display: inline-flex;
  align-items: center;
  padding: 0.15rem 0.55rem;
  border-radius: 6px;
  font-size: 0.72rem;
  font-weight: 700;
  letter-spacing: 0.01em;
  line-height: 1.6;
}
.chip--notice { background: #eff6ff; color: #2563eb; }
.chip--vote   { background: #f0fdf4; color: #16a34a; }
.chip--new    { background: #fef2f2; color: #ef4444; }

/* 제목 */
.feed-title {
  font-size: 1rem;
  font-weight: 600;
  color: #111827;
  letter-spacing: -0.01em;
  line-height: 1.5;
  margin: 0 0 0.7rem 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 하단 메타 */
.feed-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.footer-left {
  display: flex;
  align-items: center;
  gap: 0.3rem;
  font-size: 0.8rem;
  color: #9ca3af;
  flex-wrap: wrap;
}
.rank-icon  { font-size: 0.85rem; }
.hot-icon   { font-size: 0.8rem; }
.author     { font-weight: 500; color: #6b7280; }
.dot        { color: #d1d5db; }
.time       { color: #9ca3af; }

.footer-right {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.stat {
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
  font-size: 0.78rem;
  color: #9ca3af;
  font-weight: 500;
}
.stat i { font-size: 0.78rem; }
.stat--like    { color: #f87171; }
.stat--comment { color: #60a5fa; }

@media (max-width: 576px) {
  .feed-item  { padding: 0.9rem 1rem; border-radius: 12px; }
  .feed-title { font-size: 0.95rem; }
  .footer-left, .footer-right { font-size: 0.75rem; }
}
</style>