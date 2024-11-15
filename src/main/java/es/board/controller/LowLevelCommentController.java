package es.board.controller;


import es.board.model.req.ReqCommentDTO;
import es.board.model.req.UpdateCommentDTO;
import es.board.model.res.CommentSaveDTO;
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

    @GetMapping("/search")
    public String search(@RequestParam String index) throws IOException {
        return commentService.searchIndex(index);
    }
    @GetMapping("/search/paging/{num}")
    public List<ReqCommentDTO> PagingSearch(@PathVariable int num) throws IOException {
        return  commentService.PagingSearchIndex(num);
    }
    @GetMapping("/searchs/{text}")
    public List<Comment> searchText(@PathVariable String text) throws IOException {
        return  commentService.SearchTextEx(text);
    }

    @GetMapping("/search/like")
    public List<ReqCommentDTO> LikeDESC() throws IOException {
        return  commentService.LikeDESCTo();
    }
    @PostMapping("/index")
    public String indexDocument(@RequestParam String index, @RequestBody CommentSaveDTO dto) throws IOException {
        return commentService.indexDocument(index, dto);
    }
    @PostMapping("/bulks")
    public  List<CommentSaveDTO> BulkIndex(@RequestBody List<CommentSaveDTO> comments) throws IOException {

        return commentService.BulkIndexTo(comments);
    }


    @PutMapping("/edit/{id}")
    public List<Comment> EditEx(@PathVariable String id, @RequestBody UpdateCommentDTO eq) throws IOException {

        return commentService.EditCommentEx(id,eq);
    }



}
