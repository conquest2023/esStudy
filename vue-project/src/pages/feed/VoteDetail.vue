<template>
  <div class="container my-4">
    <div class="poll-card mb-5">
      <div v-if="vote">
        <div class="poll-title mb-2" id="poll-title">{{ vote.title }}</div>
        <div class="text-muted mb-3">
          {{ badge }} {{ vote.username }} · {{ formatDate(vote.createdAt) }}
          <button v-if="vote.Owner" class="btn btn-sm btn-danger ms-2" @click="deleteVote">삭제</button>
        </div>
        <p class="mb-3" v-if="vote.description" style="white-space:pre-wrap">{{ vote.description }}</p>

        <!-- 항목 -->
        <h6 class="fw-bold">투표 항목</h6>
        <div class="vote-options mb-4">
          <div v-for="(opt, idx) in vote.voteType" :key="idx"
               class="poll-choice d-flex justify-content-between align-items-center"
               :class="{ active: selectedIndex === idx }"
               @click="selectOption(idx)">
            <span>{{ opt }}</span>
            <i class="far fa-circle"></i>
          </div>
        </div>

        <!-- 결과 -->
        <h6 class="fw-bold">현재 결과</h6>
        <div class="vote-results">
          <div class="mb-3" v-for="(cnt, opt, idx) in voteCounts" :key="opt">
            <div class="d-flex justify-content-between mb-1">
              <span>{{ opt }}</span>
              <span class="text-muted">{{ cnt }}명 · {{ getPercentage(cnt) }}%</span>
            </div>
            <div class="poll-bar-wrapper">
              <div class="poll-bar-fill" :style="{ width: getPercentage(cnt)+'%', background: pastel[idx % pastel.length] }">
                <span class="poll-bar-text">{{ getPercentage(cnt) }}%</span>
              </div>
            </div>
          </div>
        </div>

        <div class="d-flex justify-content-between align-items-center mt-4">
          <small class="text-muted">👥 {{ totalVotes }}명 참여</small>
          <div class="text-end">
            <button
                class="btn btn-kakao"
                @click="submitVote"
                :disabled="!isLoggedIn"
                :title="!isLoggedIn ? '로그인이 필요합니다' : ''"
            >
              투표하기
            </button>
            <p v-if="!isLoggedIn" class="text-danger small mt-2 mb-0">
              ※ 로그인 후 투표하실 수 있습니다.
            </p>
          </div>
        </div>
      </div>
        <div v-else class="text-center py-5">투표 정보를 불러오지 못했습니다.</div>
    </div>

    <!-- ▣ 댓글 목록 -->
    <section v-if="comments.length">
      <h5 class="mb-3"><i class="fas fa-comments"></i> 댓글 ({{ comments.length }})</h5>

      <div v-for="c in comments" :key="c.commentUID" class="border-bottom pb-3 mb-4">
        <!-- 원댓글 -->
        <div class="d-flex justify-content-between">
          <div>
            <strong>{{ c.username }}</strong>
            <small class="text-muted ms-2">{{ formatDate(c.createdAt) }}</small>
          </div>
        </div>
        <div style="white-space:pre-wrap" class="mt-2">{{ c.content }}</div>

        <!-- 답글 리스트 -->
        <div v-if="replies[c.commentUID] && replies[c.commentUID].length" class="mt-3 ms-3">
          <div v-for="rp in replies[c.commentUID]" :key="rp.replyUID" class="border-start ps-3 mb-2 text-muted small">
            <strong>{{ rp.username }}</strong> · {{ formatDate(rp.createdAt) }}<br />
            <span style="white-space:pre-wrap">{{ rp.content }}</span>
          </div>
        </div>

        <!-- 답글 작성 토글 -->
        <button class="btn btn-sm btn-outline-primary mt-2" @click="toggleReply(c.commentUID)">답글 달기</button>

        <!-- 답글 입력창 -->
        <div v-show="replyFormOpen[c.commentUID]" class="mt-2">
          <textarea v-model="replyInputs[c.commentUID]" rows="2" class="form-control mb-2" placeholder="답글 작성..."></textarea>
          <button class="btn btn-sm btn-primary" @click="submitReply(c.commentUID)">답글 작성</button>
        </div>
      </div>
    </section>

    <!-- ▣ 댓글 작성 (항상 맨 아래) -->
    <div class="card shadow-sm p-3 mt-4">
      <div class="card-body">
        <h5 class="mb-3"><i class="fas fa-pencil-alt"></i> 댓글 작성</h5>
        <div class="mb-3">
          <label class="form-label">이름</label>
          <input type="text" class="form-control" :value="currentUsername" readonly/>
        </div>
        <textarea v-model="commentInput" rows="4" class="form-control mb-3" placeholder="댓글을 입력하세요..."></textarea>
        <button class="btn btn-success" @click="submitComment">작성하기</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted ,computed } from 'vue'
