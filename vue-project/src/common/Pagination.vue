<template>
  <nav v-if="totalPages > 1" class="my-3">
    <ul class="pagination justify-content-center mb-0">
      <!-- ⇤ 처음 -->
      <li :class="['page-item',{disabled:page===0}]" @click="emitPage(0)">
        <span class="page-link">&laquo;</span>
      </li>

      <!-- ‹ 10페이지 앞으로 -->
      <li v-if="startPage>0" class="page-item" @click="emitPage(startPage-1)">
        <span class="page-link">&lsaquo;</span>
      </li>

      <!-- 숫자 -->
      <li v-for="p in visiblePages" :key="p"
          :class="['page-item',{active:p===page}]" @click="emitPage(p)">
        <span class="page-link">{{ p+1 }}</span>
      </li>

      <!-- 10페이지 뒤로 › -->
      <li v-if="endPage<totalPages-1" class="page-item"
          @click="emitPage(endPage+1)">
        <span class="page-link">&rsaquo;</span>
      </li>

      <!-- ⇥ 마지막 -->
      <li :class="['page-item',{disabled:page===totalPages-1}]"
          @click="emitPage(totalPages-1)">
        <span class="page-link">&raquo;</span>
      </li>
    </ul>
  </nav>
</template>

<script setup>
import { computed, watch } from 'vue'


const props = defineProps({
  page: Number,
  totalPages: Number
})

const emit = defineEmits(['change'])
const MAX = 10
console.log(props)
const startPage = computed(() => Math.floor(props.page / MAX) * MAX)
const endPage = computed(() =>
    Math.min(props.totalPages /10 , startPage.value + MAX - 1)
)
const visiblePages = computed(() =>
    Array.from({length: endPage.value - startPage.value + 1},
        (_, i) => startPage.value + i)
)
function emitPage(p) {
  if (p !== props.page && p >= 0 && p < props.totalPages) emit('change', p)
}
</script>
