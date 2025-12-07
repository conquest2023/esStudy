<script setup>
import { toasts } from '@/composables/useToast'
const formatAnalysisLine = (text) => {
  if (!text) return ''

  const urlRegex = /(https?:\/\/www\.youtube\.com\/results\?search_query=[^\s]+)/g;

  let formattedText = text
      .replace(/(추천 키워드:)/g, '<b>$1</b>')
      .replace(/(이유:)/g, '<br/><small class="reason">$1</small>')
      .replace(/(링크:)/g, ''); // "링크:" 텍스트는 제거

  formattedText = formattedText.replace(urlRegex, (url) => {
    return `<a href="${url}" target="_blank" class="analysis-link-btn">
              <i class="fab fa-youtube"></i> 검색 결과 보기
            </a>`;
  });

  return formattedText;
}
const emitClick = (t) => { t.onClick && t.onClick() }
const emitClose = (t) => { t.onClose ? t.onClose() : (t._close && t._close()) }
</script>

<template>
  <div class="toast-host">
    <TransitionGroup name="toast-list" tag="div" class="toast-stack">
      <div v-for="t in toasts" :key="t.id">
        <div
            v-if="t.type === 'top3'"
            class="toast-card top3 clickable"
            @click="emitClick(t)"
        >
          <div class="head">
            <span class="emoji">💎</span>
            <strong>{{ t.title || '오늘의 베스트 Top 3' }}</strong>
          </div>

          <div class="msg">{{ t.message }}</div>

          <ol class="list">
            <li
                v-for="(p, i) in (t.posts || [])"
                :key="p.postId"
                @click.stop="t.onItemClick ? t.onItemClick(p) : (t.onClick && t.onClick())"
                class="item"
                :title="p.title"
            >
              <span class="rank">{{ i + 1 }}</span>
              <div class="meta">
                <div class="title text-truncate">{{ p.title }}</div>
                <div class="sub text-muted">@{{ p.username }}</div>
              </div>
              <i class="fas fa-chevron-right arrow"></i>
            </li>
          </ol>

          <div class="actions">
            <button class="btn" @click.stop="t.onClick && t.onClick()">전체 보기</button>
            <button class="btn ghost" @click.stop="emitClose(t)">나중에</button>
          </div>
        </div>

        <div
            v-else-if="t.type === 'poll-missing'"
            class="toast-card poll clickable"
            @click="emitClick(t)"
        >
          <div class="head">
            <span class="emoji">🗳</span>
            <strong>{{ t.title || '아직 참여하지 않은 투표' }}</strong>
            <span v-if="t.count != null" class="badge">{{ t.count }}</span>
          </div>

          <div class="msg">
            {{ t.message || `참여하지 않은 투표가 ${t.count ?? (t.posts?.length || 0)}개 있습니다.` }}
          </div>

          <ul class="list">
            <li
                v-for="p in (t.posts || [])"
                :key="p.postId"
                class="item"
                :title="p.title"
            >
              <button class="link" @click.stop="t.onItemClick ? t.onItemClick(p) : (t.onClick && t.onClick())">
                • {{ p.title }}
              </button>
              <i class="fas fa-chevron-right arrow"></i>
            </li>
          </ul>

          <div class="actions">
            <button class="btn" @click.stop="t.onClick && t.onClick()">모두 보기</button>
            <button class="btn ghost" @click.stop="emitClose(t)">나중에</button>
          </div>
        </div>
        <div
            v-else-if="t.type === 'analysis'"
            class="toast-card analysis center-modal"
            @click.stop
        >
          <div class="head">
            <span class="emoji">✨</span>
            <strong>{{ t.title || '오늘의 하루 분석 리포트' }}</strong>
          </div>

          <div class="msg">{{ t.message }}</div>

          <div class="analysis-body">
            <div v-for="(line, idx) in t.analysis" :key="idx" class="analysis-line">
              <i class="fas fa-check-circle check-icon"></i>
              <span class="text" v-html="formatAnalysisLine(line)"></span>
            </div>
          </div>

          <div class="actions center-actions">
            <button class="btn btn-full" @click.stop="emitClick(t)">상세 보러가기</button>
            <button class="btn ghost btn-full" @click.stop="emitClose(t)">닫기</button>
          </div>
        </div>
        <div
            v-else
            class="toast-card clickable generic-toast"
            @click="emitClick(t)"
        >
          <button class="btn-close" @click.stop="emitClose(t)" title="닫기">
            <i class="fas fa-times"></i>
          </button>

          <div class="head">
            <strong class="title text-truncate">{{ t.title || '알림' }}</strong>
          </div>
          <div class="msg">{{ t.msg || t.message }}</div>
        </div>
      </div>
    </TransitionGroup>
  </div>
</template>

