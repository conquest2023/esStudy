export function addFeedNotification(parsed, notifications, showToast) {
    const { feedUID, message } = parsed
    notifications.unshift({
        id: Date.now(),
        feedUID,
        message,
        read: false
    })

    console.log('👉 알림 파싱 결과:', parsed)
    showToast(message, feedUID)
}
