package es.board.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.JsonData;
import es.board.model.req.ReqCommentDTO;
import es.board.repository.domain.CommentDAO;
import es.board.repository.entity.Board;
import es.board.repository.entity.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class CommentDAOImpl implements CommentDAO {

    private final ElasticsearchClient client;

    private final  CommentRepository commentRepository;

    @Override
    public Map<String, Object> search(String index) throws IOException, ElasticsearchException {
        SearchRequest request = new SearchRequest.Builder()
                .index(index)
                .build();

        SearchResponse<JsonData> response = client.search(request, JsonData.class);
        Map<String, Object> results = new HashMap<>();
        response.hits().hits().forEach(hit -> results.put(hit.id(), hit.source().toString()));

        return results;
    }
    @Override
    public String indexDocument(String index, Map<String, Object> document) throws IOException {
        IndexResponse response = client.index(i -> i
                .index(index)
                .document(document)
        );
        return response.id();
    }

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
