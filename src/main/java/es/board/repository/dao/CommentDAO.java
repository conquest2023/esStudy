package es.board.repository.dao;

import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import es.board.model.res.CommentSaveDTO;
import es.board.repository.document.Board;
import es.board.repository.document.Comment;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@Repository
public interface CommentDAO {

    Map<String, Object> search(String index)  throws IOException, ElasticsearchException;

    String indexDocument(String index, CommentSaveDTO dto) throws IOException;

    List<Comment> SearchCommentTimeDESC() throws IOException;


    String indexCommentSave(CommentSaveDTO dto) throws IOException;


    List<Comment> BulkIndex(List<Comment> pages) throws IOException;

    List<Comment> SearchTextBring(String text) throws IOException;

    List<Comment> LikeDESCBring() throws IOException;

    List<Comment> PagingSearchBring(int num) throws IOException;




    List<Comment> EditCommentEx(String id,Comment comment) throws IOException;
    Comment SearchIdBring(String id) throws IOException;

    void CommentSaveRepo(Comment dto);

    void CommentRemoveRepo(String commentUid);

    List<Comment> CommentBringRepo();


    List<Comment> SearchCommentBring(String keyword);

    Comment CommentEdit(Comment updateDTO);


    Comment getCommentId(String commentUid);

    List<Comment> SearchCommentScore(String score);
}
