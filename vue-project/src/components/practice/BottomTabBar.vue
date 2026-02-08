<template>
  <div class="fixed bottom-0 left-0 right-0 z-50">
    <div class="container pb-3">
      <div class="bg-white border rounded-4 shadow-sm px-2 py-2 d-flex justify-content-between align-items-center">
        <button
            v-for="t in tabs"
            :key="t.id"
            class="btn flex-fill mx-1 d-flex flex-column align-items-center gap-1 py-2 rounded-4"
            :class="activeTab === t.id ? 'btn-primary' : 'btn-light'"
            @click="$emit('change', t.id)"
        >
          <i :class="t.icon"></i>
          <span class="small fw-bold">{{ t.name }}</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  activeTab: String,
})
defineEmits(['change'])

const tabs = [
  { id: 'RC', name: 'RC', icon: 'fa-solid fa-file-pen' },
  { id: 'VOCAB', name: '단어', icon: 'fa-solid fa-spell-check' },
  { id: 'SPEAKING', name: '회화', icon: 'fa-solid fa-microphone-lines' },
  { id: 'REVIEW', name: '오답', icon: 'fa-solid fa-book-bookmark' },
]
</script>

<style scoped>
.btn-light {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  color: #334155;
}
.btn-light:hover { background: #eef2ff; }
.btn-primary {
  background: #2563eb;
  border: 1px solid #2563eb;
}
</style>
