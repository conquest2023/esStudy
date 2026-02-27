<template>
  <div class="poll-detail-page container pt-navbar my-4">
    <div class="modern-post-card shadow-sm mb-4">
      <div v-if="vote" class="card-body">

        <header class="post-header mb-4">
          <div class="d-flex justify-content-between align-items-start mb-2">
            <h1 class="post-title">{{ vote.title }}</h1>

            <button v-if="vote.Owner" class="btn-delete-soft ms-3 flex-shrink-0" @click="deleteVote">
              <i class="fas fa-trash-alt"></i> 삭제
            </button>
          </div>

          <div class="post-meta-row">
            <RouterLink :to="`/user/profile/${vote.username}`" class="author-link">
              <span class="badge-rank me-1">{{ badge }}</span>
              <span class="fw-bold">{{ vote.username }}</span>
            </RouterLink>
            <span class="meta-dot">·</span>
            <span class="meta-text">{{ formatDate(vote.createdAt) }}</span>
            <span class="meta-dot">·</span>
            <span class="meta-text"><i class="fas fa-users me-1"></i> {{ totalVotes }}명 참여</span>
          </div>
        </header>

        <p class="post-content mb-4" v-if="vote.description" v-html="linkify(vote.description)"></p>

        <div class="poll-section bg-light-soft p-4 rounded-4 mb-2">
          <h6 class="poll-section-title mb-3 d-flex align-items-center justify-content-between">
            <span><i class="fas fa-poll-h text-primary me-2"></i>투표 항목</span>
            <span v-if="canMulti" class="badge-pill bg-primary-soft small">
              최대 {{ maxSelectCnt }}개 선택 (현재 {{ selectedCount }}개)
            </span>
          </h6>

          <div class="poll-options-list">
            <div
                v-for="(optText, idx) in vote.voteType"
                :key="idx"
                class="modern-poll-choice"
                :class="{
                'is-selected': isSelected(getOpt(idx)?.optionId),
                'is-disabled': canMulti && !isSelected(getOpt(idx)?.optionId) && selectedCount >= maxSelectCnt
              }"
                @click="toggleOption(getOpt(idx))"
            >
              <div
                  class="poll-choice-bg"
                  :style="{ width: getPercentage(voteCounts[optText] || 0) + '%', backgroundColor: pastel[idx % pastel.length] }"
              ></div>

              <div class="poll-choice-content position-relative z-1 d-flex justify-content-between align-items-center">
                <span class="d-inline-flex align-items-center gap-2 fw-semibold">
                  <i v-if="!canMulti" :class="['far text-primary', isSelected(getOpt(idx)?.optionId) ? 'fa-dot-circle' : 'fa-circle text-muted']"></i>
                  <i v-else :class="['far text-primary', isSelected(getOpt(idx)?.optionId) ? 'fa-check-square' : 'fa-square text-muted']"></i>
                  {{ optText }}
                </span>

                <span class="poll-percent fw-bold" :style="{ color: isSelected(getOpt(idx)?.optionId) ? '#2563eb' : '#64748b' }">
                  {{ getPercentage(voteCounts[optText] || 0) }}%
                </span>
              </div>
            </div>
          </div>

          <p v-if="errorMsg" class="text-danger small mt-3 mb-0 fw-semibold"><i class="fas fa-exclamation-circle me-1"></i>{{ errorMsg }}</p>

          <div class="d-flex justify-content-end mt-4">
            <template v-if="login">
              <button
                  v-if="!hasVoted"
                  class="btn-submit-glow px-5"
                  :disabled="!canSubmit"
                  @click="submitVote"
              >
                투표 제출하기
              </button>
              <div v-else class="voted-badge px-4 py-2 rounded-pill bg-white border fw-bold text-primary shadow-sm">
                <i class="fas fa-check-circle me-1"></i> 이미 참여한 투표입니다
              </div>
            </template>
            <template v-else>
              <div class="text-end">
                <button class="btn-submit-glow px-5" disabled>투표하기</button>
                <p class="text-danger small mt-2 mb-0">로그인 후 투표에 참여할 수 있습니다.</p>
              </div>
            </template>
          </div>
        </div>

        <footer class="post-actions d-flex justify-content-between align-items-center mt-4">
          <div class="comment-count-badge">
            <i class="fas fa-comment-dots me-1"></i> 댓글 {{ comments.length }}
          </div>

          <button
              v-if="feedId"
              class="like-btn-modern"
              :class="{ 'liked': isLiked('POST', feedId) }"
              @click="() => toggleLike('POST', feedId)"
          >
            <i class="fas fa-heart" :class="{ 'heart-beat': isLiked('POST', feedId) }"></i>
            <span class="ms-1">{{ likeCountOf('POST', feedId) }}</span>
          </button>
        </footer>
      </div>

      <div v-else class="text-center py-5">
        <div class="modern-spinner mb-3"></div>
        <p class="text-muted">투표 정보를 불러오는 중입니다...</p>
      </div>
    </div>

    <section class="comments-section modern-post-card shadow-sm p-4">
      <h5 class="comments-title mb-4">
        댓글 <span class="text-primary">{{ comments.length }}</span>
      </h5>

      <div v-if="comments.length === 0" class="empty-state py-5 text-center">
        <i class="far fa-comment-dots mb-2 fs-1 text-muted"></i>
        <p class="text-muted mb-0 small">아직 댓글이 없습니다.<br>의견을 남겨주세요!</p>
      </div>

      <div v-for="c in comments" :key="c.id + '-' + reloadTrigger" class="comment-item-modern d-flex">
        <div class="comment-avatar d-none d-sm-flex me-3">
          {{ (c.username || '?').charAt(0).toUpperCase() }}
        </div>

        <div class="comment-body flex-grow-1">
          <div class="d-flex justify-content-between align-items-start mb-2">
            <div class="comment-meta">
              <RouterLink :to="`/user/profile/${c.username}`" class="comment-author">
                <span class="badge-rank me-1">{{ rankBadge(c.username) }}</span>
                <span class="fw-bold">{{ c.username }}</span>
                <span v-if="c.author" class="badge-pill bg-author ms-2">글쓴이</span>
                <span v-if="c.owner" class="badge-pill bg-author ms-2">작성자</span>
              </RouterLink>
              <span class="meta-dot mx-2">·</span>
              <span class="meta-text small">
                {{ c.updatedAt || c.modifiedAt ? `수정됨 ${formatDate(c.updatedAt || c.modifiedAt)}` : formatDate(c.createdAt) }}
              </span>
            </div>

            <div class="comment-actions gap-2">
              <button v-if="c.owner" class="action-btn text-muted" @click="startEditComment(c)">수정</button>
              <button v-if="c.owner" class="action-btn text-danger" @click="delComment(c)">삭제</button>
              <button class="action-btn like-action" :class="{ 'text-danger': isLiked('COMMENT', c.id) }" @click="() => toggleLike('COMMENT', c.id)">
                <i :class="isLiked('COMMENT', c.id) ? 'fas fa-heart' : 'far fa-heart'"></i> {{ likeCountOf('COMMENT', c.id) }}
              </button>
            </div>
          </div>

          <div class="comment-content" v-html="linkify(c.content)"></div>

          <div v-if="editingCommentId === c.id" class="edit-form-wrapper mt-3">
            <textarea v-model="editTexts[c.id]" rows="2" class="modern-input mb-2" placeholder="수정할 내용을 입력하세요."></textarea>
            <div class="d-flex justify-content-end gap-2">
              <button class="btn-cancel-soft" @click="cancelEdit">취소</button>
              <button class="btn-submit-soft" :disabled="editSending" @click="updateComment(c.id)">수정 완료</button>
            </div>
          </div>

          <div class="reply-list-modern" v-if="replies && replies[c.id]">
            <div v-for="rp in replies[c.id]" :key="rp.id" class="reply-item-modern">
              <div class="d-flex justify-content-between align-items-start mb-1">
                <div class="reply-meta">
                  <RouterLink :to="`/user/profile/${rp.username}`" class="comment-author">
                    <span class="badge-rank me-1">{{ rankBadge(rp.username) }}</span>
                    <span class="fw-bold">{{ rp.username }}</span>
                    <span v-if="rp.author || rp.owner" class="badge-pill bg-author ms-2">작성자</span>
                  </RouterLink>
                  <span class="meta-dot mx-2">·</span>
                  <span class="meta-text small">
                    {{ rp.updatedAt ? `수정됨 ${formatDate(rp.updatedAt)}` : formatDate(rp.createdAt) }}
                  </span>
                </div>

                <div class="comment-actions gap-2">
                  <button v-if="rp.owner" class="action-btn text-muted" @click="startReplyEdit(rp)">수정</button>
                  <button v-if="rp.owner" class="action-btn text-danger" @click="delReply(rp)">삭제</button>
                  <button class="action-btn like-action" :class="{ 'text-danger': isLiked('REPLY', rp.id) }" @click="() => toggleLike('REPLY', rp.id)">
                    <i :class="isLiked('REPLY', rp.id) ? 'fas fa-heart' : 'far fa-heart'"></i> {{ likeCountOf('REPLY', rp.id) }}
                  </button>
                </div>
              </div>

              <div v-if="replyEditMode[rp.id]" class="edit-form-wrapper mt-2">
                <textarea v-model="replyEditTexts[rp.id]" rows="2" class="modern-input mb-2"></textarea>
                <div class="d-flex justify-content-end gap-2">
                  <button class="btn-cancel-soft" @click="cancelReplyEdit(rp.id)">취소</button>
                  <button class="btn-submit-soft" @click="updateReply(rp)">저장</button>
                </div>
              </div>
              <div v-else class="reply-content">{{ rp.content }}</div>
            </div>
          </div>

          <button class="btn-reply-toggle mt-2" @click="toggleReplyForm(c.id)">
            <i class="fas fa-reply me-1"></i> 답글 달기
          </button>

          <div v-show="activeReply === c.id" class="reply-form-wrapper mt-2">
            <textarea v-model="replyTexts[c.id]" rows="2" class="modern-input mb-2" placeholder="답글을 남겨주세요."></textarea>
            <div class="d-flex justify-content-end">
              <button class="btn-submit-soft" @click="submitReply(c.id)" :disabled="replySendingMap[c.id]">답글 등록</button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="comment-write-card mt-4 mb-5 shadow-sm">
      <div class="p-4">
        <div class="d-flex justify-content-between align-items-center mb-3">
          <h6 class="fw-bold mb-0 text-dark"><i class="bi bi-pencil-square me-2"></i>댓글 작성</h6>
          <button v-if="!login" class="btn btn-sm btn-outline-secondary rounded-pill" @click="router.push('/login')">로그인</button>
        </div>
        <textarea
            v-model="commentText"
            rows="3"
            class="modern-input mb-3"
            :placeholder="login ? '다양한 의견을 자유롭게 남겨주세요.' : '로그인 후 댓글을 작성할 수 있습니다.'"
        ></textarea>
        <div class="text-end">
          <button class="btn-submit-primary" :disabled="!login || sending || !commentText.trim()" @click="submitComment">
            댓글 등록
          </button>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter, RouterLink } from 'vue-router'
