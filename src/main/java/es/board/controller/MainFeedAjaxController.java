package es.board.controller;

import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.jwt.JwtToken;
import es.board.controller.model.mapper.CommentMapper;
import es.board.controller.model.mapper.FeedMapper;
import es.board.controller.model.req.CommentRequest;
import es.board.controller.model.req.FeedRequest;
import es.board.controller.model.req.VoteRequest;
import es.board.controller.model.res.LoginResponse;
import es.board.repository.document.Board;
import es.board.repository.document.Comment;
import es.board.service.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MainFeedAjaxController {


    private  final AuthService userService;

    private  final RedisTemplate<String, Object> redisTemplate;

    private  final JwtTokenProvider jwtTokenProvider;

    private  final VisitorService visitService;

    private  final FeedService feedService;

    private  final ReplyService replyService;

    private  final CommentService commentService;

    private  final CommentMapper commentMapper;

    private  final VoteService voteService;

    private  final FeedMapper feedMapper;

    @GetMapping("/get-ip")
    public ResponseEntity<?> getClientIp() {

        Set<String> activeUsers = redisTemplate.keys("online_users:*");
        return ResponseEntity.ok(Map.of(
                "activeUsers", activeUsers.size(),
                "data", visitService.getStats()));
    }
    @PostMapping("/increaseViewCount")
    public ResponseEntity<?> increaseViewCount(@RequestBody Map<String, String> request, HttpServletResponse response,
                                               @CookieValue(value = "viewedFeeds", defaultValue = "") String viewedFeeds) {
        String id = request.get("id");
        if (!viewedFeeds.contains(id)) {
            feedService.saveViewCountFeed(id);
            String updatedFeeds = viewedFeeds.isEmpty() ? id : viewedFeeds + ";" + id;
            String encodedValue = URLEncoder.encode(updatedFeeds, StandardCharsets.UTF_8);
            Cookie cookie = new Cookie("viewedFeeds", encodedValue);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 30);
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
            jwtTokenProvider.addToBlacklist(token);
            log.info("[DEBUG] 블랙리스트에 추가된 토큰: " + token);
        }
        return ResponseEntity.ok(Map.of(
                "message", "로그아웃되었습니다.",
                "isLoggedIn", false));
    }

    @GetMapping("/user/id")
    public ResponseEntity<Map<String, String>> getUserId(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "토큰이 필요합니다."));
        }

        token = token.substring(7);
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "세션이 만료되었습니다."));
        }

        String userId = jwtTokenProvider.getUserId(token);
        return ResponseEntity.ok(Map.of("userId", userId));
    }
    @GetMapping("/info")
    @ResponseBody
    public ResponseEntity<?> getUserInfo(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.ok(Map.of(
                        "userId",jwtTokenProvider.getUserId(token),
                        "username",jwtTokenProvider.getUsername(token),
                        "isLoggedIn", true));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "error", "세션이 만료되었습니다."
        ));}
    @GetMapping("/today/arrgegation")
    @ResponseBody
    public ResponseEntity<?> getTodayAggregation() {
        Map<String,Double> aggregation= feedService.getDayAggregation();
        return ResponseEntity.ok(Map.of(
                "todayPosts", aggregation.get("postCount"),
                "todayViews", aggregation.get("viewCount"),
                "todayComments", commentService.getTodayCommentAggregation().get("commentCount")
        ));
    }
    @GetMapping("/search/content")
    @ResponseBody
    public ResponseEntity<?> getSearchBoardList(@RequestParam(required = false) String text,
                                                @RequestParam(required = false) LocalDateTime startDate,
                                                @RequestParam(required = false) LocalDateTime endDate) {
        if (startDate != null && endDate != null) {

            return ResponseEntity.ok(Map.of(
                    "data", feedService.getRangeTimeFeed(startDate, endDate)));
        } else {
            List<Board> boards =  feedService.getSearchBoard(text);
            log.info(boards.toString());
            return ResponseEntity.ok(Map.of(
                    "data", boards,
                    "url", "/search/view/content"
            ));
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
        return ResponseEntity.ok(Map.of(
                "accessToken", token.getAccessToken(),
                "refreshToken", token.getRefreshToken(),
                "username", authentication.getName(),
                "isLoggedIn", true
        ));
    }
    @GetMapping("/feeds")
    public ResponseEntity<?> getPagingMainFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
            Map<String,Object> feedCount=  feedService.getFetchTotalFeedStats();
            List<VoteRequest> vote=voteService.getVotePageFeed(page,size);
            List<FeedRequest> data = feedService.getPagingFeed(page, size);
            Map<String,Double> countMap = commentService.getCommentAndReplyAggregation(feedService.getfeedUIDList(data), page, size);
            Long totalPage = (Long) feedCount.get("totalFeedCount");
            return ResponseEntity.ok(Map.of(
                    "viewCount", (Long) feedCount.get("totalViewCount"),
                    "count", countMap,
                    "page", page,
                    "vote",vote,
                    "maxPage",totalPage,
                    "totalPage", totalPage,
                    "data", data
            ));
        }


    @GetMapping("/data/feed")
    public ResponseEntity<?> getPagingDataFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
            ,@RequestParam String  category) {
        Map<String, Object> results= feedService.getDataFeed(page,size,category);
        @SuppressWarnings("unchecked")
        List<Board> boardList = (List<Board>) results.get("data");
        return ResponseEntity.ok(Map.of(
                "data", feedMapper.fromBoardDtoList(boardList),
                "totalPage", results.get("total")));
    }



    @GetMapping("/notice/feed")
    public ResponseEntity<?> getPagingNoticeFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Map<String, Object> result = feedService.getNoticeFeed(page, size);

        @SuppressWarnings("unchecked")
        List<Board> boardList = (List<Board>) result.get("data");
        return ResponseEntity.ok(Map.of(
                "data", feedMapper.fromBoardDtoList(boardList),
                "totalPage", result.get("total")
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
        String username=jwtTokenProvider.getUsername(refreshToken);
        String newAccessToken = jwtTokenProvider.generateAccessToken("ROLE_USER",userId,username);
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }
    @GetMapping("/detail")
    public ResponseEntity<?> getDetailInfo(HttpServletRequest request,
                                           @RequestParam(value = "id") String feedUID) {
        Map<String, Object> response = new HashMap<>();
        Map<String,Object> commentRes= commentService.findCommentsWithCount(feedUID);
        response.put("replies", replyService.getRepliesGroupedByComment(feedUID));
        response.put("count",commentRes.get("commentCount"));
        FeedRequest feedRequest=feedService.getFeedDetail(feedUID);
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                return handleAuthenticatedRequest(feedRequest, jwtTokenProvider.getUserId(token), response, token, commentRes.get("comments"));
            }
        }
        return handleUnauthenticatedRequest( commentRes.get("comments"),feedRequest, response);
    }

    @GetMapping("/vote/detail")
    public ResponseEntity<?> getVoteDetailInfo(HttpServletRequest request,
                                           @RequestParam(value = "id") String feedUID) {
        Map<String, Object> response = new HashMap<>();
        Map<String,Object> commentRes= commentService.findCommentsWithCount(feedUID);
        response.put("replies", replyService.getRepliesGroupedByComment(feedUID));
        response.put("count",commentRes.get("commentCount"));
        VoteRequest req=voteService.getVoteDetail(feedUID);
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                return handleAuthenticatedVoteRequest(req, jwtTokenProvider.getUserId(token), response, token, commentRes.get("comments"));
            }
        }
        return handleUnauthenticatedVoteRequest(commentRes.get("comments"),req, response);
    }

    private ResponseEntity<Map<String, Object>>handleAuthenticatedRequest(FeedRequest feedRequest, String commentOwner, Map<String, Object> response, String token, Object comments) {
        response.put("isLiked",feedService.isAlreadyLiked(jwtTokenProvider.getUserId(token),feedRequest.getFeedUID()));
        response.put("Owner", jwtTokenProvider.getUserId(token).equals(feedRequest.getUserId()));
        response.put("username", jwtTokenProvider.getUsername(token));
        response.put("comment", userService.getCommentOwnerList(comments, commentOwner,feedRequest.getFeedUID(),jwtTokenProvider.getUserId(token)));
        response.put("isLoggedIn", true);
        response.put("data", feedRequest);
        return ResponseEntity.ok(response);
    }


    private ResponseEntity<Map<String, Object>> handleUnauthenticatedRequest(Object comments, FeedRequest req, Map<String, Object> response) {

        if (!(comments instanceof List<?>)) {
            throw new IllegalArgumentException("comments 파라미터가 List<CommentRequest> 타입이 아닙니다.");
        }
        List<CommentRequest> commentList = commentMapper.changeCommentListDTO((List<Comment>) comments);
        List<CommentRequest> requests=  commentList
                    .stream()
                    .peek(comment -> {
                        comment.setAuthor(req.getUserId()!=null && req.getUserId().equals(comment.getUserId()));
                    })
                    .collect(Collectors.toList());
        response.put("isLiked",false);
        response.put("comment",requests);
        response.put("Owner", req.getUserId());
        response.put("isLoggedIn", false);
        response.put("data", req);
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<Map<String, Object>>handleAuthenticatedVoteRequest(VoteRequest req, String commentOwner, Map<String, Object> response, String token, Object comments) {
//        response.put("isLiked",feedService.isAlreadyLiked(jwtTokenProvider.getUserId(token),req.getFeedUID()));
        response.put("Owner", jwtTokenProvider.getUserId(token).equals(req.getUserId()));
        response.put("username", jwtTokenProvider.getUsername(token));
        response.put("comment", userService.getCommentOwnerList(comments, commentOwner,req.getFeedUID(),jwtTokenProvider.getUserId(token)));
        response.put("isLoggedIn", true);
        response.put("data", req);
        return ResponseEntity.ok(response);
    }
    private ResponseEntity<Map<String, Object>> handleUnauthenticatedVoteRequest(Object comments, VoteRequest req, Map<String, Object> response) {

        if (!(comments instanceof List<?>)) {
            throw new IllegalArgumentException("comments 파라미터가 List<CommentRequest> 타입이 아닙니다.");
        }
        List<CommentRequest> commentList = commentMapper.changeCommentListDTO((List<Comment>) comments);
        List<CommentRequest> requests=  commentList
                .stream()
                .peek(comment -> {
                    comment.setAuthor(req.getUserId()!=null && req.getUserId().equals(comment.getUserId()));
                })
                .collect(Collectors.toList());
        response.put("isLiked",false);
        response.put("comment",requests);
        response.put("Owner", req.getUserId());
        response.put("isLoggedIn", false);
        response.put("data", req);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/search/view/feed/delete")
    @ResponseBody
    public ResponseEntity<?> deleteFeed(
            @RequestBody Map<String, String> requestData, @RequestHeader(value = "Authorization") String token) {
        String id = requestData.get("id");
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "토큰이 필요합니다."));
        }
        token = token.substring(7);
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "세션이 만료되었습니다."));
        }
        feedService.deleteFeed(id, jwtTokenProvider.getUserId(token));

        Map<String, String> response = new HashMap<>();
        response.put("redirectUrl", "/");
        return ResponseEntity.ok(response);
    }
}
