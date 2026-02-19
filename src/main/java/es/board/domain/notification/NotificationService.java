package es.board.domain.notification;

import es.board.repository.entity.Notification;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

public interface NotificationService {






    void sendEvent(String userId, Map<String,Object> payload,NotificationType type);


    List<Notification> getNotificationList(String userId);
    SseEmitter subscribe(String userId);
}
