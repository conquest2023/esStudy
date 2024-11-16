package es.board.service;

import es.board.model.req.ReqCommentDTO;
import es.board.model.req.ReqSearchCommentDTO;
import es.board.model.req.UpdateCommentDTO;
import es.board.model.res.CommentSaveDTO;
import es.board.repository.document.Comment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public interface CommentService {


    public String searchIndex(String indexName) throws IOException;
    public String indexDocument(String indexName,CommentSaveDTO dto) throws IOException;

    List<Comment> SearchTextEx(String text) throws IOException;

    List<Comment> EditCommentEx(String id,UpdateCommentDTO eq) throws IOException;

    List<ReqCommentDTO> searchTimeDESC() throws IOException;

    String indexComment(CommentSaveDTO dto) throws IOException;


    List<CommentSaveDTO> BulkIndexTo(List<CommentSaveDTO> comments) throws IOException;

    List<ReqCommentDTO> LikeDESCTo() throws IOException;

    List<ReqCommentDTO> PagingSearchIndex(int num) throws IOException;




    Comment SearchId(String id) throws IOException;


    void CommentSave(CommentSaveDTO commentSaveDTO);

    void CommentRemoveId(String commentUid);

    List<ReqSearchCommentDTO> SearchComment(String keyword);

    List<ReqCommentDTO> CommentBring();

    List<ReqCommentDTO> CommentScore(String score);

    UpdateCommentDTO CommentEdit(String CommentUID,UpdateCommentDTO updateCommentDTO);
}
