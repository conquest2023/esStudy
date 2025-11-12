package es.board.controller;

import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.dto.feed.LoginDTO;
import es.board.controller.model.dto.feed.SignUpDTO;
import es.board.controller.model.jwt.JwtToken;
import es.board.service.AuthService;
import lombok.RequiredArgsConstructor;
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
    @PostMapping("/authlogin")
    @ResponseBody
    public ResponseEntity<?> loginPass(@RequestBody LoginDTO response) {
        if (!userService.login(response)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "아이디 또는 비밀번호가 잘못되었습니다."));
        }
        Authentication authentication = userService.authenticate(response);
        JwtToken token = jwtTokenProvider.generateToken(authentication, response.getUserId());
//        userService.updateVisitCount(response.getUserId());
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
