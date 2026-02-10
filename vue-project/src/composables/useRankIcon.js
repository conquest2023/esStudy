
import { computed } from 'vue'


export function useRankIcon() {
    const rankIcon = (i) => {
        return ['👑', '🥇', '🥈', '🥉','🔥'][i] || `${i + 1}.`
    }

    const recentRankIcon = i => {
        const fire = '🔥'
        return `${i+1}. ${fire}`
    }

    return {
        rankIcon,
        recentRankIcon
    }
}