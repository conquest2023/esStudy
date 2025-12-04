<script setup>
import { toasts } from '@/composables/useToast';


const closeToast = (id) => {
  toasts.value = toasts.value.filter(t => t.id !== id);
}

const handleToastClick = (toast) => {
  if (toast.onClick) {
    toast.onClick();
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

.cursor-pointer {
  cursor: pointer;
}
</style>