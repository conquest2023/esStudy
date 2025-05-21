<!-- src/layouts/DefaultLayout.vue -->
<script setup>
import Navbar            from '@/components/common/Navbar.vue'
import RightSidebar      from '@/components/sidebar/RightSidebar.vue'
import NotificationToast from '@/components/common/NotificationToast.vue'
import MobileBottomNav   from '@/components/mobile/MobileBottomNav.vue'
import { useRouter }     from 'vue-router'
import { ref, watch, nextTick ,onMounted,computed} from 'vue'
import {useSidebarStore} from "@/stores/sidebar";
import {useUserStore} from "@/stores/user";
const sb = useSidebarStore()
const store = useUserStore()
// onMounted(async () => {
//     await sb.loadStatic()
//     await sb.loadLive()
// })

const router = useRouter()
let sseInit = false
// function initSSE () {
//   const token=localStorage.getItem("token")
//   // if (!sseInit) {
//   useSSE(token)
//   // sseInit = true
//   // }
// }
// onMounted(() => {
//   initSSE ()
// })
// watch(() => localStorage.getItem("token"), (token) => {
//   if (token) {
//     useSSE(token)
//   }
// })
const navItems = [
  { path:'/', icon:'fas fa-home fa-lg', label:'홈' },
  {
    icon:'fas fa-briefcase fa-lg', label:'취업',
    children:[
      { path:'/search/view/feed/list/job', label:'IT 정보' },
      { path:'/site', label:'취업 사이트' }
    ]
  },
  {
    icon:'fas fa-check fa-lg', label:'일정',
    children:[
      { path:'/todo', label:'투두 & D-Day' },
      { path:'/calendar', label:'캘린더' }
    ]
  },
  {
    icon:'fas fa-certificate fa-lg', label:'자격증',
    children:[
      { path:'/search/view/question', label:'문제 은행' },
      { path:'/certificate/data',   label:'자격증 자료' },
      { path:'/certificate/list',   label:'자격증 분석' },
      { path:'/certificate/calendar', label:'자격증 일정' }
    ]
  }
]
</script>

<template>
  <Navbar />
  <NotificationToast />

  <div class="page-wrap">
    <main class="main-wrap"><router-view /></main>
    <RightSidebar class="desktop-only" />
  </div>

  <MobileBottomNav
      :navItems="navItems"
      @fab="router.push('/search/view/feed/Form')"
  />
</template>

<style>
.page-wrap{display:flex;justify-content:center;gap:30px;max-width:1280px;margin:0 auto;padding:0 20px}
.main-wrap {
  flex: 1 1 0;
  min-width: 0;
  padding-top: 70px;

  padding-bottom: 60px;
}

.desktop-only{display:block}
@media(max-width:900px){.desktop-only{display:none}}
</style>
