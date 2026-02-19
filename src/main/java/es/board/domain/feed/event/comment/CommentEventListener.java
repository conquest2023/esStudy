package es.board.domain.feed.event.comment;

import es.board.domain.notification.feed.FeedNotificationService;
import es.board.mapper.CommentDomainMapper;
import es.board.repository.entity.repository.NotificationRepository;
import es.board.domain.PostRepository;
import es.board.domain.notification.NotificationService;
import es.board.domain.feed.event.CommentCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentEventListener {

    private final FeedNotificationService notificationService;
    private final NotificationRepository notificationRepository;
    private final PostRepository postRepository;

    @EventListener(CommentCreatedEvent.class)
    public void handleCommentCreated(CommentCreatedEvent event) {

        String postOwnerId = postRepository.findByUserId(event.getPostId());
        if (postOwnerId != null &&!postOwnerId.equals(event.getUserId())) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("postId",event.getPostId());
            payload.put("message",  event.getResponse().getUsername() + "님이 댓글을 작성하였습니다: " + event.getResponse().getContent());
            notificationRepository.save(CommentDomainMapper.toEntityNotification(postOwnerId, event.getResponse()));
            notificationService.sendCommentNotification(
                    postOwnerId,
                    payload
            );
        }
    }
}