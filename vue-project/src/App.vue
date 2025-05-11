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
import { useSSE } from '@/composables/useSSE'
import DefaultLayout from "@/layouts/DefaultLayout.vue";
import { useRoute } from 'vue-router'
import NavBar from '@/components/common/NavBar.vue'
import RightSidebar from '@/components/sidebar/RightSidebar.vue'
import {computed} from "vue";

const route = useRoute()
const hideLayout = computed(() =>
    route.meta.hideLayout === true
)
const store = useUserStore()
if (store.token) useSSE(store.token)
</script>

