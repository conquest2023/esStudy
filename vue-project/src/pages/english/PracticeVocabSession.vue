<template>
  <div class="we-page">
    <main class="we-container we-session">

      <div v-if="loading && vocabList.length === 0" class="we-loading-state">
        <i class="fa-solid fa-circle-notch fa-spin"></i>
        <p>단어 데이터를 불러오는 중입니다...</p>
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
            <i class="fa-solid fa-book-bookmark"></i> 오답노트 확인
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
              <span class="we-chip" v-if="tags.length"><i class="fa-solid fa-hashtag"></i> {{ tags.join(" · ") }}</span>
            </div>
          </div>

          <div class="we-status-card">
            <div class="we-status-card__top">
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
            <i class="fa-solid fa-file-pen"></i> <span>RC</span>
          </button>
          <button class="we-tabBtn is-active" @click="go('/practice/vocab')">
            <i class="fa-solid fa-spell-check"></i> <span>단어</span>
          </button>
          <button class="we-tabBtn" @click="go('/practice/speaking')">
            <i class="fa-solid fa-microphone-lines"></i> <span>회화</span>
          </button>
          <button class="we-tabBtn" @click="go('/wrong-notes')">
            <i class="fa-solid fa-book-bookmark"></i> <span>오답노트</span>
          </button>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import "@/assets/workly-english.css";
