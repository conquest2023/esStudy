<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { useRouter }      from 'vue-router'
import api                from '@/utils/api'
import { useUserStore }   from '@/stores/user'
import { useSSE } from '@/composables/useSSE'
const user = useUserStore()
const notifications = computed(() => user.notifications)
// const unreadCount   = computed(() =>
//     notifications.value.filter(n => !n.read).length)
const unreadCount = computed(() =>
    notifications.value.filter(n => !n.read).length
)
const router          = useRouter()
const openDropdownIdx = ref(null)
const showNoti        = ref(false)
const showUserMenu    = ref(false)

onMounted(() => {
  applySavedTheme()
  user.fetchMe()
  //   const token = localStorage.getItem('token')
  //   if (token) useSSE(token)
  // })
})
function applySavedTheme () {
  const saved = localStorage.getItem('theme') || 'light'
  isDarkMode.value = (saved === 'dark')

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
/* 탭/창 간 토큰 동기화 */
window.addEventListener('storage', e => {
  if (e.key === 'token') user.fetchMe()
})
watch(notifications,
    n => localStorage.setItem('notifications', JSON.stringify(n)),
    { deep: true })

function toggleDropdown(idx){
  openDropdownIdx.value = openDropdownIdx.value === idx ? null : idx
}
function toggleNoti(){
  showNoti.value = !showNoti.value
  if(showNoti.value) user.markAllRead()
}
// async function fetchNotifications(){
//   const token = localStorage.getItem('token')
//   if(!token) return
//   try{
//     const { data } = await api.get('/notifications/all',
//         { headers:{ Authorization:`Bearer ${token}` }})
//     user.notifications = data || []
//   }catch(e){ console.error('알림 불러오기 실패', e) }
// }
function logout(){
  user.logout()
  router.push('/login')
}
function updateTodoAlert(message) {
  const todoAlert = document.getElementById("todo-alert")
  if (todoAlert) todoAlert.innerText = message
}

function addFeedNotification (jsonMessage) {
  let parsed
  try {
    parsed = (typeof jsonMessage === 'string') ? JSON.parse(jsonMessage) : jsonMessage
  } catch (e) {
    console.error('JSON 파싱 오류:', e, jsonMessage)
    return
  }
  const { feedUID, message } = parsed
  notifications.value = [
    { id: Date.now(), feedUID, message, read: false },
    ...notifications.value
  ]
  console.log('👉 파싱 결과:', parsed)
  showToast(message, feedUID)
}

function showToast (message, feedUID) {
  const container = document.getElementById('toastContainer')
  if (!container) return

  const toast = document.createElement('div')
  toast.className = 'toast align-items-center bg-dark border-0 shadow mb-2 show'
  toast.setAttribute('role', 'alert')
  toast.setAttribute('aria-live', 'assertive')
  toast.setAttribute('aria-atomic', 'true')
  toast.innerHTML = `
    <div class="d-flex">
      <div class="toast-body fw-bold text-white">
        🔔 <a href="/search/view/feed/id/${feedUID}" class="text-white text-decoration-underline">${message}</a>
      </div>
      <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
    </div>`
  container.appendChild(toast)

  setTimeout(() => {
    toast.classList.remove('show')
    toast.addEventListener('transitionend', () => toast.remove())
  }, 5000)
}

const isDarkMode = ref(localStorage.getItem('theme') === 'dark')

function toggleTheme () {
  isDarkMode.value = !isDarkMode.value
  localStorage.setItem('theme', isDarkMode.value ? 'dark' : 'light')
  applySavedTheme()
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
        title: '투두 & D‑Day 매니저',
        desc: '자격증/취업 일정 관리 & 리마인더'
      },
      // {
      //   href: '/calendar',
      //   icon: 'fas fa-calendar-check text-warning',
      //   title: '캘린더',
      //   desc: '월별 일정 보기 & 전체 관리'
      // }
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
        desc: 'D‑DAY & 원서접수 일정 한눈에!'
      }
    ]
  }
]
</script>

