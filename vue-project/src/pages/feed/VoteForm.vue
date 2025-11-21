<template>
  <div class="container my-5">
    <div class="form-container">
      <h4 class="mb-3 fw-bold">투표 작성</h4>

      <form @submit.prevent="submitForm">
        <!-- 카테고리 선택 -->
        <div class="mb-3">
          <select v-model="form.category" class="form-select" required>
            <option value="" disabled>카테고리를 선택해 주세요.</option>
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
          <input
              v-model="form.title"
              type="text"
              class="form-control"
              placeholder="제목을 입력해 주세요."
              required
          />
        </div>

        <!-- 내용 -->
        <div class="mb-3">
          <textarea
              v-model="form.description"
              class="form-control"
              rows="5"
              placeholder="내용을 입력하세요."
              required
          ></textarea>
        </div>

        <!-- 복수 선택 / 익명 여부 -->
        <div class="row mb-3">
          <div class="col-auto form-check">
            <input
                id="multiSelect"
                v-model="form.multiSelect"
                type="checkbox"
                class="form-check-input"
            />
            <label for="multiSelect" class="form-check-label">복수 선택 허용</label>
          </div>

          <div class="col-auto form-check">
            <input
                id="anonymous"
                v-model="form.anonymous"
                type="checkbox"
                class="form-check-input"
            />
            <label for="anonymous" class="form-check-label">익명 투표</label>
          </div>

          <div class="col-auto" v-if="form.multiSelect">
            <input
                v-model.number="form.maxSelectCnt"
                type="number"
                min="1"
                class="form-control"
                placeholder="최대 선택 수"
            />
          </div>
        </div>

        <!-- 투표 항목 -->
        <label class="fw-bold mb-2">투표 항목 (최대 5개)</label>

        <div
            v-for="(option, index) in form.options"
            :key="index"
            class="input-group mb-2"
        >
          <input
              v-model="option.content"
              type="text"
              class="form-control"
              :placeholder="`투표 항목 ${index + 1}`"
              required
          />
          <button
              type="button"
              class="btn btn-outline-danger btn-sm"
              @click="removeOption(index)"
          >
            X
          </button>
        </div>

        <button
            type="button"
            class="btn btn-outline-secondary btn-sm mb-3"
            @click="addOption"
        >
          + 항목 추가
        </button>

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
  multiSelect: false,
  maxSelectCnt: null,
  anonymous: false,
  options: [
    { content: '' } // 처음에 1개
  ]
})

function addOption() {
  if (form.value.options.length < 5) {
    form.value.options.push({ content: '' })
  } else {
    alert('투표 항목은 최대 5개까지 추가할 수 있습니다.')
  }
}

function removeOption(index) {
  if (form.value.options.length > 1) {
    form.value.options.splice(index, 1)
    // sortOrder는 나중에 백엔드에서 인덱스로 다시 세팅 가능
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

  // 백엔드 DTO에 맞게 payload 정리
  const payload = {
    category: form.value.category,
    title: form.value.title,
    description: form.value.description,
    multiSelect: form.value.multiSelect,
    maxSelectCnt: form.value.multiSelect ? form.value.maxSelectCnt : null,
    anonymous: form.value.anonymous,
    options: form.value.options.map((opt, index) => ({
      content: opt.content,
      sortOrder: index
    })),
    closesAt: null // 나중에 마감 시간 추가하면 여기서 넣으면 됨
  }

  try {
    const res = await fetch('/api/poll', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`
      },
      body: JSON.stringify(payload)
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
