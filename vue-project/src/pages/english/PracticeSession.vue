<template>
  <div class="we-page">
<!--    <header class="we-topbar">-->
<!--      <div class="we-container we-topbar__inner">-->
<!--        <div class="we-brand" @click="go('/practice')">-->
<!--          <div class="we-logo">W</div>-->
<!--          <div>-->
<!--            <div class="we-brand__title">WORKLY <span>ENGLISH</span></div>-->
<!--            <div class="we-brand__sub">Practice Session</div>-->
<!--          </div>-->
<!--        </div>-->

<!--        <div class="we-topbar__right">-->
<!--          <button class="we-btn we-btn&#45;&#45;small we-btn&#45;&#45;outline" @click="go('/wrong-notes')">-->
<!--            <i class="fa-solid fa-book-bookmark"></i> 오답노트-->
<!--          </button>-->
<!--        </div>-->
<!--      </div>-->
<!--    </header>-->

    <main class="we-container we-session">
      <div v-if="loading && questions.length === 0" class="we-loading-state">
        <i class="fa-solid fa-circle-notch fa-spin"></i> 문제를 불러오는 중입니다...
      </div>

      <div v-else-if="isBatchComplete" class="we-complete-state">
        <div class="we-complete-icon">🎉</div>
        <h2 class="we-complete-title">오늘의 연습 완료!</h2>
        <p class="we-complete-sub">한 세트(10문제)를 모두 정복하셨습니다.</p>

        <div class="we-complete-actions">
          <button @click="handleLoadMore" class="we-btn we-btn--primary">
            <i class="fa-solid fa-rotate-right"></i> 10문제 더 풀기
          </button>
          <button @click="go('/wrong-notes')" class="we-btn we-btn--outline">
            <i class="fa-solid fa-book"></i> 오답노트 확인
          </button>
        </div>
      </div>

      <div v-else-if="questions.length > 0">
        <div class="we-sessionHead">
          <div>
            <h1 class="we-sessionTitle">RC 문제풀이</h1>
            <div class="we-sessionSub">Part 5 · 문법/어휘 빈칸 채우기</div>

            <div class="we-chipRow" style="margin-top:10px;">
              <div class="we-chip"><i class="fa-solid fa-layer-group"></i> RC</div>
              <div class="we-chip"><i class="fa-solid fa-medal"></i> {{ level }}</div>
              <div class="we-chip"><i class="fa-solid fa-hashtag"></i> {{ tags.join(" · ") }}</div>
            </div>
          </div>

          <div class="we-sessionHead__right">
            <div class="we-progressTop">
              <div class="we-progressTop__row">
                <div class="we-progressTop__label">Current Batch Progress</div>
                <div class="we-progressTop__value">{{ index + 1 }} / {{ questions.length }}</div>
              </div>
              <div class="we-progressTop__bar">
                <div class="we-progressTop__fill" :style="{ width: progress + '%' }"></div>
              </div>
            </div>
          </div>
        </div>

        <RcQuestionRenderer
            v-if="current"
            :question="current"
            :selected-index="selectedIndex"
            :result="result"
            :show-explanation="showExplanation"
            :timer-text="timerText"
            @select="onSelect"
            @grade="onGrade"
            @toggle-explain="showExplanation = !showExplanation"
            @next="onNext"
            @save-wrong="onSaveWrong"
        />
      </div>

      <div v-else class="we-empty-state">
        <p>풀어볼 문제가 없습니다.</p>
        <button @click="fetchQuestions(null)" class="we-btn we-btn--primary" style="margin-top:20px;">
          처음부터 다시 시작
        </button>
      </div>

      <div class="we-bottomTabs">
        <div class="we-bottomTabs__inner">
          <button class="we-tabBtn is-active" @click="go('/practice/rc')">
            <i class="fa-solid fa-file-pen"></i> RC
          </button>
          <button class="we-tabBtn" @click="go('/practice/vocab')">
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
import RcQuestionRenderer from "@/components/practice/renderers/RcQuestionRenderer.vue";

const router = useRouter();
const go = (p) => router.push(p);

// 상태 관리
const questions = ref([]);
const loading = ref(false);
const isBatchComplete = ref(false);
const lastId = ref(null);
const index = ref(0);
const selectedIndex = ref(null);
const result = ref(null);
const showExplanation = ref(false);

