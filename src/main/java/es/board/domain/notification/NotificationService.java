package es.board.domain.notification;

import es.board.repository.entity.Notification;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface NotificationService {



    void sendRankingEvent(String userId, String jsonPayload, NotificationType type);

    void sendTodoNotification(String userId, String message);


    void sendEnglishEvent(String userId, NotificationType type, String message);
    void sendPointEvent(String userId, NotificationType type, String message);
    void sendPollEvent(String userId, String jsonPayload, NotificationType type);
    void sendFeedEvent(String userId, int postId, NotificationType type, String eventType, String message);

    void sendAnalysisEvent(String userId, Object payload,NotificationType type);

    List<Notification> getNotificationList(String userId);
    SseEmitter subscribe(String userId);
}
