<template>
  <div class="container">
    <div class="row justify-content-center mt-5">
      <div class="col-md-6">
        <div class="card shadow-sm">
          <div class="card-body text-center">
            <h2 class="card-title mb-4">카카오 로그인 처리 중...</h2>
            <div v-if="showUsernameForm">
              <p class="mb-3">사용할 닉네임을 입력하세요</p>
              <div class="input-group mb-3">
                <span class="input-group-text"><i class="bi bi-person"></i></span>
                <input v-model="newUsername" type="text" class="form-control" placeholder="아이디 입력" required>
              </div>
              <button @click="submitUsername" class="btn btn-primary w-100">확인</button>
            </div>

          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
// import {co} from "@fullcalendar/core/internal-common.js";

const router = useRouter()
const showUsernameForm = ref(false)
const newUsername = ref('')
const tempSocialUser = ref(null)
const loginWithSocialUser = async (user) => {
  try {
    const res = await fetch('/api/oauth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        userId: user.providerId,
        username: user.username,
        email: user.email,
        password: user.password
      })
    })
    const tokenData = await res.json()
    localStorage.setItem('token', tokenData.accessToken)
    router.push('/')
  } catch (err) {
    console.error('JWT 발급 실패:', err)
  }
}

const submitUsername = async () => {
  const payload = {
    username: newUsername.value,
    provider: tempSocialUser.value.provider,
    providerId: tempSocialUser.value.providerId,
    email: tempSocialUser.value.email
  }

  const res = await fetch('/api/oauth/username', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  })

  const data = await res.json()
  if (data.result === 'ok') {
    loginWithSocialUser(payload)
  }
}

onMounted(async () => {
  const urlParams = new URLSearchParams(window.location.search)
  const code = urlParams.get('code')
  if (code) {
    try {
      const res = await fetch(`/api/kakao/callback/json?code=${code}`)
      const text = await res.text()
      let data
      try {
        data = JSON.parse(text)
      } catch (e) {
        console.error('JSON 파싱 실패:', e)
        return
      }
      tempSocialUser.value = data

      if (data.result === 'ok') {
        loginWithSocialUser(data)
      } else if (data.result === 'newUser') {
        showUsernameForm.value = true
      }
    } catch (error) {
      console.error('Fetch 에러:', error)
    }
  }
})
</script>

<style scoped>
@import 'https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css';
@import 'https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css';
</style>
