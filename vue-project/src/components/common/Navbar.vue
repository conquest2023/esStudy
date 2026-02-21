<script setup>
import { ref, onMounted, onBeforeUnmount, watch, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useSSE } from '@/composables/useSSE'
import api from '@/utils/api'
import { useToast } from '@/composables/useToast'

const user = useUserStore()
const router = useRouter()
const { push, toasts } = useToast()

const notifications = ref([])
const unreadCount = computed(() => notifications.value.filter(n => !n.isCheck).length)
const hasUnread = computed(() => notifications.value.some(n => !n.isCheck))

const isExternal = (href) => /^https?:\/\//i.test(String(href || '').trim())

const openDropdownIdx = ref(null)
const showNoti = ref(false)
const showUserMenu = ref(false)
const isDarkMode = ref(localStorage.getItem('theme') === 'dark')

const notiPanel = ref(null)

onMounted(() => {
  const token = localStorage.getItem('token')
  if (token) useSSE(token)

  fetchNotifications()
  user.fetchMe()
  applySavedTheme()

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
    const fetchedNotifications = data || []
    notifications.value = fetchedNotifications

    let unreadNotifications = fetchedNotifications.filter(n => !n.isCheck)
    unreadNotifications.sort((a, b) => b.createdAt.localeCompare(a.createdAt))
  } catch (e) {
    console.error('알림 불러오기 실패', e)
  }
}

window.addEventListener('storage', e => {
  if (e.key === 'token') user.fetchMe()
})

