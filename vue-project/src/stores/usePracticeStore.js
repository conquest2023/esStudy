import { defineStore } from 'pinia'

const SAMPLE_RC = [/* 여기에 네 JSON 배열 그대로 붙여넣어도 됨 */]

export const usePracticeStore = defineStore('practice', {
    state: () => ({
        // 실제 서비스에선 서버에서 정답 index 내려주거나, 채점 API 사용
        // 지금은 데모용으로 임시 정답 맵(옵션 인덱스)
        answerMap: {
            '6986d1d732a8d47778dd0fb9': 2, // significantly
            '6986d1d732a8d47778dd0fba': 2, // successfully
            '6986d1d732a8d47778dd0fbb': 0, // confidential
            '6986d1d732a8d47778dd0fbc': 1, // maintaining
            '6986d1d732a8d47778dd0fbd': 0, // open
            '6986d1d732a8d47778dd0fbe': 0, // maintenance
            '6986d1d732a8d47778dd0fbf': 0, // unless
            '6986d1d732a8d47778dd0fc0': 0, // simplify
            '6986d1d732a8d47778dd0fc1': 0, // thanked
            '6986d1d732a8d47778dd0fc2': 0, // regularly
        },
        solveRecords: {}, // { [questionId]: { selectedIndex, isCorrect, solvedAt } }
    }),

    actions: {
        async fetchQuestions(type, filters) {
            let list = []
            if (type === 'RC') list = SAMPLE_RC
            else list = SAMPLE_RC // vocab 샘플 없어서 임시

            if (filters.level && filters.level !== 'ALL') {
                list = list.filter(q => q.level === filters.level)
            }
            if (filters.tag && filters.tag !== 'ALL') {
                list = list.filter(q => (q.tags || []).includes(filters.tag))
            }
            if (type === 'RC' && filters.part) {
                list = list.filter(q => q.part === filters.part)
            }

            if (filters.order === 'RANDOM') {
                list = [...list].sort(() => Math.random() - 0.5)
            }

            // 기본 10문제 세트
            return list.slice(0, 10)
        },

        getAnswerIndex(q) {
            return this.answerMap[q.id] ?? null
        },

        submitAnswer(q, selectedIndex) {
            const answerIndex = this.getAnswerIndex(q)
            const isCorrect = answerIndex === selectedIndex
            this.solveRecords[q.id] = {
                selectedIndex,
                isCorrect,
                solvedAt: new Date().toISOString(),
            }
            return isCorrect
        },
    },
})
