<template>
  <div class="we-rcGrid">
    <!-- Left: Word Card -->
    <section class="we-card we-pane">
      <div class="we-paneHead">
        <div class="we-paneTitle">
          <i class="fa-solid fa-spell-check" style="color:#10b981;"></i>
          VOCA CARD
        </div>
        <div class="we-timer">
          <i class="fa-solid fa-clock"></i>
          {{ timerText }}
        </div>
      </div>

      <div style="display:flex; align-items:flex-start; justify-content:space-between; gap:12px;">
        <div>
          <div style="font-weight:900; letter-spacing:.22em; text-transform:uppercase; color:#94a3b8; font-size:12px;">
            Daily Vocabulary
          </div>

          <div style="margin-top:10px; font-weight:900; font-size:42px; letter-spacing:-1px; color:#0f172a;">
            {{ word }}
          </div>

          <div style="margin-top:8px; color:#64748b; font-weight:900; font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;">
            {{ pronunciation }}
          </div>
        </div>

        <div style="display:flex; gap:8px;">
          <button type="button" class="we-actionBtn" style="min-width:auto; padding:10px 12px;" @click="onSpeak" title="발음(더미)">
            <i class="fa-solid fa-volume-high"></i>
          </button>

          <button
              type="button"
              class="we-actionBtn"
              style="min-width:auto; padding:10px 12px;"
              :class="starred ? 'we-actionBtn--primary' : ''"
              @click="$emit('toggle-star')"
              title="즐겨찾기"
          >
            <i class="fa-solid fa-star"></i>
          </button>
        </div>
      </div>

      <div class="we-explain" style="margin-top:16px;">
        <div class="we-explainTitle">
          <div style="width:10px;height:10px;border-radius:999px;background:#10b981;"></div>
          Example
        </div>
        <div class="we-explainBody">
          {{ example }}
        </div>
      </div>

      <div style="margin-top:12px; color:#94a3b8; font-weight:800; font-size:12px; line-height:1.6;">
        <b style="color:#0f172a;">TIP</b> “개발 문맥에서 쓰는 뜻”을 먼저 고르는 게 실전에서 강해열~??
      </div>
    </section>

    <!-- Right: Question -->
    <section class="we-card we-pane">
      <div class="we-paneHead">
        <div class="we-paneTitle">
          <i class="fa-solid fa-circle-question" style="color:#10b981;"></i>
          QUESTION
        </div>

        <div class="we-chip" style="box-shadow:none;">
          <i class="fa-solid fa-hashtag"></i>
          {{ tagText }}
        </div>
      </div>

      <h2 class="we-qTitle">가장 알맞은 뜻을 고르시오.</h2>
      <div class="we-qSub">선택 → 채점 → 해설 확인 순서로 진행해요.</div>

      <div class="we-options">
        <button
            v-for="(opt, i) in options"
            :key="i"
            type="button"
            class="we-opt"
            :class="optionClass(i)"
            @click="$emit('select', i)"
        >
          <div class="we-optBadge">{{ alpha(i) }}</div>
          <div class="we-optText">{{ opt }}</div>
        </button>
      </div>

      <div class="we-actions">
        <button
            type="button"
            class="we-actionBtn we-actionBtn--primary"
            :disabled="selectedIndex === null || !!result"
            @click="$emit('grade')"
        >
          <i class="fa-solid fa-check"></i> 채점하기
        </button>

        <button type="button" class="we-actionBtn" :disabled="!result" @click="$emit('toggle-explain')">
          <i class="fa-solid fa-circle-info"></i>
          해설 {{ showExplanation ? "닫기" : "보기" }}
        </button>

        <button
            type="button"
            class="we-actionBtn we-actionBtn--ghost"
            :disabled="!result || result?.isCorrect"
            @click="$emit('save-wrong')"
        >
          <i class="fa-solid fa-bookmark"></i> 오답 저장
        </button>

        <button type="button" class="we-actionBtn" :disabled="!result" @click="$emit('next')">
          <i class="fa-solid fa-arrow-right"></i> 다음
        </button>
      </div>

      <div v-if="showExplanation && result" class="we-explain">
        <div class="we-explainTitle">
          <div style="width:10px;height:10px;border-radius:999px;background:#10b981;"></div>
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
import {computed} from "vue";

const props = defineProps({
  question: {type: Object, required: true},
  selectedIndex: {type: Number, default: null},
  result: {type: Object, default: null}, // {isCorrect, correctIndex}
  showExplanation: {type: Boolean, default: false},
  timerText: {type: String, default: "00:00"},
  starred: {type: Boolean, default: false},
});

defineEmits(["select", "grade", "toggle-explain", "next", "save-wrong", "toggle-star"]);

const q = computed(() => props.question ?? {});
const content = computed(() => q.value.content ?? {});
const firstQ = computed(() => content.value.questions?.[0] ?? {});

const word = computed(() => content.value.word ?? "—");
const pronunciation = computed(() => content.value.pronunciation ?? "/—/");
const example = computed(() => content.value.example ?? "예문이 아직 없어요.");
const options = computed(() => firstQ.value.options ?? []);
const explanation = computed(() => firstQ.value.explanation ?? "해설이 아직 없어요.");
const tagText = computed(() => (q.value.tags?.length ? q.value.tags.join(" · ") : "voca"));

function alpha(i) {
  return String.fromCharCode(65 + i);
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

function onSpeak() {
  console.log("speak(dummy):", word.value);
}
</script>
