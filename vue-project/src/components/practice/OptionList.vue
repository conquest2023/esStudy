<template>
  <div class="list-group">
    <button
        v-for="(opt, idx) in options"
        :key="idx"
        type="button"
        class="list-group-item list-group-item-action d-flex justify-content-between align-items-center"
        :class="itemClass(idx)"
        @click="$emit('select', idx)"
        :disabled="submitted"
    >
      <div>
        <span class="me-2 text-muted">{{ idx + 1 }}.</span>
        <span class="fw-semibold">{{ opt }}</span>
      </div>

      <div v-if="submitted">
        <span v-if="idx === answerIndex" class="badge text-bg-success">정답</span>
        <span v-else-if="idx === selectedIndex" class="badge text-bg-danger">내답</span>
      </div>
    </button>
  </div>
</template>

<script setup>
const props = defineProps({
  options: Array,
  selectedIndex: Number,
  submitted: Boolean,
  answerIndex: Number,
})

function itemClass(idx) {
  if (!props.submitted) return props.selectedIndex === idx ? 'active' : ''
  if (idx === props.answerIndex) return 'list-group-item-success'
  if (idx === props.selectedIndex && idx !== props.answerIndex) return 'list-group-item-danger'
  return ''
}
</script>
