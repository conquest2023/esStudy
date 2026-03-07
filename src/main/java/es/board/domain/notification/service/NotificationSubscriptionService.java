package es.board.domain.notification.service;

import es.board.controller.model.dto.PushSubscriptionDto;
import es.board.infrastructure.entity.NotificationSubscription;
import es.board.infrastructure.entity.user.User;
import es.board.infrastructure.jpa.NotificationSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationSubscriptionService {

    private final NotificationSubscriptionRepository subscriptionRepository;

    @Transactional
    public void saveSubscription(PushSubscriptionDto dto, User user) {

        Optional<NotificationSubscription> existing = subscriptionRepository.findByEndpoint(dto.getEndpoint());

        if (existing.isEmpty()) {
            NotificationSubscription subscription = NotificationSubscription.builder()
                    .endpoint(dto.getEndpoint())
                    .p256dh(dto.getKeys().getP256dh())
                    .auth(dto.getKeys().getAuth())
                    .user(user)
                    .build();

            subscriptionRepository.save(subscription);
        }
    }
}