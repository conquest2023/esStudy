<template>
  <section class="container-fluid px-4 mt-4 modern-board">
    <div class="row gx-4 justify-content-center">
      <main class="col-lg-7"> <div class="board-wrap">
        <SearchBar class="mb-4 modern-search" />
        <RecommendedPosts :items="recommend" class="mb-4" />

        <BoardTabs v-model="activeTab" :tabs="TABS" class="mb-4" />

        <div v-if="activeTab === 'BEST'" class="category-bar mb-4">
          <div class="segmented-control">
            <button
                v-for="cat in bestCategories"
                :key="cat"
                class="seg-btn"
                :class="{ active: bestSelected === cat }"
                @click="setBestCat(cat)"
            >
              {{ cat }}
            </button>
          </div>
        </div>

        <div v-if="activeTab === 'DATA'" class="category-bar mb-4">
          <div class="segmented-control">
            <button
                v-for="cat in dataCategories"
                :key="cat"
                class="seg-btn"
                :class="{ active: selectedCategory === cat }"
                @click="changeDataCategory(cat)"
            >
              {{ cat }}
            </button>
          </div>
        </div>

        <div class="d-flex justify-content-end mb-3">
          <div class="dropdown modern-dropdown">
            <button class="btn dropdown-toggle d-flex align-items-center gap-2" data-bs-toggle="dropdown">
              <i class="fas fa-sliders-h text-muted"></i>
              <span class="fw-medium">{{ sortLabel }}</span>
            </button>
            <ul class="dropdown-menu dropdown-menu-end shadow-sm border-0 rounded-3">
              <li v-for="s in sorts" :key="s.id">
                <a href="#" class="dropdown-item py-2" :class="{ 'active': curSort === s.id }" @click.prevent="changeSort(s.id)">
                  {{ s.label }}
                </a>
              </li>
            </ul>
          </div>
        </div>

        <div class="post-list-area">
          <FeedCard
              v-for="n in notices" :key="n.id"
              :post="n" notice
          />
          <FeedCard
              v-for="p in posts" :key="p.id"
              :post="p"
              :is-vote="!p.id"
              :like-count="likeCounts[p.id]"
              :comment-count="counts[p.id]"
              :page="page"
              :posts="posts"
          />
        </div>

        <div class="d-flex justify-content-center mt-5 mb-5">
          <Spinner v-if="loading" />
          <Pagination v-else :page="page" :totalPages="totalPage" @change="fetchFeeds" />
        </div>
      </div>
      </main>
    </div>
  </section>
</template>
<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import api from '@/utils/api'
import * as bootstrap from 'bootstrap'
import DailyQuestions from '@/components/DailyQuestions.vue'
import SearchBar from '@/components/SearchBar.vue'
import Pagination from '@/common/Pagination.vue'
import BoardTabs from '@/components/BoardTabs.vue'
import FeedCard from '@/pages/feed/FeedCard.vue'
import Spinner from '@/components/Spinner.vue'
import {useRankIcon} from "@/composables/useRankIcon.js";
import RecommendedPosts from "@/pages/feed/RecommendedPosts.vue";

