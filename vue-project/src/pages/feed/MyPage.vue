<template>
  <div class="mypage container mt-3 mt-md-4">

    <!-- 프로필 헤더 -->
    <section class="profile-header card-soft p-3 p-md-4 mb-3 mb-md-4">
      <div class="d-flex align-items-center gap-3">
        <!-- 아바타(임시 이니셜) 필요 없으면 제거 가능 -->
        <div class="avatar">
          <span>{{ (username || 'U').charAt(0).toUpperCase() }}</span>
        </div>

        <div class="flex-grow-1">
          <div class="d-flex align-items-center gap-2 flex-wrap">
            <h2 class="name m-0">{{ username }}</h2>
            <span class="point-chip">
              <i class="fas fa-bolt me-1"></i>{{ point.toLocaleString() }}
            </span>
          </div>
          <div class="sub text-muted mt-1">아이디 <strong>{{ userId }}</strong></div>
        </div>

        <!-- (선택) 통계 아이콘 버튼 -->
        <button class="btn btn-icon d-none d-md-inline-flex" type="button" title="통계">
          <i class="fas fa-chart-pie"></i>
        </button>
      </div>
    </section>

    <!-- 통계 그리드 -->
    <section class="stats-grid mb-3 mb-md-4">
      <div class="stat-card" v-for="s in stats" :key="s.label">
        <div class="icon">
          <i :class="s.icon"></i>
        </div>
        <div class="meta">
          <div class="label">{{ s.label }}</div>
          <div class="value" :class="s.color">{{ (s.value ?? 0).toLocaleString() }}</div>
        </div>
      </div>
    </section>

    <!-- 탭 (모바일 가로 스크롤, 데스크탑 탭) -->
    <section class="tabs-wrap mb-3 mb-md-4">
      <div class="segmented-tabs" role="tablist">
        <button
            v-for="t in tabs"
            :key="t.id"
            type="button"
            class="seg-btn"
            :class="{ active: activeTab === t.id }"
            @click="activeTab = t.id"
        >
          {{ t.label }}
        </button>
      </div>
    </section>

    <!-- 콘텐츠 -->
    <section class="tab-content mt-3">
      <div v-show="activeTab === 'posts'">
        <PostCardList :items="feedList" type="feed"/>
      </div>

      <div v-show="activeTab === 'comments'">
        <CommentList :items="comments" />
      </div>

      <div v-show="activeTab === 'commentedPosts'">
        <PostCardList :items="commentedPosts" type="commented" />
      </div>

      <div v-show="activeTab === 'likedPosts'">
        <PostCardList :items="likedPosts" type="liked" />
      </div>

      <div v-show="activeTab === 'bookmarkedQuestions'">
        <BookmarkList :items="bookmarkedQuestions"/>
      </div>

      <Pagination class="mt-4" :page="page" :totalPages="totalPages" @change="changePage"/>
    </section>

  </div>
</template>


<script setup>
import { ref, onMounted, watch } from 'vue'
import { RouterLink } from 'vue-router'
import PostCardList from '@/components/mypage/PostCardList.vue'
import CommentList from '@/components/mypage/CommentList.vue'
import BookmarkList from '@/components/mypage/BookmarkList.vue'
import Pagination from '@/components/common/Pagination.vue'
import api from '@/utils/api'

const token = localStorage.getItem('token')

// 사용자 정보
const username = ref('')
const userId = ref('')
const point = ref(0)

// 탭
const activeTab = ref('posts')
const tabs = [
  { id: 'posts', label: '내가 작성한 게시글' },
  { id: 'comments', label: '작성한 댓글' },
  { id: 'commentedPosts', label: '댓글 단 게시물' },
  { id: 'likedPosts', label: '좋아요 한 글' },
  { id: 'bookmarkedQuestions', label: '북마크한 문제' }
]

// 데이터 저장
const feedList = ref([])
const comments = ref([])
const commentedPosts = ref([])
const likedPosts = ref([])
const bookmarkedQuestions = ref([])

// 페이징
const page = ref(0)
const size = ref(10)
const totalPages = ref(1)

// 통계
const stats = ref([
  { label: '게시물 수', icon: 'fas fa-pen', value: 0, color: 'text-primary' },
  { label: '댓글 수', icon: 'fas fa-comments', value: 0, color: 'text-success' },
  { label: '받은 좋아요', icon: 'fas fa-heart', value: 0, color: 'text-danger' },
  { label: '방문 수', icon: 'fas fa-eye', value: 0, color: 'text-info' }
])

// ----------------------------------
// API 요청 함수
// ----------------------------------

async function fetchUserInformation() {
  const { data } = await api.get('/user/information', auth())
  username.value = data.username
  userId.value = data.userId
}

async function fetchPoint() {
  const { data } = await api.get('/mypage/point', auth())
  point.value = data.point.userPoint
  stats.value[2].value = data.point.likeCount
}

// async function fetchStats() {
//   const { data } = await api.get('/mypage', auth())
//   stats.value[0].value = data.feedCount
//   stats.value[1].value = data.commentCount
//   stats.value[2].value = data.like
//   stats.value[3].value = data.visitCount
// }

async function loadPage() {
  if (activeTab.value === 'posts') await loadPosts()
  else if (activeTab.value === 'comments') await loadComments()
  else if (activeTab.value === 'commentedPosts') await loadCommentedPosts()
  else if (activeTab.value === 'likedPosts') await loadLikedPosts()
  else if (activeTab.value === 'bookmarkedQuestions') await loadBookmarks()
}

