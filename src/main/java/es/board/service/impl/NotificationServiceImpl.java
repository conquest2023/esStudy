package es.board.service.impl;


import es.board.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl  implements NotificationService {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    // SSE 구독 요청이 들어오면 emitter 생성

    @Override
    public SseEmitter subscribe(String userId) {
            log.info("🔗 SSE 구독 요청 - userId: {}", userId);
            SseEmitter emitter = new SseEmitter(60 * 1000L); // 60초 동안 연결 유지
            emitters.put(userId, emitter);
        for (String s : emitters.keySet()) {
            log.info("List={}",s);
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
    public void sendNotification(String userId, String message) {
        SseEmitter emitter = emitters.get(userId);
//        log.info(emitters.get(userId).toString());
        if (emitter == null) {
            log.warn("❗ Emitter가 존재하지 않아 알림 전송 불가 - 대상 사용자: {}", userId);
            return; // 여기서 즉시 리턴
        }
        if (emitter != null) {
            try {
                log.info("🚀 알림 전송 시작 - 대상 사용자: {}, 메시지: {}", userId, message);
                emitter.send(SseEmitter.event().name("comment-notification")
                        .data(message));
                log.info("✅ 알림 전송 성공 - 대상 사용자: {}", userId);
            } catch (IOException e) {
                log.error("❌ 알림 전송 실패 - 대상 사용자: {}", userId, e);
                emitter.complete();
                emitters.remove(userId);
            }
            log.warn("❗ Emitter가 존재하지 않아 알림 전송 불가 - 대상 사용자: {}", userId);
        }
    }
}
