// composables/useSSE.js
import { onBeforeUnmount } from 'vue'
import { useToast } from '@/composables/useToast'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'

// 🔒 모듈 스코프 싱글턴들
let esRef = null
let subscribed = false
let reconnectTimer = null

export function useSSE(token) {
    if (!token) {
        console.warn('[SSE] 토큰 없음: 연결하지 않음')
        return
    }

    const router = useRouter()
    const { push } = useToast()
    const store = useUserStore()

    // ─────────────────────────────────────────
    // 핸들러들 (항상 동일 참조 유지)
    // ─────────────────────────────────────────
    const handleRankTop1 = (e) => {
        let parsed; try { parsed = JSON.parse(e.data) } catch { return }
        const msg = `👑 ${parsed.message || '랭킹 1위 게시글'}`
        if (parsed.postId) push(msg, `/post/${parsed.postId}`)
        else push(msg, '/trending/top3')
    }

    const handleRankTop3 = (e) => {
        let parsed; try { parsed = JSON.parse(e.data) } catch { return }
        const posts = Array.isArray(parsed.posts) ? parsed.posts.slice(0, 3) : []
        if (!posts.length) return

        // 동일 이벤트 합치기 위한 id 키 (서버에서 id 주면 그걸 쓰는 게 최고)
        const idem = parsed.id || `top3:${posts.map(p => p.postId).join(',')}`

        push({
            id: idem,  // 같은 id면 덮어쓰기
            type: 'top3',
            title: '오늘의 베스트 Top 3',
            message: parsed.message || '업데이트 되었습니다!',
            posts,
            duration: 7000,
            onClick: () => router.push('/trending/top3'),
            onItemClick: (p) => router.push(`/post/${p.postId}`),
        })
    }

    const handlePollMissing = (e) => {
        let parsed; try { parsed = JSON.parse(e.data) } catch { return }
        const posts = Array.isArray(parsed.posts) ? parsed.posts.slice(0, 5) : []
        if (!posts.length) return

        // idem 키: 서버 payload에 id가 있으면 그걸 쓰고, 없으면 조합
        const idem = parsed.id || `poll-missing:${parsed.count}:${posts.map(p => p.postId).join(',')}`

        push({
            id: idem, // 같은 id면 덮어쓰기
            type: 'poll-missing',
            title: '아직 안 한 투표가 있어요',
            message: parsed.message || `아직 참여하지 않은 투표가 ${parsed.count ?? posts.length}개 있습니다.`,
            posts, // [{ postId, title }]
            duration: 10000,
            label: '보러가기',
            onClick: () => router.push('/poll'),
            onItemClick: (p) => router.push(`/post/${p.postId}`),
        })
    }

    const handleNotification = (e, emoji) => {
        let parsed
        try { parsed = JSON.parse(e.data) } catch (error) {
            return console.error('[SSE] 알림 JSON 파싱 실패:', e.data, error)
        }
        const n = store.addNotification(parsed)
        const msg = `${emoji} ${n.message ?? '새 알림이 도착했습니다'}`

        // 일반 알림은 중복 덮어쓰기 id 지정 (postId 기준)
        const idem = parsed.id || (n.postId ? `notif:${n.type || 'generic'}:${n.postId}` : undefined)

        if (n.postId) {
            push({ id: idem, msg, routePath: `/post/${n.postId}` })
        } else {
            push({ id: idem, msg, routePath: '/notifications' })
        }
    }

    // ─────────────────────────────────────────
    // 실제 연결 함수 (중복 방지 + 재연결)
    // ─────────────────────────────────────────
    const connect = () => {
        if (esRef) {
            // 이미 연결되어 있으면 재사용
            return
        }

        esRef = new EventSource(`/api/subscribe?token=${encodeURIComponent(token)}`)

        esRef.onopen = () => {
            console.log('[SSE] 연결 성공 및 스트리밍 시작')
            if (reconnectTimer) {
                clearTimeout(reconnectTimer)
                reconnectTimer = null
            }
        }

        esRef.onerror = (error) => {
            console.warn('[SSE] 오류/끊김 → 20초 후 재연결', error)
            try { esRef.close() } catch {}
            esRef = null
            if (!reconnectTimer) {
                reconnectTimer = setTimeout(() => {
                    reconnectTimer = null
                    connect()
                }, 20000)
            }
        }

        // 리스너는 최초 1회만 바인딩
        if (!subscribed) {
            esRef.addEventListener('comment-notification', e => handleNotification(e, '💬'))
            esRef.addEventListener('todo-notification',    e => handleNotification(e, '📝'))
            esRef.addEventListener('reply-notification',   e => handleNotification(e, '↩️'))
            esRef.addEventListener('notice-notification',  e => handleNotification(e, '📢'))
            esRef.addEventListener('point-notification',   e => handleNotification(e, '💰'))
            esRef.addEventListener('like-notification',    e => handleNotification(e, '💗'))

            esRef.addEventListener('rank-top1-notification', handleRankTop1)
            esRef.addEventListener('rank-top3-notification', handleRankTop3)

            esRef.addEventListener('poll-notification', handlePollMissing)

            subscribed = true
        }
    }

    connect()

    // 레이아웃(App/DefaultLayout)에서 1회만 쓰는 것을 권장
    onBeforeUnmount(() => {
        // 화면 전환으로 이 훅이 파괴되어도 싱글턴을 유지하고 싶으면 닫지 말고 유지
        // 완전 종료 원하면 아래 주석 해제
        // try { esRef?.close() } catch {}
        // esRef = null
    })

    // HMR 중복 방지
    if (import.meta?.hot) {
        import.meta.hot.dispose(() => {
            try { esRef?.close() } catch {}
            esRef = null
            subscribed = false
            if (reconnectTimer) { clearTimeout(reconnectTimer); reconnectTimer = null }
        })
    }
}
