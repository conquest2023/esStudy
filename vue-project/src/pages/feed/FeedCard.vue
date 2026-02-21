<template>
  <div
      class="feed-item"
      :class="{ 'feed-item--notice': notice, 'feed-item--vote': isVoteCard }"
      v-lift
      @click="goToDetail"
  >
    <div class="feed-main">
      <div class="feed-title-line">
        <h6 class="feed-title text-truncate mb-0">
          <span v-if="notice" class="notice-badge">📢 공지</span>
          <span v-else-if="isVoteCard" class="vote-badge">🗳️ 투표</span>
          {{ post.title }}
        </h6>
        <span v-if="!notice && isNew" class="badge-pill badge-new">New</span>
        <span v-if="!notice && commentCount" class="badge-pill badge-comment">
          💬 {{ commentCount }}
        </span>
      </div>

      <div class="feed-meta">
        <div class="meta-left">
          <span class="meta-author">
            <span v-if="userRankIndex !== -1">{{ rankIcon(userRankIndex) }}</span>
            <span v-if="isHotUser(post.username)" class="hot-fire" aria-label="top recent">🔥</span>
            {{ post.username }}
          </span>
          <span class="meta-dot">·</span>
          <span class="meta-time">{{ time }}</span>
        </div>

        <div v-if="!notice" class="meta-right">
          <span class="meta-stat">
            <i class="fas fa-eye"></i> {{ viewCount }}
          </span>
          <span class="meta-stat like-stat">
            <i class="fas fa-heart"></i> {{ likeCount }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>


<script setup>
import {useRankIcon} from "@/composables/useRankIcon.js";

import { computed } from 'vue'
import { useRouter } from 'vue-router'
import {useSidebarStore} from "@/stores/sidebar.js";
import {storeToRefs} from "pinia";

const props = defineProps({
  post: { type: Object, required: true },
  posts: Array,
  notice: { type: Boolean, default: false },
  isVote: { type: Boolean, default: false },
  likeCount: { type: Number, default: null },
  commentCount: { type: Number, default: 0 },
  page: { type: Number, default: 1 }
})
const NEW_THRESHOLD_MS = 6 * 60 * 60 * 1000;

const isNew = computed(() => {
  const postCreatedTime = new Date(props.post.createdAt).getTime();

  const latestCommentTime = props.post.latestCommentTime
      ? new Date(props.post.latestCommentTime).getTime()
      : 0;
  const now = Date.now();
  const isPostNew = (now - postCreatedTime) < NEW_THRESHOLD_MS;
  const isCommentActivityNew = props.commentCount > 0 &&
      (now - latestCommentTime) < NEW_THRESHOLD_MS;

  return isPostNew || isCommentActivityNew;
});
const { rankIcon } = useRankIcon()
const router = useRouter()
const isVoteCard = computed(
    () => props.post?.category === '투표'
)
const sb = useSidebarStore()
const { topWriters, topRecentWriters } = storeToRefs(sb)
const userRankIndex = computed(() => {
  if (!topWriters.value || !props.post.username) {
    return -1
  }
  const index = topWriters.value.findIndex(
      writer => writer.username === props.post.username
  )

  return index
})

const topRecentSet = computed(() =>
    new Set((topRecentWriters.value ?? []).slice(0, 5).map(w => w.username))
)
function isHotUser(username) {
  if (!username) return false
  return topRecentSet.value.has(username) // 최근 Top5만 불꽃
}
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
const likeCount = computed(() =>
    props.likeCount ?? props.post?.likeCount ?? 0
)
const viewCount = computed(() => props.post?.viewCount ?? 0)

function goToDetail () {
  // if (isVoteCard.value) {
  //   router.push({
  //     path: '/post/',
  //     query: { id: props.post.id, page: props.page }
  //   })
  //   return
  // }

  router.push({
    name: 'PostDetail',
    params: { id: props.post.id },
    query: { page: props.page }
  })
}
</script>

<style scoped>
.feed-item {
  background: #ffffff;
  border: 1px solid #f1f5f9;
  border-radius: 16px; /* 더 둥글고 부드러운 모서리 */
  padding: 1.25rem 1.5rem;
  margin-bottom: 0.875rem;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.02);
}

/* 마이크로 인터랙션: 호버 시 부드럽게 떠오름 */
.feed-item:hover {
  border-color: #cbd5e1;
  box-shadow: 0 12px 24px -6px rgba(0, 0, 0, 0.05), 0 4px 10px -4px rgba(0, 0, 0, 0.03);
  transform: translateY(-2px);
}

.feed-item--notice {
  background: #f8fafc;
  border-left: 4px solid #3b82f6;
}

.feed-title-line {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.6rem;
}

.feed-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: #0f172a; /* 완전한 검은색보다 세련된 딥 네이비/그레이 */
  letter-spacing: -0.01em;
}

/* 뱃지 디자인 개선 (배경색과 텍스트 색상 매칭) */
.badge-pill {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0.2rem 0.6rem;
  border-radius: 9999px;
  font-size: 0.75rem;
  font-weight: 700;
  line-height: 1;
}

.badge-new {
  background: #fee2e2;
  color: #ef4444;
}

.badge-comment {
  background: #eff6ff;
  color: #3b82f6;
  font-weight: 600;
}

.notice-badge, .vote-badge {
  font-size: 0.85rem;
  font-weight: 700;
  margin-right: 4px;
}
.notice-badge { color: #3b82f6; }
.vote-badge { color: #10b981; }

/* 메타 정보 (작성자, 시간, 조회수 등) */
.feed-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #64748b;
  font-size: 0.85rem;
}

.meta-left, .meta-right {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.meta-author {
  font-weight: 500;
  color: #475569;
}

.meta-dot {
  color: #cbd5e1;
}

.meta-stat {
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
}

.meta-stat i {
  font-size: 0.8rem;
}

.like-stat {
  color: #ef4444;
}

@media (max-width: 576px) {
  .feed-item {
    padding: 1rem;
    border-radius: 12px;
  }
  .feed-title { font-size: 1rem; }
}
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
