package es.board.service.point;

import es.board.repository.entity.PointHistoryEntity;
import es.board.repository.entity.repository.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {

    private final PointHistoryRepository repository;

    private final StringRedisTemplate redisTemplate;

    @Override
    public void grantActivityPoint(String userId, String activityType, int pointChange, int limitCount) {
        if (isExcludedUser(userId)) {
            return;
        }
        String today = LocalDate.now().toString();
        String key = String.format("%s:point:%s:%s", activityType.toLowerCase(), userId, today);

        Long currentCount = redisTemplate.opsForValue().increment(key);
        if (currentCount == 1) {
            redisTemplate.expire(key, Duration.ofDays(1));
        }

        if (currentCount > limitCount) {
            log.info("{}님은 오늘 {} 작성 포인트 한도({}개)를 초과했습니다.", userId, activityType, limitCount);
            return;
        }
        createPointHistory(userId, pointChange, activityType);
        log.info("{} 작성 포인트 지급 완료! 현재 작성 횟수: {}", activityType, currentCount);
    }

    @Override
    public void createPointHistory(String userId, int pointChange, String reason) {
        PointHistoryEntity history = PointHistoryEntity.builder()
                .userId(userId)
                .pointChange(pointChange)
                .reason(reason)
                .createdAt(LocalDateTime.now())
                .build();
                repository.save(history);
    }
    private boolean isExcludedUser(String userId) {
        return userId == null || userId.equals("hoeng") || userId.equals("asd");
    }
}
