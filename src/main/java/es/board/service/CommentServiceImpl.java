package es.board.service;

import es.board.model.req.*;
import es.board.model.res.CommentSaveDTO;
import es.board.repository.domain.CommentDAO;
import es.board.repository.entity.Board;
import es.board.repository.entity.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {



    private final CommentDAO commentDAO;

    @Override
    public void CommentSave(CommentSaveDTO commentSaveDTO) {

        Comment comment=new Comment();

        commentDAO.CommentSaveRepo(comment.CommentToEntity(commentSaveDTO));

    }

    @Override
    public void CommentRemoveId(String commentUid) {
            commentDAO.CommentRemoveRepo(commentUid);
    }

    @Override
    public List<ReqSearchCommentDTO> SearchComment(String keyword) {

      ReqSearchCommentDTO req=new ReqSearchCommentDTO();

      return req.DTOFromSearch(commentDAO.SearchCommentBring(keyword));

    }

    @Override
    public List<ReqCommentDTO> CommentBring() {
        ReqCommentDTO req=new ReqCommentDTO();
        return req.DTOFromEntity(commentDAO.CommentBringRepo());
    }

    @Override
    public List<ReqCommentDTO> CommentScore(String score) {
        ReqCommentDTO reqCommentDTO=new ReqCommentDTO();
        return  reqCommentDTO.DTOFromEntity(commentDAO.SearchCommentScore(score));
    }

    @Override
    public UpdateCommentDTO CommentEdit(String CommentUID,UpdateCommentDTO updateCommentDTO) {
        Comment updateComment=commentDAO.getCommentId(CommentUID);

        commentDAO.CommentEdit(updateCommentDTO(CommentUID,updateComment,updateCommentDTO));

        return  updateCommentDTO;
    }

    public Comment updateCommentDTO(String id, Comment comment ,UpdateCommentDTO update){
        String username = update.getUsername() != null ? update.getUsername() : comment.getUsername();
        String content = update.getContent() != null ? update.getContent() : comment.getContent();

        return Comment.builder()
                .commentUID(id)
                .username(username)
                .content(content)
                .updatedAt(LocalDateTime.now())
                .build();
    }

}
