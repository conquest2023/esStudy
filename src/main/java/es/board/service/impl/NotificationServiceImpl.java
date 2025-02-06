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
public class NotificationServiceImpl  implements NotificationService {
    private final RedisTemplate<String, String> redisTemplate;
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    // SSE 구독 요청이 들어오면 emitter 생성

    @Override
    public SseEmitter subscribe(String userId) {
            log.info("🔗 SSE 구독 요청 - userId: {}", userId);
            SseEmitter emitter = new SseEmitter(60 * 1000L); // 60초 동안 연결 유지
            emitters.put(userId, emitter);
            List<String> notifications = redisTemplate.opsForList().range("notifications:" + userId, 0, -1);
            if (notifications != null && !notifications.isEmpty()) {
                try {
                    for (String message : notifications) {
                        emitter.send(SseEmitter.event().name("comment-notification").data(message));
                    }
                    redisTemplate.delete("notifications:" + userId); // 전송 후 삭제
                } catch (IOException e) {
                    log.error("❌ 기존 알림 전송 실패 - 사용자: {}", userId, e);
                }
            }
            emitter.onCompletion(() -> {
                log.info("[{}] onCompletion callback", userId);
                emitters.remove(userId);
            });
            // 타임아웃 시
            emitter.onTimeout(() -> {
                log.info("[{}] onTimeout callback", userId);
                emitter.complete();
                emitters.remove(userId);
            });
            //에러(Broken pipe 등) 발생 시
            emitter.onError(ex -> {
                log.error("[{}] onError callback - {}", userId, ex.getMessage(), ex);
                emitter.complete();
                emitters.remove(userId);
            });

        return emitter;
    }

    // 댓글이 달리면 해당 게시글 작성자에게 알림 전송
    @Override
    public  void sendNotification(String userId, String feedUID, String message)  {
        String redisKey = "notifications:" + userId;
        Notification notification = new Notification(feedUID, message);

        if (!emitters.containsKey(userId)) {
            redisTemplate.opsForList().leftPush(redisKey, message);
            redisTemplate.opsForList().trim(redisKey, 0, 49);
            log.warn("❗ SSE 구독 없음 - Redis에 저장: {}", message);
            return;
        }
        SseEmitter emitter = emitters.get(userId);
        if (emitter == null) {
            log.warn("❗ Emitter가 존재하지 않아 알림 전송 불가 - 대상 사용자: {}", userId);
            return;
        }
        try {
            log.info(notification.toString());
            log.info("🚀 알림 전송 - 대상 사용자: {}, 메시지: {}", userId, message);
            emitter.send(SseEmitter.event()
                    .name("comment-notification")
                    .data(notification));
            log.info("✅ 알림 전송 성공 - 대상 사용자: {}", userId);
        } catch (IOException e) {
            log.error("❌ 알림 전송 실패 (Broken Pipe 가능) - 대상 사용자: {}", userId, e);
            emitters.remove(userId); // 연결이 끊어진 경우 삭제
        }
    }

    @Override
    public List<String> getUserNotifications(String userId) {
        String redisKey = "notifications:" + userId;
        return redisTemplate.opsForList().range(redisKey, 0, -1);
    }
}
