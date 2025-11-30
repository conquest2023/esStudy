<template>
  <section class="container-fluid px-4 mt-4">
    <div class="row gx-4">
      <aside class="col-lg-3 d-none d-lg-block">
<!--        <div class="card shadow-sm p-3">-->
<!--&lt;!&ndash;          <h6 class="fw-bold mb-3">🌟 추천 글</h6>&ndash;&gt;-->
<!--        </div>-->
        <div class="card shadow-sm p-3 mt-3">
          <DailyQuestions />
        </div>
      </aside>

      <main class="col-lg-6">
        <div class="d-block d-lg-none">
          <div class="mb-3">
            <DailyQuestions />
          </div>
<!--          <button class="btn btn-primary btn-sm mb-3" @click="showSidebar = true">-->
<!--            <i class="fas fa-list me-1"></i> 추천 글 보기-->
<!--          </button>-->
        </div>

        <transition name="slide">
          <div
              v-if="showSidebar"
              class="mobile-sidebar-backdrop"
              @click="showSidebar = false"
          >
            <div
                class="mobile-sidebar bg-white shadow position-fixed top-0 start-0 h-100 p-3"
                @click.stop
                style="z-index: 1060; width: 80%; max-width: 300px;"
            >
              <div class="d-flex justify-content-between align-items-center mb-3 border-bottom pb-2">
                <h5 class="fw-bold text-primary">추천 글</h5>
                <button class="btn-close" @click="showSidebar = false"></button>
              </div>
              <ul class="list-unstyled small">
                <li v-for="item in recommendPosts" :key="item.feedUID" class="mb-3 border-bottom pb-2">
                  <router-link
                      :to="`/search/view/feed/id/${item.feedUID}`"
                      class="text-dark text-decoration-none d-block"
                      @click="showSidebar = false"
                  >
                    <div class="fw-semibold text-truncate text-break">{{ item.title }}</div>
                    <small class="text-muted">{{ item.username }}</small>
                  </router-link>
                </li>
              </ul>
            </div>
          </div>
        </transition>

        <div class="board-wrap">
          <SearchBar class="mb-4" />
          <BoardTabs v-model="activeTab" :tabs="TABS" class="mb-3" />
          <div v-if="activeTab === 'DATA'" class="category-bar">
            <button
                v-for="cat in dataCategories"
                :key="cat"
                class="category-chip"
                :class="{ active: selectedCategory === cat }"
                @click="changeCategory(cat)"
            >
              {{ cat }}
            </button>
          </div>

          <div class="d-flex justify-content-between align-items-center mb-4 border-bottom pb-2">
            <h5 class="fw-bold text-dark"></h5>
            <div class="dropdown">
              <button class="btn btn-sm btn-outline-secondary dropdown-toggle d-flex align-items-center gap-1" data-bs-toggle="dropdown">
                <i class="fas fa-sort-amount-down-alt"></i> {{ sortLabel }}
              </button>
              <ul class="dropdown-menu dropdown-menu-end shadow-sm">
                <li v-for="s in sorts" :key="s.id">
                  <a href="#" class="dropdown-item" :class="{ 'active fw-bold': curSort === s.id }" @click.prevent="changeSort(s.id)">{{ s.label }}</a>
                </li>
              </ul>
            </div>
          </div>
          <div class="post-list-area">
            <router-link
                v-for="n in notices" :key="n.id" :to="`/notice/detail/${n.id}`"
                class="text-decoration-none text-dark d-block notice-card">
              <FeedCard :post="n" notice class="mb-3 bg-light-info border-start border-4 border-info" />
            </router-link>
            <FeedCard v-for="p in posts"
                      :key="p.id"
                      :post="p"
                      :is-vote="!p.id"
                      :like-count="likeCounts[p.id]"
                      :comment-count="counts[p.id]" class="mb-3 post-card"
                      :page="page"
                      :posts="posts" />
          </div>
          <div class="d-flex justify-content-center mt-5">
            <Pagination :page="page" :totalPages="totalPage" @change="fetchFeeds" />
            <Spinner v-if="loading" />
          </div>
        </div>
      </main>
      <div class="col-lg-3 d-none d-lg-block" />
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
const likeCounts = ref({})
const itQs = ref([])
const genQs = ref([])
const curCat = ref('IT')
const curIdx = ref(0)
const answerInput = ref('')
const isInterviewOpen = ref(false)
const curArr = computed(() => (curCat.value === 'IT' ? itQs.value : genQs.value))
const curQuestion = computed(() => curArr.value[curIdx.value] ?? null)
const { rankIcon } = useRankIcon()
function changeCat(cat) {
  curCat.value = cat
  curIdx.value = 0
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
onMounted(loadInterviewQs)

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

/* ▣ 베스트 답변 */
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
  document.getElementById('bestAnswerModalLabel').innerText = `💬 ${ans.title}`
  document.getElementById('bestAnswerModalBody').innerHTML = `<p><strong>작성자:</strong> ${ans.username || '익명'}</p><hr><p>${ans.answer}</p>`
  modal ??= new bootstrap.Modal('#bestAnswerModal')
  modal.show()
}
function nextBest() {
  bestIdx.value = (bestIdx.value + 1) % bestAnswers.value.length
  openBestModal()
}

