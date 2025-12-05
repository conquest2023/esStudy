<template>
  <section class="search-result-wrap">

    <!-- 헤더 -->
    <div class="result-header">
      <div class="result-title">
        <i class="fas fa-search"></i>
        <span class="keyword">'{{ keyword }}'</span> 검색 결과
        <span class="mode-badge">{{ typeLabel }}</span>
      </div>
      <div class="result-meta">
        <span class="count-dot"></span>
        <span class="meta-text">총 {{ allResults.length }}건</span>
      </div>
    </div>

    <!-- 로딩 스켈레톤 -->
    <div v-if="loading" class="skeleton-list">
      <div v-for="n in 4" :key="n" class="skeleton-card">
        <div class="sk-title shimmer"></div>
        <div class="sk-sub shimmer"></div>
        <div class="sk-metrics">
          <div class="sk-metric shimmer"></div>
          <div class="sk-metric shimmer"></div>
        </div>
      </div>
    </div>

    <!-- 결과 리스트 -->
    <div v-else-if="displayed.length" class="result-list">
      <article
          v-for="item in displayed"
          :key="item.feedUID ?? item.id"
          class="feed-card"
          @click="goDetail(item.feedUID ?? item.id)"
          tabindex="0"
          @keyup.enter="goDetail(item.feedUID ?? item.id)"
          role="button"
          aria-label="검색 결과 항목">
        <div class="feed-card__main">
          <h6 class="feed-card__title">{{ item.title }}</h6>
          <div class="feed-card__meta">
            <span class="author">{{ item.username }}</span>
            <span class="dot">•</span>
            <span class="time">{{ formatTime(item.createdAt) }}</span>
          </div>
        </div>
        <div class="feed-card__stats">
          <span class="metric">
            <i class="bi bi-eye"></i>{{ item.viewCount ?? 0 }}
          </span>
          <span class="metric">
            <i class="bi bi-heart-fill"></i>{{ item.likeCount ?? 0 }}
          </span>
        </div>
      </article>

      <!-- 페이징 -->
      <nav class="pager" v-if="totalPages > 1" aria-label="Search pagination">
        <button class="pg-btn" :disabled="page <= 1" @click="move(page - 1)">이전</button>
        <button
            v-for="p in pagesToShow"
            :key="p"
            class="pg-btn"
            :class="{ active: p === page }"
            @click="move(p)">
          {{ p }}
        </button>
        <button class="pg-btn" :disabled="page >= totalPages" @click="move(page + 1)">다음</button>
      </nav>
    </div>

    <!-- 빈 상태 -->
    <div v-else class="empty">
      <div class="empty-illust">
        <div class="circle"></div>
        <div class="glass"></div>
      </div>
      <p class="empty-text">
        검색 결과가 없습니다.<br />
        다른 키워드를 시도해보세요!
      </p>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '@/utils/api'

const route = useRoute()
const router = useRouter()

const keyword = ref(route.query.text || '')
const type    = ref(route.query.type || 'content') // 'content' | 'user'
const loading = ref(false)

// 전체 결과 + 클라 페이징
const allResults = ref([])
const pageSize = 10
const page = ref(Number(route.query.page) || 1)

const totalPages = computed(() =>
    Math.max(1, Math.ceil((allResults.value?.length || 0) / pageSize))
)
const displayed = computed(() => {
  const start = (page.value - 1) * pageSize
  return allResults.value.slice(start, start + pageSize)
})

const pagesToShow = computed(() => {
  const arr = []
  const maxButtons = 5
  let start = Math.max(1, page.value - Math.floor(maxButtons / 2))
  let end   = Math.min(totalPages.value, start + maxButtons - 1)
  if (end - start + 1 < maxButtons) start = Math.max(1, end - maxButtons + 1)
  for (let p = start; p <= end; p++) arr.push(p)
  return arr
})

const typeLabel = computed(() => (type.value === 'user' ? '이름 검색' : '제목+내용 검색'))

onMounted(fetchSearch)
watch(
    () => [route.query.text, route.query.type, route.query.page],
    () => {
      keyword.value = route.query.text || ''
      type.value    = route.query.type || 'content'
      page.value    = Number(route.query.page) || 1
      fetchSearch()
    }
)

async function fetchSearch() {
  if (!keyword.value) return
  loading.value = true
  try {
    let resp
    if (type.value === 'user') {
      resp = await api.get(`/search/user/${encodeURIComponent(keyword.value)}`)
    } else {
      resp = await api.get(`/search/${encodeURIComponent(keyword.value)}`)
    }
    const list = resp.data?.content ?? resp.data?.data ?? []
    allResults.value = Array.isArray(list) ? list : []
  } catch (e) {
    console.error('검색 실패', e)
    allResults.value = []
  } finally {
    loading.value = false
  }
}

function move(next) {
  if (next < 1 || next > totalPages.value) return
  router.replace({ query: { ...route.query, page: next } })
}

function goDetail(id) {
  router.push({ name: 'feed-detail', params: { id } })
}

function formatTime(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString('ko-KR', { hour12: false })
}
</script>

<style scoped>
.search-result-wrap {
  max-width: 840px;
  margin: 0 auto;
  padding: 100px 16px 40px;
}

