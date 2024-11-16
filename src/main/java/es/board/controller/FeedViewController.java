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
@RequiredArgsConstructor
@Slf4j
@Controller
public class FeedViewController {
    private final FeedService feedService;


    @GetMapping("/")
    public String mainPage(Model model) {

        model.addAttribute("feedSaveDTO", new FeedSaveDTO());
        return "basic/main";
    }
    @GetMapping("/search/view/feed/feedAll")
    public String searchAll(Model model) throws IOException {
        model.addAttribute("data",feedService.searchAll());
        return "basic/feedAll";
    }


    @GetMapping("/search/view/feed")
    public String search(@RequestParam String index,Model model) throws IOException {
        model.addAttribute("feedSaveDTO", new FeedSaveDTO());
        model.addAttribute("data",feedService.searchBoard(index));
        return "basic/feedList";
    }


    @GetMapping("/search/view/feed/paging/{num}")
    public String PagingSearch(@PathVariable int num,Model model) throws IOException {

        model.addAttribute("data",feedService.PagingSearchBoard(num));

        return "basic/feedList";
    }


    @GetMapping("/search/view/feed/time")
    public String searchNewFeedDSEC(Model model) throws IOException {
        model.addAttribute("data",feedService.searchTimeDESC());
        return "basic/time";
    }
    @GetMapping("/search/view/feed/like")
    public String LikeDESC(Model model) throws IOException {
        model.addAttribute("data", feedService.LikeBoardDESCTo());
        return  "basic/like";
    }
    @PostMapping("/search/view/feed/save")
    public String saveFeed(Model model, FeedSaveDTO feedSaveDTO) throws IOException {
        model.addAttribute("feedSaveDTO", new FeedSaveDTO());
        feedService.SaveFeed(feedSaveDTO);
        return "basic/feedList";  // 저장 후 메인 페이지로 리다이렉트
    }
    @PostMapping("/feed/view")
    public String indexDocument(@RequestParam String index, @RequestBody FeedSaveDTO dto)
            throws IOException {
        return feedService.indexFeed(index, dto);
    }


    @PostMapping("/feed/view/bulks")
    public  List<FeedSaveDTO> BulkIndex(@RequestBody List<FeedSaveDTO> comments) throws IOException {

        return feedService.BulkBoardTo(comments);
    }




    @PostMapping("/dto/view/feed")
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

    @DeleteMapping("/delete/view/{id}")
    public void deleteFeed(@PathVariable("id") String id){

        feedService.delete(id);
    }
}
