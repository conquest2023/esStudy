import { createApp } from 'vue'
import { createPinia } from 'pinia'
import "@/assets/workly-english.css";
import { registerSW } from 'virtual:pwa-register'
import router from '@/router'
import App from '@/App.vue'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap-icons/font/bootstrap-icons.css'
import 'bootstrap/dist/js/bootstrap.bundle.min.js'
import uiEnhancer from './plugins/uiEnhancer.js'


registerSW({
    immediate: true,
    onNeedRefresh() {
        console.log('새로운 콘텐츠가 있습니다. 새로고침하세요.')
    },
    onOfflineReady() {
        console.log('앱이 오프라인에서 실행될 준비가 되었습니다.')
    },
})
createApp(App)
    .use(createPinia())
    .use(uiEnhancer)
    .use(router)
    .mount('#app')
