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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommonController {


    private final JwtTokenProvider jwtTokenProvider;


    private  final RedisTemplate<String, Object> redisTemplate;


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

    @GetMapping("/ping-empty")
    public ResponseEntity<Void> pingEmpty() {
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/ping")
    public ResponseEntity<?> test(){

        return ResponseEntity.ok(Map.of("ok","ok"));
    }

    @GetMapping("/get-ip")
    public ResponseEntity<?> getClientIp() {
        Set<String> activeUsers = redisTemplate.keys("online_users:*");
        ZoneId KST = ZoneId.of("Asia/Seoul");
        LocalDateTime now = LocalDateTime.now(KST);
        LocalDate today = now.toLocalDate();
//        "data", visitService.getStats())
        Long size = redisTemplate.opsForSet().size("visit:unique:" + today);
        return ResponseEntity.ok(Map.of(
                "activeUsers", activeUsers.size(),
                "today",size));
    }


}
