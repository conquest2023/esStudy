package es.board.infrastructure.mq;

import es.board.infrastructure.entity.feed.PostEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeedEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishFeedCreated(PostEntity post) {

        Map<String, Object> event = new HashMap<>();
        event.put("eventId", UUID.randomUUID().toString());
        event.put("type", "feed.created");
        event.put("aggregateId", String.valueOf(post.getId()));
        event.put("occurredAt", Instant.now().toString());

        Map<String, Object> payload = new HashMap<>();
        payload.put("feedId", post.getId());
        payload.put("title", post.getTitle());
        payload.put("content", post.getDescription());
        payload.put("authorId", post.getUserId());
        payload.put("createdAt", post.getCreatedAt().atOffset(ZoneOffset.UTC).toString());
//        payload.put("updatedAt", post.getModifiedAt().toString());
        event.put("payload", payload);

        // 🚀 MQ 발행! 라우팅키 = feed.index

        rabbitTemplate.convertAndSend("feed.events", "feed.index", event);
    }
}
