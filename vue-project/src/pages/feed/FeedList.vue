<!-- src/pages/feed/FeedList.vue -->
<template>
  <section class="board-wrap">
    <SearchBar class="mb-3" />

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
          <div class="d-flex justify-content-between align-items-center mb-3">
            <div class="btn-group rounded-pill shadow-sm">
              <button v-for="cat in ['IT','일반']"
                      :key="cat"
                      class="btn btn-outline-primary"
                      :class="{ active: curCat === cat }"
                      @click="changeCat(cat)">
                <i :class="cat==='IT' ? 'fas fa-laptop-code' : 'fas fa-building'"></i>
                {{ cat }} 기업
              </button>
            </div>

            <button class="btn btn-outline-dark btn-sm d-flex align-items-center gap-1"
                    @click="showBestAnswers">
              <i class="fas fa-trophy text-warning"></i> 베스트 답변
            </button>
          </div>

          <div v-if="curQuestion" class="card border-0 shadow-sm">
            <div class="card-header bg-white border-bottom">
              <strong>{{ curQuestion.question }}</strong>
            </div>
            <div class="card-body">
        <textarea v-model="answerInput"
                  class="form-control"
                  rows="4"
                  placeholder="최소 35자 이상 입력해주세요"
                  maxlength="1000"></textarea>
              <div class="text-end text-muted small mt-1">{{ answerInput.length }} / 1000자</div>
              <button class="btn btn-primary w-100 mt-3" @click="submitAnswer">
                <i class="fas fa-paper-plane me-1"></i> 제출하기
              </button>
            </div>
          </div>

          <div class="d-flex justify-content-between mt-3">
            <button class="btn btn-outline-secondary" @click="prevQ"><i class="fas fa-arrow-left me-1"></i> 이전</button>
            <button class="btn btn-outline-secondary" @click="nextQ">다음 <i class="fas fa-arrow-right ms-1"></i></button>
          </div>
        </div>
      </transition>
    </section>

    <BoardTabs v-model="activeTab" :tabs="TABS" />
    <div v-if="activeTab === 'DATA'" class="mb-2">
      <ul class="nav nav-pills small">
        <li v-for="cat in dataCategories" :key="cat"
            class="nav-item">
          <button class="nav-link"
                  :class="{ active: selectedCategory === cat }"
                  @click="changeCategory(cat)">
            {{ cat }}
          </button>
        </li>
      </ul>
    </div>
    <FeedCard
        v-for="n in notices"
        :key="n.feedUID"
        :post="n"
        notice
        class="mb-2"
    />


    <FeedCard
        v-for="p in posts"
        :key="p.feedUID"
        :post="p"
        :is-vote="!p.id"
        :comment-count="counts[p.feedUID]"
        class="mb-2"
    />


    <Pagination
        :page="page"
        :totalPages="totalPage"
        @change="fetchFeeds"
    />

    <Spinner v-if="loading" />
  </section>
</template>

<script setup>

import { ref, computed, watch, onMounted , onActivated, onDeactivated } from 'vue'
import { useRouter }             from 'vue-router'
import { useRoute } from 'vue-router'
import api                       from '@/utils/api'
import * as bootstrap from 'bootstrap'
import SearchBar from '@/components/SearchBar.vue'
import Pagination                from '@/common/Pagination.vue'
import BoardTabs                 from '@/components/BoardTabs.vue'
import FeedCard                  from '@/pages/feed/FeedCard.vue'
import Spinner                   from '@/components/Spinner.vue'
const keyword   = ref('')
const todoAlert = ref('')
const route = useRoute()
  function doSearch () {
    if (!keyword.value.trim()) return
    router.push({ path:'/search', query:{ q:keyword.value.trim() } })
  }
// const sb = useSidebarStore()
//
//
// const { dDayList, todoList, todoProgress, visitorStats, topWriters } = storeToRefs(sb)


