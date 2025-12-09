<template>
  <div class="mypage with-fixed-bars container mt-3 mt-md-4">
    <!-- 프로필 헤더 -->
    <section class="profile-header card-soft p-3 p-md-4 mb-3 mb-md-4">
      <div class="d-flex align-items-center gap-3">
        <div class="avatar">
          <span>{{ (username || 'U').charAt(0).toUpperCase() }}</span>
        </div>
        <div class="flex-grow-1">
          <div class="d-flex align-items-center gap-2 flex-wrap">
            <h2 class="name m-0">{{ username }}</h2>
            <span class="point-chip">
              <i class="fas fa-bolt me-1"></i>{{ (user.point || 0).toLocaleString() }}
            </span>
          </div>
          <div class="sub text-muted mt-1">아이디 <strong>{{ userId }}</strong></div>
        </div>
        <button class="btn btn-icon d-none d-md-inline-flex" type="button" title="통계">
          <i class="fas fa-chart-pie"></i>
        </button>
      </div>
    </section>

    <!-- 통계 그리드 -->
    <section class="stats-grid mb-3 mb-md-4">
      <div class="stat-card" v-for="s in stats" :key="s.label">
        <div class="icon"><i :class="s.icon"></i></div>
        <div class="meta">
          <div class="label">{{ s.label }}</div>
          <div class="value" :class="s.color">{{ (s.value ?? 0).toLocaleString() }}</div>
        </div>
      </div>
    </section>

    <!-- 탭 -->
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
      <!-- posts -->
      <div v-show="activeTab === 'posts'">
        <PostCardList :items="feedList" type="feed" />
        <div class="mt-4 pager-sticky">
          <PaginationControls :paging="pagingInfo.posts" @go-to-page="goToPage" />
        </div>
      </div>

      <!-- comments -->
      <div v-show="activeTab === 'comments'">
        <CommentList :items="comments" />
        <div class="mt-4 pager-sticky">
          <PaginationControls :paging="pagingInfo.comments" @go-to-page="goToPage" />
        </div>
      </div>

      <!-- commentedPosts -->
      <div v-show="activeTab === 'commentedPosts'">
        <PostCardList :items="commentedPosts" type="commented" />
        <div class="mt-4 pager-sticky">
          <PaginationControls :paging="pagingInfo.commentedPosts" @go-to-page="goToPage" />
        </div>
      </div>

      <!-- likedPosts -->
      <div v-show="activeTab === 'likedPosts'">
        <PostCardList :items="likedPosts" type="liked" />
        <div class="mt-4 pager-sticky">
          <PaginationControls :paging="pagingInfo.likedPosts" @go-to-page="goToPage" />
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import PostCardList from '@/components/mypage/PostCardList.vue'
import CommentList from '@/components/mypage/CommentList.vue'
import PaginationControls from '@/components/common/PaginationControls.vue'

const route = useRoute()
const router = useRouter()
const token = localStorage.getItem('token')

// ---- 프로필/표시값 ----
const user = ref({ value: null, point: 0 })
const username = route.params.username ?? ''
const userId = username

// ---- 탭 & 데이터 ----
const activeTab = ref('posts')
const tabs = [
  { id: 'posts', label: '작성한 게시글' },
  { id: 'comments', label: '작성한 댓글' },
  { id: 'commentedPosts', label: '댓글 단 게시글' },
  { id: 'likedPosts', label: '좋아요한 글' },
]

const feedList = ref([])
const comments = ref([])
const commentedPosts = ref([])
const likedPosts = ref([])

// ---- 페이징 상태 ----
const pagingInfo = ref({
  posts:          { currentPage: 0, totalPages: 1, totalElements: 0, size: 10 },
  comments:       { currentPage: 0, totalPages: 1, totalElements: 0, size: 10 },
  commentedPosts: { currentPage: 0, totalPages: 1, totalElements: 0, size: 10 },
  likedPosts:     { currentPage: 0, totalPages: 1, totalElements: 0, size: 10 },
})

// ---- 통계 ----
const stats = ref([
  { label: '게시물 수',   icon: 'fas fa-pen',      value: 0, color: 'text-primary' },
  { label: '댓글 수',     icon: 'fas fa-comments', value: 0, color: 'text-success' },
  { label: '받은 좋아요', icon: 'fas fa-heart',    value: 0, color: 'text-danger' },
  { label: '방문 수',     icon: 'fas fa-eye',      value: 0, color: 'text-info' },
])

// ---- 공통 fetch 유틸 ----
async function fetchSomeoneData(endpoint, headers, username, page, size) {
  const params = new URLSearchParams()
  if (username) params.set('username', username)
  if (page !== null && page !== undefined) params.set('page', page)
  if (size !== null && size !== undefined) params.set('size', size)
  const res = await fetch(`/api${endpoint}?${params.toString()}`, { headers })
  if (!res.ok) return null
  return await res.json()
}

