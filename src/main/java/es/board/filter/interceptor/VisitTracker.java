package es.board.filter.interceptor;

import es.board.domain.point.PointService;
import es.board.service.AuthService;
import es.board.service.VisitorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;

@Service
@Slf4j
@RequiredArgsConstructor
public class VisitTracker {
    private static final String VISIT_KEY_PREFIX = "visit:";
    private static final String ONLINE_PREFIX = "online_users:";
    private final StringRedisTemplate redis;
    private final VisitorService visitorService;
    private final PointService pointService;
    private final AuthService authService;

    public void trackVisit(String userId, String ip, String userAgent) {
        String visitKey = VISIT_KEY_PREFIX + LocalDate.now() + ":" + ip;
        Boolean first = redis.opsForValue().setIfAbsent(visitKey, "1", Duration.ofDays(1));
        log.info(first.toString());
        if (Boolean.TRUE.equals(first)) {
            if (userId != null) {
                String userDailyKey = VISIT_KEY_PREFIX + "user:" + LocalDate.now() + ":" + userId;
                Boolean firstUser = redis.opsForValue().setIfAbsent(userDailyKey, "1", Duration.ofDays(1));
                if (Boolean.TRUE.equals(firstUser)) {
                    pointService.grantActivityPoint(userId, "로그인", 3, 1);
                    authService.updateLastLogin(userId);
                }
            }
            visitorService.saveIP(userId, ip, userAgent);
        }
    }

    public void markOnline(String ip) {
        redis.opsForValue().set(ONLINE_PREFIX + ip, "active", Duration.ofMinutes(10));
    }
}
