package es.board.controller;

import es.board.model.res.FeedCreateResponse;
import es.board.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
@RequiredArgsConstructor
@Slf4j
@Controller
public class FeedViewController {
    private final FeedService feedService;


    @GetMapping("/")
    public String mainPage(Model model) {

        model.addAttribute("feedSaveDTO", new FeedCreateResponse());
        return "basic/main";
    }
    @GetMapping("/search/view/feed/feedAll")
    public String getFeedList(Model model) throws IOException {
        model.addAttribute("data",feedService.getFeedList());
        return "basic/feedAll";
    }


    @GetMapping("/search/view/feed")
    public String getFeed(@RequestParam String index, Model model) throws IOException {
        model.addAttribute("feedSaveDTO", new FeedCreateResponse());
      //  model.addAttribute("data",feedService.searchBoard(index));
        return "basic/feedList";
    }


    @GetMapping("/search/view/feed/paging/{num}")
    public String getPagingFeed(@PathVariable int num, Model model) throws IOException {

        model.addAttribute("data",feedService.getPagingFeedList(num));

        return "basic/feedList";
    }


    @GetMapping("/search/view/feed/time")
    public String getRecentFeedList(Model model) throws IOException {
        model.addAttribute("data",feedService.getRecentFeed());
        return "basic/time";
    }
    @GetMapping("/search/view/feed/like")
    public String getLikeCount(Model model) throws IOException {
        model.addAttribute("data", feedService.getLikeCountList());
        return  "basic/like";
    }
    @PostMapping("/search/view/feed/save")
    public String saveFeed(Model model, FeedCreateResponse feedSaveDTO) throws IOException {
        model.addAttribute("feedSaveDTO", new FeedCreateResponse());
        feedService.saveFeed(feedSaveDTO);
        return "basic/feedList";  // 저장 후 메인 페이지로 리다이렉트
    }

//    @PostMapping("/feed/view")
//    public String indexDocument(@RequestParam String index, @RequestBody FeedCreateResponse dto)
//            throws IOException {
//        return feedService.indexFeed(index, dto);
//    }


    @PostMapping("/feed/view/bulks")
    public  List<FeedCreateResponse> postBulkFeed(@RequestBody List<FeedCreateResponse> comments) throws IOException {

        return feedService.createBulkFeed(comments);
    }

}
