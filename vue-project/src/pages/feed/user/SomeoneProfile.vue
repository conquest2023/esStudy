<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute, RouterLink } from 'vue-router'
import PostCardList from '@/components/mypage/PostCardList.vue'
import CommentList from '@/components/mypage/CommentList.vue'
import { watch } from 'vue'

const router = useRouter()
const route = useRoute()
const token = localStorage.getItem('token')

const user = ref({})
const feedList = ref([])
const comments = ref([])
const commentedPosts = ref([])
const likedPosts = ref([])
const activeTab = ref('posts')

const stats = ref([
  { label: '게시물 수', icon: 'fas fa-pen', value: 0, color: 'text-primary' },
  { label: '댓글 수', icon: 'fas fa-comments', value: 0, color: 'text-success' },
  { label: '받은 좋아요', icon: 'fas fa-heart', value: 0, color: 'text-danger' },
  { label: '방문 수', icon: 'fas fa-eye', value: 0, color: 'text-info' }
])

const tabs = [
  { id: 'posts', label: '작성한 게시글' },
  { id: 'comments', label: '작성한 댓글' },
  { id: 'commentedPosts', label: '댓글 단 게시글' },
  { id: 'likedPosts', label: '좋아요한 글' }
]

async function fetchFullProfileData() {
  const headers = { Authorization: `Bearer ${token}` }
  const username = route.params.username
  const res = await fetch(`/api/someone/profile/full?username=${encodeURIComponent(username)}&page=0&size=10`, { headers })
  const data = await res.json()

  user.value = data.user
  stats.value[0].value = data.user.feedCount
  stats.value[1].value = data.user.commentCount
  stats.value[2].value = data.user.like
  stats.value[3].value = data.user.visitCount

  feedList.value = data.feedList || []
  comments.value = data.commentList || []
  commentedPosts.value = data.commentAndFeed || []
  likedPosts.value = data.likedList || []
}

onMounted(() => {
  if (!token) {
    alert("로그인을 하셔야 이용가능합니다")
    router.push("/login")
  }
  fetchFullProfileData()
})
</script>

<template>
  <div class="container mt-4">
    <div class="d-flex justify-content-end">
      <RouterLink to="/search/view/feed?index=board" class="btn btn-secondary btn-home">
        <i class="fas fa-home"></i> 메인으로 돌아가기
      </RouterLink>
    </div>

    <div class="container mt-5">
      <div class="profile-header d-flex justify-content-between align-items-center">
        <div class="profile-info d-flex align-items-center">
          <div>
            <h2 class="fw-bold text-primary">{{ user.username }}</h2>
            <p><i class="fas fa-bolt"></i> 활동 점수: <strong>{{ user.point }}</strong></p>
          </div>
        </div>
      </div>

      <div class="profile-stats d-flex justify-content-around mt-4">
        <div class="stat-box" v-for="stat in stats" :key="stat.label">
          <span><i :class="stat.icon"></i> {{ stat.label }}</span>
          <h4 :class="stat.color + ' fw-bold'">{{ stat.value }}</h4>
        </div>
      </div>

      <div class="post-list mt-5">
        <ul class="nav nav-tabs" role="tablist">
          <li class="nav-item" v-for="tab in tabs" :key="tab.id">
            <button class="nav-link" :class="{ active: activeTab === tab.id }" @click="activeTab = tab.id">
              {{ tab.label }}
            </button>
          </li>
        </ul>

        <div class="tab-content mt-3">
          <div v-show="activeTab === 'posts'">
            <PostCardList :items="feedList" type="feed" />
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
        </div>
      </div>
    </div>
  </div>
</template>

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
