<template>
  <div class="search-bar d-flex align-items-center p-3 mb-3 rounded shadow-sm search-bg">
    <i class="fa-solid fa-search" @click="goSearch" />
    <input
        v-model="keyword"
        @keyup.enter="goSearch"
        type="text"
        class="form-control border-0 shadow-none flex-grow-1 search-input"
        placeholder="관심있는 내용을 검색해보세요!"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const keyword = ref('')
const router = useRouter()

function goSearch() {
  const query = keyword.value.trim()
  if (query) {
    router.push({ path: '/search/view/content', query: { text: query } })
  }
}
</script>

<style scoped>
.search-bg {
  background-color: #f9f9f9; /* 검색 화면과 통일된 색상 */
}

.search-icon {
  font-size: 1.2rem;
  color: #888;
  cursor: pointer;
}

.search-icon:hover {
  color: #555;
}
.search-bg {
  background-color: #ffffff; /* or #f8f9fa 등 페이지 전체와 맞춰주세요 */
  border: 1px solid #ddd;
  border-radius: 8px;
}
body {
  background-color: #ffffff; /* 검색창과 동일하게 */
}
.search-input::placeholder {
  color: #bbb;
}
</style>
