package es.board.controller;

import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.res.SignUpResponse;
import es.board.repository.document.Comment;
import es.board.service.CommentService;
import es.board.service.FeedService;
import es.board.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@Slf4j
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
                log.info("commentList={}",commentList.toString());
                Map<String, Object> response = Map.of(
                        "commentList",commentList
                );

                return ResponseEntity.ok(response);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
    }



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
                        "userId",userId,
                        "visitCount", userService.findVisitCount(userId));
                return ResponseEntity.ok(response);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
    }

    @PostMapping("/signup/pass")
    public String signIn(@ModelAttribute SignUpResponse sign, Model model) {
        boolean isIdAvailable = userService.checkId(sign);
        log.info("아이디 중복 여부: " + isIdAvailable);

        if (isIdAvailable) {
            userService.createUser(sign);
            return "redirect:/login";
        } else {
            model.addAttribute("errorButton", false);
            model.addAttribute("error", "아이디가 중복됩니다.");
            model.addAttribute("signUpResponse", sign);
            return "basic/login/SignUp";
        }
    }

    @PostMapping("/check")
    public ResponseEntity<Boolean> checkUserId(@RequestBody SignUpResponse sign) {
        boolean isAvailable = userService.checkId(sign);
        log.info(String.valueOf(isAvailable));
        return ResponseEntity.ok(isAvailable); // true: 사용 가능, false: 중복
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
