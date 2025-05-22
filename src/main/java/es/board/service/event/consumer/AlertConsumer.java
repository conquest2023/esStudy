package es.board.service.event.consumer;

import es.board.config.rabbitmq.RabbitMQQueue;
import es.board.controller.model.mapper.CommentMapper;
import es.board.repository.entity.entityrepository.NotificationRepository;
import es.board.service.NotificationService;
import es.board.service.event.FeedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class AlertConsumer {

    private  final CommentMapper commentMapper;

    private  final NotificationService notificationService;

    private  final NotificationRepository notificationRepository;

    @RabbitListener(queues = RabbitMQQueue.QUEUE_ALERT)
    public void receive(FeedEvent event) {
        if (event == null || event.getPostOwnerId() == null || event.getCommenterId() == null) {
            log.warn("수신된 FeedEvent가 null이거나 필드가 null입니다: {}", event);
            return;
        }
        if (!event.getPostOwnerId().equals(event.getCommenterId())) {
            log.info("댓글 이벤트 발행됨: {}", event);
            notificationService.sendCommentNotification(
                    event.getPostOwnerId(), event.getFeedUID(),
                    event.getUsername() + "님이 댓글을 작성하였습니다: " + event.getContent()
            );
            notificationService.sendPointNotification(event.getCommenterId(),event.getFeedUID(),"댓글 작성 포인트를 발급 받으셨습니디");
            notificationRepository.save(commentMapper.toCommentEvent(event.getPostOwnerId(), event));
        }
    }
}
