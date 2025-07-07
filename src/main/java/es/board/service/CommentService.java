package es.board.service;

import es.board.controller.model.req.CommentUpdate;
import es.board.controller.model.req.FeedDTO;
import es.board.controller.model.res.CommentDTO;
import es.board.repository.document.Board;
import es.board.repository.document.Comment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface CommentService {


    List<Board> getMantComment();
    Map<String,Long> getTodayCommentAggregation();
    Map<String, Object> getUserComments(String userId);
    List<CommentDTO.Request> getUserRangeTimeActive(String userId);
    List<FeedDTO.Request> getFeedAndCommentMyPage(String userId, int page , int size);
//    String saveDocument(String indexName, CommentDTO dto) ;

    List<Comment> getSearchComment(String text) ;

//    double getUserCommentCount(String userId);
    Comment editComment(String id, CommentUpdate eq) ;

    List<CommentDTO.Request> getRecentComment() ;

    void saveComment(CommentDTO.Response dto) ;

    List<CommentDTO.Request> getCommentOne(String commentUID);
    void  plusCommentLike(String id);
    List<Comment> getMostCommentCount() ;
    List<CommentDTO.Response> saveBulkComment(List<CommentDTO.Response> comments) ;

    List<Comment> getMyPageComment(String  userId, int num ,int size);
    Map<String, Object> findCommentsWithCount(String feedUID);

    List<CommentDTO.Request> getLikeCount() ;

    Map<String, Double> getCommentAndReplyAggregation(List<String> feedUIDs , int num, int size) ;


//    List<CommentRequest> getComment() ;


//    List<Comment> getCommentId(String id) ;


   // List<CommentSearchRequest> SearchComment(String keyword);

    void deleteComment(String id) ;

}
