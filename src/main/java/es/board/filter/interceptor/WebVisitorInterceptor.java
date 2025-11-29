package es.board.filter.interceptor;

import es.board.filter.ClientIpResolver;
import es.board.filter.PathBypassMatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebVisitorInterceptor implements HandlerInterceptor {
    private final PathBypassMatcher bypass;
    private final ClientIpResolver ipResolver;
    private final VisitTracker tracker;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
        String uri = req.getRequestURI();
        if (bypass.bypass(uri))
            return true;
        String ip = ipResolver.resolve(req);
        String userId = (String) req.getAttribute("userId"); // JwtUserResolverFilter가 넣어둠
        String ua = req.getHeader("User-Agent");
        if (!"127.0.0.1".equals(ip)) {
            tracker.trackVisit(userId, ip, ua);
            tracker.markOnline(ip);
        }
        return true;
    }
}