const route = useRoute()
const router = useRouter()
const recommendPosts = ref([])
const showSidebar = ref(false)
// onMounted(async () => {
//   // showSidebar.value = window.innerWidth >= 992
//   try {
//     const { data } = await api.get('/search/view/feed/recommend')
//     recommendPosts.value = data.recommend ?? []
//   } catch (err) {
//     console.error('추천 글 로딩 실패:', err)
//   }
// })
const itQs = ref([])
const genQs = ref([])
const curCat = ref('IT')
const curIdx = ref(0)
const loading    = ref(false)
const page       = ref(0)
const totalPage  = ref(0)
const posts      = ref([])
const notices    = ref([])
const counts     = ref({})
const likeCounts = ref({})
const answerInput = ref('')
const isInterviewOpen = ref(false)
const curArr = computed(() => (curCat.value === 'IT' ? itQs.value : genQs.value))
const curQuestion = computed(() => curArr.value[curIdx.value] ?? null)
const { rankIcon } = useRankIcon()
const recommend = ref([])
const bestCategories  = ['오늘', '이번주', '이번달']
const dataCategories  = ['자료', '기술', '취업', '자격증']
const activeTab       = ref('ALL')
const bestSelected     = ref('오늘')
const selectedCategory= ref('자료')
const dayMap = {
  '오늘': 'today',
  '이번주': 'week',
  '이번달': 'month'
}
const SORT_ENDPOINTS = {
  COMMENT: '/post/comment',
  REPLY:   '/post/reply',
  LIKE:    '/post/like',
  VIEW:    '/post/view',
}
const sorts = [
  { id: 'COMMENT', label: '댓글순' },
  { id: 'REPLY',   label: '답글순' },
  { id: 'LIKE',    label: '좋아요순' },
  { id: 'VIEW',    label: '조회순' },
]
const TABS = [
  { id: 'ALL',    label: '전체 글',        url: '/posts' },
  { id: 'BEST',   label: '인기글 모음',  url: '/posts/popular/week' },
  { id: 'VOTE',   label: '투표',          url: '/polls' },
  { id: 'DATA',   label: '학습 자료',      url: '/data/feed', requiresCategory: true },
  { id: 'NOTICE', label: '공지사항',       url: '/notices', category: '공지사항' },
  { id: 'QNA',    label: 'Q&A',           url: '/data/feed', category: 'Q/A' },
]
function changeDataCategory(cat) {
  selectedCategory.value = cat
  fetchFeeds(0)
}
function bestListUrl() {
  switch (bestSelected.value) {
    case '오늘':   return '/posts/popular/today'
    case '이번주': return '/posts/popular/week'
    case '이번달': return '/posts/popular/month'
    default:       return '/posts/popular/today'
  }
}
function setBestCat(cat) {
  if (bestSelected.value !== cat) {
    bestSelected.value = cat
    fetchFeeds(0)
  }
}

function prevQ() {
  curIdx.value = (curIdx.value - 1 + curArr.value.length) % curArr.value.length
}
function nextQ() {
  curIdx.value = (curIdx.value + 1) % curArr.value.length
}

async function loadInterviewQs() {
  try {
    const { data } = await api.get('/interview/test')
    itQs.value = data.filter(q => q.category === 'IT')
    genQs.value = data.filter(q => q.category === '일반')
    curIdx.value = 0
  } catch (e) {
    console.error('면접 질문 로드 실패', e)
  }
}
// onMounted(loadInterviewQs)

async function submitAnswer() {
  const txt = answerInput.value.trim()
  if (!txt) return alert('답변을 입력하세요')
  if (txt.length < 35) return alert('답변은 최소 35자 이상입니다')
  const token = localStorage.getItem('token')
  if (!token) return alert('로그인이 필요합니다')
  try {
    await api.post('/save/interview/question', {
      questionId: curQuestion.value.id,
      answer: txt,
      title: curQuestion.value.question,
      category: curQuestion.value.category,
    }, { headers: { Authorization: `Bearer ${token}` } })
    alert('답변 저장 완료!')
    answerInput.value = ''
  } catch {
    alert('저장 실패')
  }
}


const bestAnswers = ref([])
const bestIdx = ref(0)
let modal = null
async function showBestAnswers() {
  try {
    const ids = ['263', '87', '93'].join(',')
    const { data } = await api.get('/interview/best/answer', { headers: { 'X-Question-Ids': ids } })
    if (!data.length) return alert('베스트 답변이 없습니다')
    bestAnswers.value = data
    bestIdx.value = 0
    openBestModal()
  } catch {
    alert('데이터 오류')
  }
}

