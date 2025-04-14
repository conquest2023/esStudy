package es.board.controller;


import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.jwt.JwtToken;
import es.board.controller.model.res.LoginResponse;
import es.board.controller.model.res.OAuthSignUp;

import es.board.service.AuthService;
import es.board.service.OAuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;


@Slf4j
@RestController
@RequiredArgsConstructor
public class OAuthLoginController {

    private  final OAuthService authService;

    private  final AuthService userService;

    private  final JwtTokenProvider jwtTokenProvider;

    @Value("${kakao.client.id}")
    private String clientId;
    @Value("${kakao.redirect.uri}")
    private String redirectUri;

    @Value("${naver.client.id}")
    private String naverClientId;
    @Value("${naver.redirect.uri}")
    private String navarRedirectUri;

    @Value("${google.client.id}")
    private String googleClientId;
    @Value("${google.redirect.uri}")
    private String googleRedirectUri;

    @GetMapping("/naver")
    public ResponseEntity<Void> naverLoginRedirect(HttpSession session) {
        String state = UUID.randomUUID().toString();
        session.setAttribute("oauth_state", state);
        String url = "https://nid.naver.com/oauth2.0/authorize?" +
                "response_type=code" +
                "&client_id=" + naverClientId +
                "&redirect_uri=" + navarRedirectUri +
                "&state=" + state;
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(url));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
    @GetMapping("/naver/callback/json")
    public ResponseEntity<?> naverCallback(@RequestParam String code,
                                           @RequestParam String state,
                                           HttpSession session) {
        String savedState = (String) session.getAttribute("oauth_state");
        if (!state.equals(savedState)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("state 불일치");
        }
        try {
            Map<String, Object> response = authService.naverLogin(code,state);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("에러 발생");
        }
    }
    @GetMapping("/google")
    public ResponseEntity<Void> googleLoginRedirect() {
        String url = "https://accounts.google.com/o/oauth2/v2/auth?" +
                "client_id=" + googleClientId +
                "&redirect_uri=" + googleRedirectUri +
                "&response_type=code" +
                "&scope=openid%20profile%20email" +
                "&access_type=offline";
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(url));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
    @GetMapping("/google/callback/json")
    public ResponseEntity<?> googleCallback(@RequestParam String code) {
        try {
            Map<String, Object> response = authService.googleLogin(code);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("에러 발생");
        }
    }
    @GetMapping("/kakao")
    public ResponseEntity<Void> kakaoLoginRedirect() {
        String url = "https://kauth.kakao.com/oauth/authorize?" +
                "client_id=" + clientId + "&redirect_uri=" + redirectUri + "&response_type=code";
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(url));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
    @GetMapping("/kakao/callback/json")
    public ResponseEntity<Map<String, Object>> kakaoCallbackJson(@RequestParam String code) {
        try {
            Map<String, Object> response = authService.kakaoLogin(code);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "서버 에러 발생"));
        }
    }
    @PostMapping("/oauth/username")
    public ResponseEntity<?> createUsername(@RequestBody OAuthSignUp sign) {
        authService.saveUserName(sign);
        return ResponseEntity.ok(Map.of("result", "ok", "redirect", "/"));
        }
    @PostMapping("/oauth/login")
    @ResponseBody
    public ResponseEntity<?> loginPass(@RequestBody LoginResponse response) {
        Authentication authentication = authService.authenticate(response);
        JwtToken token = jwtTokenProvider.generateToken(authentication, response.getUserId());
        userService.updateVisitCount(response.getUserId());
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", token.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Lax")
                .maxAge(Duration.ofDays(7))
                .build();
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(Map.of(
                        "accessToken", token.getAccessToken(),
                        "username", authentication.getName(),
                        "isLoggedIn", true
                ));
    }
}
