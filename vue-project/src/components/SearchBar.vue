<template>
  <div class="search-bar d-flex align-items-center p-3 mb-3 rounded shadow-sm search-bg gap-2">
    <div class="dropdown">
      <button
          class="btn btn-outline-secondary dropdown-toggle"
          type="button"
          data-bs-toggle="dropdown"
          aria-expanded="false">
        {{ searchLabel }}
      </button>
      <ul class="dropdown-menu">
        <li><a class="dropdown-item" href="#" @click.prevent="setMode('title')">제목</a></li>
        <li><a class="dropdown-item" href="#" @click.prevent="setMode('content')">내용</a></li>
        <li><a class="dropdown-item" href="#" @click.prevent="setMode('post')">제목+내용</a></li>
        <li><a class="dropdown-item" href="#" @click.prevent="setMode('user')">유저</a></li>
      </ul>
    </div>

    <i class="fa-solid fa-search search-icon" @click="goSearch" />
    <input
        v-model="keyword"
        @keyup.enter="goSearch"
        type="text"
        class="form-control border-0 shadow-none flex-grow-1 search-input"
        :placeholder="placeHolderText"
    />
  </div>
</template>

<script setup>
import {ref, computed} from 'vue'
import {useRouter} from 'vue-router'

const keyword = ref('')
const mode = ref('post') // 기본: 제목+내용
const router = useRouter()

const modeLabelMap = {
  title: '제목',
  content: '내용',
  post: '제목+내용',
  user: '유저'
}
const searchLabel = computed(() => modeLabelMap[mode.value] || '제목+내용')
const placeHolderText = computed(() =>
    mode.value === 'user' ? '닉네임을 입력하세요' :
        mode.value === 'title' ? '제목을 입력하세요' :
            mode.value === 'content' ? '내용 키워드를 입력하세요' :
                '관심있는 내용을 검색해보세요!'
)

function setMode(m) {
  mode.value = m
}

function goSearch() {
  const text = keyword.value.trim()
  if (!text) return
  router.push({path: '/search/view/content', query: {text, type: mode.value, page: 1}})
}
</script>

<style scoped>
.search-bg {
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.search-icon {
  font-size: 1.2rem;
  color: #888;
  cursor: pointer;
}

.search-icon:hover {
  color: #555;
}

.search-input::placeholder {
  color: #bbb;
}
</style>
