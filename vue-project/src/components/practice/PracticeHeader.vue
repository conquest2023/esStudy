<template>
  <div class="d-flex align-items-center justify-content-between mb-3">
    <div>
      <div class="d-flex gap-2 align-items-center">
        <span class="badge" :class="badgeClass">{{ typeLabel }}</span>
        <span v-if="type==='RC'" class="badge text-bg-light border">Part {{ part }}</span>
        <span class="badge text-bg-light border">{{ level }}</span>
      </div>
      <div class="mt-2">
        <div class="small text-muted">진행률 {{ current }} / {{ total }}</div>
        <div class="progress" style="height: 8px;">
          <div class="progress-bar" :style="{ width: pct + '%' }"></div>
        </div>
      </div>
    </div>

    <div class="d-flex gap-2 align-items-center">
      <div class="form-check form-switch">
        <input class="form-check-input" type="checkbox" :checked="showExplanationMode" @change="$emit('toggleExplanationMode')" />
        <label class="form-check-label small text-muted">즉시 해설</label>
      </div>
      <RouterLink class="btn btn-outline-secondary btn-sm" to="/wrong-notes">오답노트</RouterLink>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  type: String,
  part: [Number, String],
  level: String,
  current: Number,
  total: Number,
  showExplanationMode: Boolean,
})

const pct = computed(() => props.total ? Math.round((props.current / props.total) * 100) : 0)
const typeLabel = computed(() => props.type === 'RC' ? 'RC' : props.type === 'VOCAB' ? '단어' : '회화')
const badgeClass = computed(() => props.type === 'RC' ? 'text-bg-primary' : props.type === 'VOCAB' ? 'text-bg-success' : 'text-bg-warning')
</script>
