import { onBeforeUnmount } from 'vue'
import { useToast } from './useToast'
import { useUserStore } from '@/stores/user'
import { addFeedNotification } from '@/utils/notification'
import { showToast } from '@/utils/showToast'
export function useSSE(token) {
    if (!token) return
    const { push } = useToast()
    const store = useUserStore()

    const es = new EventSource(`/api/subscribe?token=${encodeURIComponent(token)}`)

    const handleNotification = (e, emoji) => {
        let parsed
        try { parsed = JSON.parse(e.data) }
        catch { return console.error('알림 JSON 파싱 실패', e.data) }

        store.addNotification({
            id: Date.now(),
            feedUID:  parsed.feedUID,
            message:  parsed.message,
            read:     false
        })
        addFeedNotification(parsed, store, showToast)
        // addFeedNotification(parsed, store.notifications, (msg, id) => {
        //     push(`${emoji} ${msg}`)
        // })
    }

    es.onopen = () => {
        console.log('[SSE] 연결 성공')
    }

    es.onerror = () => {
        console.warn('[SSE] 오류 발생 – 20초 뒤 재연결 시도')
        es.close()
        setTimeout(() => useSSE(token), 30000)
    }

    // 다양한 타입의 알림
    es.addEventListener('comment-notification', e => handleNotification(e, '💬'))
    es.addEventListener('todo-notification', e => handleNotification(e, '📝'))
    es.addEventListener('reply-notification', e => handleNotification(e, '↩️'))
    es.addEventListener('notice-notification', e => handleNotification(e, '📢'))
    es.addEventListener('point-notification', e => handleNotification(e, '📢'))

    onBeforeUnmount(() => {
        console.log('[SSE] 연결 종료')
        es.close()
    })
}