// 타이머 관리
const timerText = ref("01:24");
let t = 84;
let timerInterval = null;

const mapAnswerToIndex = (ans) => {
  if (!ans) return 0;
  return { 'A': 0, 'B': 1, 'C': 2, 'D': 3 }[ans.toUpperCase().trim()] ?? 0;
};

// API 호출
async function fetchQuestions(targetId = null) {
  loading.value = true;
  isBatchComplete.value = false; // 새 문제를 부를 땐 완료 상태 초기화
  try {
    const size = 10;
    const url = `/api/english?size=${size}${targetId ? `&lastId=${targetId}` : ''}`;
    const response = await fetch(url);
    if (!response.ok) throw new Error("네트워크 응답이 좋지 않습니다.");

    const data = await response.json();
    const questionList = data?.ok;

    if (Array.isArray(questionList) && questionList.length > 0) {
      questions.value = questionList.map(q => {
        const subQuestions = q.content?.questions || [];
        return {
          ...q,
          content: {
            ...q.content,
            questions: subQuestions.map(subQ => ({
              ...subQ,
              correctIndex: mapAnswerToIndex(subQ.answer)
            }))
          }
        };
      });

      lastId.value = questionList[questionList.length - 1]._id || questionList[questionList.length - 1].id;
      index.value = 0;
    } else {
      if (targetId) alert("더 이상 불러올 문제가 없습니다.");
    }
  } catch (error) {
    console.error("Fetch Error:", error);
    alert("문제를 불러오는 중 오류가 발생했습니다.");
  } finally {
    loading.value = false;
  }
}

function handleLoadMore() {
  fetchQuestions(lastId.value);
}

onMounted(() => {
  fetchQuestions(null);

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
const current = computed(() => questions.value[index.value]);
const total = computed(() => questions.value.length);
const level = computed(() => current.value?.level ?? "—");
const tags = computed(() => current.value?.tags ?? []);
const progress = computed(() => {
  if (total.value === 0) return 0;
  return Math.round(((index.value + 1) / total.value) * 100);
});

// 인터랙션 함수
function onSelect(i) {
  if (result.value) return;
  selectedIndex.value = i;
}

function onGrade() {
  if (selectedIndex.value === null) return;

  const correctIndex = current.value.content.questions[0].correctIndex;
  const isCorrect = selectedIndex.value === correctIndex;

  result.value = { isCorrect, correctIndex };

  saveEnglishLog(isCorrect);

  if (!isCorrect) showExplanation.value = true;
}
async function saveEnglishLog(isCorrect) {
  const q = current.value;
  const chosenAnswer = String.fromCharCode(65 + selectedIndex.value);

  const payload = {
    objectId: q._id || q.id,
    chosenAnswer: chosenAnswer,
    isCorrect: isCorrect,
    category: q.type,
    part: q.part,
    level: q.level
  };

  try {
    const token = localStorage.getItem('token');
    const res = await fetch('/api/english/log', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(payload)
    });

    if (!res.ok)
      throw new Error("로그 저장 실패");
  } catch (err) {
    console.error("Save Log Error:", err);
  }
}

function onNext() {
  // 마지막 문제인 경우 완료 화면으로 전환
  if (index.value >= total.value - 1) {
    questions.value = []; // 문제 배열 비우기
    isBatchComplete.value = true;
    return;
  }

  // 다음 문제로 초기화
  index.value += 1;
  selectedIndex.value = null;
  result.value = null;
  showExplanation.value = false;
  t = 84;
}

async function onSaveWrong() {
  if (!result.value) return;
  const q = current.value;
  const cq = q.content.questions[0];

  const payload = {
    questionId: q._id || q.id,
    type: q.type,
    part: q.part,
    level: q.level,
    tags: q.tags,
    passage: q.content.passage,
    options: cq.options,
    selectedIndex: selectedIndex.value,
    correctIndex: result.value.correctIndex,
    explanation: cq.explanation,
    solvedAt: new Date().toISOString()
  };

  try {
    const res = await fetch('/api/v1/learning-logs', {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify(payload)
    });
    if (res.ok) alert("오답노트에 저장되었습니다.");
  } catch (err) {
    console.error("Save Wrong Error:", err);
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
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-15px);
  }
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
</style>