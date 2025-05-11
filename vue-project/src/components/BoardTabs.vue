<!-- src/components/BoardTabs.vue -->
<template>
  <div class="tabs-wrapper">
    <div class="modern-tabs">
      <button
          v-for="tab in tabs"
          :key="tab.id"
          class="tab-button"
          :class="{ active: modelValue === tab.id }"
          @click="$emit('update:modelValue', tab.id)"
      >
        {{ tab.label }}
      </button>
    </div>
  </div>
</template>

<script setup>
defineProps({
  modelValue: String,
  tabs: Array
})
</script>

<style scoped>
.tabs-wrapper {
  overflow-x: auto;
  padding: 0.5rem 0;
}

.modern-tabs {
  display: flex;
  gap: 8px;
  flex-wrap: nowrap;
  white-space: nowrap;
  overflow-x: auto;
  padding: 0 10px;
}

.tab-button {
  padding: 8px 16px;
  border-radius: 999px;
  font-weight: 500;
  background-color: #f1f3f5;
  border: 1px solid transparent;
  transition: all 0.2s ease-in-out;
  color: #333;
}

.tab-button:hover {
  background-color: #e2e6ea;
}

.tab-button.active {
  background-color: #007bff;
  color: white;
  font-weight: 600;
  box-shadow: 0 2px 6px rgba(0,0,0,0.15);
}
</style>
