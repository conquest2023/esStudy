<template>
  <div>
    <NavBar />
    <RightSidebar v-if="!hideLayout" />
    <!--    <DefaultLayout />-->
    <router-view />
  </div>
</template>
<script setup>
import { useUserStore } from '@/stores/user'
import { onMounted } from 'vue'
import { useSSE } from '@/composables/useSSE'
import DefaultLayout from "@/layouts/DefaultLayout.vue";
import { useRoute } from 'vue-router'
import NavBar from '@/components/common/Navbar.vue'
import RightSidebar from '@/components/sidebar/RightSidebar.vue'
import {computed} from "vue";

const route = useRoute()
const hideLayout = computed(() =>
    route.meta.hideLayout === true
)


const store = useUserStore()

onMounted(() => {
  if (store.token) useSSE(store.token)
})
if (store.token) useSSE(store.token)
</script>
<style>
:root {
  --radius-lg: 1rem;
}
</style>


