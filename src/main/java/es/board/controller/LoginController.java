package es.board.controller;

import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.dto.feed.LoginDTO;
import es.board.controller.model.dto.feed.SignUpDTO;
import es.board.controller.model.jwt.JwtToken;
import es.board.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LoginController {

    private final AuthService userService;

    private  final StringRedisTemplate redis;

    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signup/pass")
    @ResponseBody
    public ResponseEntity<?> signIn(@RequestBody SignUpDTO sign) {
        boolean isIdAvailable = userService.checkId(sign);
        if (isIdAvailable) {
            userService.registerUser(sign);
            return ResponseEntity.ok("회원가입 완료");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("아이디가 중복됩니다.");
        }
    }

    @PostMapping("/authlogout")
    @ResponseBody
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            jwtTokenProvider.addToBlacklist(token);
//            log.info("[DEBUG] 블랙리스트에 추가된 토큰: " + token);
        }
        return ResponseEntity.ok(Map.of(
                "message", "로그아웃되었습니다.",
                "isLoggedIn", false));
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
    @PostMapping("/authlogin")
    @ResponseBody
    public ResponseEntity<?> login(@RequestBody LoginDTO response) {
        if (!userService.login(response)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "아이디 또는 비밀번호가 잘못되었습니다."));
        }
        Authentication authentication = userService.authenticate(response);
        JwtToken token = jwtTokenProvider.generateToken(authentication, response.getUserId());
        if(response.isAutoLogin()){
            userService.autoLogin(response.getUserId(),token.getRefreshToken());
        }
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", token.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Lax")
                .maxAge(Duration.ofDays(7))
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(Map.of(
                        "accessToken", token.getAccessToken(),
                        "username", authentication.getName(),
                        "isLoggedIn", true
                ));
    }
}
