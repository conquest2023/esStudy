<template>
  <div class="search-bar d-flex align-items-center p-3 mb-3 rounded shadow-sm search-bg gap-2">
    <!-- 드롭다운 -->
    <div class="dropdown">
      <button
          class="btn btn-outline-secondary dropdown-toggle"
          type="button"
          data-bs-toggle="dropdown"
          aria-expanded="false">
        {{ searchLabel }}
      </button>
      <ul class="dropdown-menu">
        <li><a class="dropdown-item" href="#" @click.prevent="setMode('content')">제목+내용</a></li>
        <li><a class="dropdown-item" href="#" @click.prevent="setMode('user')">유저네임</a></li>
      </ul>
    </div>

    <i class="fa-solid fa-search search-icon" @click="goSearch" />
    <input
        v-model="keyword"
        @keyup.enter="goSearch"
        type="text"
        class="form-control border-0 shadow-none flex-grow-1 search-input"
        :placeholder="mode === 'user' ? '닉네임을 입력하세요' : '관심있는 내용을 검색해보세요!'"
    />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

const keyword = ref('')
const mode = ref('content') // 'content' | 'user'
const router = useRouter()

const searchLabel = computed(() => (mode.value === 'user' ? '유저네임' : '제목+내용'))

function setMode(m) {
  mode.value = m
}

function goSearch() {
  const query = keyword.value.trim()
  if (!query) return

  router.push({ path: '/search/view/content', query: { text: query, type: mode.value, page: 1 } })
}
</script>

<style scoped>
.search-bg {
  background-color: #ffffff;
  border: 1px solid #ddd;
  border-radius: 8px;
}
.search-icon {
  font-size: 1.2rem;
  color: #888;
  cursor: pointer;
}
.search-icon:hover { color: #555; }
body { background-color: #ffffff; }
.search-input::placeholder { color: #bbb; }
</style>
