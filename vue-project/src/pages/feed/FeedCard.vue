<template>
  <div
      class="card feed-card" v-lift
      :class="notice?'notice':''" @click="goToDetail">
    <div class="card-body d-flex justify-content-between align-items-center">
      <div class="me-2 flex-grow-1">
        <h6 class="fw-bold mb-1 text-truncate">
          <span v-if="notice">📢&nbsp;</span>
          <span v-else-if="isVote">🗳️&nbsp;</span>
          {{ post.title }}
          <span v-if="!notice && !isVote && commentCount" class="text-danger fw-bold ms-1">[{{ commentCount }}]</span>
        </h6>
        <small class="text-muted">{{ post.username }} · {{ time }}</small>
      </div>
      <div v-if="!notice && !isVote"
           class="d-flex align-items-center meta-stats small gap-3 flex-shrink-0">
      <span><i class="bi bi-eye me-1"></i>{{ post.viewCount }}</span>
        <span><i class="bi bi-heart-fill text-danger me-1"></i>{{ post.likeCount }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'

const props = defineProps({
  post:Object,
  notice:{type:Boolean,default:false},
  isVote:{type:Boolean,default:false},
  commentCount:{type:Number,default:0}
})
const router=useRouter()

const time=computed(()=>{
  const d=new Date(props.post.createdAt)
  return `${d.getMonth()+1}. ${d.getDate()}. ${d.getHours()>=12?'오후':'오전'} ${(d.getHours()%12)||12}:${d.getMinutes().toString().padStart(2,'0')}`
})
const isVote=computed(()=>!props.notice&&!props.post.id)
function goToDetail(){
  router.push(isVote.value?`/search/view/vote/detail?id=${props.post.feedUID}`:`/search/view/feed/id/${props.post.feedUID}`)
}
</script>

<style scoped>
.feed-card {
  border:none;
  box-shadow: var(--elev-1, 0 1px 3px rgba(0,0,0,.08));
  border-radius:var(--radius-lg);
  cursor:pointer;
  font-weight: 500;
  background: var(--c-surface, #fff);
  color: var(--c-text);
}
.feed-card .text-muted {
  color: var(--c-text-muted);
}
.feed-card .meta-stats {
  color: var(--c-meta-stats);
}
.feed-card.notice{
  border:2px solid #f0ad4e;
}

</style>