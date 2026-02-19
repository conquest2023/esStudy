<template>
  <div class="we-page">
    <main class="we-container we-main">

      <section class="we-section we-mobile-dashboard">
        <div class="we-card we-panel we-dashboard-card" :class="{ 'is-expanded': isExpanded }">
          <div class="we-dashboard-header" @click="isExpanded = !isExpanded">
            <div class="we-dashboard-summary">
              <i class="fa-solid fa-fire we-fire"></i>
              <span class="we-summary-text">오늘의 학습 <b>{{ totalProgress }}%</b> 달성 중</span>
            </div>
            <button class="we-toggle-btn">
              <i class="fa-solid" :class="isExpanded ? 'fa-chevron-up' : 'fa-chevron-down'"></i>
            </button>
          </div>

          <transition name="slide">
            <div v-if="isExpanded" class="we-dashboard-detail">
              <div class="we-mission-mini-list">
                <div class="we-mission__item" v-for="m in missions" :key="m.id">
                  <i :class="[m.completed ? 'fa-solid fa-check-circle we-done' : 'fa-regular fa-circle']"></i>
                  <span :class="{ 'is-done': m.completed }">{{ m.text }}</span>
                </div>
              </div>
              <div class="we-progress-mini">
                <div class="we-progress__row" v-for="m in missions" :key="m.id">
                  <span class="we-mini-label">{{ m.id === 1 ? 'RC' : 'VOCA' }}</span>
                  <div class="we-bar"><div class="we-bar__fill" :style="{ width: m.progress + '%' }"></div></div>
                  <span class="we-mini-percent">{{ m.progress }}%</span>
                </div>
              </div>
            </div>
          </transition>
        </div>
      </section>

      <section class="we-section we-section--top">
        <div class="we-section__head">
          <p class="we-section__sub we-section__sub--hero">RC · 단어 · 회화 · 오답 복습</p>
          <button class="we-btn we-btn--small we-btn--outline" @click="go('/wrong-notes')">
            <i class="fa-solid fa-book-bookmark"></i> 오답노트 바로가기
          </button>
        </div>
        <div class="we-grid">
          <article class="we-card we-track" @click="go('/practice/rc')">
            <div class="we-track__top">
              <div class="we-track__icon we-track__icon--blue"><i class="fa-solid fa-file-pen"></i></div>
              <span class="we-badge we-badge--blue">RC</span>
            </div>
            <div class="we-track__name">RC 실전풀이</div>
            <div class="we-track__desc">문법·어휘 빈칸 채우기 (Part 5)</div>
            <div class="we-track__cta">START <i class="fa-solid fa-chevron-right"></i></div>
          </article>

          <article class="we-card we-track" @click="showVocaModal = true">
            <div class="we-track__top">
              <div class="we-track__icon we-track__icon--green"><i class="fa-solid fa-spell-check"></i></div>
              <span class="we-badge we-badge--green">VOCA</span>
            </div>
            <div class="we-track__name">영어 단어</div>
            <div class="we-track__desc">수준별 학습 · 랜덤 테스트</div>
            <div class="we-track__cta">START <i class="fa-solid fa-chevron-right"></i></div>
          </article>

          <article class="we-card we-track" @click="go('/practice/speaking')">
            <div class="we-track__top">
              <div class="we-track__icon we-track__icon--rose"><i class="fa-solid fa-microphone-lines"></i></div>
              <span class="we-badge we-badge--rose">SPEAKING</span>
            </div>
            <div class="we-track__name">영어 회화</div>
            <div class="we-track__desc">상황 기반 선택형 / 말하기</div>
            <div class="we-track__cta">START <i class="fa-solid fa-chevron-right"></i></div>
          </article>
          <article class="we-card we-track" @click="go('/wrong-notes')">
            <div class="we-track__top">
              <div class="we-track__icon we-track__icon--amber"><i class="fa-solid fa-book-bookmark"></i></div>
              <span class="we-badge we-badge--amber">REVIEW</span>
            </div>
            <div class="we-track__name">오답노트</div>
            <div class="we-track__desc">틀린 문제만 재풀이 · 메모로 고정</div>
            <div class="we-track__cta">REVIEW <i class="fa-solid fa-chevron-right"></i></div>
          </article>
        </div>
      </section>

      <section class="we-section we-section--secondary we-desktop-dashboard">
        <div class="we-split">
          <div class="we-card we-panel">
            <div class="we-panel__head">
              <h3 class="we-panel__title"><i class="fa-solid fa-fire we-fire"></i> 오늘의 미션</h3>
              <span class="we-pill">DAYS STREAK</span>
            </div>
            <div class="we-mission__list">
              <div class="we-mission__item" v-for="m in missions" :key="m.id">
                <div :class="['we-mission__icon', m.completed ? 'is-done' : '']">
                  <i :class="m.completed ? 'fa-solid fa-check' : 'fa-regular fa-circle'"></i>
                </div>
                <div class="we-mission__text">
                  <div :class="['we-mission__name', m.completed ? 'is-done' : '']"
                       :style="m.completed ? 'text-decoration: line-through; opacity: 0.6;' : ''">
                    {{ m.text }}
                  </div>
                  <div class="we-mission__xp">{{ m.point }} XP</div>
                </div>
              </div>
            </div>
          </div>

          <div class="we-card we-panel">
            <div class="we-panel__head">
              <h3 class="we-panel__title"><i class="fa-solid fa-chart-line we-blue"></i> 진행도</h3>
              <span class="we-pill we-pill--soft">Total</span>
            </div>
            <div class="we-progress">
              <div class="we-progress__row" v-for="m in missions" :key="m.id">
                <div class="we-muted">{{ m.id === 1 ? 'RC' : 'VOCA' }}</div>
                <div class="we-bar">
                  <div class="we-bar__fill" :style="{ width: m.progress + '%' }"></div>
                </div>
                <div class="we-muted">{{ m.progress }}%</div>
              </div>
              <div class="we-progress__big">
                <div class="we-progress__num">{{ totalProgress }}%</div>
                <div class="we-progress__hint">이번 주 목표 달성률</div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <button class="we-fab" @click="go('/wrong-notes')"><i class="fa-solid fa-book-bookmark"></i></button>

      <div v-if="showVocaModal" class="we-modal-overlay" @click.self="showVocaModal = false">
        <div class="we-modal">
          <div class="we-modal__header">
            <h3>난이도 선택</h3>
            <button class="we-close-btn" @click="showVocaModal = false"><i class="fa-solid fa-xmark"></i></button>
          </div>
          <div class="we-modal__body">
            <div class="we-level-grid">
              <button class="we-level-btn btn-bronze" @click="startVoca('BRONZE')">
                <span class="we-level-icon">🥉</span>
                <span class="we-level-text">BRONZE(쉬움)</span>
              </button>
              <button class="we-level-btn btn-silver" @click="startVoca('SILVER')">
                <span class="we-level-icon">🥈</span>
                <span class="we-level-text">SILVER(중간)</span>
              </button>
              <button class="we-level-btn btn-gold" @click="startVoca('GOLD')">
                <span class="we-level-icon">🥇</span>
                <span class="we-level-text">GOLD(어려움)</span>
              </button>
              <button class="we-level-btn btn-random" @click="startVoca('RANDOM')">
                <span class="we-level-icon">🎲</span>
                <span class="we-level-text">랜덤 출제</span>
              </button>
            </div>
          </div>
        </div>
      </div>

    </main>
  </div>
