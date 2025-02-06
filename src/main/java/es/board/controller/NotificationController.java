package es.board.controller;

import es.board.config.jwt.JwtTokenProvider;
import es.board.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    private final JwtTokenProvider jwtTokenProvider;

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
        log.info("Notification Subscription 컨트롤러");
        if (token != null) {

            String userId = jwtTokenProvider.getUserId(token);
            SseEmitter emitter = notificationService.subscribe(userId);
            return new ResponseEntity<>(emitter, HttpStatus.OK);
        }

        // 401 Unauthorized 반환
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

}