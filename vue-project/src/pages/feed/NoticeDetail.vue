<template>
  <section class="container my-4 pt-navbar">
    <div v-if="loading" class="text-center pt-5">
      <i class="bi bi-arrow-repeat fs-2 spin"></i>
    </div>

    <div v-else-if="error" class="alert alert-danger">
      {{ error }} <button class="btn btn-sm btn-outline-secondary ms-2" @click="goList">목록</button>
    </div>

    <div v-else>
      <div v-if="isOwner" class="text-end mb-2">
        <div class="dropdown d-none d-md-inline-block">
          <button class="btn btn-outline-secondary btn-sm rounded-pill" data-bs-toggle="dropdown">
            <i class="fas fa-ellipsis-v"></i>
          </button>
          <ul class="dropdown-menu dropdown-menu-end">
            <li>
              <button type="button" class="dropdown-item" @click="goEdit">
                <i class="fas fa-edit me-2"></i> 수정
              </button>
            </li>
            <li>
              <button type="button" class="dropdown-item text-danger" @click="onDelete">
                <i class="fas fa-trash me-2"></i> 삭제
              </button>
            </li>
          </ul>
        </div>
      </div>

      <div class="card shadow-sm feed-card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-start gap-3">
            <h2 class="feed-title mb-0">{{ notice.title }}</h2>
          </div>
          <p class="feed-meta text-muted mt-2">
            <span class="badge bg-primary-subtle text-primary me-2">공지사항</span>
            <strong class="me-2">{{ notice.username || '관리자' }}</strong>
            · <span class="me-2">{{ dateText }}</span>
            · <span><i class="bi bi-eye"></i> {{ notice.viewCount ?? 0 }}</span>
          </p>

          <hr>

          <div class="feed-content" v-html="htmlDesc"></div>

          <footer class="post-actions d-flex justify-content-between align-items-center mt-4">
            <div class="text-muted small">
              <i class="bi bi-chat-dots me-1"></i> 댓글 {{ comments.length }}
            </div>
            <button
                class="btn btn-sm btn-outline-danger like-btn d-inline-flex align-items-center"
                @click="() => toggleLike('POST', notice.id)"
            >
              <i :class="['bi', isLiked('POST', notice.id) ? 'bi-heart-fill text-like' : 'bi-heart']"
                 class="me-1"
              ></i>
              <span>{{ likeCountOf('POST', notice.id) }}</span>
            </button>
          </footer>
        </div>
      </div>
    </div>

    <section v-if="!loading && !error" class="comments-section mt-4">
      <div class="d-flex justify-content-between align-items-center mb-2">
        <h5 class="mb-0">
          <i class="bi bi-chat"></i>
          댓글 <span class="text-muted small">({{ comments.length }})</span>
        </h5>
      </div>

      <div v-if="comments.length === 0" class="text-muted py-3 small">
        아직 댓글이 없습니다. 첫 번째 댓글을 남겨보세요.
      </div>

      <div v-for="c in comments" :key="c.id + '-' + reloadTrigger" class="comment-item d-flex">
        <div class="comment-body flex-grow-1">
          <div class="d-flex justify-content-between align-items-start">
            <div class="comment-meta">
              <RouterLink
                  :to="`/user/profile/${c.username}`"
                  class="comment-author text-decoration-none">
                <span class="me-1">{{ rankBadge(c.username) }}</span>
                <span class="fw-semibold">{{ c.username }}</span>
                <span v-if="c.author" class="badge-author ms-1">글쓴이</span>
                <span v-if="c.owner" class="badge-author ms-1">작성자</span>
              </RouterLink>

              <small class="ms-2 text-muted">
                <template v-if="c.updatedAt">
                  (수정됨 · {{ formatDate(c.updatedAt) }})
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
                  @click="delComment(c)">삭제
              </button>

              <button
                  class="btn btn-sm btn-link text-danger p-0 d-inline-flex align-items-center"
                  @click="() => toggleLike('COMMENT', c.id)"><i :class="[ 'bi',  isLiked('COMMENT', c.id) ? 'bi-heart-fill text-like' : 'bi-heart' ]"
                                                                class="me-1"
              ></i>
                <span class="small">{{ likeCountOf('COMMENT', c.id) }}</span>
              </button>
            </div>
          </div>

          <div class="mt-1 comment-content" v-html="linkify(c.content)"></div>
          <div v-if="editingCommentId === c.id" class="mt-2">
      <textarea
          v-model="editTexts[c.id]" rows="2"
          class="form-control mb-2" placeholder="수정할 내용을 입력하세요."/>
            <div class="d-flex gap-2">
              <button
                  class="btn btn-sm btn-primary"
                  :disabled="editSending" @click="updateComment(c.id)">수정 완료
              </button>
              <button class="btn btn-sm btn-outline-secondary" @click="cancelEdit">
                취소
              </button>
            </div>
          </div>

          <div class="mt-2 reply-list" v-if="replies && replies[c.id]">
            <div v-for="rp in replies[c.id]" :key="rp.id" class="reply-item">
              <div class="d-flex justify-content-between align-items-start">
                <div class="reply-meta">
                  <RouterLink
                      :to="`/user/profile/${rp.username}`"
                      class="comment-author text-decoration-none">
                    <span class="me-1">{{ rankBadge(rp.username) }}</span>
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

                  <button
                      class="btn btn-link btn-sm text-danger p-0 d-inline-flex align-items-center"
                      @click="() => toggleLike('REPLY', rp.id)"
                  >
                    <i
                        :class="['bi', isLiked('REPLY', rp.id) ? 'bi-heart-fill  text-like' : 'bi-heart']"
                        class="me-1"
                    ></i>
                    <span class="small">{{ likeCountOf('REPLY', rp.id) }}</span>
                  </button>
                </div>
              </div>


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
                  <button
                      class="btn btn-sm btn-outline-secondary"
                      @click="cancelReplyEdit(rp.id)"
                  >
                    취소
                  </button>
                </div>
              </div>
              <div v-else class="reply-content">
                {{ rp.content }}
              </div>
            </div>
          </div>
          <button
              class="btn btn-sm btn-outline-primary mt-2"
              @click="toggleReplyForm(c.id)">
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

    <section v-if="!loading && !error" class="card shadow-sm p-3 mt-4 comment-write-card">
      <div class="d-flex justify-content-between align-items-center mb-2">
        <h6 class="mb-0">
          <i class="bi bi-pencil-square me-1"></i> 댓글 쓰기
        </h6>
        <button v-if="!login" class="btn btn-sm btn-outline-secondary" @click="router.push('/login')">로그인
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
            :disabled="!login || sending || !commentText.trim()" @click="submitComment">
          작성하기
        </button>
      </div>
    </section>
  </section>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '@/utils/api'
