package es.board.repository.domain;

import es.board.model.req.ReqCommentDTO;
import es.board.repository.entity.Board;
import es.board.repository.entity.Comment;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CommentDAO {


    void CommentSaveRepo(Comment dto);

    void CommentRemoveRepo(String commentUid);

    List<Comment> CommentBringRepo();


    Comment CommentEdit(Comment updateDTO);


  Comment getCommentId(String commentUid);
}
