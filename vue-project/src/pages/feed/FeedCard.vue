<template>
  <div
      class="card feed-card shadow-sm"
      :class="notice ? 'border-warning border-3' : ''"
      @click="$emit('click')">
    <div class="card-body d-flex justify-content-between align-items-center">
      <div class="me-2 flex-grow-1">
        <h6 class="fw-bold mb-1 text-truncate" :class="{notice}">
          <span v-if="notice">📢&nbsp;</span>{{ post.title }}
          <span v-if="commentCount" class="text-danger fw-bold ms-1">
            [{{ commentCount }}]
          </span>
        </h6>
        <small class="text-muted">
          {{ post.username }} · {{ time }}
        </small>
      </div>


      <div
          v-if="!notice"
          class="d-flex align-items-center text-muted small gap-3 flex-shrink-0"
      >
        <span><i class="bi bi-eye me-1"></i>{{ post.viewCount }}</span>
        <span><i class="bi bi-heart-fill text-danger me-1"></i>{{ post.likeCount }}</span>
      </div>
    </div>
  </div>
</template>


  <script setup>
    import { computed } from 'vue'
    const props = defineProps({
      post:          Object,
      notice:        { type:Boolean, default:false },
      commentCount:  { type:Number,  default:0 }
    })
    const time = computed(() =>
        new Date(props.post.createdAt).toLocaleString('ko-KR', {hour12:false})
    )
  </script>

  <style scoped>
  .feed-card{ cursor:pointer; transition:background .15s }
  .feed-card:hover{ background:#f8f9fa }
  .notice{ color:#d48806 }             /* 제목 색상 강조 */
  </style>
