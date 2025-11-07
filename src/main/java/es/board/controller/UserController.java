package es.board.controller;

import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.jwt.JwtToken;
import es.board.controller.model.dto.feed.LoginDTO;
import es.board.controller.model.dto.feed.SignUpDTO;
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

//    @GetMapping("/someone/feed/comment/paging")
//    public ResponseEntity<?> getSomeoneCommentAndFeed(HttpServletRequest request,@RequestParam(defaultValue = "0") int page,
//                                                     @RequestParam(defaultValue = "10") int size,String username) {
//        String token = request.getHeader("Authorization");
//        if (token != null && token.startsWith("Bearer ")) {
//            token = token.substring(7);
//            if (jwtTokenProvider.validateToken(token)) {
//                String userId = userService.findById(username);
//                Map<String, Object> response = Map.of(
//                        "commentAndFeed",commentService.getFeedAndCommentMyPage(userId,page,size));
//                return ResponseEntity.ok(response);
//            }
//        }
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
//    }
//
//    @GetMapping("/someone/feed/paging")
//    public ResponseEntity<?> getSomeoneFeed(HttpServletRequest request,@RequestParam(defaultValue = "0") int page,
//                                       @RequestParam(defaultValue = "10") int size,String username) {
//        String token = request.getHeader("Authorization");
//        if (token != null && token.startsWith("Bearer ")) {
//            token = token.substring(7);
//            if (jwtTokenProvider.validateToken(token)) {
//                String userId = userService.findById(username);
//                Map<String,Object> feedLists=feedService.getFeedUserList(userId,page,size);
//                Map<String, Object> response = Map.of(
//                        "feedList",  feedLists.get("boardList"),
//                        "username", jwtTokenProvider.getUsername(token)
//                );
//
//                return ResponseEntity.ok(response);
//            }
//        }
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
//    }
//
//    @GetMapping("/someone/comment/paging")
//    public ResponseEntity<?> getSomeoneComment(HttpServletRequest request,@RequestParam(defaultValue = "0") int page,
//                                              @RequestParam(defaultValue = "10") int size,String username) {
//        String token = request.getHeader("Authorization");
//        if (token != null && token.startsWith("Bearer ")) {
//            token = token.substring(7);
//            if (jwtTokenProvider.validateToken(token)) {
//                String userId = userService.findById(username);
//                List<CommentEntity> commentList=commentService.getMyPageComment(userId,page,size);
//                Map<String, Object> response = Map.of(
//                        "commentList",commentList
//                );
//
//                return ResponseEntity.ok(response);
//            }
//        }
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
//    }
    @GetMapping("/someone/profile/full")
    public ResponseEntity<?> getFullSomeoneInfo(HttpServletRequest request,
                                            @RequestParam String username,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size) {

    String token = request.getHeader("Authorization");
    if (token != null && token.startsWith("Bearer ")) {
        token = token.substring(7);
        if (jwtTokenProvider.validateToken(token)) {
            String userId = userService.findById(username);
            if (userId == null) return ResponseEntity.notFound().build();

            Map<String, Object> boardStats = feedService.getUserMapageLikeAndFeedCount(userId);
            Map<String,Object> commentStats = commentService.getUserComments(userId);
            int point = Optional.ofNullable(feedService.getPointAll(userId)).orElse(0);
            Map<String,Object> feedList  = feedService.getFeedUserList(userId, page, size);
            List<Comment> commentList    = commentService.getMyPageComment(userId, page, size);
            List<?> commentAndFeed       = commentService.getFeedAndCommentMyPage(userId, page, size);
            Map<String, Object> response = Map.of(
                    "user", Map.of(
                            "like", boardStats.get("totalLikes"),
                            "commentCount", commentStats.get("totalComments"),
                            "feedCount",  boardStats.get("totalBoards"),
                            "point", point,
                            "username", username,
                            "visitCount", userService.findVisitCount(userId)
                    ),
                    "feedList", feedList.get("boardList"),
                    "commentList", commentList,
                    "commentAndFeed", commentAndFeed
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
    @GetMapping("/user/profile")
    public ResponseEntity<?> getSomeoneInfo(HttpServletRequest request,String username) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
//                String userId = userService.findById(username);
//                Map<String, Object> boardStats = feedService.getUserMapageLikeAndFeedCount(userId);
//                Map<String,Object> commentStats= commentService.getUserComments(userId);
//                int point = Optional.ofNullable(feedService.getPointAll(userId)).orElse(0);
//                Map<String, Object> response = Map.of(
//                        "like", boardStats.get("totalLikes"),
//                        "commentCount", commentStats.get("totalComments"),
//                        "feedCount",  boardStats.get("totalBoards"),
//                        "point",point,
//                        "username",username,
//                        "visitCount", userService.findVisitCount(userId));
                return ResponseEntity.ok("ok");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
    }


    @GetMapping("/mypage")
    public ResponseEntity<?> getMyPageInfo(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
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
    public ResponseEntity<?> signIn(@RequestBody SignUpDTO sign) {
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
    public ResponseEntity<?> loginPass(@RequestBody LoginDTO response) {
        if (!userService.login(response)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "아이디 또는 비밀번호가 잘못되었습니다."));
        }
        Authentication authentication = userService.authenticate(response);
        JwtToken token = jwtTokenProvider.generateToken(authentication, response.getUserId());
        userService.updateVisitCount(response.getUserId());
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
    @PostMapping("/check")
    public ResponseEntity<Boolean> checkUserId(@RequestBody SignUpDTO sign) {
        boolean isAvailable = userService.checkId(sign);
        return ResponseEntity.ok(isAvailable);
    }


//    @PostMapping("/login/pass")
//    public String loginPass(Model model, LoginDTO response) {
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
