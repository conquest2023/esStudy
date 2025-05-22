package es.board.service;

import es.board.repository.entity.Notification;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface NotificationService {


    void sendPointNotification(String userId, String feedUID, String message);
    void sendNoticeNotification(List<String> userIds, String feedUID, String message);
    void  sendReplyNotification(String userId,String feedUID,String message);
    void sendCommentNotification(String userId, String feedUID, String message);
    void sendTodoNotification(String userId, String message);
    List<String> getUserNotifications(String userId);

    List<Notification> getNotificationList(String userId);
    SseEmitter subscribe(String userId);
}
