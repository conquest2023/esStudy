package es.board.filter;

import es.board.config.jwt.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class JwtUserResolverFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwt;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest req) {
        String uri = req.getRequestURI();
        return uri.startsWith("/assets")
                || uri.startsWith("/static")
                || uri.startsWith("/actuator/health");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        long s = System.nanoTime();
        String auth = req.getHeader("Authorization");
        String uid ="";
        if (auth != null && auth.startsWith("Bearer ")) {
            uid = jwt.getUserId(auth.substring(7));
        }
        req.setAttribute("userId", uid);
        try {
            chain.doFilter(req, res);
        }finally {
            long ms = (System.nanoTime()-s)/1_000_000;
            log.info("[RT] {} {} {}ms", req.getMethod(), req.getRequestURI(), ms);
        }
    }
}