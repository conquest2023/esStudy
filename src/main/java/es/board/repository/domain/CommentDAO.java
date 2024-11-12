package es.board.repository.domain;

import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import es.board.model.req.ReqCommentDTO;
import es.board.model.req.UpdateCommentDTO;
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


    List<Comment> SearchTextBring(String indexName,String text) throws IOException;

    List<Comment> EditCommentEx(String id,Comment comment) throws IOException;
    Comment PracticeSearch(String indexName, String id) throws IOException;

    List<Comment> BulkIndex(List<Comment> pages) throws IOException;

    void CommentSaveRepo(Comment dto);

    void CommentRemoveRepo(String commentUid);

    List<Comment> CommentBringRepo();


    List<Comment> SearchCommentBring(String keyword);

    Comment CommentEdit(Comment updateDTO);


    Comment getCommentId(String commentUid);

    List<Comment> SearchCommentScore(String score);
}
