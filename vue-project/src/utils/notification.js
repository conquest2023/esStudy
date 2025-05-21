export function addFeedNotification(parsed, store, showToast) {
    const { feedUID, message } = parsed

    // 새로운 배열로 대체해야 Pinia가 반응함!
    store.notifications = [
        {
            id: Date.now(),
            feedUID,
            message,
            read: false
        },
        ...store.notifications
    ]

    console.log('👉 알림 파싱 결과:', parsed)
    showToast(message, feedUID)
}
