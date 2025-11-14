<script setup>
import { ref, onMounted, onBeforeUnmount, watch, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useSSE } from '@/composables/useSSE'
import api from '@/utils/api'
import { useToast } from '@/composables/useToast'

const user = useUserStore()
const router = useRouter()
const { push } = useToast()

const notifications = ref([])
const unreadCount = computed(() =>
    notifications.value.filter(n => !n.isCheck).length
)
const hasUnread = computed(() =>
    notifications.value.some(n => !n.isCheck)
)

const openDropdownIdx = ref(null)
const showNoti = ref(false)
const showUserMenu = ref(false)
const isDarkMode = ref(localStorage.getItem('theme') === 'dark')

// 알림 패널 ref
const notiPanel = ref(null)

onMounted(() => {
  const token = localStorage.getItem('token')
  if (token) useSSE(token)
  applySavedTheme()
  user.fetchMe()
  fetchNotifications()

  // 전역 클릭 리스너 (알림/유저메뉴 닫기)
  window.addEventListener('click', handleGlobalClick)
})

onBeforeUnmount(() => {
  window.removeEventListener('click', handleGlobalClick)
})

async function fetchNotifications() {
  const token = localStorage.getItem('token')
  if (!token) return

  try {
    const { data } = await api.get('/notifications/recent', {
      headers: { Authorization: `Bearer ${token}` }
    })

    notifications.value = data || []
    const unread = notifications.value.filter(n => !n.isCheck).length
    if (unread > 0 && typeof push === 'function') {
      push('🔔 새로운 알림이 있습니다!')
    }
  } catch (e) {
    console.error('알림 불러오기 실패', e)
  }
}

window.addEventListener('storage', e => {
  if (e.key === 'token') user.fetchMe()
})

watch(
    notifications,
    n => {
      localStorage.setItem('notifications', JSON.stringify(n))
    },
    { deep: true }
)

function toggleDropdown(idx) {
  openDropdownIdx.value = openDropdownIdx.value === idx ? null : idx
}

function toggleNoti() {
  showNoti.value = !showNoti.value
}

function logout() {
  user.logout()
  router.push('/login')
}

function applySavedTheme() {
  const saved = localStorage.getItem('theme') || 'light'
  isDarkMode.value = saved === 'dark'
  const root = document.documentElement
  if (isDarkMode.value) {
    root.style.setProperty('--c-surface', '#1d1f24')
    root.style.setProperty('--c-text', '#e5e7eb')
    document.body.style.background = '#1d1f24'
    document.body.style.color = '#e5e7eb'
  } else {
    root.style.setProperty('--c-surface', '#ffffff')
    root.style.setProperty('--c-text', '#212529')
    document.body.style.background = '#ffffff'
    document.body.style.color = '#212529'
  }
}

function toggleTheme() {
  const next = isDarkMode.value ? 'light' : 'dark'
  localStorage.setItem('theme', next)
  applySavedTheme()
}