function openBestModal() {
  const ans = bestAnswers.value[bestIdx.value]
  document.getElementById('bestAnswerModalLabel').innerText =
      `💬 ${ans.title}`
  document.getElementById('bestAnswerModalBody').innerHTML =
      `<p><strong>작성자:</strong> ${ans.username || '익명'}</p><hr><p>${ans.answer}</p>`
  modal ??= new bootstrap.Modal('#bestAnswerModal')
  modal.show()
}
function nextBest() {
  bestIdx.value = (bestIdx.value + 1) % bestAnswers.value.length
  openBestModal()
}
const curSort = ref('ALL')
const sortLabel = computed(() => sorts.find(s => s.id === curSort.value)?.label ?? '정렬')
function changeSort(id) {
  if (curSort.value !== id) {
    curSort.value = id
    fetchFeeds(0)
  }
}

const targetPath = computed(() =>
    props.notice
        ? `/notice/detail/${props.post.id}`
        : `/post/${props.post.id}`
)

function changeCategory(cat) {
  selectedCategory.value = cat
  fetchFeeds(0)
}
function extractList(payload) {
  let list = payload?.content ?? payload?.data ?? []
  if (list && !Array.isArray(list) && Array.isArray(list.content)) {
    list = list.content       // Page<T> 대응
  }
  return Array.isArray(list) ? list : []
}

async function fetchStatsForCurrentList(list) {
  const ids = list.map(p => p.id).filter(Boolean)
  if (!ids.length) {
    counts.value = {}
    likeCounts.value = {}
    return
  }
  try {
    const { data } = await api.post('/post/stats/by-ids', ids)
    const stats = data?.stats || []
    const nextCounts = {}
    const nextLikeCounts = {}
    for (const s of stats) {
      if (s.postId != null && s.totalCommentCount !== undefined) {
        nextCounts[s.postId] = (s.totalCommentCount ?? 0) + (s.replyCount ?? 0)
        nextLikeCounts[s.postId] = s.likeCount ?? 0
      }
    }
    counts.value = nextCounts
    likeCounts.value = nextLikeCounts
  } catch (e) {
    console.error('[by-ids stats] 실패', e)
    counts.value = {}
    likeCounts.value = {}
  }
}


async function fetchFeedsAll(newPage = page.value) {
  try {
    loading.value = true

    const uiPage = Number(newPage ?? 0)
    page.value = uiPage
    router.replace({ query: { ...route.query, page: uiPage } }).catch(() => {})

    const zeroBasedPage = Math.max(0, uiPage)
    const params = { page: zeroBasedPage, size: 10 }

    const isSorted = curSort.value !== 'ALL'
    const listUrl = isSorted ? (SORT_ENDPOINTS[curSort.value] || '/posts') : '/posts'

    const [postsRes, noticeRes] = await Promise.all([
      api.get(listUrl, { params }),
      api.get('/notice'),
    ])
    const payload    = postsRes.data?.ok ?? postsRes.data ?? {}
    const list       = extractList(payload)
    const totalPages = payload.totalPages ?? payload.totalPage ?? 0

    posts.value     = list
    totalPage.value = Number.isFinite(totalPages) ? totalPages : 0
    notices.value   = Array.isArray(noticeRes.data) ? noticeRes.data : (noticeRes.data ?? [])

    if (!isSorted) {
      await fetchPostStats(params)
    } else {
      await fetchStatsForCurrentList(posts.value)
    }
  } catch (err) {
    console.error('전체 글 로딩 실패:', err)
    posts.value = []
    counts.value = {}
    likeCounts.value = {}
    notices.value = []
    totalPage.value = 0
  } finally {
    loading.value = false
  }
}

async function fetchPostStats(params) {
  try {
    const { data } = await api.get('/post/stats', { params })
    const postStats = data?.stats || []
    const nextCounts = {}
    const nextLikeCounts = {}
    for (const stat of postStats) {
      if (stat.postId && stat.totalCommentCount !== undefined)
        nextCounts[stat.postId] = stat.totalCommentCount
      if (stat.postId && stat.likeCount !== undefined)
        nextLikeCounts[stat.postId] = stat.likeCount
    }
    counts.value = nextCounts
    likeCounts.value = nextLikeCounts
  } catch (err) {
    console.error('[stats] 로딩 실패:', err)
    counts.value = {}
    likeCounts.value = {}
  }
}

