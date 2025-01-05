package es.board.repository;

import es.board.controller.model.res.CommentCreateResponse;
import es.board.repository.document.Comment;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
public interface CommentDAO {


    double findUserCommentCount(String userId);

    String createCommentOne(String index, CommentCreateResponse dto);

    List<Comment> findRecentComment();

    String indexCommentSave(CommentCreateResponse dto);

    List<Comment> CreateManyComment(List<Comment> pages);

    List<Comment> findSearchComment(String text);

    List<Comment> findLikeCount();

    List<Comment> findPagingComment(int num);

    Map<String, Long> findPagingCommentDESC(List<String> feedUIDs, int page, int size);

    void deleteCommentId(String id);

    int findSumComment(String id);

    List<Comment> findDetailComment(String id);

    Map<String, Long> findPagingComment(List<String> feedUIDs, int page, int size);

    Comment modifyComment(String id, Comment comment) ;

    List<Comment> findCommentAll();

    Comment findCommentId(String commentUid);

}
