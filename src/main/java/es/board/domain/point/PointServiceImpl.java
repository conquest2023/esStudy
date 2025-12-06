package es.board.domain.point;

import es.board.controller.model.dto.feed.TopWriter;
import es.board.repository.entity.PointHistoryEntity;
import es.board.repository.entity.repository.PointHistoryRepository;
import es.board.infrastructure.jpa.projection.UserPointProjection;
import es.board.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {

    private final PointHistoryRepository repository;

    private final NotificationService notificationService;

    private final StringRedisTemplate stringRedisTemplate;

    private  final RedisTemplate redisTemplate;

    private  static  final String TOP5_USER_KEY= "TOP_USER5_KEY";

    private  static  final String RECENT_USER_KEY= "TOP_RECENT_KEY";


    @Override
    public void grantActivityPoint(String userId, String activityType, int pointChange, int limitCount) {
        if (isExcludedUser(userId)) {
            return;
        }
        String today = LocalDate.now().toString();
        String key = String.format("%s:point:%s:%s", activityType.toLowerCase(), userId, today);

        Long currentCount = stringRedisTemplate.opsForValue().increment(key);
        if (currentCount == 1) {
            stringRedisTemplate.expire(key, Duration.ofDays(1));
        }

        if (currentCount > limitCount) {
            log.info("{}님은 오늘 {} 작성 포인트 한도({}개)를 초과했습니다.", userId, activityType, limitCount);
            return;
        }
        String msg = String.format("포인트를 발급받으셨습니다. %s: +%d점", activityType, pointChange);
        notificationService.sendPointNotification(userId, msg);

        createPointHistory(userId, pointChange, activityType);
        log.info("{} 작성 포인트 지급 완료! 현재 작성 횟수: {}", activityType, currentCount);
    }

    @Override
    public List<TopWriter> getSumTop5RecentUser() {
        ValueOperations<String, List<TopWriter>> valueOps = redisTemplate.opsForValue();
        List<TopWriter> cachedData = valueOps.get(RECENT_USER_KEY);
        if (cachedData != null) {
            return cachedData;
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastSevenDay = now.minusDays(7);
        List<UserPointProjection> recentTop5= repository.sumPointUserRecentTop5(lastSevenDay);
        List<TopWriter> topWriters = recentTop5.stream().map(p -> new TopWriter(p.getUsername(),
                p.getTotalCount())).toList();
        valueOps.set(RECENT_USER_KEY, topWriters, 3, TimeUnit.HOURS);

        return topWriters;
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

    @Override
    public List<TopWriter> getSumTop5User() {
        ValueOperations<String, List<TopWriter>> valueOps = redisTemplate.opsForValue();
        List<TopWriter> cachedData = valueOps.get(TOP5_USER_KEY);
        if (cachedData != null) {
            return cachedData;
        }
        List<TopWriter> topWriters = getWriters();
        valueOps.set(TOP5_USER_KEY, topWriters, 3, TimeUnit.HOURS);
        return topWriters;
    }

    private boolean isExcludedUser(String userId) {
        return userId == null || userId.equals("hoeng") || userId.equals("asd");
    }

    private List<TopWriter> getWriters() {
        List<UserPointProjection> pointHistories = repository.sumPointUserTop5();
        List<TopWriter> list = pointHistories.stream().map(p -> new TopWriter(p.getUsername(),
                p.getTotalCount())).toList();
        return list;
    }
}
