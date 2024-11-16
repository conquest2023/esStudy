package es.board.controller;


import es.board.model.req.CommentRequest;
import es.board.model.req.CommentUpdate;
import es.board.model.res.CommentCreateResponse;
import es.board.repository.document.Comment;
import es.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class LowLevelCommentController {
    private final CommentService commentService;

 
    @GetMapping("/search/paging/{num}")
    public List<CommentRequest> PagingSearch(@PathVariable int num) throws IOException {
        return  commentService.getPagingComment(num);
    }
    @GetMapping("/searchs/{text}")
    public List<Comment> searchText(@PathVariable String text) throws IOException {
        return  commentService.getSearchComment(text);
    }

    @GetMapping("/search/like")
    public List<CommentRequest> LikeDESC() throws IOException {
        return  commentService.getLikeCount();
    }
    @PostMapping("/index")
    public String indexDocument(@RequestParam String index, @RequestBody CommentCreateResponse dto) throws IOException {
        return commentService.saveDocument(index, dto);
    }
    @PostMapping("/bulks")
    public  List<CommentCreateResponse> BulkIndex(@RequestBody List<CommentCreateResponse> comments) throws IOException {

        return commentService.createBulkComment(comments);
    }


    @PutMapping("/edit/{id}")
    public List<Comment> EditEx(@PathVariable String id, @RequestBody CommentUpdate eq) throws IOException {

        return commentService.editComment(id,eq);
    }



}
