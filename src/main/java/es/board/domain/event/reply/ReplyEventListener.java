package es.board.domain.event.reply;

import es.board.controller.model.mapper.ReplyDomainMapper;
import es.board.repository.entity.repository.NotificationRepository;
import es.board.domain.CommentRepository;
import es.board.service.NotificationService;
import es.board.domain.event.ReplyCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReplyEventListener {

    private final NotificationService notificationService;

    private final NotificationRepository notificationRepository;

    private final CommentRepository commentRepository;


    @EventListener
    public void handleReplyCreated(ReplyCreatedEvent event) {

        String commenterId = event.getUserId();
        String postOwnerId = commentRepository.findByUserId(event.getCommentId());
        log.info(postOwnerId);
        if (postOwnerId != null && !commenterId.equals(postOwnerId)) {


            notificationService.sendReplyNotification(
                    commenterId,
                    event.getPostId(),
                    event.getResponse().getUsername() + "님이 답글을 작성하였습니다: " + event.getResponse().getContent()
            );

            notificationRepository.save(ReplyDomainMapper.toEntityNotification(postOwnerId, event.getResponse()));

        }
    }
}
