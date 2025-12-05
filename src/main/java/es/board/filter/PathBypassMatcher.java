package es.board.filter;

import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.List;

@Component
public class PathBypassMatcher {
    private static final List<String> PATTERNS = List.of(
            "/error/**", "/api/auth/status", "/api/feeds/**",
            "/api/notice/**","/api/points/summary/**","/api/points/recent/**",
             "/api/comments/**",
//            "/api/likes/**", "/api/like/**"
            "/api/notifications/recent/**",
            "/api/replys/**", "/api/post/stats", "/api/post/**",
            "/api/subscribe/**", "/api/notifications/all",
            "/api/top-writers/**", "/api/day", "/api/get-ip",
            "/api/interview/**", "/api/search/today/todo", "/api/list/notice",
            "/api/upload-images", "/api/info/**", "/api/vote/detail", "/api/police/**", "/api/toeic/**", "/api/civil/**"
    );
    private final PathMatcher matcher = new AntPathMatcher();
    public boolean bypass(String uri) {
        return PATTERNS.stream().anyMatch(p -> matcher.match(p, uri));
    }
}