/* ▣ 정렬 옵션 */
const sorts = [
  { id: 'COMMENT', label: '댓글순' },
  { id: 'REPLY',   label: '답글순' },
  { id: 'VIEW',    label: '조회순' },
]

const curSort = ref('ALL')
const sortLabel = computed(() => sorts.find(s => s.id === curSort.value)?.label ?? '정렬')
function changeSort(id) {
  if (curSort.value !== id) {
    curSort.value = id
    fetchFeeds(0)
  }
}

const TABS = [
  { id: 'ALL',    label: '전체 글',        url: '/posts' },
  { id: 'BEST',   label: '이번주 인기글',  url: '/posts/popular/week' },
  { id: 'VOTE',   label: '투표',          url: '/polls' },
  { id: 'DATA',   label: '학습 자료',      url: '/data/feed', requiresCategory: true },
  { id: 'NOTICE', label: '공지사항',       url: '/notices', category: '공지사항' },
  { id: 'QNA',    label: 'Q&A',           url: '/data/feed', category: 'Q/A' },
]
const dataCategories  = ['자료', '기술', '취업', '자격증']
const activeTab       = ref('ALL')
const selectedCategory= ref('자료')
const targetPath = computed(() =>
    props.notice
        ? `/notice/detail/${props.post.id}`
        : `/post/${props.post.id}`
)
const loading    = ref(false)
const page       = ref(0)
const totalPage  = ref(0)
const posts      = ref([])
const notices    = ref([])
const counts     = ref({})

function changeCategory(cat) {
  selectedCategory.value = cat
  fetchFeeds(0)
}
onMounted(async () => {
  try {
    const res = await api.get('/auto/login', { withCredentials: true })

    const accessToken = res.headers['authorization']?.replace('Bearer ', '')
    if (accessToken) {
      localStorage.setItem('token', accessToken)
    }
  } catch (err) {
    console.log('자동 로그인 실패')
  }

  const p = parseInt(route.query.page) || 0
})


async function fetchNotice() {
  try {
    const { data } = await api.get('/notice')
    notices.value = data ?? []
  } catch (err) {
    console.error('공지사항 로딩 실패:', err)
  }
}

async function fetchFeedsAll(newPage = page.value) {
  try {
    loading.value = true
    const uiPage = Number(newPage ?? 1)
    page.value = uiPage
    router.replace({ query: { ...route.query, page: uiPage } })
    const zeroBasedPage = Math.max(0, uiPage)
    const params = { page: zeroBasedPage, size: 10 }
    const { data } = await api.get('/posts', { params })
    const payload    = data?.ok ?? data ?? {}
    const content    = payload.content    ?? payload.data   ?? []
    const totalPages = payload.totalPages ?? payload.totalPage ?? 0
    posts.value     = Array.isArray(content) ? content : []
    totalPage.value = Number.isFinite(totalPages) ? totalPages : 0
    const { data: statsRes } = await api.get('/post/stats', { params })
    const postStats = statsRes.stats || []

    const nextCounts = {}
    const nextLikeCounts = {}
    fetchNotice()
    for (const stat of postStats) {
      if (stat.postId && stat.totalCommentCount !== undefined) {
        nextCounts[stat.postId] = stat.totalCommentCount
      }


      if (stat.postId && stat.likeCount !== undefined) {
        nextLikeCounts[stat.postId] = stat.likeCount
      }
    }

    counts.value = nextCounts
    likeCounts.value = nextLikeCounts
  } catch (err) {
    console.error('전체 글 로딩 실패:', err)
    posts.value = []
    counts.value = {}
    likeCounts.value = {}
    totalPage.value = 0
  } finally {
    loading.value = false
  }
}

