<template>
  <div class="login-wrapper">
    <div class="login-card">
      <h2 class="brand-title">Workly</h2>
      <p class="text-muted">로그인하여 서비스를 이용하세요.</p>

      <form @submit.prevent="handleLogin">
        <div v-if="errorMessage" class="error-message text-danger mb-3">{{ errorMessage }}</div>

        <div class="mb-3">
          <input v-model="userId" type="text" class="form-control" placeholder="아이디 입력" required />
        </div>

        <div class="mb-3">
          <input v-model="password" type="password" class="form-control" placeholder="비밀번호 입력" required />
        </div>

        <button type="submit" class="btn btn-primary w-100">로그인</button>

        <div class="d-flex flex-column gap-2 mt-4">
          <button type="button" class="btn btn-warning w-100" @click="goOAuth('/kakao')">
            <img src="/kakao.png" alt="카카오 로그인" style="height: 24px; margin-right: 8px" />
            카카오 로그인
          </button>

          <button type="button" class="btn w-100 d-flex align-items-center justify-content-center" @click="goOAuth('/naver')" style="background-color: #05bf18; color: #fff; font-weight: bold">
            <img src="/naver.png" alt="네이버 로그인" style="height: 24px; margin-right: 8px" />
            네이버 로그인
          </button>

          <button type="button" class="btn w-100 d-flex align-items-center justify-content-center" @click="goOAuth('/google')" style="background-color: #fff; color: #5f6368; font-weight: 500; border: 1px solid #dadce0">
            <img src="/google.png" alt="구글 로그인" style="height: 20px; margin-right: 12px" />
            <span>Google로 로그인</span>
          </button>
        </div>

        <div class="d-flex gap-2 mt-3">
          <button type="button" class="btn btn-outline-secondary w-50" @click="goPage('/signup')">회원가입</button>
          <button type="button" class="btn btn-secondary w-50" @click="goPage('/')">홈으로</button>
        </div>
      </form>
    </div>
  </div>
</template>


<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()
const userId = ref('')
const password = ref('')
const errorMessage = ref('')

function handleLogin() {
  if (!userId.value || !password.value) {
    errorMessage.value = '아이디와 비밀번호를 입력하세요.'
    return
  }

  axios
      .post('http://localhost:8080/authlogin', {
        userId: userId.value,
        password: password.value
      })
      .then((res) => {
        console.log(res)
        if (res.data.accessToken) {
          localStorage.setItem('token', res.data.accessToken)
          router.push('/')
        } else {
          errorMessage.value = '아이디 또는 비밀번호가 잘못되었습니다.'
        }
      })
      .catch(() => {
        errorMessage.value = '로그인 실패! 다시 시도하세요.'
      })
}

function goOAuth(url) {
  window.location.href = url
}

function goPage(path) {
  router.push(path)
}
</script>

<style scoped>
  body {
    font-family: 'Poppins', sans-serif;
    background: linear-gradient(135deg, #f0f7ff, #e3efff);
    min-height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .login-wrapper {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    background: linear-gradient(135deg, #f0f7ff, #e3efff);
  }

  .login-card {
    background: #ffffff;
    border-radius: 12px;
    padding: 40px;
    box-shadow: 0px 4px 15px rgba(0, 0, 0, 0.1);
    width: 100%;
    max-width: 400px;
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
  }

  .btn-primary:hover {
    background: #1d6ede;
  }

  .error-message {
    color: red;
    font-size: 14px;
    margin-top: 5px;
  }
</style>
