package es.board.config.jwt;

import es.board.controller.model.jwt.JwtToken;
import es.board.ex.TokenInvalidException;
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
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {
    private  final Key key;

    private final long ACCESS_TOKEN_EXPIRY = 24 * 60 * 60 * 1000;
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }
    private final Set<String> blacklistedTokens = new HashSet<>();

    // 블랙리스트에 토큰 추가
    public void addToBlacklist(String token) {
        blacklistedTokens.add(token);
    }

    // 토큰이 블랙리스트에 있는지 확인
    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }


    public JwtToken generateToken(Authentication authentication,String userId) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        long now = (new Date()).getTime();
        // Access Token 생성
        String accessToken = Jwts.builder()
                .setSubject(userId) // "sub"에 userId 저장
                .claim("username", authentication.getName()) // "username" 클레임에 이름 저장
                .claim("auth", authorities) // 권한 정보 저장
//                .claim("created_at", LocalDate.now().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRY))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        Date refreshTokenExpiresIn = new Date(now + 1000L * 60 * 60 * 24 * 30);

        String refreshToken= Jwts.builder()
                .setSubject(userId)
                .claim("type", "refresh")
                .claim("username", authentication.getName())
                .setExpiration(refreshTokenExpiresIn)
                .signWith(key,SignatureAlgorithm.HS256)
                .compact();
        return JwtToken.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public String generateAccessToken(String authority, String  userId, String  username) {
        long now = (new Date()).getTime();
        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + 864000000);
        return Jwts.builder()
                .setSubject(userId)
                .claim("username", username)
                .claim("auth", authority)
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
    public String getUsername(String token) {
        return parseClaims(token).get("username", String.class); // "username" 클레임에서 값 추출
    }

    public  String getUserId(String token){
        return  parseClaims(token).get("sub",String.class);
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

    }
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token");
            return false;
        } catch (Exception e) {
            log.error("Invalid JWT Token", e);
            return false;
        }
    }
    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (JwtException e) {
            throw new TokenInvalidException("유효하지 않은 토큰입니다.");
        }
    }

}
