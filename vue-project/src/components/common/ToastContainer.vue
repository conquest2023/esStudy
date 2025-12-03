<script setup>
import { toasts } from '@/composables/useToast';


const closeToast = (id) => {
  // 토스트를 닫는 로직. 라우팅 후에도 이 함수를 호출할 수 있습니다.
  toasts.value = toasts.value.filter(t => t.id !== id);
}

const handleToastClick = (toast) => {
  if (toast.onClick) {
    // 1. 라우팅 실행
    toast.onClick();
    // 2. (선택적) 라우팅 후 토스트 닫기
    closeToast(toast.id);
  }
}
</script>

<template>
  <div
      v-for="toast in toasts"
      :key="toast.id"
      class="toast show"
      role="alert"
      @click="handleToastClick(toast)"
  :class="{ 'cursor-pointer': toast.onClick }"
  >
  <div class="toast-header">
    <strong class="me-auto">🔔 알림</strong>
    <button type="button" class="btn-close" @click.stop="closeToast(toast.id)"></button>
  </div>
  <div class="toast-body">
    {{ toast.msg }}
  </div>
  </div>
</template>

<style scoped>
.toast {
  margin-bottom: 10px;
  pointer-events: auto;
}

/* 클릭 가능한 토스트에만 마우스 포인터 스타일을 적용 */
.cursor-pointer {
  cursor: pointer;
}
</style>