async function fetchFeeds(newPage = page.value) {
  const tab = TABS.find(t => t.id === activeTab.value)
  posts.value = []
  notices.value = []
  counts.value = {}
  likeCounts.value = {}
  if (!tab) return

  if (tab.id === 'ALL') {
    return fetchFeedsAll(newPage)
  }

  loading.value = true
  try {
    const zeroBasedPage = Number(newPage) || 0
    page.value = zeroBasedPage
    const uiPageForUrl = zeroBasedPage + 1
    router.replace({ query: { ...route.query, page: uiPageForUrl } })

    const params = { page: zeroBasedPage, size: 10 }

    let listUrl = tab.url
    if (tab.id === 'BEST') {
      listUrl = bestListUrl()
    } else if (tab.id === 'DATA') {
      listUrl = `/post/category/${encodeURIComponent(selectedCategory.value)}`
    } else if (tab.category) {
      listUrl = `/post/category/${encodeURIComponent(tab.category)}`
    }

    const { data } = await api.get(listUrl, { params })
    const payload     = data?.ok ?? data ?? {}
    const content     = payload.posts ?? payload.content ?? payload.data ?? []
    const totalPages  = payload.totalPages ?? payload.totalPage ?? 0

    // stats: BEST/VOTE는 전용 통계 API, 그 외에는 공통 /post/stats
    if (tab.id === 'BEST') {
      const raw = bestSelected.value || '오늘'
      const day = dayMap[raw] || 'today'
      const { data: statsRes } = await api.get(`/post/popular/stats/${day}`, { params })
      const postStats = statsRes.stats || []
      const nextCounts = {}
      const nextLikeCounts = {}
      for (const stat of postStats) {
        if (stat.postId && stat.totalCommentCount !== undefined) nextCounts[stat.postId] = stat.totalCommentCount
        if (stat.postId && stat.likeCount !== undefined)          nextLikeCounts[stat.postId] = stat.likeCount
      }
      counts.value = nextCounts
      likeCounts.value = nextLikeCounts
    } else if (tab.id === 'VOTE') {
      const { data: statsRes } = await api.get('/poll/stats', { params })
      const postStats = statsRes.stats || []
      const nextCounts = {}
      const nextLikeCounts = {}
      for (const stat of postStats) {
        if (stat.postId && stat.totalCommentCount !== undefined) nextCounts[stat.postId] = stat.totalCommentCount
        if (stat.postId && stat.likeCount !== undefined)          nextLikeCounts[stat.postId] = stat.likeCount
      }
      counts.value = nextCounts
      likeCounts.value = nextLikeCounts
    } else {
      // 그 외 탭도 항상 /post/stats 호출해서 댓글/좋아요 숫자 채움
      await fetchPostStats(params)
    }

    if (tab.id === 'NOTICE') {
      notices.value = Array.isArray(content) ? content : []
    } else {
      posts.value = Array.isArray(content) ? content : []
      if (payload.count) counts.value = payload.count
    }

    totalPage.value = Number.isFinite(totalPages) ? totalPages : 0
  } catch (err) {
    console.error(`${tab?.label ?? ''} 로딩 실패`, err)
    posts.value = []
    notices.value = []
    counts.value = {}
    likeCounts.value = {}
    totalPage.value = 0
  } finally {
    loading.value = false
  }
}


function goToNextPage() {
  const nextPage = page.value + 1;
  fetchFeeds(nextPage);
}
onMounted(() => {
  // pingEmpty()
  // pingNormal()
  const p = parseInt(route.query.page) || 0
  fetchFeeds(p)

})
onMounted(async () => {
  try {
    const { data } = await api.get('/post/recommend')
    // 서버 응답: { post: [...] }
    recommend.value = Array.isArray(data?.post) ? data.post : []
  } catch (e) {
    console.error('추천 글 로딩 실패:', e)
    recommend.value = []
  }
})
watch(activeTab, (id) => {
  // 전체 글 탭으로 오면 정렬을 'ALL'로 리셋
  if (id === 'ALL' && curSort.value !== 'ALL') {
    curSort.value = 'ALL'
  }
  // 페이지도 0으로 초기화해서 깔끔하게
  fetchFeeds(0)
})
async function pingEmpty() {
  const t = performance.now()
  await fetch('/api/ping-empty')
  console.log('[PING-EMPTY]', (performance.now() - t).toFixed(2), 'ms')
}

