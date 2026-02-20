<template>
  <div class="we-page">
    <main class="we-container we-main">

      <section class="we-section we-section--top">
        <div class="we-section__head">
          <div class="we-section__title-group">
            <h2 class="we-title"><i class="fa-solid fa-microphone-lines we-rose"></i> 영어 회화 연습</h2>
            <p class="we-section__sub">상황 기반 리스닝 및 스피킹</p>
          </div>
          <button class="we-btn we-btn--small we-btn--outline" @click="showLevelModal = true">
            <i class="fa-solid fa-layer-group"></i> 난이도 변경
          </button>
        </div>
      </section>

      <div v-if="isLoading" class="we-loading-state">
        <i class="fa-solid fa-circle-notch fa-spin"></i> 문장을 불러오는 중입니다...
      </div>

      <section v-else-if="sentences.length === 0" class="we-empty-state">
        <div class="we-card we-panel">
          <p>아직 선택된 난이도가 없습니다.<br>난이도를 선택하고 회화 연습을 시작해보세요!</p>
          <button class="we-btn we-btn--rose" @click="showLevelModal = true">난이도 선택하기</button>
        </div>
      </section>

      <section v-else class="we-section">
        <div class="we-card we-speaking-card">
          <div class="we-speaking-header">
            <span :class="['we-badge', badgeColor]">{{ selectedLevel }}</span>
            <div class="we-progress-text">
              <span class="current">{{ currentIndex + 1 }}</span> / {{ sentences.length }}
            </div>
          </div>

          <div class="we-sentence-area">

            <div v-if="!showText" class="we-hidden-state">
              <button class="we-btn we-btn--outline we-btn-show" @click="showText = true">
                <i class="fa-solid fa-eye"></i> 문장 확인하기
              </button>
              <p class="we-hint">먼저 원어민 음성을 듣고 따라해 보세요!</p>
            </div>

            <div v-else class="we-visible-state fade-in">
              <h3 class="we-sentence-en">{{ currentSentence.en }}</h3>
              <p class="we-sentence-ko">{{ currentSentence.ko }}</p>

              <div class="we-tags" v-if="currentSentence.tags && currentSentence.tags.length > 0">
                <span class="we-tag" v-for="tag in currentSentence.tags" :key="tag">#{{ tag }}</span>
              </div>
            </div>

          </div>

          <div class="we-audio-controls">
            <button class="we-btn-icon" @click="prevSentence" :disabled="currentIndex === 0">
              <i class="fa-solid fa-chevron-left"></i>
            </button>

            <button class="we-btn-play" @click="playAudio" :disabled="!hasAudio" :class="{ 'is-playing': isPlaying }">
              <i class="fa-solid" :class="isPlaying ? 'fa-stop' : 'fa-volume-high'"></i>
            </button>
            <audio
                ref="audioPlayer"
                :key="audioSrc"  :src="audioSrc"
                @ended="isPlaying = false"
                @pause="isPlaying = false"
                @play="isPlaying = true"
                style="display: none;"
            ></audio>

            <button class="we-btn-icon" @click="nextSentence" :disabled="currentIndex === sentences.length - 1">
              <i class="fa-solid fa-chevron-right"></i>
            </button>
          </div>
        </div>
      </section>

      <div v-if="showLevelModal" class="we-modal-overlay" @click.self="showLevelModal = false">
        <div class="we-modal">
          <div class="we-modal__header">
            <h3>난이도 선택</h3>
            <button class="we-close-btn" @click="showLevelModal = false"><i class="fa-solid fa-xmark"></i></button>
          </div>
          <div class="we-modal__body">
            <div class="we-level-grid">
              <button class="we-level-btn btn-bronze" @click="startSpeaking('Bronze')">
                <span class="we-level-icon">🥉</span>
                <span class="we-level-text">BRONZE(쉬움)</span>
              </button>
              <button class="we-level-btn btn-silver" @click="startSpeaking('Silver')">
                <span class="we-level-icon">🥈</span>
                <span class="we-level-text">SILVER(중간)</span>
              </button>
              <button class="we-level-btn btn-gold" @click="startSpeaking('Gold')">
                <span class="we-level-icon">🥇</span>
                <span class="we-level-text">GOLD(어려움)</span>
              </button>
              <button class="we-level-btn btn-random" @click="startSpeaking('RANDOM')">
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
import {ref, computed, nextTick, watch} from 'vue';

