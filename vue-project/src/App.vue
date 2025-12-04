<template>

  <NavBar />

<!--  <div id="toastContainer"-->
<!--       class="position-fixed top-0 end-0 p-3"-->
<!--       style="z-index: 2500; pointer-events: none; width: 400px;">-->
<!--  <ToastContainer />-->
    <ToastHost />
<!--  </div>-->
  <RouterView v-slot="{ Component }">
    <keep-alive include="FeedList">
      <component :is="Component" />
    </keep-alive>
  </RouterView>
</template>
<script setup>
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import NavBar from '@/components/common/Navbar.vue'
import ToastContainer from "@/components/common/ToastContainer.vue";
import ToastHost from "@/components/common/ToastHost.vue";
const route = useRoute()
const store = useUserStore()
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
