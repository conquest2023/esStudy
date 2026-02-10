<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { RouterLink, useRouter } from 'vue-router'

const props = defineProps({
  navItems: { type: Array, default: () => [] }
})

const emit = defineEmits(['nav-click', 'fab'])

const isMobile = ref(false)

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

function onClickItem(item) {
  if (!item?.path) return

  const path = String(item.path).trim()

  if (isExternal(path) || item?.isExternal) {
    window.open(path, '_blank', 'noopener,noreferrer')
    return
  }

  emit('nav-click', item)
}

function onClickChild(child) {
  if (!child?.path) return
  const path = String(child.path).trim()

  if (isExternal(path) || child?.isExternal) {
    window.open(path, '_blank', 'noopener,noreferrer')
    return
  }

  emit('nav-click', child)
}
</script>

<template>
  <nav v-if="isMobile" class="mobile-bottom-nav shadow-sm">
    <template v-for="n in navItems" :key="n.label">
      <!-- ✅ children 없는 경우: RouterLink 유지해도 되지만,
           외부 링크도 있을 수 있으니 click 핸들링으로 통일 -->
      <a
          v-if="!n.children || n.children.length === 0"
          href="#"
          class="nav-link text-center flex-fill"
          @click.prevent="onClickItem(n)"
      >
        <i :class="n.icon"></i>
        <span class="small d-block">{{ n.label }}</span>
      </a>

      <!-- children 있는 경우 -->
      <div v-else class="dropdown dropup flex-fill text-center">
        <a class="nav-link dropdown-toggle" href="#" data-bs-toggle="dropdown">
          <i :class="n.icon"></i>
          <span class="small d-block">{{ n.label }}</span>
        </a>

        <ul class="dropdown-menu text-center">
          <li
              v-for="c in n.children"
              :key="c.path"
              class="dropdown-item fw-bold"
              @click="onClickChild(c)"
          >
            {{ c.label }}
          </li>
        </ul>
      </div>
    </template>

    <button class="fab-button" @click="$emit('fab')">
      <i class="fas fa-pen"></i>
    </button>
  </nav>
</template>

<style>
.mobile-bottom-nav {
  position: fixed;
  bottom: 0; left: 0; right: 0;
  background: #fff;
  border-top: 1px solid #ddd;
  display: flex;
  justify-content: space-around;
  align-items: center;
  padding: 8px 0;
  z-index: 1500;
  box-shadow: 0 -1px 4px rgba(0, 0, 0, 0.05);
}

.mobile-bottom-nav .nav-link {
  color: #555;
  font-size: 14px;
  text-decoration: none;
  padding: 6px 0;
  transition: background-color 0.2s ease, color 0.2s ease;
}
.mobile-bottom-nav .nav-link:hover {
  background-color: #f8f8f8;
  color: #000;
  border-radius: 6px;
}

.mobile-bottom-nav .dropdown-menu {
  font-size: 14px;
  min-width: 140px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  border-radius: 8px;
}
.mobile-bottom-nav .dropdown-item {
  padding: 10px 14px;
}
.mobile-bottom-nav .dropdown-item:hover {
  background-color: #f0f0f0;
  color: #000;
}

.fab-button {
  position: absolute;
  top: -22px;
  left: 50%;
  transform: translateX(-50%);
  width: 56px;
  height: 56px;
  border-radius: 50%;
  border: none;
  color: #fff;
  background: linear-gradient(145deg, #3b82f6, #2563eb);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
  z-index: 1600;
}

body {
  padding-bottom: 64px;
}
</style>
