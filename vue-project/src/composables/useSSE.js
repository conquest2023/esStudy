import { onBeforeUnmount } from 'vue'
import { useToast } from '@/composables/useToast'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'

export function useSSE(token) {
    if (!token) {
        console.warn('[SSE] 토큰이 없어 연결을 시도하지 않습니다.')
        return
    }

    const router = useRouter()
    const { push } = useToast()
    const store = useUserStore()

    const es = new EventSource(`/api/subscribe?token=${encodeURIComponent(token)}`)

    const handleRankTop1 = (e) => {
        let parsed
        try { parsed = JSON.parse(e.data) } catch { return }
        const msg = `👑 ${parsed.message || '랭킹 1위 게시글'}`
        if (parsed.postId) {
            push(msg, `/post/${parsed.postId}`)
        } else {
            push(msg, '/trending/top3')
        }
    }
    const handleRankTop3 = (e) => {
        let parsed
        try { parsed = JSON.parse(e.data) }
        catch {
            return
        }
        const posts = Array.isArray(parsed.posts) ? parsed.posts.slice(0, 3) : []
        if (posts.length === 0) return
        push({
            type: 'top3',
            title: '오늘의 베스트 Top 3',
            message: parsed.message || '업데이트 되었습니다!',
            posts,                   // [{postId,title,username}]
            duration: 7000,
            onClick: () => router.push('/trending/top3'),
            onItemClick: (p) => router.push(`/post/${p.postId}`),
        })
    }

    const handleNotification = (e, emoji) => {
        let parsed
        try { parsed = JSON.parse(e.data) }
        catch (error) {
            return console.error('[SSE] 알림 JSON 파싱 실패:', e.data, error)
        }

        // 스토어에 기록(배지/알림함)
        const n = store.addNotification(parsed)

        const msg = `${emoji} ${n.message ?? '새 알림이 도착했습니다'}`
        // 일반 알림은 단건 post 이동이 대부분
        if (n.postId) {
            push(msg, `/post/${n.postId}`)
        } else {
            // postId 없으면 알림함으로
            push(msg, '/notifications')
        }
    }

    es.onopen = () => {
        console.log('[SSE] 연결 성공 및 스트리밍 시작')
    }

    es.onerror = (error) => {
        console.warn('[SSE] ❌ 오류 발생 또는 연결 끊김. 20초 뒤 재연결 시도', error)
        es.close()
        setTimeout(() => useSSE(token), 20000)
    }

    // 이벤트 바인딩
    es.addEventListener('comment-notification', e => handleNotification(e, '💬'))
    es.addEventListener('todo-notification',    e => handleNotification(e, '📝'))
    es.addEventListener('reply-notification',   e => handleNotification(e, '↩️'))
    es.addEventListener('notice-notification',  e => handleNotification(e, '📢'))
    es.addEventListener('point-notification',   e => handleNotification(e, '💰'))
    es.addEventListener('like-notification',    e => handleNotification(e, '💗'))

    es.addEventListener('rank-top1-notification', e => handleRankTop1(e))
    es.addEventListener('rank-top3-notification', e => handleRankTop3(e))

    onBeforeUnmount(() => {
        console.log('[SSE] 🚪 컴포넌트 언마운트 시 연결 종료')
        es.close()
    })
}
