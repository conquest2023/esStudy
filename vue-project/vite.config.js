import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

export default defineConfig({
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // Spring Boot
        changeOrigin: true
      },
      '/jumpit': 'http://localhost:8080',
      '/programmers': 'http://localhost:8080',
      '/subscribe': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        selfHandleResponse: false,
      },
      '/info': {
        target: 'http://localhost:8080',
        changeOrigin: true
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
