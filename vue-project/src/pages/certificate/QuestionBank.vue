<template>
  <div class="container py-5">
    <!-- 안내 -->
    <div class="mb-4 text-center">
      <h2 class="fw-bold text-primary">📘 문제은행</h2>
      <p class="text-muted mb-0">자격증 → 필기/실기 → 연도 & 회차 선택</p>
    </div>

    <!-- STEP 1: 자격증 선택 -->
    <div v-if="step === 'cert'">
      <div class="row row-cols-1 row-cols-md-3 g-4">
        <div v-for="cert in certList" :key="cert.name" class="col">
          <div class="card cert-card h-100 text-center p-3" @click="openTypeModal(cert.name)">
            <img :src="cert.icon" class="mx-auto cert-img" alt="아이콘" />
            <h5 class="fw-bold mb-1">{{ cert.name }}</h5>
            <p class="text-secondary small mb-0">필기/실기 문제</p>
          </div>
        </div>
      </div>
    </div>

    <!-- STEP 2: 연도 선택 -->
    <div v-if="step === 'year'">
      <div class="text-center mb-4">
        <h3 class="fw-bold text-success">🛠️ {{ selectedCategory }}</h3>
        <p class="text-muted mb-0">원하는 연도와 회차를 선택하세요</p>
      </div>
      <div class="row row-cols-1 row-cols-md-4 g-4">
        <div v-for="year in [2024, 2023, 2022, 2021]" :key="year" class="col">
          <div class="card year-card text-center p-4" @click="openSessionModal(year)">
            <h3 class="fw-bold">{{ year }}</h3>
            <p class="text-secondary mb-0">문제 선택</p>
          </div>
        </div>
        <div class="col">
          <div class="card year-card text-center p-4" @click="showTagModal = true">
            <h3 class="fw-bold">과목별 문제</h3>
            <p class="text-secondary mb-0">문제 선택</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 유형 모달 -->
    <div v-if="showTypeModal" class="modal-mask">
      <div class="modal-wrapper">
        <div class="modal-container">
          <div class="modal-header bg-primary text-white d-flex justify-content-between align-items-center px-3 py-2">
            <h5 class="modal-title">유형 선택</h5>
            <button class="btn-close btn-close-white" @click="showTypeModal = false" />
          </div>
          <div class="modal-body text-center p-4">
            <p class="mb-4 fw-bold fs-5 text-primary">{{ selectedCert }}</p>
            <button class="btn btn-primary w-100 mb-3 fs-5 py-2" @click="chooseType('필기')">📘 필기 문제</button>
            <button class="btn btn-success w-100 fs-5 py-2" @click="chooseType('실기')">🛠️ 실기 문제</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 회차 모달 -->
    <div v-if="showSessionModal" class="modal-mask">
      <div class="modal-wrapper">
        <div class="modal-container">
          <div class="modal-header bg-success text-white d-flex justify-content-between align-items-center px-3 py-2">
            <h5 class="modal-title">회차 선택</h5>
            <button class="btn-close btn-close-white" @click="showSessionModal = false" />
          </div>
          <div class="modal-body text-center p-4">
            <p class="mb-4 fw-bold fs-4 text-primary">{{ selectedYear }}년</p>
            <div class="d-grid gap-3">
              <button class="btn btn-primary" @click="loadQuestions(1)">1회차</button>
              <button class="btn btn-primary" @click="loadQuestions(2)">2회차</button>
              <button class="btn btn-primary" @click="loadQuestions(3)">3회차</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 태그 모달 -->
    <div v-if="showTagModal" class="modal-mask">
      <div class="modal-wrapper">
        <div class="modal-container modal-lg">
          <div class="modal-header bg-info text-white d-flex justify-content-between align-items-center px-3 py-2">
            <h5 class="modal-title">📘 과목별 문제 선택</h5>
            <button class="btn-close btn-close-white" @click="showTagModal = false" />
          </div>
          <div class="modal-body py-4 px-3">
            <div class="row row-cols-2 row-cols-md-4 g-3 justify-content-center">
              <button v-for="tag in tags" :key="tag.id" class="btn btn-outline-primary rounded-pill px-4 py-2" @click="loadTagQuestions(tag.id)">{{ tag.name }}</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {onMounted, ref} from 'vue'
