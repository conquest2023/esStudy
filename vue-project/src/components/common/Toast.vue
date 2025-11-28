<template>
  <div
      v-for="t in toasts"
      :key="t.id"
      class="toast-item"
      @click="go(t)"
  >
    {{ t.msg }}
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

function go(t) {
  if (t.routePath) {
    router.push(t.routePath)
  }
}
</script>

<style>
.toast-item {
  cursor: pointer;
}
</style>
