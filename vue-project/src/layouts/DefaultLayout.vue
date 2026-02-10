<script setup>
import RightSidebar      from '@/components/sidebar/RightSidebar.vue'
import MobileBottomNav   from '@/components/mobile/MobileBottomNav.vue'
import { useRouter }     from 'vue-router'
import { useUserStore }  from "@/stores/user"

const store = useUserStore()
const router = useRouter()


const isExternal = (path) => /^https?:\/\//i.test(String(path || '').trim())

const handleNavClick = (item) => {
  const path = String(item?.path || '').trim()
  if (!path) return

  if (isExternal(path) || item?.isExternal) {
    window.open(path, '_blank', 'noopener,noreferrer')
    return
  }

  router.push(path)
}

const navItems = [
  {
    path: '/',
    icon: 'fas fa-search-location fa-lg', // 책찾기에 적합한 아이콘
    label: '책찾기',
    children: [
      {
        path: 'https://lib.workly.info',
        label: '도서관',
        isExternal: true
      },
    ]
  },
  {
    icon: 'fas fa-user-tie fa-lg', // 취업/비즈니스에 적합한 아이콘
    label: '취업',
    children: [
      {path: '/search/view/feed/list/job', label: 'IT 정보'},
      {path: '/site', label: '취업 사이트'}
    ]
  },
  {
    icon: 'fas fa-calendar-check fa-lg', // 일정 관리에 적합한 아이콘
    label: '일정',
    children: [
      {path: '/todo', label: '투두 & D-Day'},
    ]
  },
  {
    icon: 'fas fa-language fa-lg', // 영어 학습에 적합한 아이콘
    label: '영어',
    children: [
      {path: '/practice', label: '영어 문제'},
    ]
  },
  {
    icon: 'fas fa-comments-dollar fa-lg', // 면접의 가치를 나타내는 아이콘
    label: '면접',
    children: [
      {
        path: '/interview/govinterview',
        label: '공무원',
        icon: 'fas fa-user-shield text-primary',
        desc: '실제 면접 기출 문제로 철저 대비!'
      },
      // {
      //   path: '/interview/priinterview', // 경로 오타 수정 (/ 추가)
      //   label: 'IT',
      //   icon: 'fas fa-laptop-code text-info',
      //   desc: '기술면접/코테까지 완벽 준비!'
      // },
      {
        path: '/interview/priinterview',
        label: '사기업',
        icon: 'fas fa-building text-success',
        desc: '기업별 면접 포인트와 합격 전략'
      },
      // {
      //   path: '/interview/public',
      //   label: '공기업',
      //   icon: 'fas fa-landmark text-warning',
      //   desc: 'NCS부터 인성까지 완벽 분석'
      // }
    ]
  }
]
</script>

<template>
  <div class="page-wrap">
    <main class="main-wrap">
      <router-view/>
    </main>

    <RightSidebar class="desktop-only"/>
  </div>

  <MobileBottomNav
      :navItems="navItems"
      @nav-click="handleNavClick"
      @fab="router.push('/search/view/feed/Form')"
  />
</template>

<style>
.page-wrap {
  display: flex;
  justify-content: center;
  gap: 30px;
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 20px;
}

.main-wrap {
  flex: 1 1 0;
  min-width: 0;
  padding-top: 70px;
  padding-bottom: 60px;
}

.desktop-only {
  display: block;
}

/* 모바일 뷰포트 설정 */
@media (max-width: 900px) {
  .desktop-only {
    display: none;
  }
}
</style>