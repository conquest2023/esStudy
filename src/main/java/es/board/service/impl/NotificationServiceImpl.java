package es.board.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.board.repository.entity.Notification;
import es.board.repository.entity.repository.NotificationRepository;
import es.board.repository.entity.repository.UserRepository;
import es.board.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {


    private final RedisTemplate<String, String> redisTemplate;
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    private  final ObjectMapper objectMapper;

    private  final NotificationRepository notificationRepository;

    private final UserRepository userRepository;

    private static final String COMMENT_NOTIFICATION_KEY = "notifications:comment:";
    private static final String TODO_NOTIFICATION_KEY = "notifications:todo:";

    private static final String REPLY_NOTIFICATION_KEY = "notifications:reply:";

    private static final String LIKE_NOTIFICATION_KEY = "notifications:like:";
    private static final String NOTICE_NOTIFICATION_KEY = "notifications:notice:";

    private static final String POINT_NOTIFICATION_KEY = "notifications:point:";


    @Override
    public SseEmitter subscribe(String userId) {
        SseEmitter emitter = new SseEmitter(0L);
        emitters.put(userId, emitter);
        sendPendingNotifications(userId, COMMENT_NOTIFICATION_KEY, "comment-notification", emitter);

        sendPendingNotifications(userId, TODO_NOTIFICATION_KEY, "todo-notification", emitter);

        sendPendingNotifications(userId, LIKE_NOTIFICATION_KEY, "like-notification", emitter);

        sendPendingNotifications(userId, REPLY_NOTIFICATION_KEY, "reply-notification", emitter);

        sendPendingNotifications(userId, NOTICE_NOTIFICATION_KEY, "notice-notification", emitter);

        sendPendingNotifications(userId, POINT_NOTIFICATION_KEY, "point-notification", emitter);

        emitter.onCompletion(() -> removeEmitter(userId, "onCompletion"));
        emitter.onTimeout(() -> removeEmitter(userId, "onTimeout"));
        emitter.onError(ex -> {
            log.error("[{}] SSE 연결 오류 - {}", userId, ex.getMessage(), ex);
            removeEmitter(userId, "onError");
        });

        return emitter;
    }

    @Override
    public void sendLikeNotification(String userId, int postId, String message) {
        sendFeedNotification(userId, postId, LIKE_NOTIFICATION_KEY, "like-notification", message);
    }
    @Override
    public void sendCommentNotification(String userId, int postId, String message) {
        sendFeedNotification(userId,postId, COMMENT_NOTIFICATION_KEY, "comment-notification", message);
    }

    @Override
    public void sendPointNotification(String userId,String message) {
        sendPointNotification(userId,POINT_NOTIFICATION_KEY, "point-notification", message);
    }

    @Override
    public void sendNoticeNotification(int postId, String message) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastMonth = now.minusMonths(1);
        List<String> userIds = userRepository.findMonthActiveUser(lastMonth);
        for (String userId : userIds) {
            sendFeedNotification(userId, postId, NOTICE_NOTIFICATION_KEY, "notice-notification", message);
        }
    }
    @Override
    public void sendTodoNotification(String userId, String message) {
        sendNotification(userId, TODO_NOTIFICATION_KEY, "todo-notification", message);
    }
    @Override
    public void sendReplyNotification(String userId,int postId, String message) {
        sendFeedNotification(userId,postId, REPLY_NOTIFICATION_KEY, "reply-notification", message);
    }

    private void sendPendingNotifications(String userId, String redisKeyPrefix, String eventType, SseEmitter emitter) {
        String redisKey = redisKeyPrefix + userId;
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
    private void sendPointNotification(String userId, String redisKeyPrefix, String eventType, String message) {
        String redisKey = redisKeyPrefix + userId;
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

    private void sendFeedNotification(String userId, int postId, String redisKeyPrefix, String eventType, String message) {

        String redisKey = redisKeyPrefix + userId;
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

    private void sendNotification(String userId, String redisKeyPrefix, String eventType, String message) {
        String redisKey = redisKeyPrefix + userId;
        if (!emitters.containsKey(userId)) {
            redisTemplate.opsForList().leftPush(redisKey, message);
            redisTemplate.opsForList().trim(redisKey, 0, 49);
            redisTemplate.expire(redisKey, 7, TimeUnit.DAYS);
            log.warn("SSE 구독 없음 - Redis에 저장: {}", message);
            return;
        }
        SseEmitter emitter = emitters.get(userId);
        if (emitter == null) {
            log.warn("Emitter 없음, 알림 전송 불가 - userId: {}", userId);
            return;
        }
        try {
            log.info("알림 전송 - userId: {}, 메시지: {}", userId, message);
            emitter.send(SseEmitter.event()
                    .name(eventType)
                    .data(message));
        } catch (IOException e) {
            log.error("알림 전송 실패 - userId: {}", userId, e);
            emitters.remove(userId);
        }
    }


    private void removeEmitter(String userId, String reason) {
        log.info("[{}] SSE 연결 종료 - Reason: {}", userId, reason);
        emitters.remove(userId);
    }

    @Override
    public List<String> getUserNotifications(String userId) {
        String redisKey = "notifications:" + userId;
        return redisTemplate.opsForList().range(redisKey, 0, -1);

    }

    @Override
    public List<Notification> getNotificationList(String userId) {
        return notificationRepository.findByNotificationList(userId);
    }

}
