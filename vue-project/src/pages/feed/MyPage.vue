<template>
  <div class="container mt-4">

    <!-- 프로필 정보 -->
    <div class="container mt-5">
      <div class="profile-header d-flex justify-content-between align-items-center">
        <div>
          <h2 class="fw-bold text-primary">{{ username }}</h2>
          <p class="text-muted">아이디: <strong>{{ userId }}</strong></p>
          <p><i class="fas fa-bolt text-warning"></i> 활동 점수: <strong>{{ point }}</strong></p>
        </div>
      </div>

      <!-- 통계 -->
      <div class="profile-stats d-flex justify-content-around mt-4">
        <div class="stat-box" v-for="stat in stats" :key="stat.label">
          <span><i :class="stat.icon"></i> {{ stat.label }}</span>
          <h4 :class="stat.color + ' fw-bold'">{{ stat.value }}</h4>
        </div>
      </div>

      <!-- 탭 -->
      <div class="post-list mt-5">
        <ul class="nav nav-tabs">
          <li class="nav-item" v-for="tab in tabs" :key="tab.id">
            <button class="nav-link" :class="{ active: activeTab === tab.id }"
                    @click="activeTab = tab.id">
              {{ tab.label }}
            </button>
          </li>
        </ul>

        <div class="tab-content mt-4">
          <div v-show="activeTab === 'posts'">
            <PostCardList :items="feedList" type="feed"/>
          </div>

          <div v-show="activeTab === 'comments'">
            <CommentList :items="comments" />
          </div>

          <div v-show="activeTab === 'commentedPosts'">
            <PostCardList :items="commentedPosts" type="commented" />
          </div>

          <div v-show="activeTab === 'likedPosts'">
            <PostCardList :items="likedPosts" type="liked" />
          </div>

          <div v-show="activeTab === 'bookmarkedQuestions'">
            <BookmarkList :items="bookmarkedQuestions"/>
          </div>
        </div>

        <!-- 페이징 -->
        <Pagination :page="page" :totalPages="totalPages" @change="changePage"/>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { RouterLink } from 'vue-router'
import PostCardList from '@/components/mypage/PostCardList.vue'
import CommentList from '@/components/mypage/CommentList.vue'
import BookmarkList from '@/components/mypage/BookmarkList.vue'
import Pagination from '@/components/common/Pagination.vue'
import api from '@/utils/api'

const token = localStorage.getItem('token')

// 사용자 정보
const username = ref('')
const userId = ref('')
const point = ref(0)

// 탭
const activeTab = ref('posts')
const tabs = [
  { id: 'posts', label: '내가 작성한 게시글' },
  { id: 'comments', label: '작성한 댓글' },
  { id: 'commentedPosts', label: '댓글 단 게시물' },
  { id: 'likedPosts', label: '좋아요 한 글' },
  { id: 'bookmarkedQuestions', label: '북마크한 문제' }
]

// 데이터 저장
const feedList = ref([])
const comments = ref([])
const commentedPosts = ref([])
const likedPosts = ref([])
const bookmarkedQuestions = ref([])

// 페이징
const page = ref(0)
const size = ref(10)
const totalPages = ref(1)

// 통계
const stats = ref([
  { label: '게시물 수', icon: 'fas fa-pen', value: 0, color: 'text-primary' },
  { label: '댓글 수', icon: 'fas fa-comments', value: 0, color: 'text-success' },
  { label: '받은 좋아요', icon: 'fas fa-heart', value: 0, color: 'text-danger' },
  { label: '방문 수', icon: 'fas fa-eye', value: 0, color: 'text-info' }
])

// ----------------------------------
// API 요청 함수
// ----------------------------------

async function fetchUserInformation() {
  const { data } = await api.get('/user/information', auth())
  username.value = data.username
  userId.value = data.userId
}

async function fetchPoint() {
  const { data } = await api.get('/mypage/point', auth())
  point.value = data.point
}

async function fetchStats() {
  const { data } = await api.get('/mypage', auth())
  stats.value[0].value = data.feedCount
  stats.value[1].value = data.commentCount
  stats.value[2].value = data.like
  stats.value[3].value = data.visitCount
}

/* 각 탭별 데이터 로딩 */
async function loadPage() {
  if (activeTab.value === 'posts') await loadPosts()
  else if (activeTab.value === 'comments') await loadComments()
  else if (activeTab.value === 'commentedPosts') await loadCommentedPosts()
  else if (activeTab.value === 'likedPosts') await loadLikedPosts()
  else if (activeTab.value === 'bookmarkedQuestions') await loadBookmarks()
}

/* 1. 작성한 게시글 */
async function loadPosts() {
  const { data } = await api.get(`/mypage/post/paging?page=${page.value}&size=${size.value}`, auth())
  feedList.value = data.posts ?? []
  totalPages.value = data.totalPage ?? 1

  stats.value[0].value = data.totalCountPost ?? 0
}

async function loadComments() {
  const { data } = await api.get(`/mypage/comment/paging?page=${page.value}&size=${size.value}`, auth())
  comments.value = data.comments
  totalPages.value = data.totalPages ?? 1
  stats.value[1].value = data.totalCountComment ?? 0
}

async function loadCommentedPosts() {
  const { data } = await api.get(`/mypage/post/comment/paging?page=${page.value}&size=${size.value}`, auth())
  commentedPosts.value = data.all
  totalPages.value = data.totalPages ?? 1
}

async function loadLikedPosts() {
  const { data } = await api.get(`/mypage/feed/like/paging?page=${page.value}&size=${size.value}`, auth())
  likedPosts.value = data.likedFeedList
  totalPages.value = data.totalPages ?? 1
}

async function loadBookmarks() {
  const { data } = await api.get(`/get/daily/bookmark?page=${page.value}&size=${size.value}`, auth())
  bookmarkedQuestions.value = data.bookmarks
  totalPages.value = data.totalPages ?? 1
}

function auth() {
  return { headers: { Authorization: `Bearer ${token}` } }
}

watch(activeTab, () => {
  page.value = 0
  loadPage()
})
function changePage(newPage) {
  page.value = newPage
  loadPage()
}

onMounted(async () => {
  if (!token) return
  await fetchUserInformation()
  await fetchPoint()
  await loadPosts()
  await fetchStats()
})
</script>

<style scoped>

.btn-home {
  position: absolute;
  top: 80px;
  right: 20px;
  padding: 8px 15px;
  font-size: 14px;
  border-radius: 8px;
}
.stat-box {
  text-align: center;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 10px;
  box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.1);
}
</style>
