<template>
  <section v-if="loaded" class="container my-4">
<!--    <div v-if="data.Owner">-->
<!--      <button class="btn btn-sm btn-danger">삭제</button>-->
<!--    </div>-->
    <!-- ─── 글 카드 ─────────────────────────────────────────── -->
    <div class="card shadow-sm feed-card">
      <div class="card-body">
        <h2 class="feed-title">{{ feed.title }}</h2>
        <p class="feed-meta">
          <strong>작성자:</strong> {{ badge }} {{ feed.username }} ·
          <span>{{ dateText }}</span>
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
    <div v-for="c in comments" :key="c.commentUID" class="comment-item">
      <div class="comment-body flex-grow-1">
        <div class="d-flex justify-content-between">
          <div class="meta">
            <b>{{ rankBadge(c.username) }} {{ c.username }}</b>
            <small class="ms-2 text-muted">{{ fmtDate(c.createdAt) }}</small>
          </div>
          <div v-if="c.commentOwner">
            <button class="btn btn-sm btn-link text-danger" @click="delComment(c)">삭제</button>
          </div>
        </div>
        <div class="mt-1" v-html="linkify(c.content)"></div>

        <!-- 대댓글 표시 -->
        <div class="mt-2" v-if="replies && replies[c.commentUID]">
          <div
              v-for="rp in replies[c.commentUID]"
              :key="rp.replyUID"
              class="border-start ps-3 mb-2"
              style="font-size:0.9rem;"
          >
            <strong>{{ rankBadge(rp.username) }}{{ rp.username }}</strong>
            <small class="text-muted ms-2">{{ fmtDate(rp.createdAt) }}</small>
            <div>{{ rp.content }}</div>
          </div>
        </div>


        <!-- 답글 작성 영역 -->
        <button class="btn btn-sm btn-outline-primary mt-2" @click="toggleReplyForm(c.commentUID)">답글 달기</button>
        <div v-show="activeReply === c.commentUID" class="mt-2">
          <textarea v-model="replyText" rows="2" class="form-control mb-2" placeholder="답글 입력" />
          <button class="btn btn-sm btn-primary" @click="submitReply(c.commentUID)">답글 작성</button>
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

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '@/utils/api'
import { useUserStore } from '@/stores/user'
import { useToast } from '@/composables/useToast'

  const route   = useRoute()
  const router  = useRouter()
  const store   = useUserStore()
  const { push } = useToast()

  const id         = route.params.id
  const feed       = ref({})
  const feedHtml   = ref('')
  const comments   = ref([])
  const likeCount  = ref(0)
  const liked      = ref(false)
  const loaded     = ref(false)
  const replies = ref({})
  const replyText = ref('')
  const activeReply = ref(null)
  const commentText= ref('')
  const sending    = ref(false)
  const login      = computed(() => !!store.token)
  function linkify(text = '') {
    const urlRegex = /(https?:\/\/[^\s]+)/g
    return text.replace(urlRegex, url => `<a href="${url}" target="_blank">${url}</a>`)
  }
  console.log(login)

  onMounted(async () => {
    try {
      const { data } = await api.get('http://localhost:8080/detail', { params: { id } })

      console.log(data)
      feed.value       = data.data
      feedHtml.value   = convertLinks(data.data.description || '').replace(/\n/g, '<br>')
      comments.value   = data.comment || []
      replies.value    = data.replies || {}
      liked.value      = data.isLiked
      likeCount.value  = data.data.likeCount || 0
      loaded.value     = true
    } catch (e) {
      push('게시글을 불러오지 못했습니다.')
      router.replace('/')
    }
  })
  function convertLinks(txt=''){
    return txt.replace(/(https?:\/\/[^\s<]+)/g, '<a href="$1" target="_blank">$1</a>')
  }
  function fmtDate(t){ return new Date(t).toLocaleString('ko-KR') }
  const dateText = computed(()=>fmtDate(feed.value.createdAt))


  const topWriters = JSON.parse(localStorage.getItem('topWriters')||'{}')
    function rankBadge(name){
      const r = topWriters[name]||0
      return r===1?'👑':r===2?'🥇':r===3?'🥈':r>0&&r<=5?'🥉':''
    }


  async function toggleLike(){
    if(!login.value){ push('로그인이 필요합니다'); return }
    const prev = liked.value
    liked.value = !prev
    likeCount.value += prev?-1:1
    try {
      await api.post(prev?`/search/view/feed/cancel-like/${id}`:`/search/view/feed/increase-like/${id}`)
    } catch(e){ liked.value=prev; likeCount.value+=prev?1:-1 }
  }

  async function submitComment() {
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

      await new Promise(resolve => setTimeout(resolve, 200))

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
      if (!login.value) {
        push('로그인이 필요합니다')
        router.push('/login')
        return
      }
      if (!replyText.value.trim()) {
        alert('답글 내용을 입력하세요.')
        return
      }
      try {
        await api.post('/search/view/reply/save', {
          commentUID,
          feedUID: id,
          content: replyText.value,
          username: localStorage.getItem('username') || '익명'
        })
        const { data } = await api.get('/detail', { params: { id } })
        replies.value = data.replies || {}
        replyText.value = ''
        activeReply.value = null
      } catch (e) {
        push('답글 저장 실패')
      }
    }
</script>

<style scoped>
  .feed-card { border-radius:10px }
  .feed-title{ font-size:1.8rem;font-weight:700 }
  .comment-item{ gap:12px;padding:12px 0;border-bottom:1px solid #eee }
  .comment-avatar{ width:36px;height:36px;border-radius:50%;background:#ddd;display:flex;align-items:center;justify-content:center;font-weight:bold }
  .spin{ animation:spin 1s linear infinite }
@keyframes spin{100%{transform:rotate(360deg)}}
</style>
