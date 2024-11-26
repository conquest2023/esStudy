package es.board.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import es.board.model.req.FeedRequest;
import es.board.model.res.FeedCreateResponse;
import es.board.repository.dao.FeedDAO;
import es.board.repository.document.Board;
import es.board.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FeedController {


    private final FeedService feedService;


//    @GetMapping("/search/feed")
//    public String search(@RequestParam String index) throws IOException {
//        return feedService.searchBoard(index);
//    }
//    @GetMapping("/search/feed/paging/{num}")
//    public List<FeedRequest> PagingSearch(@PathVariable int num) throws IOException {
//
//        return  feedService.getPagingFeed(page,size);
//    }


//    @GetMapping("/search/view/time")
//    public String searchView(@RequestParam String index,Model model) throws IOException {
//        model.addAttribute("data",feedService.searchBoard(index));
//        return "feedList";
//    }

    @GetMapping("/search/feed/time")
    public List<FeedRequest> searchNewFeedDSEC() throws IOException {
        log.info(feedService.getRecentFeed().toString());
        return feedService.getRecentFeed();
    }

    @GetMapping("/search/feedId")
    public FeedRequest getFeedIdEx(Model model,@RequestParam String FeedUID) throws IOException {
        model.addAttribute("getFeedId",feedService.getFeedId(FeedUID));
        return feedService.getFeedId(FeedUID);
    }

    @PostMapping("/feed")
    public String indexDocument(@RequestParam String index, @RequestBody FeedCreateResponse dto) throws IOException {
        return feedService.createFeed(index, dto);
    }

    @GetMapping("/search/feed/like")
    public List<FeedRequest> LikeDESC() throws IOException {
        return  feedService.getLikeCount();
    }

    @GetMapping("/search/range")
    public List<FeedRequest> getRange(Model model, @RequestParam LocalDateTime startDate
            , @RequestParam LocalDateTime endDate ) throws IOException{
            log.info(startDate.toString());
        return       feedService.getRangeTimeFeed(startDate,endDate);

    }

    @PostMapping("/feed/bulks")
    public  List<FeedCreateResponse> BulkIndex(@RequestBody List<FeedCreateResponse> comments) throws IOException {

        return feedService.createBulkFeed(comments);
    }


    @GetMapping("/test")
    public FeedRequest test() throws IOException {

        return feedService.getPopularFeedOne();
    }
//    @GetMapping("/get/feed/{keyword}")
//    public Board getFeedId(@PathVariable("keyword") String id) {
//
//   //    log.info(crud.getFeed(id).toString());
//       return feedService.getFeed(id);
//    }



}