import api from '@/utils/api'
import { useUserStore } from '@/stores/user'
import { usePostDetailStore } from '@/stores/postDetail.js'

// ─── 공통 상태 ─────────────────────────────────────────────
const route      = useRoute()
const router     = useRouter()
const store      = useUserStore()
const postStore  = usePostDetailStore()

const vote            = ref(null)
const voteCounts      = ref({})
const pastel          = ['#5AC8FA','#FF9F40','#4CD964','#FF5E7E','#AF7CFF','#FFD460']
const totalVotes      = ref(0)

const isLoggedIn      = ref(false)     // 로컬 토큰 확인용
const pollOptions     = ref([])        // [{optionId, content, ...}]

// 댓글/답글/좋아요 관련
const comments        = ref([])
const replies         = ref({})
const commentText     = ref('')
const sending         = ref(false)

const activeReply     = ref(null)
const replyTexts      = ref({})
const replySendingMap = ref({})
const replyEditTexts  = ref({})
const replyEditMode   = ref({})
const likeStates      = ref({})
const reloadTrigger   = ref(0)
const editingCommentId = ref(null)
const editTexts        = ref({})
const editSending      = ref(false)

const pollId          = ref(null)
const hasVoted        = ref(false)

const errorMsg        = ref('')
const topWriters = JSON.parse(localStorage.getItem('topWriters') || '{}')

