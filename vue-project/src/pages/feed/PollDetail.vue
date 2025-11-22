<template>
  <div class="poll-page container my-4">
    <div class="poll-card mb-5">
      <div v-if="vote">
        <div class="poll-title mb-2" id="poll-title">{{ vote.title }}</div>
        <div class="text-muted mb-3">
          {{ badge }} {{ vote.username }} · {{ formatDate(vote.createdAt) }}
          <button v-if="vote.Owner" class="btn btn-sm btn-danger ms-2" @click="deleteVote">삭제</button>
        </div>
        <p class="mb-3" v-if="vote.description" style="white-space:pre-wrap">{{ vote.description }}</p>

        <!-- 투표 영역 -->
        <h6 class="fw-bold d-flex align-items-center gap-2">
          투표
          <small v-if="canMulti" class="text-muted">
            (최대 {{ maxSelectCnt }}개 선택 가능 · 현재 {{ selectedCount }}개)
          </small>
        </h6>

        <div v-for="(optText, idx) in vote.voteType" :key="idx" class="mb-3">
          <div
              class="poll-choice d-flex justify-content-between align-items-center"
              :class="{
              active: isSelected(getOpt(idx)?.optionId),
              'disabled-choice': canMulti && !isSelected(getOpt(idx)?.optionId) && selectedCount >= maxSelectCnt
            }"
              @click="toggleOption(getOpt(idx))"
          >
            <span class="d-inline-flex align-items-center gap-2">
              <i v-if="!canMulti" :class="['far', isSelected(getOpt(idx)?.optionId) ? 'fa-dot-circle' : 'fa-circle']"></i>
              <i v-else :class="['far', isSelected(getOpt(idx)?.optionId) ? 'fa-check-square' : 'fa-square']"></i>
              {{ optText }}
            </span>
            <i class="far fa-circle"></i>
          </div>

          <div class="poll-bar-wrapper mt-1">
            <div
                class="poll-bar-fill"
                :style="{ width: getPercentage(voteCounts[optText] || 0) + '%', background: pastel[idx % pastel.length] }"
            >
              <span class="poll-bar-text">
                {{ getPercentage(voteCounts[optText] || 0) }}%
              </span>
            </div>
          </div>
        </div>

        <p v-if="errorMsg" class="text-danger small mt-2">{{ errorMsg }}</p>

        <div class="d-flex justify-content-between align-items-center mt-4">
          <small class="text-muted">👥 {{ totalVotes }}명 참여</small>
          <div class="text-end">
            <template v-if="login">
              <button
                  v-if="!hasVoted"
                  class="btn btn-kakao"
                  :disabled="!canSubmit"
                  @click="submitVote"
              >
                투표하기
              </button>
              <p v-else class="text-muted small mt-2 mb-0">이미 투표하신 설문입니다.</p>
            </template>
            <template v-else>
              <button class="btn btn-kakao" disabled title="로그인이 필요합니다">투표하기</button>
              <p class="text-danger small mt-2 mb-0">※ 로그인 후 투표하실 수 있습니다.</p>
            </template>
          </div>
        </div>
      </div>
      <div v-else class="text-center py-5">투표 정보를 불러오지 못했습니다.</div>
    </div>

    <!-- 댓글/답글/좋아요 영역 -->
    <section class="comments-section mt-4">
      <div class="d-flex justify-content-between align-items-center mb-2">
        <h5 class="mb-0">
          <i class="bi bi-chat"></i>
          댓글 <span class="text-muted small">({{ comments.length }})</span>
        </h5>
      </div>

      <div v-if="comments.length === 0" class="text-muted py-3 small">
        아직 댓글이 없습니다. 첫 번째 댓글을 남겨보세요.
      </div>

      <div
          v-for="c in comments"
          :key="c.id + '-' + reloadTrigger"
          class="comment-item d-flex"
      >
        <!-- 아바타 -->
        <div class="comment-avatar d-none d-sm-flex">
          <span>{{ (c.username || '?').charAt(0).toUpperCase() }}</span>
        </div>

        <!-- 본문 -->
        <div class="comment-body flex-grow-1">
          <div class="d-flex justify-content-between align-items-start">
            <div class="comment-meta">
              <RouterLink
                  :to="`/user/profile/${c.username}`"
                  class="comment-author text-decoration-none">
                <span class="badge-rank me-1">{{ rankBadge(c.username) }}</span>
                <span class="fw-semibold">{{ c.username }}</span>
                <span v-if="c.author" class="badge-author ms-1">글쓴이</span>
                <span v-if="c.owner" class="badge-author ms-1">작성자</span>
              </RouterLink>

              <small class="ms-2 text-muted">
                <template v-if="c.updatedAt || c.modifiedAt">
                  (수정됨 · {{ formatDate(c.updatedAt || c.modifiedAt) }})
                </template>
                <template v-else>
                  {{ formatDate(c.createdAt) }}
                </template>
              </small>
            </div>

            <div class="ms-2 d-flex align-items-center gap-2">
              <button
                  v-if="c.owner"
                  class="btn btn-sm btn-link text-secondary p-0"
                  @click="startEditComment(c)"
              >
                수정
              </button>
              <button
                  v-if="c.owner"
                  class="btn btn-sm btn-link text-danger p-0"
                  @click="delComment(c)"
              >
                삭제
              </button>

              <!-- 댓글 좋아요 -->
              <button
                  class="btn btn-sm btn-link text-danger p-0 d-inline-flex align-items-center"
                  @click="() => toggleLike('COMMENT', c.id)"
              >
                <i
                    :class="['bi', isLiked('COMMENT', c.id) ? 'bi-heart-fill text-like' : 'bi-heart']"
                    class="me-1"
                ></i>
                <span class="small">{{ likeCountOf('COMMENT', c.id) }}</span>
              </button>
            </div>
          </div>

          <!-- 댓글 내용/수정폼 -->
          <div class="mt-1 comment-content" v-html="linkify(c.content)"></div>

          <div v-if="editingCommentId === c.id" class="mt-2">
            <textarea
                v-model="editTexts[c.id]"
                rows="2"
                class="form-control mb-2"
                placeholder="수정할 내용을 입력하세요."
            />
            <div class="d-flex gap-2">
              <button
                  class="btn btn-sm btn-primary"
                  :disabled="editSending"
                  @click="updateComment(c.id)"
              >
                수정 완료
              </button>
              <button
                  class="btn btn-sm btn-outline-secondary"
                  @click="cancelEdit"
              >
                취소
              </button>
            </div>
          </div>

          <!-- 대댓글 리스트 -->
          <div class="mt-2 reply-list" v-if="replies && replies[c.id]">
            <div v-for="rp in replies[c.id]" :key="rp.id" class="reply-item">
              <div class="d-flex justify-content-between align-items-start">
                <div class="reply-meta">
                  <RouterLink
                      :to="`/user/profile/${rp.username}`"
                      class="comment-author text-decoration-none">
                    <span class="badge-rank me-1">{{ rankBadge(rp.username) }}</span>
                    <span class="fw-semibold">{{ rp.username }}</span>
                    <span v-if="rp.author" class="badge-author ms-1">글쓴이</span>
                    <span v-if="rp.owner" class="badge-author ms-1">작성자</span>
                  </RouterLink>
                  <small class="text-muted ms-2">
                    <template v-if="rp.updatedAt">
                      (수정됨 · {{ formatDate(rp.updatedAt) }})
                    </template>
                    <template v-else>
                      {{ formatDate(rp.createdAt) }}
                    </template>
                  </small>
                </div>

                <div class="ms-2 d-flex align-items-center gap-2 small">
                  <button
                      v-if="rp.owner"
                      class="btn btn-link btn-sm text-secondary p-0 me-2"
                      @click="startReplyEdit(rp)"
                  >
                    수정
                  </button>
                  <button
                      v-if="rp.owner"
                      class="btn btn-link btn-sm text-danger p-0"
                      @click="delReply(rp)"
                  >
                    삭제
                  </button>

                  <!-- 대댓글 좋아요 -->
                  <button
                      class="btn btn-link btn-sm text-danger p-0 d-inline-flex align-items-center"
                      @click="() => toggleLike('REPLY', rp.id)"
                  >
                    <i
                        :class="['bi', isLiked('REPLY', rp.id) ? 'bi-heart-fill text-like' : 'bi-heart']"
                        class="me-1"
                    ></i>
                    <span class="small">{{ likeCountOf('REPLY', rp.id) }}</span>
                  </button>
                </div>
              </div>

              <!-- 대댓글 내용/수정 폼 -->
              <div v-if="replyEditMode[rp.id]" class="mt-2">
                <textarea
                    v-model="replyEditTexts[rp.id]"
                    rows="2"
                    class="form-control mb-2"
                />
                <div class="d-flex gap-2">
                  <button class="btn btn-sm btn-primary" @click="updateReply(rp)">
                    저장
                  </button>
                  <button class="btn btn-sm btn-outline-secondary" @click="cancelReplyEdit(rp.id)">
                    취소
                  </button>
                </div>
              </div>
              <div v-else class="reply-content">
                {{ rp.content }}
              </div>
            </div>
          </div>

          <!-- 대댓글 작성 -->
          <button
              class="btn btn-sm btn-outline-primary mt-2"
              @click="toggleReplyForm(c.id)"
          >
            답글 달기
          </button>
          <div v-show="activeReply === c.id" class="mt-2">
            <textarea
                v-model="replyTexts[c.id]"
                rows="2"
                class="form-control mb-2"
                placeholder="답글 입력"
            />
            <button
                class="btn btn-sm btn-primary"
                @click="submitReply(c.id)"
                :disabled="replySendingMap[c.id]"
            >
              답글 작성
            </button>
          </div>
        </div>
      </div>
    </section>

    <!-- 댓글 작성 -->
    <section class="card shadow-sm p-3 mt-4 comment-write-card">
      <div class="d-flex justify-content-between align-items-center mb-2">
        <h6 class="mb-0">
          <i class="bi bi-pencil-square me-1"></i> 댓글 쓰기
        </h6>
        <button v-if="!login" class="btn btn-sm btn-outline-secondary" @click="router.push('/login')">
          로그인
        </button>
      </div>
      <textarea
          v-model="commentText"
          rows="3"
          class="form-control mb-2"
          :placeholder="login ? '내용을 입력하세요.' : '로그인 후 이용 가능합니다.'"
      />
      <div class="text-end">
        <button
            class="btn btn-success px-4"
            :disabled="!login || sending || !commentText.trim()"
            @click="submitComment"
        >
          작성하기
        </button>
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

