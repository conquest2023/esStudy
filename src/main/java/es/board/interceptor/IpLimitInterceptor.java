package es.board.interceptor;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import es.board.config.jwt.JwtTokenProvider;
import es.board.service.VisitorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class IpLimitInterceptor implements HandlerInterceptor {
    ;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private VisitorService visitorService;

    private final Cache<String, LocalDateTime> visitCache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.DAYS)  // 하루 후 자동 만료
            .maximumSize(10000)  // 최대 저장 크기 제한
            .build();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("Authorization");
        String ipAddress = getClientIpAddress(request);
        String sessionId = request.getSession().getId();
        String userAgent = request.getHeader("User-Agent");
        // 로그인 여부 확인 (토큰 유무)
        String userId = (token != null && token.startsWith("Bearer "))
                ? jwtTokenProvider.getUserId(token.substring(7))
                : "guest";

        // 중복 검사 키 (IP + userId 기준)
        String uniqueKey = userId.equals("guest")
                ? "guest:" + ipAddress  // 익명 사용자는 IP 기준으로 캐시 처리
                : userId + ":" + sessionId;  // 로그인 사용자는 sessionId 포함

        // 방문 여부 확인 (1일 동안 캐싱)
        if (visitCache.getIfPresent(uniqueKey) == null) {
            visitCache.put(uniqueKey, LocalDateTime.now());
            // 비동기 처리를 통한 IP 저장 (속도 최적화)
            CompletableFuture.runAsync(() -> visitorService.saveIP(userId, ipAddress, sessionId, userAgent));
            log.info("새로운 방문자 기록 - {}", uniqueKey);
        } else {
            log.info("중복 방문 방지 - {}", uniqueKey);
        }
        return true;
        }

        private String getClientIpAddress(HttpServletRequest request) {
            String ip = request.getHeader("CF-Connecting-IP");

            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }

            return ip;
        }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

    }

//    private void startCacheCleaner() {
//        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
//        scheduler.scheduleAtFixedRate(() -> {
//            visitCache.clear();
//            System.out.println("Daily visitCache cleared at midnight.");
//        }, 1, 1, TimeUnit.DAYS);
//    }


}
