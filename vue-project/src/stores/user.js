import { defineStore } from 'pinia'
export const useUserStore = defineStore('user', {
    state: () => ({
        token: localStorage.getItem('token') ?? '',
        username: localStorage.getItem('username') ?? ''
    }),
    actions: {
        setAuth(t, name) {
            this.token = t
            this.username = name
            localStorage.setItem('token', t)
            localStorage.setItem('username', name)
        },
        logout() {
            this.token = ''
            localStorage.clear()
        }
    }
})
