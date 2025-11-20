<template>

  <NavBar />
  <div id="toastContainer"
       class="position-fixed top-0 end-0 p-3"
       style="z-index: 2500; pointer-events: none; width: 400px;">
  <ToastContainer />
  </div>
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
import ToastContainer from "@/components/common/ToastContainer.vue";

const route = useRoute()
const store = useUserStore()
const sb = useSidebarStore()
const hideLayout = computed(() => route.meta.hideLayout === true)
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
