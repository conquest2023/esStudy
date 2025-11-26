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
      <div class="feed-title-line">
        <h6 class="feed-title text-truncate mb-0">
          <span v-if="notice">📢 공지 | </span>
          <span v-else-if="isVoteCard">🗳️ 투표 | </span>
          {{ post.title }}
        </h6>
        <span
            v-if="!notice && isNew"
            class="feed-badge-new ms-1">
          N
        </span>
        <span
            v-if="!notice && commentCount"
            class="feed-badge-comment"
        >
          {{ commentCount }}
        </span>
      </div>

      <div class="feed-meta">
      <span class="feed-meta__item">
        <span v-if="userRankIndex !== -1">{{ rankIcon(userRankIndex) }}</span>
        {{ post.username }}
      </span>
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
const { topWriters } = storeToRefs(sb)
const userRankIndex = computed(() => {
  if (!topWriters.value || !props.post.username) {
    return -1
  }
  const index = topWriters.value.findIndex(
      writer => writer.username === props.post.username
  )

  return index
})

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

.feed-row--notice {
  background: #fff9e7;
  border-bottom-color: #f6e3b3;
}

.feed-row--notice:hover {
  background: #fff4cf;
}

.feed-row--vote .feed-title {
  color: #15803d;
}

.feed-main {
  flex: 1;
  min-width: 0;
}

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
.feed-badge-new {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 1.25rem; /* 너비 고정 */
  height: 1.25rem;
  padding: 0;
  font-size: 0.7rem;
  font-weight: 700;
  color: #dc2626;
  box-sizing: border-box;
  flex-shrink: 0;
}
.feed-meta__dot {
  color: #d1d5db;
}

.text-like {
  color: #ef4444 !important;
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
