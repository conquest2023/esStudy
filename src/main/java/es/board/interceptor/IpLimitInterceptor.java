package es.board.interceptor;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import es.board.config.jwt.JwtTokenProvider;
import es.board.config.redis.RedisConfig;
import es.board.service.VisitorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class IpLimitInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private VisitorService visitorService;
    private final Cache<String, LocalDateTime> visitCache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.DAYS)
            .maximumSize(10000)
            .build();

    private static final String VISIT_KEY_PREFIX = "visit:";
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String uri = request.getRequestURI();
        log.info(uri);
        if (uri.startsWith("/api/top-writers") ||
                uri.startsWith("/api/get-ip") ||
                uri.startsWith("/api/day") ||
                uri.startsWith("/api/toeic") ||
                uri.startsWith("/api/police") ||
                uri.startsWith("/api/civil") ||
                uri.startsWith("/api/interview/test") ||
                uri.startsWith("/api/search/today/todo") ||
                uri.startsWith("/api/list/notice") ||
                uri.startsWith("/api/feeds") ||
                uri.startsWith("/api/vote/detail") ||
                uri.startsWith("/error") ||
                uri.startsWith("/api/get/ticket/vote") ||
                uri.startsWith("/api/search/view/feed/id") ||
                uri.startsWith("/api/subscribe") ||
                uri.startsWith("/api/notifications/all") ||
                uri.startsWith("/api/interview/best/answer") ||
                uri.contains(".js") || uri.contains(".css") || uri.contains(".ico") || uri.contains(".png")) {
            return true;
        }

        String token = request.getHeader("Authorization");
        String ipAddress = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        String userId = (token != null && token.startsWith("Bearer ")) ? jwtTokenProvider.getUserId(token.substring(7)) : "guest";
//        String uniqueKey = userId.equals("guest") ? ipAddress : userId;
        String uniqueKey = ipAddress;

        String today = LocalDate.now().toString();
        String visitKey = VISIT_KEY_PREFIX + today + ":" + uniqueKey;
        Boolean hasVisited = redisTemplate.hasKey(visitKey);
        if (!"127.0.0.1".equals(ipAddress) && (hasVisited == null || !hasVisited)) {
            redisTemplate.opsForValue().set(visitKey, "visited", Duration.ofDays(1));
            CompletableFuture.runAsync(() -> visitorService.saveIP(userId, ipAddress, userAgent));
            log.info("새로운 방문자 기록 - {} , {}", uniqueKey ,visitKey);
        } else {
            log.info("중복 방문 방지 - {}", uniqueKey);
        }
        String onlineKey = "online_users:" + uniqueKey;
        redisTemplate.opsForValue().set(onlineKey, "active", Duration.ofMinutes(10));

        return true;
    }

        private String getClientIpAddress(HttpServletRequest request) {
            String ip = request.getHeader("CF-Connecting-IP");
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("CF-Connecting-IP");
            }
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
            return ip;
        }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

    }
}
