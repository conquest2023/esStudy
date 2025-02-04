package es.board.service;

import es.board.controller.model.req.CommentRequest;
import es.board.controller.model.req.CommentUpdate;
import es.board.controller.model.req.FeedRequest;
import es.board.controller.model.res.CommentCreate;
import es.board.repository.document.Comment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface CommentService {

    List<CommentRequest> getUserRangeTimeActive(String userId);
    List<FeedRequest>  getFeedAndComment(String userId);
    public String saveDocument(String indexName, CommentCreate dto) ;

    List<Comment> getSearchComment(String text) ;

    double getUserCommentCount(String userId);
    Comment editComment(String id, CommentUpdate eq) ;

    List<CommentRequest> getRecentComment() ;

    void indexComment(CommentCreate dto) ;

    List<CommentRequest> getCommentOne(String commentUID);
    void  plusCommentLike(String id);
    Map<String, Double> getPagingCommentDESC(List<String> feedUIDs, int num, int size) ;
    List<CommentCreate> createBulkComment(List<CommentCreate> comments) ;


    int getSumComment(String id);

    List<CommentRequest> getLikeCount() ;

    Map<String, Double> getPagingComment(List<String> feedUIDs , int num, int size) ;


    List<CommentRequest> getComment() ;


    List<Comment> getCommentId(String id) ;


   // List<CommentSearchRequest> SearchComment(String keyword);

    void deleteComment(String id) ;

}
