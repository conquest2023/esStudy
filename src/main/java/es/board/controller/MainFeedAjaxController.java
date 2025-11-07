package es.board.controller;

import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.mapper.CommentMapper;
import es.board.controller.model.mapper.FeedMapper;
import es.board.controller.model.dto.feed.PostDTO;
import es.board.controller.model.dto.feed.VoteDTO;
import es.board.controller.model.dto.feed.CommentDTO;
import es.board.ex.TokenValidator;
import es.board.repository.document.Board;
import es.board.repository.document.Comment;
import es.board.service.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class MainFeedAjaxController {


    private  final AuthService userService;

    private  final StringRedisTemplate redis;

    private  final RedisTemplate<String, Object> redisTemplate;

    private  final JwtTokenProvider jwtTokenProvider;

    private  final TokenValidator tokenValidator;

    private  final VisitorService visitService;

    private  final FeedService feedService;

    private  final ReplyIndexService replyService;

    private  final CommentIndexService commentService;

    private  final CommentMapper commentMapper;

    private  final VoteService voteService;

    private  final FeedMapper feedMapper;

    @GetMapping("/get-ip")
    public ResponseEntity<?> getClientIp() {
        Set<String> activeUsers = redisTemplate.keys("online_users:*");
//        List<String> rawKeys = new ArrayList<>(redisTemplate.keys("visit*"));
//        List<String> todayKeys = new ArrayList<>();
//        todayAggregation(rawKeys, todayKeys);
//        log.info("오늘 자정까지 유효한 방문자 수: {}", todayKeys.size());
        return ResponseEntity.ok(Map.of(
                "activeUsers", activeUsers.size(),
                "data", visitService.getStats()));
    }
    @GetMapping("/auto")
    public ResponseEntity<?> main(@CookieValue(value = "refreshToken", required = false) String refreshToken) {
        if (refreshToken != null) {
            String userId = jwtTokenProvider.getUserId(refreshToken);
            String storedRefresh = redis.opsForValue().get("RT:" + userId);

            if (storedRefresh != null && storedRefresh.equals(refreshToken)) {
                String newAccessToken = jwtTokenProvider.generateAccessToken("user", jwtTokenProvider.getUsername(refreshToken), userId);
                return ResponseEntity.ok().header("Authorization", "Bearer " + newAccessToken).body("자동 로그인 성공!");
            }
        }
        return ResponseEntity.status(401).body("다시 로그인 필요");
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
            cookie.setMaxAge(60 * 40);
            response.addCookie(cookie);
        }
        return ResponseEntity.ok("조회수 증가 성공");
    }
    @PostMapping("/authlogout")
    @ResponseBody
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
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
    public ResponseEntity<?> getUserId(@RequestHeader(value = "Authorization", required = false) String token) {
        ResponseEntity<?> tokenCheckResponse = tokenValidator.validateTokenOrRespond(token);
        if (tokenCheckResponse == null) {
            return tokenCheckResponse;
        }
        String userId = jwtTokenProvider.getUserId(token.substring(7));
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
//                        "userId",jwtTokenProvider.getUserId(token),
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
            return ResponseEntity.ok(Map.of(
                    "data", boards,
                    "url", "/search/view/content"
            ));
        }
    }
    @GetMapping("/feeds")
    public ResponseEntity<?> getPagingMainFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
            Map<String,Object> feedCount=  feedService.getFetchTotalFeedStats();
            List<VoteDTO.Request> vote=voteService.getVotePageFeed(page,size);
            List<PostDTO.Request> data = feedService.getPagingFeed(page, size);
            Map<String,Double> countMap = commentService.getCommentAndReplyAggregation(feedService.getfeedUIDList(data), page, size);
            Long totalPage = (Long) feedCount.get("totalFeedCount");
            return ResponseEntity.ok(Map.of(
                    "count", countMap,
                    "page", page,
                    "vote",vote,
                    "maxPage",totalPage,
                    "totalPage", totalPage,
                    "data", data
            ));
        }
    @GetMapping("/feeds/ids")
    public ResponseEntity<?> getPagingFeedIds(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<String> ids=feedService.findPagingFeedIds(page,size);
        return ResponseEntity.ok(Map.of( "ids",ids));
    }

    @GetMapping("/search/view/feed/list/most")
    public ResponseEntity<?> getMostViewFeed(
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(Map.of( "data", feedService.getMostViewFeed(page, size)));
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
    @GetMapping("/search/view/feed/recommend")
    @ResponseBody
    public ResponseEntity<?> getRecommendFeed() {
        return ResponseEntity.ok(Map.of("recommend", feedService.getRecommendFeed()));
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
                "totalPage", result.get("total")));
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
    public Object getDetailInfo(HttpServletRequest request, @ModelAttribute Model model,
                                           @RequestParam(value = "id") String feedUID) {

        Map<String, Object> response = new HashMap<>();
        Map<String,Object> commentRes= commentService.findCommentsWithCount(feedUID);
        response.put("replies", replyService.getRepliesGroupedByComment(feedUID));
        response.put("count",commentRes.get("commentCount"));
        PostDTO.Request feedRequest=feedService.getFeedDetail(feedUID);
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
    public ResponseEntity<?> getVoteDetailInfo( @RequestHeader(value = "Authorization",required = false) String token,
                                           @RequestParam(value = "id") String feedUID) {
        Map<String, Object> response = new HashMap<>();
        Map<String,Object> commentRes= commentService.findCommentsWithCount(feedUID);
        response.put("replies", replyService.getRepliesGroupedByComment(feedUID));
        response.put("count",commentRes.get("commentCount"));
        VoteDTO.Request req=voteService.getVoteDetail(feedUID);
        if (token != null && token.startsWith("Bearer ")) {
            token =token.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                return handleAuthenticatedVoteRequest(req, jwtTokenProvider.getUserId(token), response, token, commentRes.get("comments"));
            }
        }
        return handleUnauthenticatedVoteRequest(commentRes.get("comments"),req, response);
    }
    @PostMapping("/search/view/feed/delete")
    @ResponseBody
    public ResponseEntity<?> deleteFeed(
            @RequestBody Map<String, String> requestData, @RequestHeader(value = "Authorization") String token) {
        String id = requestData.get("id");
        ResponseEntity<?> tokenCheckResponse = tokenValidator.validateTokenOrRespond(token);
        if (tokenCheckResponse == null) {
            return tokenCheckResponse;
        }
        feedService.deleteFeed(id, jwtTokenProvider.getUserId(token.substring(7)));
        Map<String, String> response = new HashMap<>();
        response.put("redirectUrl", "/");
        return ResponseEntity.ok(response);
    }
    private ResponseEntity<Map<String, Object>>handleAuthenticatedRequest(PostDTO.Request feedRequest, String commentOwner, Map<String, Object> response, String token, Object comments) {
        response.put("isLiked",feedService.isAlreadyLiked(jwtTokenProvider.getUserId(token),feedRequest.getFeedUID()));
        response.put("Owner", jwtTokenProvider.getUserId(token).equals(feedRequest.getUserId()));
        response.put("username", jwtTokenProvider.getUsername(token));
        response.put("comment", userService.getCommentOwnerList(comments, commentOwner,feedRequest.getFeedUID(),jwtTokenProvider.getUserId(token)));
        response.put("isLoggedIn", true);
        response.put("data", feedRequest);
        return ResponseEntity.ok(response);
    }
    private ResponseEntity<Map<String, Object>> handleUnauthenticatedRequest(Object comments, PostDTO.Request req, Map<String, Object> response) {

        if (!(comments instanceof List<?>)) {
            throw new IllegalArgumentException("comments 파라미터가 List<CommentRequest> 타입이 아닙니다.");
        }
        List<CommentDTO.Request> commentList = commentMapper.changeCommentListDTO((List<Comment>) comments);
        List<CommentDTO.Request> requests=  commentList
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

    private ResponseEntity<Map<String, Object>>handleAuthenticatedVoteRequest(VoteDTO.Request req, String commentOwner, Map<String, Object> response, String token, Object comments) {
//        response.put("isLiked",feedService.isAlreadyLiked(jwtTokenProvider.getUserId(token),req.getFeedUID()));
        response.put("Owner", jwtTokenProvider.getUserId(token).equals(req.getUserId()));
        response.put("username", jwtTokenProvider.getUsername(token));
        response.put("comment", userService.getCommentOwnerList(comments, commentOwner,req.getFeedUID(),jwtTokenProvider.getUserId(token)));
        response.put("isLoggedIn", true);
        response.put("data", req);
        return ResponseEntity.ok(response);
    }
    private ResponseEntity<Map<String, Object>> handleUnauthenticatedVoteRequest(Object comments, VoteDTO.Request req, Map<String, Object> response) {

        if (!(comments instanceof List<?>)) {
            throw new IllegalArgumentException("comments 파라미터가 List<CommentRequest> 타입이 아닙니다.");
        }
        List<CommentDTO.Request> commentList = commentMapper.changeCommentListDTO((List<Comment>) comments);
        List<CommentDTO.Request> requests=  commentList
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

    private void todayAggregation(List<String> rawKeys, List<String> todayKeys) {
        if (rawKeys != null) {
            long secondsUntilMidnight = ChronoUnit.SECONDS.between(
                    LocalDateTime.now(),
                    LocalDate.now().plusDays(1).atStartOfDay()
            );
            for (String key : rawKeys) {
                Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
                if (ttl != null && ttl > 0 && ttl <= secondsUntilMidnight) {
                    todayKeys.add(key);
                }
            }
        }
    }
}
