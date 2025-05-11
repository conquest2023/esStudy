<template>
  <nav class="mobile-bottom-nav shadow-sm">
    <template v-for="n in navItems" :key="n.path">
      <RouterLink
          v-if="!n.children"
          :to="n.path"
          class="nav-link text-center flex-fill"
      >
        <i :class="n.icon"></i>
        <span class="small d-block">{{ n.label }}</span>
      </RouterLink>

      <!-- 드롭업 메뉴가 필요한 항목 -->
      <div v-else class="dropdown dropup flex-fill text-center">
        <a
            class="nav-link dropdown-toggle"
            href="#"
            data-bs-toggle="dropdown"
            role="button"
        >
          <i :class="n.icon"></i>
          <span class="small d-block">{{ n.label }}</span>
        </a>
        <ul class="dropdown-menu text-center">
          <li
              v-for="c in n.children"
              :key="c.path"
              class="dropdown-item fw-bold"
              @click="$router.push(c.path)"
          >
            {{ c.label }}
          </li>
        </ul>
      </div>
    </template>

    <!-- 중앙 FAB -->
    <button class="fab-button" @click="$emit('fab')">
      <i class="fas fa-pen"></i>
    </button>
  </nav>
</template>

<script setup>
import { RouterLink, useRouter } from 'vue-router'

defineProps({
  /*
    navItems: [
      { path: '/', icon: 'fas fa-home fa-lg', label: '홈' },
      { icon: 'fas fa-briefcase fa-lg', label: '취업', children:[{path:'/site',label:'사이트'}] },
      …
    ]
  */
  navItems: { type: Array, default: () => [] }
})

const router = useRouter()
</script>

<style scoped>
.mobile-bottom-nav {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #ffffff;
  border-top: 1px solid #ddd;
  display: flex;
  justify-content: space-around;
  align-items: center;
  padding: 6px 0;
  z-index: 1000;
}
.mobile-bottom-nav .nav-link {
  color: #555;
  font-size: 15px;
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
}
</style>