import { useUserStore } from '@/stores/user'
import { storeToRefs } from 'pinia'
import { useSidebarStore } from '@/stores/sidebar.js'
import { useRankIcon } from '@/composables/useRankIcon.js'
import { RouterLink } from 'vue-router'

const route = useRoute()
const router = useRouter()
const store = useUserStore()

// --- 기존 Notice 상태 ---
const loading = ref(true)
const error   = ref('')
const notice  = ref({})
const idParam = computed(() => route.params.id)

// --- PostDetail에서 가져온 상태 변수들 ---
const comments = ref([])
const replies = ref({})
const likeStates = ref({})
const activeReply = ref(null)
const replyTexts = ref({})
const replySendingMap = ref({})
const commentText = ref('')
const sending = ref(false)
const reloadTrigger = ref(0)
const editingCommentId = ref(null)
const editTexts = ref({})
const editSending = ref(false)
const replyEditTexts = ref({})
const replyEditMode = ref({})

// --- Store 및 헬퍼 ---
const { rankIcon } = useRankIcon() // 랭킹 아이콘 (댓글/답글에 사용)
const sb = useSidebarStore()
const { topWriters } = storeToRefs(sb)

// --- Computed 속성 ---
const htmlDesc = computed(() => convertLinks(notice.value.description))
const dateText = computed(() => formatDate(notice.value.createdAt))
const login = computed(() => store.isLoggedIn)
const isOwner = computed(() => notice.value.owner === true)

