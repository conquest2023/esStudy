package es.board.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface NotificationService {


    void sendNotification(String userId, String feedUID, String message);

    List<String> getUserNotifications(String userId);
    SseEmitter subscribe(String userId);
}
