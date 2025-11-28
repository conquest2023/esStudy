<template>
  <teleport to="body">
    <div class="toast-container" @click="onToastContainerClick">
      <div v-for="t in toasts" :key="t.id" class="toast-item">
        <!-- 여기에서 v-html 로 메시지를 렌더 -->
        <div class="toast-body" v-html="t.msg"></div>
      </div>
    </div>
  </teleport>
</template>

<script setup>
import { toasts } from '@/composables/useToast'
import { useRouter } from 'vue-router'
const router = useRouter()

function onToastContainerClick(e) {
  const a = e.target.closest('a.toast-link')
  if (a) {
    e.preventDefault()
    router.push(a.getAttribute('href'))
  }
}
</script>

<style scoped>
.toast-container { position: fixed; right: 16px; bottom: 16px; display:flex; flex-direction:column; gap:12px; z-index: 2147483647; }
.toast-item { background:#fff; border-radius:12px; padding:12px 14px; box-shadow:0 8px 24px rgba(0,0,0,.15); min-width:260px; }
.toast-body a.toast-link { text-decoration: underline; cursor: pointer; }
</style>
