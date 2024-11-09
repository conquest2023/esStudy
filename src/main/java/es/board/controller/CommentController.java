package es.board.controller;

import es.board.model.req.*;
import es.board.model.res.CommentSaveDTO;
import es.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

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
