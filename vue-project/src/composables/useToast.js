import { ref } from 'vue'
export const toasts = ref([])

export function useToast() {
    const push = msg => {
        toasts.value.unshift({
            id: Date.now(), msg }
        )
        setTimeout(() => toasts.value.pop(), 5000)
    }
    return {
        push,
        toasts
    }
}
