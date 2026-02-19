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


//    @Override


    @Override
    public void sendEvent(String userId, Map<String,Object> payload, NotificationType type) {
        String redisKey = type.getRedisKey(userId);
        String eventType = type.getEventName();
        log.info("[notify] type={}, targetUserId={}, containsEmitter={}, keys={}",
                type.getEventName(),userId, emitters.containsKey(userId), emitters.keySet());
        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);
            redisTemplate.opsForList().leftPush(redisKey, jsonPayload);
            redisTemplate.opsForList().trim(redisKey, 0, 20); // 최근 20개 유지
            redisTemplate.expire(redisKey, 7, TimeUnit.DAYS);

            log.info("[Notify] type={}, userId={}, payload={}", eventType, userId, jsonPayload);

            // 3. SSE 실시간 전송 로직
            SseEmitter emitter = emitters.get(userId);
            if (emitter == null) {
                log.warn("[Notify] Emitter 없음, Redis에만 저장됨 - userId: {}", userId);
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
