package es.board.domain.notification.service;

import es.board.infrastructure.entity.NotificationSubscription;
import lombok.RequiredArgsConstructor;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Security;

@Service
@RequiredArgsConstructor
public class PushNotificationService {
    @Value("${vapid.public.key}")
    private String publicKey;

    @Value("${vapid.private.key}")
    private String privateKey;

    public void sendPush(NotificationSubscription sub, String title, String message) {
        try {
            Security.addProvider(new BouncyCastleProvider());

            PushService pushService = new PushService(publicKey, privateKey, "mailto:admin@workly.info");

            Subscription subscription = new Subscription(
                    sub.getEndpoint(),
                    new Subscription.Keys(sub.getP256dh(), sub.getAuth())
            );

            String payload = String.format("{\"title\":\"%s\", \"content\":\"%s\"}", title, message);

            // 5. 발송
            Notification notification = new Notification(subscription, payload);
            pushService.send(notification);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}