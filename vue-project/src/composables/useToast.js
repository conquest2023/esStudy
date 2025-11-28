import { ref } from 'vue'
export const toasts = ref([])

export function useToast() {
    const push = (msg, routePath = null, opts = {}) => {
        toasts.value.unshift({
            id: Date.now(),
            msg,
            routePath,
            ...opts    // ← onClick, label 저장
        })
        setTimeout(() => toasts.value.pop(), 5000)
    }
    return { push, toasts }

}
