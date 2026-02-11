import { ref } from 'vue'
import { useRouter } from 'vue-router'

export const toasts = ref([])

// id -> timeoutHandle
const timers = new Map()

// 내부 유틸
const armTimer = (id, duration, removeFn) => {
    if (!Number.isFinite(duration) || duration <= 0) return
    const old = timers.get(id)
    if (old) clearTimeout(old)
    const handle = setTimeout(() => removeFn(id), duration)
    timers.set(id, handle)
}

export function useToast() {
    const router = useRouter()

    const remove = (id) => {
        // 타이머 정리
        const t = timers.get(id)
        if (t) { clearTimeout(t); timers.delete(id) }
        // 배열 일부만 제거(TransitionGroup 안정)
        const idx = toasts.value.findIndex(x => x.id === id)
        if (idx !== -1) toasts.value.splice(idx, 1)
    }

    const push = (msgOrOpts, routePath = null, opts = {}) => {
        const base = typeof msgOrOpts === 'string'
            ? { type: 'text', msg: msgOrOpts, routePath, ...opts }
            : { ...msgOrOpts }

        const id = String(
            base.id ??
            base.stableKey ??
            (Date.now() + Math.random())
        )

        const duration = Number.isFinite(base.duration) ? base.duration : 5000

        if (base.routePath && !base.onClick) {
            base.onClick = () => router.push(base.routePath)
        }

        const toastObj = { id, ...base }
        toastObj._close = () => remove(id)

        const idx = toasts.value.findIndex(t => t.id === id)
        if (idx >= 0) {
            toasts.value[idx] = { ...toasts.value[idx], ...toastObj }
        } else {
            toasts.value.unshift(toastObj)
        }

        armTimer(id, duration, remove)
        return id
    }

    return { push, remove, toasts }
}
