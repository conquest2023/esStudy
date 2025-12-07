// composables/useToast.js
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
        // 1) 옵션 표준화
        const base = typeof msgOrOpts === 'string'
            ? { type: 'text', msg: msgOrOpts, routePath, ...opts }
            : { ...msgOrOpts }

        // 2) id 안정화: 전달된 id를 최우선 사용(없으면 생성)
        const id = String(
            base.id ??
            base.stableKey ?? // 외부에서 넘긴 안정 키
            (Date.now() + Math.random())
        )

        // 3) 지속 시간 기본값
        const duration = Number.isFinite(base.duration) ? base.duration : 5000

        // 4) 라우팅 클릭 핸들러 기본값
        if (base.routePath && !base.onClick) {
            base.onClick = () => router.push(base.routePath)
        }

        // 5) 토스트 객체 구성 + 내부 close
        const toastObj = { id, ...base }
        toastObj._close = () => remove(id)

        // 6) 동일 id가 이미 있으면 덮어쓰기(타이머 리셋)
        const idx = toasts.value.findIndex(t => t.id === id)
        if (idx >= 0) {
            toasts.value[idx] = { ...toasts.value[idx], ...toastObj }
        } else {
            toasts.value.unshift(toastObj)
        }

        // 7) 타이머 무장
        armTimer(id, duration, remove)
        return id
    }

    return { push, remove, toasts }
}
