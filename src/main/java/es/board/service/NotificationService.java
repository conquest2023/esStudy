package es.board.service;

import es.board.infrastructure.entity.feed.PostEntity;
import es.board.repository.entity.Notification;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface NotificationService {


    void sendPointNotification(String userId,String message);
    void sendNoticeNotification(int postId, String message);
    void  sendReplyNotification(String userId,int postId,String message);
    void sendCommentNotification(String userId, int postId, String message);
    void sendTodoNotification(String userId, String message);

    void sendTop3RankingNotification(String userId, List<PostEntity> top3);
    void sendTop1RankingNotification(String userId, PostEntity top1);

    void sendLikeNotification(String userId, int postId, String message);
    List<Notification> getNotificationList(String userId);
    SseEmitter subscribe(String userId);
}