// 랭킹 계산 (공지사항에는 불필요하지만 댓글/답글에 사용하기 위해 로직 유지)
const userRankIndex = computed(() => {
  // 공지사항 작성자는 관리자이므로 랭킹 계산에서 제외하거나 필요 없다고 가정
  return -1
})
function getRankIconForUser(username) {
  if (!topWriters.value || !username) {
    return ''
  }
  const index = topWriters.value.findIndex(
      writer => writer.username === username
  )
  return index !== -1 ? rankIcon(index) : ''
}
function rankBadge(username) {
  return getRankIconForUser(username)
}

// --- 공지사항 본문 헬퍼 ---
function convertLinks(html = '') {
  if (!html) return ''
  const normalized = html.replace(/<\/div>/g, '<br>').replace(/<div>/g, '')
  return normalized.replace(/(https?:\/\/[^\s<"]+)/g, m => `<a href="${m}" target="_blank" rel="noopener">${m}</a>`)
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
// fmtDate 헬퍼 함수는 formatDate로 통일 (PostDetail의 fmtDate 역할)
const fmtDate = formatDate
function linkify(text = '') {
  const urlRegex = /(https?:\/\/[^\s]+)/g
  return text.replace(urlRegex, url => `<a href="${url}" target="_blank">${url}</a>`)
}


// --- 좋아요 관련 함수 ---
const likeKey = (targetType, targetId) => `${targetType}-${targetId}`

function ensureLikeState(targetType, targetId, initialCount = 0, initialLiked = false) {
  const key = likeKey(targetType, targetId)
  if (!likeStates.value[key]) {
    likeStates.value[key] = {
      liked: initialLiked,
      count: initialCount,
    }
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

async function toggleLike(targetType, targetId) {
  if (!login.value) {
    alert('로그인이 필요합니다')
    router.push('/login')
    return
  }

  const postId = notice.value.id // 공지사항 ID를 Post ID로 사용
  ensureLikeState(targetType, targetId)

  const key = likeKey(targetType, targetId)
  const state = likeStates.value[key]
  const prevLiked = state.liked
  const prevCount = state.count

  state.liked = !prevLiked
  state.count = prevCount + (prevLiked ? -1 : 1)

  try {
    // API 경로는 Post와 동일한 좋아요 시스템을 사용한다고 가정
    await api.post('/like', {
      postId,
      targetId,
      targetType, // 'POST' | 'COMMENT' | 'REPLY'
    })
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
    const repliesObj =
        replies.value && typeof replies.value === 'object' ? replies.value : {}

    const commentIdSet = new Set(commentArr.map(c => Number(c.id)))
    const replyIdSet = new Set()
    Object.values(repliesObj).forEach(arr => {
      (arr || []).forEach(rp => {
        replyIdSet.add(Number(rp.id))
      })
    })

    list.forEach(item => {
      const targetId = Number(item.targetId ?? item.id)
      const count = Number(item.count ?? 0)
      if (!Number.isFinite(targetId)) return

      const postIdNum = Number(notice.value.id) // notice.value.id 사용

      let targetType = null
      if (targetId === postIdNum) {
        targetType = 'POST'
      } else if (commentIdSet.has(targetId)) {
        targetType = 'COMMENT'
      } else if (replyIdSet.has(targetId)) {
        targetType = 'REPLY'
      }

      if (!targetType) return

      const key = likeKey(targetType, targetId)
      const prev = likeStates.value[key] || { liked: false, count: 0 }

      likeStates.value[key] = {
        liked: prev.liked,
        count
      }
    })
  } catch (e) {
    console.error('like count 로드 실패', e)
  }
}

async function loadLikeDetail(postId) {
  const token = localStorage.getItem('token')
  if (!token) return

  try {
    const { data } = await api.get(`/like/detail/${postId}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    const list = Array.isArray(data?.likes) ? data.likes : []

    list.forEach(item => {
      const targetType = item.targetType ?? item.target_type ?? item.type
      const targetId = Number(item.targetId ?? item.target_id ?? item.id)
      const liked = Boolean(item.isOwner ?? item.owner ?? item.liked)

      if (!targetType || !targetId) return

      const key = likeKey(targetType, targetId)
      const prev = likeStates.value[key] || { liked: false, count: 0 }

      likeStates.value[key] = {
        liked,
        count: prev.count
      }
    })
  } catch (e) {
    console.error('like detail 로드 실패', e)
  }
}

// --- 댓글/답글 로드 및 관리 함수 ---
async function loadComments(postId) {
  const { data } = await api.get('/comments', { params: { postId } })
  comments.value =
      (Array.isArray(data?.ok) && data.ok) ||
      (Array.isArray(data?.comments) && data.comments) ||
      (Array.isArray(data?.data?.comments) && data.data.comments) ||
      []
}
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

async function submitComment() {
  if (sending.value) return
  if (!commentText.value.trim()) { alert("댓글 내용을 입력하세요!"); return }
  const token = localStorage.getItem("token")
  if (!token) { alert("로그인이 필요합니다."); router.push("/login"); return }
  sending.value = true

  try {
    await api.post(`/comment`, {
      content: commentText.value,
      username: store.username,
      postId: notice.value.id // notice.value.id 사용
    }, { headers: { Authorization: `Bearer ${token}` } })

    // 로딩 후 재조회
    await new Promise(resolve => setTimeout(resolve, 500))
    await loadComments(notice.value.id)
    commentText.value = ''
  } catch (e) {
    console.error(e)
    alert("댓글 저장 중 오류 발생")
  } finally {
    sending.value = false
  }
}

async function delComment(c) {
  if (!confirm('댓글을 삭제할까요?')) return
  const token = localStorage.getItem('token')
  if (!token) { alert('로그인이 필요합니다.'); router.push('/login'); return }
  try {
    await api.delete(`/comment/${c.id}`, { headers: { Authorization: `Bearer ${token}` } })
    comments.value = comments.value.filter(v => v.id !== c.id)
    alert('댓글이 삭제되었습니다.')
  } catch (e) {
    console.error(e)
    alert('삭제 중 오류 발생')
  }
}

function startEditComment(c) {
  editingCommentId.value = c.id
  editTexts.value[c.id] = c.content
}

function cancelEdit() {
  if (editingCommentId.value != null) { editTexts.value[editingCommentId.value] = '' }
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
    const { data } = await api.put(
        `/comment/${commentId}`,
        { content: text },
        { headers: { Authorization: `Bearer ${token}` } }
    )
    const updated = data?.comment ?? data ?? {}

    comments.value = comments.value.map(c =>
        c.id === commentId
            ? { ...c, content: updated.content ?? text, modifiedAt: updated.modifiedAt ?? updated.updatedAt ?? c.modifiedAt }
            : c)

    editingCommentId.value = null
  } catch (e) {
    console.error(e)
    alert('댓글 수정 중 오류 발생')
  } finally {
    editSending.value = false
  }
}

function toggleReplyForm(commentUID) {
  activeReply.value = activeReply.value === commentUID ? null : commentUID
  replyTexts.value[commentUID] = '' // 폼 열 때 내용 초기화
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
      postId: notice.value.id, // notice.value.id 사용
      username: store.username,
      content: text,
    }, { headers: { Authorization: `Bearer ${token}` } })

    await Promise.all([
      loadComments(notice.value.id),
      loadReplies(notice.value.id),
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
    const { data } = await api.put(
        `/reply/${rp.id}`,
        { content: text },
        { headers: { Authorization: `Bearer ${token}` } }
    )
    const updated = data?.reply ?? {}
    const commentId = Object.keys(replies.value).find(cid => replies.value[cid].some(x => x.id === rp.id))

    if (commentId) {
      replies.value[commentId] = replies.value[commentId].map(x =>
          x.id === rp.id
              ? { ...x, content: updated.content ?? text, updatedAt: updated.updatedAt ?? new Date().toISOString() }
              : x
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

    if (commentId) {
      replies.value[commentId] = replies.value[commentId].filter(x => x.id !== rp.id)
    }
  } catch (e) {
    console.error(e)
    alert('답글 삭제 실패')
  }
}

// --- 공지사항 로드 ---
async function loadDetail(id) {
  loading.value = true
  error.value = ''
  try {
    const { data } = await api.get(`/notice/detail/${id}`)
    const raw = data.detail ?? data?.ok ?? {}

    // 공지사항 데이터 매핑
    notice.value = {
      id:         raw.id ?? null,
      title:      raw.title ?? '',
      description: raw.description ?? '',
      username:   raw.username ?? '관리자',
      createdAt:  raw.createdAt ?? null,
      viewCount:  raw.viewCount ?? 0,
      owner:      raw.owner ?? false, // ✅ 관리자 수정/삭제를 위해 owner 필드 추가
    }

    // 좋아요, 댓글/답글 로드 (공지사항 ID를 Post ID로 사용)
    ensureLikeState('POST', notice.value.id, 0, data.isLiked ?? false)
    await Promise.all([
      loadComments(notice.value.id),
      loadReplies(notice.value.id),
    ])
    await loadLikeCounts(notice.value.id)
    await loadLikeDetail(notice.value.id)

  } catch (e) {
    console.error(e)
    error.value = '공지사항을 불러오지 못했습니다.'
  } finally {
    loading.value = false
  }
}

// --- 공지사항 수정/삭제 ---
function goEdit(){
  router.push({
        path:'/notice/update',
        query:{ id: notice.value.id}
      }
  )
}
async function onDelete() {
  if (!confirm('정말로 이 공지사항을 삭제하시겠습니까?')) return

  try {
    const token = localStorage.getItem('token')
    await api.delete(`/notice/${notice.value.id}`, {
      headers: token ? { Authorization: `Bearer ${token}` } : {}
    })
    router.push('/notice')
  } catch (e) {
    console.error(e)
    alert('삭제 중 오류 발생')
  }
}

function goList() {
  router.push('/notice')
}

// --- Lifecycle & Watcher ---
onMounted(() => loadDetail(idParam.value))
watch(() => idParam.value, (n, o) => {
  if (n && n !== o) loadDetail(n)
})
</script>

<style scoped>
/* 기존 NoticeDetail 스타일 유지 + PostDetail의 관련 스타일 추가 */
.pt-navbar { padding-top: 60px; }
.feed-card { border-radius: 10px; }
.feed-title { font-size: 1.8rem; font-weight: 700; }
.feed-content { line-height: 1.7; word-break: break-word; }
.spin { animation: spin 1s linear infinite; }
@keyframes spin { 100% { transform: rotate(360deg); } }

/* PostDetail 스타일 추가 */
.post-actions { border-top: 1px solid #f1f3f5; padding-top: 0.75rem; margin-top: 1.25rem; }
.like-btn { min-width: 80px; }
.comments-section { margin-top: 1.5rem; }
.comment-item { padding: 0.75rem 0; border-bottom: 1px solid #f3f4f6; gap: 10px; }
.comment-author { color: #2563eb; }
.reply-list { border-left: 2px solid #e5e7eb; padding-left: 0.75rem; margin-top: 0.35rem; }
.reply-item { margin-bottom: 0.35rem; font-size: 0.86rem; }
.comment-write-card { border-radius: 12px; }
.text-like { color: #ef4444 !important; }
.badge-author { display: inline-flex; align-items: center; padding: 0 6px; height: 18px; font-size: 0.75rem; border-radius: 999px; background: #fee2e2; color: #b91c1c; font-weight: 600; }
</style>