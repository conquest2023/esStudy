package es.board.infrastructure.mq;

import es.board.infrastructure.entity.feed.CommentEntity;
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
public class CommentEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishCommentCreated(CommentEntity c) {
        Map<String, Object> ev = new HashMap<>();
        ev.put("eventId", UUID.randomUUID().toString());
        ev.put("type", "comment.created");
        ev.put("aggregateId", String.valueOf(c.getId()));
        ev.put("occurredAt", Instant.now().toString());

        Map<String, Object> p = new HashMap<>();
        p.put("docType", "comment");
        p.put("id", String.valueOf(c.getId()));
        p.put("parentId", String.valueOf(c.getPostId())); // 상위 피드
        p.put("content", c.getContent());
        p.put("authorId", c.getUserId());
        p.put("username",c.getUsername());
        p.put("createdAt", c.getCreatedAt().atOffset(ZoneOffset.UTC).toString());
        p.put("updatedAt", null);
        p.put("eventVersion", 1);
        ev.put("payload", p);

        rabbitTemplate.convertAndSend("feed.events", "feed.index", ev);
    }

    public void publishCommentUpdated(CommentEntity c) {
        Map<String, Object> ev = new HashMap<>();
        ev.put("eventId", UUID.randomUUID().toString());
        ev.put("type", "comment.updated");
        ev.put("aggregateId", String.valueOf(c.getId()));
        ev.put("occurredAt", Instant.now().toString());

        Map<String, Object> p = new HashMap<>();
        p.put("docType", "comment");
        p.put("id", String.valueOf(c.getId()));
        p.put("parentId", String.valueOf(c.getPostId()));
        p.put("content", c.getContent());
        p.put("authorId", c.getUserId());
        p.put("username",c.getUsername());
        p.put("createdAt", c.getCreatedAt().atOffset(ZoneOffset.UTC).toString());
        p.put("updatedAt", c.getUpdatedAt().atOffset(ZoneOffset.UTC).toString());
        p.put("eventVersion", 2);
        ev.put("payload", p);
        rabbitTemplate.convertAndSend("feed.events", "feed.index", ev);
    }

    public void publishCommentDeleted(long id) {

        Map<String, Object> event = new HashMap<>();
        event.put("eventId", UUID.randomUUID().toString());
        event.put("type", "comment.deleted");
        event.put("aggregateId", String.valueOf(id));
        event.put("occurredAt", Instant.now().toString());
        Map<String, Object> p = new HashMap<>();
        p.put("docType", "comment");
        p.put("id", String.valueOf(id));
        p.put("parentId", null);
        p.put("eventVersion", 1);
        event.put("payload", p);

        rabbitTemplate.convertAndSend("feed.events", "feed.index", event);
    }
}
