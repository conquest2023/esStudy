package es.board.controller;

import es.board.config.jwt.JwtTokenProvider;
import es.board.config.s3.S3Uploader;
import es.board.controller.model.req.FeedUpdate;
import es.board.controller.model.res.CommentCreateResponse;
import es.board.controller.model.res.FeedCreateResponse;
import es.board.service.CommentService;
import es.board.service.FeedService;
import es.board.service.ReplyService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@RequiredArgsConstructor
@Slf4j
@Controller
public class FeedController {
    @Value("%{file.dir}")
    private  String fileDir;



    private final ReplyService replyService;

    private final FeedService feedService;

    private  final CommentService commentService;

    private  final S3Uploader s3Uploader;

    private  final JwtTokenProvider jwtTokenProvider;

    private final  int page=0;
    private final  int size=10;


    @GetMapping("/logout/user")
    public String logoutPage(Model model) {
//        basicSettingFeed(model, page, size);
        model.addAttribute("isLoggedIn", false);
        return "basic/feed/feedList";
    }

    @GetMapping("/")
    public String mainPage(Model model, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // "Bearer " 이후의 토큰만 추출
        }
//        basicSettingFeed(model, page, size);
        if (token != null && jwtTokenProvider.validateToken(token)) {
            log.info(jwtTokenProvider.getAuthentication(token).getName());
//            model.addAttribute("isLoggedIn", true);
//            model.addAttribute("username", jwtTokenProvider.getAuthentication(token).getName());
            return "basic/feed/feedList";
        } else {
//            model.addAttribute("isLoggedIn", false);
            return "basic/feed/feedList";
        }
    }


    @GetMapping("/search/view/feed/update")
    public String editFeed(@RequestParam("id") String id, Model model)  {
        model.addAttribute("feedUpdate",feedService.getFeedId(id));
        return "basic/feed/EditFeed";
    }

    @GetMapping("/search/view/feed/category")
    public String getCategory(Model model, String text)  {

        String decodedCategory = URLDecoder.decode(text, StandardCharsets.UTF_8);
        model.addAttribute("data",feedService.getCategoryFeed(decodedCategory));
        return "basic/feed/CategoryFeed";
    }

