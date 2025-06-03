<template>
  <section class="container-fluid px-4 mt-4">
    <div class="row gx-4">
      <!-- ─── ① 왼쪽 추천 글 (3) ─── -->
      <aside class="col-lg-3 d-none d-lg-block">
        <div class="card shadow-sm p-3">
          <h6 class="fw-bold mb-3">🌟 추천 글</h6>
          <ul class="list-unstyled mb-0 small">
            <li v-for="item in recommendPosts" :key="item.feedUID" class="mb-2">
              <router-link :to="`/search/view/feed/id/${item.feedUID}`" class="text-dark text-decoration-none d-block">
                <div class="fw-semibold text-truncate">{{ item.title }}</div>
                <small class="text-muted">{{ item.username }}</small>
              </router-link>
            </li>
          </ul>
        </div>
        <div class="card shadow-sm p-3 mt-3">
          <DailyQuestions />
        </div>
      </aside>

      <main class="col-lg-6">

        <div class="d-block d-lg-none">
          <div class="d-lg-none mb-3">
            <DailyQuestions />
          </div>
          <button class="btn btn-link text-primary px-2" @click="showSidebar = true">
            <i class="fas fa-bars"></i> 추천 글
          </button>
        </div>

        <transition name="slide">
          <div
              v-if="showSidebar"
              class="mobile-sidebar bg-white shadow position-fixed top-0 start-0 h-100 p-3"
              style="z-index: 1050; width: 80%; max-width: 300px;"
          >
            <div class="d-flex justify-content-between align-items-center mb-2">
              <h6 class="fw-bold">🌟 추천 글</h6>
              <button class="btn-close" @click="showSidebar = false"></button>
            </div>
            <ul class="list-unstyled small">
              <li v-for="item in recommendPosts" :key="item.feedUID" class="mb-2">
                <router-link
                    :to="`/search/view/feed/id/${item.feedUID}`"
                    class="text-dark text-decoration-none d-block"
                    @click="showSidebar = false"
                >
                  <div class="fw-semibold text-truncate">{{ item.title }}</div>
                  <small class="text-muted">{{ item.username }}</small>
                </router-link>
              </li>
            </ul>
          </div>
        </transition>
        <div class="board-wrap">
          <!-- ▣ 검색바 -->
          <SearchBar class="mb-3" />

          <!-- ▣ 면접 질문 섹션 -->
          <section class="my-4">
            <h2 class="interview-title border-bottom py-3 d-flex justify-content-between align-items-center">
              <div class="d-flex align-items-center gap-2">
                <i class="fas fa-comments fa-lg text-primary"></i>
                <span class="fs-4 fw-bold">면접 질문</span>
              </div>
              <button class="btn btn-sm btn-outline-secondary" @click="isInterviewOpen = !isInterviewOpen">
                {{ isInterviewOpen ? '닫기 ▲' : '열기 ▼' }}
              </button>
            </h2>

            <transition name="fade">
              <div v-show="isInterviewOpen" class="p-3 bg-light rounded-3 shadow-sm mt-3">
                <!-- 카테고리 버튼 -->
                <div class="d-flex justify-content-between align-items-center mb-3">
                  <div class="btn-group rounded-pill shadow-sm">
                    <button v-for="cat in ['IT', '일반']" :key="cat" class="btn btn-outline-primary" :class="{ active: curCat === cat }" @click="changeCat(cat)">
                      <i :class="cat === 'IT' ? 'fas fa-laptop-code' : 'fas fa-building'" />
                      {{ cat }} 기업
                    </button>
                  </div>
                  <button class="btn btn-outline-dark btn-sm d-flex align-items-center gap-1" @click="showBestAnswers">
                    <i class="fas fa-trophy text-warning" /> 베스트 답변
                  </button>
                </div>

                <!-- 질문 카드 -->
                <div v-if="curQuestion" class="card border-0 shadow-sm">
                  <div class="card-header bg-white border-bottom"><strong>{{ curQuestion.question }}</strong></div>
                  <div class="card-body">
                    <textarea v-model="answerInput" class="form-control" rows="4" placeholder="최소 35자 이상 입력해주세요" maxlength="1000" />
                    <div class="text-end text-muted small mt-1">{{ answerInput.length }} / 1000자</div>
                    <button class="btn btn-primary w-100 mt-3" @click="submitAnswer">
                      <i class="fas fa-paper-plane me-1" /> 제출하기
                    </button>
                  </div>
                </div>

                <div class="d-flex justify-content-between mt-3">
                  <button class="btn btn-outline-secondary" @click="prevQ"><i class="fas fa-arrow-left me-1" /> 이전</button>
                  <button class="btn btn-outline-secondary" @click="nextQ">다음 <i class="fas fa-arrow-right ms-1" /></button>
                </div>
              </div>
            </transition>
          </section>

          <!-- ▣ 정렬 드롭다운 (오른쪽 정렬) -->
          <div class="d-flex justify-content-end mb-3">
            <div class="dropdown">
              <button class="btn btn-outline-secondary dropdown-toggle d-flex align-items-center gap-1" data-bs-toggle="dropdown">
                <i class="fas fa-bars"></i> {{ sortLabel }}
              </button>
              <ul class="dropdown-menu dropdown-menu-end">
                <li v-for="s in sorts" :key="s.id">
                  <a href="#" class="dropdown-item" :class="{ active: curSort === s.id }" @click.prevent="changeSort(s.id)">{{ s.label }}</a>
                </li>
              </ul>
            </div>
          </div>

          <!-- ▣ 게시판 탭 -->
          <BoardTabs v-model="activeTab" :tabs="TABS" />

          <!-- ▣ 학습 자료 카테고리 -->
          <div v-if="activeTab === 'DATA'" class="mb-2">
            <ul class="nav nav-pills small">
              <li v-for="cat in dataCategories" :key="cat" class="nav-item">
                <button class="nav-link" :class="{ active: selectedCategory === cat }" @click="changeCategory(cat)">{{ cat }}</button>
              </li>
            </ul>
          </div>

          <!-- ▣ 공지 / 피드 카드 -->
          <FeedCard v-for="n in notices" :key="n.feedUID" :post="n" notice class="mb-2" />
          <FeedCard v-for="p in posts" :key="p.feedUID" :post="p" :is-vote="!p.id" :comment-count="counts[p.feedUID]" class="mb-2"  :posts="posts" />

          <!-- ▣ 페이지네이션 & 로딩 -->
          <Pagination :page="page" :totalPages="totalPage" @change="fetchFeeds" />
          <Spinner v-if="loading" />
        </div>
      </main>

      <!-- ─── ③ 오른쪽 여백 (3) ─── -->
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

