package es.board.repository.domain;

import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import es.board.model.req.ReqCommentDTO;
import es.board.repository.entity.Board;
import es.board.repository.entity.Comment;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@Repository
public interface CommentDAO {

    Map<String, Object> search(String index)  throws IOException, ElasticsearchException;
    String indexDocument(String index, Map<String, Object> document) throws IOException;

    void CommentSaveRepo(Comment dto);

    void CommentRemoveRepo(String commentUid);

    List<Comment> CommentBringRepo();

    List<Comment> SearchCommentBring(String keyword);

    Comment CommentEdit(Comment updateDTO);


    Comment getCommentId(String commentUid);

    List<Comment> SearchCommentScore(String score);
}
