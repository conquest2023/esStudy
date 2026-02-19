package es.board.domain.feed.event.reply;

import es.board.domain.notification.feed.FeedNotificationService;
import es.board.mapper.entity.ReplyDomainMapper;
import es.board.repository.entity.repository.NotificationRepository;
import es.board.domain.CommentRepository;
import es.board.domain.notification.NotificationService;
import es.board.domain.feed.event.ReplyCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReplyEventListener {

    private final FeedNotificationService notificationService;

    private final NotificationRepository notificationRepository;

    private final CommentRepository commentRepository;


    @EventListener(ReplyCreatedEvent.class)
    public void handleReplyCreated(ReplyCreatedEvent event) {

        String commentOwnerId = commentRepository.findByUserId(event.getCommentId());
        if (commentOwnerId != null && !commentOwnerId.equals(event.getUserId())) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("postId",event.getPostId());
            payload.put("message",  event.getResponse().getUsername() + "님이 답글을 작성하였습니다: " + event.getResponse().getContent());

            notificationService.sendReplyNotification(
                    commentOwnerId,
                    payload
            );

            notificationRepository.save(ReplyDomainMapper.toEntityNotification(commentOwnerId, event.getResponse()));

        }
    }
}
