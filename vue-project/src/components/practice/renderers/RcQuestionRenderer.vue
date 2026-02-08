<template>
  <div class="we-rcGrid">
    <!-- Passage -->
    <section class="we-card we-pane">
      <div class="we-paneHead">
        <div class="we-paneTitle">
          <i class="fa-solid fa-align-left" style="color:#2563eb;"></i>
          RC PASSAGE
        </div>
        <div class="we-timer">
          <i class="fa-solid fa-clock"></i>
          {{ timerText }}
        </div>
      </div>

      <div class="we-passage">
        <div class="we-quote">
          <span v-html="highlightBlank(question.content.passage)"></span>
        </div>

        <div style="color:#64748b; font-weight:800; font-size:13px; margin-top:12px;">
          문맥에 맞는 단어/형태를 선택하세요.
        </div>
      </div>
    </section>

    <!-- Question -->
    <section class="we-card we-pane">
      <div class="we-paneHead">
        <div class="we-paneTitle">
          <i class="fa-solid fa-circle-question" style="color:#2563eb;"></i>
          QUESTION
        </div>
        <div class="we-chip" style="box-shadow:none;">
          <i class="fa-solid fa-layer-group"></i> Part {{ question.part }}
        </div>
      </div>

      <h2 class="we-qTitle">빈칸에 들어갈 가장 적절한 답을 고르시오.</h2>
      <div class="we-qSub">선택 후 채점하면 결과와 해설을 확인할 수 있어열~??</div>

      <div class="we-options">
        <button
            v-for="(opt, i) in options"
            :key="opt"
            class="we-opt"
            :class="optionClass(i)"
            @click="$emit('select', i)"
        >
          <div class="we-optBadge">{{ alpha(i) }}</div>
          <div class="we-optText">{{ opt }}</div>
        </button>
      </div>

      <!-- Actions under question (no fixed action bar) -->
      <div class="we-actions">
        <button
            class="we-actionBtn we-actionBtn--primary"
            :disabled="selectedIndex === null || !!result"
            @click="$emit('grade')"
        >
          <i class="fa-solid fa-check"></i>
          채점하기
        </button>

        <button
            class="we-actionBtn"
            :disabled="!result"
            @click="$emit('toggle-explain')"
        >
          <i class="fa-solid fa-circle-info"></i>
          해설 {{ showExplanation ? "닫기" : "보기" }}
        </button>

        <button
            class="we-actionBtn we-actionBtn--ghost"
            :disabled="!result || result?.isCorrect"
            @click="$emit('save-wrong')"
        >
          <i class="fa-solid fa-bookmark"></i>
          오답노트 저장
        </button>

        <button
            class="we-actionBtn"
            :disabled="!result"
            @click="$emit('next')"
        >
          <i class="fa-solid fa-arrow-right"></i>
          다음 문제
        </button>
      </div>

      <!-- Explanation -->
      <div v-if="showExplanation && result" class="we-explain">
        <div class="we-explainTitle">
          <div style="width:10px;height:10px;border-radius:999px;background:#2563eb;"></div>
          해설
        </div>
        <div class="we-explainBody">
          {{ explanation }}
        </div>
        <div style="margin-top:10px; color:#94a3b8; font-weight:800; font-size:12px;">
          정답: <b style="color:#0f172a;">{{ options[result.correctIndex] }}</b>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
const props = defineProps({
  question: {type: Object, required: true},
  selectedIndex: {type: Number, default: null},
  result: {type: Object, default: null}, // {isCorrect, correctIndex}
  showExplanation: {type: Boolean, default: false},
  timerText: {type: String, default: "00:00"},
});

defineEmits(["select", "grade", "toggle-explain", "next", "save-wrong"]);

const options = props.question.content.questions[0].options;
const explanation = props.question.content.questions[0].explanation;

function alpha(i) {
  return String.fromCharCode(65 + i);
}

function highlightBlank(text) {
  // ------- 를 플랫폼 스타일 blank pill로 보여주기
  return text.replace(/-+/g, `<span class="we-blank">_______</span>`);
}

function optionClass(i) {
  const base = [];
  if (props.selectedIndex === i) base.push("is-selected");

  if (props.result) {
    if (i === props.result.correctIndex) base.push("is-correct");
    if (props.selectedIndex === i && i !== props.result.correctIndex) base.push("is-wrong");
  }
  return base.join(" ");
}
</script>
