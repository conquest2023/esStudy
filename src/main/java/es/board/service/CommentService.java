package es.board.service;

import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import es.board.model.req.ReqCommentDTO;
import es.board.model.req.ReqSearchCommentDTO;
import es.board.model.req.UpdateCommentDTO;
import es.board.model.res.CommentSaveDTO;
import es.board.repository.entity.Comment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public interface CommentService {


    public String searchIndex(String indexName) throws IOException;
    public String indexDocument(String indexName, Map<String, Object> document) throws IOException;


    List<Comment> SearchTextEx(String indexName, String text) throws IOException;

    List<Comment> EditCommentEx(String id,UpdateCommentDTO eq) throws IOException;

    List<Comment> BulkIndexTo(List<Comment> comments) throws IOException;


    Comment PracticeSearch(String indexName, String id) throws IOException;


    void CommentSave(CommentSaveDTO commentSaveDTO);

    void CommentRemoveId(String commentUid);

    List<ReqSearchCommentDTO> SearchComment(String keyword);

    List<ReqCommentDTO> CommentBring();

    List<ReqCommentDTO> CommentScore(String score);

    UpdateCommentDTO CommentEdit(String CommentUID,UpdateCommentDTO updateCommentDTO);
}
