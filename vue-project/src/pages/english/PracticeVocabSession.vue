<template>
  <div class="we-page">


    <main class="we-container we-session">
      <div v-if="loading && vocabList.length === 0" class="we-loading-state">
        <i class="fa-solid fa-circle-notch fa-spin"></i> 단어 데이터를 불러오는 중입니다...
      </div>

      <div v-else-if="isBatchComplete" class="we-complete-state">
        <div class="we-complete-icon">🎉</div>
        <h2 class="we-complete-title">단어 학습 완료!</h2>
        <p class="we-complete-sub">한 세트(10단어)를 모두 정복하셨습니다.</p>

        <div class="we-complete-actions">
          <button @click="handleLoadMore" class="we-btn we-btn--primary">
            <i class="fa-solid fa-rotate-right"></i> 10문제 더 풀기
          </button>
          <button @click="go('/wrong-notes')" class="we-btn we-btn--outline">
            <i class="fa-solid fa-book"></i> 오답노트 확인
          </button>
        </div>
      </div>

      <div v-else-if="vocabList.length > 0" class="we-content-wrapper">
        <div class="we-sessionHead">
          <div class="we-sessionHead__main">
            <h1 class="we-sessionTitle">단어 학습</h1>
            <p class="we-sessionSub">뜻/용법 객관식 · 예문 기반</p>

            <div class="we-chipRow">
              <span class="we-chip"><i class="fa-solid fa-layer-group"></i> VOCA</span>
              <span class="we-chip"><i class="fa-solid fa-medal"></i> {{ level }}</span>
              <span class="we-chip"><i class="fa-solid fa-hashtag"></i> {{ tags.join(" · ") }}</span>
            </div>
          </div>

          <div class="we-status-card">
            <div class="we-status-card__top">
<!--              <div class="we-timer">-->
<!--                <i class="fa-solid fa-clock-rotate-left"></i>-->
<!--                <span>{{ timerText }}</span>-->
<!--              </div>-->
              <div class="we-progress-text">
                <strong>{{ index + 1 }}</strong> / {{ total }}
              </div>
              <div class="we-progressTop__bar">
                <div class="we-progressTop__fill" :style="{ width: progress + '%' }"></div>
              </div>
            </div>
          </div>
        </div>

        <VocabQuestionRenderer
            v-if="current"
            :question="current"
            :selected-index="selectedIndex"
            :result="result"
            :show-explanation="showExplanation"
            :timer-text="timerText"
            :starred="starred"
            @select="onSelect"
            @grade="onGrade"
            @toggle-explain="showExplanation = !showExplanation"
            @next="onNext"
            @toggle-star="starred = !starred"
            @save-wrong="saveWrong"
        />
      </div>

      <div v-else class="we-empty-state">
        <p>학습할 단어가 없습니다.</p>
        <button @click="fetchVocab(null)" class="we-btn we-btn--primary" style="margin-top:20px;">
          처음부터 다시 시작
        </button>
      </div>

      <div class="we-bottomTabs">
        <div class="we-bottomTabs__inner">
          <button class="we-tabBtn" @click="go('/practice/rc')">
            <i class="fa-solid fa-file-pen"></i> RC
          </button>
          <button class="we-tabBtn is-active" @click="go('/practice/vocab')">
            <i class="fa-solid fa-spell-check"></i> 단어
          </button>
          <button class="we-tabBtn" @click="go('/practice/speaking')">
            <i class="fa-solid fa-microphone-lines"></i> 회화
          </button>
          <button class="we-tabBtn" @click="go('/wrong-notes')">
            <i class="fa-solid fa-book-bookmark"></i> 오답노트
          </button>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import "@/assets/workly-english.css";
import { computed, onMounted, ref, onUnmounted } from "vue";
import { useRouter } from "vue-router";
import VocabQuestionRenderer from "@/components/practice/renderers/VocabQuestionRenderer.vue";

const router = useRouter();
const go = (p) => {
  if (router.currentRoute.value.path === p) return;
  router.push(p);
};
const props = defineProps({
  question: {type: Object, required: true},
  selectedIndex: {type: Number, default: null},
  result: {type: Object, default: null}, // {isCorrect, correctIndex}
  showExplanation: {type: Boolean, default: false},
  timerText: {type: String, default: "00:00"},
  starred: {type: Boolean, default: false},
});
// 상태 관리
const vocabList = ref([]);
const loading = ref(false);
const isBatchComplete = ref(false);
const lastId = ref(null);
const index = ref(0);
const selectedIndex = ref(null);
const result = ref(null);
const showExplanation = ref(false);
const starred = ref(false);

// 타이머 관리
const timerText = ref("01:24");
let t = 84;
let timerInterval = null;

// 정답 문자('A','B'..)를 인덱스(0,1..)로 변환
const mapAnswerToIndex = (ans) => {
  if (!ans) return 0;
  return { 'A': 0, 'B': 1, 'C': 2, 'D': 3 }[ans.toUpperCase().trim()] ?? 0;
};

async function fetchVocab(targetId = null) {
  loading.value = true;
  isBatchComplete.value = false;
  try {
    const size = 10;
    const url = `/api/vocab?size=${size}${targetId ? `&lastId=${targetId}` : ''}`;
    const response = await fetch(url);
    if (!response.ok) throw new Error("단어 데이터를 불러오지 못했습니다.");

    const data = await response.json();
    const list = data?.ok;

    if (Array.isArray(list) && list.length > 0) {
      vocabList.value = list.map(v => {
        // 단어 문제는 content.questions[0] 구조를 유지한다고 가정
        const subQuestions = v.content?.questions || [];
        return {
          ...v,
          content: {
            ...v.content,
            questions: subQuestions.map(subQ => ({
              ...subQ,
              correctIndex: mapAnswerToIndex(subQ.answer)
            }))
          }
        };
      });
      lastId.value = list[list.length - 1]._id || list[list.length - 1].id;
      index.value = 0;
    } else {
      if (targetId) alert("더 이상 불러올 단어가 없습니다.");
    }
  } catch (error) {
    console.error("Fetch Error:", error);
  } finally {
    loading.value = false;
  }
}

