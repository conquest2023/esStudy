<template>
  <div class="we-page">
    <header class="we-topbar">
      <div class="we-container we-topbar__inner">
        <div class="we-brand" @click="go('/practice')">
          <div class="we-logo">W</div>
          <div>
            <div class="we-brand__title">WORKLY <span>ENGLISH</span></div>
            <div class="we-brand__sub">Review Center</div>
          </div>
        </div>
        <div class="we-topbar__right">
          <button class="we-btn we-btn--small" @click="go('/practice/rc')">
            <i class="fa-solid fa-play"></i> 학습하러 가기
          </button>
        </div>
      </div>
    </header>

    <main class="we-container we-review">
      <!-- 1. Summary Stats Section -->
      <section class="we-summaryGrid">
        <div class="we-summaryCard is-main">
          <div class="we-summaryLabel">Total Wrong Notes</div>
          <div class="we-summaryValue">
            {{ filteredNotes.length }}<span>건</span>
          </div>
          <!-- 이건 아직 실제 통계 연결 전이라 임시 -->
          <div class="we-summaryTrend">
            <i class="fa-solid fa-arrow-down"></i> 지난주 대비 12% 감소
          </div>
        </div>

        <div class="we-summaryCard">
          <div class="we-summaryLabel">Weakness Part</div>
          <div class="we-summaryValue">{{ weaknessPartText }}</div>
          <div class="we-summaryTag">오답률 {{ weaknessRate }}%</div>
        </div>

        <div class="we-summaryCard">
          <div class="we-summaryLabel">Mastery Rate</div>
          <div class="we-summaryValue">{{ masteryRate }}<span>%</span></div>
          <div class="we-progressSmall">
            <div class="we-progressFill" :style="{ width: masteryRate + '%' }"></div>
          </div>
        </div>
      </section>

      <!-- 2. Filter Bar -->
      <div class="we-filterRow">
        <div class="we-tabGroup">
          <button
              v-for="cat in ['ALL', 'RC', 'VOCA']"
              :key="cat"
              :class="['we-tabItem', activeTab === cat ? 'is-active' : '']"
              @click="activeTab = cat"
          >
            {{ cat }}
          </button>
        </div>

        <div class="we-searchBox">
          <i class="fa-solid fa-magnifying-glass"></i>
          <input
              type="text"
              placeholder="오답 태그, 문장 검색..."
              v-model="searchQuery"
          />
        </div>
      </div>

      <!-- Loading -->
      <div v-if="loading && normalizedNotes.length === 0" class="we-loading">
        <i class="fa-solid fa-circle-notch fa-spin"></i> 오답노트를 불러오는 중...
      </div>

      <!-- Empty -->
      <div v-else-if="!loading && normalizedNotes.length === 0" class="we-empty">
        <div class="we-emptyTitle">오답노트가 없습니다.</div>
        <div class="we-emptySub">문제를 풀고 틀린 문제를 저장해보세요.</div>
        <button class="we-btn we-btn--small" @click="go('/practice/rc')">
          <i class="fa-solid fa-play"></i> RC 풀러 가기
        </button>
      </div>

      <!-- 3. Wrong Note List -->
      <div v-else class="we-noteList">
        <div v-for="note in filteredNotes" :key="note.id" class="we-noteCard">
          <div class="we-noteCard__head">
            <div class="we-chipRow">
              <span class="we-chip is-category">{{ note.category }}</span>
              <span class="we-chip is-level">{{ note.level }}</span>
              <span class="we-chip is-date">{{ note.solvedAt }}</span>
              <span class="we-chip is-part">Part {{ note.part }}</span>
            </div>

            <button class="we-btnIcon" @click="removeNote(note.id)">
              <i class="fa-solid fa-trash-can"></i>
            </button>
          </div>

          <div class="we-noteCard__body">
            <h3 class="we-passageSnippet" v-html="highlight(note.passage)"></h3>

            <div class="we-answerCompare">
              <div class="we-ansBox is-wrong">
                <div class="we-ansLabel">Your Answer</div>
                <div class="we-ansValue">
                  <i class="fa-solid fa-circle-xmark"></i>
                  {{ note.userAnswer ?? '-' }}
                </div>
              </div>

              <div class="we-ansBox is-correct">
                <div class="we-ansLabel">Correct Answer</div>
                <div class="we-ansValue">
                  <i class="fa-solid fa-circle-check"></i>
                  {{ note.correctAnswer ?? '-' }}
                </div>
              </div>
            </div>

            <div class="we-explanationBox">
              <div class="we-explainTitle">
                <i class="fa-solid fa-lightbulb"></i> Explanation
              </div>
              <p class="we-explainText">
                {{ note.explanation || '해설이 없습니다.' }}
              </p>
            </div>

            <!-- Memo (local only for now) -->
            <div class="we-memoPad">
              <div class="we-memoTitle">📌 My Learning Note</div>
              <textarea
                  class="we-memoInput"
                  v-model="note.memo"
                  placeholder="나만의 암기 비법을 기록하세요... (현재는 로컬 저장)"
              ></textarea>
            </div>
          </div>

          <div class="we-noteCard__footer">
            <div class="we-tags">
              <span v-for="tag in note.tags" :key="tag">#{{ tag }}</span>
            </div>

            <button class="we-btn we-btn--primary we-btn--small" @click="retry(note.id)">
              <i class="fa-solid fa-rotate-right"></i> 다시 풀어보기
            </button>
          </div>
        </div>

        <!-- Load more -->
        <div class="we-loadMoreRow">
          <button class="we-btn" @click="fetchWrongNotes(false)" :disabled="loading || !hasMore">
            <i class="fa-solid fa-rotate"></i>
            {{ loading ? '불러오는 중...' : (hasMore ? '더 보기' : '마지막입니다') }}
          </button>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import api from "@/utils/api.js";

