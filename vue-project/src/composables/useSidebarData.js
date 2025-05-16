// src/composables/useSidebarData.js
import { ref, computed } from 'vue'
import api from '@/utils/api'

const dDayList      = ref([])
const todoList      = ref([])
const remaining     = ref(0)
const visitorStats  = ref({ active: 0, today: 0, total: 0 })
const topWriters    = ref([])
const calendarEvents= ref([])

const todoProgress = computed(() => {
    const done = todoList.value.length - remaining.value
    return todoList.value.length
        ? Math.round((done / todoList.value.length) * 100)
        : 0
})

let fetched = false


async function fetchSidebarData() {
    if (!fetched) {
        fetched = true

        try {
            const [dayRes, todoRes] = await Promise.allSettled([
                api.get('/day'),
                api.get('/search/today/todo')
            ])

            if (dayRes.status === 'fulfilled') {
                dDayList.value = dayRes.value.data.D_Day ?? []
            }

            if (todoRes.status === 'fulfilled') {
                todoList.value = todoRes.value.data.todos ?? []
                remaining.value = todoRes.value.data.remainingCount ?? 0
            }
        } catch (e) {
            console.error('캐싱 요청 실패', e)
        }
    }

    try {
        const [visitorRes, writerRes]
            = await Promise.allSettled([
            api.get('/get-ip'),
            api.get('/top-writers')
        ])

        if (visitorRes.status === 'fulfilled') {
            visitorStats.value = {
                active: visitorRes.value.data.activeUsers,
                today: visitorRes.value.data.data.todayVisitors,
                total: visitorRes.value.data.data.totalVisitors
            }
        }

        if (writerRes.status === 'fulfilled') {
            topWriters.value = writerRes.value.data ?? []
        }
    } catch (e) {
        console.error('반드시 갱신될 요청 실패', e)
    }
}

export function useSidebarData () {
    fetchSidebarData()

    return {
        dDayList,
        todoList,
        remaining,
        visitorStats,
        topWriters,
        calendarEvents,
        todoProgress,
        fetchSidebarData
    }
}
