package es.board.controller;

import es.board.model.req.*;
import es.board.model.res.CommentSaveDTO;
import es.board.repository.entity.Comment;
import es.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@RestController
@RequiredArgsConstructor
public class CommentController {

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


    @PutMapping("/edit/{id}")
    public List<Comment> EditEx(@PathVariable String id, @RequestBody UpdateCommentDTO eq) throws IOException {

        return commentService.EditCommentEx(id,eq);
    }

    @GetMapping("/search/{id}")
    public Comment searchGetId(@PathVariable String id) throws IOException{
        return commentService.SearchId(id);
    }
    @PostMapping("/bulks")
    public  List<CommentSaveDTO> BulkIndex(@RequestBody List<CommentSaveDTO> comments) throws IOException {

        return commentService.BulkIndexTo(comments);
    }
    // 문서 색인
    @PostMapping("/index")
    public String indexDocument(@RequestParam String index, @RequestBody CommentSaveDTO dto) throws IOException {
        return commentService.indexDocument(index, dto);
    }
    @PostMapping("/save/comment")
    public void CommentSave(@RequestBody CommentSaveDTO commentSaveDTO){
        commentService.CommentSave(commentSaveDTO);
    }

    @GetMapping("/get/comment/{keyword}")
    public List<ReqSearchCommentDTO> CommentSearch( @PathVariable("keyword") String keyword)
    {
        return  commentService.SearchComment(keyword);
    }

    @GetMapping("/get/comment/score/{score}")
    public List<ReqCommentDTO>  CommentScore(@PathVariable("score") String score){

        return commentService.CommentScore(score);
    }



    @GetMapping("/get/comment")
    public List<ReqCommentDTO> CommentGet(){

        return commentService.CommentBring();
    }
    @PutMapping("/update/comment/{CommentUID}")
    public UpdateCommentDTO CommentUpdate(@PathVariable String CommentUID,@RequestBody UpdateCommentDTO updateCommentDTO) {

            return commentService.CommentEdit(CommentUID,updateCommentDTO);
    }

    @DeleteMapping("/delete/comment/{id}")
    public  void CommentRemove(String commentUid){

        commentService.CommentRemoveId(commentUid);
    }
}
