package es.board.controller;

import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.jwt.JwtToken;
import es.board.controller.model.res.LoginResponse;
import es.board.service.CommentService;
import es.board.service.FeedService;
import es.board.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class FeedController {


    private  final UserService userService;

    private  final JwtTokenProvider jwtTokenProvider;

    private  final FeedService feedService;



    private  final CommentService commentService;


    @PostMapping("/authlogout")
    @ResponseBody
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        log.info(token);
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            jwtTokenProvider.addToBlacklist(token); // 토큰 블랙리스트 처리
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
        log.info("pass={}",token);
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.ok(Map.of(
                        "feedCount",(int) feedService.getUserFeedCount(jwtTokenProvider.getUserId(token)),
                        "commentCount",(int)  commentService.getUserCommentCount(jwtTokenProvider.getUserId(token)),
                        "visitCount",userService.findVisitCount(jwtTokenProvider.getUserId(token)),
                        "userId",jwtTokenProvider.getUserId(token),
                        "username",jwtTokenProvider.getUsername(token),
                        "isLoggedIn", true));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "error", "세션이 만료되었습니다."
        ));

    }

    @PostMapping("/authlogin")
    @ResponseBody
    public ResponseEntity<?> loginPass(@RequestBody LoginResponse response) {

        if (!userService.login(response)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "아이디 또는 비밀번호가 잘못되었습니다."));
        }
        userService.updateVisitCount(response.getUserId());
        Authentication authentication = userService.authenticate(response);
        JwtToken token = jwtTokenProvider.generateToken(authentication, response.getUserId());
        return ResponseEntity.ok(Map.of(
                "accessToken", token.getAccessToken(),
                "refreshToken", token.getRefreshToken(),
                "username", authentication.getName(),
                "isLoggedIn", true
        ));
    }
    @PostMapping("/auth/refresh")
    @ResponseBody
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 Refresh Token입니다.");
        }
        String userId = jwtTokenProvider.getUserId(refreshToken);
        String username=userService.getUsername(userId);
        String newAccessToken = jwtTokenProvider.generateAccessToken("ROLE_USER",userId,username);
        log.info("newAccessToken=={}",newAccessToken);
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }




}
