<template>
  <div class="container my-5">
    <h1 class="text-center text-primary mb-4">자격증 캘린더</h1>

    <!-- 카테고리 선택 영역 -->
    <CategorySelector @subCategorySelected="handleSubCategory" />

    <!-- 캘린더 -->
    <div v-show="showCalendar" id="calendar" class="mt-4 rounded shadow-sm p-2 bg-white" />

    <!-- 공부 팁 플로팅 버튼 -->
    <div v-if="showTipBtn" id="studyTipFloatBtn" @click="openTips">팁</div>

    <!-- 팁 슬라이드 패널 -->
<!--    <StudyTipPanel v-if="showTipPanel" :keyword="selectedCert" @close="closeTips" />-->
  </div>
</template>

<script setup>
import { ref, onMounted, watch ,nextTick } from 'vue'
import CategorySelector from '../certificate/component/CategorySelector.vue'
// import StudyTipPanel from './StudyTipPanel.vue'
import { Calendar } from '@fullcalendar/core'
import dayGridPlugin from '@fullcalendar/daygrid'
import timeGridPlugin from '@fullcalendar/timegrid'
import interactionPlugin from '@fullcalendar/interaction'
import koLocale from '@fullcalendar/core/locales/ko'
import axios from 'axios'

const showCalendar = ref(false)
const showTipBtn = ref(false)
const showTipPanel = ref(false)
const selectedCert = ref('')
let calendar = null

function openTips() {
  showTipPanel.value = true
}
function closeTips() {
  showTipPanel.value = false
}

// 자격증 선택 시 실행
async function handleSubCategory(certName) {
  selectedCert.value = certName
  showTipBtn.value = true

  const token = localStorage.getItem('token')
  const { data } = await axios.get(`/api/certificate/schedule/${encodeURIComponent(certName)}`, {
    headers: { Authorization: `Bearer ${token}` }
  })

  const events = data.certSchedule.flatMap(cert => [
    { title: `[필기 원서접수] ${cert.name} - ${cert.examRound}`, start: cert.writtenRegStart, color: "#007bff" },
    { title: `[필기 시험] ${cert.name} - ${cert.examRound}`, start: cert.writtenExamStart, color: "#ff6b6b" },
    { title: `[필기 합격발표] ${cert.name} - ${cert.examRound}`, start: cert.writtenPassDate, color: "#ffc107" },
    { title: `[실기 원서접수] ${cert.name} - ${cert.examRound}`, start: cert.practicalRegStart, color: "#17a2b8" },
    { title: `[실기 시험] ${cert.name} - ${cert.examRound}`, start: cert.practicalExamStart, color: "#28a745" },
    { title: `[실기 합격발표] ${cert.name} - ${cert.examRound}`, start: cert.practicalPassStart, color: "#ff851b" }
  ])
  await nextTick()

  if (!calendar) {
    calendar = new Calendar(document.getElementById('calendar'), {
      height: 600,
      contentHeight: 'auto',
      dayMaxEventRows: 3,
      plugins: [dayGridPlugin, timeGridPlugin, interactionPlugin],
      initialView: 'dayGridMonth',
      locale: koLocale,
      events,
      eventClick: ({ event }) => {
        alert(`${event.title} - ${event.startStr}`)
      }
    })
    calendar.render()
  } else {
    calendar.removeAllEvents()
    calendar.addEventSource(events)
  }

  showCalendar.value = true
}
</script>

<style scoped>
  #studyTipFloatBtn {
    position: fixed;
    top: 50%;
    right: 0;
    transform: translateY(-50%);
    width: 40px;
    height: 80px;
    background-color: #007bff;
    color: #fff;
    border-radius: 8px 0 0 8px;
    writing-mode: vertical-rl;
    text-orientation: mixed;
    font-size: 0.9rem;
    cursor: pointer;
    z-index: 3000;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .container {
    max-width: 1200px;
    margin: 50px auto;
    padding-top: 80px;
  }
</style>
