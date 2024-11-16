package es.board.service;

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

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {


    private final CommentDAO commentDAO;




    @Override
    public String saveDocument(String indexName, CommentCreateResponse dto) throws IOException {
//        Request request = new Request("POST", "/" + indexName + "/_doc");
        return  commentDAO.createCommentOne(indexName,dto);
        // 결과 반환
        // JSON 형태로 변환 후 요청 본문에 추가
//        StringEntity entity = new StringEntity(
//                new ObjectMapper().writeValueAsString(document),
//                ContentType.APPLICATION_JSON);
//        request.setEntity(entity);
//        Response response = client.performRequest(request);
//        return new String(response.getEntity().getContent().readAllBytes());
    }

    @Override
    public List<Comment> getSearchComment(String text) throws IOException {
        return commentDAO.findSearchComment(text);
    }
    @Override
    public List<Comment> editComment(String id, CommentUpdate eq) throws IOException {
        Comment comment=new Comment();

       return commentDAO.modifyComment(id,comment.convertDtoToEntity(eq));
    }

    @Override
    public List<CommentRequest> getRecentComment() throws IOException {
        CommentRequest commentDTO=new CommentRequest();

        return  commentDTO.changeCommentToDTO(commentDAO.findRecentComment());
    }

    @Override
    public String indexComment(CommentCreateResponse dto) throws IOException {

        return  commentDAO.indexCommentSave(dto);
    }

    @Override
    public List<CommentCreateResponse> createBulkComment(List<CommentCreateResponse> comments) throws IOException {
         commentDAO.CreateManyComment(BulkToEntity(comments));
          return  comments;
    }

    @Override
    public List<CommentRequest> getLikeCount() throws IOException {
        CommentRequest req=new CommentRequest();
        return  req.changeCommentToDTO(commentDAO.findLikeCount());
    }

    @Override
    public List<CommentRequest> getPagingComment(int num) throws IOException {
        CommentRequest req=new CommentRequest();
        List<Comment> comments= commentDAO.findPagingComment(num);
        log.info(comments.toString());
        return  req.changeCommentToDTO(comments);
    }

    @Override
    public List<CommentRequest> getComment() throws IOException {
        CommentRequest commentRequest=new CommentRequest();
        return commentRequest.changeCommentToDTO(commentDAO.findCommentAll());
    }

    @Override
    public Comment getCommentId(String id) throws IOException {
        return commentDAO.findIdOne(id);
    }



    public Comment updateCommentDTO(String id, Comment comment , CommentUpdate update){
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
