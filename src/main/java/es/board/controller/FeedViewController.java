package es.board.controller;

import es.board.model.file.FileStore;
import es.board.model.req.FeedUpdate;
import es.board.model.res.CommentCreateResponse;
import es.board.model.res.FeedCreateResponse;
import es.board.service.CommentService;
import es.board.service.FeedService;
import es.board.service.ReplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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


    private final FeedService feedService;

    private  final CommentService commentService;

    private  final FileStore fileStore;

    private final ReplyService replyService;
    @GetMapping("/")
    public String mainPage(Model model) {
        model.addAttribute("feedSaveDTO", new FeedCreateResponse());
        return "basic/feed/main";
    }

    @GetMapping("/search/view/feed/update")
    public String editFeed(@RequestParam("id") String id, @RequestParam("username") String username, Model model)  {
        model.addAttribute("feedUpdate",feedService.getFeedId(id));
    //        model.addAttribute("id", id);
//        model.addAttribute("username", username);
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
        feedDetailParts(model, id);
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
        return "redirect:/search/view/feed/id?id=" + id;
    }
    @GetMapping("/search/view/feed/list/popular")
    public String getPopularFeedDESC(Model model,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size) throws IOException {
        int maxPage = (int) Math.ceil((double) feedService.getTotalPage(page,size) / size);
        int totalPage=(int) Math.ceil( feedService.getTotalFeed());
        basicSettingFeed(model, page, size, maxPage, totalPage);
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
        int maxPage = (int) Math.ceil((double) feedService.getTotalPage(page,size) / size);
        int totalPage=(int) Math.ceil( feedService.getTotalFeed());
        basicSettingFeed(model, page, size, maxPage, totalPage);
        return "basic/feed/feedList";
    }

    @GetMapping("/search/view/feed/list/most")
    public String getMostViewFeed(Model model,
                          @RequestParam(defaultValue = "0") int page, // 페이지 번호 (0부터 시작)
                          @RequestParam(defaultValue = "10") int size) throws IOException { // 페이지 크기
        int maxPage = (int) Math.ceil((double) feedService.getTotalPage(page,size) / size);
        int totalPage=(int) Math.ceil( feedService.getTotalFeed());
        basicSettingFeed(model, page, size, maxPage, totalPage);
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
    @PostMapping("/search/view/feed/save")
    public String saveFeed( Model model, @ModelAttribute FeedCreateResponse feedSaveDTO)  {
        model.addAttribute("res",feedService.saveFeed(feedSaveDTO));
//        UploadFile attachFile=fileStore.storeFile(feedSaveDTO.getAttachFile());
//        List<UploadFile> storeImageFiles=fileStore.storeFiles(feedSaveDTO.getImageFiles());
        return "redirect:/search/view/feed?index=board";
    }


    @GetMapping("/search/view/feed/Form")
    public String feedSaveForm( Model model, @ModelAttribute FeedCreateResponse feedSaveDTO) {
        model.addAttribute("FeedCreateResponse", new FeedCreateResponse());
        return  "basic/feed/PostFeed";
    }


    @PostMapping("/feed/view/bulks")
    public  List<FeedCreateResponse> postBulkFeed(@RequestBody List<FeedCreateResponse> comments)  {

        return feedService.createBulkFeed(comments);
    }

        @GetMapping("/search/view/comment/desc")
        public  String  getMostCommentDESC(Model model, @RequestParam(defaultValue = "0") int page, // 페이지 번호 (0부터 시작)
                                       @RequestParam(defaultValue = "10") int size) throws  IOException{
            int maxPage = (int) Math.ceil((double) feedService.getTotalPage(page,size) / size);
            int totalPage=(int) Math.ceil( feedService.getTotalFeed());
            model.addAttribute("commentDESC",commentService.getPagingCommentDESC(feedService.getfeedUIDList(page,size),page,size));
            basicSettingFeed(model, page, size, maxPage, totalPage);
            return  "basic/comment/MostCommentDESC";
        }



    @PostMapping("/search/view/feed/delete")
    public  String deleteFeed(@RequestParam String id) {
        feedService.deleteFeed(id);
        return "redirect:/search/view/feed?index=board";
    }
    @GetMapping("/search/view/feed/reload")
    public  String reloadViewCount(Model model){

        model.addAttribute("data",feedService.getFeed());
        return "basic/feed/feedList?index=board";
    }

    private void basicSettingFeed(Model model, int page, int size, int maxPage, int totalPage) {
        model.addAttribute("viewCount",feedService.getViewCountAll());
        model.addAttribute("count",commentService.getPagingComment(feedService.getfeedUIDList(page,size),page,size));
        model.addAttribute("page", page);  // 현재 페이지 번호
        model.addAttribute("maxPage", maxPage);
        model.addAttribute("totalPage", totalPage);
        model.addAttribute("data", feedService.getPagingFeed(page, size)); // 서비스 호출 시 페이지와 크기 전달
        model.addAttribute("month",feedService.getMonthPopularFeed());
    }

    private void feedDetailParts(Model model, String id) {
        feedService.saveViewCountFeed(id);
        model.addAttribute("count",commentService.getSumComment(id));
        model.addAttribute("data",feedService.getFeedId(id));
        model.addAttribute("comment",commentService.getCommentId(id));
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