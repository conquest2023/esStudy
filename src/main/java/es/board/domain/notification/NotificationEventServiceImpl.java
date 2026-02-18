package es.board.domain.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.board.repository.entity.Notification;
import es.board.repository.entity.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class  NotificationEventServiceImpl implements NotificationService {


    private final RedisTemplate<String, String> redisTemplate;
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    private  final ObjectMapper objectMapper;

    private  final NotificationRepository notificationRepository;


    @Override
    public SseEmitter subscribe(String userId) {
        SseEmitter emitter = new SseEmitter(0L);
        emitters.put(userId, emitter);

        for (NotificationType type : NotificationType.values()) {
            sendPendingNotifications(userId, type, emitter);
        }

        emitter.onCompletion(() -> removeEmitter(userId, "onCompletion"));
        emitter.onTimeout(() -> removeEmitter(userId, "onTimeout"));
        emitter.onError(ex -> {
            log.error("[{}] SSE 연결 오류 - {}", userId, ex.getMessage(), ex);
            removeEmitter(userId, "onError");
        });

        return emitter;
    }


    @Override
    public void sendTodoNotification(String userId, String message) {
//        sendNotification(userId, TODO_NOTIFICATION_KEY, "todo-notification", message);
    }

    @Override
    public void sendEnglishEvent(String userId, NotificationType type, String message) {
        String redisKey = type.getRedisKey(userId);
        String eventType = type.getEventName();
        redisTemplate.opsForList().leftPush(redisKey, message);
        redisTemplate.expire(redisKey, 7, TimeUnit.DAYS);
        log.warn("SSE 구독 없음 - Redis에 저장: {}", message);
        SseEmitter emitter = emitters.get(userId);
        if (emitter == null) {
            log.warn("Emitter 없음, 알림 전송 불가 - userId: {}", userId);
            return;
        }
        Map<String, Object> payload = new HashMap<>();
        payload.put("message", message);
        payload.put("type", eventType);
        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);
            emitter.send(SseEmitter.event()
                    .id(UUID.randomUUID().toString())
                    .name(eventType)
                    .data(jsonPayload));
        } catch (IOException e) {
            log.error("랭킹 알림 전송 실패", e);
            emitters.remove(userId);
        }
    }