//    @GetMapping("/search/view/feed/popularfeed")
//    public String getMonthPopular(Model model) throws Exception {
//        return "FeedList";
//    }

    @PostMapping("/search/view/feed/update/save")
    public String editSaveFeed(Model model,@ModelAttribute FeedUpdate feedUpdate)  {
        feedService.updateFeed(feedUpdate.getFeedUID(),feedUpdate);
        model.addAttribute("feedUpdate", feedUpdate);
        return  "redirect:/search/view/feed/id?id=" +feedUpdate.getFeedUID();
    }

    @GetMapping("/search/view/feed/id")
    public String getFeedDetail(Model model,@RequestParam String id)  {

//        feedDetailParts(model, id, true);
        return "basic/feed/FeedDetails";
    }

    @PostMapping("/search/view/feed/increase-like/{feedUID}")
    public ResponseEntity<Map<String, Integer>> increaseLikeCount(@PathVariable String feedUID) {
        feedService.plusLike(feedUID);
        Map<String, Integer> response = new HashMap<>();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/view/feed/list/popular")
    @ResponseBody
    public ResponseEntity<?> getPopularFeedDESC(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Double totalFeeds =  feedService.getTotalFeed();
        int maxPage = (int) Math.ceil((double) totalFeeds / size);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data",  feedService.getPopularFeedDESC(page, size));
        response.put("page", page);
        response.put("maxPage", maxPage);
        response.put("totalFeeds", totalFeeds);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/content")
    @ResponseBody
    public ResponseEntity<?> getSearchBoardList(@RequestParam(required = false) String text,
                                                @RequestParam(required = false) LocalDateTime startDate,
                                                @RequestParam(required = false) LocalDateTime endDate)  {
        if (startDate != null && endDate != null) {

            return   ResponseEntity.ok(Map.of(
                    "data",  feedService.getRangeTimeFeed(startDate,endDate)));
        }else{

            return   ResponseEntity.ok(Map.of(
                    "data",feedService.getSearchBoard(text)
            ));
        }
//        return "basic/feed/SearchFeed";
    }

    @GetMapping("/search/view/feed")
    public String getFeed(){ // 페이지 크기
        return "basic/feed/feedList";
    }

    @GetMapping("/search/view/feed/list/most")
    public String getMostViewFeed(Model model,
                          @RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "10") int size)  { // 페이지 크기
        model.addAttribute("data",feedService.getMostViewFeed(page,size));
        return "basic/feed/MostViewFeed";
    }



    @GetMapping("/search/view/feed/list/{category}")
    public String getCategoryListFeed(Model model, @PathVariable String category){
        model.addAttribute("data",feedService.getCategoryFeed(category));
        return "basic/feed/CategoryFeed";
    }

    @GetMapping("/search/view/feed/latest")
    public String getRecentFeedList(Model model)  {
        model.addAttribute("data",feedService.getRecentFeed());
        return "basic/feed/RecentFeed";
    }


    @GetMapping("/search/view/feed/range")
    public  String getRangeTime(Model model, @RequestParam LocalDateTime startDate, @RequestParam LocalDateTime endDate ) {
        model.addAttribute("data",feedService.getRangeTimeFeed(startDate,endDate));
        return "/basic/feed/RangeFeed";
    }
//    required = false,
    @PostMapping("/search/view/feed/save")
    @ResponseBody
    public Map<String, Object> saveFeed(Model model,
                                        @RequestParam(required = false,value ="imageFiles") MultipartFile file,
                                        @ModelAttribute FeedCreateResponse feedSaveDTO,
                                        @RequestHeader(value = "Authorization", required = false) String token) throws IOException {
        if (file != null && !file.isEmpty()) {
            log.info("File upload started");
            feedSaveDTO.setImageURL(s3Uploader.upload(file, feedSaveDTO.getUserId()));
        }

        Map<String, Object> response = new HashMap<>();
        if (token == null || !token.startsWith("Bearer ")) {
            model.addAttribute("userId", null);
        } else {
            model.addAttribute("userId",  jwtTokenProvider.getUserId(token.substring(7)));
        }
        response.put("success", true);
        response.put("feed", feedService.saveFeed(feedSaveDTO));
        response.put("redirectUrl", "/search/view/feed?index=board");
        return response;
    }


    @GetMapping("/search/view/feed/Form")
    public String feedSaveForm( Model model,  @RequestHeader(value = "Authorization", required = false) String token) {

            return  "basic/feed/PostFeed";
    }


    @PostMapping("/feed/view/bulks")
    public  List<FeedCreateResponse> postBulkFeed(@RequestBody List<FeedCreateResponse> comments)  {

        return feedService.createBulkFeed(comments);
    }

        @GetMapping("/search/view/comment/desc")
        public  String  getMostCommentDESC(Model model, @RequestParam(defaultValue = "0") int page, // 페이지 번호 (0부터 시작)
                                       @RequestParam(defaultValue = "10") int size) throws  IOException{

            model.addAttribute("commentDESC",commentService.getPagingCommentDESC(feedService.getfeedUIDList(page,size),page,size));
            return  "basic/comment/MostCommentDESC";
        }



    @PostMapping("/search/view/feed/delete")
    @ResponseBody // JSON 응답을 위해 추가
    public ResponseEntity<?> deleteFeed(@RequestBody Map<String, String> requestData) {

        String id = requestData.get("id");
        String userId = requestData.get("userId");
        log.info("Deleting feed with ID: {}, UserID: {}", id, userId);
        // 실제 삭제 로직
        feedService.deleteFeed(id, userId);
        // 삭제 후 리다이렉트 URL 반환
        Map<String, String> response = new HashMap<>();
        response.put("redirectUrl", "/");
        return ResponseEntity.ok(response);
    }
    @GetMapping("/search/view/feed/reload")
    public  String reloadViewCount(Model model){

        model.addAttribute("data",feedService.getFeed());
        return "basic/feed/feedList?index=board";
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
    @GetMapping("/detail")
    public ResponseEntity<?> getFeedDetail(@RequestParam String id) {
        // 필요한 데이터 조회
        Map<String, Object> response = new HashMap<>();
        response.put("replies", replyService.getRepliesGroupedByComment(id));
        response.put("count", commentService.getSumComment(id));
        response.put("data", feedService.getFeedId(id));       // 단일 피드 데이터
        response.put("comment", commentService.getCommentOne(id)); // 댓글 리스트
        response.put("reply", replyService.getPartialReply(id));   // 추가 정보(필요시)
        response.put("feedId", id);

        return ResponseEntity.ok(response);
    }



    private static void commentSetIds(String id, CommentCreateResponse commentSaveDTO) {
        commentSaveDTO.setFeedUID(id);
        commentSaveDTO.TimeNow();
        commentSaveDTO.setCommentUID(UUID.randomUUID().toString());
    }
}








//        String imageName =request.getParameter(feedSaveDTO.getImage());
//        Collection<Part> parts =request.getParts();
//        for (Part part : parts) {
//            log.info("name={}",part.getName());
//
//            Collection<String> headerNames=part.getHeaderNames();
//
//            for (String headerName : headerNames) {
//                log.info("header {}:{}",headerName,part.getHeader(headerName));
//            }
//            log.info("submittedFileName={}",part.getSubmittedFileName());
//            log.info("size={}",part.getSize());
//
//            InputStream inputStream=part.getInputStream();
//            String fullPath="/fileSave"+part.getSubmittedFileName();
//            part.write(fullPath);
//        }