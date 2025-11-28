<template>
  <PrevNextButtons v-if="loaded" :posts="posts" class="mb-3" />

  <div v-if="!loaded" class="post-detail-loading text-center pt-5">
    <i class="bi bi-arrow-repeat fs-2 spin"></i>
  </div>

  <section v-else class="post-detail-page container pt-navbar my-4">
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

      <!-- 모바일 플로팅 -->
      <div class="dropup d-md-none position-fixed action-fab">
        <button
            class="btn btn-primary rounded-circle shadow dropdown-toggle"
            data-bs-toggle="dropdown"
            aria-expanded="false"
        >
          <i class="fas fa-ellipsis-v"></i>
        </button>
        <ul class="dropdown-menu dropdown-menu-end">
          <li>
            <button class="dropdown-item" @click="goEdit">
              <i class="fas fa-edit me-2"></i> 수정
            </button>
          </li>
          <li>
            <button class="dropdown-item text-danger" @click="onDelete">
              <i class="fas fa-trash me-2"></i> 삭제
            </button>
          </li>
        </ul>
      </div>
    </div>

    <!-- 본문 카드 -->
    <article class="card shadow-sm post-card">
      <div class="card-body">
        <header class="post-header mb-3">
          <h1 class="post-title">
            {{ feed.title }}
          </h1>

          <div class="post-meta-row small">
            <RouterLink :to="`/user/profile/${feed.username}`" class="post-author-link text-decoration-none d-inline-flex align-items-center">
              <span v-if="userRankIndex !== -1" class="me-1">{{ rankIcon(userRankIndex) }}</span>
              <span v-if="isHotUser(feed.username)" class="hot-fire me-1" aria-label="top recent">🔥</span>
              <span class="fw-semibold">{{ feed.username }}</span>
            </RouterLink>
            <span class="dot">·</span>
            <span v-if="!feed.modifiedAt" class="ms-1">· {{ dateText }}</span>
            <span class="dot">·</span>
            <span>조회 {{ feed.viewCount ?? 0 }}</span>
            <span class="dot">·</span>
            <span class="d-inline-flex align-items-center">
            <i
            :class="[  'bi', isLiked('POST', feed.id) ? 'bi-heart-fill text-like' : 'bi-heart'  ]"
            class="me-1"></i><span>{{ likeCountOf('POST', feed.id) }}</span></span>
            <span v-if="feed.modifiedAt" class="dot">·</span>
            <span v-if="feed.modifiedAt" class="text-muted"> 수정 {{ fmtDate(feed.modifiedAt) }}
         </span>
          </div>
        </header>


        <section class="post-content" v-html="processedDescription" />
        <footer class="post-actions d-flex justify-content-between align-items-center mt-4">
          <div class="text-muted small">
            <i class="bi bi-chat-dots me-1"></i> 댓글 {{ comments.length }}
          </div>
          <button
              class="btn btn-sm btn-outline-danger like-btn d-inline-flex align-items-center"
              @click="() => toggleLike('POST', feed.id)"
          >
            <i :class="['bi', isLiked('POST', feed.id) ? 'bi-heart-fill text-like' : 'bi-heart']"
                class="me-1"
            ></i>
            <span>{{ likeCountOf('POST', feed.id) }}</span>
          </button>
        </footer>
      </div>
    </article>

    <!-- 댓글 영역 -->
    <section class="comments-section mt-4">
      <div class="d-flex justify-content-between align-items-center mb-2">
        <h5 class="mb-0">
          <i class="bi bi-chat"></i>
          댓글 <span class="text-muted small">({{ comments.length }})</span>
        </h5>
      </div>

      <!-- 댓글 리스트 -->
      <div v-if="comments.length === 0" class="text-muted py-3 small">
        아직 댓글이 없습니다. 첫 번째 댓글을 남겨보세요.
      </div>

      <div v-for="c in comments" :key="c.id + '-' + reloadTrigger" class="comment-item d-flex">
        <div class="comment-body flex-grow-1">
          <div class="d-flex justify-content-between align-items-start">
            <div class="comment-meta">
              <RouterLink :to="`/user/profile/${c.username}`" class="comment-author text-decoration-none">
                <span class="me-1">{{ rankBadge(c.username) }}</span>
                <span v-if="isHotUser(c.username)" class="hot-fire me-1" aria-label="top recent">🔥</span>
                <span class="fw-semibold">{{ c.username }}</span>
                <span v-if="c.author" class="badge-author ms-1">글쓴이</span>
                <span v-if="c.owner" class="badge-author ms-1">작성자</span>
              </RouterLink>

              <small class="ms-2 text-muted">
                <template v-if="c.updatedAt">
                  (수정됨 · {{ fmtDate(c.updatedAt) }})
                </template>
                <template v-else>
                  {{ fmtDate(c.createdAt) }}
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


            <!-- 내용 -->
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
                  <RouterLink :to="`/user/profile/${rp.username}`" class="comment-author text-decoration-none">
                    <span class="me-1">{{ rankBadge(rp.username) }}</span>
                    <span v-if="isHotUser(rp.username)" class="hot-fire me-1" aria-label="top recent">🔥</span>
                    <span class="fw-semibold">{{ rp.username }}</span>
                    <span v-if="rp.author" class="badge-author ms-1">글쓴이</span>
                    <span v-if="rp.owner" class="badge-author ms-1">작성자</span>
                  </RouterLink>


                  <small class="text-muted ms-2">
                    <template v-if="rp.updatedAt">
                      (수정됨 · {{ fmtDate(rp.updatedAt) }})
                    </template>
                    <template v-else>
                      {{ fmtDate(rp.createdAt) }}
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


                <!-- 내용 or 수정 폼 -->
              <div v-if="replyEditMode[rp.id]" class="mt-2">
              <textarea v-model="replyEditTexts[rp.id]" rows="2" class="form-control mb-2"/>
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

    <!-- 댓글 작성 -->
    <section class="card shadow-sm p-3 mt-4 comment-write-card">
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
import { ref, onMounted, computed ,watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '@/utils/api'
import PrevNextButtons from '@/components/PrevNextButtons.vue'
import { useUserStore } from '@/stores/user'
import { usePostDetailStore } from '@/stores/postDetail'
import { RouterLink } from 'vue-router'
import {storeToRefs} from "pinia";
import {useRankIcon} from "@/composables/useRankIcon.js";
import {useSidebarStore} from "@/stores/sidebar.js";
const route   = useRoute()
const router  = useRouter()
const store   = useUserStore()
const posts = ref([])
const id         = route.params.id
const feed       = ref({})
const replyEditTexts = ref({})
const replyEditMode  = ref({})
const likeStates = ref({})
const { rankIcon } = useRankIcon()
const feedHtml   = ref('')
const comments = ref([])
const hasPoll = ref(false)
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
const isOwner = computed(() => feed.value.owner === true)
const editingCommentId = ref(null)
const editTexts = ref({})
const editSending = ref(false)
const postDetailStore = usePostDetailStore()
const PAGE_SIZE = 10
const pageParam = computed(() => {
  const q = parseInt(route.query.page ?? '0', 10)
  return Number.isNaN(q) ? 0 : q        // 잘못된 값이면 0
})
const sb = useSidebarStore()
const { topWriters , topRecentWriters } = storeToRefs(sb)

const userRankIndex = computed(() => {
  const username = feed.value.username
  if (!topWriters.value || !username) {
    return -1
  }
  const index = topWriters.value.findIndex(
      writer => writer.username === username
  )
  return index
})
const topRecentSet = computed(() =>
    new Set((topRecentWriters.value ?? []).slice(0, 5).map(w => w.username))
)
function isHotUser(username) {
  if (!username) return false
  return topRecentSet.value.has(username)
}
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
function startReplyEdit(rp) {
  replyEditTexts.value[rp.id] = rp.content
  replyEditMode.value[rp.id] = true
}

function cancelReplyEdit(id) {
  replyEditMode.value[id] = false
}

async function updateReply(rp) {
  const text = (replyEditTexts.value[rp.id] || '').trim()
  if (!text) {
    alert('답글 내용을 입력하세요.')
    return
  }

  const token = localStorage.getItem('token')
  if (!token) {
    alert('로그인이 필요합니다.')
    router.push('/login')
    return
  }

  try {
    const { data } = await api.put(
        `/reply/${rp.id}`,
        { content: text },
        { headers: { Authorization: `Bearer ${token}` } }
    )
    const updated = data?.reply ?? {}

    // 어느 댓글 밑의 답글인지 찾기
    const commentId = Object.keys(replies.value).find(cid =>
        replies.value[cid].some(x => x.id === rp.id)
    )

    if (commentId) {
      replies.value[commentId] = replies.value[commentId].map(x =>
          x.id === rp.id
              ? {
                ...x,
                content:   updated.content ?? text,
                updatedAt: updated.updatedAt ?? new Date().toISOString()
              }
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
  if (!token) {
    alert('로그인이 필요합니다.')
    router.push('/login')
    return
  }

  try {
    await api.delete(`/reply/${rp.id}`, {
      headers: { Authorization: `Bearer ${token}` }
    })

    const commentId = Object.keys(replies.value).find(cid =>
        replies.value[cid].some(x => x.id === rp.id)
    )

    if (commentId) {
      replies.value[commentId] = replies.value[commentId].filter(x => x.id !== rp.id)
    }
  } catch (e) {
    console.error(e)
    alert('답글 삭제 실패')
  }
}

function startEditComment(c) {
  editingCommentId.value = c.id
  // 기존 내용으로 초기화
  editTexts.value[c.id] = c.content
}

function cancelEdit() {
  if (editingCommentId.value != null) {
    editTexts.value[editingCommentId.value] = ''
  }
  editingCommentId.value = null
}

async function updateComment(commentId) {
  const text = editTexts.value[commentId] || ''

  if (!text.trim()) {
    alert('수정할 내용을 입력하세요.')
    return
  }

  const token = localStorage.getItem('token')
  if (!token) {
    alert('로그인이 필요합니다.')
    router.push('/login')
    return
  }

  if (editSending.value) return
  editSending.value = true
  try {
    const { data } = await api.put(
        `/comment/${commentId}`,
        { content: text },
        {
          headers: {
            Authorization: `Bearer ${token}`
          }
        }
    )
    const updated = data?.comment ?? data ?? {}

    comments.value = comments.value.map(c =>
        c.id === commentId
            ? {
              ...c,
              content: updated.content ?? text,
              modifiedAt: updated.modifiedAt ?? updated.updatedAt ?? c.modifiedAt
    } : c)

    editingCommentId.value = null
  } catch (e) {
    console.error(e)
    alert('댓글 수정 중 오류 발생')
  } finally {
    editSending.value = false
  }
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

async function loadComments(postId) {

  const { data } = await api.get('/comments', { params: { postId } })
  comments.value =
      (Array.isArray(data?.ok) && data.ok) ||
      (Array.isArray(data?.comments) && data.comments) ||
      (Array.isArray(data?.data?.comments) && data.data.comments) ||
      []
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
async function loadFeedDetail(postId) {
  try {
    loaded.value = false
    const {data} = await api.get(`/post/${postId}`)
    hasPoll.value = data?.ok?.hasPoll ?? false
    if (hasPoll.value && data.ok.poll) {
      postDetailStore.setDetail({

        post: data.ok.post,
        poll: data.ok.poll,
      })
   try{
      fetch('/api/view/count', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        credentials: 'include',
        body: JSON.stringify({id})
      }).catch(e => console.error('조회수 증가 실패', e))
    } catch (e)
    {
      console.error(e)
      router.replace('/')
    }
    router.replace({
        name: 'PollDetail',
        params: { id: data.ok.post.id || postId },
      })
      return
    }

    const raw = data?.ok.post ?? {}
    feed.value = {
      id:          raw.id,
      username:    raw.username ?? '',
      imageURL:    raw.imageURL ?? raw.imageUrl ?? null,
      title:       raw.title ?? '',
      description: raw.description ?? '',
      category:    raw.category ?? null,
      likeCount:   raw.likeCount ?? 0,
      viewCount:   raw.viewCount ?? 0,
      createdAt:   raw.createdAt ?? null,
      modifiedAt:   raw.modifiedAt ?? null,
      owner:      raw.owner,
    }
    ensureLikeState(
        'POST',
        feed.value.id,
        feed.value.likeCount ?? 0,
        data.isLiked ?? false
    )
    await Promise.all([
      loadComments(feed.value.id),
      loadReplies(feed.value.id),
    ])
    await loadLikeCounts(feed.value.id)
    await loadLikeDetail(feed.value.id)
    function normalize(html = '') {
      return html
          .replace(/></g, '>\u200B<')
          .replace(/<\/div>/g, '<br>')
          .replace(/<div>/g, '');
    }
    // const uidList = await fetchFeedUIDs(pageParam.value, PAGE_SIZE)
    // posts.value   = uidList.map(uid => ({ feedUID: uid }))

    // feedHtml.value = convertLinks(normalize(decodeHtmlEntities(data.data.description || '')))
    // comments.value = data.comment || []
    // likeCount.value= data.data.likeCount || 0
    liked.value    = data.isLiked
    loaded.value   = true
    fetch('/api/view/count', {
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
onMounted(
    () => loadFeedDetail(route.params.id)


)
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

watch(
    () => route.params.id,
    (newId, oldId) => {
      if (newId && newId !== oldId) loadFeedDetail(newId)
    }
)

async function toggleLike(targetType, targetId) {
  if (!login.value) {
    if (typeof push === 'function') {
      push('로그인이 필요합니다')
    } else {
      alert('로그인이 필요합니다')
    }
    router.push('/login')
    return
  }

  const postId = feed.value.id
  ensureLikeState(targetType, targetId)

  const key = likeKey(targetType, targetId)
  const state = likeStates.value[key]
  const prevLiked = state.liked
  const prevCount = state.count

  // 낙관적 업데이트
  state.liked = !prevLiked
  state.count = prevCount + (prevLiked ? -1 : 1)

  try {
    await api.post('/like', {
      postId,
      targetId,
      targetType, // 'POST' | 'COMMENT' | 'REPLY'
    })
  } catch (e) {
    console.error(e)
    // 실패 시 롤백
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

      const postIdNum = Number(feed.value.id)

      let targetType = null
      if (targetId === postIdNum) {
        targetType = 'POST'
      } else if (commentIdSet.has(targetId)) {
        targetType = 'COMMENT'
      } else if (replyIdSet.has(targetId)) {
        targetType = 'REPLY'
      }

      if (!targetType) {
        console.log('매칭 안 된 targetId:', targetId)
        return
      }

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
  if (!token) return // 비로그인: isOwner 정보 없음, 그냥 개수만 사용

  try {
    const { data } = await api.get(`/like/detail/${postId}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    const list = Array.isArray(data?.likes) ? data.likes : []

    list.forEach(item => {
      const targetType =
          item.targetType ?? item.target_type ?? item.type
      const targetId =
          Number(item.targetId ?? item.target_id ?? item.id)
      const liked =
          Boolean(item.isOwner ?? item.owner ?? item.liked)

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
    await api.post(`/comment`, {
      content: commentText.value,
      username: store.username,
      postId: feed.value.id
    }, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
    await new Promise(resolve => setTimeout(resolve, 1000))
    await loadComments(feed.value.id)
    // comments.value = data.comment || []
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
        path:'/post/update',
        query:{ id:feed.value.id}
      }
  )
}
async function onDelete() {
  if (!confirm('정말로 이 게시글을 삭제하시겠습니까?')) return

  try {
    const token = localStorage.getItem('token')

    await api.delete(`/post/${feed.value.id}`, {
      headers: token ? { Authorization: `Bearer ${token}` } : {}
    })
    router.push('/')
  } catch (e) {
    console.error(e)
    alert('삭제 중 오류 발생')
  }
}

async function delComment(c) {
  if (!confirm('삭제할까요?')) return

  const token = localStorage.getItem('token')
  if (!token) {
    alert('로그인이 필요합니다.')
    router.push('/login')
    return
  }
  try {
    await api.delete(`/comment/${c.id}`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })

    comments.value = comments.value.filter(v => v.id !== c.id)
    alert('댓글이 삭제되었습니다.')
  } catch (e) {
    console.error(e)
    alert('삭제 중 오류 발생')
  }
}


function toggleReplyForm(commentUID) {
  activeReply.value = activeReply.value === commentUID ? null : commentUID
  replyText.value = ''
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
      postId: feed.value.id,
      username: store.username, // 서버가 토큰에서 유저 꺼내면 이 필드는 빼도 됨
      content: text,
    }, {
      headers: { Authorization: `Bearer ${token}` }
    })


    await Promise.all([
      loadComments(feed.value.id),
      loadReplies(feed.value.id),
    ])

    // 폼 초기화
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
const processedDescription = computed(() => {
  const desc = feed.value.description || '';
  return convertLinks(decodeHtmlEntities(desc));
});

</script>

<style scoped>
.pt-navbar {
  padding-top: 60px;
}
.badge-author {
  display: inline-flex;
  align-items: center;
  padding: 0 6px;
  height: 18px;
  font-size: 0.75rem;
  border-radius: 999px;
  background: #fee2e2;
  color: #b91c1c;
  font-weight: 600;
}

/* 전체 페이지 배경 감성 맞추기 */
.post-detail-page {
  max-width: 900px;
}

/* 상단 FAB 버튼 */
.action-fab {
  bottom: 80px;
  right: 24px;
  z-index: 1051;
}

.action-fab .btn {
  width: 56px;
  height: 56px;
}

/* 본문 카드 */
.post-card {
  border-radius: 14px;
  border: none;
}

.post-title {
  font-size: 1.6rem;
  font-weight: 700;
  letter-spacing: -0.02em;
  margin-bottom: 0.3rem;
}

.post-header {
  border-bottom: 1px solid #f1f3f5;
  padding-bottom: 0.6rem;
}

.post-header {
  border-bottom: 1px solid #f1f3f5;
  padding-bottom: 0.6rem;
}

.post-title {
  font-size: 1.6rem;
  font-weight: 700;
  letter-spacing: -0.02em;
  margin-bottom: 0.35rem;
}

/* 🔥 한 줄 메타 */
.post-meta-row {
  display: flex;
  align-items: center;
  flex-wrap: wrap;           /* 모바일에서 자연스럽게 줄바꿈 */
  gap: 0.35rem;
  color: #6b7280;
}

.post-author-link {
  color: #2563eb;
}

.post-author-link:hover {
  text-decoration: underline;
}

.post-meta-row .dot {
  color: #d1d5db;
  font-size: 0.8rem;
}


.post-meta-sub .dot {
  color: #d1d5db;
}

.hot-fire {
  display: inline-flex;
  align-items: center;
  font-size: 0.95rem;      /* 살짝만 작게 */
  line-height: 1;
  vertical-align: text-bottom;
}
/* 본문 내용 */
.post-content {
  margin-top: 1rem;
  line-height: 1.7;
  font-size: 0.96rem;
  color: #111827;
}

.post-content img {
  max-width: 100%;
  height: auto;
}

/* 본문 하단 액션 */
.post-actions {
  border-top: 1px solid #f1f3f5;
  padding-top: 0.75rem;
  margin-top: 1.25rem;
}

.like-btn {
  min-width: 80px;
}

/* 댓글 영역 */
.comments-section {
  margin-top: 1.5rem;
}

/* 댓글 아이템 */
.comment-item {
  padding: 0.75rem 0;
  border-bottom: 1px solid #f3f4f6;
  gap: 10px;
}

/* 댓글 아바타 */
.comment-avatar {
  width: 32px;
  height: 32px;
  border-radius: 999px;
  background: #e5e7eb;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.85rem;
  font-weight: 600;
  color: #374151;
  margin-right: 8px;
}

/* 댓글 내용 */
.comment-body {
  font-size: 0.9rem;
}

.comment-author {
  color: #2563eb;
}

.comment-author:hover {
  text-decoration: underline;
}

.comment-content a {
  color: #2563eb;
  text-decoration: underline;
}

/* 대댓글 리스트 */
.reply-list {
  border-left: 2px solid #e5e7eb;
  padding-left: 0.75rem;
  margin-top: 0.35rem;
}

.reply-item {
  margin-bottom: 0.35rem;
  font-size: 0.86rem;
}

.reply-meta {
  font-weight: 500;
}

.reply-content {
  margin-top: 2px;
}

/* 댓글 작성 카드 */
.comment-write-card {
  border-radius: 12px;
}
.text-like {
  color: #ef4444 !important;
}
/* 로딩 스피너 */
.post-detail-loading .spin {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  100% {
    transform: rotate(360deg);
  }
}

/* 반응형 조정 */
@media (max-width: 576px) {
  .post-title {
    font-size: 1.3rem;
  }

  .post-content {
    font-size: 0.94rem;
  }

  .comment-avatar {
    display: none;
  }
}
</style>


