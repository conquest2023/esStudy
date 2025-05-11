<template>
  <div class="modal fade show d-block" tabindex="-1" style="background: rgba(0,0,0,0.5);" v-if="visible">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">D-DAY 일정 등록</h5>
          <button type="button" class="btn-close" @click="close"></button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="submit">
            <div class="mb-3">
              <label class="form-label">시험명</label>
              <input v-model="form.examName" type="text" class="form-control" required />
            </div>
            <div class="mb-3">
              <label class="form-label">카테고리</label>
              <select v-model="form.category" class="form-select" required>
                <option value="영어">영어</option>
                <option value="기사">기사</option>
                <option value="컴활">컴활</option>
                <option value="공무원">공무원</option>
                <option value="기타">기타</option>
              </select>
            </div>
            <div class="mb-3">
              <label class="form-label">시험일</label>
              <input v-model="form.examDate" type="date" class="form-control" required />
            </div>
            <div class="mb-3">
              <label class="form-label">목표</label>
              <input v-model="form.goal" type="text" class="form-control" placeholder="ex) 합격, 700점 목표" />
            </div>
            <button type="submit" class="btn btn-primary w-100">등록</button>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import api from '@/utils/api'

const props = defineProps({
  visible: Boolean
})
const emit = defineEmits(['close', 'submitted'])

const form = ref({
  examName: '',
  category: '영어',
  examDate: '',
  goal: ''
})

function close() {
  emit('close')
}

async function submit() {
  try {
    const token = localStorage.getItem('token')
    await api.post('/day/save', form.value, {
      headers: { Authorization: `Bearer ${token}` }
    })
    alert('✅ D-DAY가 등록되었습니다!')
    emit('submitted')
    close()
  } catch (err) {
    console.error(err)
    alert('등록 실패: ' + err.message)
  }
}
</script>

<style scoped>
.modal.fade {
  display: block !important;
}
</style>
