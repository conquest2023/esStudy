package es.board.infrastructure.mq;

import es.board.controller.record.NormalizedContent;
import es.board.domain.NormalizeService;
import es.board.infrastructure.entity.feed.PostEntity;
import es.board.infrastructure.entity.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
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

    public void publishFeedViewed(PostEntity post, User user) {
        ZoneId KST = ZoneId.of("Asia/Seoul");
        ZonedDateTime nowKst = ZonedDateTime.now(KST);
        Instant nowUtc = nowKst.toInstant();

        LocalDateTime created = post.getCreatedAt(); // KST 기준 LDT
        OffsetDateTime createdKst = created.atZone(ZoneId.of("Asia/Seoul")).toOffsetDateTime();
        Instant createdUtc = createdKst.toInstant();

        NormalizedContent norm = normalizeService.normalize(post.getDescription(), post.getTitle());
        String excerpt = norm.excerpt() == null ? "" : norm.excerpt();
        int charLen = norm.contentLength();
        int tokenCount = norm.tokenCount();
        String contentHash = norm.contentHash() == null ? "" : norm.contentHash();
        List<String> links = norm.links() == null ? List.of() : norm.links();

        Map<String, Object> payload = new HashMap<>();
        payload.put("docType", "feed.view");
        payload.put("id", String.valueOf(post.getId()));
        payload.put("category", post.getCategory());
        payload.put("title", post.getTitle());
        payload.put("content", norm.contentText() );
        payload.put("excerpt", excerpt);
        payload.put("contentLength", charLen);
        payload.put("tokenCount", tokenCount);
        payload.put("contentHash", contentHash);
        payload.put("outboundLinks", links);
        payload.put("authorId", post.getUserId());
        payload.put("username", post.getUsername());
        payload.put("visibility", "PUBLIC");

        payload.put("createdAt", createdUtc.toString());       // UTC (ES 정렬용)
        payload.put("createdAtLocal", createdKst.toString());  // +09:00 포함한 KST ri
        payload.put("createdHourLocal", createdKst.getHour());

        payload.put("viewedAt", nowUtc.toString());                       // UTC ISO
        payload.put("viewedAtLocal", nowKst.toOffsetDateTime().toString());           // +09:00
        payload.put("viewedHourLocal", nowKst.getHour());

        payload.put("viewerId", user.getUserId());
        payload.put("viewerName", user.getUsername());

        payload.put("initialScores", Map.of("view", 0, "like", 0, "comment", 0, "reply", 0));
        payload.put("eventVersion", 3);

        // --- 이벤트 래퍼
        Map<String, Object> event = new HashMap<>();
        String eventId = UUID.randomUUID().toString();
        event.put("eventId", eventId);
        event.put("schemaVersion", 3);
        event.put("type", "feed.viewed");
        event.put("aggregateId", String.valueOf(post.getId()));
        event.put("occurredAt", nowUtc.toString());
        event.put("producer", "feed-service");
        event.put("partitionKey", String.valueOf(user.getId())); // 파티션키로도 활용
        event.put("payload", payload);
        // 발행
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