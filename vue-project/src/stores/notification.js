import { defineStore } from 'pinia'
import api from '@/utils/api'

export const useNotificationStore = defineStore('notification', {
    state: () => ({
        notifications: [],
    }),
    actions: {
        async fetchNotifications() {
            const { data } = await api.get('/notifications/recent')
            this.notifications = data
        }
    }
})
