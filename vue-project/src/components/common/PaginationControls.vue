<template>
  <nav v-if="paging.totalPages > 1" aria-label="Page navigation">
    <ul class="pagination justify-content-center">
      <li class="page-item" :class="{ disabled: paging.currentPage === 0 }">
        <button class="page-link" @click="$emit('goToPage', paging.currentPage - 1)">이전</button>
      </li>

      <li class="page-item"
          v-for="i in paging.totalPages"
          :key="i"
          :class="{ active: i - 1 === paging.currentPage }">
        <button class="page-link" @click="$emit('goToPage', i - 1)">{{ i }}</button>
      </li>

      <li class="page-item" :class="{ disabled: paging.currentPage === paging.totalPages - 1 }">
        <button class="page-link" @click="$emit('goToPage', paging.currentPage + 1)">다음</button>
      </li>
    </ul>
  </nav>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue';

const props = defineProps({
  paging: {
    type: Object,
    required: true,
    default: () => ({ currentPage: 0, totalPages: 1, totalElements: 0, size: 10 })
  }
});

const emit = defineEmits(['goToPage']);
</script>

<style scoped>
/* 기본적인 Bootstrap pagination 스타일 사용 */
</style>