// src/stores/user.js
import { defineStore } from 'pinia'
import api from '@/utils/api'

export const useUserStore = defineStore('user', {
    /* ---------------- state ---------------- */
    state: () => ({
        isLoggedIn: !!localStorage.getItem('token'),

        username:   localStorage.getItem('username') || '',

        notifications: JSON.parse(localStorage.getItem('notifications') || '[]'),

        loading: false,
    }),

    /* --------------- actions --------------- */
    actions: {
        /** ▣ /info 호출해서 세션 확인 */
        async fetchMe () {
            const token = localStorage.getItem('token')
            if (!token) return this.$reset()

            try {
                this.loading = true
                const { data } = await api.get('/info')   // axios interceptor가 토큰 붙임
                this.isLoggedIn = !!data?.isLoggedIn
                this.username   = data?.username || ''
            } catch (e) {
                // 401·네트워크 오류 → 세션 초기화
                this.$reset()
            } finally {
                this.loading = false
            }
        },

        /** ▣ 로그인 성공 시 호출 */
        setAuth (token, name) {
            localStorage.setItem('token', token)
            localStorage.setItem('username', name)
            this.isLoggedIn = true
            this.username   = name
        },

        /** ▣ 로그아웃 */
        logout () {
            localStorage.removeItem('token')
            localStorage.removeItem('username')
            this.$reset()
        },

        /** ▣ 알림 추가 */
        addNotification (n) {
            this.notifications.unshift(n)
            localStorage.setItem('notifications', JSON.stringify(this.notifications))
        },

        /** ▣ 알림 일괄 읽음 */
        markAllRead () {
            this.notifications.forEach(n => (n.read = true))
            localStorage.setItem('notifications', JSON.stringify(this.notifications))
        },

        /** ▣ 상태 초기화(내장) + 커스텀 필드 클리어 */
        $reset () {
            // Pinia option-store엔 기본 $reset이 있으므로
            // 필요한 값만 덮어씌워도 됩니다.
            this.isLoggedIn   = false
            this.username     = ''
            this.notifications = []
            this.loading      = false
        },
    },
})