//    @Override

    @Override
    public void sendRankingEvent(String userId, String jsonPayload, NotificationType type) {
        String redisKey = type.getRedisKey(userId);
        String eventType = type.getEventName();
        redisTemplate.opsForList().leftPush(redisKey, jsonPayload);
        redisTemplate.expire(redisKey, 7, TimeUnit.DAYS);
        log.warn("SSE 구독 없음 - Redis에 저장: {}", jsonPayload);
        SseEmitter emitter = emitters.get(userId);
        if (emitter == null) {
            log.warn("Emitter 없음, 알림 전송 불가 - userId: {}", userId);
            return;
        }
        try {
            emitter.send(SseEmitter.event()
                    .id(UUID.randomUUID().toString())
                    .name(eventType)
                    .data(jsonPayload));
        } catch (IOException e) {
            log.error("랭킹 알림 전송 실패", e);
            emitters.remove(userId);
        }
    }
    @Override
    public void sendFeedEvent(String userId, int postId, NotificationType type, String eventType, String message) {

        String redisKey = type.getRedisKey(userId);
        log.info("[notify] type={}, targetUserId={}, containsEmitter={}, keys={}",
                eventType, userId, emitters.containsKey(userId), emitters.keySet());
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("message", message);
            payload.put("postId", postId);
            String jsonPayload = objectMapper.writeValueAsString(payload);

            redisTemplate.opsForList().leftPush(redisKey, jsonPayload);

            redisTemplate.opsForList().trim(redisKey, 0, 20);
            redisTemplate.expire(redisKey, 7, TimeUnit.DAYS);
            log.warn("SSE 구독 없음 - Redis에 저장: {}", jsonPayload);

            SseEmitter emitter = emitters.get(userId);
            if (emitter == null) {
                log.warn("Emitter 없음, 알림 전송 불가 - userId: {}", userId);
                return;
            }
            emitter.send(SseEmitter.event()
                    .id(UUID.randomUUID().toString())
                    .name(eventType)
                    .data(jsonPayload));
        } catch (IOException e) {
            log.error("알림 전송 실패 - userId: {}", userId, e);
            emitters.remove(userId);
        }
    }
    @Override
    public void sendPollEvent(String userId, String jsonPayload, NotificationType type) {
        String redisKey = type.getRedisKey(userId);
        String eventType = type.getEventName();
        redisTemplate.opsForList().leftPush(redisKey, jsonPayload);
        redisTemplate.expire(redisKey, 7, TimeUnit.DAYS);
        log.warn("SSE 구독 없음 - Redis에 저장: {}", jsonPayload);
        SseEmitter emitter = emitters.get(userId);
        if (emitter == null) {
            log.warn("Emitter 없음, 알림 전송 불가 - userId: {}", userId);
            return;
        }
        try {
            emitter.send(SseEmitter.event()
                    .id(UUID.randomUUID().toString())
                    .name(eventType)
                    .data(jsonPayload));
        } catch (IOException e) {
            log.error("랭킹 알림 전송 실패", e);
            emitters.remove(userId);
        }
    }

    @Override
    public void sendPointEvent(String userId, NotificationType type, String message) {
        String redisKey = type.getRedisKey(userId);
        String eventType = type.getEventName();
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("message", message);
            String jsonPayload = objectMapper.writeValueAsString(payload);
            if (!emitters.containsKey(userId)) {
                redisTemplate.opsForList().leftPush(redisKey, jsonPayload);
                redisTemplate.opsForList().trim(redisKey, 0, 20);
                log.warn("SSE 구독 없음 - Redis에 저장: {}", jsonPayload);
                return;
            }
            SseEmitter emitter = emitters.get(userId);
            if (emitter == null) {
                log.warn("Emitter 없음, 알림 전송 불가 - userId: {}", userId);
                return;
            }
            emitter.send(SseEmitter.event()
                    .name(eventType)
                    .data(jsonPayload));
        } catch (IOException e) {
            log.error("알림 전송 실패 - userId: {}", userId, e);
            emitters.remove(userId);
        }

    }

    @Override
    public void sendAnalysisEvent(String userId, Object payload, NotificationType type) {
        String redisKey = type.getRedisKey(userId);
        String eventType = type.getEventName();

        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);

            redisTemplate.opsForList().leftPush(redisKey, jsonPayload);
            redisTemplate.expire(redisKey, 3, TimeUnit.DAYS);
            redisTemplate.opsForList().trim(redisKey, 0, 20);
            SseEmitter emitter = emitters.get(userId);
            if (emitter != null) {
                emitter.send(SseEmitter.event()
                        .id(UUID.randomUUID().toString())
                        .name(eventType)
                        .data(jsonPayload));
            } else {
                log.warn("사용자 접속 중 아님 (SSE 건너뜀) - userId: {}", userId);
            }
        } catch (IOException e) {
            log.error("알림 전송 실패 - userId: {}", userId, e);
            emitters.remove(userId);
        }
    }

    private void sendPendingNotifications(String userId,NotificationType type, SseEmitter emitter) {
        String redisKey = type.getRedisKey(userId);
        String eventType = type.getEventName();
        List<String> notifications = redisTemplate.opsForList().range(redisKey, 0, -1);
        if (notifications != null && !notifications.isEmpty()) {
            try {
                for (String message : notifications) {
                    emitter.send(
                            SseEmitter.event()
                                    .name(eventType)
                                    .data(message));
                }
                redisTemplate.delete(redisKey);
            } catch (IOException e) {
                log.error("기존 알림 전송 실패 - userId: {}, eventType: {}", userId, eventType, e);
            }
        }
    }
    private void removeEmitter(String userId, String reason) {
        log.info("[{}] SSE 연결 종료 - Reason: {}", userId, reason);
        emitters.remove(userId);
    }

    @Override
    public List<Notification> getNotificationList(String userId) {
        return notificationRepository.findByNotificationList(userId);
    }

}
