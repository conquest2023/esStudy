package es.board.controller;

import es.board.model.req.ReqCommentDTO;
import es.board.model.req.ReqFeedDTO;
import es.board.model.req.UpdateCommentDTO;
import es.board.model.req.UpdateFeedDTO;
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
