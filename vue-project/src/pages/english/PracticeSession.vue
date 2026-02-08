<template>
  <div class="we-page">
    <!-- reuse topbar from home? 간단 버전 -->
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
          <h1 class="we-sessionTitle">RC 문제풀이</h1>
          <div class="we-sessionSub">Part 5 · 문법/어휘 빈칸 채우기</div>

          <div class="we-chipRow" style="margin-top:10px;">
            <div class="we-chip"><i class="fa-solid fa-layer-group"></i> RC</div>
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

      <!-- RC Renderer -->
      <RcQuestionRenderer
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

      <!-- Bottom Tabs (fixed) -->
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
import { computed, onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import RcQuestionRenderer from "@/components/practice/renderers/RcQuestionRenderer.vue";

const router = useRouter();
const go = (p) => router.push(p);

/**
 * 지금은 네가 준 샘플 JSON 기반 “프론트 더미”.
 * 나중에 store + API로 교체하면 됨.
 */
const questions = ref([
  {
    id: "6986d1d732a8d47778dd0fb9",
    type: "RC",
    part: 5,
    level: "BRONZE",
    tags: ["grammar", "adverb", "it-performance"],
    content: {
      passage: "The new backend server is ------- more efficient than the previous one.",
      questions: [
        {
          options: ["signify", "significant", "significantly", "significance"],
          correctIndex: 2,
          explanation: "형용사 more efficient를 수식하는 부사 significantly가 정답입니다.",
        },
      ],
    },
  },
  {
    id: "6986d1d732a8d47778dd0fba",
    type: "RC",
    part: 5,
    level: "SILVER",
    tags: ["grammar", "adverb", "refactoring"],
    content: {
      passage: "The developer ------- finalized the code refactoring before the deadline.",
      questions: [
        {
          options: ["successful", "success", "successfully", "succeed"],
          correctIndex: 2,
          explanation: "동사 finalized를 수식하는 부사 successfully가 적절합니다.",
        },
      ],
    },
  },
]);

const index = ref(0);
const selectedIndex = ref(null);

// result: null | { isCorrect: boolean, correctIndex: number }
const result = ref(null);
const showExplanation = ref(false);

const total = computed(() => questions.value.length);
const current = computed(() => questions.value[index.value]);

const level = computed(() => current.value?.level ?? "—");
const tags = computed(() => current.value?.tags ?? []);

const progress = computed(() => Math.round(((index.value + 1) / total.value) * 100));

// Timer (UI용 더미)
const timerText = ref("01:24");
let t = 84;
let timer = null;
onMounted(() => {
  timer = setInterval(() => {
    t = Math.max(0, t - 1);
    const mm = String(Math.floor(t / 60)).padStart(2, "0");
    const ss = String(t % 60).padStart(2, "0");
    timerText.value = `${mm}:${ss}`;
  }, 1000);
});

function onSelect(i) {
  if (result.value) return; // 채점 후에는 선택 잠금
  selectedIndex.value = i;
}

function onGrade() {
  if (selectedIndex.value === null) return;

  const correctIndex = current.value.content.questions[0].correctIndex;
  const isCorrect = selectedIndex.value === correctIndex;

  result.value = { isCorrect, correctIndex };

  // 오답이면 해설 바로 열어도 됨 (원하면 정책 바꿔)
  if (!isCorrect) showExplanation.value = true;
}

function onNext() {
  if (index.value >= total.value - 1) {
    // 마지막이면 홈으로 보내거나 결과 페이지 만들면 됨
    go("/practice");
    return;
  }
  index.value += 1;
  selectedIndex.value = null;
  result.value = null;
  showExplanation.value = false;

  // 타이머 리셋(더미)
  t = 84;
}


function onSaveWrong() {
  if (!result.value) return;

  const q = current.value;
  const cq = q.content.questions[0];

  onSaveWrong({
    questionId: q.id,
    type: q.type,
    part: q.part,
    level: q.level,
    tags: q.tags,
    passage: q.content.passage,
    options: cq.options,
    selectedIndex: selectedIndex.value,
    correctIndex: result.value.correctIndex,
    explanation: cq.explanation,
  });
}

</script>
<style scoped>
@import "@/assets/workly-english.css";
</style>