import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'


export default defineConfig({
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
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

  build: {
    target: 'es2020',
    cssCodeSplit: true,
    assetsInlineLimit: 0,
    modulePreload: { polyfill: false },

    rollupOptions: {
      output: {
        manualChunks(id) {
          if (id.includes('node_modules')) return 'vendor'

          // 폴더 기준 라우트 묶음 (필요에 따라 수정)
          if (id.includes('/pages/feed/'))        return 'feed'
          if (id.includes('/pages/site/'))        return 'site'
          if (id.includes('/pages/todo/'))        return 'todo'
          if (id.includes('/pages/certificate/')) return 'certificate'
          if (id.includes('/pages/interview/'))   return 'interview'
          if (id.includes('/pages/auth/'))        return 'auth'

          return undefined
        }
      }
    }
  },

  // (선택) 사전 번들링 최적화: 개발 체감 개선
  optimizeDeps: {
    // include: [], exclude: []
  }
})