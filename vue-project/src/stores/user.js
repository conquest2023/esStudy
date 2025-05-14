import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
    state: () => ({
        token:       localStorage.getItem('token')       ?? '',
        username:    localStorage.getItem('username')    ?? '',
        notifications: JSON.parse(localStorage.getItem('notifications') ?? '[]')
    }),

    actions: {
        setAuth(t, name) {
            this.token    = t
            this.username = name
            localStorage.setItem('token',    t)
            localStorage.setItem('username', name)
        },

        logout() {
            this.token = ''
            localStorage.clear()
        },

        addNotification(n) {
            this.notifications.unshift(n)
            localStorage.setItem('notifications', JSON.stringify(this.notifications))
        },
        markAllRead() {
            this.notifications.forEach(n => (n.read = true))
            localStorage.setItem('notifications', JSON.stringify(this.notifications))
        }
    }
})
