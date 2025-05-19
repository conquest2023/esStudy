import { createApp } from 'vue'
import { createPinia } from 'pinia'
import router from '@/router'
import App from '@/App.vue'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap-icons/font/bootstrap-icons.css'
import 'bootstrap/dist/js/bootstrap.bundle.min.js'
import uiEnhancer from './plugins/uiEnhancer.js'

createApp(App)
    .use(createPinia())
    .use(uiEnhancer)
    .use(router)
    .mount('#app')