import axios from 'axios'

const step = ref('cert')
const selectedCert = ref('')
const selectedCategory = ref('')
const selectedYear = ref('')
const showTypeModal = ref(false)
const showSessionModal = ref(false)
const showTagModal = ref(false)

const certList = [
  { name: '정보처리기사', icon: 'https://img.icons8.com/office/80/book.png' },
  { name: '정보보안기사', icon: 'https://img.icons8.com/office/80/security-checked.png' },
  { name: '정보처리기능사', icon: 'https://img.icons8.com/office/80/computer.png' },
  // { name: 'SQLD', icon: 'https://img.icons8.com/office/80/database.png' },
  { name: '빅분기', icon: 'https://img.icons8.com/office/80/analysis.png' }
]

const tags = [
  { id: 1, name: 'C언어' }, { id: 2, name: '네트워크' }, { id: 11, name: '데이터베이스' },
  { id: 7, name: '디자인패턴' }, { id: 6, name: '보안' }, { id: 14, name: '상속' },
  { id: 12, name: '소프트웨어 공학' }, { id: 8, name: '스택' }, { id: 10, name: '운영체제' },
  { id: 9, name: '자료구조' }, { id: 5, name: '자바' }, { id: 13, name: '테스트기법' },
  { id: 3, name: '파이썬' }, { id: 4, name: '포인터' }
]

function openTypeModal(certName) {
  selectedCert.value = certName
  showTypeModal.value = true
}

function chooseType(type) {
  selectedCategory.value = `${selectedCert.value} ${type}`
  showTypeModal.value = false
  step.value = 'year'
}

function openSessionModal(year) {
  selectedYear.value = year
  showSessionModal.value = true
}

async function loadQuestions(sessionNum) {
  const token = localStorage.getItem('token')
  const category = selectedCategory.value
  const round = `${selectedYear.value}_${sessionNum}회`

  try {
    const { data } = await axios.get('/api/practical', {
      params: { category, round },
      headers: { Authorization: `Bearer ${token}` }
    })
    localStorage.setItem('questions', JSON.stringify(data.DTOS))
    localStorage.setItem('category', category)
    localStorage.setItem('round', round)
    window.location.href = '/api/search/view/practical/question'
  } catch (e) {
    alert('문제를 불러오지 못했습니다.')
  }
}
onMounted(() => {
  alert('아직 준비중인 기능입니다!')
  window.location.href ="/"
})
async function loadTagQuestions(tagId) {
  const token = localStorage.getItem('token')
  try {
    const { data } = await axios.get(`/api/practical/tag`, {
      params: { tagId },
      headers: { Authorization: `Bearer ${token}` }
    })
    localStorage.setItem('questions', JSON.stringify(data.DTOS))
    localStorage.setItem('category', `tag_${tagId}`)
    localStorage.setItem('round', '태그 문제')

    await new Promise(resolve => setTimeout(resolve, 1000))


    window.location.href = '/api/search/view/practical/question'
  } catch (e) {
    alert('태그 문제를 불러오지 못했습니다.')
  }
}
</script>

<style scoped>
.cert-card, .year-card {
  transition: transform 0.2s, box-shadow 0.2s;
  border: none;
  border-radius: 1rem;
  overflow: hidden;
}
.cert-card:hover, .year-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 20px rgba(0,0,0,0.1);
}
.cert-img {
  width: 60px;
  height: 60px;
  margin-bottom: 1rem;
}
.modal-mask {
  position: fixed;
  z-index: 9998;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
}
.modal-wrapper {
  max-width: 700px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
}
.modal-container {
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.33);
  overflow: hidden;
}
.container {
  max-width: 1200px;
  margin: 50px auto;
  padding-top: 100px;
}
</style>
