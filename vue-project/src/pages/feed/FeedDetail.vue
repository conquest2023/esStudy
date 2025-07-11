<template>
  <PrevNextButtons  v-if="loaded" :posts="posts" />

  <section v-if="loaded" class="container my-4 pt-navbar">
    <div v-if="isOwner" class="text-end mb-2">
      <div class="dropdown d-none d-md-inline">
        <button class="btn btn-outline-secondary" data-bs-toggle="dropdown">
          <i class="fas fa-ellipsis-v"></i>
        </button>
        <ul class="dropdown-menu dropdown-menu-end">
          <li><a class="dropdown-item" @click="goEdit"><i class="fas fa-edit me-2"></i>수정</a></li>
          <li><a class="dropdown-item text-danger" @click="onDelete"><i class="fas fa-trash me-2"></i>삭제</a></li>
        </ul>
      </div>
      <div class="dropup d-md-none position-fixed" style="bottom:80px;right:24px;z-index:1051">
        <button
            class="btn btn-primary rounded-circle shadow dropdown-toggle"
            data-bs-toggle="dropdown"
            aria-expanded="false"
            style="width:56px;height:56px;">
          <i class="fas fa-ellipsis-v"></i>
        </button>
        <ul class="dropdown-menu dropdown-menu-end">
          <li><a class="dropdown-item" @click="goEdit"><i class="fas fa-edit me-2"></i>수정</a></li>
          <li><a class="dropdown-item text-danger" @click="onDelete"><i class="fas fa-trash me-2"></i>삭제</a></li>
        </ul>
      </div>
    </div>

    <div class="card shadow-sm feed-card">
      <div class="card-body">
        <h2 class="feed-title">{{ feed.title }}</h2>
        <p class="feed-meta">
          <strong>작성자:</strong>
          <RouterLink :to="`/user/profile/${feed.username}`" class="text-primary fw-semibold">
            {{ rankBadge(feed.username) }} {{ feed.username }}
          </RouterLink>
          · <span>{{ dateText }}</span>
        </p>
        <div class="feed-content" v-html="feedHtml" />

        <div class="feed-actions d-flex justify-content-between align-items-center mt-3 position-relative">
          <span class="text-muted">
            <i class="bi bi-chat-dots"></i> {{ comments.length }}
          </span>
          <button class="btn btn-sm btn-outline-danger like-btn position-relative" @click="toggleLike">
            <i :class="['bi', liked ? 'bi-heart-fill' : 'bi-heart']" />
            <span class="ms-1">{{ likeCount }}</span>
          </button>
        </div>
      </div>
    </div>

    <h5 class="mt-5"><i class="bi bi-chat"></i> 댓글</h5>
    <div v-if="comments.length === 0" class="text-muted">댓글이 없습니다.</div>
    <div v-for="c in comments" :key="c.commentUID + '-' + reloadTrigger" class="comment-item">
      <div class="comment-body flex-grow-1">
        <div class="d-flex justify-content-between">
          <div class="meta">
            <RouterLink :to="`/user/profile/${c.username}`" class="text-primary fw-semibold">
              {{ rankBadge(c.username) }} {{ c.username }}
            </RouterLink>
            <small class="ms-2 text-muted">{{ fmtDate(c.createdAt) }}</small>
          </div>
          <div v-if="c.commentOwner">
            <button class="btn btn-sm btn-link text-danger" @click="delComment(c)">삭제</button>
          </div>
        </div>
        <div class="mt-1" v-html="linkify(c.content)"></div>

        <div class="mt-2" v-if="replies && replies[c.commentUID]">
          <div
              v-for="rp in replies[c.commentUID]"
              :key="rp.replyUID"
              class="border-start ps-3 mb-2"
              style="font-size:0.9rem;">
            <strong>{{ rankBadge(rp.username) }}{{ rp.username }}</strong>
            <small class="text-muted ms-2">{{ fmtDate(rp.createdAt) }}</small>
            <div>{{ rp.content }}</div>
          </div>
        </div>

        <button class="btn btn-sm btn-outline-primary mt-2" @click="toggleReplyForm(c.commentUID)">답글 달기</button>
        <div v-show="activeReply === c.commentUID" class="mt-2">
          <textarea v-model="replyTexts[c.commentUID]" rows="2" class="form-control mb-2" placeholder="답글 입력" />
          <button
              class="btn btn-sm btn-primary"
              @click="submitReply(c.commentUID)"
              :disabled="replySendingMap[c.commentUID]"
          >
            답글 작성
          </button>
        </div>
      </div>
    </div>

    <div class="card shadow-sm p-3 mt-4">
      <h6 class="mb-3 d-flex justify-content-between align-items-center">
        <span><i class="bi bi-pencil-square"></i> 댓글 쓰기</span>
        <button v-if="!login" class="btn btn-sm btn-outline-secondary" @click="router.push('/login')">로그인</button>
      </h6>
      <textarea v-model="commentText" rows="3" class="form-control mb-2" :placeholder="login ? '내용 입력' : '로그인 후 이용'" />
      <button class="btn btn-success" :disabled="!login || sending || !commentText.trim()" @click="submitComment">
        작성하기
      </button>
    </div>
  </section>
  <div v-else class="text-center pt-5"><i class="bi bi-arrow-repeat fs-2 spin"></i></div>
