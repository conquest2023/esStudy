import { ref } from 'vue'
import { useRouter } from 'vue-router'

export const toasts = ref([])

export function useToast() {
    const router = useRouter()

    const remove = (id) => {
        toasts.value = toasts.value.filter(t => t.id !== id)
    }

    const push = (msgOrOpts, routePath = null, opts = {}) => {
        const base = typeof msgOrOpts === 'string'
            ? { type: 'text', msg: msgOrOpts, routePath, ...opts }
            : { ...msgOrOpts }

        const id = Date.now() + Math.random()
        const duration = Number.isFinite(base.duration) ? base.duration : 5000

        if (base.routePath && !base.onClick) {
            base.onClick = () => router.push(base.routePath)
        }

        toasts.value.unshift({ id, ...base })

        if (duration > 0) {
            setTimeout(() => remove(id), duration)
        }
        return id
    }

    return { push, remove, toasts }
}
