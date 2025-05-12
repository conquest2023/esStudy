import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

export default defineConfig({
  server: {
    proxy: {
      '/jumpit': 'http://localhost:8080',
      '/programmers': 'http://localhost:8080',
      '/subscribe': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        selfHandleResponse: false,
      }
    }

    },
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
})
