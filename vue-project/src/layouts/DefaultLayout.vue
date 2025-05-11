<!-- src/layouts/DefaultLayout.vue -->
<template>
  <div>
    <Navbar />
    <NotificationToast />

    <!-- ① flex 컨테이너 -->
    <div class="page-wrap">
      <!-- ② 메인 컨텐츠(길이 자동) -->
      <main class="main-wrap">
        <router-view />
      </main>

      <RightSidebar class="desktop-only" />
    </div>

    <MobileBottomNav class="mobile-only" />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import Navbar from '@/components/common/Navbar.vue'
import RightSidebar from '@/components/sidebar/RightSidebar.vue'
import NotificationToast from '@/components/common/NotificationToast.vue'
import MobileBottomNav from '@/components/mobile/MobileBottomNav.vue'

const isMobile = computed(() => window.innerWidth <= 768)
</script>
<style scoped>
.page-wrap {               /* ① 페이지 전체 영역 */
  display: flex;
  justify-content: center; /* 메인 폭이 작으면 가운데 정렬 */
  gap: 30px;               /* 메인‑사이드 간격 */
  width: 100%;
  max-width: 1280px;       /* container-custom 와 비슷한 폭 */
  margin: 0 auto;          /* 가운데 */
  padding: 0 20px;
}

.main-wrap {
  padding-top: 70px; /* 🔧 fixed된 navbar 높이만큼 여백 확보 */
}
.main-wrap {               /* ② 메인(피드) */
  flex: 1 1 0;             /* 남는 공간 전부 차지 */
  min-width: 0;            /* flex shrink 시 오버플로 방지 */
}

.desktop-only { display: block; }
.mobile-only  { display: none;  }
</style>