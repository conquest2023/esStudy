<!-- /components/ToastHost.vue -->
<script setup>
import { toasts } from '@/composables/useToast'

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
            <button class="btn ghost" @click.stop="t.onClose && t.onClose()">나중에</button>
          </div>
        </div>

        <!-- 기본 텍스트 토스트 -->
        <div
            v-else
            class="toast-card clickable"
            @click="emitClick(t)"
        >
          <div class="head">
            <strong>{{ t.title || '알림' }}</strong>
          </div>
          <div class="msg">{{ t.msg }}</div>
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
.top3 .list {
  list-style: none; margin: 0; padding: 0; display: grid; gap: 8px;
}
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

@media (max-width: 600px) {
  .toast-host { right: 12px; top: calc(12px + env(safe-area-inset-top, 0px)); }
  .toast-card { max-width: min(320px, 90vw); }
  .top3 .item { padding: 8px; }
}
</style>
