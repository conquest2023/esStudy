import { fileURLToPath, URL } from 'node:url'
import { defineConfig, splitVendorChunkPlugin } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import purgeCss from 'vite-plugin-purgecss'   // ★ 추가

export default defineConfig({
  server: {
    proxy: {
      '/api': { target: 'http://localhost:8080', changeOrigin: true },
      '/jumpit': 'http://localhost:8080',
      '/programmers': 'http://localhost:8080',
      '/subscribe': { target: 'http://localhost:8080', changeOrigin: true, selfHandleResponse: false },
      '/info': { target: 'http://localhost:8080', changeOrigin: true }
    }
  },

  plugins: [
    vue(),
    vueDevTools(),
    splitVendorChunkPlugin(),   // ★ 벤더 분리 보조
    purgeCss({                  // ★ 사용 안 되는 CSS 제거(bootstrap/FA 덩치 컷)
      content: [
        './index.html',
        './src/**/*.vue',
        './src/**/*.ts',
        './src/**/*.js'
      ],
      safelist: [/^fa-/, /^bi-/, /^btn/, /^col-/, /^row/], // 쓰는 유틸은 필요 시 화이트리스트
    }),
    // (선택) 브로틀리/지집 산출물
    // viteCompression({ algorithm: 'brotliCompress' }),
    // viteCompression({ algorithm: 'gzip' })
  ],

  resolve: {
    alias: { '@': fileURLToPath(new URL('./src', import.meta.url)) },
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
})
