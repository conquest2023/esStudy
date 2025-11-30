<!--&lt;!&ndash; ToastNotification.vue &ndash;&gt;-->
<!--<template>-->
<!--  <div v-if="visible" class="toast-container position-fixed top-0 end-0 p-3" style="z-index: 1055;">-->
<!--    <div class="toast show text-white bg-success shadow">-->
<!--      <div class="d-flex align-items-center p-2">-->
<!--        <i class="fas fa-check-circle me-2" />-->
<!--        <div class="me-auto">{{ message }}</div>-->
<!--        <button type="button" class="btn-close btn-close-white ms-2" @click="visible = false"></button>-->
<!--      </div>-->
<!--    </div>-->
<!--  </div>-->
<!--</template>-->

<!--<script setup>-->
<!--import { ref, watch } from 'vue'-->

<!--const props = defineProps({ message: String, show: Boolean })-->
<!--const visible = ref(false)-->

<!--watch(() => props.show, val => {-->
<!--  if (val) {-->
<!--    visible.value = true-->
<!--    setTimeout(() => visible.value = false, 2000)-->
<!--  }-->
<!--})-->
<!--</script>-->
