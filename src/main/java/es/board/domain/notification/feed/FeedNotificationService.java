package es.board.domain.notification.feed;

import es.board.domain.notification.NotificationService;
import es.board.domain.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class FeedNotificationService {



    private final NotificationService notificationService;


    public void sendPointNotification(String userId,String message) {
        notificationService.sendPointEvent(userId,NotificationType.POINT, message);
    }
    public void sendLikeNotification(String userId, int postId, String message) {
        notificationService.sendFeedEvent(userId, postId, NotificationType.LIKE, "like-notification", message);
    }
    public void sendCommentNotification(String userId, int postId, String message) {
        notificationService.sendFeedEvent(userId,postId, NotificationType.COMMENT, "comment-notification", message);
    }
    public void sendReplyNotification(String userId,int postId, String message) {
        notificationService.sendFeedEvent(userId,postId, NotificationType.REPLY, "reply-notification", message);
    }

}