/* 1. 작성한 게시글 */
async function loadPosts() {
  const { data } = await api.get(`/mypage/post/paging?page=${page.value}&size=${size.value}`, auth())
  feedList.value = data.posts ?? []
  totalPages.value = data.totalPage ?? 1

  stats.value[0].value = data.totalCountPost ?? 0
}

async function loadComments() {
  const { data } = await api.get(`/mypage/comment/paging?page=${page.value}&size=${size.value}`, auth())
  comments.value = data.comments
  totalPages.value = data.totalPages ?? 1
  stats.value[1].value = data.totalCountComment ?? 0
}

async function loadCommentedPosts() {
  const { data } = await api.get(`/mypage/post/comment/paging?page=${page.value}&size=${size.value}`, auth())
  commentedPosts.value = data.all
  totalPages.value = data.totalPages ?? 1
}

// async function loadLikedPosts() {
//   const { data } = await api.get(`/mypage/feed/like/paging?page=${page.value}&size=${size.value}`, auth())
//   totalPages.value = data.totalPages ?? 1
// }

async function loadBookmarks() {
  const { data } = await api.get(`/get/daily/bookmark?page=${page.value}&size=${size.value}`, auth())
  bookmarkedQuestions.value = data.bookmarks
  totalPages.value = data.totalPages ?? 1
}

function auth() {
  return { headers: { Authorization: `Bearer ${token}` } }
}

watch(activeTab, () => {
  page.value = 0
  loadPage()
})
function changePage(newPage) {
  page.value = newPage
  loadPage()
}

onMounted(async () => {
  if (!token) return
  await fetchUserInformation()
  await fetchPoint()
  await loadPosts()
  // await fetchStats()
})
</script>

<style scoped>
/* 공통 카드 톤 */
.card-soft {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 6px 18px rgba(0,0,0,.06);
  border: 1px solid rgba(0,0,0,.04);
}

/* 프로필 헤더 */
.profile-header .name {
  font-weight: 800;
  letter-spacing: -0.02em;
  color: #2563eb; /* text-primary보다 덜 튀는 블루 */
  font-size: clamp(1.25rem, 5.5vw, 2rem);
}
.profile-header .sub { font-size: .95rem; }

.avatar {
  width: 56px; height: 56px; border-radius: 50%;
  background: #eef2ff; color: #3b82f6;
  display: grid; place-items: center;
  font-weight: 800; font-size: 1.1rem;
  box-shadow: inset 0 0 0 1px rgba(59,130,246,.25);
}

.point-chip {
  display: inline-flex; align-items: center; gap: .25rem;
  background: #fff7ed; color: #ea580c;
  border: 1px solid #fed7aa; border-radius: 999px;
  padding: .25rem .6rem; font-weight: 700; font-size: .9rem;
}

/* 통계: 모바일 2열, md이상 4열 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}
@media (min-width: 768px) {
  .stats-grid { grid-template-columns: repeat(4, 1fr); gap: 14px; }
}

.stat-card {
  background: #fff;
  border-radius: 14px;
  padding: 12px;
  box-shadow: 0 4px 12px rgba(0,0,0,.05);
  display: flex; align-items: center; gap: 10px;
  border: 1px solid rgba(0,0,0,.04);
}
.stat-card .icon {
  width: 40px; height: 40px; border-radius: 12px;
  display: grid; place-items: center;
  background: #f8fafc; color: #64748b;
}
.stat-card .icon i { font-size: 16px; }
.stat-card .meta .label { font-size: .85rem; color: #6b7280; }
.stat-card .meta .value {
  margin-top: 2px; font-weight: 800; font-size: 1.25rem; letter-spacing: -.02em;
}

/* 탭: 모바일 가로 스크롤 세그먼트 */
.tabs-wrap { position: sticky; top: 0; background: transparent; z-index: 5; }
.segmented-tabs {
  display: flex; gap: 6px; overflow-x: auto; padding: 4px;
  border: 1px solid #e5e7eb; border-radius: 12px; background: #f9fafb;
  scroll-snap-type: x proximity;
}
.segmented-tabs::-webkit-scrollbar { display: none; }

.seg-btn {
  scroll-snap-align: start;
  flex: 0 0 auto;
  border: 0; background: transparent;
  padding: 8px 12px; border-radius: 10px;
  font-size: .92rem; color: #374151; font-weight: 600;
  transition: background .15s ease, color .15s ease, transform .05s ease;
}
.seg-btn:hover { background: #eef2f7; }
.seg-btn.active { background: #2563eb; color: #fff; box-shadow: 0 2px 6px rgba(37,99,235,.25); }
.seg-btn:active { transform: translateY(1px); }

/* 리스트 여백 */
.tab-content :deep(.post-card),
.tab-content :deep(.comment-card),
.tab-content :deep(.bookmark-card) {
  border-radius: 14px;
}

/* 기존 클래스 살림 */
.stat-box { /* (이전 호환용) 미사용시 제거 가능 */
  text-align: center;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 10px;
  box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.1);
}

.btn-icon {
  width: 40px; height: 40px; border-radius: 10px;
  display: inline-grid; place-items: center;
  border: 1px solid rgba(0,0,0,.08);
  background: #fff;
}
.btn-icon i { color: #2563eb; }
</style>

