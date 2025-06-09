<template>
  <div class="prev-next-fixed" v-if="prevPost || nextPost">
    <button
        class="nav-btn"
        :disabled="!prevPost"
        @click="prevPost && goTo(prevPost.feedUID)"
        title="이전글"
    >
      <i class="bi bi-chevron-up"></i>
    </button>

    <button
        class="nav-btn"
        :disabled="!nextPost"
        @click="nextPost && goTo(nextPost.feedUID)"
        title="다음글"
    >
      <i class="bi bi-chevron-down"></i>
    </button>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const props = defineProps({
  posts: {
    type: Array,
    required: true
  }
})
const route = useRoute()
const router = useRouter()

const currentFeedUID = computed(() => route.params.id || route.query.id)
const currentIndex = computed(() =>
    props.posts.findIndex(p => String(p.feedUID) === String(currentFeedUID.value))
)

const prevPost = computed(() =>
    currentIndex.value > 0 ? props.posts[currentIndex.value - 1] : null
)

const nextPost = computed(() =>
    currentIndex.value < props.posts.length - 1
        ? props.posts[currentIndex.value + 1]
        : null
)
console.log('feedUIDs in posts:', props.posts.map(p => p.feedUID))

function goTo(feedUID) {
  router.push({
    path: `/search/view/feed/id/${feedUID}`,
    state: {posts: props.posts}
  })
}
</script>

<style scoped>
.prev-next-fixed {
  position: fixed;
  top: 45%;
  right: 16px;
  transform: translateY(-50%);
  display: flex;
  flex-direction: column;
  gap: 10px;
  z-index: 1000;
}

.nav-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: none;
  background-color: #f1f1f1;
  color: #333;
  font-size: 1.2rem;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.nav-btn:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.nav-btn:hover:not(:disabled) {
  background-color: #e0e0e0;
}
</style>
