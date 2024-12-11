package es.board.controller;

import es.board.model.file.FileStore;
import es.board.model.req.FeedUpdate;
import es.board.model.res.CommentCreateResponse;
import es.board.model.res.FeedCreateResponse;
import es.board.service.CommentService;
import es.board.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@RequiredArgsConstructor
@Slf4j
@Controller
public class FeedViewController {
    @Value("%{file.dir}")
    private  String fileDir;


    private final FeedService feedService;

    private  final CommentService commentService;

    private  final FileStore fileStore;


    @GetMapping("/")
    public String mainPage(Model model) {
        model.addAttribute("feedSaveDTO", new FeedCreateResponse());
        return "basic/feed/main";
    }

//    @GetMapping("/search/view/feed/update")
//    public String editFeed(Model model,@RequestParam String id,
//                           @RequestBody FeedUpdate feedUpdate) throws Exception {
//
//        feedService.updateFeed(id,feedUpdate);
//        model.addAttribute("data", new FeedUpdate());
//        return "basic/EditFeed";
//    }
    @GetMapping("/search/view/feed/update")
    public String editFeed(@RequestParam("id") String id, @RequestParam("username") String username, Model model) throws Exception {
        model.addAttribute("id", id);
        model.addAttribute("username", username);
        return "basic/feed/EditFeed";
    }

    @GetMapping("/search/view/feed/category")
    public String getCategory(Model model,@RequestParam String text) throws Exception {
        model.addAttribute("data",feedService.getCategoryFeed(text));
        return "basic/feed/CategoryFeed";
    }

//    @GetMapping("/search/view/feed/popularfeed")
//    public String getMonthPopular(Model model) throws Exception {
//        return "FeedList";
//    }

    @PostMapping("/search/view/feed/update/save")
    public String editSaveFeed(Model model,@ModelAttribute FeedUpdate feedUpdate) throws Exception {
        log.info(feedUpdate.toString());
        feedService.updateFeed(feedUpdate.getFeedUID(),feedUpdate);
        model.addAttribute("data", feedUpdate);
        return  "redirect:/search/view/feed/id?id=" +feedUpdate.getFeedUID();
    }


    @GetMapping("/search/view/feed/id")
    public String getFeedDetail(Model model,@RequestParam String id) throws IOException {
        feedService.saveViewCountFeed(id);
        model.addAttribute("data",feedService.getFeedId(id));
        model.addAttribute("comment",commentService.getCommentId(id));
        model.addAttribute("feedId", id);
        return "basic/feed/FeedDetails";
    }
    @PostMapping("/search/view/feed/id")
    public String saveCommentId(@RequestParam String id,
                                   @ModelAttribute CommentCreateResponse commentSaveDTO,
                                   Model model) throws IOException {
        commentSaveDTO.setFeedUID(id);
        commentService.indexComment(commentSaveDTO);
        model.addAttribute("push",commentSaveDTO);
        return "redirect:/search/view/feed/id?id=" + id;
    }

    @GetMapping("/search/view/feed/feedAll")
    public String getFeedList(Model model, @RequestBody Map<String, String> request) throws IOException {

        String feedUID = request.get("feedUID");
        if (feedUID != null) {
            feedService.saveViewCountFeed(feedUID);
        }
        model.addAttribute("data",feedService.getFeed());
        return "basic/feed/feedList?index=board";
    }
    @GetMapping("/search/view/feed/text")
    public String getSearchBoardList(Model model, @RequestParam String text) throws IOException {
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
    public String getCategoryListFeed(Model model, @PathVariable String category) throws IOException {
        model.addAttribute("data",feedService.getCategoryFeed(category));

        return "/basic/feed/CategoryFeed";
    }

    @GetMapping("/search/view/feed/latest")
    public String getRecentFeedList(Model model) throws IOException {
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
            , @RequestParam LocalDateTime endDate ) throws IOException{
        model.addAttribute("data",feedService.getRangeTimeFeed(startDate,endDate));
        return "/basic/feed/RangeFeed";
    }
    @PostMapping("/search/view/feed/save")
    public String saveFeed( Model model, @ModelAttribute FeedCreateResponse feedSaveDTO) throws IOException {
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
    public  List<FeedCreateResponse> postBulkFeed(@RequestBody List<FeedCreateResponse> comments) throws IOException {

        return feedService.createBulkFeed(comments);
    }

    @PostMapping("/search/view/feed/delete")
    public  String deleteFeed(@RequestParam String id) throws IOException {
        feedService.deleteFeed(id);
        return "redirect:/search/view/feed?index=board";
    }
    @GetMapping("/search/view/feed/reload")
    public  String reloadViewCount(Model model) throws  IOException{

        model.addAttribute("data",feedService.getFeed());
        return "basic/feed/feedList?index=board";
    }

    private void basicSettingFeed(Model model, int page, int size, int maxPage, int totalPage) throws IOException {
        model.addAttribute("count",commentService.getPagingComment(feedService.getfeedUIDList(page,size),page,size));
        model.addAttribute("page", page);  // 현재 페이지 번호
        model.addAttribute("maxPage", maxPage);
        model.addAttribute("totalPage", totalPage);
        model.addAttribute("data", feedService.getPagingFeed(page, size)); // 서비스 호출 시 페이지와 크기 전달
        model.addAttribute("month",feedService.getMonthPopularFeed());
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