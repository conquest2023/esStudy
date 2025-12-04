package es.board.infrastructure.mq;

import es.board.infrastructure.entity.feed.CommentEntity;
import es.board.infrastructure.entity.feed.ReplyEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReplyEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishReplyCreated(ReplyEntity reply) {
        Map<String, Object> ev = new HashMap<>();
        ev.put("eventId", UUID.randomUUID().toString());
        ev.put("type", "reply.created");
        ev.put("aggregateId", String.valueOf(reply.getId()));
        ev.put("occurredAt", Instant.now().toString());
        Map<String, Object> p = new HashMap<>();
        p.put("docType", "reply");
        p.put("id", String.valueOf(reply.getId()));
        p.put("parentId", String.valueOf(reply.getPostId()));
        p.put("content", reply.getContent());
        p.put("authorId", reply.getUserId());
        p.put("createdAt", reply.getCreatedAt().atOffset(ZoneOffset.UTC).toString());
        p.put("eventVersion", 1);
        ev.put("payload", p);
        rabbitTemplate.convertAndSend("feed.events", "feed.index", ev);
    }


    public void publishReplyUpdated(ReplyEntity reply) {
        Map<String, Object> ev = new HashMap<>();
        ev.put("eventId", UUID.randomUUID().toString());
        ev.put("type", "reply.updated");
        ev.put("aggregateId", String.valueOf(reply.getId()));
        ev.put("occurredAt", Instant.now().toString());
        Map<String, Object> p = new HashMap<>();
        p.put("docType", "reply");
        p.put("id", String.valueOf(reply.getId()));
        p.put("parentId", String.valueOf(reply.getPostId()));
        p.put("content", reply.getContent());
        p.put("authorId", reply.getUserId());
        p.put("createdAt", reply.getCreatedAt().atOffset(ZoneOffset.UTC).toString());
        p.put("eventVersion", 1);
        ev.put("payload", p);
        rabbitTemplate.convertAndSend("feed.events", "feed.index", ev);
    }
    public void publishReplyDeleted(long id) {

        Map<String, Object> event = new HashMap<>();
        event.put("eventId", UUID.randomUUID().toString());
        event.put("type", "reply.deleted");
        event.put("aggregateId", String.valueOf(id));
        event.put("occurredAt", Instant.now().toString());
        Map<String, Object> p = new HashMap<>();
        p.put("docType", "reply");
        p.put("id", String.valueOf(id));
        p.put("parentId", null);
        p.put("eventVersion", 1);
        event.put("payload", p);

        rabbitTemplate.convertAndSend("feed.events", "feed.index", event);
    }
}
