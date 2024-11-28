package es.board.service;

import es.board.model.req.CommentRequest;
import es.board.model.req.CommentUpdate;
import es.board.model.req.FeedRequest;
import es.board.model.res.CommentCreateResponse;
import es.board.repository.document.Comment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public interface CommentService {


    public String saveDocument(String indexName, CommentCreateResponse dto) throws IOException;

    List<Comment> getSearchComment(String text) throws IOException;

    List<Comment> editComment(String id, CommentUpdate eq) throws IOException;

    List<CommentRequest> getRecentComment() throws IOException;

    String indexComment(CommentCreateResponse dto) throws IOException;


    List<CommentCreateResponse> createBulkComment(List<CommentCreateResponse> comments) throws IOException;

    List<CommentRequest> getLikeCount() throws IOException;

    List<CommentRequest> getPagingComment(int num) throws IOException;


    List<CommentRequest> getComment() throws IOException;


    List<Comment> getCommentId(String id) throws IOException;


   // List<CommentSearchRequest> SearchComment(String keyword);


}
