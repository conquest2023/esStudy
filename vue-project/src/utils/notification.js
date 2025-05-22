export function addFeedNotification(parsed, store, showToast) {
    const { feedUID, message } = parsed


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
