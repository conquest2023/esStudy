<!-- TodoNew.vue (Due + Repeat 포함 버전) -->
<template>
  <div class="page">
    <div class="wrap">
      <!-- 헤더 -->
      <div class="topbar">
        <div class="title">
          <span class="emoji">📝</span>
          <div>
            <h2>새 Todo 추가</h2>
            <p class="sub">오늘 해야 할 가장 중요한 한 가지부터 적어보세요.</p>
          </div>
        </div>

        <RouterLink to="/todo" class="btn-ghost">돌아가기</RouterLink>
      </div>

      <!-- 폼 카드 -->
      <section class="panel">
        <form @submit.prevent="submitTodo" class="form">
          <!-- 제목 -->
          <div class="field">
            <label class="label">제목 <span class="req">*</span></label>
            <input
                class="input"
                type="text"
                v-model.trim="title"
                placeholder="ex) 알고리즘 1문제 풀기 / 자소서 1문항 초안"
                maxlength="60"
                required
            />
            <div class="hint">{{ title.length }}/60</div>
          </div>

          <!-- 설명 -->
          <div class="field">
            <label class="label">설명</label>
            <textarea
                class="textarea"
                v-model.trim="description"
                rows="5"
                placeholder="오늘 어디까지 할지 간단히 적어보세요. (선택)"
                maxlength="500"
            />
            <div class="hint">{{ description.length }}/500</div>
          </div>

          <!-- 옵션 2열 -->
          <div class="grid2">
            <!-- 카테고리 -->
            <div class="field">
              <label class="label">카테고리</label>

              <div class="chips">
                <button
                    v-for="c in categories"
                    :key="c.value"
                    type="button"
                    class="chip"
                    :class="{ active: category === c.value }"
                    @click="category = c.value"
                >
                  <span class="chip-emoji">{{ c.emoji }}</span>
                  {{ c.label }}
                </button>
              </div>

              <select class="select" v-model="category">
                <option v-for="c in categories" :key="c.value" :value="c.value">
                  {{ c.emoji }} {{ c.label }}
                </option>
              </select>

              <div class="hint">카테고리는 통계/필터에 활용돼요.</div>
            </div>

            <!-- 우선순위 -->
            <div class="field">
              <label class="label">우선순위</label>

              <div class="priority-row">
                <button
                    v-for="p in priorities"
                    :key="p.value"
                    type="button"
                    class="pill"
                    :class="{ active: priority === p.value }"
                    @click="priority = p.value"
                >
                  <span class="dot" />
                  {{ p.label }}
                </button>
              </div>

              <select class="select" v-model.number="priority">
                <option v-for="p in priorities" :key="p.value" :value="p.value">
                  {{ p.value }} - {{ p.label }}
                </option>
              </select>

              <div class="hint">높을수록 목록 상단에 배치돼요.</div>
            </div>
          </div>

          <!-- ✅ 마감기한 + 반복 (새 섹션) -->
          <div class="grid2">
            <!-- 마감기한 프리셋 -->
            <div class="field">
              <label class="label">마감기한</label>

              <div class="chips">
                <button
                    v-for="d in duePresets"
                    :key="d.value"
                    type="button"
                    class="chip"
                    :class="{ active: duePreset === d.value }"
                    @click="setDuePreset(d.value)"
                >
                  {{ d.label }}
                </button>
              </div>

              <div v-if="duePreset === 'CUSTOM'" class="row">
                <input class="input" type="date" v-model="customDate" />
                <input class="input" type="time" v-model="customTime" />
              </div>

              <div v-else class="hint">
                선택된 마감: <strong>{{ duePreview }}</strong>
              </div>

              <div class="hint">
                * 마감이 필요 없으면 ‘사용자 지정’에서 날짜를 비워둘 수도 있어요.
              </div>
            </div>

            <!-- 반복 -->
            <div class="field">
              <label class="label">반복</label>

              <div class="chips">
                <button
                    v-for="r in repeatOptions"
                    :key="r.value"
                    type="button"
                    class="chip"
                    :class="{ active: repeatType === r.value }"
                    @click="repeatType = r.value"
                >
                  {{ r.label }}
                </button>
              </div>

              <!-- 매주일 때 요일 선택 -->
              <div v-if="repeatType === 'WEEKLY'" class="weekday">
                <button
                    v-for="w in weekdays"
                    :key="w.value"
                    type="button"
                    class="day"
                    :class="{ active: repeatWeekdays.includes(w.value) }"
                    @click="toggleWeekday(w.value)"
                >
                  {{ w.label }}
                </button>
              </div>

              <div class="hint">
                반복은 “투두 템플릿” 개념으로 확장하기 좋아요.
              </div>
            </div>
          </div>

          <!-- 푸터 -->
          <div class="footer">
            <div class="mini">
              <span class="mini-badge">미리보기</span>
              <span class="mini-title">{{ title || '제목을 입력해 주세요' }}</span>
              <span class="mini-meta">
                · {{ category }} · P{{ priority }}
                · {{ dueBadge }}
                · {{ repeatBadge }}
              </span>
            </div>

            <button class="btn-primary" type="submit" :disabled="isSubmitting || !title">
              <span v-if="!isSubmitting">추가하기</span>
              <span v-else>저장 중...</span>
            </button>
          </div>
        </form>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/utils/api'