import { useRoute } from 'vue-router'
import api from '@/utils/api'
import { useUserStore } from '@/stores/user'
// ─── 상태 ─────────────────────────────────────────────────
const route           = useRoute()
const vote            = ref(null)
const voteCounts      = ref({})
const selectedOption  = ref('')
const pastel          = ['#5AC8FA','#FF9F40','#4CD964','#FF5E7E','#AF7CFF','#FFD460']
const badge           = ref('')
const selectedIndex   = ref(null)
const totalVotes      = ref(0)
const isLoggedIn = ref(false)
const comments        = ref([])
const replies         = ref({})
const commentInput    = ref('')
const replyInputs     = reactive({})
const replyFormOpen   = reactive({})
const currentUsername = computed(() => store.username)
const store = useUserStore()

// ─── 유틸 ────────────────────────────────────────────────
onMounted(() => {
  const token = localStorage.getItem('token')
  isLoggedIn.value = !!token
  fetchVote()
})
function formatDate(dt){
  const d = new Date(dt);const m=d.getMonth()+1;const day=d.getDate();let h=d.getHours();const mi=d.getMinutes();const p=h>=12?'오후':'오전';h=h%12||12;return `${m}. ${day}. ${p} ${h}:${mi.toString().padStart(2,'0')}`
}
function getPercentage(c){ return totalVotes.value?Math.round(c/totalVotes.value*100):0 }
function selectOption(idx){ selectedIndex.value = idx }

async function fetchVote(){
  const id = route.query.id

  const [detail,status] = await Promise.all([
    api.get(`/vote/detail?id=${id}`),
    api.get(`/get/ticket/vote?id=${id}`)
  ])
  vote.value = {
    ...detail.data.data,
    Owner: detail.data.Owner
  }

  voteCounts.value = status.data?.selectOption || {}
  totalVotes.value = Object.values(voteCounts.value).reduce((a,b)=>a+b,0)
  // badge.value = '🥇'
  comments.value = detail.data.comment || []
  replies.value  = detail.data.replies || {}
}

async function submitVote(){
  if(selectedIndex.value === null) return alert('투표 항목을 선택해주세요.')
  const selectedOption = vote.value.voteType[selectedIndex.value]
  await api.post('/save/ticket/vote', {
    feedUID: route.query.id,
    title: vote.value.title,
    selectedOption
  })
  alert('투표 완료!'); location.reload()
}

async function submitComment(){
  if(!commentInput.value.trim()) return alert('댓글 내용을 입력하세요.')
  await api.post(`/search/view/vote/comment/id?feedUID=${route.query.id}`,{
    username: currentUsername.value,
    content:commentInput.value.trim() })
  commentInput.value = ''
  fetchVote()
}

function toggleReply(cid){ replyFormOpen[cid] = !replyFormOpen[cid] }
async function submitReply(cid){
  const text = replyInputs[cid]?.trim(); if(!text) return alert('답글 내용을 입력하세요.')
  await api.post('/search/view/vote/reply/save',{ commentUID:cid, feedUID:route.query.id,
    username:currentUsername.value,
    content:text })
  replyInputs[cid] = ''
  fetchVote()
}
async function deleteVote(){
  if (!confirm('\uacb0\uc815\ud558\uc2dc\uaca0\uc5b4\uc694?')) return
  const token = localStorage.getItem('token')
  const res = await fetch('http://localhost:8080/api/search/view/vote/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify({ id: vote.value.feedUID })
  })
  const result = await res.json()
  if (res.ok) {
    alert('\ud22c\ud45c\uac8c\uc2dc\uac00 \uc0ad\uc81c\ub418\uc5c8\uc2b5\ub2c8\ub2e4.')
    router.push('/')
  } else {
    alert(result.error || '\uc0ad\uc81c \uc2e4\ud328')
  }
}

</script>

<style scoped>
@import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css');
@import url('https://cdn.jsdelivr.net/npm/bootstrap@5.1.0/dist/css/bootstrap.min.css');
#poll-title {
  scroll-margin-top: 100px;
}

.poll-card{background:#fff;border-radius:18px;padding:24px;box-shadow:0 2px 6px rgba(0,0,0,.05);} .poll-title{font-size:1.4rem;font-weight:700;word-break:keep-all;} .poll-choice{border:1px solid #e3e6ea;border-radius:14px;padding:14px 18px;font-weight:600;background:#fafbfc;cursor:pointer;transition:.25s;} .poll-choice:hover{background:#e9f3ff;border-color:#76a9ff;} .poll-choice.active{background:#2d8cff;color:#fff;border-color:#2d8cff;} .poll-bar-wrapper{background:#edeff1;height:38px;border-radius:14px;overflow:hidden;position:relative;} .poll-bar-fill{height:100%;background:#2d8cff;position:relative;} .poll-bar-text{position:absolute;left:12px;top:50%;transform:translateY(-50%);font-weight:600;color:#fff;} .btn-kakao{background:#fae100;color:#000;font-weight:700;border:none;}
</style>
