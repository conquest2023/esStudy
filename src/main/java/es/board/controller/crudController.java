package es.board.controller;

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

    @GetMapping("/get/feed/{username}")
    public List<Board> getFeed(@PathVariable("username") String username) {


       log.info(crud.getFeed(username).toString());
       return crud.getFeed(username);
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
}