const router = useRouter()

const title = ref('')
const description = ref('')
const category = ref('기타')
const priority = ref(3)
const isSubmitting = ref(false)

// 카테고리/우선순위
const categories = [
  { value: '공부', label: '공부', emoji: '📖' },
  { value: '운동', label: '운동', emoji: '🏋️' },
  { value: '알바', label: '알바', emoji: '📝' },
  { value: '약속', label: '약속', emoji: '📅' },
  { value: '데이트', label: '데이트', emoji: '💑' },
  { value: '기타', label: '기타', emoji: '🧩' },
]
const priorities = [
  { value: 1, label: '높음' },
  { value: 2, label: '조금 높음' },
  { value: 3, label: '보통' },
  { value: 4, label: '낮음' },
  { value: 5, label: '아주 낮음' },
]

const duePresets = [
  { value: 'NONE',  label: '없음' },
  { value: 'TODAY', label: '오늘' },
  { value: 'WEEK',  label: '이번주' },
  { value: 'MONTH', label: '이번달' },
  { value: 'CUSTOM', label: '사용자 지정' },
]
const duePreset = ref('TODAY')

// 사용자 지정 날짜/시간
const customDate = ref('') // yyyy-mm-dd
const customTime = ref('23:59') // HH:mm

function startOfDay(d) {
  const x = new Date(d)
  x.setHours(0,0,0,0)
  return x
}
function endOfDay(d) {
  const x = new Date(d)
  x.setHours(23,59,59,999)
  return x
}
function endOfWeek(d) {
  const x = new Date(d)
  const day = x.getDay() // 0=Sun ... 6=Sat
  const diff = (6 - day) // Sat end
  x.setDate(x.getDate() + diff)
  return endOfDay(x)
}
function endOfMonth(d) {
  const x = new Date(d)
  // 다음달 0일 = 이번달 마지막날
  const last = new Date(x.getFullYear(), x.getMonth() + 1, 0)
  return endOfDay(last)
}

function setDuePreset(p) {
  duePreset.value = p
  if (p === 'CUSTOM' && !customDate.value) {
    const today = new Date()
    customDate.value = today.toISOString().slice(0,10)
    customTime.value = '23:59'
  }
}

const dueAt = computed(() => {
  const now = new Date()
  if (duePreset.value === 'NONE') return null

  if (duePreset.value === 'TODAY') return endOfDay(now).toISOString()
  if (duePreset.value === 'WEEK') return endOfWeek(now).toISOString()
  if (duePreset.value === 'MONTH') return endOfMonth(now).toISOString()

  // CUSTOM
  if (duePreset.value === 'CUSTOM') {
    // 날짜를 비워두면 마감 없음 처리
    if (!customDate.value) return null
    const [hh, mm] = (customTime.value || '23:59').split(':').map(Number)
    const d = new Date(customDate.value)
    d.setHours(hh, mm, 0, 0)
    return d.toISOString()
  }
  return null
})

