package es.board.controller;

import es.board.model.res.FeedCreateResponse;
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
    private int nextPage=0;
    private final FeedService feedService;


    @GetMapping("/")
    public String mainPage(Model model) {

        model.addAttribute("feedSaveDTO", new FeedCreateResponse());
        return "basic/main";
    }
    @GetMapping("/search/view/feed/id")
    public String getFeedId(Model model,@RequestParam String id) throws IOException {
        log.info(feedService.getFeedId(id).toString());
        model.addAttribute("data",feedService.getFeedId(id));
        return "basic/FeedDetails";
    }
    @GetMapping("/search/view/feed/feedAll")
    public String getFeedList(Model model) throws IOException {


        model.addAttribute("data",feedService.getFeed());
        return "basic/feedList";
    }
    @GetMapping("/search/view/feed/text")
    public String getSearchBoardList(Model model, @RequestParam String text) throws IOException {
        log.info(feedService.getSearchBoard(text).toString());
        model.addAttribute("data",feedService.getSearchBoard(text));
        return "basic/SearchFeed";
    }

    @GetMapping("/search/view/feed")
    public String getFeed(Model model,
                          @RequestParam(defaultValue = "0") int page, // 페이지 번호 (0부터 시작)
                          @RequestParam(defaultValue = "8") int size) throws IOException { // 페이지 크기
        model.addAttribute("page",page);  // 현재 페이지 번호
        model.addAttribute("maxPage", size);  // 한 페이지당 데이터 개수
        model.addAttribute("data", feedService.getPagingFeed(page, size)); // 서비스 호출 시 페이지와 크기 전달
        return "basic/feedList";
    }



    @GetMapping("/search/view/feed/time")
    public String getRecentFeedList(Model model) throws IOException {
        model.addAttribute("data",feedService.getRecentFeed());
        return "basic/RecentFeed";
    }
    @GetMapping("/search/view/feed/like")
    public String getLikeCountList(Model model) throws IOException {
        model.addAttribute("data", feedService.getLikeCount());
        return "basic/LikeFeed";
    }
    @GetMapping("/search/view/feed/range")
    public  String getRangeTime(Model model, @RequestParam LocalDateTime startDate
            , @RequestParam LocalDateTime endDate ) throws IOException{

        model.addAttribute("data",feedService.getRangeTimeFeed(startDate,endDate));
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
