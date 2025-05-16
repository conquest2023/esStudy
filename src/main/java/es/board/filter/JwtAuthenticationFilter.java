//package es.board.filter;
//
//import es.board.config.jwt.JwtTokenProvider;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.ServletRequest;
//import jakarta.servlet.ServletResponse;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.util.StringUtils;
//import org.springframework.web.filter.GenericFilterBean;
//
//import java.io.IOException;
//
//@RequiredArgsConstructor
//@Slf4j
//public class JwtAuthenticationFilter extends GenericFilterBean {
//    private final JwtTokenProvider jwtTokenProvider;
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//        HttpServletRequest httpRequest = (HttpServletRequest) request;
//        String uri = httpRequest.getRequestURI();
//        log.info("hello={}",uri);
//
//        if (uri.startsWith("/css/") || uri.startsWith("/js/") ||
//                uri.startsWith("/images/") || uri.equals("/favicon.ico")) {
//            chain.doFilter(request, response);
//            return;
//        }
//
//        String token=resolveToken((HttpServletRequest)request);
//        if(token !=null&& jwtTokenProvider.validateToken(token)){
//            if (jwtTokenProvider.isTokenBlacklisted(token)) {
//                log.info("[DEBUG] 블랙리스트에 등록된 토큰 요청 차단: " + token);
//                ((HttpServletResponse)response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                ((HttpServletResponse)response).getWriter().write("Unauthorized: Token is invalid or expired");
//                return;
//            }
//            Authentication authentication = jwtTokenProvider.getAuthentication(token);
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//        }
//        chain.doFilter(request, response);
//    }
//
//
//    private String resolveToken(HttpServletRequest request) {
//        String bearerToken = request.getHeader("Authorization");
//        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
//            return null;
//        }
//
//        if (bearerToken.length() < 8) {
//            return null;
//        }
//
//        return bearerToken.substring(7);
//    }
//}