// 상태 관리
const showLevelModal = ref(true);
const isLoading = ref(false);
const selectedLevel = ref('');
const sentences = ref([]);
const currentIndex = ref(0);
const isPlaying = ref(false);
const showText = ref(false); // 텍스트 숨김/노출 상태

const audioPlayer = ref(null);

// 현재 선택된 문장 DTO
const currentSentence = computed(() => sentences.value[currentIndex.value] || {});

// 오디오 존재 여부 및 소스 URL
const hasAudio = computed(() => !!currentSentence.value.audioUrl);
const audioSrc = computed(() => currentSentence.value.audioUrl || '');

const badgeColor = computed(() => {
  if (selectedLevel.value === 'BRONZE') return 'we-badge--amber';
  if (selectedLevel.value === 'SILVER') return 'we-badge--blue';
  if (selectedLevel.value === 'GOLD') return 'we-badge--rose';
  return 'we-badge--green';
});

const resetSentenceState = async () => {
  showText.value = false;
  isPlaying.value = false;

  if (audioPlayer.value) {
    audioPlayer.value.pause();
  }

  await nextTick();
  if (audioPlayer.value) {
    audioPlayer.value.load();
  }
};

const startSpeaking = async (level) => {
  showLevelModal.value = false;
  selectedLevel.value = level;
  isLoading.value = true;
  sentences.value = [];
  currentIndex.value = 0;
  showText.value = false;

  try {
    const token = localStorage.getItem('token');
    if (!token) {
      alert('로그인이 필요한 서비스입니다.');
      router.push('/login');
      return;
    }
    const headers = {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    };

    const response = await fetch(`/api/audio?level=${level}`, { headers });
    if (response.ok) {
      const data = await response.json();
      sentences.value = data.ok || [];
    } else {
      console.error('데이터를 불러오는데 실패했습니다.');
    }
  } catch (err) {
    console.error('API Error:', err);
  } finally {
    isLoading.value = false;
    nextTick(() => {
      if (audioPlayer.value) audioPlayer.value.load();
    });
  }
};

// 이동 컨트롤
const prevSentence = () => {
  if (currentIndex.value > 0) {
    currentIndex.value--;
    resetSentenceState();
  }
};

const nextSentence = () => {
  if (currentIndex.value < sentences.value.length - 1) {
    currentIndex.value++;
    resetSentenceState();
  }
};
const playAudio = () => {
  if (!audioPlayer.value || !hasAudio.value) return;

  if (isPlaying.value) {
    audioPlayer.value.pause();
    audioPlayer.value.currentTime = 0;
    isPlaying.value = false;
  } else {
    audioPlayer.value.play().catch(e => console.error("Audio play failed:", e));
    isPlaying.value = true;
  }
};
</script>

<style scoped>
@import "@/assets/workly-english.css";

