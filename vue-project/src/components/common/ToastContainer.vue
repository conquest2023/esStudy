<script setup>
import { ref, computed } from 'vue';
import { toasts, useToast } from '@/composables/useToast';

const { remove } = useToast();
const isExpanded = ref(false);

const toastCount = computed(() => toasts.value.length);
const currentToasts = computed(() => toasts.value);

const clearAll = () => {
  [...toasts.value].forEach(t => remove(t.id));
  isExpanded.value = false;
};
</script>

<template>
  <div class="toast-container p-3">

    <div v-if="toastCount >= 2 && !isExpanded" class="toast show summary-box">
      <div class="toast-header bg-dark text-white">
        <strong class="me-auto">🔔 알림</strong>
        <span class="badge bg-danger me-2">{{ toastCount }}</span>
        <button type="button" class="btn-close btn-close-white" @click="clearAll"></button>
      </div>
      <div class="toast-body d-flex justify-content-between align-items-center">
        <div class="text-truncate" style="max-width: 180px;">
          {{ currentToasts[0].msg }}
        </div>
        <button class="btn btn-sm btn-link p-0 ms-2" @click="isExpanded = true">더보기</button>
      </div>
    </div>

    <div v-else-if="toastCount > 0" class="toast-list">
      <div v-if="isExpanded" class="d-flex justify-content-between mb-2 align-items-center">
        <small class="text-muted">전체 알림</small>
        <div>
          <button class="btn btn-xs btn-secondary me-1" @click="isExpanded = false">접기</button>
          <button class="btn btn-xs btn-outline-danger" @click="clearAll">전체삭제</button>
        </div>
      </div>

      <TransitionGroup name="toast-fade">
        <div
            v-for="toast in currentToasts"
            :key="toast.id"
            class="toast show mb-2 shadow-sm"
            @click="handleToastClick(toast)"
        >
        </div>
      </TransitionGroup>
    </div>
  </div>
</template>

<style scoped>
.toast-container {
  position: fixed;
  top: 1rem;
  right: 1rem;
  z-index: 9999;
  width: 320px;
}
.summary-box {
  border-left: 4px solid #0d6efd;
}
.cursor-pointer { cursor: pointer; }
.btn-xs { padding: 2px 5px; font-size: 0.7rem; }

/* 애니메이션 */
.toast-fade-enter-active, .toast-fade-leave-active {
  transition: all 0.4s ease;
}
.toast-fade-enter-from { opacity: 0; transform: translateY(-20px); }
.toast-fade-leave-to { opacity: 0; transform: translateX(50px); }
</style>