</template>

<script setup>
import { useRouter } from "vue-router";
import { ref, onMounted, computed } from "vue";

const router = useRouter();
const go = (path) => router.push(path);

// VOCA 모달 상태 관리
const showVocaModal = ref(false);

const startVoca = (level) => {
  showVocaModal.value = false; // 모달 닫기

  if (level === 'RANDOM') {
    router.push('/practice/vocab');
  } else {
    router.push({ path: '/practice/vocab', query: { level: level } });
  }
};

const isExpanded = ref(false);
const rcCount = ref(0);
const vocaCount = ref(0);
const rcGoal = 10;
const vocaGoal = 25;

const missions = computed(() => [
  { id: 1, text: `RC Part 5 ${rcGoal}문제 풀기`, point: 200, completed: rcCount.value >= rcGoal, progress: Math.min(Math.round((rcCount.value / rcGoal) * 100), 100) },
  { id: 2, text: `신규 단어 ${vocaGoal}개 학습`, point: 150, completed: vocaCount.value >= vocaGoal, progress: Math.min(Math.round((vocaCount.value / vocaGoal) * 100), 100) }
]);

const totalProgress = computed(() => {
  const rcP = Math.min(rcCount.value / rcGoal, 1);
  const vocaP = Math.min(vocaCount.value / vocaGoal, 1);
  return Math.round(((rcP + vocaP) / 2) * 100);
});

