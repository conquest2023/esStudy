package es.board.controller;

import es.board.model.res.FeedCreateResponse;
import es.board.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
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
        model.addAttribute("data",feedService.getFeed());
        return "basic/feedList";
    }


    @GetMapping("/search/view/feed")
    public String getFeed(@RequestParam String index, Model model) throws IOException {
        model.addAttribute("data",feedService.getFeed());
        //  model.addAttribute("data",feedService.searchBoard(index));
        return "basic/feedList";
    }


    @GetMapping("/search/view/feed/paging/{num}")
    public String getPagingFeedList(@PathVariable int num, Model model) throws IOException {

        model.addAttribute("data",feedService.getPagingFeed(num));

        return "basic/feedList";
    }


    @GetMapping("/search/view/feed/time")
    public String getRecentFeedList(Model model) throws IOException {
        model.addAttribute("data",feedService.getRecentFeed());
        return "basic/time";
    }
    @GetMapping("/search/view/feed/like")
    public String getLikeCountList(Model model) throws IOException {
        model.addAttribute("data", feedService.getLikeCount());
        return  "basic/like";
    }
    @GetMapping("/search/view/feed/range")
    public  String getRangeTime(Model model,@RequestParam String time) throws IOException{

        model.addAttribute("data",feedService.getRangeTimeFeed(time));
        return  "basic/RangeTime";
    }

    @PostMapping("/search/view/feed/save")
    public String saveFeed(Model model, FeedCreateResponse feedSaveDTO) throws IOException {
        model.addAttribute("res",feedService.saveFeed(feedSaveDTO));
        return "redirect:/search/view/feed?index=board";   // 저장 후 메인 페이지로 리다이렉트
    }

    @GetMapping("/search/view/feed/Form")
    public String FeedSaveForm(Model model, FeedCreateResponse feedSaveDTO) throws IOException {
        model.addAttribute("FeedCreateResponse", new FeedCreateResponse());
        return  "basic/PostFeed";
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
