package es.board.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import es.board.model.req.*;
import es.board.model.res.CommentSaveDTO;
import es.board.model.res.FeedSaveDTO;
import es.board.repository.domain.CommentDAO;
import es.board.repository.entity.Comment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
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

    private final RestClient client;

    private final ElasticsearchClient esClient;

    private final CommentDAO commentDAO;

    @Override
    public String searchIndex(String indexName) throws IOException {
        Request request = new Request("GET", "/" + indexName + "/_search");
        request.addParameter("pretty", "true");

        Response response = client.performRequest(request);
        return new String(response.getEntity().getContent().readAllBytes());
    }
    @Override
    public String indexDocument(String indexName, CommentSaveDTO dto) throws IOException {
//        Request request = new Request("POST", "/" + indexName + "/_doc");
        return  commentDAO.indexDocument(indexName,dto);
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
    public List<Comment> SearchTextEx(String text) throws IOException {
        return commentDAO.SearchTextBring(text);
    }
    @Override
    public List<Comment> EditCommentEx(String id, UpdateCommentDTO eq) throws IOException {
        Comment comment=new Comment();

       return commentDAO.EditCommentEx(id,comment.convertDtoToEntity(eq));
    }
    @Override
    public List<CommentSaveDTO> BulkIndexTo(List<CommentSaveDTO> comments) throws IOException {
         commentDAO.BulkIndex(BulkToEntity(comments));
          return  comments;
    }

    @Override
    public List<ReqCommentDTO> LikeDESCTo() throws IOException {
        ReqCommentDTO req=new ReqCommentDTO();
        return  req.DTOFromEntity(commentDAO.LikeDESCBring());
    }

    @Override
    public List<ReqCommentDTO> PagingSearchIndex(int num) throws IOException {
        ReqCommentDTO req=new ReqCommentDTO();
        List<Comment> comments= commentDAO.PagingSearchBring(num);
        log.info(comments.toString());
        return  req.DTOFromEntity(comments);
    }

    @Override
    public Comment SearchId(String id) throws IOException {
        return commentDAO.SearchIdBring(id);
    }

    public void closeClient() throws IOException {
        client.close();

    }





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

    public List<Comment> BulkToEntity(List<CommentSaveDTO> res) {
        List<Comment> comments = new ArrayList<>();
        for (CommentSaveDTO dto : res) {
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
