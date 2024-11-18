package es.board.controller;

import es.board.model.req.FeedRequest;
import es.board.model.res.FeedCreateResponse;
import es.board.repository.dao.FeedDAO;
import es.board.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
    @GetMapping("/search/feed/paging/{num}")
    public List<FeedRequest> PagingSearch(@PathVariable int num) throws IOException {

        return  feedService.getPagingFeed(num);
    }


//    @GetMapping("/search/view/time")
//    public String searchView(@RequestParam String index,Model model) throws IOException {
//        model.addAttribute("data",feedService.searchBoard(index));
//        return "feedList";
//    }

    @GetMapping("/search/feed/time")
    public List<FeedRequest> searchNewFeedDSEC() throws IOException {
        return feedService.getRecentFeed();
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
    public List<FeedRequest> getRange(Model model, @RequestParam String time) throws IOException{

        return       feedService.getRangeTimeFeed(time);

    }

    @PostMapping("/feed/bulks")
    public  List<FeedCreateResponse> BulkIndex(@RequestBody List<FeedCreateResponse> comments) throws IOException {

        return feedService.createBulkFeed(comments);
    }

//    @GetMapping("/get/feed/{keyword}")
//    public Board getFeedId(@PathVariable("keyword") String id) {
//
//   //    log.info(crud.getFeed(id).toString());
//       return feedService.getFeed(id);
//    }



}