const duePreview = computed(() => {
  if (!dueAt.value) return '마감 없음'
  const d = new Date(dueAt.value)
  const yyyy = d.getFullYear()
  const mm = String(d.getMonth() + 1).padStart(2, '0')
  const dd = String(d.getDate()).padStart(2, '0')
  const hh = String(d.getHours()).padStart(2, '0')
  const mi = String(d.getMinutes()).padStart(2, '0')
  return `${yyyy}-${mm}-${dd} ${hh}:${mi}`
})

const dueBadge = computed(() => {
  const map = { NONE:'마감없음', TODAY:'오늘마감', WEEK:'이번주', MONTH:'이번달', CUSTOM:'지정' }
  return map[duePreset.value] || '마감'
})

const repeatOptions = [
  { value: 'NONE', label: '없음' },
  { value: 'DAILY', label: '매일' },
  { value: 'WEEKLY', label: '매주' },
  { value: 'MONTHLY', label: '매월' },
]
const repeatType = ref('NONE')

const weekdays = [
  { value: 'MON', label: '월' },
  { value: 'TUE', label: '화' },
  { value: 'WED', label: '수' },
  { value: 'THU', label: '목' },
  { value: 'FRI', label: '금' },
  { value: 'SAT', label: '토' },
  { value: 'SUN', label: '일' },
]
const repeatWeekdays = ref(['MON']) // 기본값

function toggleWeekday(day) {
  const arr = repeatWeekdays.value
  if (arr.includes(day)) {
    repeatWeekdays.value = arr.filter(x => x !== day)
  } else {
    repeatWeekdays.value = [...arr, day]
  }
  // 매주인데 다 빼버리면 최소 1개는 유지
  if (repeatType.value === 'WEEKLY' && repeatWeekdays.value.length === 0) {
    repeatWeekdays.value = ['MON']
  }
}

const repeatBadge = computed(() => {
  if (repeatType.value === 'NONE') return '반복없음'
  if (repeatType.value === 'DAILY') return '매일'
  if (repeatType.value === 'MONTHLY') return '매월'
  if (repeatType.value === 'WEEKLY') return `매주(${repeatWeekdays.value.join(',')})`
  return '반복'
})

// 서버로 보낼 repeat 객체
const repeat = computed(() => {
  if (repeatType.value === 'NONE') return null
  if (repeatType.value === 'WEEKLY') {
    return { type: 'WEEKLY', weekdays: repeatWeekdays.value }
  }
  return { type: repeatType.value }
})

// 제출
const submitTodo = async () => {
  if (!title.value) return
  try {
    isSubmitting.value = true
    const token = localStorage.getItem('token')

    const payload = {
      title: title.value,
      description: description.value,
      priority: priority.value,
      category: category.value,
      status: 'IN_PROGRESS',
      dueDate: dueAt.value,
      // repeat: repeat.value,
    }

    await api.post('/todo', payload, {
      headers: { Authorization: 'Bearer ' + token },
    })

    router.push('/todo')
  } catch (err) {
    alert('오류 발생: ' + (err?.response?.data?.message ?? err.message))
  } finally {
    isSubmitting.value = false
  }
}
</script>

