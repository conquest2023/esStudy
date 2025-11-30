import { ref } from 'vue'
import { useRouter } from 'vue-router'

export const toasts = ref([])

export function useToast() {
    const router = useRouter()

    const push = (msg, routePath = null, opts = {}) => {
        const onClickHandler = routePath
            ? () => {
                router.push(routePath)
            }
            : null

        toasts.value.unshift({
            id: Date.now(),
            msg,
            routePath,
            onClick: onClickHandler,
            ...opts
        })

        setTimeout(() => toasts.value.pop(), 5000)
    }
    return { push, toasts }
}