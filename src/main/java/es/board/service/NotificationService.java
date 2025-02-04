package es.board.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationService {


    void sendNotification(String  userId, String message);


    SseEmitter subscribe(String userId);
}
