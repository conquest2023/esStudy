<template>
  <div class="card shadow-sm">
    <div class="card-body">
      <component
          :is="renderer"
          :question="question"
          :selectedIndex="selectedIndex"
          :submitted="submitted"
          :answerIndex="answerIndex"
          @select="$emit('select', $event)"
      />

      <ExplanationBox
          v-if="submitted"
          :question="question"
          :isCorrect="isCorrect"
          :selectedIndex="selectedIndex"
          :answerIndex="answerIndex"
      />

      <BottomActionBar
          :canSubmit="selectedIndex !== null && !submitted"
          :submitted="submitted"
          :wrongNoteSaved="wrongNoteSaved"
          @submit="$emit('submit')"
          @next="$emit('next')"
          @prev="$emit('prev')"
          @toggleWrongNote="$emit('toggleWrongNote')"
      />
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import RcQuestionRenderer from './renderers/RcQuestionRenderer.vue'
import VocabQuestionRenderer from './renderers/VocabQuestionRenderer.vue'
import ExplanationBox from './ExplanationBox.vue'
import BottomActionBar from './BottomActionBar.vue'
import { usePracticeStore } from '@/stores/usePracticeStore'

const props = defineProps({
  question: Object,
  type: String,
  selectedIndex: Number,
  submitted: Boolean,
  showExplanationMode: Boolean,
  wrongNoteSaved: Boolean,
})
const practiceStore = usePracticeStore()

const renderer = computed(() => props.type === 'RC' ? RcQuestionRenderer : VocabQuestionRenderer)
const answerIndex = computed(() => practiceStore.getAnswerIndex(props.question))
const isCorrect = computed(() => props.selectedIndex === answerIndex.value)
</script>
