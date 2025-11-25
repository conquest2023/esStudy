
import { computed } from 'vue'


export function useRankIcon() {
    const rankIcon = (i) => {
        return ['👑', '🥇', '🥈', '🥉'][i] || `${i + 1}.`
    }

    return {
        rankIcon
    }
}