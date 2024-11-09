package es.board.repository;

import es.board.model.req.ReqCommentDTO;
import es.board.repository.domain.CommentDAO;
import es.board.repository.entity.Board;
import es.board.repository.entity.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentDAOImpl implements CommentDAO {

    private final  CommentRepository commentRepository;
    @Override
    public void CommentSaveRepo(Comment dto) {
        commentRepository.save(dto);
    }

    @Override
    public void CommentRemoveRepo(String commentUid) {
        commentRepository.deleteById(commentUid);
    }

    @Override
    public List<Comment> CommentBringRepo() {
        return commentRepository.findAllCommentBy();
    }

    @Override
    public List<Comment> SearchCommentBring(String keyword) {
        return commentRepository.findCommentsByUsernameAndContent(keyword);
    }

    @Override
    public Comment CommentEdit(Comment comment) {
        return  commentRepository.save(comment);
    }

    @Override
    public Comment getCommentId(String commentUid) {
        return  commentRepository.findByCommentUID(commentUid);
    }

    @Override
    public List<Comment> SearchCommentScore(String score) {
        return commentRepository.findByContentMatchingQuery(score);
    }
}
