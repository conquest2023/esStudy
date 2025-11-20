<script setup>
import { toasts } from '@/composables/useToast'; // 💡 직접 import 할 수도 있습니다.

// const { toasts } = useToast() // 또는 이렇게 가져와도 됩니다.

const closeToast = (id) => {
  // 5초 타임아웃 전에 사용자가 직접 닫을 수 있도록 제거 로직 추가
  toasts.value = toasts.value.filter(t => t.id !== id);
}
</script>

<template>
  <div v-for="toast in toasts" :key="toast.id" class="toast show" role="alert">
    <div class="toast-header">
      <strong class="me-auto">🔔 알림</strong>
      <button type="button" class="btn-close" @click="closeToast(toast.id)"></button>
    </div>
    <div class="toast-body">
      {{ toast.msg }}
    </div>
  </div>
</template>

<style scoped>
.toast {
  margin-bottom: 10px; /* 토스트 간의 간격 */
  pointer-events: auto; /* 상위 div에서 pointer-events: none을 설정했으므로 여기서 다시 auto로 설정 */
}
</style>