const router = useRouter()
const go = (p) => router.push(p)

const activeTab = ref('ALL')
const searchQuery = ref('')

const wrongNotes = ref([]) // 서버 원본 리스트
const loading = ref(false)

const page = ref(0)
const size = ref(20)
const hasMore = ref(true)

// ====== (임시) Summary 값들 ======
const masteryRate = 68
const weaknessRate = 42
const weaknessPartText = 'RC Part 5'

// ====== Auth ======
function authHeadersOrRedirect() {
  const token = localStorage.getItem('token')
  if (!token) {
    alert('로그인이 필요합니다.')
    router.push('/login')
    return null
  }
  return { Authorization: `Bearer ${token}` }
}

// ====== Fetch ======
async function fetchWrongNotes(reset = false) {
  const headers = authHeadersOrRedirect()
  if (!headers) return

  if (reset) {
    page.value = 0
    wrongNotes.value = []
    hasMore.value = true
  }
  if (!hasMore.value) return

  loading.value = true

  try {
    if (activeTab.value === 'ALL') {
      // ALL은 서버가 path variable이라 RC + VOCA 합쳐서 처리
      const [rcRes, vocaRes] = await Promise.all([
        api.get(`/wrong-note/RC`, {params: {page: page.value, size: size.value}, headers}),
        api.get(`/wrong-note/VOCA`, {params: {page: page.value, size: size.value}, headers})
      ])

      const rcList = rcRes.data?.ok ?? []
      const vocaList = vocaRes.data?.ok ?? []
      const merged = [...rcList, ...vocaList]

      // 최신순 정렬(필요 없으면 삭제 가능)
      merged.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))

      wrongNotes.value.push(...merged)

      // ALL은 두 리스트 중 하나라도 꽉 차면 다음 페이지 있을 가능성
      hasMore.value = (rcList.length >= size.value) || (vocaList.length >= size.value)
    } else {
      const res = await api.get(`/wrong-note/${activeTab.value}`, {
        params: {page: page.value, size: size.value},
        headers
      })
      const list = res.data?.ok ?? []
      console.log(list)
      wrongNotes.value.push(...list)
      hasMore.value = list.length >= size.value
    }

    page.value += 1
  } catch (e) {
    console.error('오답노트 조회 실패', e)
    alert('오답노트 불러오기에 실패했습니다.')
  } finally {
    loading.value = false
  }
}

