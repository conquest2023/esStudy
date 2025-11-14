<template>
  <div class="tabs-container">
    <div class="tabs-inner">
      <button
          v-for="tab in tabs"
          :key="tab.id"
          ref="tabRefs"
          class="tab-btn"
          :class="{ active: modelValue === tab.id }"
          @click="selectTab(tab.id)"
      >
        {{ tab.label }}
      </button>

      <!-- 하이라이트 바 -->
      <div class="highlight-bar" :style="highlightStyle"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, nextTick } from 'vue'

const props = defineProps({
  modelValue: String,
  tabs: Array
})
const emit = defineEmits(['update:modelValue'])

const tabRefs = ref([])         // 각 탭 버튼 DOM
const highlightStyle = ref({})  // underline 위치/너비

function selectTab(id) {
  emit('update:modelValue', id)
}

function updateHighlight() {
  nextTick(() => {
    const idx = props.tabs.findIndex(t => t.id === props.modelValue)
    const el = tabRefs.value[idx]
    if (!el) return

    const rect = el.getBoundingClientRect()
    const parentRect = el.parentNode.getBoundingClientRect()

    highlightStyle.value = {
      width: rect.width + 'px',
      transform: `translateX(${rect.left - parentRect.left}px)`
    }
  })
}

onMounted(() => updateHighlight())
watch(() => props.modelValue, () => updateHighlight())
</script>

<style scoped>
/* 전체 컨테이너 */
.tabs-container {
  width: 100%;
  overflow-x: auto;
  padding: 0.4rem 0;
}

/* 내부 래퍼 */
.tabs-inner {
  position: relative;
  display: flex;
  gap: 4px;
  padding-bottom: 4px;
  border-bottom: 1px solid #e5e7eb;
}

/* 탭 버튼 */
.tab-btn {
  appearance: none;
  background: none;
  border: none;
  padding: 10px 14px;
  font-size: 0.95rem;
  font-weight: 500;
  color: #555;
  cursor: pointer;
  transition: color .2s ease, background .2s ease;
  border-radius: 6px;
  white-space: nowrap;
}

.tab-btn:hover {
  background: #f4f5f6;
  color: #111;
}

.tab-btn.active {
  color: #007bff;
  font-weight: 600;
}

/* 하이라이트 바 (슬라이딩 underline) */
.highlight-bar {
  position: absolute;
  bottom: 0;
  height: 3px;
  background-color: #007bff;
  border-radius: 3px;
  transition: transform .25s ease, width .25s ease;
  will-change: transform, width;
}
</style>
