<template>
  <PrevNextButtons v-if="loaded" :posts="posts" class="mb-3" />

  <div v-if="!loaded" class="post-detail-loading text-center pt-5">
    <div class="modern-spinner"></div>
  </div>

  <section v-else class="post-detail-page container pt-navbar my-4">
    <article class="modern-post-card shadow-sm mb-4">
      <div class="card-body">

        <header class="post-header mb-4">
          <div class="d-flex justify-content-between align-items-start mb-2">
            <h1 class="post-title">{{ feed.title }}</h1>

            <div v-if="isOwner" class="dropdown ms-3 flex-shrink-0">
              <button class="icon-btn-soft" data-bs-toggle="dropdown">
                <i class="fas fa-ellipsis-v"></i>
              </button>
              <ul class="dropdown-menu dropdown-menu-end modern-dropdown shadow-sm border-0">
                <li>
                  <button type="button" class="dropdown-item" @click="goEdit">
                    <i class="fas fa-edit me-2 text-secondary"></i> 수정
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

          <div class="post-meta-row">
            <RouterLink :to="`/user/profile/${feed.username}`" class="author-link">
              <span v-if="userRankIndex !== -1" class="me-1">{{ rankIcon(userRankIndex) }}</span>
              <span v-if="isHotUser(feed.username)" class="hot-fire me-1" aria-label="top recent">🔥</span>
              <span class="fw-bold">{{ feed.username }}</span>
            </RouterLink>
            <span class="meta-dot">·</span>
            <span v-if="!feed.modifiedAt" class="meta-text">{{ dateText }}</span>
            <span v-else class="meta-text">수정됨 {{ fmtDate(feed.modifiedAt) }}</span>
            <span class="meta-dot">·</span>
            <span class="meta-text"><i class="fas fa-eye me-1"></i>{{ feed.viewCount ?? 0 }}</span>
          </div>
        </header>

        <section class="post-content" v-html="processedDescription" />

        <footer class="post-actions d-flex justify-content-between align-items-center mt-5">
          <div class="comment-count-badge">
            <i class="fas fa-comment-dots me-1"></i> 댓글 {{ comments.length }}
          </div>

          <button
              class="like-btn-modern"
              :class="{ 'liked': isLiked('POST', feed.id) }"
              @click="() => toggleLike('POST', feed.id)"
          >
            <i class="fas fa-heart" :class="{ 'heart-beat': isLiked('POST', feed.id) }"></i>
            <span class="ms-1">{{ likeCountOf('POST', feed.id) }}</span>
          </button>
        </footer>
      </div>
    </article>

    <section class="comments-section modern-post-card shadow-sm p-4">
      <h5 class="comments-title mb-4">
        댓글 <span class="text-primary">{{ comments.length }}</span>
      </h5>

      <div v-if="comments.length === 0" class="empty-state py-5 text-center">
        <i class="far fa-comment-dots mb-2 fs-1 text-muted"></i>
        <p class="text-muted mb-0 small">아직 댓글이 없습니다.<br>첫 번째 댓글을 남겨보세요!</p>
      </div>

      <div v-for="c in comments" :key="c.id + '-' + reloadTrigger" class="comment-item-modern">
        <div class="comment-body">
          <div class="d-flex justify-content-between align-items-start mb-2">

            <div class="comment-meta">
              <RouterLink :to="`/user/profile/${c.username}`" class="comment-author">
                <span class="me-1">{{ rankBadge(c.username) }}</span>
                <span v-if="isHotUser(c.username)" class="hot-fire me-1" aria-label="top recent">🔥</span>
                <span class="fw-bold">{{ c.username }}</span>
                <span v-if="c.author" class="badge-pill bg-author ms-2">글쓴이</span>
              </RouterLink>
              <span class="meta-dot mx-2">·</span>
              <span class="meta-text small">
                {{ c.updatedAt ? `수정됨 ${fmtDate(c.updatedAt)}` : fmtDate(c.createdAt) }}
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
                    <span class="me-1">{{ rankBadge(rp.username) }}</span>
                    <span v-if="isHotUser(rp.username)" class="hot-fire me-1">🔥</span>
                    <span class="fw-bold">{{ rp.username }}</span>
                    <span v-if="rp.author" class="badge-pill bg-author ms-2">글쓴이</span>
                  </RouterLink>
                  <span class="meta-dot mx-2">·</span>
                  <span class="meta-text small">
                    {{ rp.updatedAt ? `수정됨 ${fmtDate(rp.updatedAt)}` : fmtDate(rp.createdAt) }}
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
            <i class="fas fa-reply me-1"></i> 답글 쓰기
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
      <div class="p-3">
        <div class="d-flex justify-content-between align-items-center mb-2">
          <h6 class="fw-bold mb-0 text-dark">댓글 쓰기</h6>
          <button v-if="!login" class="btn btn-sm btn-outline-secondary rounded-pill" @click="router.push('/login')">로그인</button>
        </div>
        <textarea
            v-model="commentText"
            rows="3"
            class="modern-input mb-3"
            :placeholder="login ? '상호 존중하는 댓글 문화를 만들어가요.' : '로그인 후 댓글을 작성할 수 있습니다.'"
        ></textarea>
        <div class="text-end">
          <button class="btn-submit-primary" :disabled="!login || sending || !commentText.trim()" @click="submitComment">
            댓글 등록
          </button>
        </div>
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
      try {
        const token = localStorage.getItem('token');

        fetch('/api/view/count', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            ...(token && { Authorization: `Bearer ${token}` })
          },
          credentials: 'include',
          body: JSON.stringify({ id: postId })
        })
            .then(response => {
              if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
              }
            })
            .catch(e => console.error('조회수 증가 실패:', e));
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
      const token = localStorage.getItem('token');

      fetch('/api/view/count', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          ...(token && { Authorization: `Bearer ${token}` })
        },
        credentials: 'include',
        body: JSON.stringify({ id: postId })
      })
          .then(response => {
            if (!response.ok) {
              throw new Error(`HTTP error! status: ${response.status}`);
            }
          })
          .catch(e => console.error('조회수 증가 실패:', e));
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
  if (!token) return

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
.pt-navbar { padding-top: 60px; }
.post-detail-page { max-width: 860px; }