watch(
    notifications,
    n => localStorage.setItem('notifications', JSON.stringify(n)),
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
  return `${date.getMonth() + 1}.${date.getDate()} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

async function markAsRead(ids) {
  const token = localStorage.getItem('token')
  if (!token) return
  try {
    await api.post('/notification/check', ids, {
      headers: { Authorization: `Bearer ${token}` }
    })
    notifications.value = notifications.value.map(n =>
        ids.includes(n.notificationId) ? { ...n, read: true, isCheck: true } : n
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
    notifications.value = notifications.value.filter(n => !ids.includes(n.notificationId))
  } catch (e) {
    console.error('삭제 실패', e)
  }
}

function handleGlobalClick(e) {
  // 알림창 닫기
  if (showNoti.value) {
    const bell = e.target.closest('.bell-trigger')
    const panel = notiPanel.value
    if (!bell && (!panel || !panel.contains(e.target))) {
      showNoti.value = false
    }
  }

  // 유저 메뉴 닫기
  if (showUserMenu.value) {
    const userMenuBtn = e.target.closest('.user-menu-trigger')
    const userMenuDropdown = e.target.closest('.user-menu-dropdown')
    if (!userMenuBtn && !userMenuDropdown) {
      showUserMenu.value = false
    }
  }

  // 상단 드롭다운 닫기
  if (openDropdownIdx.value !== null) {
    const navArea = e.target.closest('.top-nav-menu-area')
    if (!navArea) openDropdownIdx.value = null
  }
}

const menus = [
  {
    label: '취업 사이트',
    items: [
      { href: '/search/view/feed/list/job', icon: 'fas fa-briefcase text-primary', title: 'IT 취업 정보', desc: '실시간 채용 공고 & 커뮤니티 피드' },
      { href: '/site', icon: 'fas fa-link text-info', title: '취업 사이트', desc: '원스톱 취업 준비를 위한 사이트 모음' }
    ]
  },
  {
    label: '일정 관리',
    items: [
      { href: '/todo', icon: 'fas fa-check text-success', title: '투두 & D-Day 매니저', desc: '자격증/취업 일정 관리 & 리마인더' }
    ]
  },
  {
    label: '영어',
    items: [
      { href: '/practice', icon: 'fas fa-file-alt text-primary', title: '영어문제', desc: '기출/모의 문제로 실전 연습!' }
    ]
  },
  {
    label: '책찾기',
    items: [
      {
        href: 'https://lib.workly.info',
        external: true,
        icon: 'fas fa-book-open text-primary',
        title: '도서관',
        desc: '지역 도서관 검색 / 소장 도서 확인'
      }
    ]
  },
  {
    label: '면접',
    items: [
      { href: '/interview/govinterview', icon: 'fas fa-user-shield text-primary', title: '공무원', desc: '실제 면접 기출 문제로 철저 대비!' },
      // { href: '/certificate/data', icon: 'fas fa-laptop-code text-info', title: 'IT', desc: '기술면접/코테까지 완벽 준비!' },
      { href: '/interview/priinterview', icon: 'fas fa-building text-success', title: '사기업', desc: '기업별 면접 포인트와 합격 전략' },
      // { href: '/certificate/calendar', icon: 'fas fa-landmark text-warning', title: '공기업', desc: 'NCS부터 인성까지 완벽 분석' }
    ]
  }
]
</script>
<template>
  <nav class="modern-navbar fixed-top px-3">
    <div class="container-fluid d-flex justify-content-between align-items-center h-100">

      <div class="d-flex align-items-center gap-3">
        <router-link to="/" class="brand-logo">Workly</router-link>
        <span class="tagline d-none d-md-inline">미래를 준비하는 사람들을 위한 공간</span>
      </div>

      <ul class="nav d-none d-md-flex gap-1 top-nav-menu-area">
        <li class="nav-item position-relative" v-for="(m, idx) in menus" :key="idx">
          <a class="nav-link modern-nav-link" href="#" @click.prevent="toggleDropdown(idx)">
            {{ m.label }}
            <i class="fas fa-chevron-down ms-1" :class="{ 'rotate-180': openDropdownIdx === idx }"></i>
          </a>

          <Transition name="dropdown-fade">
            <div v-show="openDropdownIdx === idx" class="modern-dropdown shadow-lg">
              <template v-for="item in m.items" :key="item.href">
                <a
                    v-if="item.external || isExternal(item.href)"
                    class="dropdown-item flex-column align-items-start"
                    :href="item.href" target="_blank" rel="noopener noreferrer"
                    @click="openDropdownIdx = null"
                >
                  <div class="d-flex align-items-center justify-content-between w-100">
                    <span class="item-title"><i :class="item.icon" class="me-2"></i>{{ item.title }}</span>
                    <i class="fas fa-arrow-up-right-from-square text-muted small"></i>
                  </div>
                  <small class="item-desc">{{ item.desc }}</small>
                </a>

                <router-link
                    v-else
                    class="dropdown-item flex-column align-items-start"
                    :to="item.href"
                    @click="openDropdownIdx = null"
                >
                  <span class="item-title"><i :class="item.icon" class="me-2"></i>{{ item.title }}</span>
                  <small class="item-desc">{{ item.desc }}</small>
                </router-link>
              </template>
            </div>
          </Transition>
        </li>
      </ul>

      <div class="d-flex align-items-center ms-auto gap-3 position-relative action-area">

        <div class="position-relative">
          <button class="icon-btn bell-trigger" @click.stop="toggleNoti">
            <i class="fas fa-bell" :class="hasUnread ? 'text-primary' : 'text-secondary'"></i>
            <span v-if="unreadCount > 0" class="noti-badge">{{ unreadCount }}</span>
          </button>

          <Transition name="dropdown-fade">
            <div v-show="showNoti" ref="notiPanel" class="noti-panel shadow-lg">
              <div class="noti-header">
                <div>
                  <span class="fw-bold fs-6">알림</span>
                  <span v-if="unreadCount > 0" class="badge-pill bg-danger-soft ms-2">{{ unreadCount }}건</span>
                </div>
                <button v-if="unreadCount > 0" class="text-btn" @click="markAsRead(notifications.filter(n => !n.isCheck).map(n => n.notificationId))">모두 읽음</button>
              </div>

              <ul v-if="notifications.length > 0" class="noti-list">
                <li v-for="n in notifications" :key="n.notificationId" :class="['noti-item', { 'unread': !n.isCheck }]">
                  <router-link :to="'/post/' + n.postId" class="noti-link" @click="markAsRead([n.notificationId])">
                    <div class="noti-content">
                      <span v-if="!n.isCheck" class="unread-dot"></span>
                      <p class="mb-1 text-truncate-2">
                        <template v-if="n.message.includes('좋아요')">{{ n.message }}</template>
                        <template v-else><b>{{ n.username }}</b>님이 "{{ n.message }}" 라고 작성하셨습니다.</template>
                      </p>
                      <span class="noti-time">{{ formatDate(n.createdAt) }}</span>
                    </div>
                  </router-link>
                  <button class="delete-btn" @click.stop="deleteNotification([n.notificationId])"><i class="fas fa-times"></i></button>
                </li>
              </ul>
              <div v-else class="empty-noti">최근 알림이 없습니다.</div>

              <div class="noti-footer">
                <button class="btn-primary-soft w-100" @click="router.push('/notifications')">전체 알림 보기</button>
              </div>
            </div>
          </Transition>
        </div>

        <button class="icon-btn" @click="toggleTheme">
          <i :class="isDarkMode ? 'fas fa-sun' : 'fas fa-moon'"></i>
        </button>
        <button class="btn-write d-none d-md-inline-flex" @click="router.push('/search/view/feed/Form')">
          <i class="fas fa-pen me-1"></i> 글쓰기
        </button>

        <template v-if="user.isLoggedIn">
          <div class="position-relative user-menu-trigger" @click.stop="showUserMenu = !showUserMenu">
            <div class="profile-avatar">{{ user.username.charAt(0).toUpperCase() }}</div>

            <Transition name="dropdown-fade">
              <div v-show="showUserMenu" class="modern-dropdown user-dropdown shadow-lg">
                <div class="px-3 py-2 border-bottom mb-2">
                  <span class="d-block fw-bold text-dark">{{ user.username }}님</span>
                  <span class="small text-muted">환영합니다!</span>
                </div>
                <router-link class="dropdown-item" to="/mypage"><i class="fas fa-user"></i> 마이페이지</router-link>
                <button class="dropdown-item text-danger" @click="logout"><i class="fas fa-sign-out-alt"></i> 로그아웃</button>
              </div>
            </Transition>
          </div>
        </template>
        <template v-else>
          <button class="btn-login" @click="router.push('/login')">로그인</button>
        </template>
      </div>
    </div>
  </nav>
</template>

<style scoped>
/* 1. 글래스모피즘 네비게이션 */
.modern-navbar {
  height: 64px;
  background: rgba(255, 255, 255, 0.85); /* 반투명 흰색 */
  backdrop-filter: blur(12px); /* 배경 블러 처리 */
  -webkit-backdrop-filter: blur(12px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  z-index: 2000;
  transition: background 0.3s;
}

/* 로고 및 텍스트 */
.brand-logo {
  font-size: 1.4rem;
  font-weight: 800;
  color: #2563eb;
  text-decoration: none;
  letter-spacing: -0.5px;
}
.tagline {
  font-size: 0.85rem;
  color: #94a3b8;
  font-weight: 500;
}

/* 2. 네비게이션 링크 */
.modern-nav-link {
  color: #475569;
  font-weight: 600;
  padding: 8px 12px;
  border-radius: 8px;
  transition: all 0.2s ease;
}
.modern-nav-link:hover {
  background: #f1f5f9;
  color: #0f172a;
}
.fa-chevron-down {
  font-size: 0.7rem;
  transition: transform 0.2s ease;
}
.rotate-180 { transform: rotate(180deg); }

/* 3. 모던 드롭다운 (애플 스타일) */
.modern-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  margin-top: 8px;
  background: #ffffff;
  border-radius: 16px;
  border: 1px solid #f1f5f9;
  padding: 8px;
  min-width: 260px;
  box-shadow: 0 10px 25px rgba(0,0,0,0.08);
}
.user-dropdown { left: auto; right: 0; min-width: 200px; }

.dropdown-item {
  border-radius: 10px;
  padding: 10px 12px;
  color: #334155;
  transition: all 0.15s;
}
.dropdown-item:hover {
  background: #f8fafc;
  color: #2563eb;
}
.item-title { font-weight: 600; font-size: 0.95rem; }
.item-desc { color: #94a3b8; font-size: 0.75rem; margin-top: 2px; }

/* 4. 아이콘 버튼 & 알림 */
.icon-btn {
  background: transparent;
  border: none;
  font-size: 1.2rem;
  color: #64748b;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}
.icon-btn:hover { background: #f1f5f9; color: #0f172a; }

.noti-badge {
  position: absolute;
  top: 0; right: 0;
  background: #ef4444;
  color: white;
  font-size: 0.65rem;
  font-weight: bold;
  padding: 2px 5px;
  border-radius: 10px;
  border: 2px solid #fff;
}

/* 알림 패널 */
.noti-panel {
  position: absolute;
  top: 48px; right: -10px;
  width: 340px;
  background: #fff;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  overflow: hidden;
}
.noti-header { display: flex; justify-content: space-between; padding: 16px; border-bottom: 1px solid #f1f5f9; }
.bg-danger-soft { background: #fee2e2; color: #ef4444; padding: 2px 8px; border-radius: 12px; font-size: 0.75rem; }
.noti-list { max-height: 350px; overflow-y: auto; padding: 0; margin: 0; list-style: none; }
.noti-item { position: relative; padding: 12px 16px; border-bottom: 1px solid #f8fafc; display: flex; align-items: flex-start; transition: background 0.2s; }
.noti-item:hover { background: #f8fafc; }
.noti-item.unread { background: #eff6ff; }
.unread-dot { width: 8px; height: 8px; background: #3b82f6; border-radius: 50%; display: inline-block; margin-right: 6px; }
.noti-link { flex: 1; text-decoration: none; color: #334155; }
.text-truncate-2 { display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; font-size: 0.9rem; }
.noti-time { font-size: 0.75rem; color: #94a3b8; }
.delete-btn { background: none; border: none; color: #cbd5e1; cursor: pointer; }
.delete-btn:hover { color: #ef4444; }
.empty-noti { padding: 40px; text-align: center; color: #94a3b8; font-size: 0.9rem; }
.noti-footer { padding: 12px; border-top: 1px solid #f1f5f9; }

/* 유저 아바타 */
.profile-avatar {
  width: 36px; height: 36px;
  background: linear-gradient(135deg, #60a5fa, #2563eb);
  color: white;
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-weight: bold; cursor: pointer;
  box-shadow: 0 2px 4px rgba(37,99,235,0.2);
}

/* 액션 버튼들 */
.btn-write {
  background: #0f172a; color: white; border: none;
  padding: 6px 14px; border-radius: 20px; font-size: 0.85rem; font-weight: 600;
  transition: transform 0.1s, box-shadow 0.2s;
}
.btn-write:hover { transform: translateY(-1px); box-shadow: 0 4px 12px rgba(15,23,42,0.2); }
.btn-primary-soft { background: #eff6ff; color: #2563eb; border: none; padding: 8px; border-radius: 10px; font-weight: 600; }
.btn-primary-soft:hover { background: #dbeafe; }
.text-btn { background: none; border: none; color: #64748b; font-size: 0.8rem; cursor: pointer; }
.text-btn:hover { color: #2563eb; text-decoration: underline; }

/* 애니메이션 */
.dropdown-fade-enter-active, .dropdown-fade-leave-active { transition: all 0.2s cubic-bezier(0.16, 1, 0.3, 1); }
.dropdown-fade-enter-from, .dropdown-fade-leave-to { opacity: 0; transform: translateY(-10px) scale(0.95); }

@keyframes fadeSlide {
    from { opacity: 0; transform: translateY(-6px); }
    to   { opacity: 1; transform: translateY(0); }
  }
@media (max-width: 420px) {
  .notification-dropdown {
    right: calc(8px + env(safe-area-inset-right, 0px));
    width: calc(100vw - 16px - env(safe-area-inset-right, 0px) - env(safe-area-inset-left, 0px));
    border-radius: 14px;
  }
}



.toast-wrapper {
  position: fixed;
  top: 74px;
  right: 20px;
  z-index: 3000;
  display: flex;
  flex-direction: column;
  gap: 10px;
  max-width: 320px;
}

.custom-toast {
  background-color: var(--c-surface, #ffffff);
  color: var(--c-text, #212529);
  padding: 12px 18px;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
  border-left: 5px solid #2563eb;
  display: flex;
  align-items: center;
  justify-content: space-between;
  opacity: 0.95;
  transition: all 0.3s ease;
  transform: translateX(0);
  animation: slideIn 0.3s ease-out;
}

.custom-toast.clickable {
  cursor: pointer;
  border-color: #2563eb; /* 클릭 가능한 토스트 강조 */
}

.custom-toast.clickable:hover {
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
}

.toast-message {
  font-weight: 500;
  line-height: 1.4;
  padding-right: 10px;
}

.toast-icon {
  font-size: 0.8em;
  color: #2563eb;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(100%);
  }
  to {
    opacity: 0.95;
    transform: translateX(0);
  }
}
</style>