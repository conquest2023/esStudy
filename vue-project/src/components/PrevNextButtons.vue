<template>
  <div class="prev-next-fixed" v-if="prevUID || nextUID">
    <button class="nav-btn" :disabled="!prevUID" @click="goTo(prevUID)" title="이전글">
      <i class="bi bi-chevron-up"></i>
    </button>
    <button class="nav-btn" :disabled="!nextUID" @click="goTo(nextUID)" title="다음글">
      <i class="bi bi-chevron-down"></i>
    </button>
  </div>
</template>

<script setup>
import {computed} from 'vue'
import {useRoute, useRouter} from 'vue-router'

const props = defineProps({posts: Array})
const route = useRoute()
const router = useRouter()

const currentFeedUID = computed(() => route.params.id || route.query.id)
const feedUIDs = computed(() => props.posts.map(p => p.feedUID))

const currentIndex = computed(() =>
    feedUIDs.value.findIndex(uid => String(uid) === String(currentFeedUID.value))
)

const prevUID = computed(() =>
    currentIndex.value > 0 ? feedUIDs.value[currentIndex.value - 1] : null
)

const nextUID = computed(() =>
    currentIndex.value < feedUIDs.value.length - 1
        ? feedUIDs.value[currentIndex.value + 1]
        : null
)


function goTo(uid) {
  if (uid) {
    router.push({path: `/search/view/feed/id/${uid}`})
  }
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
