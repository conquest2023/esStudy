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
              <p class="we-hint">먼저 원어민 음성을 듣고 따라해 보세요!</p>

              <div class="we-dictation-box">
                <input
                    type="text"
                    v-model="userInput"
                    class="we-input"
                    placeholder="들리는 문장을 영어로 적어보세요..."
                    @keyup.enter="checkAnswer"
                />
                <button class="we-btn we-btn--rose we-btn--check" @click="checkAnswer">
                  <i class="fa-solid fa-check"></i> 제출
                </button>
              </div>

              <div class="we-divider"><span>OR</span></div>

              <button class="we-btn we-btn--outline we-btn-show" @click="showText = true">
                <i class="fa-solid fa-eye"></i> 그냥 문장 확인하기
              </button>
            </div>

            <div v-else class="we-visible-state fade-in">

              <div v-if="checkResult === 'correct'" class="we-result we-result--success">
                <i class="fa-solid fa-circle-check"></i> 정답입니다! 아주 잘 들으셨어요.
              </div>
              <div v-else-if="checkResult === 'incorrect'" class="we-result we-result--fail">
                <i class="fa-solid fa-circle-xmark"></i> 틀렸습니다! 다시 시도해 보세요.
              </div>

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
import {ref, computed, nextTick} from 'vue';
import {useRouter} from 'vue-router'; // 라우터 추가

const router = useRouter(); // 미로그인 처리를 위해

// 상태 관리
const showLevelModal = ref(true);
const isLoading = ref(false);
const selectedLevel = ref('');
const sentences = ref([]);
const currentIndex = ref(0);
const isPlaying = ref(false);
const showText = ref(false);

const audioPlayer = ref(null);

// 추가된 상태 변수 (사용자 입력값 및 채점 결과)
const userInput = ref('');
const checkResult = ref(null);

const currentSentence = computed(() => sentences.value[currentIndex.value] || {});

const hasAudio = computed(() => {
  const s = currentSentence.value;
  return !!s.audioUrl || (s.audioUrls && s.audioUrls.length > 0);
});

// 백엔드에서 배열로 주고 있다면 currentIndex를 이용
const audioSrc = computed(() => {
  const s = currentSentence.value;

  // 1. 백엔드에서 단일 String (audioUrl)로 깔끔하게 보내주는 경우
  if (s.audioUrl) {
    return s.audioUrl;
  }

  // 2. 백엔드에서 예전처럼 10개짜리 배열 (audioUrls)로 보내주는 경우
  if (s.audioUrls && s.audioUrls.length > currentIndex.value) {
    return s.audioUrls[currentIndex.value];
  }

  return '';
});

const badgeColor = computed(() => {
  if (selectedLevel.value === 'Bronze') return 'we-badge--amber';
  if (selectedLevel.value === 'Silver') return 'we-badge--blue';
  if (selectedLevel.value === 'Gold') return 'we-badge--rose';
  return 'we-badge--green';
});

// 정답 비교를 위한 텍스트 포맷팅 (대소문자, 문장부호 무시)
const formatText = (text) => {
  if (!text) return '';
  return text.replace(/[.,!?]/g, '').trim().toLowerCase();
};

// 정답 확인 로직
// 정답 확인 및 로깅 처리
const checkAnswer = async () => {
  if (!userInput.value.trim()) {
    alert('들리는 문장을 먼저 입력해주세요!');
    return;
  }

  const isMatch = formatText(userInput.value) === formatText(currentSentence.value.en);

  // 결과 UI 표시
  checkResult.value = isMatch ? 'correct' : 'incorrect';
  showText.value = true;


  await logAnswer(isMatch);
};

const logAnswer = async (isCorrectAnswer) => {
  const token = localStorage.getItem('token');
  if (!token)
    return;

  const payload = {
    objectId: currentSentence.value.id,
    chosenAnswer: userInput.value,
    isCorrect: isCorrectAnswer,
    category: 'conversation',
    part: 0,
    level: currentSentence.value.level.toUpperCase()
  };

  try {
    const response = await fetch('/api/english/log', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(payload)
    });
    if (!response.ok) {
      console.error('학습 로그 기록 실패:', response.status);
    }
  } catch (err) {
    console.error('API Error:', err);
  }
};

const resetSentenceState = async () => {
  showText.value = false;
  isPlaying.value = false;
  userInput.value = '';     // 입력값 초기화
  checkResult.value = null; // 결과 초기화

  if (audioPlayer.value) {
    audioPlayer.value.pause();
  }
  await nextTick();
  if (audioPlayer.value) {
    audioPlayer.value.load();
  }
};

