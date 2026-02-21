<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'

const props = defineProps({
  navItems: { type: Array, default: () => [] }
})

const emit = defineEmits(['nav-click', 'fab'])
const isMobile = ref(false)

// 바텀 시트(Bottom Sheet) 상태 관리
const showSheet = ref(false)
const currentChildren = ref([])
const sheetTitle = ref('')

const setIsMobile = () => (isMobile.value = window.innerWidth <= 900)

onMounted(() => {
  setIsMobile()
  window.addEventListener('resize', setIsMobile)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', setIsMobile)
})

const router = useRouter()
const isExternal = (path) => /^https?:\/\//i.test(String(path || '').trim())

// 메뉴 클릭 핸들러
function onClickItem(item) {
  // 1. 하위 메뉴(children)가 있으면 바텀 시트를 엽니다.
  if (item.children && item.children.length > 0) {
    currentChildren.value = item.children
    sheetTitle.value = item.label
    showSheet.value = true
    return
  }

  // 2. 단일 메뉴면 바로 이동
  if (!item?.path) return
  const path = String(item.path).trim()

  if (isExternal(path) || item?.isExternal) {
    window.open(path, '_blank', 'noopener,noreferrer')
    return
  }
  emit('nav-click', item)
}

// 바텀 시트 내부 하위 메뉴 클릭 핸들러
function onClickChild(child) {
  showSheet.value = false // 클릭 시 시트 닫기

  if (!child?.path) return
  const path = String(child.path).trim()

  if (isExternal(path) || child?.isExternal) {
    window.open(path, '_blank', 'noopener,noreferrer')
    return
  }
  emit('nav-click', child)
}

// 바텀 시트 닫기
function closeSheet() {
  showSheet.value = false
}
</script>

<template>
  <nav v-if="isMobile" class="modern-bottom-nav">
    <div class="nav-container">
      <template v-for="n in navItems" :key="n.label">
        <a
            href="#"
            class="nav-item flex-fill text-center"
            @click.prevent="onClickItem(n)"
        >
          <div class="icon-wrapper">
            <i :class="n.icon"></i>
          </div>
          <span class="nav-label">{{ n.label }}</span>
        </a>
      </template>
    </div>

    <div class="fab-wrapper">
      <button class="fab-btn" @click="$emit('fab')">
        <i class="fas fa-pen"></i>
      </button>
    </div>
  </nav>

  <Teleport to="body">
    <Transition name="fade">
      <div
          v-if="showSheet && isMobile"
          class="sheet-backdrop"
          @click="closeSheet"
      ></div>
    </Transition>

    <Transition name="slide-up">
      <div v-if="showSheet && isMobile" class="bottom-sheet shadow-lg">
        <div class="sheet-handle-wrap" @click="closeSheet">
          <div class="sheet-handle"></div>
        </div>

        <div class="sheet-header">
          <h6 class="sheet-title">{{ sheetTitle }}</h6>
        </div>

        <ul class="sheet-list">
          <li
              v-for="c in currentChildren"
              :key="c.path"
              class="sheet-item"
              @click="onClickChild(c)"
          >
            <span class="child-label">{{ c.label }}</span>
            <i class="fas fa-chevron-right text-muted small"></i>
          </li>
        </ul>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
/* ===============================
   1. 바텀 네비게이션 스타일
================================= */
.modern-bottom-nav {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border-top: 1px solid rgba(0, 0, 0, 0.05);
  z-index: 1500;
  padding-bottom: env(safe-area-inset-bottom, 0px);
}

.nav-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  height: 60px;
  padding: 0 10px;
}

.nav-item {
  color: #94a3b8;
  text-decoration: none;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 6px 0;
  transition: color 0.2s ease;
}

.nav-item:hover, .nav-item:active {
  color: #0f172a;
}

.icon-wrapper i {
  font-size: 1.25rem;
  margin-bottom: 4px;
  transition: transform 0.2s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.nav-item:active .icon-wrapper i {
  transform: scale(0.85);
}

.nav-label {
  font-size: 0.7rem;
  font-weight: 600;
  letter-spacing: -0.3px;
}

/* 중앙 플로팅 버튼 */
.fab-wrapper {
  position: absolute;
  top: -24px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 1600;
}

.fab-btn {
  width: 54px;
  height: 54px;
  border-radius: 50%;
  border: none;
  color: #fff;
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.3rem;
  box-shadow: 0 8px 20px rgba(37, 99, 235, 0.4);
  transition: all 0.25s cubic-bezier(0.34, 1.56, 0.64, 1);
  cursor: pointer;
}

.fab-btn:active {
  transform: scale(0.9);
  box-shadow: 0 4px 10px rgba(37, 99, 235, 0.3);
}

/* ===============================
   2. 바텀 시트(Bottom Sheet) 스타일
================================= */
.sheet-backdrop {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  z-index: 2000;
  backdrop-filter: blur(2px);
}

.bottom-sheet {
  position: fixed;
  bottom: 0; left: 0; right: 0;
  background: #ffffff;
  border-radius: 24px 24px 0 0;
  z-index: 2010;
  padding-bottom: calc(20px + env(safe-area-inset-bottom, 0px));
  box-shadow: 0 -4px 20px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
}

.sheet-handle-wrap {
  width: 100%;
  padding: 12px 0;
  cursor: pointer;
  display: flex;
  justify-content: center;
}

.sheet-handle {
  width: 40px;
  height: 5px;
  background: #cbd5e1;
  border-radius: 4px;
}

.sheet-header {
  padding: 0 24px 12px;
  text-align: left;
}

.sheet-title {
  font-size: 1.1rem;
  font-weight: 700;
  color: #0f172a;
  margin: 0;
}

.sheet-list {
  list-style: none;
  margin: 0;
  padding: 0 12px;
}

.sheet-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 12px;
  border-radius: 12px;
  cursor: pointer;
  transition: background 0.2s ease;
}

.sheet-item:active {
  background: #f1f5f9;
}

.child-label {
  font-size: 1rem;
  font-weight: 600;
  color: #334155;
}

/* ===============================
   3. 트랜지션 애니메이션 (Vue Transition)
================================= */
/* 딤 처리(배경) 애니메이션 */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}

/* 바텀 시트 슬라이드 애니메이션 (쫀득한 cubic-bezier) */
.slide-up-enter-active, .slide-up-leave-active {
  transition: transform 0.35s cubic-bezier(0.2, 0.8, 0.2, 1);
}
.slide-up-enter-from, .slide-up-leave-to {
  transform: translateY(100%);
}
</style>