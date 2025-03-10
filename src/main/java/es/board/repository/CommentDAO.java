package es.board.repository;

import es.board.controller.model.res.CommentCreate;
import es.board.repository.document.Board;
import es.board.repository.document.Comment;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
public interface CommentDAO {
    Map<String,Long> findTodayCommentAggregation();
    Map<String, Object> findCommentsWithCount(String feedUID);
    Comment findCommentUID(String commentId);

    Map<String, Object> findUserComments(String userId);
    List<Comment> findUserRangeActive(String userId);

    List<Board> findFeedAndCommentMypage(String userId,int page ,int size);

    double findUserCommentCount(String userId);

    String createCommentOne(String index, CommentCreate dto);

    List<Comment> findRecentComment();

    void saveCommentIndex(CommentCreate dto);

    List<Comment> CreateManyComment(List<Comment> pages);

    List<Comment> findSearchComment(String text);

    List<Comment> findLikeCount();

    List<Comment> findMyPagePagingComment(String userId,int page,int size);

    Map<String, Double> findPagingCommentDESC(List<String> feedUIDs, int page, int size);

    void deleteCommentId(String id);

    int findSumComment(String id);

    List<Comment> findDetailComment(String id);

    Map<String,Double> findPagingComment(List<String> feedUIDs, int page, int size);

    Comment modifyComment(String id, Comment comment) ;

    List<Comment> findCommentAll();

    List<Comment>  findCommentId(String commentUid);

}
