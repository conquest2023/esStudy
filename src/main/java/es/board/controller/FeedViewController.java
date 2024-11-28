package es.board.controller;

import es.board.model.req.FeedUpdate;
import es.board.model.res.FeedCreateResponse;
import es.board.service.CommentService;
import es.board.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;


@RequiredArgsConstructor
@Slf4j
@Controller
public class FeedViewController {

    private final FeedService feedService;

    private  final CommentService commentService;


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

    @GetMapping("/search/view/feed/popularfeed")
    public String getMonthPopular(Model model) throws Exception {
        return "basic/feed/feedList";
    }

    @PostMapping("/search/view/feed/update/save")
    public String editSaveFeed(Model model,@ModelAttribute FeedUpdate feedUpdate) throws Exception {
        log.info(feedUpdate.toString());
        feedService.updateFeed(feedUpdate.getFeedUID(),feedUpdate);
        model.addAttribute("data", feedUpdate);
        return  "redirect:/search/view/feed?index=board";
    }


    @GetMapping("/search/view/feed/id")
    public String getFeedId(Model model,@RequestParam String id) throws IOException {
        log.info(feedService.getFeedId(id).toString());
        model.addAttribute("data",feedService.getFeedId(id));
        model.addAttribute("comment",commentService.getCommentId(id));
        return "basic/feed/FeedDetails";
    }
    @GetMapping("/search/view/feed/feedAll")
    public String getFeedList(Model model) throws IOException {
        model.addAttribute("data",feedService.getFeed());
        return "basic/feed/feedList";
    }
    @GetMapping("/search/view/feed/text")
    public String getSearchBoardList(Model model, @RequestParam String text) throws IOException {
        log.info(feedService.getSearchBoard(text).toString());
        model.addAttribute("data",feedService.getSearchBoard(text));
        return "basic/feed/SearchFeed";
    }

    @GetMapping("/search/view/feed")
    public String getFeed(Model model,
                          @RequestParam(defaultValue = "0") int page, // 페이지 번호 (0부터 시작)
                          @RequestParam(defaultValue = "8") int size) throws IOException { // 페이지 크기

        int maxPage = (int) Math.ceil((double) feedService.getTotalPage(page,size) / size);
        model.addAttribute("page",page);  // 현재 페이지 번호
        model.addAttribute("maxPage", maxPage);
        // model.addAttribute("totalLikePage",feedService.getSumLikeByPageOne(page,maxPage));
        //  log.info(String.valueOf(feedService.getSumLikeByPageOne(page,maxPage)));
        model.addAttribute("data", feedService.getPagingFeed(page, size)); // 서비스 호출 시 페이지와 크기 전달
        model.addAttribute("month",feedService.getMonthPopularFeed());

        return "basic/feed/feedList";
    }


    @GetMapping("/search/view/feed/time")
    public String getRecentFeedList(Model model) throws IOException {
        model.addAttribute("data",feedService.getRecentFeed());
        return "basic/feed/RecentFeed";
    }
    @GetMapping("/search/view/feed/like")
    public String getLikeCountList(Model model) throws IOException {
        model.addAttribute("data", feedService.getLikeCount());
        return "basic/feed/LikeFeed";
    }

    @GetMapping("/search/view/feed/range")
    public  String getRangeTime(Model model, @RequestParam LocalDateTime startDate
            , @RequestParam LocalDateTime endDate ) throws IOException{

        model.addAttribute("data",feedService.getRangeTimeFeed(startDate,endDate));
        return  "basic/feed/RangeTime";
    }
    @PostMapping("/search/view/feed/save")
    public String saveFeed(Model model, FeedCreateResponse feedSaveDTO) throws IOException {
        model.addAttribute("res",feedService.saveFeed(feedSaveDTO));
        return "redirect:/search/view/feed?index=board";   // 저장 후 메인 페이지로 리다이렉트
    }
    @GetMapping("/search/view/feed/Form")
    public String feedSaveForm(Model model, FeedCreateResponse feedSaveDTO) throws IOException {
        model.addAttribute("FeedCreateResponse", new FeedCreateResponse());
        return  "basic/feed/PostFeed";
    }


    @PostMapping("/feed/view/bulks")
    public  List<FeedCreateResponse> postBulkFeed(@RequestBody List<FeedCreateResponse> comments) throws IOException {

        return feedService.createBulkFeed(comments);
    }

}
