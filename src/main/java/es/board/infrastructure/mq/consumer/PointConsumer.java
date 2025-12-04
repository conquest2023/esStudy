package es.board.infrastructure.mq.consumer;

import es.board.config.rabbitmq.RabbitMQQueue;
import es.board.repository.entity.PointHistoryEntity;
import es.board.repository.entity.repository.PointHistoryRepository;
import es.board.infrastructure.mq.old.FeedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class PointConsumer {


    private final StringRedisTemplate stringRedisTemplate;

    private  final PointHistoryRepository pointHistoryRepository;
    @Transactional
    @RabbitListener(queues = RabbitMQQueue.QUEUE_POINT)
    public void receive(FeedEvent event) {
        grantCommentPoint(event.getCommenterId(), event.getUsername());
    }

    public void grantCommentPoint(String userId,String username) {
        String today = LocalDate.now().toString();
        String key = "comment:point:" + userId + ":" + today;
        Long currentCount = stringRedisTemplate.opsForValue().increment(key);
        if (currentCount == 1) {
            stringRedisTemplate.expire(key, Duration.ofDays(1));
        }
        if (currentCount > 10) {
            log.info("{}님은 오늘 댓글 작성 포인트 한도(10개)를 초과했습니다.", userId);
            return;
        }
        createPointHistory(userId);
        log.info("댓글 작성 포인트 지급 완료! 현재 작성 횟수: {}", currentCount);
    }

    public void createPointHistory(String userId) {
        PointHistoryEntity history = PointHistoryEntity.builder()
                .userId(userId)
//                .username(username)
                .pointChange(3)
                .reason("댓글")
                .createdAt(LocalDateTime.now())
                .build();
        pointHistoryRepository.save(history);
    }
}