/* ───── 상태 ───── */
const route = useRoute()
const router = useRouter()

/* ▣ 추천 글 */
const recommendPosts = ref([])
const showSidebar = ref(false)

onMounted(async () => {
  // showSidebar.value = window.innerWidth >= 992
  try {
    const { data } = await api.get('/search/view/feed/recommend')
    recommendPosts.value = data.recommend ?? []
  } catch (err) {
    console.error('추천 글 로딩 실패:', err)
  }
})

/* ▣ 면접 질문 */
const itQs = ref([])
const genQs = ref([])
const curCat = ref('IT')
const curIdx = ref(0)
const answerInput = ref('')
const isInterviewOpen = ref(false)
const curArr = computed(() => (curCat.value === 'IT' ? itQs.value : genQs.value))
const curQuestion = computed(() => curArr.value[curIdx.value] ?? null)

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

/************************************************
 * 3. 탭 / 카테고리 & 상태
 ************************************************/
const TABS = [
  { id: 'ALL',    label: '전체 글',        url: '/feeds' },
  { id: 'BEST',   label: '이번주 인기글',  url: '/search/view/feed/best' },
  { id: 'VOTE',   label: '투표',          url: '/search/view/vote/page' },
  { id: 'DATA',   label: '학습 자료',      url: '/data/feed', requiresCategory: true },
  { id: 'NOTICE', label: '공지사항',       url: '/notice/feed' },
  { id: 'QNA',    label: 'Q&A',           url: '/data/feed', category: 'Q/A' },
]
const dataCategories  = ['자료', '기술', '취업', '자격증']
const activeTab       = ref('ALL')
const selectedCategory= ref('자료')