const startSpeaking = async (level) => {
  // 로그인 안 되어 있으면 튕겨냄
  const token = localStorage.getItem('token');
  if (!token) {
    alert('로그인이 필요한 서비스입니다.');
    router.push('/login');
    return;
  }

  showLevelModal.value = false;
  selectedLevel.value = level;
  isLoading.value = true;
  sentences.value = [];
  currentIndex.value = 0;

  // 상태 초기화
  showText.value = false;
  userInput.value = '';
  checkResult.value = null;

  try {
    const headers = {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    };

    const response = await fetch(`/api/audio?level=${level}`, {headers});

    // 만료된 토큰 방어
    if (response.status === 401 || response.status === 403) {
      localStorage.removeItem('token');
      alert('세션이 만료되었습니다. 다시 로그인해주세요.');
      router.push('/login');
      return;
    }

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

/* 상단 타이틀 */
.we-section__title-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.we-title {
  margin: 0;
  font-size: 1.25rem;
  font-weight: 700;
  color: #1e293b;
}

.we-rose {
  color: #f43f5e;
}

/* 상태 메시지 */
.we-empty-state, .we-loading-state {
  text-align: center;
  padding: 40px 20px;
  color: #64748b;
}

.we-empty-state p {
  margin-bottom: 20px;
  line-height: 1.5;
}

.we-loading-state {
  font-size: 1.1rem;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

/* 카드 UI */
.we-speaking-card {
  padding: 30px;
  border-radius: 20px;
  min-height: 420px;
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

.we-progress-text {
  font-size: 1rem;
  font-weight: 600;
  color: #94a3b8;
}

.we-progress-text .current {
  color: #1e293b;
  font-size: 1.2rem;
}

/* 문장 영역 */
.we-sentence-area {
  text-align: center;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  margin-bottom: 30px;
}

/* 1. 타이핑 & 숨김 영역 (새로 추가/수정된 부분) */
.we-hidden-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
  width: 100%;
  max-width: 400px;
}

.we-hint {
  font-size: 0.95rem;
  color: #64748b;
  margin: 0;
  line-height: 1.4;
  font-weight: 600;
}

.we-dictation-box {
  display: flex;
  width: 100%;
  gap: 8px;
}

.we-input {
  flex: 1;
  padding: 12px 16px;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  font-size: 1.05rem;
  outline: none;
  transition: border-color 0.2s;
  background: #f8fafc;
}

.we-input:focus {
  border-color: #f43f5e;
  background: #ffffff;
}

.we-btn--check {
  border-radius: 12px;
  padding: 0 20px;
  font-weight: 700;
  white-space: nowrap;
}

/* 'OR' 구분선 */
.we-divider {
  width: 100%;
  text-align: center;
  border-bottom: 1px solid #e2e8f0;
  line-height: 0.1em;
  margin: 10px 0;
}

.we-divider span {
  background: #fff;
  padding: 0 10px;
  color: #cbd5e1;
  font-size: 0.8rem;
  font-weight: bold;
}

.we-btn-show {
  border-radius: 25px;
  padding: 10px 24px;
  font-weight: 600;
  color: #475569;
  border-color: #cbd5e1;
  width: 100%;
}

/* 2. 텍스트 노출 상태 영역 */
.we-visible-state {
  width: 100%;
}

.we-sentence-en {
  font-size: 1.8rem;
  font-weight: 700;
  color: #0f172a;
  margin: 0 0 15px 0;
  line-height: 1.3;
  word-break: keep-all;
}

.we-sentence-ko {
  font-size: 1.1rem;
  color: #64748b;
  margin: 0 0 20px 0;
}

.we-tags {
  display: flex;
  justify-content: center;
  gap: 8px;
  flex-wrap: wrap;
}

.we-tag {
  background: #f1f5f9;
  color: #475569;
  font-size: 0.8rem;
  padding: 4px 10px;
  border-radius: 20px;
  font-weight: 600;
}

/* 채점 결과 배지 */
.we-result {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border-radius: 20px;
  font-weight: 700;
  font-size: 0.95rem;
  margin-bottom: 20px;
  animation: popIn 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.we-result--success {
  background-color: #dcfce7;
  color: #166534;
}

.we-result--fail {
  background-color: #fee2e2;
  color: #991b1b;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes popIn {
  from {
    transform: scale(0.8);
    opacity: 0;
  }
  to {
    transform: scale(1);
    opacity: 1;
  }
}

/* 오디오 컨트롤 */
.we-audio-controls {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 30px;
}

.we-btn-icon {
  background: none;
  border: none;
  font-size: 1.5rem;
  color: #cbd5e1;
  cursor: pointer;
  transition: 0.2s;
  padding: 10px;
}

.we-btn-icon:not(:disabled):hover {
  color: #64748b;
  transform: scale(1.1);
}

.we-btn-icon:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.we-btn-play {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  border: none;
  background: #f43f5e;
  color: white;
  font-size: 1.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  box-shadow: 0 8px 16px rgba(244, 63, 94, 0.25);
}

.we-btn-play:hover {
  transform: translateY(-3px) scale(1.05);
  box-shadow: 0 12px 20px rgba(244, 63, 94, 0.3);
}

.we-btn-play:active {
  transform: scale(0.95);
}

.we-btn-play:disabled {
  background: #e2e8f0;
  color: #94a3b8;
  cursor: not-allowed;
  box-shadow: none;
  transform: none;
}

.we-btn-play.is-playing {
  background: #0f172a;
  box-shadow: 0 8px 16px rgba(15, 23, 42, 0.25);
}

/* 모달 */
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
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.btn-bronze {
  border-color: #cd7f32;
  background: #fff8f2;
}

.btn-bronze:hover {
  background: #cd7f32;
  color: white;
}

.btn-bronze:hover .we-level-text {
  color: white;
}

.btn-silver {
  border-color: #94a3b8;
  background: #f8fafc;
}

.btn-silver:hover {
  background: #94a3b8;
  color: white;
}

.btn-silver:hover .we-level-text {
  color: white;
}

.btn-gold {
  border-color: #eab308;
  background: #fefce8;
}

.btn-gold:hover {
  background: #eab308;
  color: white;
}

.btn-gold:hover .we-level-text {
  color: white;
}

.btn-random {
  border-color: #6366f1;
  background: #eef2ff;
  grid-column: 1 / -1;
  flex-direction: row;
  gap: 10px;
  padding: 12px;
}

.btn-random:hover {
  background: #6366f1;
  color: white;
}

.btn-random:hover .we-level-text {
  color: white;
}

.btn-random .we-level-icon {
  margin-bottom: 0;
  font-size: 1.4rem;
}

@keyframes modalPop {
  from {
    opacity: 0;
    transform: scale(0.9);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}
</style>