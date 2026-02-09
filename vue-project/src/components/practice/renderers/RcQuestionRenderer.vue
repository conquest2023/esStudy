<template>
  <div class="we-rcGrid">
    <section class="we-card we-pane we-pane--passage">
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

        <div class="we-passageHint">
          문맥에 맞는 단어/형태를 선택하세요.
        </div>
      </div>
    </section>

    <!-- Question Section -->
    <section class="we-card we-pane we-pane--question">
      <div class="we-paneHead">
        <div class="we-paneTitle">
          <i class="fa-solid fa-circle-question" style="color:#2563eb;"></i>
          QUESTION
        </div>
        <div class="we-chip" style="box-shadow:none;">
          <i class="fa-solid fa-layer-group"></i> Part {{ question.part }}
        </div>
      </div>

      <div class="we-qContent">
        <h2 class="we-qTitle">빈칸에 들어갈 가장 적절한 답을 고르시오.</h2>
        <div class="we-qSub">선택 후 채점하면 결과와 해설을 확인할 수 있습니다.</div>

        <div class="we-options">
          <button
              v-for="(opt, i) in options"
              :key="opt + i"
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
              class="we-actionBtn we-actionBtn--primary"
              :disabled="selectedIndex === null || !!result"
              @click="$emit('grade')"
          >
            <i class="fa-solid fa-check"></i>
            채점
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
              @click="saveWrong"
          >
            <i class="fa-solid fa-bookmark"></i>
            저장
          </button>

          <button
              class="we-actionBtn"
              :disabled="!result"
              @click="$emit('next')"
          >
            <i class="fa-solid fa-arrow-right"></i>
            다음
          </button>
        </div>

        <!-- Explanation Area -->
        <div v-if="showExplanation && result" class="we-explain">
          <div class="we-explainTitle">
            <div class="we-explainDot"></div>
            해설
          </div>
          <div class="we-explainBody">
            {{ explanation }}
          </div>
          <div class="we-correctMark">
            정답: <b style="color:#0f172a;">{{ options[result.correctIndex] }}</b>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import axios from 'axios';
import { computed } from 'vue';
import {useRouter} from "vue-router";
import api from "@/utils/api.js";

const props = defineProps({
  question: { type: Object, required: true },
  selectedIndex: { type: Number, default: null },
  result: { type: Object, default: null },
  showExplanation: { type: Boolean, default: false },
  timerText: { type: String, default: "00:00" },
});
defineEmits(["select", "grade", "toggle-explain", "next"]);
const router = useRouter()
const options = computed(() => {
  return props.question?.content?.questions?.[0]?.options || [];
});
const explanation = computed(() => props.question.content.questions[0].explanation);

function alpha(i) {
  return String.fromCharCode(65 + i);
}

function highlightBlank(text) {
  if (!text) return "";
  return text.replace(/-+/g, `<span class="we-blank">_______</span>`);
}

function optionClass(i) {
  const base = [];
  if (props.selectedIndex === i)
    base.push("is-selected");

  if (props.result) {
    if (i === props.result.correctIndex) base.push("is-correct");
    if (props.selectedIndex === i && i !== props.result.correctIndex) base.push("is-wrong");
  }
  return base.join(" ");
}
async function saveWrong() {
  if (!props.result || props.result.isCorrect) return
  if (!props.question) return

  if (!confirm('오답노트에 저장하시겠습니까?'))
    return

  const token = localStorage.getItem('token')
  if (!token) {
    alert('로그인이 필요합니다.')
    router.push('/login')
    return
  }

  const payload = {
    objectId: props.question.id,
    category: props.question.type,
    part: props.question.part,
    level: props.question.level
  }
  try {
    await api.post('/english/log', payload, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })

    console.log('오답노트 저장 완료')
    alert('오답노트에 저장되었습니다.')
  } catch (e) {
    console.error('오답노트 저장 실패', e)
    alert('오답노트 저장에 실패했습니다.')
  }
}
</script>

<style scoped>
.we-rcGrid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.we-pane--passage {
  display: flex;
  flex-direction: column;
}

.we-passage {
  flex: 1;
  overflow-y: auto;
}

.we-passageHint {
  color: #64748b;
  font-weight: 800;
  font-size: 13px;
  margin-top: 12px;
}

.we-explainDot {
  width: 10px;
  height: 10px;
  border-radius: 999px;
  background: #2563eb;
}

.we-correctMark {
  margin-top: 10px;
  color: #94a3b8;
  font-weight: 800;
  font-size: 12px;
}

@media (max-width: 768px) {
  .we-rcGrid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .we-pane--passage {
    max-height: 250px;
  }

  .we-passage {
    font-size: 15px;
  }

  .we-qTitle {
    font-size: 18px;
    margin-bottom: 8px;
  }

  .we-options {
    display: flex;
    flex-direction: column;
    gap: 8px;
  }

  .we-actions {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 10px;
    margin-top: 20px;
  }

  .we-actionBtn {
    width: 100%;
    padding: 12px 8px;
    font-size: 14px;
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .we-paneHead {
    padding: 12px;
  }

  .we-passage {
    padding: 12px;
  }
}
</style>