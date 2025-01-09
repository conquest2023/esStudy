package es.board.controller;

import es.board.config.jwt.JwtTokenProvider;
import es.board.config.s3.S3Uploader;
import es.board.controller.model.file.FileStore;
import es.board.controller.model.req.FeedUpdate;
import es.board.controller.model.res.CommentCreateResponse;
import es.board.controller.model.res.FeedCreateResponse;
import es.board.service.CommentService;
import es.board.service.FeedService;
import es.board.service.ReplyService;
import es.board.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@RequiredArgsConstructor
@Slf4j
@Controller
public class FeedViewController {
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
        basicSettingFeed(model, page, size);
        model.addAttribute("isLoggedIn", false);
        return "basic/feed/feedList";
    }

    @GetMapping("/")
    public String mainPage(Model model, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // "Bearer " 이후의 토큰만 추출
        }
        basicSettingFeed(model, page, size);
        if (token != null && jwtTokenProvider.validateToken(token)) {
            log.info(jwtTokenProvider.getAuthentication(token).getName());
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("username", jwtTokenProvider.getAuthentication(token).getName());
            return "basic/feed/feedList";
        } else {
            model.addAttribute("isLoggedIn", false);
            return "basic/feed/feedList";
        }
    }


    @GetMapping("/search/view/feed/update")
    public String editFeed(@RequestParam("id") String id, Model model)  {
        model.addAttribute("feedUpdate",feedService.getFeedId(id));
        log.info(feedService.getFeedId(id).toString());
        return "basic/feed/EditFeed";
    }

    @GetMapping("/search/view/feed/category")
    public String getCategory(Model model,@RequestParam String text)  {
        model.addAttribute("data",feedService.getCategoryFeed(text));
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
        feedDetailParts(model, id,true);
        return "basic/feed/FeedDetails";
    }

    @PostMapping("/search/view/feed/increase-like/{feedUID}")
    public ResponseEntity<Map<String, Integer>> increaseLikeCount(@PathVariable String feedUID) {
        feedService.plusLike(feedUID);
        Map<String, Integer> response = new HashMap<>();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search/view/feed/id")
    public String saveCommentId(@RequestParam String id,
                                   @ModelAttribute CommentCreateResponse commentSaveDTO,
                                   Model model)  {
        commentSaveDTO.commentBasicSetting(id);
        commentService.indexComment(commentSaveDTO);
        model.addAttribute("push",commentSaveDTO);
        feedDetailParts(model, id,false);
        return "redirect:/search/view/feed/id?id=" + id;
    }
    @GetMapping("/search/view/feed/list/popular")
    public String getPopularFeedDESC(Model model,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size) {
        basicSettingFeed(model, page, size);
        model.addAttribute("popular",feedService.getPopularFeedDESC(page,size));
        return "basic/feed/LikeFeed";
    }



    @GetMapping("/search/view/feed/feedAll")
    public String getFeedList(Model model, @RequestBody Map<String, String> request)  {

        String feedUID = request.get("feedUID");
        if (feedUID != null) {
            feedService.saveViewCountFeed(feedUID);
        }
        model.addAttribute("data",feedService.getFeed());
        return "basic/feed/feedList?index=board";
    }
    @GetMapping("/search/view/feed/text")
    public String getSearchBoardList(Model model, @RequestParam String text)  {
        model.addAttribute("data",feedService.getSearchBoard(text));
        return "basic/feed/SearchFeed";
    }

    @GetMapping("/search/view/feed")
    public String getFeed(Model model,
                          @RequestParam(defaultValue = "0") int page, // 페이지 번호 (0부터 시작)
                          @RequestParam(defaultValue = "10") int size) throws IOException { // 페이지 크기
        basicSettingFeed(model, page, size);
        return "basic/feed/feedList";
    }

    @GetMapping("/search/view/feed/list/most")
    public String getMostViewFeed(Model model,
                          @RequestParam(defaultValue = "0") int page, // 페이지 번호 (0부터 시작)
                          @RequestParam(defaultValue = "10") int size) throws IOException { // 페이지 크기

        basicSettingFeed(model, page, size);
        model.addAttribute("data",feedService.getMostViewFeed(page,size));
        return "basic/feed/MostViewFeed";
    }



    @GetMapping("/search/view/feed/list/{category}")
    public String getCategoryListFeed(Model model, @PathVariable String category){
        model.addAttribute("data",feedService.getCategoryFeed(category));

        return "/basic/feed/CategoryFeed";
    }

    @GetMapping("/search/view/feed/latest")
    public String getRecentFeedList(Model model)  {
        model.addAttribute("data",feedService.getRecentFeed());
        return "basic/feed/RecentFeed";
    }
//    @GetMapping("/search/view/feed/like")
//    public String getLikeCountList(Model model) throws IOException {
//        model.addAttribute("data", feedService.getLikeCount());
//        return "basic/feed/LikeFeed";
//    }

    @GetMapping("/search/view/feed/range")
    public  String getRangeTime(Model model, @RequestParam LocalDateTime startDate
            , @RequestParam LocalDateTime endDate ) {
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

        feedSaveDTO.setImageURL(s3Uploader.upload(file,feedSaveDTO.getUserId()));
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

//        log.info(token);
//        if (token != null && token.startsWith("Bearer ")) {
//            // JWT에서 userId와 username 추출
//            String jwtToken = token.substring(7); // "Bearer " 제거
//            String userId = jwtTokenProvider.getUserId(jwtToken);
//            String username = jwtTokenProvider.getUsername(jwtToken);
//            log.info(userId,username);
//            // 모델에 userId와 username 추가
//            model.addAttribute("userId", userId);
//            model.addAttribute("userName", username);
//        } else {
//            log.info("null");
            // 로그아웃 상태 처리
            model.addAttribute("userId", null);
            model.addAttribute("userName", "");
            return  "basic/feed/PostFeed";
//        log.info(feedSaveDTO.toString());
//        model.addAttribute("FeedCreateResponse", new FeedCreateResponse());

    }


    @PostMapping("/feed/view/bulks")
    public  List<FeedCreateResponse> postBulkFeed(@RequestBody List<FeedCreateResponse> comments)  {

        return feedService.createBulkFeed(comments);
    }

        @GetMapping("/search/view/comment/desc")
        public  String  getMostCommentDESC(Model model, @RequestParam(defaultValue = "0") int page, // 페이지 번호 (0부터 시작)
                                       @RequestParam(defaultValue = "10") int size) throws  IOException{

            model.addAttribute("commentDESC",commentService.getPagingCommentDESC(feedService.getfeedUIDList(page,size),page,size));
            basicSettingFeed(model, page, size);
            return  "basic/comment/MostCommentDESC";
        }



    @PostMapping("/search/view/feed/delete")
    public  String deleteFeed(@RequestParam String id,int userId) {
        log.info("userId:{}",userId);
        feedService.deleteFeed(id,userId);
        return "redirect:/search/view/feed?index=board";
    }
    @GetMapping("/search/view/feed/reload")
    public  String reloadViewCount(Model model){

        model.addAttribute("data",feedService.getFeed());
        return "basic/feed/feedList?index=board";
    }

    private void basicSettingFeed(Model model, int page, int size) {

        model.addAttribute("viewCount",feedService.getViewCountAll());
        model.addAttribute("count",commentService.getPagingComment(feedService.getfeedUIDList(page,size),page,size));
        model.addAttribute("page", page);  // 현재 페이지 번호
        model.addAttribute("maxPage", (int) Math.ceil((double) feedService.getTotalPage(page, size) / size));
        model.addAttribute("totalPage",  (int) Math.ceil(feedService.getTotalFeed()));
        model.addAttribute("data", feedService.getPagingFeed(page, size)); // 서비스 호출 시 페이지와 크기 전달
        model.addAttribute("month",feedService.getMonthPopularFeed());
    }

    public void feedDetailParts(Model model, String id,boolean isView) {
        if (isView) {
            feedService.saveViewCountFeed(id); // 조회수 증가
        }
        model.addAttribute("replies", replyService.getRepliesGroupedByComment(id));
        model.addAttribute("count",commentService.getSumComment(id));
        model.addAttribute("data",feedService.getFeedId(id));
        model.addAttribute("comment",commentService.getCommentOne(id));
        model.addAttribute("reply",replyService.getPartialReply(id));
        model.addAttribute("feedId", id);
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