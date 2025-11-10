<!-- TodoStickyWall.vue -->
<template>
  <div class="board-wrap container">
    <!-- ▣ 진행률 카드 -->
    <div v-if="todos.length" class="summary-card">
      <p>
        전체 Todo <strong>{{ todos.length }}</strong>개 /
        완료 <strong>{{ completedCount }}</strong>개
      </p>
      <div class="progress-bar-outer">
        <div class="progress-bar-inner" :style="{ width: progress + '%' }">
          {{ progress }}%
        </div>



      </div>
    </div>

    <!-- ▣ 스티키 월 -->
    <section class="sticky-wall">
      <article
          v-for="todo in todos"
          :key="todo.todo_id"
          class="sticky-note"
          :data-status="todo.status"
          :style="noteStyle(todo)"
      >
        <header class="note-header"><h6>{{ todo.title }}</h6></header>
        <p class="note-desc">{{ todo.description || '설명 없음' }}</p>

        <footer class="note-footer">
          <span class="badge priority">P{{ todo.priority }}</span>
          <div class="actions">
            <button
                class="btn-icon done"
                @click="completeTodo(todo)"
                :disabled="todo.status !== 'IN_PROGRESS'"
            >
              <i class="fas fa-check" />
            </button>
            <button class="btn-icon del" @click="deleteTodo(todo.todo_id)">
              <i class="fas fa-trash" />
            </button>
          </div>
        </footer>
      </article>

      <!-- ▣ 새 카드 추가 -->
      <button class="sticky-note add-note" @click="$router.push('/todo/new')">
        <i class="fas fa-plus fa-2x" />
      </button>
    </section>

    <slot></slot>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import api from '@/utils/api'

// ── 상태 변수
const todos           = ref([])
const completedCount  = ref(0)
const kibanaUrl       = ref('')
const palette         = ['#fff8b8', '#dff3f9', '#ffe0e4', '#ffe8d1', '#d1f7e1']

// ── 진행률
const progress = computed(() =>
    todos.value.length ? Math.round((completedCount.value / todos.value.length) * 100) : 0
)

// ── Todo 로드
async function fetchTodos () {
  const token = localStorage.getItem('token')
  const { data } = await api.get('/search/todo', {
    headers: { Authorization: 'Bearer ' + token }
  })
  // _color & _rotate 메타 주입 (한 번 고정)
  todos.value = data.todos.map(t => ({
    ...t,
    _color : palette[Math.floor(Math.random() * palette.length)],
    _rotate: (Math.random() * 4 - 2).toFixed(2) // -2° ~ +2°
  }))
  completedCount.value = data.completedCount || 0
}

// ── 카드용 스타일 반환
const noteStyle = todo => ({
  background: todo._color,
  transform : `rotate(${todo._rotate}deg)`
})

// ── 완료/삭제 로직 (기존 그대로)
async function completeTodo (todo) { /* ...snip (그대로) */ }
async function deleteTodo   (id)   { /* ...snip (그대로) */ }

// ── 최초 로드
onMounted(fetchTodos)
</script>

<style scoped>
/* === 기본 배경 & 보드 레이아웃 === */
body { font-family:'Pretendard','Inter',sans-serif;
  background:radial-gradient(circle at 30% 10%,#dfe8d8 0%,#cfdac7 100%);
  min-height:100vh; }

.board-wrap { max-width:1280px;margin:0 auto;
  padding:68px 40px 120px;background:#fff;
  border-radius:32px;box-shadow:0 22px 60px rgba(0,0,0,.12); }

/* === 진행률 카드 === */
.summary-card{display:flex;justify-content:space-between;align-items:center;
  gap:1.25rem;background:#fff;border-radius:18px;
  box-shadow:0 4px 16px rgba(0,0,0,.06);padding:1.2rem 1.6rem;
  margin-bottom:2.8rem;font-size:1rem;}
.progress-bar-outer{flex:0 0 48%;height:8px;border-radius:99px;background:#e6e9ee;}
.progress-bar-inner{height:100%;border-radius:99px;background:#2563eb;
  font-size:.75rem;color:#fff;display:flex;align-items:center;
  justify-content:flex-end;padding-right:.35rem;transition:width .3s ease;}

/* === 스티키 월 === */
.sticky-wall{display:grid;grid-template-columns:repeat(auto-fill,minmax(240px,1fr));gap:2.2rem;}

/* — 공통 노트 — */
.sticky-note{position:relative;overflow:hidden;height:220px;
  display:flex;flex-direction:column;justify-content:space-between;
  padding:1.4rem 1.6rem 1.2rem;border-radius:18px;
  box-shadow:0 6px 14px rgba(0,0,0,.08);transition:transform .18s ease,box-shadow .18s ease;}
.sticky-note:hover{transform:translateY(-6px) rotate(0deg);box-shadow:0 10px 28px rgba(0,0,0,.14);}
[data-status='DONE']{opacity:.55;}

/* 핀 데코 */
.sticky-note::before{content:'';position:absolute;top:14px;left:50%;transform:translateX(-50%);
  width:18px;height:18px;border-radius:50%;background:#374151;border:4px solid #fff;
  box-shadow:0 2px 6px rgba(0,0,0,.25);}

/* 텍스트 */
.note-header h6{margin:0 0 .55rem;font-weight:700;font-size:1.05rem;}
.note-desc{font-size:.9rem;line-height:1.45;white-space:pre-wrap;word-break:break-word;}

/* 푸터 */
.note-footer{margin-top:.9rem;display:flex;justify-content:space-between;align-items:center;}
.badge.priority{background:rgba(0,0,0,.18);color:#fff;font-size:.7rem;
  padding:.25rem .75rem;border-radius:99px;}
.actions .btn-icon{border:none;background:none;font-size:1.05rem;margin-left:.35rem;cursor:pointer;}
.btn-icon.done{color:#16a34a;}
.btn-icon.del{color:#ef4444;}

/* “+” 카드 */
.add-note{border:3px dashed #c4cdd9;background:#f7f9fc;color:#6b7280;
  display:flex;align-items:center;justify-content:center;
  transition:background .18s,border-color .18s;}
.add-note:hover{background:#ecf1f9;border-color:#94a3b8;}

/* === 모바일 === */
@media(max-width:768px){
  .board-wrap{padding:56px 20px 120px;}
  .summary-card{flex-direction:column;gap:1rem;font-size:.95rem;}
  .sticky-wall{gap:1.5rem;}
}
</style>