// ====== Normalize DTO -> UI friendly ======
const normalizedNotes = computed(() => {
  return (wrongNotes.value || []).map((n) => {
    const passage = n?.content?.passage ?? '' // ✅ 너 DTO 구조 반영
    const explanation =
        n?.content?.questions?.[0]?.explanation ??
        n?.explanation ??
        ''

    // correctAnswer가 "A" 같은 인덱스면 그대로 보여주고,
    // options 텍스트로 바꾸고 싶으면 서버에서 correctIndex 같이 주는 게 깔끔함.
    const correctAnswer =
        n?.correctAnswer ??
        n?.content?.questions?.[0]?.answer ??
        ''

    const solvedAt = n?.createdAt ? String(n.createdAt).slice(0, 10) : ''

    return {
      ...n,
      passage,
      explanation,
      correctAnswer,
      solvedAt,
      tags: Array.isArray(n?.tags) ? n.tags : [],
      memo: n?.memo ?? '' // 현재는 로컬용(서버 저장 연결 전)
    }
  })
})

// ====== Filter (tab + search) ======
const filteredNotes = computed(() => {
  const q = searchQuery.value.trim().toLowerCase()
  return normalizedNotes.value.filter((n) => {
    const tabMatch = activeTab.value === 'ALL' || n.category === activeTab.value
    if (!q) return tabMatch

    const passageMatch = (n.passage || '').toLowerCase().includes(q)
    const tagMatch = (n.tags || []).some((t) => String(t).toLowerCase().includes(q))

    return tabMatch && (passageMatch || tagMatch)
  })
})

function highlight(text) {
  return (text || '').replace(/-+/g, `<span class="we-blank">_______</span>`)
}

function retry(objectId) {
  // objectId로 다시 풀기 화면 이동
  console.log('Retry problem:', objectId)
  // 예: router.push(`/practice/rc?objectId=${objectId}`)
}

// (옵션) 삭제 버튼 — 아직 백엔드 DELETE API 없으니 일단 UI에서만 제거
async function removeNote(objectId) {
  if (!confirm('오답노트를 삭제하시겠습니까?')) return
  // TODO: 백엔드 DELETE 생기면 여기서 호출
  wrongNotes.value = wrongNotes.value.filter(n => n.id !== objectId)
}

onMounted(() => {
  fetchWrongNotes(true)
})

watch(activeTab, () => {
  fetchWrongNotes(true)
})
</script>

<style scoped>
@import "@/assets/workly-english.css";

.we-review {
  padding-bottom: 100px;
}

/* Summary Stats */
.we-summaryGrid {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr;
  gap: 20px;
  margin-bottom: 40px;
}

.we-summaryCard {
  background: white;
  padding: 30px;
  border-radius: 24px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
}

.we-summaryCard.is-main {
  background: #1e293b;
  color: white;
  border: none;
}

