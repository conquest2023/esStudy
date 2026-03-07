package es.board.domain.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.board.domain.notification.service.PushNotificationService;
import es.board.infrastructure.entity.NotificationSubscription;
import es.board.infrastructure.jpa.NotificationSubscriptionRepository;
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


    private final PushNotificationService pushNotificationService;

    private final RedisTemplate<String, String> redisTemplate;
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    private  final ObjectMapper objectMapper;

    private  final NotificationRepository notificationRepository;

    private final NotificationSubscriptionRepository subscriptionRepository;


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
    public void sendEvent(String userId, Map<String,Object> payload, NotificationType type) {
        String redisKey = type.getRedisKey(userId);
        String eventType = type.getEventName();

        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);
            redisTemplate.opsForList().leftPush(redisKey, jsonPayload);
            redisTemplate.opsForList().trim(redisKey, 0, 20); // 최근 20개 유지
            redisTemplate.expire(redisKey, 7, TimeUnit.DAYS);
            log.info("[notify] type={}, targetUserId={}, containsEmitter={}, keys={}",
                    type.getEventName(),userId, emitters.containsKey(userId), emitters.keySet());

            // 3. SSE 실시간 전송 로직
            SseEmitter emitter = emitters.get(userId);
            if (emitter == null) {
                log.warn("[Notify] Emitter 없음, Redis에만 저장됨 - userId: {}", userId);
                List<NotificationSubscription> devices = subscriptionRepository.findAllByUserId(Long.valueOf(userId));

                for (NotificationSubscription device : devices) {
                    pushNotificationService.sendPush(device, "Workly 새 알림", (String) payload.get("message"));
                }

                return;
            }

            emitter.send(SseEmitter.event()
                    .id(UUID.randomUUID().toString())
                    .name(eventType)
                    .data(jsonPayload));

        } catch (IOException e) {
            log.error("[Notify] 전송 실패 - userId: {}", userId, e);
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
