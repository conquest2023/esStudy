package es.board.controller;

import es.board.model.req.ReqFeedDTO;
import es.board.model.req.UpdateFeedDTO;
import es.board.model.res.FeedSaveDTO;
import es.board.repository.entity.Board;
import es.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BoardController {


    private final BoardService crud;

    @GetMapping("/get/feed/{keyword}")
    public Board getFeedId(@PathVariable("keyword") String id) {

   //    log.info(crud.getFeed(id).toString());
       return crud.getFeed(id);
    }
    @GetMapping("/get/feedAll")
    public List<ReqFeedDTO> getFeedAll() {

        return crud.getFeedAll();

    }


    @PostMapping("/post/feed")
    public void postFeed(@RequestBody Board board) {
        log.info(board.toString());
        crud.save(board);
    }

    @PostMapping("/dto/feed")
    public void postFeedDTO(@RequestBody FeedSaveDTO feedSaveDTO) {
        log.info(feedSaveDTO.toString());
        crud.saveDTO(feedSaveDTO);

    }
    @PutMapping("/update/feed/{id}")
    public UpdateFeedDTO updatedFeed(@PathVariable("id") String id, @RequestBody UpdateFeedDTO update){

        return  crud.update(id,update);

    }

    @DeleteMapping("/delete/{id}")
    public void deleteFeed(@PathVariable("id") String id){

        crud.delete(id);
    }
}