import { computed, onMounted, ref, onUnmounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import VocabQuestionRenderer from "@/components/practice/renderers/VocabQuestionRenderer.vue";

const router = useRouter();
const route = useRoute(); // Query 파라미터(level)를 읽기 위해 추가

const go = (p) => {
  if (router.currentRoute.value.path === p) return;
  router.push(p);
};

const props = defineProps({
  question: {type: Object, required: true},
  selectedIndex: {type: Number, default: null},
  result: {type: Object, default: null},
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

// 정답 문자 매핑
const mapAnswerToIndex = (ans) => {
  if (!ans) return 0;
  return { 'A': 0, 'B': 1, 'C': 2, 'D': 3 }[ans.toUpperCase().trim()] ?? 0;
};

// 데이터 페칭 (난이도 선택 로직 반영)
async function fetchVocab(targetId = null) {
  loading.value = true;
  isBatchComplete.value = false;

  try {
    const size = 10;
    const targetLevel = route.query.level; // 이전 페이지에서 넘긴 난이도 (BRONZE, SILVER 등)
    let url = '';

    // 난이도가 존재하고 'RANDOM'이 아니면 난이도별 API 호출, 아니면 전체 랜덤 호출
    if (targetLevel && targetLevel !== 'RANDOM') {
      url = `/api/vocab/${targetLevel}?size=${size}${targetId ? `&lastId=${targetId}` : ''}`;
    } else {
      url = `/api/vocab?size=${size}${targetId ? `&lastId=${targetId}` : ''}`;
    }

    const response = await fetch(url);
    if (!response.ok) throw new Error("단어 데이터를 불러오지 못했습니다.");

    const data = await response.json();
    const list = data?.ok;

    if (Array.isArray(list) && list.length > 0) {
      vocabList.value = list.map(v => {
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

  saveEnglishLog(isCorrect);
  if (!isCorrect) showExplanation.value = true;
}

async function saveEnglishLog(isCorrect) {
  const q = current.value;
  const chosenAnswer = String.fromCharCode(65 + selectedIndex.value);
  const token = localStorage.getItem('token');

  const payload = {
    objectId: q._id || q.id,
    chosenAnswer: chosenAnswer,
    isCorrect: isCorrect,
    category: q.type,
    part: q.part || 0,
    level: q.level
  };

  try {
    await fetch('/api/english/log', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
      body: JSON.stringify(payload)
    });
  } catch (err) { console.error("Log Save Error:", err); }
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
  if (!result.value) { alert('문제를 먼저 풀어주세요.'); return; }
  if (!current.value) return;
  if (!confirm('오답노트에 저장하시겠습니까?')) return;

  const token = localStorage.getItem('token');
  if (!token) { alert('로그인이 필요합니다.'); router.push('/login'); return; }

  const q = current.value;
  const payload = {
    objectId: q._id || q.id,
    category: q.type,
    part: q.part || 0,
    level: q.level
  };

  try {
    const res = await fetch('/api/wrongnote', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
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
/* 공통 스타일 */
.we-loading-state, .we-empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 400px;
  color: #64748b;
  font-weight: 600;
  gap: 12px;
}
.we-loading-state i {
  font-size: 2.5rem;
  color: #2563eb;
}

.we-complete-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
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
  font-size: 1.75rem;
  font-weight: 900;
  color: #1e293b;
  margin-bottom: 10px;
}
.we-complete-sub {
  font-size: 1rem;
  color: #64748b;
  margin-bottom: 32px;
}
.we-complete-actions {
  display: flex;
  gap: 14px;
  width: 100%;
  max-width: 400px;
  justify-content: center;
}

/* =========================================
   모바일 UI 최적화 (768px 이하)
   ========================================= */
@media (max-width: 768px) {

  /* 1. 전체 컨테이너 여백 축소 (하단 탭 가림 방지) */
  .we-session {
    padding: 16px 16px 100px 16px;
  }

  /* 2. 헤더 섹션 간격 및 폰트 크기 최적화 */
  .we-sessionHead {
    flex-direction: column; /* 좌우 배치를 상하 배치로 변경 */
    align-items: flex-start;
    gap: 16px;
    margin-bottom: 20px;
  }

  .we-sessionTitle {
    font-size: 1.4rem;
    margin-bottom: 4px;
  }

  .we-sessionSub {
    font-size: 0.85rem;
  }

  /* 3. 칩(VOCA, LEVEL 등) 가로 스크롤 허용 및 크기 축소 */
  .we-chipRow {
    flex-wrap: nowrap;
    overflow-x: auto;
    width: 100%;
    padding-bottom: 4px;
    -webkit-overflow-scrolling: touch; /* iOS 부드러운 스크롤 */
  }
  .we-chip {
    font-size: 0.75rem;
    padding: 4px 10px;
    white-space: nowrap;
  }

  /* 4. 진행률(Status) 카드 압축 */
  .we-status-card {
    width: 100%;
    padding: 12px 16px;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.05); /* 모바일에서는 얕은 그림자 */
    border: 1px solid #f1f5f9;
  }
  .we-progress-text {
    font-size: 0.85rem;
  }

  /* 5. 완료 화면 버튼 세로 배치 및 터치 영역 확대 */
  .we-complete-state {
    padding: 40px 20px;
    margin-top: 20px;
  }
  .we-complete-title {
    font-size: 1.5rem;
  }
  .we-complete-sub {
    word-break: keep-all; /* 단어 단위로 끊어지게 처리 */
  }
  .we-complete-actions {
    flex-direction: column;
    max-width: 100%;
  }
  .we-complete-actions .we-btn {
    width: 100%;
    padding: 14px;
    font-size: 1rem;
    display: flex;
    justify-content: center;
    align-items: center;
  }

  /* 6. 하단 탭(Bottom Tabs) 아이폰 Safe Area 대응 및 아이콘/텍스트 세로 배치 */
  .we-bottomTabs {
    padding-bottom: env(safe-area-inset-bottom, 16px);
  }
  .we-bottomTabs__inner {
    padding: 8px 16px;
  }
  .we-tabBtn {
    display: flex;
    flex-direction: column; /* 세로 배치 */
    align-items: center;
    justify-content: center;
    gap: 4px; /* 아이콘과 텍스트 사이 간격 */
    font-size: 0.7rem; /* 텍스트 크기 축소 */
    padding: 8px 0;
  }
  .we-tabBtn i {
    font-size: 1.3rem; /* 모바일에서 누르기 쉽게 아이콘 크기 약간 확대 */
    margin-right: 0;
  }
}
</style>