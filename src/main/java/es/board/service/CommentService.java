package es.board.service;

import es.board.controller.model.req.CommentRequest;
import es.board.controller.model.req.CommentUpdate;
import es.board.controller.model.res.CommentCreateResponse;
import es.board.repository.document.Comment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface CommentService {


    public String saveDocument(String indexName, CommentCreateResponse dto) ;

    List<Comment> getSearchComment(String text) ;

    double getUserCommentCount(String userId);
    Comment editComment(String id, CommentUpdate eq) ;

    List<CommentRequest> getRecentComment() ;

    String indexComment(CommentCreateResponse dto) ;

    CommentRequest getCommentOne(String commentUID);
    void  plusCommentLike(String id);
    Map<String, Long> getPagingCommentDESC(List<String> feedUIDs, int num, int size) ;
    List<CommentCreateResponse> createBulkComment(List<CommentCreateResponse> comments) ;


    int getSumComment(String id);

    List<CommentRequest> getLikeCount() ;

    Map<String, Long> getPagingComment(List<String> feedUIDs , int num, int size) ;


    List<CommentRequest> getComment() ;


    List<Comment> getCommentId(String id) ;


   // List<CommentSearchRequest> SearchComment(String keyword);

    void deleteComment(String id) ;

}