async function pingNormal() {
  const t = performance.now()
  await fetch('/api/ping')
  console.log('[PING]', (performance.now() - t).toFixed(2), 'ms')
}
// onMounted(async () => {
//   try {
//     const res = await api.get('/auto/login', { withCredentials: true })
//
//     const accessToken = res.headers['authorization']?.replace('Bearer ', '')
//     if (accessToken) {
//       localStorage.setItem('token', accessToken)
//     }
//   } catch (err) {
//     console.log('자동 로그인 실패')
//   }
//
//   const p = parseInt(route.query.page) || 0
// })
</script>

<style scoped>
/* 메인 컨테이너 레이아웃 설정 */
.modern-board {
  max-width: 1100px; /* 너무 넓게 퍼지지 않도록 응집도 상승 */
}

/* iOS/macOS 스타일 Segmented Control */
.segmented-control {
  display: inline-flex;
  align-items: center;
  background: #f1f5f9; /* 연한 회색 배경 */
  border-radius: 12px;
  padding: 4px;
}

.seg-btn {
  appearance: none;
  border: 0;
  background: transparent;
  padding: 8px 18px;
  font-size: 0.9rem;
  font-weight: 600;
  color: #64748b;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.seg-btn:hover {
  color: #0f172a;
}

.seg-btn.active {
  background: #ffffff;
  color: #0f172a;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1), 0 1px 2px rgba(0,0,0,0.06); /* 입체감 */
}

/* 모던 정렬 드롭다운 */
.modern-dropdown .btn {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 20px;
  padding: 6px 16px;
  font-size: 0.875rem;
  color: #334155;
  transition: all 0.2s;
}

.modern-dropdown .btn:hover {
  background: #f8fafc;
  border-color: #cbd5e1;
}

.modern-dropdown .dropdown-menu {
  border-radius: 12px;
  padding: 0.5rem;
}

.modern-dropdown .dropdown-item {
  border-radius: 8px;
  color: #475569;
  font-size: 0.9rem;
}

.modern-dropdown .dropdown-item.active {
  background: #eff6ff;
  color: #2563eb;
  font-weight: 600;
}


@media (max-width: 992px) {
  .post-card {
    padding: 0.75rem;
  }
}
.board-wrap { max-width: 100%; margin: 0; }
.interview-title { font-size: 1.5rem; font-weight: 700; letter-spacing: -0.02rem; display: flex; align-items: center; gap: 0.5rem; flex-wrap: wrap; cursor: pointer; }
.search-bar input { font-size: 0.95rem; }
.btn-group .btn.active { background: #0d6efd; color: #fff; border-color: #0d6efd; }
textarea.form-control:focus { border-color: #86b7fe; box-shadow: 0 0 0 0.2rem rgba(13, 110, 253, 0.25); }
@media (max-width: 576px) {
  .interview-title {
    font-size: 1.2rem; flex-direction: column; align-items: flex-start;
  }
}
.mobile-sidebar {
  position: fixed; top: 0; left: 0; z-index: 1050; height: 100%; width: 80%; max-width: 300px; background: #fff;
  box-shadow: 0 0 10px rgba(0,0,0,0.1); padding: 1rem;
}
@media (max-width: 992px) {
  .mobile-sidebar { transition: transform 0.3s ease-in-out; }
}
/* ▣ 정렬 바 */
.sort-bar .btn.active { background: #0d6efd; color: #fff; }
.sort-bar .btn { border-radius: 20px; font-size: 0.875rem; }
</style>