// 투표가 달려있는 원본 게시글 ID (댓글/좋아요에 사용)
const feedId = computed(() => vote.value?.postId ?? null)

// 로그인 여부 (템플릿에서 쓰는 login)
const login = computed(() => store.isLoggedIn)

// 멀티/최대 선택 수/선택 상태(optionId 기반)
const canMulti        = computed(() => !!vote.value?.multiSelect)
const maxSelectCnt    = computed(() => Number(vote.value?.maxSelectCnt) || 1)
const selectedOptionIds = ref(new Set()) // optionId보관
const selectedCount   = computed(() => selectedOptionIds.value.size)
const canSubmit       = computed(() =>
    canMulti.value ? selectedCount.value > 0 && selectedCount.value <= maxSelectCnt.value
        : selectedCount.value === 1
)

function rankBadge(name) {
  const r = topWriters[name] || 0
  return r === 1 ? '👑'
      : r === 2 ? '🥇'
          : r === 3 ? '🥈'
              : r > 0 && r <= 5 ? '🥉'
                  : ''
}
const badge = computed(() => vote.value ? rankBadge(vote.value.username) : '')

function formatDate(dateTimeString) {
  if (!dateTimeString) return ''
  const d = new Date(dateTimeString)
  const m = d.getMonth() + 1
  const day = d.getDate()
  let h = d.getHours()
  const mi = d.getMinutes()
  const p = h >= 12 ? '오후' : '오전'
  h = h % 12 || 12
  return `${m}. ${day}. ${p} ${h}:${mi.toString().padStart(2, '0')}`
}

function linkify(text = '') {
  const urlRegex = /(https?:\/\/[^\s]+)/g
  return text.replace(urlRegex, url => `<a href="${url}" target="_blank">${url}</a>`)
}

function getPercentage(c) {
  return totalVotes.value
      ? Math.round((c / totalVotes.value) * 100)
      : 0
}

