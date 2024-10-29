package es.board.controller;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.board.model.req.ReqFeedDTO;
import es.board.model.res.FeedSaveDTO;
import es.board.repository.entity.Board;
import es.board.service.crudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class crudController {


    private final crudService crud;

    @GetMapping("/get/feed/{keyword}")
    public List<ReqFeedDTO> getFeedId(@PathVariable("keyword") String id) {


   //    log.info(crud.getFeed(id).toString());
       return crud.getFeed(id);
    }
    @GetMapping("/get/feedAll")
    public List<ReqFeedDTO> getFeedAll() {

        log.info(crud.getFeedAll().toString());

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

    @DeleteMapping("/delete/{id}")
    public void deleteFeed(@PathVariable("id") String id){

        crud.delete(id);
    }
}
