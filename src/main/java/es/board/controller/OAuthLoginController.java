package es.board.controller;


import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.jwt.JwtToken;
import es.board.controller.model.res.LoginResponse;
import es.board.controller.model.res.OAuthSignUp;

import es.board.service.AuthService;
import es.board.service.OAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;


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
            Map<String, Object> response = authService.login(code);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "서버 에러 발생"));
        }
    }
    @PostMapping("/oauth/username")
    public ResponseEntity<?> createUsername(@RequestBody OAuthSignUp sign) {
        log.info(sign.toString());
        authService.saveUserName(sign);
        return ResponseEntity.ok(Map.of("result", "ok", "redirect", "/"));
        }

    @PostMapping("/oauth/login")
    @ResponseBody
    public ResponseEntity<?> loginPass(@RequestBody LoginResponse response) {
        Authentication authentication = authService.authenticate(response);
        JwtToken token = jwtTokenProvider.generateToken(authentication, response.getUserId());
        userService.updateVisitCount(response.getUserId());
        return ResponseEntity.ok(Map.of(
                "accessToken", token.getAccessToken(),
                "refreshToken", token.getRefreshToken(),
                "username", authentication.getName(),
                "isLoggedIn", true
        ));
    }
}