// 선택지 집계 (기존 API 쓰는 경우)
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

// 제출 (단일은 /vote, 멀티는 /votes)
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
    if (canMulti.value && maxSelectCnt.value > 1) {
      // 멀티
      await api.post('/votes', { pollId: pollId.value, optionIds }, {
        headers: { Authorization: `Bearer ${token}` }
      })
    } else {
      // 단일
      await api.post('/vote', { pollId: pollId.value, optionId: optionIds[0] }, {
        headers: { Authorization: `Bearer ${token}` }
      })
    }
    alert('투표 완료!')
    hasVoted.value = true
    // 필요 시 즉시 집계 갱신
    // await fetchVoteCounts()
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
      username: store.username,   // 서버가 토큰에서 꺼내면 제거 가능
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
      if (commentIdSet.has(targetId)) targetType = 'COMMENT'
      else if (replyIdSet.has(targetId)) targetType = 'REPLY'
      else if (feedId.value && targetId === Number(feedId.value)) targetType = 'POST'

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

#poll-title { scroll-margin-top: 100px; }
.poll-page { padding-top: 90px; }

.poll-card{background:#fff;border-radius:18px;padding:24px;box-shadow:0 2px 6px rgba(0,0,0,.05);}
.poll-title{font-size:1.4rem;font-weight:700;word-break:keep-all;}
.poll-choice{border:1px solid #e3e6ea;border-radius:14px;padding:14px 18px;font-weight:600;background:#fafbfc;cursor:pointer;transition:.25s;}
.poll-choice:hover{background:#e9f3ff;border-color:#76a9ff;}
.poll-choice.active{background:#2d8cff;color:#fff;border-color:#2d8cff;}
.poll-bar-wrapper{background:#edeff1;height:38px;border-radius:14px;overflow:hidden;position:relative;}
.poll-bar-fill{height:100%;background:#2d8cff;position:relative;}
.poll-bar-text{position:absolute;left:12px;top:50%;transform:translateY(-50%);font-weight:600;color:#fff;}
.btn-kakao{background:#fae100;color:#000;font-weight:700;border:none;}
.disabled-choice{opacity:.6;pointer-events:none;}

.pt-navbar { padding-top: 60px; }
.badge-author{display:inline-flex;align-items:center;padding:0 6px;height:18px;font-size:.75rem;border-radius:999px;background:#fee2e2;color:#b91c1c;font-weight:600;}
.post-detail-page{max-width:900px;}
.action-fab{bottom:80px;right:24px;z-index:1051;}
.action-fab .btn{width:56px;height:56px;}
.post-card{border-radius:14px;border:none;}
.post-title{font-size:1.6rem;font-weight:700;letter-spacing:-0.02em;margin-bottom:0.35rem;}
.post-header{border-bottom:1px solid #f1f3f5;padding-bottom:0.6rem;}
.post-meta-row{display:flex;align-items:center;flex-wrap:wrap;gap:0.35rem;color:#6b7280;}
.post-author-link{color:#2563eb;}
.post-author-link:hover{text-decoration:underline;}
.post-meta-row .dot{color:#d1d5db;font-size:0.8rem;}
.post-content{margin-top:1rem;line-height:1.7;font-size:0.96rem;color:#111827;}
.post-content img{max-width:100%;height:auto;}
.post-actions{border-top:1px solid #f1f3f5;padding-top:0.75rem;margin-top:1.25rem;}
.like-btn{min-width:80px;}
.comments-section{margin-top:1.5rem;}
.comment-item{padding:0.75rem 0;border-bottom:1px solid #f3f4f6;gap:10px;}
.comment-avatar{width:32px;height:32px;border-radius:999px;background:#e5e7eb;display:flex;align-items:center;justify-content:center;font-size:0.85rem;font-weight:600;color:#374151;margin-right:8px;}
.comment-body{font-size:0.9rem;}
.comment-author{color:#2563eb;}
.comment-author:hover{text-decoration:underline;}
.comment-content a{color:#2563eb;text-decoration:underline;}
.reply-list{border-left:2px solid #e5e7eb;padding-left:0.75rem;margin-top:0.35rem;}
.reply-item{margin-bottom:0.35rem;font-size:0.86rem;}
.reply-meta{font-weight:500;}
.reply-content{margin-top:2px;}
.comment-write-card{border-radius:12px;}
.text-like{color:#ef4444 !important;}
.post-detail-loading .spin{animation:spin 1s linear infinite;}
@keyframes spin{100%{transform:rotate(360deg)}}
@media (max-width: 576px){
  .post-title{font-size:1.3rem;}
  .post-content{font-size:0.94rem;}
  .comment-avatar{display:none;}
}
</style>
