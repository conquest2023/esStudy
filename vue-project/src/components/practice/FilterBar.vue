<template>
  <div class="card border-0 shadow-sm mb-3">
    <div class="card-body py-3">
      <div class="row g-2 align-items-end">
        <div class="col-md-3">
          <label class="form-label small text-muted">레벨</label>
          <select class="form-select" :value="level" @change="$emit('update:level', $event.target.value)">
            <option value="ALL">전체</option>
            <option value="BRONZE">BRONZE</option>
            <option value="SILVER">SILVER</option>
            <option value="GOLD">GOLD</option>
          </select>
        </div>

        <div class="col-md-3">
          <label class="form-label small text-muted">태그</label>
          <select class="form-select" :value="tag" @change="$emit('update:tag', $event.target.value)">
            <option value="ALL">전체</option>
            <option v-for="t in tags" :key="t" :value="t">{{ t }}</option>
          </select>
        </div>

        <div class="col-md-2" v-if="type==='RC'">
          <label class="form-label small text-muted">파트</label>
          <select class="form-select" :value="part" @change="$emit('update:part', Number($event.target.value))">
            <option :value="5">Part 5</option>
            <option :value="6">Part 6</option>
            <option :value="7">Part 7</option>
          </select>
        </div>

        <div class="col-md-2">
          <label class="form-label small text-muted">출제</label>
          <select class="form-select" :value="order" @change="$emit('update:order', $event.target.value)">
            <option value="RANDOM">랜덤</option>
            <option value="SEQ">순차</option>
          </select>
        </div>

        <div class="col-md-2">
          <button class="btn btn-dark w-100" @click="$emit('apply')">적용</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  type: String,
  level: String,
  tag: String,
  part: [Number, String],
  order: String,
})

const tags = [
  'grammar', 'adverb', 'it-performance', 'refactoring', 'security', 'api',
  'gerund', 'network', 'database', 'downtime', 'conjunction', 'training', 'backup'
]
</script>