.we-summaryLabel {
  font-size: 14px;
  font-weight: 800;
  color: #94a3b8;
  margin-bottom: 10px;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.we-summaryValue {
  font-size: 40px;
  font-weight: 900;
  line-height: 1;
}

.we-summaryValue span {
  font-size: 18px;
  font-weight: 700;
  margin-left: 4px;
  color: #64748b;
}

.is-main .we-summaryValue {
  color: #3b82f6;
}

/* Filter Bar */
.we-filterRow {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  gap: 20px;
}

.we-tabGroup {
  display: flex;
  background: #e2e8f0;
  padding: 4px;
  border-radius: 12px;
}

.we-tabItem {
  padding: 8px 24px;
  border-radius: 10px;
  font-weight: 800;
  font-size: 14px;
  color: #64748b;
  transition: all 0.2s;
}

.we-tabItem.is-active {
  background: white;
  color: #1e293b;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.we-loading, .we-empty {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 18px;
  padding: 24px;
  color: #475569;
  display: flex;
  gap: 10px;
  align-items: center;
}

.we-empty {
  flex-direction: column;
  align-items: flex-start;
}

.we-emptyTitle {
  font-weight: 900;
  font-size: 18px;
  color: #0f172a;
}

.we-emptySub {
  color: #64748b;
  margin-bottom: 10px;
}

/* Note Card */
.we-noteCard {
  background: white;
  border-radius: 32px;
  border: 1px solid #e2e8f0;
  margin-bottom: 24px;
  overflow: hidden;
  transition: transform 0.2s;
}

.we-noteCard:hover {
  transform: translateY(-4px);
  border-color: #cbd5e1;
}

.we-noteCard__head {
  padding: 20px 30px;
  background: #f8fafc;
  border-bottom: 1px solid #e2e8f0;
  display: flex;
  justify-content: space-between;
}

.we-noteCard__body {
  padding: 30px;
}

.we-chipRow {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  align-items: center;
}

.we-chip {
  padding: 6px 10px;
  border-radius: 999px;
  font-weight: 900;
  font-size: 12px;
}

.we-chip.is-category {
  background: #e0f2fe;
  color: #0369a1;
}

.we-chip.is-level {
  background: #f1f5f9;
  color: #334155;
}

.we-chip.is-date {
  background: #ecfdf5;
  color: #047857;
}

.we-chip.is-part {
  background: #fff7ed;
  color: #9a3412;
}

.we-btnIcon {
  background: transparent;
  border: none;
  cursor: pointer;
  color: #94a3b8;
  font-size: 16px;
}

.we-btnIcon:hover {
  color: #ef4444;
}

.we-passageSnippet {
  font-size: 20px;
  font-weight: 700;
  color: #1e293b;
  line-height: 1.6;
  margin-bottom: 24px;
}

.we-blank {
  color: #ef4444;
  font-style: italic;
  text-decoration: underline;
}

.we-answerCompare {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 24px;
}

.we-ansBox {
  padding: 20px;
  border-radius: 16px;
}

.we-ansBox.is-wrong {
  background: #fef2f2;
  border: 1px solid #fee2e2;
}

.we-ansBox.is-correct {
  background: #ecfdf5;
  border: 1px solid #d1fae5;
}

.we-ansLabel {
  font-size: 11px;
  font-weight: 900;
  text-transform: uppercase;
  margin-bottom: 4px;
  color: #94a3b8;
}

.we-ansValue {
  font-size: 18px;
  font-weight: 900;
}

.is-wrong .we-ansValue {
  color: #b91c1c;
}

.is-correct .we-ansValue {
  color: #047857;
}

.we-explanationBox {
  background: #f1f5f9;
  padding: 20px;
  border-radius: 16px;
  margin-bottom: 24px;
}

.we-explainTitle {
  font-weight: 800;
  color: #475569;
  margin-bottom: 8px;
  font-size: 14px;
}

.we-explainText {
  color: #64748b;
  font-size: 15px;
  line-height: 1.6;
}

/* Sticky Memo Style */
.we-memoPad {
  background: #fffbeb;
  border: 1px dashed #f59e0b;
  padding: 20px;
  border-radius: 16px;
}

.we-memoTitle {
  font-size: 13px;
  font-weight: 900;
  color: #b45309;
  margin-bottom: 8px;
}

.we-memoInput {
  width: 100%;
  background: transparent;
  border: none;
  font-family: inherit;
  font-size: 15px;
  font-weight: 600;
  color: #92400e;
  outline: none;
  resize: none;
}

.we-noteCard__footer {
  padding: 20px 30px;
  border-top: 1px solid #f1f5f9;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.we-tags {
  display: flex;
  gap: 10px;
  color: #94a3b8;
  font-size: 13px;
  font-weight: 700;
  flex-wrap: wrap;
}

.we-loadMoreRow {
  display: flex;
  justify-content: center;
  margin-top: 18px;
  margin-bottom: 40px;
}

@media (max-width: 768px) {
  .we-summaryGrid {
    grid-template-columns: 1fr;
  }

  .we-answerCompare {
    grid-template-columns: 1fr;
  }

  .we-filterRow {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
