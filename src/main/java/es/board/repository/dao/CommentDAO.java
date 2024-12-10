package es.board.repository.dao;

import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import es.board.model.res.CommentCreateResponse;
import es.board.repository.document.Board;
import es.board.repository.document.Comment;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@Repository
public interface CommentDAO {


    String createCommentOne(String index, CommentCreateResponse dto) throws IOException;

    List<Comment> findRecentComment() throws IOException;


    String indexCommentSave(CommentCreateResponse dto) throws IOException;


    List<Comment> CreateManyComment(List<Comment> pages) throws IOException;

    List<Comment> findSearchComment(String text) throws IOException;

    List<Comment> findLikeCount() throws IOException;

    List<Comment> findPagingComment(int num) throws IOException;


    void deleteCommentId(String id) throws IOException;
    List<Comment> findIdOne(String id) throws IOException ;

    Map<String, Long> findPagingComment(List<String> feedUIDs, int page, int size) throws IOException;

    Comment modifyComment(String id, Comment comment) throws Exception;

     List<Comment> findCommentAll() throws IOException, ElasticsearchException;

    Comment findCommentId(String commentUid);

}
