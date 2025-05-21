package es.board.controller;

import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.jwt.JwtToken;
import es.board.controller.model.res.LoginResponse;
import es.board.controller.model.res.SignUpResponse;
import es.board.repository.document.Comment;
import es.board.service.CommentService;
import es.board.service.FeedService;
import es.board.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@Slf4j
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {



    private final FeedService feedService;

    private final CommentService commentService;

    private final AuthService userService;

    private final JwtTokenProvider jwtTokenProvider;



    @GetMapping("/login")
    public String login() {

        return "basic/login/Login";
    }

    @GetMapping("/signup")
    public String signUp() {

        return "basic/login/SignUp";
    }
    @GetMapping("/search/view/feed/list/page")
    public  String test(){

        return "basic/feed/Mypage";
    }

    @GetMapping("/mypage/feed/comment/paging")
    public ResponseEntity<?> getCommentAndFeedMyPage(HttpServletRequest request,@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                String userId = jwtTokenProvider.getUserId(token);
                Map<String, Object> response = Map.of(
                        "commentAndFeed",commentService.getFeedAndCommentMyPage(userId,page,size));
                return ResponseEntity.ok(response);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
    }

    @GetMapping("/mypage/feed/paging")
    public ResponseEntity<?> getMyPage(HttpServletRequest request,@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                String userId = jwtTokenProvider.getUserId(token);
                Map<String,Object> feedLists=feedService.getFeedUserList(userId,page,size);
                Map<String, Object> response = Map.of(
                        "feedList",  feedLists.get("boardList"),
                        "username", jwtTokenProvider.getUsername(token)
                );

                return ResponseEntity.ok(response);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
    }

    @GetMapping("/mypage/comment/paging")
    public ResponseEntity<?> getCommentMyPage(HttpServletRequest request,@RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                String userId = jwtTokenProvider.getUserId(token);
                List<Comment> commentList=commentService.getMyPageComment(userId,page,size);
                Map<String, Object> response = Map.of(
                        "commentList",commentList
                );

                return ResponseEntity.ok(response);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
    }
//    @PostMapping("/auth/refresh")
//    @ResponseBody
//    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
//        Cookie[] cookies = request.getCookies();
//        if (cookies == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//
//        String refreshToken = Arrays.stream(cookies)
//                .filter(c -> c.getName().equals("refreshToken"))
//                .map(Cookie::getValue)
//                .findFirst()
//                .orElse(null);
//
//        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//
//        Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);
//        String userId = jwtTokenProvider.getUserId(refreshToken);
//        JwtToken newToken = jwtTokenProvider.generateToken(authentication, userId);
//
//        return ResponseEntity.ok(Map.of(
//                "accessToken", newToken.getAccessToken()
//        ));
//    }



    @GetMapping("/mypage")
    public ResponseEntity<?> getMyPageInfo(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        log.info(token);
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                String userId = jwtTokenProvider.getUserId(token);
                Map<String, Object> boardStats = feedService.getUserMapageLikeAndFeedCount(userId);
                Map<String,Object> commentStats= commentService.getUserComments(userId);
                int point = Optional.ofNullable(feedService.getPointAll(userId)).orElse(0);
                Map<String, Object> response = Map.of(
                        "like", boardStats.get("totalLikes"),
                        "commentCount", commentStats.get("totalComments"),
                        "feedCount",  boardStats.get("totalBoards"),
                        "point",point,
                        "username",jwtTokenProvider.getUsername(token),
//                        "userId",userId,
                        "visitCount", userService.findVisitCount(userId));
                return ResponseEntity.ok(response);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
    }

    @PostMapping("/signup/pass")
    @ResponseBody
    public ResponseEntity<?> signIn(@RequestBody SignUpResponse sign) {
        boolean isIdAvailable = userService.checkId(sign);
        if (isIdAvailable) {
            userService.createUser(sign);
            return ResponseEntity.ok("회원가입 완료");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("아이디가 중복됩니다.");
        }
    }
    @PostMapping("/authlogin")
    @ResponseBody
    public ResponseEntity<?> loginPass(@RequestBody LoginResponse response) {
        if (!userService.login(response)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "아이디 또는 비밀번호가 잘못되었습니다."));
        }

        Authentication authentication = userService.authenticate(response);
        JwtToken token = jwtTokenProvider.generateToken(authentication, response.getUserId());
        userService.updateVisitCount(response.getUserId());
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
        @PostMapping("/check")
    public ResponseEntity<Boolean> checkUserId(@RequestBody SignUpResponse sign) {
        boolean isAvailable = userService.checkId(sign);
        return ResponseEntity.ok(isAvailable);
    }


//    @PostMapping("/login/pass")
//    public String loginPass(Model model, LoginResponse response) {
//        log.info("Gasdsadsa");
//        if (!userService.login(response)) {
//            model.addAttribute("error", "아이디 또는 비밀번호가 잘못되었습니다.");
//            return "basic/login/Login"; // 로그인 화면 렌더링
//        }
//        log.info("Gasdsadsa");
//        userService.updateVisitCount(response.getUserId());
//        Authentication authentication = userService.authenticate(response);
//        JwtToken token = jwtTokenProvider.generateToken(authentication,response.getUserId());
//        feedMain(model);
//        model.addAttribute("isLoggedIn", true);
//        model.addAttribute("accessToken", token.getAccessToken()); // 필요 시 토큰을 뷰로 전달
//        return "basic/feed/feedList"; // 메인 화면 렌더링
//    }



}
