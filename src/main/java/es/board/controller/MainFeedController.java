package es.board.controller;

import es.board.config.jwt.JwtTokenProvider;
import es.board.config.s3.S3Uploader;
import es.board.controller.model.req.FeedRequest;
import es.board.controller.model.req.FeedUpdate;
import es.board.controller.model.req.TopWriter;
import es.board.controller.model.res.FeedCreateResponse;
import es.board.service.AuthService;
import es.board.service.CommentService;
import es.board.service.FeedService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RequiredArgsConstructor
@Slf4j
@Controller
@RequestMapping("/api")

public class MainFeedController {


    private final AuthService authService;

    private final FeedService feedService;


    private final S3Uploader s3Uploader;

    private final JwtTokenProvider jwtTokenProvider;


    @GetMapping("/top-writers")
    public ResponseEntity<List<TopWriter>> getTopWriters() {

        return ResponseEntity.ok(feedService.getTopWriters());
    }

    @GetMapping("/logout/user")
    public String logoutPage(Model model) {
        model.addAttribute("isLoggedIn", false);
        return "basic/feed/feedList";
    }

    @GetMapping("/")
    public String mainPage(Model model, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (token != null && jwtTokenProvider.validateToken(token)) {
            return "basic/feed/feedList";
        } else {
            return "basic/feed/feedList";
        }
    }

    @GetMapping("/search/view/og/feed/id")
    public String serveFeedPage(@RequestParam("id") String id, Model model) {
        FeedRequest feed = feedService.getFeedDetail(id);
        model.addAttribute("title", feed.getTitle());
        model.addAttribute("description", feed.getDescription());
        model.addAttribute("image", feed.getImageURL());
        return "feed-detail";
    }

    @GetMapping("/search/view/feed/update")
    public String editFeed(@RequestParam("id") String id,Model model) {
        model.addAttribute("feedUpdate", feedService.getFeedDetail(id));
        return "basic/feed/EditFeed";
    }

    @GetMapping("/search/view/feed/category")
    public String getCategory(Model model, String text) {

        String decodedCategory = URLDecoder.decode(text, StandardCharsets.UTF_8);
        model.addAttribute("data", feedService.getCategoryFeed(decodedCategory));
        return "basic/feed/CategoryFeed";
    }


    @PostMapping("/search/view/feed/update/save")
    public String editSaveFeed(@RequestBody FeedUpdate feedUpdate, @RequestHeader(value = "Authorization", required = false) String token) {
        log.info(feedUpdate.toString());
        feedService.updateFeed(feedUpdate.getFeedUID(), feedUpdate);
        return "redirect:/search/view/feed/id?id=" + feedUpdate.getFeedUID();
    }

    @GetMapping("/search/view/feed/id")
    public String getFeedDetail(@RequestParam("id") String id,
                                Model model){
        FeedRequest feed = feedService.getFeedDetail(id);
        model.addAttribute("title", feed.getTitle());
        model.addAttribute("description", feed.getDescription());
        model.addAttribute("image", feed.getImageURL());
        model.addAttribute("id", id);
        model.addAttribute("url", "https://workly.info/search/view/feed/id?id=" + id);
        return "basic/feed/FeedDetails";
    }


    @GetMapping("/search/view/practical")

    public String getQuestionPractical(@RequestParam String category) {
        return "basic/feed/practical";
    }

    @GetMapping("/search/view/practical/question")
    public String getQuestionPracticalQuestion() {
        return "basic/feed/practionQuestion";
    }


    @GetMapping("/search/view/question")
    public String getQuestion() {

        return "basic/feed/question";
    }

