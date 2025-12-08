<script setup>
import {ref, computed, onMounted, onBeforeUnmount, nextTick} from 'vue'
import {RouterLink} from 'vue-router'

const props = defineProps({
  items: {type: Array, default: () => []},
  title: {type: String, default: '추천 글'}
})

const railRef = ref(null)
const activeIdx = ref(0)
const cardWidth = ref(0)
const gapPx = ref(0)

const pages = computed(() => props.items.length)

function stepSize() {
  // 카드 실제 너비 + 실제 gap 픽셀
  return (cardWidth.value || 0) + (gapPx.value || 0)
}

function goTo(i) {
  if (!railRef.value) return
  railRef.value.scrollTo({left: i * stepSize(), behavior: 'smooth'})
}

function prev() {
  activeIdx.value = Math.max(0, activeIdx.value - 1);
  goTo(activeIdx.value)
}

function next() {
  activeIdx.value = Math.min(pages.value - 1, activeIdx.value + 1);
  goTo(activeIdx.value)
}

function onScroll() {
  if (!railRef.value || !stepSize()) return
  const i = Math.round(railRef.value.scrollLeft / stepSize())
  activeIdx.value = Math.min(Math.max(i, 0), pages.value - 1)
}

function pidOf(p) {
  return p.feedUID ?? p.id ?? p.postId
}

function toUrl(p) {
  const pid = pidOf(p)
  return `/post/${pid}`   // 필요시 기존 경로로 변경
}

/** 실제 치수 측정 */
function measure() {
  if (!railRef.value) return
  const first = railRef.value.querySelector('.snap-card')
  // 카드 실측 너비
  cardWidth.value = first ? Math.round(first.offsetWidth) : railRef.value.clientWidth
  // gap 변수 읽기
  const cs = getComputedStyle(railRef.value)
  const gapStr = cs.getPropertyValue('--gap') || cs.gap || '16px'
  const parsed = parseFloat(gapStr)
  gapPx.value = Number.isFinite(parsed) ? parsed : 16
}

onMounted(async () => {
  await nextTick()
  measure()
  railRef.value?.addEventListener('scroll', onScroll, {passive: true})
  window.addEventListener('resize', measure)
})
onBeforeUnmount(() => {
  railRef.value?.removeEventListener('scroll', onScroll)
  window.removeEventListener('resize', measure)
})
</script>

<template>
  <section>
    <h2 class="section-title">{{ title }}</h2>

    <!-- ▣ 모바일: 슬라이드 -->
    <div class="d-md-none position-relative">
      <div ref="railRef" class="snap-rail">
        <div v-for="p in items" :key="pidOf(p)" class="snap-card">
          <div class="card card-custom p-3 h-100 shadow-sm border-0">
            <img
                v-if="p.imageURL"
                :src="p.imageURL"
                class="card-img-top rounded"
                alt="이미지"
                style="height: 150px; object-fit: cover;"
            />
            <div class="card-body d-flex flex-column">
              <div class="d-flex justify-content-between align-items-center mb-2">
                <span class="badge badge-pick">GOOD</span>
                <small class="text-muted">추천글</small>
              </div>

              <h5 class="card-title fw-bold title">
                <RouterLink :to="toUrl(p)" class="text-decoration-none text-dark">
                  {{ p.title }}
                </RouterLink>
              </h5>
            </div>
          </div>
        </div>
      </div>

      <!-- 내비 버튼 -->
      <button v-if="pages > 1" class="nav-btn prev" type="button" @click="prev" aria-label="이전">‹</button>
      <button v-if="pages > 1" class="nav-btn next" type="button" @click="next" aria-label="다음">›</button>

      <!-- 점 인디케이터 -->
      <div v-if="pages > 1" class="dots mt-2">
        <span
            v-for="(_, i) in pages"
            :key="i"
            class="dot"
            :class="{ active: i === activeIdx }"
            @click="goTo(i)"
        />
      </div>
    </div>

    <!-- ▣ 데스크톱: 3열 그리드 -->
    <div class="row row-cols-1 row-cols-md-3 g-3 d-none d-md-flex">
      <div v-for="p in items" :key="pidOf(p)" class="col">
        <div class="card card-custom p-3 h-100 shadow-sm border-0">
          <img
              v-if="p.imageURL"
              :src="p.imageURL"
              class="card-img-top rounded"
              alt="이미지"
              style="height: 150px; object-fit: cover;"
          />
          <div class="card-body d-flex flex-column">
            <div class="d-flex justify-content-between align-items-center mb-2">
              <span class="badge badge-pick">GOOD</span>
              <small class="text-muted">추천글</small>
            </div>

            <h5 class="card-title fw-bold title">
              <RouterLink :to="toUrl(p)" class="text-decoration-none text-dark">
                {{ p.title }}
              </RouterLink>
            </h5>
          </div>
        </div>
      </div>
    </div>

    <div v-if="!items.length" class="text-muted small mt-2">추천글이 없어요.</div>
  </section>
</template>

<style scoped>
.section-title {
  font-weight: 800;
  font-size: 1.75rem;
  letter-spacing: -0.02em;
  margin-bottom: 1rem;
}

.badge-pick {
  background: #e7f1ff;
  color: #2b78ff;
  border-radius: 9999px;
  padding: 0.25rem 0.6rem;
  font-weight: 700;
}

/* 타이틀 톤(스크린샷 유사) */
.title {
  font-size: clamp(1.05rem, 3.5vw, 1.4rem);
  line-height: 1.15;
  letter-spacing: -0.02em;
  margin: 0;
}

/* ── 모바일 슬라이드 ───────────────────────────── */
.snap-rail {
  --gap: 18px; /* ← JS에서 읽어 스텝 계산 */
  display: flex;
  gap: var(--gap);
  overflow-x: auto;
  scroll-snap-type: x mandatory;
  -webkit-overflow-scrolling: touch;
  padding: 2px 2px 6px;
}

.snap-rail::-webkit-scrollbar {
  display: none;
}

/* 화면 폭 대비 카드 비율 (살짝 옆 카드 보이게) */
.snap-card {
  flex: 0 0 86%;
  scroll-snap-align: start;
}

/* 내비 버튼 */
.nav-btn {
  position: absolute;
  top: 40%;
  transform: translateY(-50%);
  border: 0;
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: #ffffff;
  box-shadow: 0 3px 10px rgba(0, 0, 0, .12);
  font-size: 20px;
  line-height: 34px;
  text-align: center;
  padding: 0;
}

.nav-btn.prev {
  left: -6px;
}

.nav-btn.next {
  right: -6px;
}

/* 점 인디케이터 */
.dots {
  display: flex;
  justify-content: center;
  gap: 6px;
}

.dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #d1d5db;
  cursor: pointer;
}

.dot.active {
  background: #0d6efd;
}

/* 카드 공통 */
.card-custom {
  border-radius: 14px;
}
</style>