.we-section__title-group { display: flex; flex-direction: column; gap: 4px; }
.we-title { margin: 0; font-size: 1.25rem; font-weight: 700; color: #1e293b; }
.we-rose { color: #f43f5e; }

/* 빈 상태 & 로딩 상태 */
.we-empty-state, .we-loading-state { text-align: center; padding: 40px 20px; color: #64748b; }
.we-empty-state p { margin-bottom: 20px; line-height: 1.5; }
.we-loading-state { font-size: 1.1rem; font-weight: 600; display: flex; align-items: center; justify-content: center; gap: 10px; }

/* 스피킹 카드 UI */
.we-speaking-card {
  padding: 30px;
  border-radius: 20px;
  min-height: 380px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.we-speaking-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #f1f5f9;
  padding-bottom: 15px;
  margin-bottom: 20px;
}

.we-progress-text { font-size: 1rem; font-weight: 600; color: #94a3b8; }
.we-progress-text .current { color: #1e293b; font-size: 1.2rem; }

/* 문장 텍스트 영역 */
.we-sentence-area {
  text-align: center;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  margin-bottom: 30px;
}

/* 텍스트 숨김 상태 (블라인드) */
.we-hidden-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}
.we-btn-show {
  border-radius: 25px;
  padding: 10px 24px;
  font-weight: 600;
  color: #475569;
  border-color: #cbd5e1;
}
.we-hint {
  font-size: 0.9rem;
  color: #94a3b8;
  margin: 0;
}

/* 텍스트 노출 상태 */
.we-visible-state { width: 100%; }
.we-sentence-en { font-size: 1.8rem; font-weight: 700; color: #0f172a; margin: 0 0 15px 0; line-height: 1.3; word-break: keep-all; }
.we-sentence-ko { font-size: 1.1rem; color: #64748b; margin: 0 0 20px 0; }

.we-tags { display: flex; justify-content: center; gap: 8px; flex-wrap: wrap; }
.we-tag { background: #f1f5f9; color: #475569; font-size: 0.8rem; padding: 4px 10px; border-radius: 20px; font-weight: 600; }

.fade-in {
  animation: fadeIn 0.4s ease-out forwards;
}
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.we-audio-controls { display: flex; align-items: center; justify-content: center; gap: 30px; }
.we-btn-icon { background: none; border: none; font-size: 1.5rem; color: #cbd5e1; cursor: pointer; transition: 0.2s; padding: 10px; }
.we-btn-icon:not(:disabled):hover { color: #64748b; transform: scale(1.1); }
.we-btn-icon:disabled { opacity: 0.5; cursor: not-allowed; }

.we-btn-play {
  width: 60px; height: 60px;
  border-radius: 50%; border: none;
  background: #f43f5e; color: white;
  font-size: 1.5rem;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer; transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  box-shadow: 0 8px 16px rgba(244, 63, 94, 0.25);
}
.we-btn-play:hover { transform: translateY(-3px) scale(1.05); box-shadow: 0 12px 20px rgba(244, 63, 94, 0.3); }
.we-btn-play:active { transform: scale(0.95); }
.we-btn-play:disabled { background: #e2e8f0; color: #94a3b8; cursor: not-allowed; box-shadow: none; transform: none; }
.we-btn-play.is-playing { background: #0f172a; box-shadow: 0 8px 16px rgba(15, 23, 42, 0.25); }

/* 모달 애니메이션 (기존 코드 유지) */
.we-modal-overlay { position: fixed; top: 0; left: 0; width: 100vw; height: 100vh; background: rgba(0, 0, 0, 0.4); display: flex; align-items: center; justify-content: center; z-index: 9999; backdrop-filter: blur(2px); }
.we-modal { background: #ffffff; width: 90%; max-width: 360px; border-radius: 20px; box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1); overflow: hidden; animation: modalPop 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275); }
.we-modal__header { display: flex; justify-content: space-between; align-items: center; padding: 16px 20px; border-bottom: 1px solid #f1f5f9; }
.we-modal__header h3 { margin: 0; font-size: 1.1rem; font-weight: 700; color: #1e293b; }
.we-close-btn { background: none; border: none; font-size: 1.2rem; color: #94a3b8; cursor: pointer; padding: 4px; }
.we-modal__body { padding: 20px; }
.we-level-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
.we-level-btn { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 16px 10px; border: 2px solid transparent; border-radius: 12px; background: #f8fafc; cursor: pointer; transition: all 0.2s; font-weight: 600; }
.we-level-icon { font-size: 1.8rem; margin-bottom: 8px; }
.we-level-text { font-size: 0.9rem; color: #334155; }
.we-level-btn:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(0,0,0,0.05); }

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