// pollOptions에서 안전하게 option 객체 가져오기
const getOpt = (idx) => pollOptions.value?.[idx]

// 선택 여부/토글 (optionId 기준)
const isSelected = (optionId) => optionId ? selectedOptionIds.value.has(optionId) : false

function toggleOption(option) {
  const id = option?.optionId
  if (!id) return

  errorMsg.value = ''

  if (!canMulti.value || maxSelectCnt.value <= 1) {
    // 단일
    selectedOptionIds.value = new Set([id])
    return
  }

  if (selectedOptionIds.value.has(id)) {
    selectedOptionIds.value.delete(id)
  } else {
    if (selectedOptionIds.value.size >= maxSelectCnt.value) {
      errorMsg.value = `최대 ${maxSelectCnt.value}개까지 선택할 수 있습니다.`
      return
    }
    selectedOptionIds.value.add(id)
  }
}

// 좋아요 키/유틸
const likeKey = (targetType, targetId) => `${targetType}-${targetId}`

function ensureLikeState(targetType, targetId, initialCount = 0, initialLiked = false) {
  const key = likeKey(targetType, targetId)
  if (!likeStates.value[key]) {
    likeStates.value[key] = { liked: initialLiked, count: initialCount }
  }
}
function isLiked(targetType, targetId) {
  const key = likeKey(targetType, targetId)
  return likeStates.value[key]?.liked ?? false
}
function likeCountOf(targetType, targetId) {
  const key = likeKey(targetType, targetId)
  return likeStates.value[key]?.count ?? 0
}

onMounted(async () => {
  const token = localStorage.getItem('token')
  isLoggedIn.value = !!token || store.isLoggedIn
  const postIdParam = route.params.id || route.query.id
  let cached = postStore.getByPostId(postIdParam)

  if (!cached) {
    try {
      const { data } = await api.get(`/post/${postIdParam}`)
      const ok   = data?.ok ?? {}
      const post = ok.post
      const poll = ok.poll
      if (!post || !poll) {
        console.error('post/poll 데이터 없음', ok)
        return
      }
      cached = { post, poll }
    } catch (e) {
      console.error('투표 상세 로드 실패', e)
      return
    }
  }

  const { post, poll } = cached
  buildVoteFromPostAndPoll(post, poll)

  if (token && pollId.value) {
    await checkAlreadyVoted(token)
  }
  if (feedId.value) {
    await Promise.all([
      loadComments(feedId.value),
      loadReplies(feedId.value),
      loadLikeCounts(feedId.value),
      loadLikeDetail(feedId.value),
    ])
  }
})

// post + poll → vote 세팅/집계
function buildVoteFromPostAndPoll(post, poll) {
  pollId.value      = poll.pollId
  pollOptions.value = Array.isArray(poll.options) ? poll.options : []

  vote.value = {
    title:        post.title,
    description:  post.description,
    username:     post.username,
    createdAt:    post.createdAt,
    Owner:        post.owner,
    voteType:     pollOptions.value.map(o => o.content), // 화면에 보여줄 텍스트
    multiSelect:  poll.multiSelect,
    maxSelectCnt: poll.maxSelectCnt,
    postId:       poll.postId,
  }

  const optionMap = new Map()
  const counts    = {}
  pollOptions.value.forEach(o => {
    optionMap.set(o.optionId, o.content)
    counts[o.content] = 0
  })
  const votes = Array.isArray(poll.votes) ? poll.votes : []
  votes.forEach(v => {
    const text = optionMap.get(v.optionId)
    if (!text) return
    counts[text] = (counts[text] || 0) + 1
  })
  voteCounts.value = counts
  totalVotes.value = votes.length
}

