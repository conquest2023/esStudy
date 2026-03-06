import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import { VitePWA } from 'vite-plugin-pwa' // 1. PWA 플러그인 임포트

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
    // 2. PWA 설정 추가
    VitePWA({
      registerType: 'autoUpdate',
      includeAssets: ['favicon.ico', 'apple-touch-icon.png', 'mask-icon.svg'],
      manifest: {
        name: 'Workly',
        short_name: 'Workly',
        description: '커리어 플랫폼',
        theme_color: '#82b1ff',
        icons: [
          {
            src: 'pwa-192x192.png', // 제가 만든 192 파일
            sizes: '192x192',
            type: 'image/png'
          },
          {
            src: 'pwa-512x512.png', // 제가 만든 512 파일
            sizes: '512x512',
            type: 'image/png'
          }
        ]
      },
      devOptions: {
        enabled: true // 개발 모드에서도 PWA 동작 확인 가능하게 설정
      }
    })
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

  optimizeDeps: {
    // include: [], exclude: []
  }
})