<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/utils/api'

/* ────────────────────────────────
 *  state
 * ───────────────────────────── */
const router = useRouter()
const showMobileMenu   = ref(false)
const openDropdownIdx  = ref(null)          // 현재 열린 1depth 드롭다운(index)
const showNoti         = ref(false)
const showUserMenu     = ref(false)
const notifications    = ref([])

/* 사용자 상태 (예시) */
const loggedIn = ref(false)
const username = ref('')

onMounted(fetchAuth)

async function fetchAuth () {
  const token = localStorage.getItem('token')
  if (!token) return

  try {
    const { data } = await api.get('/info')
    loggedIn.value = !!data?.isLoggedIn
    username.value = data?.username || ''
  } catch (err) {
    console.error('[Navbar] auth fetch error', err)
  }
}

/* ─────────────  handlers  ───────────── */
function toggleDropdown (idx) {
  openDropdownIdx.value = openDropdownIdx.value === idx ? null : idx
}
function closeAllDropdown () {
  openDropdownIdx.value = null
  showUserMenu.value    = false
}
function toggleMobileMenu () {
  showMobileMenu.value = !showMobileMenu.value
}
function toggleNoti () {
  showNoti.value = !showNoti.value
}
function logout () {
  localStorage.removeItem('token')
  loggedIn.value = false
  username.value = ''
  router.push('/login')
}

/* 예시 메뉴 데이터 → 유지보수 용이 */
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
      {
        href: '/calendar',
        icon: 'fas fa-calendar-check text-warning',
        title: '캘린더',
        desc: '월별 일정 보기 & 전체 관리'
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
        desc: 'D‑DAY & 원서접수 일정 한눈에!'
      }
    ]
  }
]
</script>

<template>
  <nav class="navbar navbar-expand-lg bg-white shadow-sm fixed-top custom-navbar">
    <div class="container-fluid align-items-center flex-wrap">
      <!-- brand / tagline -->
      <router-link class="navbar-brand fw-bold text-primary me-2" to="/">Workly</router-link>
      <span class="tagline d-none d-md-inline text-muted me-4 flex-shrink-1 text-wrap">
      미래를 준비하는 사람들을 위한 사이트
      </span>

      <!-- mobile toggler -->
      <button class="navbar-toggler" type="button" @click="toggleMobileMenu">
        <span class="navbar-toggler-icon"></span>
      </button>

      <!-- main menu (collapsible) -->
      <div
          class="collapse navbar-collapse"
          :class="{ show: showMobileMenu }"
          @click.outside="closeAllDropdown"
      >
        <ul class="navbar-nav align-items-center gap-3">
          <!-- 1depth dropdowns -->
          <li
              v-for="(m, idx) in menus"
              :key="m.label"
              class="nav-item dropdown position-relative"
              :class="{ show: openDropdownIdx === idx }"
              @click.stop="toggleDropdown(idx)"
          >
            <a class="nav-link fw-bold dropdown-toggle" href="#" role="button">
              {{ m.label }}
            </a>
            <div
                class="dropdown-menu p-3 shadow rounded-4"
                :class="{ show: openDropdownIdx === idx }"
            >
              <template v-for="item in m.items" :key="item.href">
                <router-link class="dropdown-item fw-bold d-flex flex-column mb-2" :to="item.href">
                  <span class="d-flex align-items-center mb-1">
                    <i :class="item.icon + ' me-2'" />
                    {{ item.title }}
                  </span>
                  <small class="text-muted">{{ item.desc }}</small>
                </router-link>
              </template>
            </div>
          </li>
        </ul>
      </div>

      <div class="d-flex align-items-center ms-auto gap-3 position-relative">
        <!-- notification bell -->
        <div class="position-relative me-2">
          <i class="fas fa-bell fa-lg text-secondary" style="cursor:pointer" @click="toggleNoti" />
          <span v-if="notifications.length" class="badge bg-danger position-absolute top-0 start-100 translate-middle p-1">
      !
    </span>

          <!-- dropdown list -->
          <div class="notification-dropdown dropdown-menu shadow rounded p-3" :class="{ show: showNoti }">
            <ul class="list-unstyled mb-0 small" style="max-height:200px;overflow:auto;">
              <li v-for="n in notifications" :key="n.id" class="py-2 border-bottom">
                <router-link :to="`/search/view/feed/id?id=${n.feedUID}`" class="text-decoration-none">
                  {{ n.message }}
                </router-link>
              </li>
              <li v-if="!notifications.length" class="text-muted text-center py-3">알림이 없습니다</li>
            </ul>
            <div class="text-center mt-3">
              <button class="btn w-100 py-2 fw-semibold shadow-sm border-0 text-white"
                      style="background: linear-gradient(90deg, #4a90e2, #007aff); border-radius: 12px;"
                      @click="router.push('/notifications')">
                전체 보기
              </button>
            </div>
          </div>
        </div>

        <!-- 글쓰기 버튼 -->
        <button class="btn btn-danger btn-sm" @click="router.push('/search/view/feed/Form')">
          글쓰기
        </button>
        <!-- 로그인 / 유저 메뉴 -->
        <template v-if="!loggedIn">
          <button class="btn btn-outline-dark btn-sm" @click="router.push('/login')">로그인</button>
        </template>
        <template v-else>
          <div class="position-relative" @click.stop="showUserMenu = !showUserMenu">
            <button class="btn btn-outline-secondary btn-sm">
              <i class="fas fa-user-circle" />
            </button>
            <div class="dropdown-menu dropdown-menu-end mt-2" :class="{ show: showUserMenu }">
              <span class="dropdown-item-text text-secondary"><b>{{ username }}</b>님</span>
              <router-link class="dropdown-item" to="/search/view/feed/mypage">
                <i class="fas fa-user me-2" /> 마이페이지
              </router-link>
              <button class="dropdown-item text-danger" @click="logout">
                <i class="fas fa-sign-out-alt me-2" /> 로그아웃
              </button>
            </div>
          </div>
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
