package es.board.service.impl;

import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.jwt.JwtToken;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(JwtTokenProvider jwtTokenProvider) {

        this.jwtTokenProvider = jwtTokenProvider;
    }

    public JwtToken refreshAccessToken(String refreshToken) {
        if (jwtTokenProvider.validateToken(refreshToken)) {
            // Refresh Token에서 사용자 ID 추출
            String userId = Jwts.parserBuilder()
                    .build()
                    .parseClaimsJws(refreshToken)
                    .getBody()
                    .getSubject();

            // Authentication 객체 생성 (권한 정보는 비워둠)
            Authentication authentication = new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());

            // 새로운 Access Token 생성
            return jwtTokenProvider.generateToken(authentication,userId);
        } else {
            throw new RuntimeException("Invalid Refresh Token");
        }
    }
}