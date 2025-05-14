<!-- src/pages/feed/FeedList.vue -->
<template>
  <section class="board-wrap">
    <SearchBar class="mb-3" />


    <section class="my-4">
      <h2 class="interview-title"
          :class="{ active: isInterviewOpen }"
          @click="isInterviewOpen = !isInterviewOpen">
        <span><i class="fas fa-comments"></i> 면접 질문</span>
        <small class="text-muted ms-2 d-none d-md-inline">(열기/닫기)</small>
        <span class="toggle-icon fs-5">{{ isInterviewOpen ? '▲' : '▼' }}</span>
      </h2>
      <transition name="fade">
        <div v-show="isInterviewOpen" class="mt-3">

          <!-- IT / 일반 탭 -->
          <div class="d-flex justify-content-between flex-wrap">
            <div class="btn-group mb-2">
              <button
                  v-for="cat in ['IT','일반']"
                  :key="cat"
                  class="btn btn-tab"
                  :class="{ active:curCat===cat }"
                  @click="changeCat(cat)">
                <i :class="cat==='IT' ? 'fas fa-laptop-code' : 'fas fa-building'"></i>
                {{ cat==='IT' ? 'IT 기업' : '일반 기업' }}
              </button>
            </div>

            <button class="btn btn-outline-dark btn-sm d-flex align-items-center gap-1 mb-2"
                    @click="showBestAnswers">
              <i class="fas fa-trophy"></i> 면접 베스트 답변 보기
            </button>
          </div>

          <!-- 질문 슬라이드 -->
          <div v-if="curQuestion" class="card question-card mb-3">
            <div class="card-header">
              <h6 class="mb-0">{{ curQuestion.question }}</h6>
            </div>
            <div class="card-body">
              <textarea
                  v-model="answerInput"
                  rows="3"
                  class="form-control"
                  placeholder="여기에 답변을 입력하세요…">
                </textarea>
              <button class="btn btn-primary mt-2" @click="submitAnswer">
                답변 제출
              </button>
            </div>
          </div>

          <!-- 이전 / 다음 -->
          <div class="d-flex justify-content-between">
            <button class="btn btn-outline-secondary btn-sm" @click="prevQ">← 이전</button>
            <button class="btn btn-outline-secondary btn-sm" @click="nextQ">다음 →</button>
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
    <!-- 📢 공지 -->
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
import { ref, computed, watch, onMounted } from 'vue'
import { useRouter }             from 'vue-router'
import api                       from '@/utils/api'
import * as bootstrap from 'bootstrap'
import SearchBar from '@/components/SearchBar.vue'
import Pagination                from '@/common/Pagination.vue'
import BoardTabs                 from '@/components/BoardTabs.vue'
import FeedCard                  from '@/pages/feed/FeedCard.vue'
import Spinner                   from '@/components/Spinner.vue'
  const keyword   = ref('')
  const todoAlert = ref('')
  function doSearch () {
    if (!keyword.value.trim()) return
    router.push({ path:'/search', query:{ q:keyword.value.trim() } })
  }


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
      const { data } = await api.get('/list/noitce')
      notices.value = data ?? []
    } catch (err) {
      console.error('공지사항 로딩 실패:', err)
    }
  }
  async function fetchFeeds(newPage = 0) {

    const tab = TABS.find(t => t.id === activeTab.value)
    if (!tab) return

    loading.value = true
    page.value = newPage

    const params = { page: newPage, size: 10 }
    if (tab.category) params.category = tab.category
    if (tab.id === 'DATA') params.category = selectedCategory.value
    try {
      const { data } = await api.get(tab.url, { params })
      const allPosts = [...(data.data ?? []), ...(data.vote ?? [])]
          .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
          .slice(0, 10)  // ✨ 최신순 정렬 후 상위 10개만 자르기

      posts.value = allPosts
      counts.value = data.count ?? {}
      totalPage.value = data.totalPage ?? 0
      counts.value = data.count ?? {}

      if (activeTab.value === 'ALL') {
        const noticeRes = await api.get('/list/noitce')
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
        notices.value = []  // 다른 탭에선 공지 안 보이게
      }
    } catch (err) {
      console.error('피드 로딩 실패:', err)
    } finally {
      loading.value = false
    }
  }
  onMounted(() => {
    fetchFeeds(0)
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
  /* style scoped 영역 안에 넣어주세요 */
  .interview-title {
    font-size: 1.5rem;
    font-weight: 700;
    letter-spacing: -0.02rem;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 0.5rem; /* 아이콘과 toggle 아이콘 사이 여백만 유지 */
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
    margin-right: 0.25rem; /* 아이콘과 텍스트를 거의 붙임 */
  }

  .toggle-icon {
    margin-left: auto; /* 오른쪽 끝으로 밀기 */
  }

  .interview-title.active {
    color: #0d6efd; /* Bootstrap primary */
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
