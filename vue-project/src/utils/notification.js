export function addFeedNotification(parsed, store, showToast) {
    const { postId, message } = parsed

    store.notifications = [
        {
            id: Date.now(),
            postId,
            message,
            read: false
        },
        ...store.notifications
    ]

    console.log('👉 알림 파싱 결과:', parsed)
    showToast(message,postId)
}
