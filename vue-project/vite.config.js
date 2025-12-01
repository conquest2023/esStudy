// vite.config.js
import { fileURLToPath, URL } from 'node:url'
import { defineConfig, splitVendorChunkPlugin } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import purgeCss from 'vite-plugin-purgecss'   // ★ 사용 안 하는 CSS 제거

// (선택) 빌드 산출물 압축 원하면 주석 해제
// import viteCompression from 'vite-plugin-compression'

export default defineConfig({
  server: {
    proxy: {
      '/api':        { target: 'http://localhost:8080', changeOrigin: true },
      '/jumpit':     'http://localhost:8080',
      '/programmers':'http://localhost:8080',
      '/subscribe':  { target: 'http://localhost:8080', changeOrigin: true, selfHandleResponse: false },
      '/info':       { target: 'http://localhost:8080', changeOrigin: true },
    }
  },

  plugins: [
    vue(),
    vueDevTools(),
    splitVendorChunkPlugin(),   // ★ 벤더 분할 보조로 초기 청크 축소
    purgeCss({
      // 실제 템플릿/컴포넌트 경로 넣기
      content: [
        './index.html',
        './src/**/*.vue',
        './src/**/*.js',
        './src/**/*.ts',
      ],
      // 부트스트랩/아이콘 등 꼭 남겨야 하는 유틸 클래스는 safelist
      safelist: [
        /^fa-/,          // font-awesome 쓰면 유지
        /^bi-/,          // bootstrap-icons 쓰면 유지
        /^btn/, /^col-/, /^row/, /^container/, /^d-/, /^flex/,
        /^show$/, /^collapse$/, // BS 컴포넌트 토글 클래스 등
      ],
    }),

    // (선택) 브로틀리/지집 산출물 생성 — Nginx/Cloudflare가 서빙하면 효과 큼
    // viteCompression({ algorithm: 'brotliCompress' }),
    // viteCompression({ algorithm: 'gzip' }),
  ],

  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },

  build: {
    target: 'es2020',
    cssCodeSplit: true,           // 라우트별 CSS 분할
    assetsInlineLimit: 0,
    modulePreload: { polyfill: false },

    rollupOptions: {
      output: {
        // 도메인/페이지 기준으로 청크 쪼개기 → 초기 경로 가벼워짐
        manualChunks(id) {
          if (id.includes('node_modules')) return 'vendor'

          // 페이지 폴더 기준 분리 (필요 없으면 줄여도 됨)
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

  // (선택) 개발 체감 튜닝
  optimizeDeps: {
    // include: [],
    // exclude: [],
  }
})
