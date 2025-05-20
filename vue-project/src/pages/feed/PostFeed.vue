<template>
  <div class="container mt-5">
    <div class="row justify-content-center">
      <div class="col-md-6">
        <div class="card shadow-lg">
          <div class="card-header bg-primary text-white text-center">
            <h3>📢 공지사항 작성</h3>
          </div>
          <div class="card-body">
            <form @submit.prevent="submitNotice">
              <div class="mb-3">
                <label for="title" class="form-label"> 제목 </label>
                <input type="text" class="form-control" id="title" v-model="title" required placeholder="공지사항 제목 입력" />
              </div>
              <div class="form-group mb-3">
                <label for="imageFiles"> 이미지 파일들: </label>
                <input type="file" id="imageFiles" class="form-control" accept="image/*" multiple @change="handleFileChange" />
              </div>
              <div class="mb-3">
                <label for="content" class="form-label"> 내용 </label>
                <textarea class="form-control" id="content" rows="5" v-model="description" placeholder="공지사항 내용을 입력하세요" required></textarea>
              </div>
              <button type="submit" class="btn btn-primary w-100"> 등록하기 </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const title = ref('')
const description = ref('')
const files = ref([])
const router = useRouter()

function handleFileChange(event) {
  files.value = Array.from(event.target.files)
}

async function submitNotice() {
  const token = localStorage.getItem('token')
  if (!token) {
    alert('로그인이 필요합니다.')
    return
  }

  const formData = new FormData()
  formData.append('title', title.value)
  formData.append('description', description.value.replace(/\n/g, '\\n'))

  if (files.value.length > 0) {
    formData.append('imageFile', files.value[0])
  }

  try {
    await axios.post('/api/add/notice', formData, {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'multipart/form-data',
      },
    })
    alert('📅 공지사항이 등록되었습니다!')
    router.push('/')
  } catch (error) {
    alert('❌ 공지사항 등록 실패!')
  }
}
</script>

<style scoped>
.card-header {
  font-size: 1.25rem;
}
</style>
