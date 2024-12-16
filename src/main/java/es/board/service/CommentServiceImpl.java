package es.board.service;

import es.board.ex.IndexException;
import es.board.model.req.*;
import es.board.model.res.CommentCreateResponse;
import es.board.repository.dao.CommentDAO;
import es.board.repository.document.Comment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {


    private final CommentDAO commentDAO;

    @Override
    public String saveDocument(String indexName, CommentCreateResponse dto) {
        return commentDAO.createCommentOne(indexName, dto);
    }

    @Override
    public List<Comment> getSearchComment(String text) {
        return commentDAO.findSearchComment(text);
    }

    @Override
    public Comment editComment(String id, CommentUpdate eq) {
        Comment comment = new Comment();
        return commentDAO.modifyComment(id, comment.convertDtoToEntity(eq));
    }

    @Override
    public List<CommentRequest> getRecentComment() {
        CommentRequest commentDTO = new CommentRequest();
        return commentDTO.changeCommentToDTO(commentDAO.findRecentComment());
    }

    @Override
    public String indexComment(CommentCreateResponse dto) {
        return commentDAO.indexCommentSave(dto);
    }

    @Override
    public List<CommentCreateResponse> createBulkComment(List<CommentCreateResponse> comments) {
        commentDAO.CreateManyComment(BulkToEntity(comments));
        return comments;
    }

    @Override
    public int getSumComment(String id) {
        return commentDAO.findSumComment(id);
    }

    @Override
    public List<CommentRequest> getLikeCount() {
        CommentRequest req = new CommentRequest();
        return req.changeCommentToDTO(commentDAO.findLikeCount());
    }

    @Override
    public Map<String, Long> getPagingComment(List<String> feedUIDs, int num, int size) {
        return commentDAO.findPagingComment(feedUIDs, num, size);
    }

    @Override
    public Map<String, Long> getPagingCommentDESC(List<String> feedUIDs, int num, int size) {
        return commentDAO.findPagingCommentDESC(feedUIDs, num, size);
    }

    @Override
    public List<CommentRequest> getComment() {
        CommentRequest commentRequest = new CommentRequest();
        return commentRequest.changeCommentToDTO(commentDAO.findCommentAll());
    }

    @Override
    public List<Comment> getCommentId(String id) {
        return commentDAO.findIdOne(id);
    }

    @Override
    public void deleteComment(String id) {
        commentDAO.deleteCommentId(id);
    }

    public Comment updateCommentDTO(String id, Comment comment, CommentUpdate update) {
        String username = update.getUsername() != null ? update.getUsername() : comment.getUsername();
        String content = update.getContent() != null ? update.getContent() : comment.getContent();
        return Comment.builder()
                .commentUID(id)
                .username(username)
                .content(content)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public List<Comment> BulkToEntity(List<CommentCreateResponse> res) {
        List<Comment> comments = new ArrayList<>();
        for (CommentCreateResponse dto : res) {
            // 빌더 패턴을 사용해 Comment 객체 생성
            Comment comment = Comment.builder()
                    .commentUID(dto.getCommentUID())
                    .username(dto.getUsername())
                    .content(dto.getContent())
                    .createdAt(dto.getCreatedAt())
                    .build();
            comments.add(comment);
        }
        return comments;
    }
}