<style scoped>
/* 기존 스타일 그대로 + 추가 UI만 덧붙임 */
.page {
  min-height: 100vh;
  padding: 110px 16px 90px;
  background: radial-gradient(circle at 30% 10%, #dfe8d8 0%, #cfdac7 100%);
  font-family: 'Pretendard','Inter',sans-serif;
}
.wrap { max-width: 980px; margin: 0 auto; }
.topbar { display:flex; justify-content:space-between; align-items:flex-start; gap:16px; margin-bottom:18px; }
.title { display:flex; gap:12px; align-items:center; }
.emoji { font-size:28px; line-height:1; }
.title h2 { margin:0; font-size:26px; font-weight:800; }
.sub { margin:6px 0 0; color:rgba(0,0,0,.6); font-size:14px; }

.btn-ghost {
  border: 1px solid rgba(0,0,0,.2);
  background: rgba(255,255,255,.7);
  padding: 10px 14px;
  border-radius: 12px;
  text-decoration: none;
  color: rgba(0,0,0,.75);
  font-weight: 600;
  transition: .15s ease;
}
.btn-ghost:hover { background: rgba(255,255,255,.9); transform: translateY(-1px); }

.panel {
  background:#fff;
  border-radius:28px;
  box-shadow:0 22px 60px rgba(0,0,0,.12);
  padding:26px;
}

.form { display:flex; flex-direction:column; gap:18px; }
.field { display:flex; flex-direction:column; gap:8px; }
.label { font-weight:800; color:rgba(0,0,0,.8); display:flex; align-items:center; gap:8px; }
.req { color:#ef4444; font-weight:900; }

.input, .textarea, .select {
  border: 1px solid rgba(0,0,0,.12);
  border-radius: 14px;
  padding: 12px 14px;
  font-size: 15px;
  outline: none;
  transition: border-color .15s ease, box-shadow .15s ease;
}
.textarea { resize: vertical; min-height: 120px; }
.input:focus, .textarea:focus, .select:focus {
  border-color: rgba(37,99,235,.7);
  box-shadow: 0 0 0 4px rgba(37,99,235,.12);
}
.hint { font-size:12px; color:rgba(0,0,0,.5); display:flex; justify-content:flex-end; }

.grid2 { display:grid; grid-template-columns:1fr 1fr; gap:18px; }

/* 칩 */
.chips { display:flex; flex-wrap:wrap; gap:8px; margin-bottom:8px; }
.chip {
  border:1px solid rgba(0,0,0,.12);
  background:rgba(0,0,0,.03);
  border-radius:999px;
  padding:8px 12px;
  cursor:pointer;
  font-weight:700;
  color:rgba(0,0,0,.75);
  transition:.15s ease;
}
.chip:hover { transform: translateY(-1px); }
.chip.active { border-color: rgba(37,99,235,.45); background: rgba(37,99,235,.10); color: rgba(0,0,0,.85); }
.chip-emoji { margin-right:6px; }

/* 우선순위 */
.priority-row { display:flex; flex-wrap:wrap; gap:8px; margin-bottom:8px; }
.pill {
  border:1px solid rgba(0,0,0,.12);
  background:rgba(0,0,0,.03);
  border-radius:14px;
  padding:10px 12px;
  cursor:pointer;
  font-weight:800;
  color:rgba(0,0,0,.75);
  display:flex;
  align-items:center;
  gap:8px;
  transition:.15s ease;
}
.pill .dot { width:10px; height:10px; border-radius:50%; background:rgba(0,0,0,.25); }
.pill.active { border-color: rgba(37,99,235,.45); background: rgba(37,99,235,.10); }
.pill.active .dot { background: rgba(37,99,235,.75); }

/* 마감 사용자 지정 row */
.row { display:flex; gap:10px; }

/* 매주 요일 */
.weekday { display:flex; flex-wrap:wrap; gap:8px; margin-top:4px; }
.day {
  border:1px solid rgba(0,0,0,.12);
  background:rgba(0,0,0,.03);
  border-radius:12px;
  padding:8px 10px;
  cursor:pointer;
  font-weight:900;
  color:rgba(0,0,0,.7);
  transition:.15s ease;
}
.day.active { border-color: rgba(37,99,235,.45); background: rgba(37,99,235,.10); }

/* footer */
.footer {
  margin-top:6px;
  display:flex;
  justify-content:space-between;
  align-items:center;
  gap:14px;
  padding-top:14px;
  border-top:1px solid rgba(0,0,0,.08);
}
.mini { display:flex; flex-wrap:wrap; align-items:center; gap:8px; }
.mini-badge {
  font-size:12px;
  font-weight:900;
  padding:6px 10px;
  border-radius:999px;
  background:rgba(0,0,0,.06);
}
.mini-title { font-weight:900; }
.mini-meta { color:rgba(0,0,0,.55); font-weight:700; }

.btn-primary {
  border:none;
  border-radius:14px;
  padding:12px 16px;
  font-weight:900;
  cursor:pointer;
  min-width:160px;
  background:#2563eb;
  color:#fff;
  box-shadow:0 10px 22px rgba(37,99,235,.22);
  transition:.15s ease;
}
.btn-primary:hover { transform: translateY(-1px); }
.btn-primary:disabled { opacity:.55; cursor:not-allowed; transform:none; }

@media (max-width: 860px) {
  .grid2 { grid-template-columns: 1fr; }
  .footer { flex-direction: column; align-items: stretch; }
  .btn-primary { width: 100%; }
}
</style>