const fetchProgress = async () => {
  try {
    const token = localStorage.getItem('token');
    const headers = { 'Authorization': `Bearer ${token}` };
    const [rcRes, vocaRes] = await Promise.all([
      fetch('/api/count/RC', { headers }),
      fetch('/api/count/VOCA', { headers })
    ]);
    if (rcRes.ok) rcCount.value = (await rcRes.json()).ok || 0;
    if (vocaRes.ok) vocaCount.value = (await vocaRes.json()).ok || 0;
  } catch (err) { console.error(err); }
};

onMounted(fetchProgress);
</script>

<style scoped>
@import "@/assets/workly-english.css";

/* 데스크톱/모바일 기본 노출 제어 */
.we-mobile-dashboard { display: none; }
.we-desktop-dashboard { display: block; }

@media (max-width: 768px) {
  .we-mobile-dashboard { display: block; }
  .we-desktop-dashboard { display: none; }

  /* 모바일 토글 스타일 최적화 */
  .we-dashboard-card { margin: 10px 15px; padding: 12px 16px; border-radius: 16px; box-shadow: 0 4px 12px rgba(0,0,0,0.05); }
  .we-dashboard-header { display: flex; justify-content: space-between; align-items: center; }
  .we-summary-text { font-size: 0.9rem; font-weight: 600; color: #1e293b; }
  .we-dashboard-detail { margin-top: 15px; padding-top: 15px; border-top: 1px solid #f1f5f9; }
  .we-mission__item { font-size: 0.8rem; display: flex; align-items: center; gap: 8px; margin-bottom: 5px; }
  .we-done { color: #10b981; }
  .is-done { text-decoration: line-through; opacity: 0.5; }
  .we-mini-label { font-size: 0.7rem; width: 30px; font-weight: 700; color: #64748b; }
  .we-mini-percent { font-size: 0.7rem; width: 30px; text-align: right; }
  .we-bar { height: 6px; flex: 1; margin: 0 10px; background: #f1f5f9; border-radius: 3px; }
}

/* 애니메이션 */
.slide-enter-active, .slide-leave-active { transition: max-height 0.3s ease-out, opacity 0.2s; overflow: hidden; }
.slide-enter-from, .slide-leave-to { max-height: 0; opacity: 0; }
.slide-enter-to, .slide-leave-from { max-height: 250px; opacity: 1; }

.we-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  backdrop-filter: blur(2px);
}

.we-modal {
  background: #ffffff;
  width: 90%;
  max-width: 360px;
  border-radius: 20px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  animation: modalPop 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.we-modal__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f1f5f9;
}

.we-modal__header h3 {
  margin: 0;
  font-size: 1.1rem;
  font-weight: 700;
  color: #1e293b;
}

.we-close-btn {
  background: none;
  border: none;
  font-size: 1.2rem;
  color: #94a3b8;
  cursor: pointer;
  padding: 4px;
}

.we-modal__body {
  padding: 20px;
}

.we-level-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.we-level-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 16px 10px;
  border: 2px solid transparent;
  border-radius: 12px;
  background: #f8fafc;
  cursor: pointer;
  transition: all 0.2s;
  font-weight: 600;
}

.we-level-icon {
  font-size: 1.8rem;
  margin-bottom: 8px;
}

.we-level-text {
  font-size: 0.9rem;
  color: #334155;
}

.we-level-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
}

/* 각 난이도별 테마 컬러 */
.btn-bronze { border-color: #cd7f32; background: #fff8f2; }
.btn-bronze:hover { background: #cd7f32; color: white; }
.btn-bronze:hover .we-level-text { color: white; }

.btn-silver { border-color: #94a3b8; background: #f8fafc; }
.btn-silver:hover { background: #94a3b8; color: white; }
.btn-silver:hover .we-level-text { color: white; }

.btn-gold { border-color: #eab308; background: #fefce8; }
.btn-gold:hover { background: #eab308; color: white; }
.btn-gold:hover .we-level-text { color: white; }

.btn-random { border-color: #6366f1; background: #eef2ff; grid-column: 1 / -1; flex-direction: row; gap: 10px; padding: 12px; }
.btn-random:hover { background: #6366f1; color: white; }
.btn-random:hover .we-level-text { color: white; }
.btn-random .we-level-icon { margin-bottom: 0; font-size: 1.4rem; }

@keyframes modalPop {
  from { opacity: 0; transform: scale(0.9); }
  to { opacity: 1; transform: scale(1); }
}
</style>