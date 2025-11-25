import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// (선택) 브로틀리/지집 압축 산출물 생성해서 CDN/Nginx가 서빙하도록 하고 싶으면 주석 해제
// import viteCompression from 'vite-plugin-compression'

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
    // (선택) 압축 산출물 생성
    // viteCompression({ algorithm: 'brotliCompress' }),
    // viteCompression({ algorithm: 'gzip' })
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
        // 라우트/도메인 기준으로 청크 분할 → 초기 번들 최소화 + 캐시 효율 ↑
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
