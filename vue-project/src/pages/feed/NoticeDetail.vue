<template>
  <section class="container my-4 pt-navbar">
    <div v-if="loading" class="text-center pt-5">
      <i class="bi bi-arrow-repeat fs-2 spin"></i>
    </div>

    <div v-else-if="error" class="alert alert-danger">
      {{ error }} <button class="btn btn-sm btn-outline-secondary ms-2" @click="goList">목록</button>
    </div>

    <div v-else class="card shadow-sm feed-card">
      <div class="card-body">
        <div class="d-flex justify-content-between align-items-start gap-3">
          <h2 class="feed-title mb-0">{{ notice.title }}</h2>
<!--          <button class="btn btn-sm btn-outline-secondary" @click="goList">-->
<!--            <i class="bi bi-list"></i> 목록-->
<!--          </button>-->
        </div>

        <p class="feed-meta text-muted mt-2">
          <span class="badge bg-primary-subtle text-primary me-2">공지사항</span>
          <strong class="me-2">{{ notice.username || '관리자' }}</strong>
          · <span class="me-2">{{ dateText }}</span>
          · <span><i class="bi bi-eye"></i> {{ notice.viewCount ?? 0 }}</span>
        </p>

        <hr>

        <!-- 본문 -->
        <div class="feed-content" v-html="htmlDesc"></div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '@/utils/api'

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const error   = ref('')
const notice  = ref({})

const idParam = computed(() => route.params.id)

/** 링크 자동 변환 + 간단 정리 */
function convertLinks(html = '') {
  if (!html) return ''
  // div→br 정리(선택 사항): 에디터에서 div만 썼을 때 줄바꿈 보정
  const normalized = html.replace(/<\/div>/g, '<br>').replace(/<div>/g, '')
  return normalized.replace(/(https?:\/\/[^\s<"]+)/g, m => `<a href="${m}" target="_blank" rel="noopener">${m}</a>`)
}

const htmlDesc = computed(() => convertLinks(notice.value.description))

function formatDate(iso) {
  if (!iso) return ''
  const d = new Date(iso)
  const mm = d.getMonth() + 1
  const dd = d.getDate()
  const h  = String(d.getHours()).padStart(2, '0')
  const m  = String(d.getMinutes()).padStart(2, '0')
  return `${mm}.${dd} ${h}:${m}`
}
const dateText = computed(() => formatDate(notice.value.createdAt))

async function loadDetail(id) {
  loading.value = true
  error.value = ''
  try {
    const { data } = await api.get(`/notice/detail/${id}`)
    // 서버가 바로 DTO를 주는 형태라 가정 (필드명은 질문에 맞춤)
    notice.value = {
      id:         data.detail.id ?? data?.ok?.id ?? null,
      title:      data.detail.title ?? '',
      description: data.detail.description ?? '',
      username:   data.detail.username ?? '관리자',
      createdAt:  data.detail.createdAt ?? null,
      viewCount:  data.detail.viewCount ?? 0,
    }
  } catch (e) {
    console.error(e)
    error.value = '공지사항을 불러오지 못했습니다.'
  } finally {
    loading.value = false
  }
}

function goList() {
  router.push('/notice') // 목록 라우트에 맞게 조정
}

onMounted(() => loadDetail(idParam.value))
watch(() => idParam.value, (n, o) => {
  if (n && n !== o) loadDetail(n)
})
</script>

<style scoped>
.pt-navbar { padding-top: 60px; }
.feed-card { border-radius: 10px; }
.feed-title { font-size: 1.8rem; font-weight: 700; }
.feed-content { line-height: 1.7; word-break: break-word; }
.spin { animation: spin 1s linear infinite; }
@keyframes spin { 100% { transform: rotate(360deg); } }
</style>