/* ===============================
   모던 카드 & 드롭다운 디자인
================================= */
.modern-post-card {
  background: #ffffff;
  border-radius: 20px;
  border: 1px solid #f1f5f9;
  padding: 1rem;
}

.icon-btn-soft {
  background: #f8fafc;
  border: none;
  width: 32px; height: 32px;
  border-radius: 50%;
  color: #64748b;
  display: flex; align-items: center; justify-content: center;
  transition: all 0.2s;
}
.icon-btn-soft:hover { background: #e2e8f0; color: #0f172a; }

.modern-dropdown {
  border-radius: 12px;
  padding: 8px;
}
.modern-dropdown .dropdown-item {
  border-radius: 8px;
  padding: 8px 12px;
  font-weight: 500;
}
.modern-dropdown .dropdown-item:hover { background: #f1f5f9; }

/* ===============================
   게시글 헤더 & 메타 정보
================================= */
.post-header { border-bottom: 1px solid #f1f5f9; padding-bottom: 1.5rem; }
.post-title {
  font-size: 1.8rem;
  font-weight: 800;
  color: #0f172a;
  letter-spacing: -0.02em;
  line-height: 1.35;
}

.post-meta-row {
  display: flex; align-items: center; flex-wrap: wrap; gap: 4px;
  font-size: 0.9rem;
}
.author-link { color: #334155; text-decoration: none; }
.author-link:hover { color: #2563eb; }
.meta-dot { color: #cbd5e1; margin: 0 4px; }
.meta-text { color: #64748b; }

/* ===============================
   본문 콘텐츠
================================= */
.post-content {
  margin-top: 1.5rem;
  font-size: 1.05rem;
  line-height: 1.8;
  color: #334155;
  word-break: keep-all;
}
/* 본문 내 이미지 반응형 & 둥근 테두리 처리 */
:deep(.post-content img) {
  max-width: 100%;
  height: auto;
  border-radius: 12px;
  margin: 1rem 0;
}

/* ===============================
   하단 액션 (좋아요/댓글 수)
================================= */
.post-actions {
  border-top: 1px solid #f1f5f9;
  padding-top: 1.5rem;
}

.comment-count-badge {
  font-weight: 600;
  color: #64748b;
  font-size: 0.95rem;
}

.like-btn-modern {
  background: #f1f5f9;
  color: #475569;
  border: none;
  padding: 8px 18px;
  border-radius: 999px; /* 알약 형태 */
  font-size: 0.95rem;
  font-weight: 600;
  display: flex; align-items: center;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}
.like-btn-modern:hover { background: #e2e8f0; }

/* 좋아요 활성화 상태 (부드러운 레드) */
.like-btn-modern.liked {
  background: #fee2e2;
  color: #ef4444;
}
/* 하트 통통 튀는 애니메이션 */
.heart-beat { animation: pop 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275); }
@keyframes pop {
  0% { transform: scale(1); }
  50% { transform: scale(1.3); }
  100% { transform: scale(1); }
}

/* ===============================
   댓글 & 대댓글 섹션
================================= */
.comments-title { font-weight: 700; color: #0f172a; }

.comment-item-modern {
  padding: 1.25rem 0;
  border-bottom: 1px solid #f1f5f9;
}
.comment-item-modern:last-child { border-bottom: none; }

.comment-author { color: #0f172a; text-decoration: none; }
.comment-author:hover { color: #2563eb; }

/* 글쓴이 뱃지 */
.badge-pill.bg-author {
  background: #eff6ff;
  color: #2563eb;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 0.7rem;
  font-weight: 700;
}

.comment-actions { display: flex; align-items: center; }
.action-btn {
  background: none; border: none; font-size: 0.8rem; font-weight: 500;
  padding: 0; transition: color 0.2s;
}
.action-btn:hover { text-decoration: underline; }
.like-action { color: #94a3b8; }

.comment-content {
  font-size: 0.95rem; color: #334155; line-height: 1.6;
  margin-top: 4px;
}
:deep(.comment-content a) { color: #2563eb; text-decoration: underline; }

/* 대댓글 (Thread 스타일) */
.reply-list-modern {
  margin-top: 1rem;
  padding-left: 1rem;
  border-left: 2px solid #e2e8f0; /* 계층을 나타내는 부드러운 선 */
}
.reply-item-modern { margin-bottom: 1rem; }
.reply-item-modern:last-child { margin-bottom: 0; }
.reply-content { font-size: 0.9rem; color: #475569; line-height: 1.5; }

.btn-reply-toggle {
  background: none; border: none; font-size: 0.8rem; font-weight: 600;
  color: #64748b; padding: 0; margin-top: 8px; transition: color 0.2s;
}
.btn-reply-toggle:hover { color: #2563eb; }

/* ===============================
   입력 폼 (모던 텍스트 에어리어)
================================= */
.comment-write-card {
  background: #ffffff;
  border-radius: 16px;
  border: 1px solid #f1f5f9;
}

.modern-input {
  width: 100%;
  background: #f8fafc;
  border: 1px solid transparent;
  border-radius: 12px;
  padding: 12px 16px;
  font-size: 0.95rem;
  color: #0f172a;
  resize: none;
  transition: all 0.2s ease;
}
.modern-input:focus {
  outline: none;
  background: #ffffff;
  border-color: #bfdbfe;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}
.modern-input::placeholder { color: #94a3b8; }

.btn-submit-primary {
  background: #2563eb; color: #ffffff; border: none;
  padding: 10px 24px; border-radius: 12px; font-weight: 600; font-size: 0.95rem;
  transition: background 0.2s;
}
.btn-submit-primary:hover:not(:disabled) { background: #1d4ed8; }
.btn-submit-primary:disabled { background: #94a3b8; cursor: not-allowed; }

.btn-submit-soft {
  background: #eff6ff; color: #2563eb; border: none;
  padding: 6px 16px; border-radius: 8px; font-weight: 600; font-size: 0.85rem;
}
.btn-submit-soft:hover { background: #dbeafe; }

.btn-cancel-soft {
  background: #f1f5f9; color: #64748b; border: none;
  padding: 6px 16px; border-radius: 8px; font-weight: 600; font-size: 0.85rem;
}
.btn-cancel-soft:hover { background: #e2e8f0; color: #334155; }

/* ===============================
   로딩 스피너
================================= */
.modern-spinner {
  width: 40px; height: 40px;
  border: 3px solid #f3f3f3; border-top: 3px solid #2563eb;
  border-radius: 50%; animation: spin 1s linear infinite;
  margin: 0 auto;
}
@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }

/* 반응형 */
@media (max-width: 576px) {
  .post-title { font-size: 1.5rem; }
  .post-content { font-size: 1rem; }
  .modern-post-card { padding: 0.5rem; border-radius: 16px; border-left: none; border-right: none; }
}
</style>


