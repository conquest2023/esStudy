package es.board.filter;

import es.board.config.jwt.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtTokenProvider jwtTokenProvider;
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        String token=resolveToken((HttpServletRequest)request);
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String authorizationHeader = httpRequest.getHeader("Authorization");


        if(token !=null&& jwtTokenProvider.validateToken(token)){
            if (jwtTokenProvider.isTokenBlacklisted(token)) {
                log.info("[DEBUG] 블랙리스트에 등록된 토큰 요청 차단: " + token);
                ((HttpServletResponse)response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                ((HttpServletResponse)response).getWriter().write("Unauthorized: Token is invalid or expired");
                return;
            }
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
    }
        chain.doFilter(request, response);
}


    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            return null; // 토큰이 없거나 잘못된 경우 처리
        }

        if (bearerToken.length() < 8) { // "Bearer " 포함 최소 길이 검사
            return null;
        }

        return bearerToken.substring(7); // "Bearer " 제거 후 토큰 반환
    }
}