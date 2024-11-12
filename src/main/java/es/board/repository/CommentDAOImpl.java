package es.board.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.json.JsonData;
import es.board.model.req.ReqCommentDTO;
import es.board.model.req.ReqSearchCommentDTO;
import es.board.model.req.UpdateCommentDTO;
import es.board.repository.domain.CommentDAO;
import es.board.repository.entity.Board;
import es.board.repository.entity.Comment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
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
        try {
            IndexResponse response = client.index(i -> i
                    .index(index)
                    .document(document)
            );
            // 성공적으로 문서가 저장되면, 문서 ID를 반환합니다.
            return response.id();
        } catch (IOException e) {
            // 오류가 발생한 경우 로그를 출력합니다.
            System.err.println("Error indexing document: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Comment> SearchTextBring(String indexName, String text) throws IOException {
        SearchResponse<Comment> response = client.search(s -> s
                        .index(indexName)  // 'comments' 인덱스에서 검색
                        .query(q -> q        // 쿼리 정의
                                .match(t -> t    // 'content' 필드에서  검색
                                        .field("content")
                                        .query(text)

                                )
                        ),
                Comment.class  // 결과를 Comment 클래스 객체로 매핑
        );

        List<Comment> comments = response.hits().hits().stream()
                .map(hit -> hit.source()) // Elasticsearch 문서를 Comment 객체로 변환
                .collect(Collectors.toList());
     return comments;
    }

    @Override
    public List<Comment> EditCommentEx(String id, Comment eq) throws IOException {

          UpdateResponse<Comment> response= client.update(u -> u
                        .index("comment")
                        .id(id)
                        .doc(eq),
                Comment.class
        );

        List<Comment> comments = new ArrayList<>();
        comments.add(eq);

        return comments;
    }


    @Override
    public Comment PracticeSearch(String indexName,String id) throws IOException {
        // GetResponse 객체를 사용하여 Elasticsearch에서 문서를 검색합니다.
        GetResponse<Comment> response = client.get(g -> g
                        .index(indexName)  // 인덱스 이름을 전달합니다.
                        .id(id),// 문서 ID를 전달합니다.
                Comment.class);       // Comment.class로 결과를 매핑합니다.
        // 문서가 존재하는지 확인하고, 존재하면 내용을 로그에 출력합니다.
        if (response.found()) {
            Comment comment = response.source();  // Elasticsearch 문서를 Comment 객체로 변환
            log.info("Comment content: " + comment.getContent());
            return comment;// 댓글 내용을 출력
        } else {
            log.info("Comment not found");
            return  null;
        }
    }

    @Override
    public List<Comment> BulkIndex(List<Comment> pages) throws IOException {

        List<Comment> products = fetchProducts(pages);
        BulkRequest.Builder br = new BulkRequest.Builder();

        for (Comment product : products) {
            br.operations(op -> op
                    .index(idx -> idx
                            .index("comment")
                            .document(product)
                    )
            );
        }
        for (Comment product : products) {
            IndexResponse response = client.index(i -> i
                    .index("comment")
                    .document(product));
        }

        return  products;
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
    public Comment CommentEdit(Comment comment) {
        return  commentRepository.save(comment);
    }
    @Override
    public Comment getCommentId(String commentUid) {
        return  commentRepository.findByCommentUID(commentUid);
    }

    @Override
    public List<Comment> SearchCommentBring(String keyword) {
        return commentRepository.findCommentsByUsernameAndContent(keyword);
    }



    @Override
    public List<Comment> SearchCommentScore(String score) {
        return commentRepository.findByContentMatchingQuery(score);
    }


    public List<Comment> fetchProducts(List<Comment> eq) {
        // 데이터베이스에서 Comment 데이터를 가져오는 로직
        // 예시로 간단한 더미 데이터를 반환할 수 있습니다.
        List<Comment> result = new ArrayList<>();
        for (Comment comment : eq) {
            result.add(comment);
        }
        return result;
    }
}
