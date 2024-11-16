package es.board.controller;

import es.board.model.req.ReqFeedDTO;
import es.board.model.res.FeedSaveDTO;
import es.board.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class FeedController {


    private final FeedService feedService;


    @GetMapping("/search/feed")
    public String search(@RequestParam String index) throws IOException {
        return feedService.searchBoard(index);
    }
    @GetMapping("/search/feed/paging/{num}")
    public List<ReqFeedDTO> PagingSearch(@PathVariable int num) throws IOException {

        return  feedService.PagingSearchBoard(num);
    }


    @GetMapping("/search/view/time")
    public String searchView(@RequestParam String index,Model model) throws IOException {
        model.addAttribute("data",feedService.searchBoard(index));
        return "feedList";
    }

    @GetMapping("/search/feed/time")
    public List<ReqFeedDTO> searchNewFeedDSEC() throws IOException {
        return feedService.searchTimeDESC();
    }
    @PostMapping("/feed")
    public String indexDocument(@RequestParam String index, @RequestBody FeedSaveDTO dto) throws IOException {
        return feedService.indexFeed(index, dto);
    }

    @GetMapping("/search/feed/like")
    public List<ReqFeedDTO> LikeDESC() throws IOException {
        return  feedService.LikeBoardDESCTo();
    }

    @PostMapping("/feed/bulks")
    public  List<FeedSaveDTO> BulkIndex(@RequestBody List<FeedSaveDTO> comments) throws IOException {

        return feedService.BulkBoardTo(comments);
    }

//    @GetMapping("/get/feed/{keyword}")
//    public Board getFeedId(@PathVariable("keyword") String id) {
//
//   //    log.info(crud.getFeed(id).toString());
//       return feedService.getFeed(id);
//    }


    @PostMapping("/dto/feed")
    public void postFeedDTO(@RequestBody FeedSaveDTO feedSaveDTO) {
        log.info(feedSaveDTO.toString());
        feedService.saveDTO(feedSaveDTO);

    }
//    @PutMapping("/update/feed/{id}")
//    public UpdateFeedDTO updatedFeed(@PathVariable("id") String id, @RequestBody UpdateFeedDTO update){
//
//        return  feedService.update(id,update);
//
//    }

    @DeleteMapping("/delete/{id}")
    public void deleteFeed(@PathVariable("id") String id){

        feedService.delete(id);
    }
}
