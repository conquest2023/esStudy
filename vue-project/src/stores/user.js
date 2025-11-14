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
        async fetchMe () {
            const token = localStorage.getItem('token')
            if (!token) return this.$reset()
            try {
                this.loading = true
                const { data } = await api.get('/info')   // axios interceptor가 토큰 붙임
                this.isLoggedIn = !!data?.isLoggedIn
                this.username   = data?.username || ''
            } catch (e) {
                this.$reset()
            } finally {
                this.loading = false
            }
        },

        setAuth (token, name) {
            localStorage.setItem('token', token)
            localStorage.setItem('username', name)
            this.isLoggedIn = true
            this.username   = name
        },

        logout () {
            localStorage.removeItem('token')
            localStorage.removeItem('username')
            this.$reset()
        },

        // /** ▣ 알림 추가 */
        // addNotification (n) {
        //     // Vue가 반응형으로 감지할 수 있도록 새 배열로 할당
        //     this.notifications = [n, ...this.notifications]
        //     localStorage.setItem('notifications', JSON.stringify(this.notifications))
        // },
        //

        /** ▣ 알림 일괄 읽음 */
        markAllRead () {
            this.notifications.forEach(n => (n.read = true))
            localStorage.setItem('notifications', JSON.stringify(this.notifications))
        },

        $reset () {
            this.isLoggedIn   = false
            this.username     = ''
            this.notifications = []
            this.loading      = false
        },
    },
})