</template>

<!-- 보통 public/index.html 에 포함되어야 함 -->
<script setup>
import { ref, onMounted, computed ,watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '@/utils/api'
import PrevNextButtons from '@/components/PrevNextButtons.vue'

import { useUserStore } from '@/stores/user'
import { useToast } from '@/composables/useToast'
import { RouterLink } from 'vue-router'

  const route   = useRoute()
  const router  = useRouter()
  const store   = useUserStore()
  const posts = ref([])
  const id         = route.params.id
  const feed       = ref({})
  const feedHtml   = ref('')
  const comments   = ref([])
  const likeCount  = ref(0)
  const liked      = ref(false)
  const loaded     = ref(false)
  const replies = ref({})
  const replyText = ref('')
  const replyTexts = ref({})
  const replySendingMap = ref({})
  const activeReply = ref(null)
  const commentText= ref('')
  const sending    = ref(false)
  const reloadTrigger = ref(0)
  const login = computed(() => store.isLoggedIn)
  const isOwner = computed(() => feed.value?.Owner === true)

const PAGE_SIZE = 10
const pageParam = computed(() => {
  const q = parseInt(route.query.page ?? '0', 10)
  return Number.isNaN(q) ? 0 : q        // 잘못된 값이면 0
})
function convertLinks(txt = '') {
  return txt.replace(/(https?:\/\/[^\s<"]+)/g, (m, url, offset, str) => {
    const prev = str.slice(Math.max(0, offset - 5), offset)
    if (/src=\"?$/.test(prev)) return m

    const youtubeMatch = m.match(/(?:youtu\.be\/|youtube\.com\/watch\?v=)([a-zA-Z0-9_-]+)/)
    if (youtubeMatch) {
      const videoId = youtubeMatch[1]
      return `
        <iframe width="100%" height="315"
          src="https://www.youtube.com/embed/${videoId}"
          frameborder="0"
          allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
          allowfullscreen
        ></iframe>
      `
    }

    // 일반 링크면 <a> 태그
    return `<a href="${m}" target="_blank">${m}</a>`
  })
}

function formatDate(dateTimeString) {
  if (!dateTimeString) return '';

  const date = new Date(dateTimeString);

  const month = date.getMonth() + 1;
  const day = date.getDate();
  let hour = date.getHours();
  const minute = date.getMinutes();

  const period = hour >= 12 ? '오후' : '오전';
  hour = hour % 12 || 12;
  const formattedMinute = minute < 10 ? `0${minute}` : minute;

  return `${month}. ${day}. ${period} ${hour}:${formattedMinute}`;
}
const fmtDate = formatDate

const dateText = computed(() => formatDate(feed.value.createdAt))
const topWriters = JSON.parse(localStorage.getItem('topWriters')||'{}')
function rankBadge(name){
  const r = topWriters[name]||0
  return r===1?'👑':r===2?'🥇':r===3?'🥈':r>0&&r<=5?'🥉':''
}

  function linkify(text = '') {
    const urlRegex = /(https?:\/\/[^\s]+)/g
    return text.replace(urlRegex, url => `<a href="${url}" target="_blank">${url}</a>`)
  }

  function decodeHtmlEntities (encoded = '') {
    const el = document.createElement('textarea')
    el.innerHTML = encoded
    return el.value
  }


async function loadFeedDetail(feedUID) {
  try {
    loaded.value = false
    const { data } = await api.get('/detail', { params: { id: feedUID } })
    feed.value = { ...data.data, Owner: data.Owner }
    function normalize(html = '') {
      return html
          .replace(/></g, '>\u200B<')
          .replace(/<\/div>/g, '<br>')
          .replace(/<div>/g, '');
    }
    /* UID 목록 그대로 가져오던 로직 유지 */
    const uidList = await fetchFeedUIDs(pageParam.value, PAGE_SIZE)
    posts.value   = uidList.map(uid => ({ feedUID: uid }))

    /* 나머지 상태 세팅도 그대로 */
    feedHtml.value = convertLinks(normalize(decodeHtmlEntities(data.data.description || '')))
    comments.value = data.comment || []
    replies.value  = data.replies || {}
    likeCount.value= data.data.likeCount || 0
    liked.value    = data.isLiked
    loaded.value   = true
    fetch('/api/increaseViewCount', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify({ id })
    }).catch(e => console.error('조회수 증가 실패', e))
  } catch (e) {
    console.error(e)
    router.replace('/')
  }
}
onMounted(() => loadFeedDetail(route.params.id))
async function fetchFeedUIDs(p = 0, size = 10) {
  try {
    const res = await fetch(`/api/feeds/ids?page=${p}&size=${size}`)
    const json = await res.json()
    return json.ids || []
  } catch (e) {
    console.error('feedUID 목록 로딩 실패', e)
    return []
  }
}
watch(
    () => route.params.id,
    (newId, oldId) => {
      if (newId && newId !== oldId) loadFeedDetail(newId)
    }
)

  async function toggleLike(){
    if(!login.value){
      push('로그인이 필요합니다'); return }
    const prev = liked.value
    liked.value = !prev
    likeCount.value += prev?-1:1
    try {
      await api.post(prev?`/search/view/feed/cancel-like/${id}`:`/search/view/feed/increase-like/${id}`)
    } catch(e){ liked.value=prev; likeCount.value+=prev?1:-1 }
  }

async function submitComment() {
  if (sending.value) return
  if (!commentText.value.trim()) {
    alert("댓글 내용을 입력하세요!")
    return
  }
  const token = localStorage.getItem("token")
  if (!token) {
    alert("로그인이 필요합니다.")
    router.push("/login")
    return
  }
  sending.value = true
  try {
    await api.post(`/search/view/comment/id?feedUID=${id}`, {
      content: commentText.value
    }, {
      headers: { Authorization: `Bearer ${token}` }
    })
    await new Promise(resolve => setTimeout(resolve, 1000))
    const { data } = await api.get('/detail', { params: { id } })
    comments.value = data.comment || []
    commentText.value = ''
  } catch (e) {
    console.error(e)
    alert("댓글 저장 중 오류 발생")
  } finally {
    sending.value = false
  }
}

function goEdit(){
  router.push({
    path:'/search/view/feed/update',
    query:{ id:feed.value.feedUID}
      }
  )}
  async function onDelete(){
    if(!confirm('정말로 이 게시글을 삭제하시겠습니까?')) return
    try{
    await api.post('/search/view/feed/delete',{ id:feed.value.id })
      await new Promise(resolve => setTimeout(resolve, 1000))
      router.push(`/`)
    } catch(e){
      push('삭제 중 오류 발생')
    }
  }
  async function delComment(c){
      if(!confirm('삭제할까요?')) return
      await api.post('/search/view/comment/delete', null, { params:{ id:c.commentUID, feedUID:id }})
      comments.value = comments.value.filter(v=>v.commentUID!==c.commentUID)
    }
  function toggleReplyForm(commentUID) {
    activeReply.value = activeReply.value === commentUID ? null : commentUID
    replyText.value = ''
  }
async function submitReply(commentUID) {
  const text = replyTexts.value[commentUID] || ''
  if (!login.value) {
    push('로그인이 필요합니다')
    router.push('/login')
    return
  }
  if (!text.trim()) {
    alert('답글 내용을 입력하세요.')
    return
  }
  if (replySendingMap.value[commentUID]) return
  replySendingMap.value[commentUID] = true
  try {
    await api.post('/search/view/reply/save', {
      commentUID:commentUID,
      feedUID: id,
      content: text,
    })
    await new Promise(resolve => setTimeout(resolve, 1000))
    const { data } = await api.get('/detail', { params: { id } })
    comments.value = data.comment || []
    replies.value = JSON.parse(JSON.stringify(data.replies))
    activeReply.value = null
    replyTexts.value[commentUID] = ''
    reloadTrigger.value++
  } catch (e) {
    console.error(e)
    push('답글 저장 실패')
  } finally {
    replySendingMap.value[commentUID] = false
  }
}

</script>

<style scoped>
  .pt-navbar {
    padding-top: 60px;
  }
  .feed-content {
    line-height: 1.6;
    white-space: pre-line;
  }
  .mt-4 {
    margin-top: 1.5rem;
  }
  .btn-primary.position-fixed { box-shadow:0 2px 6px rgba(0,0,0,.1); }
  .feed-card { border-radius:10px }
  .feed-title{ font-size:1.8rem;font-weight:700 }
  .comment-item{ gap:12px;padding:12px 0;border-bottom:1px solid #eee }
  .comment-avatar{ width:36px;height:36px
  ;border-radius:50%;background:#ddd;display:flex;align-items:center;justify-content:center;font-weight:bold }
  .spin{ animation:spin 1s linear infinite }
@keyframes spin{100%{transform:rotate(360deg)}}

</style>
