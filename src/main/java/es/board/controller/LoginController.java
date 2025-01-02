package es.board.controller;

import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.jwt.JwtToken;
import es.board.controller.model.res.LoginResponse;
import es.board.controller.model.res.SignUpResponse;
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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
public class LoginController {

    private final int page = 0;

    private final int size = 10;

    private final FeedService feedService;

    private final CommentService commentService;

    private final UserService userService;

    private final JwtTokenProvider jwtTokenProvider;
    @GetMapping("/api/user-info")
    @ResponseBody
    public ResponseEntity<?> getUserInfo(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

            if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // "Bearer " 이후의 토큰만 추출
            if (jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.ok(Map.of(
                        "userId",jwtTokenProvider.getUserId(token),
                        "username",jwtTokenProvider.getUsername(token),
                        "isLoggedIn", true));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "error", "세션이 만료되었습니다."
        ));
    }
    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
//        log.info(token);
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            jwtTokenProvider.addToBlacklist(token); // 토큰 블랙리스트 처리
            log.info("[DEBUG] 블랙리스트에 추가된 토큰: " + token);
        }
        return ResponseEntity.ok(Map.of(
                "message", "로그아웃되었습니다.",
                "isLoggedIn", false));
    }


    @GetMapping("/login")
    public String login() {
        return "basic/login/Login";
    }

    @GetMapping("/signup")
    public String signUp() {

        return "basic/login/SignUp";
    }

    @PostMapping("/signup/pass")
    public String signIn(@ModelAttribute SignUpResponse sign, Model model) {
        boolean isIdAvailable = userService.checkId(sign);
        log.info("아이디 중복 여부: " + isIdAvailable);

        if (isIdAvailable) {
            userService.createUser(sign);
            return "redirect:/login"; // 아이디가 사용 가능하면 로그인 페이지로 리다이렉트
        } else {
            model.addAttribute("errorButton", false); // 중복된 경우 false 전달
            model.addAttribute("error", "아이디가 중복됩니다.");
            model.addAttribute("signUpResponse", sign);
            return "basic/login/SignUp"; // 다시 회원가입 페이지로 이동
        }
    }

    @PostMapping("/check")
    public ResponseEntity<Boolean> checkUserId(@RequestBody SignUpResponse sign) {
        boolean isAvailable = userService.checkId(sign);
        log.info(String.valueOf(isAvailable));
        return ResponseEntity.ok(isAvailable); // true: 사용 가능, false: 중복
    }


    @PostMapping("/login/pass")
    public String loginPass(Model model, LoginResponse response) {
        if (!userService.login(response)) {
            model.addAttribute("error", "아이디 또는 비밀번호가 잘못되었습니다.");
            return "basic/login/Login"; // 로그인 화면 렌더링
        }
        Authentication authentication = userService.authenticate(response);
        JwtToken token = jwtTokenProvider.generateToken(authentication,response.getUserId());

        feedMain(model); // 메인 화면 렌더링 메서드 호출
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("accessToken", token.getAccessToken()); // 필요 시 토큰을 뷰로 전달
        return "basic/feed/feedList"; // 메인 화면 렌더링
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> loginPass(@RequestBody LoginResponse response) {

        if (!userService.login(response)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "아이디 또는 비밀번호가 잘못되었습니다."));
        }
//        String refreshToken = jwtTokenProvider.createRefreshToken(response.getUserId());
        Authentication authentication = userService.authenticate(response);
        JwtToken token = jwtTokenProvider.generateToken(authentication, response.getUserId());


//        log.info("Refresh Token 생성: {}", refreshToken);
        log.info("Access Token 생성: {}", token);

        return ResponseEntity.ok(Map.of(
                "accessToken", token.getAccessToken(),
                "refreshToken", token.getRefreshToken(),
                "username", authentication.getName(),
                "isLoggedIn", true
        ));
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 Refresh Token입니다.");
        }
        String userId = jwtTokenProvider.getUserId(refreshToken);
        String newAccessToken = jwtTokenProvider.generateTokenId(userId);

        return ResponseEntity.ok(Map.of(
                "accessToken", newAccessToken
        ));
    }

    private void feedMain(Model model){
        int maxPage = (int) Math.ceil((double) feedService.getTotalPage(page,size) / size);
        int totalPage=(int) Math.ceil( feedService.getTotalFeed());
        model.addAttribute("viewCount",feedService.getViewCountAll());
        model.addAttribute("count",commentService.getPagingComment(feedService.getfeedUIDList(page,size),page,size));
        model.addAttribute("page", page);  // 현재 페이지 번호
        model.addAttribute("maxPage", maxPage);
        model.addAttribute("totalPage", totalPage);
        model.addAttribute("data", feedService.getPagingFeed(page, size)); // 서비스 호출 시 페이지와 크기 전달
        model.addAttribute("month",feedService.getMonthPopularFeed());
    }
}
