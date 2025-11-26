<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute, RouterLink } from 'vue-router'
import PostCardList from '@/components/mypage/PostCardList.vue'
import CommentList from '@/components/mypage/CommentList.vue'
import PaginationControls from "@/components/common/PaginationControls.vue";

const router = useRouter()
const route = useRoute()
const token = localStorage.getItem('token')

const user = ref({ value: null, point: 0 })
const feedList = ref([])
const comments = ref([])
const commentedPosts = ref([])
const likedPosts = ref([])
const activeTab = ref('posts')

// ⭐ [NEW] 탭별 페이징 정보를 저장할 객체. 초기값은 posts 탭에 맞춰 설정
const pagingInfo = ref({
  posts: { currentPage: 0, totalPages: 1, totalElements: 0, size: 10 },
  comments: { currentPage: 0, totalPages: 1, totalElements: 0, size: 10 },
  commentedPosts: { currentPage: 0, totalPages: 1, totalElements: 0, size: 10 },
  likedPosts: { currentPage: 0, totalPages: 1, totalElements: 0, size: 10 }
});
// 현재 활성화된 탭의 페이징 정보에 쉽게 접근하는 Computed 속성 (필요 시 추가)
// const currentPaging = computed(() => pagingInfo.value[activeTab.value]);


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

// --- API 호출 유틸리티 함수 ---
async function fetchSomeoneData(endpoint, headers, username, page, size) {
  const params = new URLSearchParams({ username, page, size });
  if (size === null) {
    delete params.size;
  }
  const res = await fetch(`/api${endpoint}?${params.toString()}`, { headers });
  if (res.ok) {
    return await res.json();
  }
  return null;
}
async function fetchProfilePoint() {
  const headers = { Authorization: `Bearer ${token}` };
  const username = route.params.username;
  const pointData = await fetchSomeoneData('/someone/point', headers, username, null, null);

  if (pointData && pointData.point !== undefined) {
    user.value.point = pointData.point;
  }
  user.value.value = username;
}

// ⭐ [UPDATED] 페이지 번호를 파라미터로 받아 API 호출 및 페이징 정보 업데이트
async function fetchPostsData(page = 0) {
  if (!token) return;
  const headers = { Authorization: `Bearer ${token}` };
  const username = route.params.username;
  const size = pagingInfo.value.posts.size; // 저장된 size 사용

  const postsData = await fetchSomeoneData('/someone', headers, username, page, size);

  if (postsData) {
    feedList.value = postsData.content || [];
    // 페이징 정보 업데이트
    pagingInfo.value.posts.currentPage = postsData.page;
    pagingInfo.value.posts.totalPages = postsData.totalPages;
    pagingInfo.value.posts.totalElements = postsData.totalElements;

    // 통계 업데이트
    stats.value[0].value = postsData.totalElements || 0;
  }
}

// ⭐ [UPDATED] 댓글 데이터도 페이지 번호를 받아 처리
async function fetchCommentsData(page = 0) {
  if (!token) return;
  const headers = { Authorization: `Bearer ${token}` };
  const username = route.params.username;
  const size = pagingInfo.value.comments.size;

  const commentsData = await fetchSomeoneData('/someone/comment/paging', headers, username, page, size);

  if (commentsData) {
    comments.value = commentsData.comments || [];
    // 페이징 정보 업데이트
    pagingInfo.value.comments.currentPage = page; // API 응답에 page가 없으면 요청 page 사용
    pagingInfo.value.comments.totalPages = commentsData.totalPage;
    pagingInfo.value.comments.totalElements = commentsData.totalCountComment;

    // 통계 업데이트
    stats.value[1].value = commentsData.totalCountComment || 0;
  }
}

// ⭐ [UPDATED] 댓글 단 게시글 데이터도 페이지 번호를 받아 처리
async function fetchCommentedPostsData(page = 0) {
  if (!token) return;
  const headers = { Authorization: `Bearer ${token}` };
  const username = route.params.username;
  const size = pagingInfo.value.commentedPosts.size;

  const commentedPostsData = await fetchSomeoneData('/someone/post/comment/paging', headers, username, page, size);

  if (commentedPostsData) {
    commentedPosts.value = commentedPostsData.all || [];
    // 페이징 정보 업데이트
    pagingInfo.value.commentedPosts.currentPage = page;
    pagingInfo.value.commentedPosts.totalPages = commentedPostsData.totalPage;
    pagingInfo.value.commentedPosts.totalElements = commentedPostsData.totalCountComment;

    // (통계 업데이트는 추후 필요 시 추가)
  }
}


// ⭐ [NEW] 페이지 이동 처리 함수
const goToPage = (page) => {
  // 요청하는 페이지가 유효한 범위(0 <= page < totalPages) 내에 있는지 확인
  const currentPaging = pagingInfo.value[activeTab.value];
  if (page < 0 || page >= currentPaging.totalPages) {
    return; // 유효하지 않은 페이지 요청 무시
  }

  switch (activeTab.value) {
    case 'posts':
      fetchPostsData(page);
      break;
    case 'comments':
      fetchCommentsData(page);
      break;
    case 'commentedPosts':
      fetchCommentedPostsData(page);
      break;
    case 'likedPosts':
      // 좋아요한 글 로직 (추후 구현)
      break;
  }
};


// --- 초기 로딩 및 탭 변경 감시 로직 (기존 유지) ---

async function fetchFullProfileData() {
  if (!token) {
    alert("로그인을 하셔야 이용가능합니다")
    router.push("/login")
    return
  }

  await fetchProfilePoint();

  // 초기 로딩 시 posts 탭 데이터만 가져옵니다.
  if (activeTab.value === 'posts') {
    await fetchPostsData();
  }
}

// 탭 변경 시 해당 탭의 데이터 로딩 (페이징 정보 초기화 후 데이터 로딩)
watch(activeTab, (newTab) => {
  // 탭이 변경되면 항상 0페이지부터 시작
  const currentPaging = pagingInfo.value[newTab];
  if (currentPaging.totalPages === 1 && currentPaging.totalElements > 0) {
    // 데이터는 있지만, 총 페이지 수가 1이면 호출 방지 (데이터 이미 로딩 가정)
    return;
  }

  // 데이터가 없거나 1페이지 이상일 경우만 호출 (혹은 간단히 무조건 호출)
  goToPage(0);
}, { immediate: false });

onMounted(() => {
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
            <h2 class="fw-bold text-primary">{{ user.value }}</h2>
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
            <PostCardList :items="feedList" type="feed"/>
            <div class="mt-4">
              <PaginationControls
                  :paging="pagingInfo.posts"
                  @go-to-page="goToPage"
              />
            </div>
          </div>

          <div v-show="activeTab === 'comments'">
            <CommentList :items="comments"/>
            <div class="mt-4">
              <PaginationControls
                  :paging="pagingInfo.comments"
                  @go-to-page="goToPage"
              />
            </div>
          </div>

          <div v-show="activeTab === 'commentedPosts'">
            <PostCardList :items="commentedPosts" type="commented"/>
            <div class="mt-4">
              <PaginationControls
                  :paging="pagingInfo.commentedPosts"
                  @go-to-page="goToPage"
              />
            </div>
          </div>

          <div v-show="activeTab === 'likedPosts'">
            <PostCardList :items="likedPosts" type="liked"/>
            <div class="mt-4">
              <PaginationControls
                  :paging="pagingInfo.likedPosts"
                  @go-to-page="goToPage"
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* (스타일은 원본 그대로 유지) */
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