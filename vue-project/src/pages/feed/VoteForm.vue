<template>
  <div class="container my-5">
    <div class="form-container">
      <h4 class="mb-3 fw-bold">투표 작성</h4>
      <form @submit.prevent="submitForm">
        <!-- 카테고리 선택 -->
        <div class="mb-3">
          <select v-model="form.category" class="form-select" required>
            <option value="" disabled selected>게시판을 선택해 주세요.</option>
            <option value="자유">자유</option>
            <option value="자격증">자격증</option>
            <option value="기술">기술</option>
            <option value="취업">취업</option>
            <option value="Q/A">질문</option>
            <option value="자료">자료</option>
          </select>
        </div>

        <!-- 제목 -->
        <div class="mb-3">
          <input v-model="form.title" type="text" class="form-control" placeholder="제목을 입력해 주세요." required />
        </div>

        <!-- 내용 -->
        <div class="mb-3">
          <textarea v-model="form.description" class="form-control" rows="5" placeholder="내용을 입력하세요." required></textarea>
        </div>

        <!-- 투표 항목 -->
        <label class="fw-bold mb-2">투표 항목 (최대 5개)</label>
        <div v-for="(option, index) in form.voteType" :key="index" class="input-group mb-2">
          <input v-model="form.voteType[index]" type="text" class="form-control" :placeholder="`투표 항목 ${index + 1}`" required />
          <button type="button" class="btn btn-outline-danger btn-sm" @click="removeOption(index)">X</button>
        </div>
        <button type="button" class="btn btn-outline-secondary btn-sm mb-3" @click="addOption">+ 항목 추가</button>

        <!-- 제출 -->
        <div class="text-start">
          <button type="submit" class="btn btn-success">작성하기</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const form = ref({
  category: '',
  title: '',
  description: '',
  voteType: ['']
})

function addOption() {
  if (form.value.voteType.length < 5) {
    form.value.voteType.push('')
  } else {
    alert('투표 항목은 최대 5개까지 추가할 수 있습니다.')
  }
}

function removeOption(index) {
  if (form.value.voteType.length > 1) {
    form.value.voteType.splice(index, 1)
  } else {
    alert('최소한 하나의 투표 항목은 있어야 합니다.')
  }
}

async function submitForm() {
  const token = localStorage.getItem('token')
  if (!token) {
    alert('로그인이 필요합니다.')
    return
  }

  try {
    const res = await fetch('/api/save/user/vote', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(form.value)
    })

    const result = await res.json()

    if (res.ok) {
      alert('투표가 성공적으로 저장되었습니다!')
      router.push('/')
    } else {
      alert(result.error || '투표 저장에 실패했습니다.')
    }
  } catch (error) {
    console.error('투표 저장 오류:', error)
    alert('서버 오류가 발생했습니다.')
  }
}
</script>

<style scoped>
.form-container {
  max-width: 700px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
  padding: 30px;
  margin: auto;
}
</style>