.result-header {
  display: flex;
  align-items: end;
  justify-content: space-between;
  margin-bottom: 18px;
  padding: 16px 18px;
  border-radius: 16px;
  background: linear-gradient(135deg, #f7f9ff 0%, #f0f4ff 100%);
  border: 1px solid #e6ecff;
  box-shadow: 0 4px 16px rgba(40, 80, 200, 0.06);
}
.result-title {
  font-weight: 700;
  font-size: 18px;
  display: flex;
  align-items: center;
  gap: 8px;
}
.result-title i { color: #3b5bff; }
.keyword { color: #1f2a44; }
.mode-badge {
  margin-left: 6px;
  padding: 4px 10px;
  font-size: 12px;
  border-radius: 999px;
  background: #ffffffaa;
  border: 1px solid #dfe7ff;
  color: #3a57ff;
}
.result-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #6b7280;
  font-size: 13px;
}
.count-dot {
  width: 8px; height: 8px; border-radius: 50%;
  background: #3b82f6;
  box-shadow: 0 0 0 6px rgba(59,130,246,0.12);
}
.meta-text { letter-spacing: .2px; }

/* ===== 스켈레톤 ===== */
.skeleton-list { display: grid; gap: 12px; }
.skeleton-card {
  border: 1px solid #eee;
  border-radius: 14px;
  padding: 16px;
  background: #fff;
}
.shimmer {
  background: linear-gradient(90deg, #f4f6f8 25%, #eef1f4 37%, #f4f6f8 63%);
  background-size: 400% 100%;
  animation: shimmer 1.2s infinite;
}
.sk-title { height: 16px; width: 60%; border-radius: 8px; margin-bottom: 10px; }
.sk-sub { height: 12px; width: 35%; border-radius: 8px; }
.sk-metrics { display: flex; gap: 10px; margin-top: 12px; }
.sk-metric { height: 12px; width: 80px; border-radius: 999px; }
@keyframes shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

/* ===== 카드 ===== */
.result-list { display: grid; gap: 12px; }
.feed-card {
  position: relative;
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 12px;
  padding: 16px 18px;
  border-radius: 16px;
  background: #ffffff;
  border: 1px solid #eef0f4;
  box-shadow: 0 4px 14px rgba(0,0,0,0.04);
  transition: transform .15s ease, box-shadow .15s ease, border-color .2s ease;
  outline: none;
}
.feed-card::before{
  content: "";
  position: absolute;
  left: 0; top: 0; bottom: 0;
  width: 4px;
  border-radius: 16px 0 0 16px;
  background: linear-gradient(180deg, #7aa2ff, #3b5bff);
  opacity: .75;
}
.feed-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 22px rgba(25, 60, 150, 0.10);
  border-color: #dde4ff;
}
.feed-card:focus-visible {
  box-shadow: 0 0 0 3px rgba(59, 91, 255, 0.25), 0 8px 20px rgba(0,0,0,0.06);
}

.feed-card__title {
  margin: 0 0 6px 0;
  font-size: 16px;
  font-weight: 700;
  color: #0f172a;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.feed-card__meta {
  font-size: 13px;
  color: #6b7280;
  display: flex;
  align-items: center;
  gap: 8px;
}
.feed-card__meta .dot { color: #cbd5e1; }
.feed-card__stats {
  display: flex;
  align-items: center;
  gap: 12px;
}
.metric {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #475569;
  padding: 6px 10px;
  border-radius: 999px;
  background: #f8fafc;
  border: 1px solid #eef2f7;
}
.metric i.bi-heart-fill { color: #ef4444; }

/* ===== 페이징 ===== */
.pager {
  display: flex;
  justify-content: center;
  gap: 6px;
  margin-top: 10px;
}
.pg-btn {
  border: 1px solid #e5e7eb;
  background: #fff;
  padding: 6px 12px;
  font-size: 13px;
  border-radius: 10px;
  cursor: pointer;
  transition: all .15s ease;
}
.pg-btn:hover { border-color: #c7d2fe; box-shadow: 0 4px 12px rgba(59,91,255,0.08); }
.pg-btn:disabled { opacity: .45; cursor: not-allowed; }
.pg-btn.active {
  background: #3b5bff;
  color: #fff;
  border-color: #3b5bff;
  box-shadow: 0 6px 16px rgba(59,91,255,0.25);
}

/* ===== 빈 상태 ===== */
.empty {
  display: grid;
  place-items: center;
  gap: 12px;
  padding: 48px 0 36px;
  color: #6b7280;
}
.empty-illust {
  position: relative;
  width: 120px; height: 120px;
}
.empty-illust .circle{
  position: absolute; inset: 0;
  background: radial-gradient(circle at 30% 30%, #eef3ff, #e7edff);
  border-radius: 50%;
  box-shadow: inset 0 8px 16px rgba(59,91,255,0.06);
}
.empty-illust .glass{
  position: absolute; left: 32px; top: 28px;
  width: 56px; height: 56px; border-radius: 50%;
  border: 4px solid #3b5bff;
  background: #fff;
  box-shadow: 0 8px 20px rgba(59,91,255,0.15);
}
.empty-text { text-align: center; line-height: 1.6; }
</style>
