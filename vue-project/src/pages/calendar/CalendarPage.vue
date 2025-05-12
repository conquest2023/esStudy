
<script setup>

import { ref, reactive, onMounted } from 'vue'

import FullCalendar from '@fullcalendar/vue3'
import dayGridPlugin from '@fullcalendar/daygrid'
import timeGridPlugin from '@fullcalendar/timegrid'
import interactionPlugin from '@fullcalendar/interaction'
// import '@fullcalendar/core/index.css'
// import '@fullcalendar/daygrid/index.css'
// import '@fullcalendar/timegrid/index.css'
import api from '@/utils/api'


const calendarRef        = ref(null)
const events             = ref([])
const filter             = ref('all')
const search             = reactive({ type: 'title', q: '' })
const showKibana         = ref(false)
const kibanaUrl          = ref('')

const showAddModal       = ref(false)
const showRepeatModal    = ref(false)
const showRepeatDelete   = ref(false)
const showDetailModal    = ref(false)
const detail             = reactive({})

const addForm   = reactive({ title: '', date: '', category: '기타', desc: '' })
const repeatForm = reactive({
  title: '', category: '기타', startDate: '', endDate: '', days: [], freq: 'daily', interval: 1,
  startTime: '', endTime: '', desc: ''
})
const calendarOptions = reactive({
  plugins:      [dayGridPlugin, timeGridPlugin, interactionPlugin],
  initialView:  'dayGridMonth',
  locale:       'ko',
  height:       700,
  events,
  eventClick({ event }) {
  }
})

function statusColor (status) {
  switch (status) {
    case 'IN_PROGRESS': return { bg: '#0d6efd', border: '#0a58ca' }
    case 'DONE':        return { bg: '#198754', border: '#146c43' }
    case 'CANCELLED':   return { bg: '#dc3545', border: '#b02a37' }
    default:            return { bg: '#adb5bd', border: '#868e96' }
  }
}

/* --------------------------------------------------
 *  data fetchers
 * --------------------------------------------------*/
async function loadEvents () {
  const token = localStorage.getItem('token')
  if (!token) return
  events.value = []
  const [dDay, schedule, project] = await Promise.all([
    api.get('/day',                        { headers: { Authorization: 'Bearer '+token }}),
    api.get('/search/schedule',            { headers: { Authorization: 'Bearer '+token }}),
    api.get('/search/project/todo',        { headers: { Authorization: 'Bearer '+token }})
  ])
  /* d‑day */
  if (Array.isArray(dDay.data?.D_Day)) {
    dDay.data.D_Day.forEach(d=>{
      events.value.push({
        id:`d-${d.id}`, title:`[D-DAY] ${d.examName}`, start:d.examDate,
        allDay:true, backgroundColor:'#ffc107', borderColor:'#ffc107', textColor:'#000',
        extendedProps:{ category:d.category, goal:d.goal }
      })
    })
  }
  /* schedules */
  if (Array.isArray(schedule.data?.todos)) {
    schedule.data.todos.forEach(s=>{
      const c=statusColor(s.status||'IN_PROGRESS')
      events.value.push({
        id:`s-${s.scheduleId}`, title:`[캘린더] ${s.title}`, start:s.endDatetime,
        allDay:true, backgroundColor:c.bg, borderColor:c.border,
        extendedProps:{ description:s.description, category:s.category }
      })
    })
  }
  /* project todos */
  if (Array.isArray(project.data?.todos)) {
    project.data.todos.forEach(t=>{
      const c=statusColor(t.status||'TODO')
      events.value.push({
        id:`p-${t.todo_id}`, title:`[장기목표] ${t.title}`, start:t.end,
        allDay:true, backgroundColor:c.bg, borderColor:c.border,
        extendedProps:{ description:t.description, category:t.category }
      })
    })
  }
}

async function saveSingle () {
  const token = localStorage.getItem('token')
  try {
    await api.post('/save/schedule', {
      title:addForm.title, endDatetime:addForm.date+"T23:59:59", allDay:true,
      category:addForm.category, description:addForm.desc
    }, { headers:{ Authorization:'Bearer '+token }})
    showAddModal.value=false
    Object.assign(addForm,{title:'',date:'',category:'기타',desc:''})
    await loadEvents()
  } catch(e){ alert('저장 실패') }
}

function eventClick({ event }) {
  Object.assign(detail, {
    id:event.id, title:event.title, date:event.startStr.substring(0,10),
    category:event.extendedProps.category||'', desc:event.extendedProps.description||''
  })
  showDetailModal.value=true
}

async function deleteEvent(id){
  const token=localStorage.getItem('token')
  try{
    await api.post(`/schedule/delete/${id}`,null,{headers:{Authorization:'Bearer '+token}})
    showDetailModal.value=false
    await loadEvents()
  }catch(e){alert('삭제 실패')}
}

