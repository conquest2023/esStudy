<template>
  <section class="d-flex vh-100 justify-content-center align-items-center">
    <div class="signup-card w-100">
      <h2 class="brand-title">Workly</h2>
      <p class="text-muted mb-4">회원가입하고 다양한 서비스를 이용하세요.</p>

      <form @submit.prevent="submit">
        <div v-if="errorMessage" class="alert alert-danger text-center">{{ errorMessage }}</div>

        <div class="mb-3">
          <label for="userId" class="form-label">아이디</label>
          <div class="input-group">
            <input v-model="form.userId" type="text" id="userId" class="form-control"
                   placeholder="아이디 입력" pattern="^[a-zA-Z0-9]+$"
                   title="특수문자를 제외해주세요" required />
            <button type="button" class="btn btn-outline-secondary" @click="checkId">중복확인</button>
          </div>
          <small id="checkResult" class="form-text" :class="{ 'text-success': idAvailable, 'text-danger': !idAvailable }">
            {{ idMsg }}
          </small>
        </div>

        <div class="mb-3">
          <label for="username" class="form-label">이름</label>
          <input v-model="form.username" type="text" id="username" class="form-control"
                 placeholder="이름 입력" pattern="^[a-zA-Z가-힣0-9]+$"
                 title="특수문자를 제외해주세요" required />
        </div>

        <div class="mb-3">
          <label for="age" class="form-label">나이</label>
          <select v-model="form.age" id="age" class="form-control" required>
            <option disabled value="">나이대 선택</option>
            <option>10대</option>
            <option>20대</option>
            <option>30대</option>
            <option>40대</option>
            <option>50대</option>
            <option>60대</option>
            <option>70대</option>
          </select>
        </div>

        <div class="mb-3">
          <label for="password" class="form-label">비밀번호</label>
          <input v-model="form.password" type="password" id="password" class="form-control"
                 placeholder="비밀번호 입력" pattern="^(?=.*[a-zA-Z])(?=.*\d).{8,}$"
                 title="비밀번호는 영문, 숫자를 포함해 8자 이상이어야 합니다." required />
        </div>
        <div class="mb-3">
          <label for="confirmPassword" class="form-label">비밀번호 확인</label>
          <input v-model="form.confirmPassword" type="password" id="confirmPassword" class="form-control"
                 placeholder="비밀번호 확인" required />
          <small v-if="form.confirmPassword && form.confirmPassword !== form.password"
                 class="text-danger">
            비밀번호가 일치하지 않습니다.
          </small>
        </div>
        <button type="submit" class="btn btn-primary w-100">회원가입</button>

        <div class="d-flex gap-2 mt-3">
          <button type="button" class="btn btn-outline-secondary w-50" @click="router.push('/login')">로그인</button>
          <button type="button" class="btn btn-secondary w-50" @click="router.push('/')">홈으로</button>
        </div>
      </form>
    </div>
  </section>
</template>

<script setup>
import {createCommentVNode, reactive, ref} from 'vue'
import { useRouter } from 'vue-router'
import api from '@/utils/api'

const router = useRouter()
const form = reactive({
  userId: '',
  username: '',
  age: '',
  password: '',
  confirmPassword: ''
})

const idMsg = ref('')
const idAvailable = ref(false)
const errorMessage = ref('')

async function checkId() {
  if (!form.userId) {
    return
  }
  const { data } = await api.post(
      '/check', { userId: form.userId
      })
  idAvailable.value = data
  idMsg.value = data ? '사용 가능한 아이디입니다.' : '아이디가 중복됩니다.'
}


async function submit() {
  if (!idAvailable.value) {
    errorMessage.value = '아이디 중복 확인이 필요합니다.'
    return
  }

  if (form.password !== form.confirmPassword) {
    errorMessage.value = '비밀번호가 일치하지 않습니다.'
    return
  }

  try {
    await api.post('/signup/pass', form)
    alert('회원가입 성공!')
    router.push('/api/login')
  } catch (e) {
    errorMessage.value = '회원가입 실패. 다시 시도해주세요.'
  }
}

</script>

<style scoped>
.signup-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0px 4px 15px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 420px;
  text-align: center;
  border-top: 5px solid #2f80ed;
}

.brand-title {
  font-weight: 700;
  font-size: 32px;
  color: #2f80ed;
}

.form-control {
  border-radius: 8px;
  padding: 12px;
  font-size: 16px;
  border: 2px solid #dce8ff;
}

.form-control:focus {
  border-color: #2f80ed;
  box-shadow: none;
}

.btn-primary {
  background: #2f80ed;
  border: none;
  border-radius: 8px;
  font-size: 18px;
  padding: 12px;
  transition: 0.3s;
}

.btn-primary:hover {
  background: #1d6ede;
}

.btn-outline-secondary {
  border-radius: 8px;
  font-size: 16px;
  padding: 10px;
}

@media (max-width: 768px) {
  .signup-card {
    padding: 30px;
  }
}
</style>