function formatDate(dateStr) {
  const date = new Date(dateStr)
  return `${date.getMonth() + 1}.${date.getDate()} ${String(
      date.getHours()
  ).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

async function markAsRead(ids) {
  const token = localStorage.getItem('token')
  if (!token) return
  try {
    await api.post('/notification/check', ids, {
      headers: { Authorization: `Bearer ${token}` }
    })
    notifications.value = notifications.value.map(n =>
        ids.includes(n.notificationId)
            ? { ...n, read: true, isCheck: true }
            : n
    )
  } catch (e) {
    console.error('읽음 처리 실패', e)
  }
}

async function deleteNotification(ids) {
  const token = localStorage.getItem('token')
  if (!token) return
  try {
    await api.post('/notification/delete', ids, {
      headers: { Authorization: `Bearer ${token}` }
    })
    notifications.value = notifications.value.filter(
        n => !ids.includes(n.notificationId)
    )
  } catch (e) {
    console.error('삭제 실패', e)
  }
}

/**
 * 🔥 전역 클릭 핸들러
 * - 알림 영역 밖 클릭 시 알림창 닫기
 * - 유저 메뉴도 바깥 클릭 시 닫고 싶으면 여기서 같이 처리 가능
 */
function handleGlobalClick(e) {
  // 알림창 닫기
  if (showNoti.value) {
    const bell = e.target.closest('.bell-trigger')
    const panel = notiPanel.value
    if (!bell && (!panel || !panel.contains(e.target))) {
      showNoti.value = false
    }
  }

  // 유저 메뉴 닫기 (원하면)
  if (showUserMenu.value) {
    const userMenuBtn = e.target.closest('.user-menu-trigger')
    const userMenuDropdown = e.target.closest('.user-menu-dropdown')
    if (!userMenuBtn && !userMenuDropdown) {
      showUserMenu.value = false
    }
  }

  // 상단 대분류 드롭다운 닫기 (원하면)
  if (openDropdownIdx.value !== null) {
    const navArea = e.target.closest('.top-nav-menu-area')
    if (!navArea) {
      openDropdownIdx.value = null
    }
  }
}

const menus = [
  {
    label: '취업 사이트',
    items: [
      {
        href: '/search/view/feed/list/job',
        icon: 'fas fa-briefcase text-primary',
        title: 'IT 취업 정보',
        desc: '실시간 채용 공고 & 커뮤니티 피드'
      },
      {
        href: '/site',
        icon: 'fas fa-link text-info',
        title: '취업 사이트',
        desc: '원스톱 취업 준비를 위한 사이트 모음'
      }
    ]
  },
  {
    label: '일정 관리',
    items: [
      {
        href: '/todo',
        icon: 'fas fa-check text-success',
        title: '투두 & D-Day 매니저',
        desc: '자격증/취업 일정 관리 & 리마인더'
      }
    ]
  },
  {
    label: '자격증',
    items: [
      {
        href: '/search/view/question',
        icon: 'fas fa-file-alt text-primary',
        title: '문제은행',
        desc: '기출/모의 문제로 실전 연습!'
      },
      {
        href: '/certificate/data',
        icon: 'fas fa-book text-info',
        title: '자격증 자료',
        desc: '시험과목, 기출요약, 공부법 가이드'
      },
      {
        href: '/certificate/list',
        icon: 'fas fa-chart-bar text-success',
        title: '자격증 분석',
        desc: '합격률/응시율 기반 자격증 추천'
      },
      {
        href: '/certificate/calendar',
        icon: 'fas fa-calendar-alt text-warning',
        title: '자격증 캘린더',
        desc: 'D-DAY & 원서접수 일정 한눈에!'
      }
    ]
  },
  {
    label: '면접',
    items: [
      {
        href: '/interview/govinterview',
        icon: 'fas fa-user-shield text-primary',
        title: '공무원',
        desc: '실제 면접 기출 문제로 철저 대비!'
      },
      {
        href: '/certificate/data',
        icon: 'fas fa-laptop-code text-info',
        title: 'IT',
        desc: '기술면접/코테까지 완벽 준비!'
      },
      {
        href: '/interview/priinterview',
        icon: 'fas fa-building text-success',
        title: '사기업',
        desc: '기업별 면접 포인트와 합격 전략'
      },
      {
        href: '/certificate/calendar',
        icon: 'fas fa-landmark text-warning',
        title: '공기업',
        desc: 'NCS부터 인성까지 완벽 분석'
      }
    ]
  }
]
</script>

<template>
  <nav class="okky-navbar navbar fixed-top bg-white shadow-sm px-3">
    <div class="container-fluid d-flex justify-content-between align-items-center">
      <!-- 로고 -->
      <router-link to="/" class="navbar-brand text-primary fw-bold">Workly</router-link>

      <!-- 태그라인 -->
      <span class="tagline d-none d-md-inline text-muted me-4 flex-shrink-1 text-wrap">
        미래를 준비하는 사람들을 위한 사이트
      </span>

      <!-- 상단 메뉴 -->
      <ul class="nav d-none d-md-flex gap-3 top-nav-menu-area">
        <li class="nav-item dropdown" v-for="(m, idx) in menus" :key="idx">
          <a class="nav-link fw-semibold dropdown-toggle" href="#" @click.prevent="toggleDropdown(idx)">
            {{ m.label }}
          </a>
          <div class="dropdown-menu rounded shadow-sm small p-2" :class="{ show: openDropdownIdx === idx }">
            <router-link
                v-for="item in m.items"
                :key="item.href"
                class="dropdown-item d-flex flex-column"
                :to="item.href"
            >
              <span class="fw-bold">{{ item.title }}</span>
              <small class="text-muted">{{ item.desc }}</small>
            </router-link>
          </div>
        </li>
      </ul>

      <!-- 오른쪽 영역: 알림, 테마, 글쓰기, 유저 -->
      <div class="d-flex align-items-center ms-auto gap-3 position-relative">
        <!-- 알림 -->
        <div class="position-relative me-2">
          <i
              class="fas fa-bell fa-lg bell-trigger"
              :class="hasUnread ? 'text-primary bell-has-unread' : 'text-secondary'"
              style="cursor:pointer"
              @click.stop="toggleNoti"
          />
          <span
              v-if="unreadCount > 0"
              class="badge bg-danger position-absolute top-0 start-100 translate-middle rounded-circle"
              style="font-size: 0.7rem; min-width: 20px; height: 20px; display: flex; align-items: center; justify-content: center;"
          >
            {{ unreadCount }}
          </span>

          <div
              ref="notiPanel"
              class="notification-dropdown shadow rounded-4 p-0"
              :class="{ show: showNoti }"
          >
            <!-- 헤더 -->
            <div class="noti-header d-flex justify-content-between align-items-center px-3 py-2 border-bottom">
              <div class="d-flex flex-column">
                <span class="fw-semibold">
                  🔔 알림
                  <span v-if="unreadCount > 0" class="badge bg-danger ms-2">
                    새로운 {{ unreadCount }}건
                  </span>
                </span>
                <small class="text-muted">최근 7일 이내 알림만 표시됩니다</small>
              </div>
              <button
                  v-if="unreadCount > 0"
                  class="btn btn-link btn-sm text-muted text-decoration-none"
                  @click="markAsRead(notifications.filter(n => !n.isCheck).map(n => n.notificationId))"
              >
                모두 읽음
              </button>
            </div>

            <!-- 알림 리스트 -->
            <ul v-if="notifications.length > 0" class="list-unstyled mb-0 small noti-list">
              <li
                  v-for="n in notifications"
                  :key="n.notificationId"
                  :class="[
                  'noti-item d-flex justify-content-between align-items-start px-3 py-2 border-bottom',
                  { 'noti-unread': !n.isCheck }
                ]"
              >
                <div class="flex-grow-1 me-2">
                  <router-link
                      :to="'/post/' + n.postId"
                      class="text-decoration-none d-block"
                      @click="markAsRead([n.notificationId])"
                  >
                    <div class="d-flex align-items-center mb-1">
                      <span class="noti-dot me-2" v-if="!n.isCheck"></span>
                      <span class="fw-semibold text-dark text-truncate">
                        {{ n.username }}님이 "{{ n.message }}" 작성하셨습니다
                      </span>
                    </div>
                    <div class="small text-muted">{{ formatDate(n.createdAt) }}</div>
                  </router-link>
                </div>

                <!-- 버튼 그룹 -->
                <div class="btn-group btn-group-sm ms-1 flex-shrink-0">
                  <button
                      class="btn btn-outline-success btn-sm"
                      @click="markAsRead([n.notificationId])"
                      title="읽음 처리"
                  >
                    <i class="fas fa-eye" />
                  </button>
                  <button
                      class="btn btn-outline-danger btn-sm"
                      @click="deleteNotification([n.notificationId])"
                      title="삭제"
                  >
                    <i class="fas fa-trash" />
                  </button>
                </div>
              </li>
            </ul>

            <!-- 비어있을 때 -->
            <div
                v-else
                class="d-flex flex-column align-items-center justify-content-center text-muted py-4 small"
            >
              <i class="fas fa-inbox mb-2" style="font-size: 1.8rem;"></i>
              <div>최근 7일 이내 알림이 없습니다</div>
            </div>

            <!-- 전체 보기 버튼 -->
            <div class="text-center px-3 py-2 border-top">
              <button
                  class="btn w-100 py-2 fw-semibold shadow-sm border-0 text-white"
                  style="background:linear-gradient(90deg,#4a90e2,#007aff);border-radius:12px;"
                  @click="router.push('/notifications')"
              >
                전체 알림 보기
              </button>
            </div>
          </div>
        </div>

        <!-- 테마 토글 -->
        <button class="btn btn-outline-dark btn-sm" @click="toggleTheme">
          <i :class="isDarkMode ? 'fas fa-sun' : 'fas fa-moon'"></i>
        </button>

        <!-- 글쓰기 -->
        <button class="btn btn-danger btn-sm" @click="router.push('/search/view/feed/Form')">
          글쓰기
        </button>

        <!-- 로그인 / 유저 메뉴 -->
        <template v-if="user.isLoggedIn">
          <div class="position-relative user-menu-trigger" @click.stop="showUserMenu = !showUserMenu">
            <button class="btn btn-outline-secondary btn-sm">
              <i class="fas fa-user-circle" />
            </button>
            <div class="dropdown-menu dropdown-menu-end mt-2 user-menu-dropdown" :class="{ show: showUserMenu }">
              <span class="dropdown-item-text text-secondary">
                <b>{{ user.username }}</b>님
              </span>
              <router-link class="dropdown-item" to="/mypage">
                <i class="fas fa-user me-2" /> 마이페이지
              </router-link>
              <button class="dropdown-item text-danger" @click="logout">
                <i class="fas fa-sign-out-alt me-2" /> 로그아웃
              </button>
            </div>
          </div>
        </template>
        <template v-else>
          <button class="btn btn-outline-dark btn-sm" @click="router.push('/login')">
            로그인
          </button>
        </template>
      </div>
    </div>
  </nav>
</template>

<style scoped>
.okky-navbar {
  height: 64px;
  background: white;
  border-bottom: 1px solid #e1e4e8;
  display: flex;
  align-items: center;
  z-index: 2000;
}

/* 브랜드 */
.okky-navbar .navbar-brand {
  font-size: 1.4rem;
  font-weight: 800;
  letter-spacing: -0.5px;
  color: #2563eb !important;
}

/* 태그라인 */
.okky-navbar .tagline {
  font-size: 0.85rem;
  opacity: 0.7;
}

/* 상단 메뉴 */
.okky-navbar .nav-link {
  font-weight: 600;
  color: #444;
  padding: 0.6rem 0.2rem;
  position: relative;
}

.okky-navbar .nav-link:hover {
  color: #2563eb;
}
.user-menu-dropdown {
  right: 0;          /* 버튼 오른쪽에 붙고 */
  left: auto;        /* 왼쪽 고정 해제 */
  transform: translateX(-5%);  /* 전체를 왼쪽으로 40% 정도 이동 */
}
/* 드롭다운 (카드형 메뉴) */
.dropdown-menu {
  border-radius: 14px !important;
  padding: 12px !important;
  min-width: 260px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08) !important;
  border: 1px solid #f1f3f5;
}

.dropdown-item {
  padding: 10px 12px !important;
  border-radius: 10px;
}

.dropdown-item:hover {
  background: #f1f4ff;
}

/* 알림 종 아이콘 */
.fa-bell {
  transition: color .25s, transform .25s;
}

.fa-bell:hover {
  color: #2563eb !important;
  transform: scale(1.05);
}

/* 빨간 알림 동그라미 */
.badge {
  font-weight: 600;
  padding: 4px 6px;
}

/* 알림 드롭다운 패널 */
.notification-dropdown {
  width: 360px;
  background: white;
  border-radius: 18px;
  overflow: hidden;
  position: absolute;
  right: 0;
  top: 48px;
  border: 1px solid #e5e8eb;
  box-shadow: 0 10px 30px rgba(0,0,0,0.12);
  animation: fadeSlide .25s ease-out;
  display: none;
}

.notification-dropdown.show {
  display: block;
}

/* 애니메이션 */
@keyframes fadeSlide {
  from { opacity: 0; transform: translateY(-5px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 알림 헤더 */
.noti-header {
  background: #f9fafb;
}

/* 알림 리스트 */
.noti-list {
  max-height: 260px;
  overflow-y: auto;
}

/* 개별 알림 */
.noti-item {
  border-bottom: 1px solid #f1f3f5;
  padding: 12px 14px;
  transition: background .15s ease;
}

.noti-item:hover {
  background: #f5f7ff;
}

/* 미확인 알림 시 강조 */
.noti-unread {
  background: #edf3ff;
}

/* 작은 빨간 점 */
.noti-dot {
  width: 8px;
  height: 8px;
  background: #ff3b30;
  border-radius: 50%;
}

/* 유저 메뉴 */
.dropdown-menu.show {
  display: block;
}

.btn-outline-secondary {
  border-radius: 10px !important;
}

/* 테마 버튼 */
.btn-outline-dark {
  border-radius: 10px !important;
  padding: 6px 10px;
}

/* 글쓰기 버튼(강조) */
.btn-danger {
  padding: 6px 14px;
  background: linear-gradient(135deg, #ff4b4b, #ff2626);
  border-radius: 10px;
  border: none;
  font-weight: 600;
}

.btn-danger:hover {
  background: linear-gradient(135deg, #ff3b3b, #ff1111);
}

/* 모바일 대응 */
@media (max-width: 768px) {
  .tagline {
    display: none;
  }

  .okky-navbar {
    height: 58px;
  }
}
</style>
