self.addEventListener('push', function(event) {
    if (event.data) {
        console.log('[Service Worker] 푸시 메시지 수신:', event);
        const data = event.data.json();

        const options = {
            body: data.content,
            icon: '/pwa-192x192.png',
            badge: '/pwa-192x192.png',
            vibrate: [100, 50, 100],
            data: {
                url: '/'
            }
        };

        // 진짜로 브라우저 상단 알림을 띄우는 명령
        event.waitUntil(
            self.registration.showNotification(data.title, options)
        );
    }
});

self.addEventListener('notificationclick', function(event) {
    event.notification.close(); // 알림창 닫기

    event.waitUntil(
        clients.openWindow(event.notification.data.url)
    );
});