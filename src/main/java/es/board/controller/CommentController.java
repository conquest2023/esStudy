package es.board.controller;

import es.board.model.req.*;
import es.board.model.res.CommentCreateResponse;
import es.board.repository.document.Comment;
import es.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

//    @GetMapping("/search/{id}")
//    public Comment searchGetId(@PathVariable String id) throws IOException{
//        return commentService.getCommentId(id);
//    }
//    // 문서 색인

}
