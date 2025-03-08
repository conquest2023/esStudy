package es.board.service.impl;

import es.board.controller.model.res.Notification;
import es.board.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final RedisTemplate<String, String> redisTemplate;
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    private static final String COMMENT_NOTIFICATION_KEY = "notifications:comment:";
    private static final String TODO_NOTIFICATION_KEY = "notifications:todo:";


    private static final String REPLY_NOTIFICATION_KEY = "notifications:reply:";

    // ✅ SSE 구독 요청 처리
    @Override
    public SseEmitter subscribe(String userId) {
        log.info("SSE 구독 요청 - userId: {}", userId);
        SseEmitter emitter = new SseEmitter(60 * 1000L);
        emitters.put(userId, emitter);


        sendPendingNotifications(userId, COMMENT_NOTIFICATION_KEY, "comment-notification", emitter);

        sendPendingNotifications(userId, TODO_NOTIFICATION_KEY, "todo-notification", emitter);

        sendPendingNotifications(userId,REPLY_NOTIFICATION_KEY, "reply-notification", emitter);

        emitter.onCompletion(() -> removeEmitter(userId, "onCompletion"));
        emitter.onTimeout(() -> removeEmitter(userId, "onTimeout"));
        emitter.onError(ex -> {
            log.error("[{}] SSE 연결 오류 - {}", userId, ex.getMessage(), ex);
            removeEmitter(userId, "onError");
        });

        return emitter;
    }

    private void sendPendingNotifications(String userId, String redisKeyPrefix, String eventType, SseEmitter emitter) {
        String redisKey = redisKeyPrefix + userId;
        log.info("📩 redisKey",redisKey);
        List<String> notifications = redisTemplate.opsForList().range(redisKey, 0, -1);
        if (notifications != null && !notifications.isEmpty()) {
            try {
                for (String message : notifications) {
                    emitter.send(SseEmitter.event().name(eventType).data(message));
                }
                redisTemplate.delete(redisKey);
            } catch (IOException e) {
                log.error("기존 알림 전송 실패 - userId: {}, eventType: {}", userId, eventType, e);
            }
        }
    }


    @Override
    public void sendCommentNotification(String userId, String feedUID, String message) {
        sendNotification(userId, COMMENT_NOTIFICATION_KEY, "comment-notification", message);
    }


    @Override
    public void sendTodoNotification(String userId, String message) {
        sendNotification(userId, TODO_NOTIFICATION_KEY, "todo-notification", message);
    }
    @Override
    public void sendReplyNotification(String userId, String message) {
        sendNotification(userId, REPLY_NOTIFICATION_KEY, "reply-notification", message);
    }



    private void sendNotification(String userId, String redisKeyPrefix, String eventType, String message) {
        String redisKey = redisKeyPrefix + userId;
        log.info("redisKey={}",redisKey);
        if (!emitters.containsKey(userId)) {
            redisTemplate.opsForList().leftPush(redisKey, message);
            redisTemplate.opsForList().trim(redisKey, 0, 49);
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
            emitter.send(SseEmitter.event().name(eventType).data(message));
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
}
