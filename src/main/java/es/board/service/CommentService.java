package es.board.service;

import es.board.model.req.ReqCommentDTO;
import es.board.model.req.UpdateCommentDTO;
import es.board.model.res.CommentSaveDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CommentService {


    void CommentSave(CommentSaveDTO commentSaveDTO);

    void CommentRemoveId(String commentUid);


    List<ReqCommentDTO> CommentBring();

    UpdateCommentDTO CommentEdit(String CommentUID,UpdateCommentDTO updateCommentDTO);
}
