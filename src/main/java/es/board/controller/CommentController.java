package es.board.controller;

import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
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

    @PutMapping("/edit/{id}")
    public List<Comment> EditEx(@PathVariable String id, @RequestBody UpdateCommentDTO eq) throws IOException {

        return commentService.EditCommentEx(id,eq);
    }


    @GetMapping("/searchs/{indexName}/{text}")
    public List<Comment> searchText(@PathVariable String text
                              , @PathVariable String indexName) throws IOException {
        return  commentService.SearchTextEx(indexName,text);
    }

    @GetMapping("/search/{indexName}/{id}")
    public Comment searchPractice(@PathVariable String indexName, @PathVariable String id) throws IOException{
        return commentService.PracticeSearch(indexName,id);
    }


    @PostMapping("/bulks")
    public  List<Comment> BulkIndex(@RequestBody List<Comment> comments) throws IOException {

        return commentService.BulkIndexTo(comments);
    }


    // 문서 색인
    @PostMapping("/index")
    public String indexDocument(@RequestParam String index, @RequestBody Map<String, Object> document) throws IOException {
        return commentService.indexDocument(index, document);
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
