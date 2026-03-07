package es.board.infrastructure.jpa;

import es.board.infrastructure.entity.NotificationSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface NotificationSubscriptionRepository extends JpaRepository<NotificationSubscription, Long> {

    List<NotificationSubscription> findAllByUserId(String userId);

    Optional<NotificationSubscription> findByEndpoint(String endpoint);
}