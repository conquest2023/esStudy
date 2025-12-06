import { ref } from 'vue'
import { useRouter } from 'vue-router'

export const toasts = ref([])

// id -> timeoutHandle
const timers = new Map()

export function useToast() {
    const router = useRouter()

    const remove = (id) => {
        // 타이머 정리
        const t = timers.get(id)
        if (t) { clearTimeout(t); timers.delete(id) }
        // 목록에서 제거
        toasts.value = toasts.value.filter(x => x.id !== id)
    }

    const armTimer = (id, duration) => {
        if (duration <= 0) return
        // 기존 타이머 있으면 교체
        const old = timers.get(id)
        if (old) clearTimeout(old)
        const handle = setTimeout(() => remove(id), duration)
        timers.set(id, handle)
    }

    const push = (msgOrOpts, routePath = null, opts = {}) => {
        // 옵션 표준화
        const base = typeof msgOrOpts === 'string'
            ? { type: 'text', msg: msgOrOpts, routePath, ...opts }
            : { ...msgOrOpts }

        // 동일 id가 오면 덮어쓰기(중복 방지)
        const id = String(base.id ?? (Date.now() + Math.random()))
        const duration = Number.isFinite(base.duration) ? base.duration : 5000

        if (base.routePath && !base.onClick) {
            base.onClick = () => router.push(base.routePath)
        }

        // 수동 닫기 훅 제공 (ToastHost.vue의 emitClose가 이걸 호출)
        const toastObj = { id, ...base }
        toastObj._close = () => remove(id)

        const idx = toasts.value.findIndex(t => t.id === id)
        if (idx >= 0) {
            // 덮어쓰기 시 타이머 리셋
            toasts.value[idx] = { ...toasts.value[idx], ...toastObj }
        } else {
            toasts.value.unshift(toastObj)
        }

        armTimer(id, duration)
        return id
    }

    return { push, remove, toasts }
}
