package es.board.domain.notification.controller;

import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.dto.PushSubscriptionDto;
import es.board.domain.notification.service.NotificationSubscriptionService;
import es.board.domain.user.service.AuthService;
import es.board.infrastructure.entity.user.User;
import es.board.repository.entity.Notification;
import es.board.domain.notification.NotificationService;
import es.board.service.OAuthService;
import es.board.service.UserNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;


@Slf4j

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")

public class NotificationController {

    private final NotificationSubscriptionService subscriptionService;
    private final AuthService userService;

    private final NotificationService notificationService;

    private final JwtTokenProvider jwtTokenProvider;

    private  final UserNotificationService userNotificationService;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(@RequestParam("token") String token) {
        if (token == null || token.isBlank()) {
            throw new RuntimeException("토큰이 필요합니다");
        }
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (!jwtTokenProvider.validateToken(token)) {
                throw new RuntimeException("권한이 없습니다");
            }
        }
        String userId = jwtTokenProvider.getUserId(token);
        SseEmitter emitter = notificationService.subscribe(userId);
        log.info("[subscribe] userId={}", userId);
        return new ResponseEntity<>(emitter, HttpStatus.OK);
    }
    @PostMapping("/web-push/subscribe")
    public ResponseEntity<String> subscribe(
            @RequestBody PushSubscriptionDto dto,
            @RequestAttribute("userId") String userId) {

        log.info("[WebPush] 구독 요청 도착 - userId: {}", userId);

        try {
            User user = userService.findByUserId(userId);
            subscriptionService.saveSubscription(dto, user);

            return ResponseEntity.ok("푸시 알림 구독 성공");
        } catch (Exception e) {
            log.error("[WebPush] 구독 저장 실패", e);
            return ResponseEntity.internalServerError().body("구독 저장 실패");
        }
    }
    @GetMapping("/notifications/all")
    public ResponseEntity<?> getUsNotifications(@RequestHeader("Authorization") String token) {
        if (token == null || token.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "토큰이 필요합니다."));
        }
        List<Notification> notifications = notificationService.getNotificationList(jwtTokenProvider.getUserId(token.substring(7)));
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/notifications/recent")
    public ResponseEntity<?> getUserRecentNotifications(@RequestHeader("Authorization") String token) {
        if (token == null || token.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "토큰이 필요합니다."));
        }
        List<Notification> notifications = userNotificationService.getRecentNotifications(jwtTokenProvider.getUserId(token.substring(7)));
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/notifications/count")
    public ResponseEntity<?> getCountNotifications(@RequestHeader("Authorization") String token) {
        if (token == null || token.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "토큰이 필요합니다."));
        }
        return ResponseEntity.ok(userNotificationService.getRecentNotifications(jwtTokenProvider.getUserId(token)));
    }

    @PostMapping("/notification/delete")
    public  ResponseEntity<?> deleteNotifications(@RequestHeader("Authorization") String token,
                                                @RequestBody List<Long> id) {
        if (token == null || token.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "토큰이 필요합니다."));
        }
        userNotificationService.deleteNotification(jwtTokenProvider.getUserId(token.substring(7)),id);
        return ResponseEntity.ok("ok");

    }


    @PostMapping("/notification/check")
    public  ResponseEntity<?> checkNotifications(@RequestHeader("Authorization") String token,
                                                  @RequestBody List<Long> id) {

        if (token == null || token.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "토큰이 필요합니다."));
        }
        userNotificationService.checkedNotification(jwtTokenProvider.getUserId(token.substring(7)),id);
        return ResponseEntity.ok("ok");

    }

}