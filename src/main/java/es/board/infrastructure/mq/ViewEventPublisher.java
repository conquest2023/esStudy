package es.board.infrastructure.mq;

import es.board.controller.record.NormalizedContent;
import es.board.domain.NormalizeService;
import es.board.infrastructure.entity.feed.PostEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ViewEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    private final NormalizeService normalizeService;

    public void publishFeedViewed(PostEntity post) {
        var nowUtc = Instant.now();
        var kst = ZoneId.of("Asia/Seoul");
        var createdUtc = post.getCreatedAt(); // Instant 라고 가정
        var createdKst = createdUtc.atZone(kst);

        // 1) 정규화
        NormalizedContent norm = normalizeService.normalize(
                post.getDescription(), post.getTitle()
        );
        String contentText = norm.contentText();    // NPE 방지
        String excerpt = defaultString(norm.excerpt());
        int charLen = norm.contentLength();
        int tokenCount = norm.tokenCount();
        String contentHash = defaultString(norm.contentHash());
        List<String> links = norm.links() == null ? List.of() : norm.links();

        // 2) 본문 저장 전략 (토큰 기준 권장)

        Map<String, Object> payload = new HashMap<>();
        payload.put("docType", "feed.view");
        payload.put("id", String.valueOf(post.getId()));
        payload.put("category", post.getCategory());
        payload.put("title", post.getTitle());
        payload.put("content", contentText);
        payload.put("excerpt", excerpt);
        payload.put("contentLength", charLen);
        payload.put("tokenCount", tokenCount);
        payload.put("contentHash", contentHash);
        payload.put("outboundLinks", links);
        payload.put("authorId", post.getUserId());
        payload.put("username", post.getUsername());
        payload.put("visibility", "PUBLIC");
        payload.put("createdAt", createdUtc.atOffset(ZoneOffset.UTC).toString());
        payload.put("createdAtLocal", createdKst.toOffsetDateTime().toString());
        payload.put("createdHourLocal", createdKst.getHour());
        payload.put("initialScores", Map.of("view", 0, "like", 0, "comment", 0, "reply", 0));
        payload.put("eventVersion", 1);


        Map<String, Object> event = new HashMap<>();
        String eventId = UUID.randomUUID().toString();
        event.put("eventId", eventId);
        event.put("schemaVersion", 1);
        event.put("type", "feed.viewed");                      // 타입 명확히
        event.put("aggregateId", String.valueOf(post.getId()));
        event.put("occurredAt", nowUtc.toString());
        event.put("producer", "feed-service");
        event.put("payload", payload);

        rabbitTemplate.convertAndSend("feed.events", "feed.viewed", event, m -> {
            m.getMessageProperties().setContentType("application/json");
            m.getMessageProperties().setMessageId(eventId);
            m.getMessageProperties().setDeliveryMode(org.springframework.amqp.core.MessageDeliveryMode.PERSISTENT);
            return m;
        });
    }

    private static String defaultString(String s) {
        return s == null ? "" : s;
    }
}