// ---- API 호출들 ----
async function fetchProfilePoint() {
  const headers = { Authorization: `Bearer ${token}` }
  const data = await fetchSomeoneData('/someone/point', headers, username, null, null)
  if (data?.point) {
    user.value.point = data.point.userPoint
    stats.value[2].value = data.point.likeCount ?? 0
  }
  user.value.value = username
}
async function fetchPostsData(page = 0) {
  if (!token) return
  const headers = { Authorization: `Bearer ${token}` }
  const size = pagingInfo.value.posts.size
  const data = await fetchSomeoneData('/someone', headers, username, page, size)
  if (!data) return
  feedList.value = data.content || []
  pagingInfo.value.posts.currentPage   = data.page
  pagingInfo.value.posts.totalPages    = data.totalPages
  pagingInfo.value.posts.totalElements = data.totalElements
  stats.value[0].value = data.totalElements || 0
}
async function fetchCommentsData(page = 0) {
  if (!token) return
  const headers = { Authorization: `Bearer ${token}` }
  const size = pagingInfo.value.comments.size
  const data = await fetchSomeoneData('/someone/comment/paging', headers, username, page, size)
  if (!data) return
  comments.value = data.comments || []
  pagingInfo.value.comments.currentPage   = page
  pagingInfo.value.comments.totalPages    = data.totalPage
  pagingInfo.value.comments.totalElements = data.totalCountComment
  stats.value[1].value = data.totalCountComment || 0
}
async function fetchCommentedPostsData(page = 0) {
  if (!token) return
  const headers = { Authorization: `Bearer ${token}` }
  const size = pagingInfo.value.commentedPosts.size
  const data = await fetchSomeoneData('/someone/post/comment/paging', headers, username, page, size)
  if (!data) return
  commentedPosts.value = data.all || []
  pagingInfo.value.commentedPosts.currentPage   = page
  pagingInfo.value.commentedPosts.totalPages    = data.totalPage
  pagingInfo.value.commentedPosts.totalElements = data.totalCountComment
}
async function fetchLikedPostsData(page = 0) {
  if (!token) return
  const headers = { Authorization: `Bearer ${token}` }
  const size = pagingInfo.value.likedPosts.size
  const data = await fetchSomeoneData('/someone/feed/like/paging', headers, username, page, size)
  if (!data) {
    likedPosts.value = []
    pagingInfo.value.likedPosts = { ...pagingInfo.value.likedPosts, currentPage: 0, totalPages: 1, totalElements: 0 }
    return
  }
  likedPosts.value = data.content ?? data.posts ?? []
  pagingInfo.value.likedPosts.currentPage   = data.page ?? page
  pagingInfo.value.likedPosts.totalPages    = data.totalPages ?? data.totalPage ?? 1
  pagingInfo.value.likedPosts.totalElements = data.totalElements ?? data.total ?? 0
}

// ---- 페이지 이동 ----
const goToPage = (page) => {
  const info = pagingInfo.value[activeTab.value]
  if (!info) return
  if (page < 0 || page >= info.totalPages) return
  switch (activeTab.value) {
    case 'posts':          return fetchPostsData(page)
    case 'comments':       return fetchCommentsData(page)
    case 'commentedPosts': return fetchCommentedPostsData(page)
    case 'likedPosts':     return fetchLikedPostsData(page)
  }
}

// ---- NAV/하단바 실제 높이 측정해서 CSS 변수에 반영 ----
function measureBars() {
  const nav = document.querySelector('.fixed-top, header.navbar, .navbar')
  const bottom = document.querySelector('.app-bottom-bar, .mobile-tabbar, .fab-bar')
  const navH = nav?.offsetHeight || 64
  const bottomH = bottom?.offsetHeight || 0   // 하단바 없으면 0
  document.documentElement.style.setProperty('--app-nav-h', `${navH}px`)
  document.documentElement.style.setProperty('--app-bottom-h', `${bottomH}px`)
}

async function init() {
  if (!token) {
    alert('로그인이 필요합니다.')
    router.push('/login')
    return
  }
  measureBars()
  await fetchProfilePoint()
  await fetchPostsData(0)
}

watch(activeTab, () => { goToPage(0) })
onMounted(() => {
  init()
  // 창 크기 바뀌면 다시 측정
  window.addEventListener('resize', measureBars)
})
</script>

<!-- 레이아웃/여백은 전역으로 적용되어야 하므로 "scoped 아님" -->
<style>
:root{
  /* 기본값(측정 전 폴백) */
  --app-nav-h: 64px;
  --app-bottom-h: 64px;
}

/* 고정 헤더/하단바를 피해서 패딩 확보 + 노치 안전영역 반영 */
.with-fixed-bars{
  padding-top: calc(var(--app-nav-h) + env(safe-area-inset-top));
  padding-bottom: calc(var(--app-bottom-h) + 16px + env(safe-area-inset-bottom));
}

/* 스크롤시 상단 탭이 헤더에 붙도록 */
html{
  scroll-padding-top: calc(var(--app-nav-h) + 12px);
  scroll-padding-bottom: calc(var(--app-bottom-h) + 12px);
}
</style>

<!-- 나머지 미관 스타일은 scoped로 유지 -->
<style scoped>
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
  color: #2563eb;
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

/* 통계 그리드 */
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

/* 탭: 헤더 높이만큼 띄워 sticky */
.tabs-wrap { position: sticky; top: calc(var(--app-nav-h) + 8px); z-index: 10; }
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

/* 리스트 둥글게 */
.tab-content :deep(.post-card),
.tab-content :deep(.comment-card),
.tab-content :deep(.bookmark-card) { border-radius: 14px; }

/* 페이징: 모바일에서 하단바 위에 떠 있게 */
.pager-sticky {
  background: rgba(255,255,255,0.94);
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 8px 10px;
}
@media (max-width: 768px) {
  .pager-sticky {
    position: sticky;
    bottom: calc(var(--app-bottom-h) + 8px);
    z-index: 9;
    -webkit-backdrop-filter: blur(6px);
    backdrop-filter: blur(6px);
  }
}

.btn-icon {
  width: 40px; height: 40px; border-radius: 10px;
  display: inline-grid; place-items: center;
  border: 1px solid rgba(0,0,0,.08);
  background: #fff;
}
.btn-icon i { color: #2563eb; }

</style>
