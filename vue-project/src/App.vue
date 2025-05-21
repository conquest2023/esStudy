<template>

  <NavBar />
  <div id="toastContainer"
       class="position-fixed top-0 end-0 p-3"
       style="z-index: 2000; pointer-events: none; width: 320px;"></div>

  <!-- 사이드바: 로그인 상태 + 레이아웃이 숨김이 아닐 때만 -->
<!--  <RightSidebar v-if="store.token && !hideLayout" />-->
  <RouterView v-slot="{ Component }">
    <keep-alive include="FeedList">
      <component :is="Component" />
    </keep-alive>
  </RouterView>
</template>

<script setup>
import {computed,onMounted} from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useSidebarStore } from '@/stores/sidebar'
import NavBar from '@/components/common/Navbar.vue'
import RightSidebar from '@/components/sidebar/RightSidebar.vue'

const route = useRoute()
const store = useUserStore()
const sb = useSidebarStore()
// /* ▣ 레이아웃 감춤 여부 계산 */
// const hideLayout = ref(false)
// watch(
//     route,
//     async () => {
//       hideLayout.value = route.meta.hideLayout === true
//       await nextTick()
//
//       if (!hideLayout.value && store.token) {
//         await sb.loadStatic()
//         await sb.loadLive()
//         initSSE()
//       }
//     },
//     { immediate: true }
// )
const hideLayout = computed(() => route.meta.hideLayout === true)
// watch(
//     () => route.name,
//     async name => {
//       if (name === 'FeedList') {
//         await sb.loadLive()
//       }
//     }
// )

// onMounted(async () => {
//   if (!store.token || hideLayout.value) return
//   await sb.loadStatic()
//   await sb.loadLive()
//   initSSE()
// })
// watch(route, (to) => {
//   if (to.name === 'FeedList' && !hideLayout.value) {
//     sb.loadLive()
//   }
// })
</script>

<style>
:root {
  --radius-lg: 1rem;
  --c-text-muted-dark: rgba(255, 255, 255, 0.85);
  --c-meta-stats-dark: rgba(255, 255, 255, 0.8);
}
#toastContainer .toast {
  pointer-events: auto;
  opacity: 0.95;
  transition: opacity 0.3s ease;
}
</style>
