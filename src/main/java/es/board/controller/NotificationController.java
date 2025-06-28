package es.board.controller;

import es.board.config.jwt.JwtTokenProvider;
import es.board.ex.TokenValidator;
import es.board.repository.entity.Notification;
import es.board.service.NotificationService;
import es.board.service.UserNotificationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")

public class NotificationController {

    private final NotificationService notificationService;

    private final JwtTokenProvider jwtTokenProvider;

    private  final UserNotificationService userNotificationService;


    private  final TokenValidator tokenValidator;

    // 클라이언트가 SSE 연결을 요청할 때 호출됨
//    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public SseEmitter subscribe(HttpServletRequest request) {
//        String token = request.getHeader("Authorization");
//        log.info(token);
//        if (token != null && token.startsWith("Bearer ")) {
//            token = token.substring(7);
//        }
//        return notificationService.subscribe(jwtTokenProvider.getUserId(token));
//    }
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(@RequestParam("token") String token) {
        if (token == null || token.isBlank()) {
            throw  new RuntimeException("토근이 필요합니다");
        }
        SseEmitter emitter = notificationService.subscribe(jwtTokenProvider.getUserId(token));
        return new ResponseEntity<>(emitter, HttpStatus.OK);
    }

    @GetMapping("/notifications/all")
    public ResponseEntity<?> getAllNotifications(@RequestHeader("Authorization") String token) {
        if (token == null || token.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "토큰이 필요합니다."));
        }
        List<Notification> notifications = notificationService.getNotificationList(jwtTokenProvider.getUserId(token.substring(7)));
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/notifications/check")
    public ResponseEntity<?> getCheckNotifications(@RequestHeader("Authorization") String token) {
        if (token == null || token.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "토큰이 필요합니다."));
        }
        List<Notification> notifications = userNotificationService.getCheckNotifications(jwtTokenProvider.getUserId(token.substring(7)));
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/notifications/count")
    public ResponseEntity<?> getCountNotifications(@RequestHeader("Authorization") String token) {
        if (token == null || token.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "토큰이 필요합니다."));
        }
        return ResponseEntity.ok(userNotificationService.getCheckNotifications(jwtTokenProvider.getUserId(token)));
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