    @PostMapping("/search/view/feed/increase-like/{feedUID}")
    public ResponseEntity<Map<String, Integer>> increaseLikeCount(@PathVariable String feedUID,
                                                                  @RequestHeader("Authorization") String token) {

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String userId = jwtTokenProvider.getUserId(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        feedService.plusLike(feedUID, userId);

        Map<String, Integer> response = new HashMap<>();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search/view/feed/cancel-like/{feedUID}")
    public ResponseEntity<Map<String, Integer>> cancelLikeCount(@PathVariable String feedUID,
                                                                @RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String userId = jwtTokenProvider.getUserId(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        feedService.cancelLike(userId, feedUID);

        Map<String, Integer> response = new HashMap<>();

        return ResponseEntity.ok(response);
    }


    @GetMapping("/search/view/feed/list/popular")
    @ResponseBody
    public ResponseEntity<?> getPopularFeedDESC(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Map<String, Object> feedCount = feedService.getFetchTotalFeedStats();
        Double totalFeeds = (double) feedCount.get("totalFeedCount");
        int maxPage = (int) Math.ceil((double) totalFeeds / size);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", feedService.getPopularFeedDESC(page, size));
        response.put("page", page);
        response.put("maxPage", maxPage);
        response.put("totalFeeds", totalFeeds);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/view/content")
    public String getContentFeed() {

        return "basic/feed/SearchFeed";
    }



    @GetMapping("/search/view/feed")
    public String getFeed() {

        return "basic/feed/feedList";
    }


    @GetMapping("/resume/template/1")
    public String getResume1() {

        return "basic/feed/resume-template1";
    }

    @GetMapping("/resume/template/2")
    public String getResume2() {

        return "basic/feed/resume-template2";
    }

    @GetMapping("/team/calendar")
    public String getTeamCalendar() {

        return "basic/feed/TeamCalendar";
    }

    @GetMapping("/interview/question")
    public String getInterview() {

        return "basic/feed/interview";
    }


    @GetMapping("/resume/template/3")
    public String getResume3() {

        return "basic/feed/resume-template3";
    }

    @GetMapping("/search/view/resume")
    public String getResume() {
        return "basic/feed/Resume";
    }


    @GetMapping("/search/view/feed/vote")
    public String getVoteFeed() {

        return "basic/feed/VoteAdd";
    }


    @GetMapping("/certificate/data")
    public String getCertificateData() {
        return "basic/feed/CertificationData";
    }
    @GetMapping("/certificate/detail")
    public String getCertificateDetail(@RequestParam("text") String text, Model model) {
        return "basic/feed/CertificationDetail";
    }


    @GetMapping("/search/view/feed/voteex")
    public String getVoteExFeed() {

        return "basic/feed/voteEx";
    }

    @GetMapping("/search/view/vote/detail")
    public String getVoteDetail(@RequestParam String  id) {

        return "basic/feed/VoteDetail";
    }

    @GetMapping("/site")
    public String getSite() {
        return "basic/feed/site";
    }

    @GetMapping("/post/page")
    public String getNoticeOne() {

        return "basic/feed/PostFeedList";
    }

    @GetMapping("/search/view/feed/list/{category}")
    public String getCategoryListFeed(Model model, @PathVariable String category) {
        model.addAttribute("data", feedService.getCategoryFeed(category));
        return "basic/feed/CategoryFeed";
    }

    @GetMapping("/search/view/feed/recommend")
    @ResponseBody
    public ResponseEntity<?> getRecommendFeed() {
        return ResponseEntity.ok(Map.of("recommend", feedService.getRecommendFeed()));
    }


    @GetMapping("/search/view/feed/latest")
    public String getRecentFeedList(Model model) {
        model.addAttribute("data", feedService.getRecentFeed());
        return "basic/feed/RecentFeed";
    }


    @GetMapping("/search/view/feed/range")
    public String getRangeTime(Model model, @RequestParam LocalDateTime startDate, @RequestParam LocalDateTime endDate) {
        model.addAttribute("data", feedService.getRangeTimeFeed(startDate, endDate));
        return "/basic/feed/RangeFeed";
    }
    @PostMapping("/search/view/feed/upload-image")
    @ResponseBody
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file,
                                         @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            String imageUrl = s3Uploader.upload(file, jwtTokenProvider.getUsername(token));
            return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
        } catch (Exception e) {
            log.error("이미지 업로드 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "이미지 업로드 실패"));
        }
    }

    @PostMapping("/search/view/feed/save")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveFeed(
            @RequestParam(required = false, value = "imageFiles") MultipartFile file,
            @RequestPart("feed") FeedCreateResponse res,
            @RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            processFileUpload(file, res);
            authService.extractUserIdFromToken(token, res);
            log.info(res.toString());
            response.put("feed", feedService.saveFeed(res));
            response.put("success", true);
            response.put("redirectUrl", "/search/view/feed?index=board");
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            log.error("Invalid feed data: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error occurred: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "error", "요청 처리 중 오류가 발생했습니다."));
        }
    }

    @GetMapping("/search/view/feed/list/job")
    public String getProgrammersList() {
        return "basic/feed/ItCrawlingFeed";
    }


    @GetMapping("/search/view/feed/Form")
    public String feedSaveForm(@RequestHeader(value = "Authorization", required = false) String token,
                               Model model) {
        model.addAttribute("FeedCreateResponse", new FeedCreateResponse());
        return "basic/feed/PostFeed";
    }


    @PostMapping("/feed/view/bulks")
    public List<FeedCreateResponse> postBulkFeed(@RequestBody List<FeedCreateResponse> comments) {

        return feedService.createBulkFeed(comments);
    }

    @GetMapping("/search/view/feed/best")
    public ResponseEntity<?> getWeekBestFeed(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size) {

        Map<String, Object> response = new HashMap<>();
        response.put("data", feedService.findWeekBestFeed(page, size));
        return ResponseEntity.ok(response);
    }

//    @GetMapping("/kakao/callback")
//    public String kakaoCallbackView(@RequestParam String code, Model model) {
//        model.addAttribute("code", code);
//        return "basic/login/kakao_callback";
//    }
//
//    @GetMapping("/naver/callback")
//    public String naverCallbackView(@RequestParam String code, Model model) {
//        model.addAttribute("code", code);
//        return "basic/login/naver_callback";
//    }
//
//    @GetMapping("/google/callback")
//    public String googleCallbackView(@RequestParam String code, Model model) {
//        model.addAttribute("code", code);
//        return "basic/login/google_callback.html";
//    }
    @GetMapping("/naver/callback")
    public RedirectView naverCallbackView(@RequestParam String code) {
        return new RedirectView("https://workly.info/naver/callback?code=" + code);
    }

    @GetMapping("/google/callback")
    public RedirectView googleCallbackView(@RequestParam String code) {

        return new RedirectView("https://workly.info/google/callback?code=" + code);
    }
    @GetMapping("/kakao/callback")
    public RedirectView kakaoCallbackRedirect(@RequestParam String code) {
        // 그냥 클라이언트 라우트로 리다이렉트 (쿼리스트링 포함)
        return new RedirectView("https://workly.info/kakao/callback?code=" + code);
    }


    @GetMapping("/get/user/vote/details")
    public String getUserVoteDetail() {
        return "basic/feed/VoteDetail";
    }

    private void processFileUpload(MultipartFile file, FeedCreateResponse feedSaveDTO) throws IOException {
        if (file != null && !file.isEmpty()) {
            log.info("File upload started");
            feedSaveDTO.setImageURL(s3Uploader.upload(file, feedSaveDTO.getUsername()));
            }
        }
    private boolean isBot(String ua) {
        String lowered = ua.toLowerCase();
        return lowered.contains("kakaotalk") ||
                lowered.contains("facebookexternalhit") ||
                lowered.contains("slackbot") ||
                lowered.contains("twitterbot") ||
                lowered.contains("naver") ||
                lowered.contains("telegrambot");
    }

}