function handleLoadMore() {
  fetchVocab(lastId.value);
}

onMounted(() => {
  fetchVocab(null);
  timerInterval = setInterval(() => {
    t = Math.max(0, t - 1);
    const mm = String(Math.floor(t / 60)).padStart(2, "0");
    const ss = String(t % 60).padStart(2, "0");
    timerText.value = `${mm}:${ss}`;
  }, 1000);
});

onUnmounted(() => {
  if (timerInterval) clearInterval(timerInterval);
});

// Computed 속성
const current = computed(() => vocabList.value[index.value]);
const total = computed(() => vocabList.value.length);
const level = computed(() => current.value?.level ?? "—");
const tags = computed(() => current.value?.tags ?? []);
const progress = computed(() => {
  if (total.value === 0) return 0;
  return Math.round(((index.value + 1) / total.value) * 100);
});

// 액션 함수
function onSelect(i) {
  if (result.value) return;
  selectedIndex.value = i;
}

function onGrade() {
  if (selectedIndex.value === null) return;
  const correctIndex = current.value.content.questions[0].correctIndex;
  const isCorrect = selectedIndex.value === correctIndex;
  result.value = { isCorrect, correctIndex };

  // 로그 저장 (자동)
  saveEnglishLog(isCorrect);

  if (!isCorrect) showExplanation.value = true;
}

// 학습 로그 저장 API 호출
async function saveEnglishLog(isCorrect) {
  const q = current.value;
  const chosenAnswer = String.fromCharCode(65 + selectedIndex.value);
  const token = localStorage.getItem('token');

  const payload = {
    objectId: q._id || q.id,
    chosenAnswer: chosenAnswer,
    isCorrect: isCorrect,
    category: q.type, // "VOCA"
    part: q.part || 0,
    level: q.level
  };

  try {
    await fetch('/api/english/log', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(payload)
    });
  } catch (err) {
    console.error("Log Save Error:", err);
  }
}

function onNext() {
  if (index.value >= total.value - 1) {
    vocabList.value = [];
    isBatchComplete.value = true;
    return;
  }
  index.value += 1;
  selectedIndex.value = null;
  result.value = null;
  showExplanation.value = false;
  starred.value = false;
  t = 84;
}

async function saveWrong() {
  // 1. props가 아니라 로컬 ref인 result.value 확인
  if (!result.value) {
    alert('문제를 먼저 풀어주세요.');
    return;
  }

  // 2. 현재 문제 데이터 확인
  if (!current.value) return;

  if (!confirm('오답노트에 저장하시겠습니까?')) return;

  const token = localStorage.getItem('token');
  if (!token) {
    alert('로그인이 필요합니다.');
    router.push('/login');
    return;
  }

  // 3. 페이로드 구성 (current.value 참조)
  const q = current.value;
  const payload = {
    objectId: q._id || q.id, // 몽고DB ID 우선 참조
    category: q.type,       // "VOCA"
    part: q.part || 0,
    level: q.level
  };

  try {
    // 4. api.post 대신 fetch 사용 (또는 axios 임포트 확인)
    const res = await fetch('/api/wrongnote', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(payload)
    });

    if (res.ok) {
      alert('오답노트에 저장되었습니다.');
    } else {
      const errData = await res.json();
      throw new Error(errData.message || '저장 실패');
    }
  } catch (e) {
    console.error('오답노트 저장 실패', e);
    alert('오답노트 저장에 실패했습니다.');
  }
}
</script>

<style scoped>
.we-loading-state, .we-empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 400px;
  color: #64748b;
  font-weight: 800;
}
.we-loading-state i {
  font-size: 2rem;
  margin-bottom: 1rem;
  color: #2563eb;
}
.we-complete-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  text-align: center;
  background: white;
  border-radius: 24px;
  box-shadow: 0 10px 30px -5px rgba(0, 0, 0, 0.05);
  margin-top: 40px;
}
.we-complete-icon {
  font-size: 72px;
  margin-bottom: 24px;
  animation: complete-bounce 1.5s infinite ease-in-out;
}
@keyframes complete-bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-15px); }
}
.we-complete-title {
  font-size: 28px;
  font-weight: 900;
  color: #1e293b;
  margin-bottom: 10px;
}
.we-complete-sub {
  font-size: 16px;
  color: #64748b;
  margin-bottom: 32px;
}
.we-complete-actions {
  display: flex;
  gap: 14px;
}
/* 모바일 텍스트 숨김 처리 (좁은 화면) */
@media (max-width: 380px) {
  .we-mobile-hide-text span {
    display: none;
  }
  .we-mobile-hide-text {
    padding: 8px;
  }
}

/* 로딩 및 완료 상태 */
.we-loading-state, .we-complete-state {
  padding: 60px 20px;
  text-align: center;
}

.we-complete-actions {
  display: grid;
  grid-template-columns: 1fr;
  gap: 10px;
  width: 100%;
  max-width: 300px;
}

@media (min-width: 480px) {
  .we-complete-actions {
    grid-template-columns: 1fr 1fr;
  }
}
</style>