/************************************************
 * 4. 피드 / 페이지네이션 상태
 ************************************************/
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

async function fetchNotice() {
  try {
    const { data } = await api.get('/list/notice')
    notices.value = data ?? []
  } catch (err) {
    console.error('공지사항 로딩 실패:', err)
  }
}

/************************************************
 * 5. 메인 피드 로딩 (✔ 핵심 수정 – URL 분기만 단순하게)
 ************************************************/
async function fetchFeeds(newPage = page.value) {
  const tab = TABS.find(t => t.id === activeTab.value)
  if (!tab) return

  loading.value = true
  page.value = newPage
  router.replace({ query: { ...route.query, page: newPage } })

  const params = { page: newPage, size: 10 }
  if (tab.category) params.category = tab.category
  if (tab.id === 'DATA') params.category = selectedCategory.value

  // ▣ URL 결정 로직 : ALL 탭 + 정렬값에 따라 별도 API로 교체
  let url = tab.url
  if (tab.id === 'ALL') {
    if (curSort.value === 'COMMENT')      url = '/comment/count'
    else if (curSort.value === 'REPLY')   url = '/reply/count'
    else if (curSort.value === 'VIEW')    url = '/view/count'
    else url = '/feeds'
  }

  try {
    const { data } = await api.get(url, { params })

    // 반환 형식이 리스트든 기존 구조든 동일하게 posts 로만 세팅 (최소 변경)
    posts.value      = data.data ?? data ?? []
    counts.value     = data.count ?? {}
    totalPage.value  = data.totalPage ?? Math.ceil(posts.value.length / 10)

    if (activeTab.value === 'ALL') await fetchNotice()
    else notices.value = []
  } catch (err) {
    console.error(`${tab.label} 로딩 실패`, err)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  const p = parseInt(route.query.page) || 0
  fetchFeeds(p)
  // fetchNotice()
})

watch(activeTab, () =>
    fetchFeeds(0)
)
</script>

<style scoped>
.board-wrap { max-width: 100%; margin: 0; }
.interview-title { font-size: 1.5rem; font-weight: 700; letter-spacing: -0.02rem; display: flex; align-items: center; gap: 0.5rem; flex-wrap: wrap; cursor: pointer; }
.search-bar { background: #f8f9fa; border: 1px solid #dee2e6; border-radius: 8px; }
.search-bar input { font-size: 0.95rem; }
.btn-group .btn.active { background: #0d6efd; color: #fff; border-color: #0d6efd; }
textarea.form-control:focus { border-color: #86b7fe; box-shadow: 0 0 0 0.2rem rgba(13, 110, 253, 0.25); }
@media (max-width: 576px) {
  .interview-title { font-size: 1.2rem; flex-direction: column; align-items: flex-start; } }
.mobile-sidebar { position: fixed; top: 0; left: 0; z-index: 1050; height: 100%; width: 80%; max-width: 300px; background: #fff; box-shadow: 0 0 10px rgba(0,0,0,0.1); padding: 1rem; }
@media (max-width: 992px) {
  .mobile-sidebar { transition: transform 0.3s ease-in-out; }
  .slide-enter-active, .slide-leave-active { transition: transform 0.3s ease-in-out; }
  .slide-enter-from, .slide-leave-to { transform: translateX(-100%); }
  .slide-enter-to, .slide-leave-from { transform: translateX(0); }
}
/* ▣ 정렬 바 */
.sort-bar .btn.active { background: #0d6efd; color: #fff; }
.sort-bar .btn { border-radius: 20px; font-size: 0.875rem; }
</style>
