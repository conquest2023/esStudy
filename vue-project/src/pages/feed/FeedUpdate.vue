<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '@/utils/api'

const route = useRoute()
const router = useRouter()

const feedId = route.query.id
const title = ref('')
const description = ref('')
const loading = ref(false)
const errorMsg = ref('')

onMounted(async () => {
  try {
    const { data } = await api.get('/detail', { params: { id: feedId } })
    title.value = data.data.title
    description.value = data.data.description
  } catch (err) {
    errorMsg.value = '게시글을 불러오지 못했습니다.'
  }
})

async function submitEdit() {
  if (!title.value.trim() || !description.value.trim()) {
    errorMsg.value = '제목과 내용을 입력해주세요.'
    return
  }
  loading.value = true
  try {
    const token = localStorage.getItem('token')  // 토큰 꺼내오기

    await api.post('/search/view/feed/update', {
      feedUID: feedId,
      title: title.value,
      description: description.value
    }, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
    await new Promise(resolve => setTimeout(resolve, 1000))
    router.push(`/search/view/feed/id/${feedId}`)
  } catch (e) {
    errorMsg.value = '수정 중 오류가 발생했습니다.'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="container py-5" style="max-width:720px">
    <h2 class="fw-bold mb-4 text-center">✏️ 게시글 수정</h2>

    <div v-if="errorMsg" class="alert alert-danger">{{ errorMsg }}</div>

    <div class="form-floating mb-3">
      <input v-model="title" type="text" class="form-control" id="titleInput" placeholder="제목" />
      <label for="titleInput">제목</label>
    </div>

    <div class="form-floating mb-4">
      <textarea v-model="description" class="form-control" placeholder="내용 입력" id="descInput" style="height: 200px"></textarea>
      <label for="descInput">내용</label>
    </div>

    <button class="btn btn-primary w-100 py-2" :disabled="loading" @click="submitEdit">
      <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
      수정 완료
    </button>
  </div>
</template>

<style scoped>
textarea.form-control:focus,
input.form-control:focus {
  border-color: #86b7fe;
  box-shadow: 0 0 0 0.2rem rgba(13, 110, 253, 0.25);
}
</style>
