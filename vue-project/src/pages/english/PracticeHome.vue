<template>
  <div class="we-page">
    <main class="we-container we-main">
      <section class="we-section we-section--top">
        <div class="we-section__head">
          <div>
<!--            <h1 class="we-section__title we-section__title&#45;&#45;hero">오늘은 어떤 학습을 할까요?</h1>-->
            <p class="we-section__sub we-section__sub--hero">RC · 단어 · 회화 · 오답 복습</p>
          </div>

          <button class="we-btn we-btn--small we-btn--outline" @click="go('/wrong-notes')">
            <i class="fa-solid fa-book-bookmark"></i>
            오답노트 바로가기
          </button>
        </div>

        <div class="we-grid">
          <!-- RC -->
          <article class="we-card we-track" @click="go('/practice/rc')">
            <div class="we-track__top">
              <div class="we-track__icon we-track__icon--blue">
                <i class="fa-solid fa-file-pen"></i>
              </div>
              <span class="we-badge we-badge--blue">RC</span>
            </div>
            <div class="we-track__name">RC 실전풀이</div>
            <div class="we-track__desc">문법·어휘 빈칸 채우기 (Part 5)</div>

            <div class="we-track__meta">
              <span><i class="fa-solid fa-layer-group"></i> Part 5</span>
              <span><i class="fa-solid fa-stopwatch"></i> 10문항</span>
              <span><i class="fa-solid fa-star"></i> Bronze~Gold</span>
            </div>

            <div class="we-track__cta">
              START <i class="fa-solid fa-chevron-right"></i>
            </div>
          </article>

          <!-- VOCA -->
          <article class="we-card we-track" @click="go('/practice/vocab')">
            <div class="we-track__top">
              <div class="we-track__icon we-track__icon--green">
                <i class="fa-solid fa-spell-check"></i>
              </div>
              <span class="we-badge we-badge--green">VOCA</span>
            </div>
            <div class="we-track__name">영어 단어</div>
            <div class="we-track__desc">뜻 · 용법 · 예문 객관식</div>

            <div class="we-track__meta">
              <span><i class="fa-solid fa-brain"></i> 20단어</span>
              <span><i class="fa-solid fa-bolt"></i> 데일리</span>
              <span><i class="fa-solid fa-code"></i> IT 중심</span>
            </div>

            <div class="we-track__cta">
              START <i class="fa-solid fa-chevron-right"></i>
            </div>
          </article>

          <!-- SPEAKING -->
          <article class="we-card we-track" @click="go('/practice/speaking')">
            <div class="we-track__top">
              <div class="we-track__icon we-track__icon--rose">
                <i class="fa-solid fa-microphone-lines"></i>
              </div>
              <span class="we-badge we-badge--rose">SPEAKING</span>
            </div>
            <div class="we-track__name">영어 회화</div>
            <div class="we-track__desc">상황 기반 선택형 / 말하기</div>

            <div class="we-track__meta">
              <span><i class="fa-solid fa-headphones"></i> 쉐도잉</span>
              <span><i class="fa-solid fa-microphone"></i> 녹음</span>
              <span><i class="fa-solid fa-comment-dots"></i> 피드백</span>
            </div>

            <div class="we-track__cta">
              START <i class="fa-solid fa-chevron-right"></i>
            </div>
          </article>

          <!-- REVIEW -->
          <article class="we-card we-track" @click="go('/wrong-notes')">
            <div class="we-track__top">
              <div class="we-track__icon we-track__icon--amber">
                <i class="fa-solid fa-book-bookmark"></i>
              </div>
              <span class="we-badge we-badge--amber">REVIEW</span>
            </div>
            <div class="we-track__name">오답노트</div>
            <div class="we-track__desc">틀린 문제만 재풀이 · 메모로 고정</div>

            <div class="we-track__meta">
              <span><i class="fa-solid fa-triangle-exclamation"></i> 오답 집중</span>
              <span><i class="fa-solid fa-pen"></i> 메모</span>
              <span><i class="fa-solid fa-rotate"></i> 재풀이</span>
            </div>

            <div class="we-track__cta">
              REVIEW <i class="fa-solid fa-chevron-right"></i>
            </div>
          </article>
        </div>
      </section>

<!--      &lt;!&ndash; TIP &ndash;&gt;-->
<!--      <section class="we-tip">-->
<!--        <i class="fa-solid fa-lightbulb"></i>-->
<!--        <b>TIP</b>-->
<!--        <span>-->
<!--          오답노트에서 <b>“오답만 재풀이”</b> 루틴 만들면 점수 빨리 오른다 아시겠어열~??-->
<!--        </span>-->
<!--      </section>-->

      <!-- (Optional) Secondary info: mission/progress (아래로 강등) -->
      <section class="we-section we-section--secondary">
        <div class="we-split">
          <div class="we-card we-panel">
            <div class="we-panel__head">
              <h3 class="we-panel__title">
                <i class="fa-solid fa-fire we-fire"></i>
                오늘의 미션
              </h3>
              <span class="we-pill">12 DAYS STREAK</span>
            </div>

            <div class="we-mission__list">
              <div class="we-mission__item" v-for="m in missions" :key="m.id">
                <div :class="['we-mission__icon', m.completed ? 'is-done' : '']">
                  <i :class="m.completed ? 'fa-solid fa-check' : 'fa-regular fa-circle'"></i>
                </div>
                <div class="we-mission__text">
                  <div :class="['we-mission__name', m.completed ? 'is-done' : '']">
                    {{ m.text }}
                  </div>
                  <div class="we-mission__xp">{{ m.point }} XP</div>
                </div>
              </div>
            </div>
          </div>

          <div class="we-card we-panel">
            <div class="we-panel__head">
              <h3 class="we-panel__title">
                <i class="fa-solid fa-chart-line we-blue"></i>
                진행도
              </h3>
              <span class="we-pill we-pill--soft">Total</span>
            </div>

            <div class="we-progress">
              <div class="we-progress__row">
                <div class="we-muted">RC</div>
                <div class="we-bar"><div class="we-bar__fill" style="width: 72%"></div></div>
                <div class="we-muted">72%</div>
              </div>

              <div class="we-progress__row">
                <div class="we-muted">VOCA</div>
                <div class="we-bar"><div class="we-bar__fill" style="width: 54%"></div></div>
                <div class="we-muted">54%</div>
              </div>

              <div class="we-progress__row">
                <div class="we-muted">SPEAK</div>
                <div class="we-bar"><div class="we-bar__fill" style="width: 35%"></div></div>
                <div class="we-muted">35%</div>
              </div>

              <div class="we-progress__big">
                <div class="we-progress__num">65%</div>
                <div class="we-progress__hint">이번 주 목표 달성률</div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- Floating -->
      <button class="we-fab" @click="go('/wrong-notes')" title="오답노트">
        <i class="fa-solid fa-book-bookmark"></i>
      </button>
    </main>
  </div>
</template>

<script setup>
import { useRouter } from "vue-router";

const router = useRouter();
const go = (path) => router.push(path);

const missions = [
  { id: 1, text: "RC Part 5 10문제 풀기", point: 200, completed: true },
  { id: 2, text: "신규 단어 20개 학습", point: 150, completed: true },
  { id: 3, text: "쉐도잉 5문장 녹음하기", point: 150, completed: false },
];
</script>

<style scoped>
@import "@/assets/workly-english.css";
</style>
