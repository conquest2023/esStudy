<template>
  <div class="we-page">
    <header class="we-topbar">
      <div class="we-container we-topbar__inner">
        <div class="we-brand" @click="go('/practice')">
          <div class="we-logo">W</div>
          <div>
            <div class="we-brand__title">WORKLY <span>ENGLISH</span></div>
            <div class="we-brand__sub">Practice Session</div>
          </div>
        </div>

        <div class="we-topbar__right">
          <button class="we-btn we-btn--small we-btn--outline" @click="go('/wrong-notes')">
            <i class="fa-solid fa-book-bookmark"></i> 오답노트
          </button>
        </div>
      </div>
    </header>

    <main class="we-container we-session">
      <div class="we-sessionHead">
        <div>
          <h1 class="we-sessionTitle">단어 학습</h1>
          <div class="we-sessionSub">뜻/용법 객관식 · 예문 기반</div>

          <div class="we-chipRow" style="margin-top:10px;">
            <div class="we-chip"><i class="fa-solid fa-layer-group"></i> VOCA</div>
            <div class="we-chip"><i class="fa-solid fa-medal"></i> {{ level }}</div>
            <div class="we-chip"><i class="fa-solid fa-hashtag"></i> {{ tags.join(" · ") }}</div>
          </div>
        </div>

        <div style="min-width:260px; width: 320px; max-width: 45vw;">
          <div class="we-progressTop">
            <div class="we-progressTop__row">
              <div class="we-progressTop__label">Progress</div>
              <div class="we-progressTop__value">{{ index + 1 }} / {{ total }}</div>
            </div>
            <div class="we-progressTop__bar">
              <div class="we-progressTop__fill" :style="{ width: progress + '%' }"></div>
            </div>
          </div>
        </div>
      </div>

      <VocabQuestionRenderer
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
          @save-wrong="onSaveWrong"
          @toggle-star="starred = !starred"
      />

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
import { computed, onBeforeUnmount, onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import VocabQuestionRenderer from "@/components/practice/renderers/VocabQuestionRenderer.vue";

const router = useRouter();
const go = (p) => {
  if (router.currentRoute.value.path === p) return;
  router.push(p);
};

const vocabList = ref([
  {
    id: "mock-vocab-001",
    type: "VOCA",
    part: 0,
    level: "SILVER",
    tags: ["it", "refactoring"],
    content: {
      word: "Refactoring",
      pronunciation: "/ˌriːˈfæk.tər.ɪŋ/",
      example: "We refactored the service layer to improve maintainability.",
      questions: [
        {
          options: ["코드 구조 개선", "기능 전면 수정", "데이터베이스 삭제", "성능 테스트"],
          correctIndex: 0,
          explanation: "refactoring은 기능 변경 없이 코드 구조를 개선하는 것을 의미합니다.",
        },
      ],
    },
  },
  {
    id: "mock-vocab-002",
    type: "VOCA",
    part: 0,
    level: "GOLD",
    tags: ["infra", "scalability"],
    content: {
      word: "Scalability",
      pronunciation: "/ˌskeɪ.ləˈbɪl.ə.ti/",
      example: "Scalability is critical when traffic spikes unexpectedly.",
      questions: [
        {
          options: ["확장성", "신뢰성", "가용성", "보안성"],
          correctIndex: 0,
          explanation: "scalability는 트래픽 증가에 따라 시스템이 확장 가능한 성질입니다.",
        },
      ],
    },
  },
]);

const index = ref(0);
const selectedIndex = ref(null);
const result = ref(null);
const showExplanation = ref(false);
const starred = ref(false);

const total = computed(() => vocabList.value.length);
const current = computed(() => vocabList.value[index.value]);

const level = computed(() => current.value?.level ?? "—");
const tags = computed(() => current.value?.tags ?? []);
const progress = computed(() => Math.round(((index.value + 1) / total.value) * 100));

const timerText = ref("01:24");
let t = 84;
let timer = null;

function resetTimer() {
  t = 84;
  timerText.value = "01:24";
}

onMounted(() => {
  timer = setInterval(() => {
    t = Math.max(0, t - 1);
    const mm = String(Math.floor(t / 60)).padStart(2, "0");
    const ss = String(t % 60).padStart(2, "0");
    timerText.value = `${mm}:${ss}`;
  }, 1000);
});

onBeforeUnmount(() => {
  if (timer) clearInterval(timer);
});

function onSelect(i) {
  if (result.value) return;
  selectedIndex.value = i;
}

function onGrade() {
  if (selectedIndex.value === null) return;
  const cq = current.value.content.questions[0];
  const correctIndex = cq.correctIndex;
  const isCorrect = selectedIndex.value === correctIndex;
  result.value = { isCorrect, correctIndex };
  if (!isCorrect) showExplanation.value = true;
}

function onNext() {
  if (index.value >= total.value - 1) {
    go("/practice");
    return;
  }
  index.value += 1;
  selectedIndex.value = null;
  result.value = null;
  showExplanation.value = false;
  starred.value = false;
  resetTimer();
}

function onSaveWrong() {
  if (!result.value) return;
  const q = current.value;
  const cq = q.content.questions[0];

  console.log("SAVE WRONG (VOCA):", {
    questionId: q.id,
    type: q.type,
    level: q.level,
    tags: q.tags,
    word: q.content.word,
    example: q.content.example,
    options: cq.options,
    selectedIndex: selectedIndex.value,
    correctIndex: result.value.correctIndex,
    explanation: cq.explanation,
  });
}
</script>
