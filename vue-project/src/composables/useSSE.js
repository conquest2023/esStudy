import { onBeforeUnmount } from 'vue'
import { useToast } from '@/composables/useToast'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'

let esRef = null
let subscribed = false
let reconnectTimer = null

const seen = new Map()
const SEEN_TTL = 15_000

function dedup(key) {
    if (!key) return false
    const now = Date.now()
    // 가끔 청소
    for (const [k, exp] of seen) if (exp < now) seen.delete(k)
    const exp = seen.get(key)
    if (exp && exp > now) return true
    seen.set(key, now + SEEN_TTL)
    return false
}

function stableKeyFromParsed(type, parsed, fallback) {
    if (parsed?.id) return String(parsed.id)
    if (fallback) return String(fallback)

    if (type === 'point') {
        const tx = parsed.pointTxId || parsed.txId || parsed.eventId || parsed.userId || 'anon'
        const amt = parsed.amount ?? parsed.points ?? '0'
        const at = parsed.createdAt || parsed.time || ''
        return `point:${tx}:${amt}:${at}`
    }

    if (parsed?.postId) {
        return `${parsed.type || 'notif'}:${parsed.postId}`
    }

    return `ev:${type}:${Date.now()}`
}

export function useSSE(token) {
    if (!token) {
        console.warn('[SSE] 토큰 없음: 연결하지 않음')
        return
    }

    const router = useRouter()
    const { push } = useToast()
    const store = useUserStore()

    const handleAnalysisNotification = (e) => {
        let parsed; try { parsed = JSON.parse(e.data) } catch { return }
        const listItems = Array.isArray(parsed.analysis) ? parsed.analysis : []

        const key = stableKeyFromParsed('analysis', parsed, e.lastEventId)
        if (dedup(key)) return

        push({
            id: key,
            type: 'analysis',
            title: parsed.message || '오늘의 하루 분석 결과',
            analysis: listItems,
            duration: 1000000,
            isCenter: true,
            onClick: () => router.push('/analysis-detail'),
        })
    }

    const handleRankTop1 = (e) => {
        let parsed; try { parsed = JSON.parse(e.data) } catch { return }
        const key = stableKeyFromParsed('rank-top1', parsed, e.lastEventId)
        if (dedup(key)) return

        const msg = `👑 ${parsed.message || '랭킹 1위 게시글'}`
        if (parsed.postId) {
            push({ id: key, msg, routePath: `/post/${parsed.postId}`, duration: 5000 })
        } else {
            push({ id: key, msg, routePath: '/trending/top3', duration: 5000 })
        }
    }

    const handleRankTop3 = (e) => {
        let parsed; try { parsed = JSON.parse(e.data) } catch { return }
        const posts = Array.isArray(parsed.posts) ? parsed.posts.slice(0, 3) : []
        if (!posts.length) return

        const idem = parsed.id || `top3:${posts.map(p => p.postId).join(',')}`
        if (dedup(idem)) return

        push({
            id: idem,
            type: 'top3',
            title: '오늘의 베스트 Top 3',
            message: parsed.message || '업데이트 되었습니다!',
            posts,
            duration: 100000,
            onClick: () => router.push('/trending/top3'),
            onItemClick: (p) => router.push(`/post/${p.postId}`),
        })
    }

    const handlePollMissing = (e) => {
        let parsed; try { parsed = JSON.parse(e.data) } catch { return }
        const posts = Array.isArray(parsed.posts) ? parsed.posts.slice(0, 5) : []
        if (!posts.length) return

        const idem = parsed.id || `poll-missing:${parsed.count}:${posts.map(p => p.postId).join(',')}`
        if (dedup(idem)) return

        push({
            id: idem,
            type: 'poll-missing',
            title: '아직 안 한 투표가 있어요',
            message: parsed.message || `아직 참여하지 않은 투표가 ${parsed.count ?? posts.length}개 있습니다.`,
            posts,
            duration: 100000,
            label: '보러가기',
            onClick: () => router.push('/poll'),
            onItemClick: (p) => router.push(`/post/${p.postId}`),
        })
    }

    const handleNotification = (e, emoji, typeAlias) => {
        let parsed
        try {
            parsed = JSON.parse(e.data)
        } catch (error) {
            return console.error('[SSE] 알림 JSON 파싱 실패:', e.data, error)
        }

        const kind = typeAlias === 'point' ? 'point' : (parsed.type || typeAlias || 'generic')
        const key = stableKeyFromParsed(kind, parsed, e.lastEventId);
        // if (dedup(key)) return;

        const toastId = `${key}_${Date.now()}`;

        const n = store.addNotification?.(parsed) ?? parsed;
        const msg = `${emoji} ${n.message ?? '새 알림이 도착했습니다'}`;

        const options = {
            id: toastId, // 유니크한 ID (리스트 쌓기용)
            msg,
            duration: 100000
        };

        if (n.postId) {
            push({ ...options, routePath: `/post/${n.postId}` });
        } else {
            push({ ...options, routePath: '/notifications' });
        }
    }

    const connect = () => {
        if (esRef) return
        esRef = new EventSource(`/api/subscribe?token=${encodeURIComponent(token)}`)

        esRef.onopen = () => {
            console.log('[SSE] 연결 성공')
            if (reconnectTimer) { clearTimeout(reconnectTimer); reconnectTimer = null }
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

        if (!subscribed) {
            esRef.addEventListener('comment-notification', e => handleNotification(e, '💬', 'comment'))
            esRef.addEventListener('todo-notification',    e => handleNotification(e, '📝', 'todo'))
            esRef.addEventListener('reply-notification',   e => handleNotification(e, '↩️', 'reply'))
            esRef.addEventListener('notice-notification',  e => handleNotification(e, '📢', 'notice'))
            esRef.addEventListener('point-notification',   e => handleNotification(e, '💰', 'point'))
            esRef.addEventListener('like-notification',    e => handleNotification(e, '💗', 'like'))

            esRef.addEventListener('analysis-notification', handleAnalysisNotification)
            esRef.addEventListener('rank-top1-notification', handleRankTop1)
            esRef.addEventListener('rank-top3-notification', handleRankTop3)
            esRef.addEventListener('poll-notification', handlePollMissing)

            subscribed = true
        }
    }

    connect()

    onBeforeUnmount(() => {
        // 필요 시 닫기 로직 추가
    })

    if (import.meta?.hot) {
        import.meta.hot.dispose(() => {
            try { esRef?.close() } catch {}
            esRef = null
            subscribed = false
            if (reconnectTimer) { clearTimeout(reconnectTimer); reconnectTimer = null }
        })
    }
}
