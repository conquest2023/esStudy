import { onBeforeUnmount } from 'vue'
import { useToast } from './useToast'

export function useSSE(token) {
    const { push } = useToast()
    const es = new EventSource(`/subscribe?token=${encodeURIComponent(token)}`)
    es.addEventListener('comment-notification', e => {
        const { message } = JSON.parse(e.data)
        push(`💬 ${message}`)
    })
    es.onerror = () => setTimeout(() => useSSE(token), 10000)
    onBeforeUnmount(() => es.close())
}
