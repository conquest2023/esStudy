package es.board.config.jwt;

import es.board.model.jwt.JwtToken;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {
    private  final Key key;
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }
//    public JwtToken createAccessToken(LoginResponse login){
//
//        return  generateToken(login);
//    }
private final Set<String> blacklistedTokens = new HashSet<>();

    // 블랙리스트에 토큰 추가
    public void addToBlacklist(String token) {
        blacklistedTokens.add(token);
    }

    // 토큰이 블랙리스트에 있는지 확인
    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }
    //    Authentication authentication
    public JwtToken generateToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        long now = (new Date()).getTime();
        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + 864000);
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth",authorities)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken= Jwts.builder()
                .setExpiration(new Date(now + 864000000))
                .signWith(key,SignatureAlgorithm.HS256)
                .compact();

        return JwtToken.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public String  generateTokenId(String  userId) {
        long now = (new Date()).getTime();
        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + 864000);
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    public String createRefreshToken(String userId) {
        Claims claims = Jwts.claims().setSubject(userId);

        long now = (new Date()).getTime();
        Date refreshTokenExpiresIn = new Date(now + 86400000);

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(refreshTokenExpiresIn)
                .signWith(key,SignatureAlgorithm.HS256)
                .compact();
    }

    public  String getUserId(String token){
        return  parseClaims(token).get("loginId",String.class);
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);
        if (claims.get("auth")==null) {
            throw new RuntimeException("유효하지 않은 토큰: 사용자 정보가 없습니다.");
        }
        String rawAuth = claims.get("auth") != null ? claims.get("auth").toString() : "ROLE_GUEST"; // 기본 권한 추가
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(rawAuth.split(","))
                .filter(role -> !role.isBlank()) // 빈 문자열 필터링
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);


//         String username = Jwts.parser()
//                .setSigningKey(key)
//                .parseClaimsJws(accessToken)
//                .getBody()
//                .getSubject();

//       UserDetails principal = new User(claims.getSubject(), "", Collections.emptyList());

    }
    public boolean blackListValidateToken(String token) {
        if (isTokenBlacklisted(token)) {
            System.out.println("[DEBUG] 블랙리스트에 등록된 토큰: " + token);
            return false; // 블랙리스트에 있으면 무효화
        }
        // 기존 유효성 검사 로직
        return true;
    }
    // 토큰 정보를 검증하는 메서드
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }
    // accessToken
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

}
