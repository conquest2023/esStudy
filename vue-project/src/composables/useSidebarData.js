// src/composables/useSidebarData.js
import { ref, computed, onMounted } from 'vue'
import api from '@/utils/api'

export function useSidebarData () {
    /* ── state ───────────────────────── */
    const dDayList      = ref([])
    const todoList      = ref([])
    const remaining     = ref(0)
    const visitorStats  = ref({ active: 0, today: 0, total: 0 })
    const topWriters    = ref([])
    const calendarEvents= ref([])

    /* ── derived ─────────────────────── */
    const todoProgress = computed(() => {
        const done = todoList.value.length - remaining.value
        return todoList.value.length
            ? Math.round((done / todoList.value.length) * 100)
            : 0
    })

    /* ── api call ─────────────────────── */
    async function fetchSidebarData () {
        try {
            const [dayRes, todoRes, visitorRes, writerRes] = await Promise.allSettled([
                api.get('/day'),
                api.get('/search/today/todo'),
                api.get('/get-ip'),
                api.get('/top-writers')
            ])

            if (dayRes.status === 'fulfilled') {
                dDayList.value = dayRes.value.data.D_Day ?? []
            }

            if (todoRes.status === 'fulfilled') {
                todoList.value = todoRes.value.data.todos ?? []
                remaining.value = todoRes.value.data.remainingCount ?? 0
            }

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
            console.error('Sidebar fetch error', e)
        }
    }

        /* ── lifecycle ────────────────────── */
    onMounted(fetchSidebarData)      // ⬅️ 이제 정상!

    /* ── expose ───────────────────────── */
    return {
        // state
        dDayList,
        todoList,
        remaining,
        visitorStats,
        topWriters,
        calendarEvents,
        // derived
        todoProgress,
        // methods
        fetchSidebarData
    }
}
