package es.board.filter.interceptor;

import es.board.domain.point.PointService;
import es.board.service.AuthService;
import es.board.service.NotificationService;
import es.board.service.VisitorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class VisitTracker {
    private static final String VISIT_KEY_PREFIX = "visit:";
    private static final String ONLINE_PREFIX = "online_users:";
    private final StringRedisTemplate redisTemplate;

    private final VisitorService visitorService;

    private final PointService pointService;

    private final AuthService authService;


    public void trackVisit(String userId, String ip, String userAgent) {
        ZoneId KST = ZoneId.of("Asia/Seoul");
        LocalDateTime now = LocalDateTime.now(KST);
        LocalDate today = now.toLocalDate();
        LocalDateTime midnight = today.plusDays(1).atStartOfDay();
        long expireAtEpochSec = midnight.atZone(KST).toEpochSecond();
        Date expireAt = Date.from(Instant.ofEpochSecond(expireAtEpochSec));
        String dailyIpSetKey = "visit:unique:" + today;
        Long addedIp = redisTemplate.opsForSet().add(dailyIpSetKey, ip);
        if (addedIp != null && addedIp == 1L) {
            visitorService.saveIP(userId, ip, userAgent);
            redisTemplate.expireAt(dailyIpSetKey, expireAt);
        }

        // 2) 유저 기준 '오늘 유니크 사용자' 집합 (포인트/마지막 로그인 1회)
        if (userId != null) {
            String dailyUserSetKey = "visit:user:unique:" + today;
            Long addedUser = redisTemplate.opsForSet().add(dailyUserSetKey, userId);
            if (addedUser != null && addedUser == 1L) {
                pointService.grantActivityPoint(userId, "로그인", 3, 1);
                authService.updateLastLogin(userId);
                redisTemplate.expireAt(dailyUserSetKey, expireAt);
            }
        }

    }
    public void markOnline(String ip) {
        redisTemplate.opsForValue().set(ONLINE_PREFIX + ip, "active", Duration.ofMinutes(10));
    }
}
