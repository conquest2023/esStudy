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
public class PostEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishFeedCreated(PostEntity post) {

        Map<String, Object> event = new HashMap<>();
        event.put("eventId", UUID.randomUUID().toString());
        event.put("type", "feed.created");
        event.put("aggregateId", String.valueOf(post.getId()));
        event.put("occurredAt", Instant.now().toString());

        Map<String, Object> p = new HashMap<>();
        p.put("docType", "feed");
        p.put("id", String.valueOf(post.getId()));
        p.put("parentId", null);
        p.put("category", post.getCategory());        // 있으면
        p.put("title", post.getTitle());
        p.put("content", post.getDescription());
        p.put("authorId", post.getUserId());
        p.put("createdAt", post.getCreatedAt().atOffset(ZoneOffset.UTC).toString());
        p.put("updatedAt", null);
        p.put("eventVersion", 1);
        event.put("payload", p);


        rabbitTemplate.convertAndSend("feed.events", "feed.index", event);
    }

    public void publishFeedUpdated(PostEntity post) {

        Map<String, Object> event = new HashMap<>();
        event.put("eventId", UUID.randomUUID().toString());
        event.put("type", "feed.updated");
        event.put("aggregateId", String.valueOf(post.getId()));
        event.put("occurredAt", Instant.now().toString());

        Map<String, Object> p = new HashMap<>();
        p.put("docType", "feed");
        p.put("id", String.valueOf(post.getId()));
        p.put("parentId", null);
        p.put("category", post.getCategory());        // 있으면
        p.put("title", post.getTitle());
        p.put("content", post.getDescription());
        p.put("authorId", post.getUserId());
        p.put("createdAt", post.getCreatedAt().atOffset(ZoneOffset.UTC).toString());
        p.put("updatedAt", post.getModifiedAt().atOffset(ZoneOffset.UTC).toString());
        p.put("eventVersion", 1);
        event.put("payload", p);


        rabbitTemplate.convertAndSend("feed.events", "feed.index", event);
    }

    public void publishFeedDeleted(int id) {

        Map<String, Object> event = new HashMap<>();
        event.put("eventId", UUID.randomUUID().toString());
        event.put("type", "feed.deleted");
        event.put("aggregateId", String.valueOf(id));
        event.put("occurredAt", Instant.now().toString());
        Map<String, Object> p = new HashMap<>();
        p.put("docType", "feed");
        p.put("id", String.valueOf(id));
        p.put("parentId", null);
        p.put("eventVersion", 1);
        event.put("payload", p);


        rabbitTemplate.convertAndSend("feed.events", "feed.index", event);
    }
}
