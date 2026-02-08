import { defineStore } from 'pinia'

const KEY = 'wrong_notes_v1'

function load() {
    try { return JSON.parse(localStorage.getItem(KEY) || '[]') } catch { return [] }
}
function save(list) {
    localStorage.setItem(KEY, JSON.stringify(list))
}

export const useWrongNoteStore = defineStore('wrongNotes', {
    state: () => ({
        list: load(), // [{questionId, type, level, tags, snapshot, selectedIndex, answerIndex, wrongCount, lastWrongAt, mastered}]
    }),

    actions: {
        async isSaved(questionId) {
            return this.list.some(x => x.questionId === questionId)
        },

        async save(question, selectedIndex, answerIndex) {
            const existing = this.list.find(x => x.questionId === question.id)
            const now = new Date().toISOString()

            if (existing) {
                existing.wrongCount = (existing.wrongCount || 1) + 1
                existing.lastWrongAt = now
                existing.selectedIndex = selectedIndex
                existing.answerIndex = answerIndex
            } else {
                this.list.unshift({
                    questionId: question.id,
                    type: question.type,
                    level: question.level,
                    tags: question.tags || [],
                    snapshot: question, // MVP는 통째 저장(나중에 최소 필드로 줄이면 됨)
                    selectedIndex,
                    answerIndex,
                    wrongCount: 1,
                    lastWrongAt: now,
                    mastered: false,
                })
            }
            save(this.list)
        },

        async remove(questionId) {
            this.list = this.list.filter(x => x.questionId !== questionId)
            save(this.list)
        },

        async toggleMastered(questionId) {
            const x = this.list.find(v => v.questionId === questionId)
            if (!x) return
            x.mastered = !x.mastered
            save(this.list)
        },
    },
})
