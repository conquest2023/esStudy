package es.board.controller;

import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.jwt.JwtToken;
import es.board.controller.model.req.CommentRequest;
import es.board.controller.model.req.FeedRequest;
import es.board.controller.model.res.LoginResponse;
import es.board.service.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AjaxController {


    private  final AuthService userService;

    private  final JwtTokenProvider jwtTokenProvider;

    private  final VisitorService visitService;

    private  final FeedService feedService;

    private  final ReplyService replyService;

    private  final CommentService commentService;

    @GetMapping("/get-ip")
    @ResponseBody
    public Map<String, Long> getClientIp() {
        return visitService.getStats();
    }
    @PostMapping("/increaseViewCount")
    public ResponseEntity<?> increaseViewCount(@RequestBody Map<String, String> request, HttpServletResponse response,
                                               @CookieValue(value = "viewedFeeds", defaultValue = "") String viewedFeeds) {
        String id = request.get("id");

        // 쿠키에서 해당 게시글 조회 여부 확인
        if (!viewedFeeds.contains(id)) {
            feedService.saveViewCountFeed(id);

            // 새 쿠키 설정 (30분 동안 유지)
            String updatedFeeds = viewedFeeds.isEmpty() ? id : viewedFeeds + ";" + id;
            String encodedValue = URLEncoder.encode(updatedFeeds, StandardCharsets.UTF_8);
            Cookie cookie = new Cookie("viewedFeeds", encodedValue);
            cookie.setHttpOnly(true);  // 클라이언트 스크립트에서 접근 방지
            cookie.setSecure(true);    // HTTPS에서만 전송 (운영 환경 고려)
            cookie.setPath("/");        // 전체 도메인에서 쿠키 유효
            cookie.setMaxAge(60 * 30);  // 30분 유지
            response.addCookie(cookie);
        }

        return ResponseEntity.ok("조회수 증가 성공");
    }
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
        ));}
    @GetMapping("/detail")
    public ResponseEntity<?> getDetailInfo(HttpServletRequest request,
    @RequestParam(value = "id") String feedUID) {
        Map<String, Object> response = new HashMap<>();
        response.put("replies", replyService.getRepliesGroupedByComment(feedUID));
        response.put("count", commentService.getSumComment(feedUID));
        FeedRequest req=feedService.getFeedDetail(feedUID);
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                return handleAuthenticatedRequest(req, jwtTokenProvider.getUserId(token),req.getUserId(),feedUID, response, token);
            }
        }
        return handleUnauthenticatedRequest(req, feedUID, response);
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
        return ResponseEntity.ok(Map.of(
                "accessToken", token.getAccessToken(),
                "refreshToken", token.getRefreshToken(),
                "username", authentication.getName(),
                "isLoggedIn", true
        ));
    }
    @GetMapping("/feeds")
    public ResponseEntity<?> getFeeds(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        int viewCount = feedService.getViewCountAll();
        Map<String,Double> countMap = commentService.getPagingComment(feedService.getfeedUIDList(page, size), page, size);
        int maxPage = (int) Math.ceil((double) feedService.getTotalPage(page, size) / size);
        int totalPage = (int) Math.ceil(feedService.getTotalFeed());
        List<FeedRequest> data = feedService.getPagingFeed(page, size);
        List<FeedRequest> month = feedService.getMonthPopularFeed();
        return ResponseEntity.ok(Map.of(
                "viewCount", viewCount,
                "count", countMap,
                "page", page,
                "maxPage", maxPage,
                "totalPage", totalPage,
                "data", data,
                "month", month
        ));
    }
    @GetMapping("/auth/status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAuthStatus(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        Map<String, Object> response = new HashMap<>();

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);

                if (jwtTokenProvider.validateToken(token)) {
                    response.put("isLoggedIn", true);
                    response.put("userId",jwtTokenProvider.getUserId(token));
                    response.put("username", jwtTokenProvider.getUsername(token));
                    return ResponseEntity.ok(response);
                }
            }
            response.put("isLoggedIn", false);
            return ResponseEntity.ok(response);
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
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    private ResponseEntity<Map<String, Object>>handleAuthenticatedRequest(FeedRequest req, String commentOwner, String userId, String feedUID, Map<String, Object> response, String token) {
        response.put("Owner", jwtTokenProvider.getUsername(token).equals(feedService.getFeedDetail(feedUID).getUserId()));
        response.put("username", jwtTokenProvider.getUsername(token));
        response.put("comment", userService.getCommentOwnerList(commentOwner,feedUID,userId));
        response.put("isLoggedIn", true);
        response.put("data", req);
        return ResponseEntity.ok(response);
    }


    private ResponseEntity<Map<String, Object>> handleUnauthenticatedRequest(FeedRequest req, String feedUID, Map<String, Object> response) {
        String postOwnerId = req.getUserId();

       List<CommentRequest> requests=  commentService.getCommentOne(feedUID)
                    .stream()
                    .peek(comment -> {
                        // 댓글 작성자가 게시글 작성자인지 여부 확인
                        comment.setAuthor(postOwnerId.equals(comment.getUserId()));
                    })
                    .collect(Collectors.toList());
        response.put("comment",requests);
        response.put("Owner", req.getUserId());
        response.put("isLoggedIn", false);
        response.put("data", req);
        return ResponseEntity.ok(response);
    }
}