async function buildKibana(){
  const token=localStorage.getItem('token')
  const { data } = await api.get('/user/id',{headers:{Authorization:'Bearer '+token}})
  const userId=data.userId
  const gObj={filters:[],refreshInterval:{pause:true,value:0},time:{from:'now-30d',to:'now'}}
  const aObj={dataView:'9766ed94-3782-4050-b2d1-9d1f6f418b76',query:{language:'kuery',query:`userId.keyword : "${userId}"`}}
  const g=window.rison.encode(gObj); const a=window.rison.encode(aObj)
  kibanaUrl.value=`https://kibana.montkim.com/app/visualize#/create?embed=true&type=horizontal_bar&indexPattern=9766ed94-3782-4050-b2d1-9d1f6f418b76&_g=${encodeURIComponent(g)}&_a=${encodeURIComponent(a)}`
}

onMounted(async ()=>{
  await loadEvents();
  await buildKibana();
})
</script>

<template>
  <div class="container py-4">
    <!-- top bar -->
    <div class="d-flex justify-content-between align-items-center mb-3 flex-wrap gap-2">
      <h4 class="mb-0">📅 내 캘린더</h4>
      <div class="d-flex flex-wrap gap-2">
        <button class="btn btn-primary"             @click="filter='all'">📌 전체</button>
        <button class="btn btn-success"             @click="filter='completed'">✅ 완료</button>
        <button class="btn btn-warning"             @click="filter='pending'">⏳ 예정</button>
        <button class="btn btn-info"                @click="showKibana=!showKibana">📊 통계</button>
        <button class="btn btn-primary"             @click="showAddModal=true">+ 일정 추가</button>
        <button class="btn btn-outline-warning"     @click="showRepeatModal=true">🔄 반복 일정</button>
        <button class="btn btn-outline-danger"      @click="showRepeatDelete=true">🗑 반복 삭제</button>
      </div>
    </div>

    <!-- calendar -->
    <div>
      <FullCalendar
          ref="calendarRef"
          class="shadow rounded-3 p-2"
          :plugins="[dayGridPlugin, timeGridPlugin, interactionPlugin]"
          :initial-view="'dayGridMonth'"
          locale="ko"
          height="700"
          :events="events"
          @event-click="eventClick"
      />
    </div>

    <!-- kibana -->
    <div v-if="showKibana" class="text-center mt-4">
      <iframe :src="kibanaUrl" height="600" width="100%" style="max-width:800px"></iframe>
    </div>

    <!-- ────────────── modals (teleport to body) ────────────── -->
    <teleport to="body">
      <!-- add single -->
      <div v-if="showAddModal" class="modal-backdrop">
        <div class="modal-box">
          <h5>📌 새 일정 추가</h5>
          <input v-model="addForm.title" class="form-control mb-2" placeholder="제목" />
          <input type="date" v-model="addForm.date" class="form-control mb-2" />
          <select v-model="addForm.category" class="form-select mb-2">
            <option value="운동">운동</option><option value="약속">약속</option>
            <option value="공부">공부</option><option value="알바">알바</option>
            <option value="데이트">데이트</option><option value="기타">기타</option>
          </select>
          <textarea v-model="addForm.desc" class="form-control mb-3" placeholder="내용" />
          <div class="d-flex justify-content-end gap-2">
            <button class="btn btn-secondary btn-sm" @click="showAddModal=false">취소</button>
            <button class="btn btn-primary btn-sm"    @click="saveSingle">저장</button>
          </div>
        </div>
      </div>

      <!-- detail -->
      <div v-if="showDetailModal" class="modal-backdrop">
        <div class="modal-box">
          <h5>{{ detail.title }}</h5>
          <p class="mb-1"><b>날짜</b> : {{ detail.date }}</p>
          <p class="mb-1"><b>카테고리</b> : {{ detail.category }}</p>
          <p><b>내용</b> : {{ detail.desc }}</p>
          <div class="d-flex justify-content-end gap-2">
            <button class="btn btn-danger btn-sm"  @click="deleteEvent(detail.id)">삭제</button>
            <button class="btn btn-secondary btn-sm" @click="showDetailModal=false">닫기</button>
          </div>
        </div>
      </div>
    </teleport>
  </div>
  <FullCalendar v-bind="calendarOptions" />
</template>

<style scoped>
.container {
  max-width: 1200px;
  margin: 50px auto;
  padding-top: 80px;
}
.modal-backdrop{position:fixed;inset:0;display:flex;align-items:center;justify-content:center;background:rgba(0,0,0,.4);z-index:1060}
.modal-box{background:#fff;border-radius:12px;padding:20px;width:380px;max-width:90vw;box-shadow:0 4px 12px rgba(0,0,0,.15)}
</style>
