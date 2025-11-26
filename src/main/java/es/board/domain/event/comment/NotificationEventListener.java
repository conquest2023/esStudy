package es.board.domain.event.comment;

import es.board.controller.model.mapper.CommentDomainMapper;
import es.board.repository.entity.repository.NotificationRepository;
import es.board.domain.PostRepository;
import es.board.service.NotificationService;
import es.board.domain.event.CommentCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;
    private final PostRepository postRepository;

    @EventListener
    public void handleCommentCreated(CommentCreatedEvent event) {

        String commenterId = event.getUserId();
        String postOwnerId = postRepository.findByUserId(event.getPostId());

        if (postOwnerId != null && !commenterId.equals(postOwnerId)) {

            // 2. 알림 전송 로직
            notificationService.sendCommentNotification(
                    commenterId,
                    event.getPostId(),
                    event.getResponse().getUsername() + "님이 댓글을 작성하였습니다: " + event.getResponse().getContent()
            );

             notificationRepository.save(CommentDomainMapper.toEntityNotification(postOwnerId, event.getResponse()));

        }
    }
}