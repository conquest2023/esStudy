package es.board.domain.event.producer;


import es.board.config.rabbitmq.RabbitMQQueue;
import es.board.domain.event.FeedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishCommentEvent(FeedEvent event) {
        rabbitTemplate.convertAndSend(RabbitMQQueue.EXCHANGE_NAME, "", event); // Fanout이므로 routing key는 ""
    }

}