async function fetchFeeds(newPage = page.value) {
  const tab = TABS.find(t => t.id === activeTab.value)
  posts.value = []
  notices.value = []
  counts.value = {}
  likeCounts.value = {}
  if (!tab)
    return

  if (tab.id === 'ALL') {
    return fetchFeedsAll(newPage)
  }
  loading.value = true
  const zeroBasedPage = Number(newPage) || 0
  page.value = zeroBasedPage
  const uiPageForUrl = zeroBasedPage + 1;
  router.replace({ query: { ...route.query, page: uiPageForUrl } })

  const params = { page: zeroBasedPage, size: 10 } // P
  if (tab.category) params.category = tab.category
  if (tab.id === 'DATA') {
    params.category = selectedCategory.value
  }

  if (tab.id !== 'NOTICE' && curSort.value !== 'ALL') {
    params.sort = curSort.value
  }
  try {
    const { data } = await api.get(tab.url, { params })
    const payload    = data?.ok ?? data ?? {}
    const content = payload.posts ?? payload.content ?? payload.data ?? []
    const totalPages = payload.totalPages ?? payload.totalPage ?? 0
    if (tab.id === 'BEST') {
      const { data: statsRes } = await api.get('/post/popular/stats', { params })
      const postStats = statsRes.stats || []
      const nextCounts = {}
      const nextLikeCounts = {}
      for (const stat of postStats) {
        if (stat.postId && stat.totalCommentCount !== undefined) {
          nextCounts[stat.postId] = stat.totalCommentCount
        }
        if (stat.postId && stat.likeCount !== undefined) {
          nextLikeCounts[stat.postId] = stat.likeCount
        }
      }
      counts.value = nextCounts
      likeCounts.value = nextLikeCounts

    }

    if (tab.id === 'VOTE') {
      const { data: statsRes } = await api.get('/poll/stats', { params })
      const postStats = statsRes.stats || []
      const nextCounts = {}
      const nextLikeCounts = {}
      for (const stat of postStats) {
        if (stat.postId && stat.totalCommentCount !== undefined) {
          nextCounts[stat.postId] = stat.totalCommentCount
        }
        if (stat.postId && stat.likeCount !== undefined) {
          nextLikeCounts[stat.postId] = stat.likeCount
        }
      }
      counts.value = nextCounts
      likeCounts.value = nextLikeCounts

    }
    if (tab.id === 'NOTICE') {
      notices.value = Array.isArray(content) ? content : []
    } else {
      posts.value = Array.isArray(content) ? content : []

      if (payload.count) {
        counts.value = payload.count
      }
    }
    totalPage.value = Number.isFinite(totalPages) ? totalPages : 0

  } catch (err) {
    console.error(`${tab.label} 로딩 실패`, err)
    posts.value = []
    notices.value = []
    counts.value = {}
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
  const p = parseInt(route.query.page) || 0
  fetchFeeds(p)
})

watch(activeTab, () =>
    fetchFeeds(0)
)
</script>

<style scoped>
.container-fluid {
  max-width: 1300px;
}


.post-list-area {
  min-height: 500px;
}

.post-card {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  transition: all 0.2s ease-in-out;
  padding: 1rem;
}
.post-card:hover {
  border-color: #0d6efd;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05);
  transform: translateY(-2px);
}

.notice-card .feed-card-body {
  background-color: #f7f7ff;
  border-left: 4px solid #0d6efd;
}

.mobile-sidebar-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 1050;
  transition: opacity 0.3s ease-in-out;
}

.mobile-sidebar {
  box-shadow: 4px 0 10px rgba(0,0,0,0.15) !important; /* 사이드바 그림자 강조 */
  z-index: 1060 !important;
}


.nav-pills .nav-item {
  margin-right: 0.5rem;
  margin-bottom: 0.5rem;
}
.nav-pills .nav-link {
  border-radius: 20px;
  font-size: 0.85rem;
}
.nav-pills .nav-link.active {
  font-weight: 600;
}


.search-bar input {
  border: none;
  background: transparent;
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
