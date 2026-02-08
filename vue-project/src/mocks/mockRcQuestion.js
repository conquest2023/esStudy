// mockRcQuestion.js
export const mockRcQuestion = {
    id: "mock-rc-001",
    type: "RC",
    part: 5,
    level: "BRONZE",
    tags: ["grammar", "adverb", "it-performance"],

    content: {
        passage: "The new backend server is ------- more efficient than the previous one.",

        questions: [
            {
                options: [
                    "signify",
                    "significant",
                    "significantly",
                    "significance"
                ],
                correctIndex: 2,
                explanation:
                    "형용사 more efficient를 수식해야 하므로 부사 형태인 significantly가 정답입니다."
            }
        ]
    }
};
