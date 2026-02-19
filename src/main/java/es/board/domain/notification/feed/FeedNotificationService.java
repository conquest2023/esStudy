package es.board.domain.notification.feed;

import es.board.domain.notification.NotificationService;
import es.board.domain.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
@Slf4j
@RequiredArgsConstructor
public class FeedNotificationService {



    private final NotificationService notificationService;


    public void sendPointNotification(String userId,Map<String,Object> payload) {
        notificationService.sendEvent(userId, payload,NotificationType.POINT);
    }
    public void sendLikeNotification(String userId, Map<String, Object> payload) {
        notificationService.sendEvent(userId, payload, NotificationType.LIKE);
    }
    public void sendCommentNotification(String userId, Map<String, Object> payload) {
        notificationService.sendEvent(userId,payload, NotificationType.COMMENT);
    }
    public void sendReplyNotification(String userId, Map<String, Object> payload) {
        notificationService.sendEvent(userId,payload, NotificationType.REPLY);
    }

}
