import { onBeforeUnmount } from 'vue'
import { useToast } from './useToast'
import { useUserStore } from '@/stores/user'
export function useSSE(token) {
    if (!token) {
        console.warn('[SSE] 토큰이 없어 연결을 시도하지 않습니다.')
        return
    }

    const { push } = useToast()
    const store = useUserStore()


    const es = new EventSource(`/api/subscribe?token=${encodeURIComponent(token)}`)

    const handleNotification = (e, emoji) => {
        console.log(`[SSE] ${emoji} 이벤트 수신 성공!`, e.type, e.data);
        let parsed

        try {
            parsed = JSON.parse(e.data)
        }
        catch (error) {
            return console.error('[SSE] 알림 JSON 파싱 실패:', e.data, error)
        }


        const newNotification = store.addNotification(parsed)

        const beautifulMessage = `${emoji} ${newNotification.message}`
        const postPath = `/post/${newNotification.postId}`
        push(beautifulMessage, postPath);
    }


    es.onopen = () => {
        console.log('[SSE] 연결 성공 및 스트리밍 시작')
    }

    es.onerror = (error) => {
        console.warn('[SSE] ❌ 오류 발생 또는 연결 끊김. 20초 뒤 재연결 시도', error)
        es.close()

        setTimeout(() => useSSE(token), 20000)
    }


    es.addEventListener('comment-notification', e => {
        console.log('[SSE raw] comment-notification', e.data)
        handleNotification(e, '💬')
    })
    es.addEventListener('todo-notification', e => handleNotification(e, '📝'))
    es.addEventListener('reply-notification', e => handleNotification(e, '↩️'))
    es.addEventListener('notice-notification', e => handleNotification(e, '📢'))
    es.addEventListener('point-notification', e => handleNotification(e, '💰'))
    es.addEventListener('like-notification', e => handleNotification(e, '💗'))

    onBeforeUnmount(() => {
        console.log('[SSE] 🚪 컴포넌트 언마운트 시 연결 종료')
        es.close()
    })
}
