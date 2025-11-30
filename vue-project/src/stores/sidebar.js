// src/stores/sidebar.js
import { defineStore } from 'pinia'
import api from '@/utils/api'

export const useSidebarStore = defineStore('sidebar', {
    state: () => ({
        dDayList: [],
        todoList: [],

        remaining: 0,

        visitorStats: { active: 0, today: 0, total: 0 },
        topWriters: [],
        topRecentWriters: [],
        _staticLoaded: false,
        _staticInFlight: null,

        _liveLoaded: false,      // ← 추가
        _liveInFlight: null,
    }),

    getters: {
        todoProgress (state) {
            const done = state.todoList.length - state.remaining
            return state.todoList.length
                ? Math.round((done / state.todoList.length) * 100)
                : 0
        }
    },

    actions: {
        async loadStatic() {
            if (this._staticLoaded) return
            if (this._staticInFlight) return this._staticInFlight

            this._staticInFlight = (async () => {
                try {
                    const [dayRes, todoRes] = await Promise.all([
                        api.get('/day'),
                        api.get('/search/today/todo')
                    ])
                    this.dDayList = dayRes.data.D_Day ?? []
                    this.todoList = todoRes.data.todos ?? []
                    this.remaining = todoRes.data.remainingCount ?? 0
                    this._staticLoaded = true
                } catch (err) {
                    console.error('[sidebar] static 로딩 실패', err)
                } finally {
                    this._staticInFlight = null
                }
            })()

            return this._staticInFlight
        },

        async loadLive() {
            if (this._liveLoaded) return
            if (this._liveInFlight) return this._liveInFlight

            this._liveLoaded = true

            this._liveInFlight = (async () => {
                try {
                    const [ipRes, writerRes,recentRes] = await Promise.all([
                        api.get('/get-ip'),
                        api.get('/points/summary'),
                        api.get('/points/recent')
                    ])
                    console.log(ipRes)
                    this.visitorStats = {
                        active: ipRes.data.activeUsers,
                        today: ipRes.data.today,
                        total: ipRes.data.data.totalVisitors
                    }
                    this.topWriters = writerRes.data.top5 ?? []
                    this.topRecentWriters = recentRes.data.recent ??[]
                } catch (e) {
                    console.error('[sidebar] live 로딩 실패', e)
                    this._liveLoaded = false
                } finally {
                    this._liveInFlight = null
                }
            })()

            return this._liveInFlight
        }
    }

})
