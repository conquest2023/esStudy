package es.board.controller.feed;

import es.board.config.jwt.JwtTokenProvider;
import es.board.ex.TokenValidator;
import es.board.service.VisitorService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

@RestController
@Slf4j
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommonController {


    private  final VisitorService visitService;


    private final JwtTokenProvider jwtTokenProvider;


    private  final StringRedisTemplate redis;


    private  final RedisTemplate<String, Object> redisTemplate;

    @PostMapping("/authlogout")
    @ResponseBody
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            jwtTokenProvider.addToBlacklist(token);
            log.info("[DEBUG] 블랙리스트에 추가된 토큰: " + token);
        }
        return ResponseEntity.ok(Map.of(
                "message", "로그아웃되었습니다.",
                "isLoggedIn", false));
    }
    @GetMapping("/info")
    @ResponseBody
    public ResponseEntity<?> getUserInfo(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.ok(Map.of(
//                        "userId",jwtTokenProvider.getUserId(token),
                        "username",jwtTokenProvider.getUsername(token),
                        "isLoggedIn", true));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "error", "세션이 만료되었습니다."
        ));
    }


    @GetMapping("/get-ip")
    public ResponseEntity<?> getClientIp() {
        Set<String> activeUsers = redisTemplate.keys("online_users:*");
//        List<String> rawKeys = new ArrayList<>(redisTemplate.keys("visit*"));
//        List<String> todayKeys = new ArrayList<>();
//        todayAggregation(rawKeys, todayKeys);
//        log.info("오늘 자정까지 유효한 방문자 수: {}", todayKeys.size());
        return ResponseEntity.ok(Map.of(
                "activeUsers", activeUsers.size(),
                "data", visitService.getStats()));
    }

    @GetMapping("/auto/login")
    public ResponseEntity<?> autoLogin(
            @CookieValue(value = "refreshToken", required = false) String refreshToken) {
        if (refreshToken != null) {
            String userId = jwtTokenProvider.getUserId(refreshToken);
            String storedRefresh = redis.opsForValue().get("RT:" + userId);
            if (storedRefresh != null && storedRefresh.equals(refreshToken)) {
                String newAccessToken = jwtTokenProvider.generateAccessToken("user", jwtTokenProvider.getUsername(refreshToken), userId);
                return ResponseEntity.ok().header("Authorization", "Bearer " + newAccessToken).body("자동 로그인 성공!");
            }
        }
        return ResponseEntity.status(401).body("다시 로그인 필요");
    }

}
