<template>
  <div class="skeleton-card" aria-hidden="true">
    <div class="line w-75 mb-2"></div>
    <div class="line w-50"></div>
  </div>
</template>

<style scoped>
.skeleton-card{
  height:64px;
  border-radius:var(--radius-lg);
  background:linear-gradient(90deg,#f2f4f7 25%,#e5e7eb 50%,#f2f4f7 75%);
  background-size:400% 100%;
  animation:pulse 1.4s ease infinite;
  margin-bottom:.75rem;
}
.line{height:12px;background:#fff3;border-radius:6px;}
@keyframes pulse{0%{background-position:100% 0}100%{background-position:-100% 0}}
</style>