async function checkAlreadyVoted(token) {
  try {
    const { data } = await api.get(`/poll/check/${pollId.value}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    hasVoted.value = !!data?.check
  } catch (e) {
    console.error('투표 여부 확인 실패', e)
  }
}

// 선택지 집계 (기존 API 쓰는 경우 - 필요시 사용)
async function fetchVoteCounts() {
  const id = route.query.id || route.params.id
  try {
    const { data } = await api.get(`/get/ticket/vote?id=${id}`)
    const countsObj = data?.selectOption || data?.data?.selectOption || {}
    voteCounts.value = countsObj
    totalVotes.value = Object.values(countsObj).reduce((a, b) => a + b, 0)
  } catch (e) {
    console.error('vote count 로드 실패', e)
  }
}

async function submitVote() {
  if (!vote.value) return
  const token = localStorage.getItem('token')
  if (!token || !login.value) { alert('로그인이 필요합니다.'); router.push('/login'); return }

  const optionIds = Array.from(selectedOptionIds.value)
  if (optionIds.length === 0) { alert('항목을 선택해주세요.'); return }
  if (canMulti.value && optionIds.length > maxSelectCnt.value) {
    alert(`최대 ${maxSelectCnt.value}개까지 선택할 수 있습니다.`)
    return
  }

  try {
    const postIdParam = route.params.id || route.query.id
    if (canMulti.value && maxSelectCnt.value > 1) {
      await api.post('/votes', { pollId: pollId.value, optionIds }, {
        headers: { Authorization: `Bearer ${token}` }
      })
    } else {
      await api.post('/vote', { pollId: pollId.value, optionId: optionIds[0] }, {
        headers: { Authorization: `Bearer ${token}` }
      })
    }
    alert('투표 완료!')
    hasVoted.value = true
    window.location.href = '/poll/'+postIdParam
  } catch (e) {
    console.error(e)
    alert('투표 중 오류 발생')
  }
}

// ─── 댓글 ────────────────────────────────────────────
async function loadComments(postId) {
  const { data } = await api.get('/comments', { params: { postId } })
  comments.value =
      (Array.isArray(data?.ok) && data.ok) ||
      (Array.isArray(data?.comments) && data.comments) ||
      (Array.isArray(data?.data?.comments) && data.data.comments) ||
      []
}

async function submitComment() {
  if (sending.value) return
  if (!commentText.value.trim()) {
    alert('댓글 내용을 입력하세요!')
    return
  }
  const token = localStorage.getItem('token')
  if (!token || !login.value) { alert('로그인이 필요합니다.'); router.push('/login'); return }

  sending.value = true
  try {
    await api.post('/comment', {
      content: commentText.value,
      username: store.username,
      postId: feedId.value,
    }, {
      headers: { Authorization: `Bearer ${token}` }
    })
    await loadComments(feedId.value)
    commentText.value = ''
  } catch (e) {
    console.error(e)
    alert('댓글 저장 중 오류 발생')
  } finally {
    sending.value = false
  }
}

async function delComment(c) {
  if (!confirm('댓글을 삭제하시겠습니까?')) return
  const token = localStorage.getItem('token')
  if (!token) { alert('로그인이 필요합니다.'); router.push('/login'); return }
  try {
    await api.delete(`/comment/${c.id}`, { headers: { Authorization: `Bearer ${token}` } })
    comments.value = comments.value.filter(v => v.id !== c.id)
  } catch (e) {
    console.error(e)
    alert('댓글 삭제 중 오류 발생')
  }
}

function startEditComment(c) {
  editingCommentId.value = c.id
  editTexts.value[c.id] = c.content
}
function cancelEdit() {
  if (editingCommentId.value != null) editTexts.value[editingCommentId.value] = ''
  editingCommentId.value = null
}
async function updateComment(commentId) {
  const text = editTexts.value[commentId] || ''
  if (!text.trim()) { alert('수정할 내용을 입력하세요.'); return }
  const token = localStorage.getItem('token')
  if (!token) { alert('로그인이 필요합니다.'); router.push('/login'); return }

  if (editSending.value) return
  editSending.value = true
  try {
    const { data } = await api.put(`/comment/${commentId}`, { content: text }, {
      headers: { Authorization: `Bearer ${token}` }
    })
    const updated = data?.comment ?? data ?? {}
    comments.value = comments.value.map(c =>
        c.id === commentId
            ? { ...c, content: updated.content ?? text, modifiedAt: updated.modifiedAt ?? updated.updatedAt ?? c.modifiedAt }
            : c
    )
    editingCommentId.value = null
  } catch (e) {
    console.error(e)
    alert('댓글 수정 중 오류 발생')
  } finally {
    editSending.value = false
  }
}

// ─── 대댓글 ──────────────────────────────────────────
async function loadReplies(postId) {
  const { data } = await api.get('/replys', { params: { postId } })
  const list =
      (Array.isArray(data?.ok) && data.ok) ||
      (Array.isArray(data?.replies) && data.replies) ||
      (Array.isArray(data?.data) && data.data) ||
      []

  const grouped = list.reduce((acc, r) => {
    const key = r.commentId ?? r.commentUID ?? r.comment_id
    if (!key) return acc
        ;(acc[key] ||= []).push({
      id:        r.id,
      username:  r.username,
      content:   r.content,
      createdAt: r.createdAt,
      updatedAt: r.updatedAt ?? null,
      owner:     r.owner,
      author:    r.author
    })
    return acc
  }, {})
  replies.value = grouped
}

function toggleReplyForm(commentId) {
  activeReply.value = activeReply.value === commentId ? null : commentId
}

async function submitReply(commentId) {
  const text = replyTexts.value[commentId] || ''
  if (!login.value) { router.push('/login'); return }
  if (!text.trim()) { alert('답글 내용을 입력하세요.'); return }
  if (replySendingMap.value[commentId]) return
  replySendingMap.value[commentId] = true

  const token = localStorage.getItem('token')
  try {
    await api.post('/reply', {
      commentId,
      postId: feedId.value,
      username: store.username,
      content: text,
    }, {
      headers: { Authorization: `Bearer ${token}` }
    })

    await Promise.all([
      loadComments(feedId.value),
      loadReplies(feedId.value),
    ])

    activeReply.value = null
    replyTexts.value[commentId] = ''
    reloadTrigger.value++
  } catch (e) {
    console.error(e)
    alert('답글 저장 실패')
  } finally {
    replySendingMap.value[commentId] = false
  }
}

function startReplyEdit(rp) {
  replyEditTexts.value[rp.id] = rp.content
  replyEditMode.value[rp.id] = true
}
function cancelReplyEdit(id) {
  replyEditMode.value[id] = false
}
async function updateReply(rp) {
  const text = (replyEditTexts.value[rp.id] || '').trim()
  if (!text) { alert('답글 내용을 입력하세요.'); return }
  const token = localStorage.getItem('token')
  if (!token) { alert('로그인이 필요합니다.'); router.push('/login'); return }

  try {
    const { data } = await api.put(`/reply/${rp.id}`, { content: text }, {
      headers: { Authorization: `Bearer ${token}` }
    })
    const updated = data?.reply ?? {}
    const commentId = Object.keys(replies.value).find(cid => replies.value[cid].some(x => x.id === rp.id))
    if (commentId) {
      replies.value[commentId] = replies.value[commentId].map(x =>
          x.id === rp.id ? { ...x, content: updated.content ?? text, updatedAt: updated.updatedAt ?? new Date().toISOString() } : x
      )
    }
    replyEditMode.value[rp.id] = false
  } catch (e) {
    console.error(e)
    alert('답글 수정 실패')
  }
}
async function delReply(rp) {
  if (!confirm('답글을 삭제하시겠습니까?')) return
  const token = localStorage.getItem('token')
  if (!token) { alert('로그인이 필요합니다.'); router.push('/login'); return }
  try {
    await api.delete(`/reply/${rp.id}`, { headers: { Authorization: `Bearer ${token}` } })
    const commentId = Object.keys(replies.value).find(cid => replies.value[cid].some(x => x.id === rp.id))
    if (commentId) replies.value[commentId] = replies.value[commentId].filter(x => x.id !== rp.id)
  } catch (e) {
    console.error(e)
    alert('답글 삭제 실패')
  }
}

// ─── 좋아요 ──────────────────────────────────────────
async function toggleLike(targetType, targetId) {
  if (!login.value) { alert('로그인이 필요합니다.'); router.push('/login'); return }
  if (!feedId.value) return

  ensureLikeState(targetType, targetId)
  const key = likeKey(targetType, targetId)
  const state = likeStates.value[key]
  const prevLiked = state.liked
  const prevCount = state.count

  // 낙관적 업데이트
  state.liked = !prevLiked
  state.count = prevCount + (prevLiked ? -1 : 1)

  try {
    await api.post('/like', { postId: feedId.value, targetId, targetType })
  } catch (e) {
    console.error(e)
    state.liked = prevLiked
    state.count = prevCount
  }
}

async function loadLikeCounts(postId) {
  try {
    const { data } = await api.get(`/like/count/${postId}`)
    const list = Array.isArray(data?.likes) ? data.likes : []
    const commentArr = Array.isArray(comments.value) ? comments.value : []
    const repliesObj = replies.value && typeof replies.value === 'object' ? replies.value : {}

    const commentIdSet = new Set(commentArr.map(c => Number(c.id)))
    const replyIdSet = new Set()
    Object.values(repliesObj).forEach(arr => (arr || []).forEach(rp => replyIdSet.add(Number(rp.id))))

    list.forEach(item => {
      const targetId = Number(item.targetId ?? item.id)
      const count    = Number(item.count ?? 0)
      if (!Number.isFinite(targetId)) return

      let targetType = null
      if (feedId.value && targetId === Number(feedId.value)) targetType = 'POST'
      else if (commentIdSet.has(targetId)) targetType = 'COMMENT'
      else if (replyIdSet.has(targetId)) targetType = 'REPLY'

      const key  = likeKey(targetType, targetId)
      const prev = likeStates.value[key] || { liked: false, count: 0 }
      likeStates.value[key] = { liked: prev.liked, count }
    })
  } catch (e) {
    console.error('like count 로드 실패', e)
  }
}

async function loadLikeDetail(postId) {
  const token = localStorage.getItem('token')
  if (!token) return
  try {
    const { data } = await api.get(`/like/detail/${postId}`, { headers: { Authorization: `Bearer ${token}` } })
    const list = Array.isArray(data?.likes) ? data.likes : []
    list.forEach(item => {
      const targetType = item.targetType ?? item.target_type ?? item.type
      const targetId   = Number(item.targetId ?? item.target_id ?? item.id)
      const liked      = Boolean(item.isOwner ?? item.owner ?? item.liked)
      if (!targetType || !targetId) return
      const key  = likeKey(targetType, targetId)
      const prev = likeStates.value[key] || { liked: false, count: 0 }
      likeStates.value[key] = { liked, count: prev.count }
    })
  } catch (e) {
    console.error('like detail 로드 실패', e)
  }
}

// ─── 투표 글 삭제 ─────────────────────────────────────────
async function deleteVote() {
  if (!confirm('정말 삭제하시겠습니까?')) return
  if (!vote.value) return
  try {
    const token = localStorage.getItem('token')
    await api.delete(`/post/${vote.value.postId}`, {
      headers: token ? {Authorization: `Bearer ${token}`} : {}
    })
    router.push('/')
  } catch (e) {
    console.error(e)
    alert('삭제 중 오류 발생')
  }
}
</script>

<style scoped>
@import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css');
@import url('https://cdn.jsdelivr.net/npm/bootstrap@5.1.0/dist/css/bootstrap.min.css');

.pt-navbar { padding-top: 80px; }
.poll-detail-page { max-width: 860px; }

.modern-post-card {
  background: #ffffff;
  border-radius: 24px;
  border: 1px solid #f1f5f9;
  padding: 1.5rem;
}

/* ===============================
   헤더 및 메타 (PostDetail 동일)
================================= */
.post-header { border-bottom: 1px solid #f1f5f9; padding-bottom: 1.5rem; }
.post-title { font-size: 1.8rem; font-weight: 800; color: #0f172a; letter-spacing: -0.02em; line-height: 1.35; }
.post-meta-row { display: flex; align-items: center; flex-wrap: wrap; gap: 4px; font-size: 0.95rem; }
.author-link { color: #334155; text-decoration: none; }
.author-link:hover { color: #2563eb; }
.meta-dot { color: #cbd5e1; margin: 0 6px; }
.meta-text { color: #64748b; }

.btn-delete-soft {
  background: #fee2e2; color: #ef4444; border: none; padding: 6px 12px;
  border-radius: 8px; font-size: 0.85rem; font-weight: 600; transition: all 0.2s;
}
.btn-delete-soft:hover { background: #fecaca; }

.post-content { font-size: 1.05rem; line-height: 1.7; color: #334155; word-break: keep-all; white-space: pre-wrap; }
:deep(.post-content a) { color: #2563eb; text-decoration: underline; }

/* ===============================
   투표 영역 특화 스타일 (YouTube/Twitter 느낌)
================================= */
.bg-light-soft { background: #f8fafc; border: 1px solid #e2e8f0; }
.poll-section-title { font-weight: 800; color: #0f172a; }
.bg-primary-soft { background: #eff6ff; color: #2563eb; padding: 4px 10px; border-radius: 12px; font-weight: 600; }

.poll-options-list { display: flex; flex-direction: column; gap: 12px; }

/* 투표 선택지 박스 */
.modern-poll-choice {
  position: relative;
  background: #ffffff;
  border: 1px solid #cbd5e1;
  border-radius: 12px;
  padding: 16px 20px;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}
.modern-poll-choice:hover:not(.is-disabled) { border-color: #94a3b8; transform: translateY(-1px); }

/* 활성화(선택됨) 상태 */
.modern-poll-choice.is-selected {
  border: 2px solid #2563eb;
  padding: 15px 19px; /* 테두리 1px 추가된 만큼 보정 */
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.1);
}

/* 비활성화 상태 (다중선택 제한 초과 시) */
.modern-poll-choice.is-disabled {
  opacity: 0.5; cursor: not-allowed; background: #f1f5f9;
}

/* 배경 퍼센티지 바 */
.poll-choice-bg {
  position: absolute; top: 0; left: 0; bottom: 0;
  opacity: 0.15; /* 부드러운 배경색 톤 다운 */
  transition: width 0.6s cubic-bezier(0.34, 1.56, 0.64, 1);
  border-radius: 0 8px 8px 0;
}
.modern-poll-choice.is-selected .poll-choice-bg { opacity: 0.25; } /* 선택한 건 살짝 더 진하게 */

/* 투표 텍스트 & 아이콘 */
.poll-choice-content { font-size: 1.05rem; color: #334155; }
.poll-percent { font-size: 1.1rem; }

/* 투표 제출 버튼 */
.btn-submit-glow {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: #fff; border: none; padding: 12px 24px; border-radius: 12px;
  font-size: 1.05rem; font-weight: 700; cursor: pointer;
  box-shadow: 0 6px 16px rgba(37, 99, 235, 0.3);
  transition: all 0.2s cubic-bezier(0.34, 1.56, 0.64, 1);
}
.btn-submit-glow:hover:not(:disabled) { transform: translateY(-2px); box-shadow: 0 10px 20px rgba(37, 99, 235, 0.4); }
.btn-submit-glow:disabled { background: #cbd5e1; box-shadow: none; cursor: not-allowed; color: #64748b; }

/* ===============================
   좋아요 및 댓글 (PostDetail 동일)
================================= */
.post-actions { border-top: 1px solid #f1f5f9; padding-top: 1.5rem; }
.comment-count-badge { font-weight: 600; color: #64748b; font-size: 0.95rem; }

.like-btn-modern {
  background: #f1f5f9; color: #475569; border: none; padding: 8px 18px;
  border-radius: 999px; font-size: 0.95rem; font-weight: 600;
  display: flex; align-items: center; transition: all 0.25s ease;
}
.like-btn-modern:hover { background: #e2e8f0; }
.like-btn-modern.liked { background: #fee2e2; color: #ef4444; }
.heart-beat { animation: pop 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275); }
@keyframes pop { 0% { transform: scale(1); } 50% { transform: scale(1.3); } 100% { transform: scale(1); } }

/* ===============================
   댓글 섹션
================================= */
.comments-title { font-weight: 700; color: #0f172a; }
.comment-item-modern { padding: 1.25rem 0; border-bottom: 1px solid #f1f5f9; }
.comment-item-modern:last-child { border-bottom: none; }

.comment-avatar {
  width: 40px; height: 40px; border-radius: 50%; background: linear-gradient(135deg, #e2e8f0, #cbd5e1);
  color: #475569; font-weight: 800; font-size: 1.1rem;
  display: flex; align-items: center; justify-content: center;
}

.comment-author { color: #0f172a; text-decoration: none; }
.comment-author:hover { color: #2563eb; }
.badge-pill.bg-author { background: #eff6ff; color: #2563eb; padding: 2px 8px; border-radius: 12px; font-size: 0.7rem; font-weight: 700; }

.comment-actions { display: flex; align-items: center; }
.action-btn { background: none; border: none; font-size: 0.8rem; font-weight: 600; padding: 0; transition: color 0.2s; }
.action-btn:hover { text-decoration: underline; }
.like-action { color: #94a3b8; }

.comment-content { font-size: 0.95rem; color: #334155; line-height: 1.6; margin-top: 4px; }
:deep(.comment-content a) { color: #2563eb; text-decoration: underline; }

/* 대댓글 (Thread) */
.reply-list-modern { margin-top: 1rem; padding-left: 1rem; border-left: 2px solid #e2e8f0; }
.reply-item-modern { margin-bottom: 1rem; }
.reply-item-modern:last-child { margin-bottom: 0; }
.reply-content { font-size: 0.9rem; color: #475569; line-height: 1.5; }

.btn-reply-toggle { background: none; border: none; font-size: 0.8rem; font-weight: 600; color: #64748b; padding: 0; margin-top: 8px; }
.btn-reply-toggle:hover { color: #2563eb; }

/* ===============================
   입력 폼 (모던 텍스트 에어리어)
================================= */
.comment-write-card { background: #ffffff; border-radius: 16px; border: 1px solid #f1f5f9; }
.modern-input {
  width: 100%; background: #f8fafc; border: 1px solid transparent; border-radius: 12px;
  padding: 12px 16px; font-size: 0.95rem; color: #0f172a; resize: none; transition: all 0.2s ease;
}
.modern-input:focus { outline: none; background: #ffffff; border-color: #bfdbfe; box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1); }
.modern-input::placeholder { color: #94a3b8; }

.btn-submit-primary {
  background: #2563eb; color: #ffffff; border: none; padding: 10px 24px; border-radius: 12px; font-weight: 600; transition: background 0.2s;
}
.btn-submit-primary:hover:not(:disabled) { background: #1d4ed8; }
.btn-submit-primary:disabled { background: #94a3b8; cursor: not-allowed; }

.btn-submit-soft { background: #eff6ff; color: #2563eb; border: none; padding: 6px 16px; border-radius: 8px; font-weight: 600; font-size: 0.85rem; }
.btn-submit-soft:hover { background: #dbeafe; }
.btn-cancel-soft { background: #f1f5f9; color: #64748b; border: none; padding: 6px 16px; border-radius: 8px; font-weight: 600; font-size: 0.85rem; }
.btn-cancel-soft:hover { background: #e2e8f0; color: #334155; }

/* 스피너 */
.modern-spinner { width: 40px; height: 40px; border: 3px solid #f3f3f3; border-top: 3px solid #2563eb; border-radius: 50%; animation: spin 1s linear infinite; margin: 0 auto; }
@keyframes spin { 100% { transform: rotate(360deg); } }

/* 반응형 */
@media (max-width: 576px) {
  .post-title { font-size: 1.5rem; }
  .post-content { font-size: 1rem; }
  .modern-post-card { padding: 1.25rem; border-radius: 16px; }
  .poll-section { padding: 1.25rem; }
}
</style>
