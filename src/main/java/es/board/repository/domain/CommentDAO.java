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

    List<Comment> SearchCommentBring(String keyword);

    Comment CommentEdit(Comment updateDTO);


    Comment getCommentId(String commentUid);

    List<Comment> SearchCommentScore(String score);
}