<style scoped>
.toast-host {
  position: fixed;
  right: 16px;
  top: calc(16px + env(safe-area-inset-top, 0px));
  z-index: 9999;
  pointer-events: none;
}
.toast-stack {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
/* 닫기 버튼 스타일 */
.toast-card {
  position: relative; /* 버튼 배치를 위해 추가 */
}


.btn-close:hover {
  background: #f1f3f5;
  color: #495057;
}

/* 일반 알림 전용 패딩 (닫기 버튼 공간 확보) */
.generic-toast {
  padding-right: 40px !important;
}

/* 닫기 버튼 강제 스타일링 */
.btn-close {
  position: absolute !important;
  top: 10px !important;
  right: 10px !important;
  width: 26px;
  height: 26px;
  border-radius: 50%;
  border: none;
  background: #f1f3f5; /* 버튼 배경색을 줘서 가시성 확보 */
  color: #495057 !important; /* 아이콘 색상을 진하게 */
  display: flex !important; /* grid 대신 flex로 확실히 중앙 정렬 */
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 20;
  pointer-events: auto; /* 클릭 가능하게 설정 */
}

.btn-close i {
  font-size: 14px;
}

.btn-close:hover {
  background: #e9ecef;
  color: #000 !important;
}

/* 제목이 길어질 때 버튼 침범 방지 */
.generic-toast .title {
  display: block;
  max-width: 180px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.generic-toast .head .title {
  max-width: 90%; /* 제목이 길어져도 닫기 버튼을 가리지 않게 */
}
.toast-card {
  pointer-events: auto;
  max-width: min(360px, 92vw);
  background: #fff;
  color: #111;
  border-radius: 16px;
  box-shadow: 0 10px 24px rgba(0,0,0,.14);
  padding: 12px 14px;
  border: 1px solid #eef0f3;
}
.toast-card.clickable { cursor: pointer; }
.toast-card .head { display:flex; align-items:center; gap:8px; margin-bottom:6px; }
.toast-card .msg { font-size:.92rem; margin-bottom:8px; }

.top3 .emoji { font-size: 1.05rem; }
.top3 .list { list-style: none; margin: 0; padding: 0; display: grid; gap: 8px; }
.top3 .item {
  display: grid; grid-template-columns: 22px 1fr 18px; align-items: center;
  gap: 10px; padding: 8px 10px; border-radius: 12px; background: #fafbff;
}
.top3 .item:hover { background: #f1f4ff; }
.top3 .rank {
  width: 22px; height: 22px; display: grid; place-items: center;
  border-radius: 7px; background: #e9efff; font-weight: 700; color: #2452f0;
}
.top3 .meta .title { font-weight: 600; }
.top3 .meta .sub { font-size: .8rem; }
.top3 .arrow { color: #9aa4b2; }

.actions { display:flex; gap:8px; margin-top:10px; }
.btn {
  appearance:none; border:0; padding:8px 12px; border-radius:10px;
  background: linear-gradient(90deg,#4a90e2,#007aff); color:#fff; font-weight:700;
}
.btn.ghost {
  background: transparent; color: #3b82f6; border: 1px solid #dbe3ff;
}

.toast-list-enter-from, .toast-list-leave-to {
  opacity: 0; transform: translateY(-8px) scale(.98);
}
.toast-list-enter-active, .toast-list-leave-active {
  transition: .18s ease;
}

.poll .emoji { font-size: 1.05rem; }
.poll .badge {
  margin-left: auto;
  font-size: .75rem;
  line-height: 1;
  padding: 4px 8px;
  border-radius: 999px;
  background: #eef7ff;
  color: #0b69ff;
  border: 1px solid #dbe8ff;
}
.poll .list { list-style: none; margin: 0; padding: 0; display: grid; gap: 6px; }
.poll .item {
  display: grid; grid-template-columns: 1fr 16px; align-items: center;
  gap: 10px; padding: 6px 8px; border-radius: 10px; background: #fafbff;
}
.poll .item:hover { background: #f1f4ff; }
.poll .link {
  background: none; border: 0; padding: 0; text-align: left; cursor: pointer;
  font-weight: 600; color: #111;
}
.poll .arrow { color: #9aa4b2; }

@media (max-width: 600px) {
  .toast-host { right: 12px; top: calc(12px + env(safe-area-inset-top, 0px)); }
  .toast-card { max-width: min(320px, 90vw); }
  .top3 .item, .poll .item { padding: 8px; }
}
/* ✨ Analysis 전용 중앙 알림 스타일 */
.toast-card.analysis.center-modal {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%); /* 화면 정중앙 */
  width: min(400px, 90vw);
  max-height: 80vh;
  overflow-y: auto;
  border: 2px solid #e1e8ff;
  box-shadow: 0 20px 50px rgba(0, 75, 255, 0.15); /* 강조된 그림자 */
  padding: 24px;
}

.analysis-body {
  display: flex;
  flex-direction: column;
  gap: 12px;
  background: #f8faff;
  padding: 16px;
  border-radius: 12px;
  margin-top: 10px;
}

.analysis-line {
  display: flex;
  gap: 10px;
  line-height: 1.5;
  font-size: 0.95rem;
}

.check-icon { color: #3b82f6; margin-top: 3px; }

:deep(.analysis-link-btn) {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  background-color: #ff000010; /* 유튜브 레드 투명도 */
  color: #ff0000;
  padding: 4px 10px;
  border-radius: 6px;
  text-decoration: none;
  font-weight: 600;
  font-size: 0.85rem;
  margin: 5px 0;
  border: 1px solid #ff000030;
  transition: all 0.2s;
}

:deep(.analysis-link-btn:hover) {
  background-color: #ff0000;
  color: #fff;
}

.analysis-line {
  align-items: flex-start; /* 아이콘과 텍스트 상단 정렬 */
  margin-bottom: 15px; /* 항목 간 간격 확보 */
}

:deep(b) {
  color: #1e293b;
  font-size: 1rem;
}

:deep(.reason) {
  display: block;
  color: #64748b;
  margin-top: 4px;
  line-height: 1.4;
}

.center-actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

.btn-full { width: 100%; text-align: center; }

</style>
