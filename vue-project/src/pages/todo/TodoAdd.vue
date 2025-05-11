<template>
  <div class="container mt-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h2 class="mb-0">📝 새 Todo 추가</h2>
      <RouterLink to="/todo" class="btn btn-outline-secondary btn-sm">돌아가기</RouterLink>
    </div>

    <div class="card shadow-sm p-4">
      <form @submit.prevent="submitTodo">
        <div class="mb-3">
          <label for="title" class="form-label">제목</label>
          <input type="text" id="title" v-model="title" class="form-control" required />
        </div>

        <div class="mb-3">
          <label for="description" class="form-label">설명</label>
          <textarea id="description" v-model="description" class="form-control" rows="3" />
        </div>

        <div class="mb-3">
          <label for="category" class="form-label">카테고리</label>
          <select id="category" v-model="category" class="form-select">
            <option value="운동">🏋️ 운동</option>
            <option value="약속">🗕️ 약속</option>
            <option value="공부">📖 공부</option>
            <option value="데이트">💑 데이트</option>
            <option value="알바">📝 알바</option>
            <option value="기타" selected>📝 기타</option>
          </select>
        </div>

        <div class="mb-3">
          <label for="priority" class="form-label">우선순위</label>
          <select id="priority" v-model.number="priority" class="form-select">
            <option value="1">1 - 높음</option>
            <option value="2">2</option>
            <option value="3">3 - 보통</option>
            <option value="4">4</option>
            <option value="5">5 - 낮음</option>
          </select>
        </div>

        <button type="submit" class="btn btn-primary w-100">추가하기</button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/utils/api'

const router = useRouter()
const title = ref('')
const description = ref('')
const category = ref('기타')
const priority = ref(3)

const submitTodo = async () => {
  try {
    const token = localStorage.getItem('token')
    const payload = {
      title: title.value,
      description: description.value,
      priority: priority.value,
      category: category.value,
      status: 'IN_PROGRESS'
    }
    await api.post('/save/todo', payload, {
      headers: { Authorization: 'Bearer ' + token }
    })
    alert('Todo가 추가되었습니다!')
    router.push('/todo')
  } catch (err) {
    alert('오류 발생: ' + err.message)
  }
}
</script>

<style scoped>
.container {
  max-width: 1200px;
  margin: 50px auto;
  padding-top: 100px;
}
</style>