// let timer = null
// onActivated(() => {
//   timer = setInterval(sidebar.loadLive, 30_000)   // 30초
// })
// onDeactivated(() => {
//   if (timer) clearInterval(timer)
// })
  const itQs  = ref([])
  const genQs = ref([])
  const curCat = ref('IT')
  const curIdx = ref(0)
  const answerInput = ref('')
  const isInterviewOpen = ref(false)

  const curArr       = computed(() => curCat.value==='IT'?itQs.value:genQs.value)
  const curQuestion  = computed(() => curArr.value[ curIdx.value ] ?? null)

  function changeCat (cat) {
    curCat.value = cat
    curIdx.value = 0
  }
  function prevQ(){ curIdx.value = (curIdx.value-1+curArr.value.length)%curArr.value.length }
  function nextQ(){ curIdx.value = (curIdx.value+1)%curArr.value.length }

  async function loadInterviewQs(){
    try {
      const { data } = await api.get('/interview/test')
      itQs.value  = data.filter(q => q.category === 'IT')
      genQs.value = data.filter(q => q.category === '일반')

      curIdx.value = 0
    } catch (e) {
      console.error('면접 질문 로드 실패', e)
    }
  }
  async function submitAnswer(){
    const txt = answerInput.value.trim()
    if(!txt)       return alert('답변을 입력하세요')
    if(txt.length<35) return alert('답변은 최소 35자 이상입니다')

    const token = localStorage.getItem('token')
    if(!token) return alert('로그인이 필요합니다')

    try{
      await api.post('/api/save/interview/question',{
        questionId : curQuestion.value.id,
        answer     : txt,
        title      : curQuestion.value.question,
        category   : curQuestion.value.category
      },{ headers:{ Authorization:`Bearer ${token}` }})
      alert('답변 저장 완료!')
      answerInput.value=''
    }catch(e){
      alert('저장 실패')
    }
  }

  const bestAnswers = ref([])
  const bestIdx     = ref(0)
  let   modal       = null

  async function showBestAnswers(){
    try{
      const ids = ['263','87','93'].join(',')
      const { data } = await api.get('/interview/best/answer',
          { headers:{ 'X-Question-Ids':ids }})
      if(!data.length) return alert('베스트 답변이 없습니다')

      bestAnswers.value = data
      bestIdx.value     = 0
      openBestModal()
    }catch(e){ alert('데이터 오류') }
  }
  function openBestModal(){
    const ans = bestAnswers.value[bestIdx.value]
    document.getElementById('bestAnswerModalLabel').innerText = `💬 ${ans.title}`
    document.getElementById('bestAnswerModalBody').innerHTML  =
        `<p><strong>작성자:</strong> ${ans.username||'익명'}</p><hr><p>${ans.answer}</p>`
    modal ??= new bootstrap.Modal('#bestAnswerModal')
    modal.show()
  }
  function nextBest(){
    bestIdx.value = (bestIdx.value+1)%bestAnswers.value.length
    openBestModal()
  }

  onMounted(loadInterviewQs)

  const TABS = [
    { id: 'ALL', label: '전체 글', url: '/feeds' },
    { id: 'BEST', label: '이번주 인기글', url: '/search/view/feed/best' },
    { id: 'VOTE', label: '투표', url: '/search/view/vote/page' },
    { id: 'DATA', label: '학습 자료', url: '/data/feed', requiresCategory: true },
    { id: 'NOTICE', label: '공지사항', url: '/notice/feed' },
    { id: 'QNA', label: 'Q&A', url: '/data/feed', category: 'Q/A' }
  ]
  const dataCategories = ['자료', '기술', '취업', '자격증'];
  const activeTab  = ref('ALL')
  const selectedCategory = ref('자료')
  const loading    = ref(false)
  const page       = ref(0)
  const totalPage  = ref(0)
  const posts      = ref([])
  const notices    = ref([])
  const counts     = ref({})

  const router = useRouter()
  function changeCategory(cat) {
    selectedCategory.value = cat
    fetchFeeds(0)
  }

  async function fetchNotice () {
    try {
      const { data } = await api.get('/list/notice')
      notices.value = data ?? []
    } catch (err) {
      console.error('공지사항 로딩 실패:', err)
    }
  }
async function fetchFeeds(newPage = page.value) {
  const tab = TABS.find(t => t.id === activeTab.value)
  if (!tab) return

  loading.value = true
  page.value = newPage

  // ⭐ 페이지 쿼리 URL 반영
  router.replace({ query: { ...route.query, page: newPage } })

  const params = { page: newPage, size: 10 }
  if (tab.category) params.category = tab.category
  if (tab.id === 'DATA') params.category = selectedCategory.value

  try {
    const { data } = await api.get(tab.url, { params })
    const allPosts = [...(data.data ?? []), ...(data.vote ?? [])]
        .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
        .slice(0, 10)

    posts.value = allPosts
    counts.value = data.count ?? {}
    totalPage.value = data.totalPage ?? 0

    if (activeTab.value === 'ALL') {
      const noticeRes = await api.get('/list/notice')
      notices.value = noticeRes.data ?? []
    } else {
      notices.value = []
    }
  } catch (err) {
    console.error(`${tab.label} 로딩 실패`, err)
  } finally {
    loading.value = false
  }
}


async function fetchPage (p = 0) {
    page.value = p;
    loading.value = true

    const tabInfo = TABS.find(t => t.id === activeTab.value)
    if (!tabInfo) {
      console.warn('탭 정보가 없습니다:', activeTab.value)
      return
    }

    try {
      const { data } = await api.get(tabInfo.url, { params: { page: p, size: 10 } })
      posts.value      = data.data ?? []
      counts.value     = data.count ?? {}
      totalPage.value  = data.totalPage ?? 0

      if (activeTab.value === 'ALL') {
        await fetchNotice()
      } else {
        notices.value = []
      }
    } catch (err) {
      console.error('피드 로딩 실패:', err)
    } finally {
      loading.value = false
    }
  }
  onMounted(() => {
    const p = parseInt(route.query.page) || 0
    fetchFeeds(p)
    fetchNotice()
  })

  watch(activeTab, () => fetchFeeds(0))
  function goDetail (post) {
    router.push({ name: 'feed-detail', params: { id: post.feedUID } })
  }
</script>

<style scoped>
  .board-wrap {
    max-width: 750px;
    margin: 0 auto;
  }

  .interview-title {
    font-size: 1.5rem;
    font-weight: 700;
    letter-spacing: -0.02rem;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 0.5rem;
    flex-wrap: wrap;
  }
  .search-bar {
    background: #f8f9fa;
    border: 1px solid #dee2e6;
    border-radius: 8px;
  }
  .search-bar input {
    font-size: 0.95rem;
  }
  .search-bar i {
    font-size: 1rem;
  }
  .interview-title i {
    margin-right: 0.25rem;
  }
  .btn-group .btn.active {
    background-color: #0d6efd;
    color: white;
    border-color: #0d6efd;
  }

  textarea.form-control:focus {
    border-color: #86b7fe;
    box-shadow: 0 0 0 0.2rem rgba(13, 110, 253, 0.25);
  }


  .interview-title.active {
    color: #0d6efd;
  }

  @media (max-width: 576px) {
    .interview-title {
      font-size: 1.2rem;
      flex-direction: column;
      align-items: flex-start;
    }

    .toggle-icon {
      font-size: 1.2rem;
      margin-top: 0.5rem;
    }
  }

</style>