<template>
  <nav class="okky-navbar navbar fixed-top bg-white shadow-sm px-3">
    <div class="container-fluid d-flex justify-content-between align-items-center">

      <router-link to="/" class="navbar-brand text-primary fw-bold">Workly</router-link>
      <span class="tagline d-none d-md-inline text-muted me-4 flex-shrink-1 text-wrap">
      미래를 준비하는 사람들을 위한 사이트
      </span>

      <ul class="nav d-none d-md-flex gap-3">
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


      <div class="d-flex align-items-center ms-auto gap-3 position-relative">
        <!-- 알림 -->
        <div class="position-relative me-2">
          <i class="fas fa-bell fa-lg text-secondary" style="cursor:pointer" @click="toggleNoti" />
          <span v-if="unreadCount.value > 0" class="badge bg-danger position-absolute top-0 start-100 translate-middle p-1">!</span>

          <div class="notification-dropdown dropdown-menu shadow rounded p-3" :class="{ show: showNoti }">
            <ul v-if="notifications.some(n => !n.read)" class="list-unstyled mb-0 small" style="max-height:200px;overflow:auto;">
              <li
                  v-for="n in notifications.filter(n => !n.read)"
                  :key="n.id"
                  class="py-2 border-bottom"
              >
                <router-link
                    :to="'/search/view/feed/id/' + n.feedUID"
                    class="text-decoration-none"
                    @click="n.read = true"
                >
                  {{ n.message }}
                </router-link>
              </li>
            </ul>
            <ul v-else class="list-unstyled mb-0 small text-muted text-center py-3">
              <li>알림이 없습니다</li>
            </ul>
            <div class="text-center mt-3">
              <button class="btn w-100 py-2 fw-semibold shadow-sm border-0 text-white"
                      style="background:linear-gradient(90deg,#4a90e2,#007aff);border-radius:12px;"
                      @click="router.push('/notifications')">
                전체 보기
              </button>
            </div>
          </div>
        </div>

        <button class="btn btn-outline-dark btn-sm" @click="toggleTheme">
          <i :class="isDarkMode ? 'fas fa-sun' : 'fas fa-moon'"></i>
        </button>
        <button class="btn btn-danger btn-sm" @click="router.push('/search/view/feed/Form')">
          글쓰기
        </button>
        <!-- 로그인 / 유저 메뉴 -->
        <template v-if="user.isLoggedIn">
          <!-- 로그인 상태 UI -->
          <div class="position-relative" @click.stop="showUserMenu = !showUserMenu">
            <button class="btn btn-outline-secondary btn-sm">
              <i class="fas fa-user-circle" />
            </button>
            <div class="dropdown-menu dropdown-menu-end mt-2" :class="{ show: showUserMenu }">
              <span class="dropdown-item-text text-secondary"><b>{{ user.username }}</b>님</span>
              <router-link class="dropdown-item" to="/mypage"><i class="fas fa-user me-2" /> 마이페이지</router-link>
              <button class="dropdown-item text-danger" @click="user.logout"><i class="fas fa-sign-out-alt me-2" /> 로그아웃</button>
            </div>
          </div>
        </template>
        <template v-else>
          <!-- 비로그인 UI -->
          <button class="btn btn-outline-dark btn-sm" @click="router.push('/login')">로그인</button>
        </template>
      </div>
    </div>
  </nav>
</template>

<style scoped>
.custom-navbar {
  font-size: 1.05rem;
  z-index: 1040; /* over sidebar */
}
.tagline {
  white-space: nowrap;
}
#boardTab .nav-link.active {
  background-color: #007bff !important;
  color: white !important;
  border-radius: 12px;
  font-weight: bold;
  padding: 6px 16px;
}
.notification-dropdown {
  min-width: 240px;
  max-width: 300px;
  max-height: 320px;
  overflow: auto;
  right: 0;
}
.okky-navbar {
  font-size: 0.95rem;
  z-index: 1040;
  border-bottom: 1px solid #eee;
  transition: box-shadow 0.2s ease-in-out;
}

.okky-navbar .nav-link {
  color: #333;
  transition: color 0.2s;
}
.okky-navbar .nav-link:hover {
  color: #007bff;
}

.dropdown-menu {
  min-width: 200px;
  transition: all 0.2s;
  border-radius: 12px;
}

.dropdown-item:hover {
  background-color: #f8f9fa;
  border-radius: 8px;
}

.btn-sm {
  font-size: 0.85rem;
  border-radius: 8px;
}

.badge-noti {
  position: absolute;
  top: -4px;
  right: -6px;
  background-color: red;
  color: white;
  font-size: 10px;
  padding: 2px 5px;
  border-radius: 50%;
}
  .dropdown-menu {
    top: 100% !important;
    bottom: auto !important;
    transform: translateY(4px);
    z-index: 1050;
  }
  .dropdown-menu.show {
    top: 100% !important;
    transform: translateY(4px);
    right: 0;
    left: auto;
    z-index: 1050;
  }

@media (max-width: 768px) {
  .tagline {
    display: none !important;
  }
}

</style>
