package es.board.repository.dao;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.*;
import es.board.model.res.CommentCreateResponse;
import es.board.repository.CommentRepository;
import es.board.repository.document.Board;
import es.board.repository.document.Comment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.rmi.server.LogStream.log;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CommentDAOImpl implements CommentDAO {

    private final ElasticsearchClient client;

    private final CommentRepository commentRepository;


    @Override
    public String createCommentOne(String index, CommentCreateResponse dto) throws IOException {
        dto.TimeNow();
        try {
            IndexResponse response = client.index(i -> i
                    .index(index)
                    .document(dto));
            // 성공적으로 문서가 저장되면, 문서 ID를 반환.
            return response.id();
        } catch (IOException e) {
            // 오류가 발생한 경우 로그를 출력합니다.
            System.err.println("Error indexing document: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public String indexCommentSave(CommentCreateResponse dto) throws IOException {
        dto.TimeNow();
        try {
            IndexResponse response = client.index(i -> i
                    .index("comment")
                    .document(dto));
            log.info("hekkio");
            // 성공적으로 문서가 저장되면, 문서 ID를 반환.
            return response.id();
        } catch (IOException e) {
            // 오류가 발생한 경우 로그를 출력합니다.
            System.err.println("Error indexing document: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Comment> findRecentComment() throws IOException {
//        SearchResponse<Comment> response = client.search(s -> s
//                        .index("comment")
//                        .query(q -> q.matchAll(t -> t))  // 모든 문서를 검색
//                        .sort(sort -> sort.field(f -> f
//                                .field("createdAt")
//                                .order(SortOrder.Desc)// 내림차순 정렬
//                        )),
//                Comment.class   // 결과를 Comment 클래스 객체로 매핑
//        );
//
//        List<Comment> comments = response.hits().hits().stream()
//                .map(hit -> hit.source())
//                // Elasticsearch 문서를 Comment 객체로 변환
//                .collect(Collectors.toList());
//        return comments;
        return null;
    }

    @Override
    public List<Comment> findSearchComment(String text) throws IOException {
//        log.info(text);
//        SearchResponse<Comment> response = client.search(s -> s
//                        .index("comment")  // 'comments' 인덱스에서 검색
//                        .query(q -> q        // 쿼리 정의
//                                .match(t -> t    // 'content' 필드에서  검색
//                                        .field("content")
//                                        .query(text))),
//                Comment.class  // 결과를 Comment 클래스 객체로 매핑
//        );
//
//        List<Comment> comments = response.hits().hits().stream()
//                .map(hit -> hit.source()) // Elasticsearch 문서를 Comment 객체로 변환
//                .collect(Collectors.toList());
//        return comments;
        return null;
    }

    @Override
    public List<Comment> findLikeCount() throws IOException {
        SearchResponse<Comment> response = client.search(s -> s
                        .index("comment")  // 'comment' 인덱스에서 검색

                        .query(q -> q.matchAll(t -> t))  // 모든 문서를 검색

                        .sort(sort -> sort.field(f -> f
                                .field("likeCount")  // 정렬 기준 필드: likeCount
                                .order(SortOrder.Desc)// 내림차순 정렬
                        )),
                Comment.class   // 결과를 Comment 클래스 객체로 매핑
        );

        List<Comment> comments = response.hits().hits().stream()
                .map(hit -> hit.source())
                // Elasticsearch 문서를 Comment 객체로 변환
                .collect(Collectors.toList());
        return comments;

    }


    @Override
    public List<Comment> findPagingComment(int num) throws IOException {
        SearchResponse<Comment> response = client.search(s -> s
                        .index("comment")  // 'comments' 인덱스에서 검색
                        .from(num)
                        .size(num + 3)
                        .query(q -> q
                                .matchAll(t -> t)),
                Comment.class  // 결과를 Comment 클래스 객체로 매핑
        );

        List<Comment> comments = response.hits().hits().stream()
                .map(hit -> hit.source())
                // Elasticsearch 문서를 Comment 객체로 변환
                .collect(Collectors.toList());
        return comments;
    }

    @Override
    public Comment modifyComment(String id, Comment eq) throws Exception {

        SearchResponse<Comment> searchResponse = client.search(s -> s
                .index("comment")
                .query(q -> q
                        .term(t -> t
                                .field("feedUID")
                                .value(id)
                        )
                ), Comment.class);

        // 검색 결과가 없는 경우 예외 처리
        if (searchResponse.hits().hits().isEmpty()) {
            throw new Exception("게시물을 찾을 수 없습니다.");
        }
        String documentId = searchResponse.hits().hits().get(0).id();

        // 게시물 업데이트
        UpdateResponse<Board> response = client.update(u -> u
                .index("comment")
                .id(documentId)
                .doc(eq), Board.class);

        // 응답이 null인 경우 예외 처리
        GetResponse<Comment> getResponse = client.get(g -> g
                .index("comment")
                .id(documentId), Comment.class);

        if (getResponse.found()) {
            return getResponse.source();
        } else {
            throw new Exception("업데이트된 문서를 찾을 수 없습니다.");
        }
    }


    @Override
    public List<Comment> findIdOne(String id) throws IOException {


        SearchResponse<Comment> response = client.search(g -> g
                        .index("comment")
                        .query(q -> q
                                .match(t -> t
                                        .field("feedUID")
                                        .query(id))),
                Comment.class);

        List<Comment> comments = response.hits().hits().stream()
                .map(hit -> hit.source())
                // Elasticsearch 문서를 Comment 객체로 변환
                .collect(Collectors.toList());
        return comments;

    }

    @Override
    public List<Comment> CreateManyComment(List<Comment> pages) throws IOException {

        BulkRequest.Builder br = new BulkRequest.Builder();
        log.info(pages.toString());
        for (Comment product : pages) {
            log("인덱싱 중: " + product);
            br.operations(op -> op
                    .index(idx -> idx
                            .index("comment")
                            .document(product)
                    )
            );
            log("인덱싱 중: " + product);
        }
        BulkResponse response = client.bulk(br.build());
        log.info(response.toString());

        // 에러가 발생한 경우 로그 출력
        if (response.errors()) {
            response.items().forEach(item -> {
                if (item.error() != null) {
                    log("Failed to index document with ID: " + item.id() + " Error: " + item.error().reason());
                }
            });
        } else {
            response.items().forEach(item -> {
                log("Successfully indexed document with ID: " + item.id());
            });
        }

        return pages;
    }


    @Override
    public Comment findCommentId(String commentUid) {
//        log.info(commentUid);
        return commentRepository.findByCommentUID(commentUid);
    }


    @Override
    public void deleteCommentId(String id) throws IOException {
        SearchResponse<Comment> searchResponse = client.search(s -> s
                .index("comment")
                .query(q -> q
                        .term(t -> t
                                .field("feedUID")
                                .value(id)
                        )
                ), Comment.class
        );

        log.info(searchResponse.toString());
        String documentId = searchResponse.hits().hits().get(0).id();
        try {
            commentRepository.deleteById(documentId);
            log.info("Successfully deleted board with id: " + id);
        } catch (Exception e) {
            log.error("Error deleting board with id: " + id, e);
        }
    }

    @Override
    public Map<String, Long> findPagingComment(List<String> feedUIDs, int page, int size) throws IOException {
        List<FieldValue> fieldValues = feedUIDs.stream()
                .map(FieldValue::of)
                .collect(Collectors.toList());
        SearchResponse<Comment> response = client.search(s -> s
                        .index("comment")
                        .size(1000)
                        .query(q -> q
                                .terms(a -> a.field("feedUID")
                                        .terms(v -> v.value(fieldValues)))),
//                        .aggregations("feed_comment_count", a -> a
//                                .terms(t -> t
//                                        .field("feedUID") // 게시글 ID별 댓글 수 집계
//                                        .size(feedUIDs.size()) // 최대 게시글 수만큼 집계
//                                )
//                                .aggregations("comment_count", ag -> ag
//                                        .valueCount(v -> v.field("feedUID")) // 각 게시글에 해당하는 댓글 수를 집계
//                                )
//                        ),
                Comment.class);
        List<Comment> comments = response.hits().hits().stream()
                .map(hit -> hit.source())
                .collect(Collectors.toList());
        return aggregationCountComment(comments);

    }
    @Override
    public Map<String, Long> findPagingCommentDESC(List<String> feedUIDs, int page, int size) throws IOException {
        List<FieldValue> fieldValues = feedUIDs.stream()
                .map(FieldValue::of)
                .collect(Collectors.toList());
        SearchResponse<Comment> response = client.search(s -> s
                        .index("comment")
                        .size(1000)
                        .query(q -> q
                                .terms(a -> a.field("feedUID")
                                        .terms(v -> v.value(fieldValues))))
                        .aggregations("feed_comment_count", a -> a
                                .terms(t -> t
                                        .field("feedUID.keyword") // 게시글 ID별 댓글 수 집계
                                        .size(feedUIDs.size()) // 최대 게시글 수만큼 집계
                                )
                                .aggregations("comment_count", ag -> ag
                                        .valueCount(v -> v.field("feedUID")) // 각 게시글에 해당하는 댓글 수를 집계
                                )
                        ),
                Comment.class);

        List<Comment> comments = response.hits().hits().stream()
                .map(hit -> hit.source())
                .collect(Collectors.toList());


        return countCommentDESC(comments);

    }

    @Override
    public List<Comment> findCommentAll() throws IOException {
        SearchResponse<Comment> response = client.search(s -> s
                        .index("comment")
                        .query(q -> q

                                .matchAll(t -> t)),
                Comment.class  // 결과를 Comment 클래스 객체로 매핑
        );
        List<Comment> comments = response.hits().hits().stream()
                .map(hit -> hit.source())
                .collect(Collectors.toList());
        return comments;

    }


    private Map<String, Long> aggregationCountComment(List<Comment> comments) {

        return comments.stream()
                .collect(Collectors.groupingBy(Comment::getFeedUID, Collectors.counting()));
    }


    private Map<String, Long> countCommentDESC(List<Comment> comments) {
        Map<String, Long> countMap = comments.stream()
                .collect(Collectors.groupingBy(Comment::getFeedUID, Collectors.counting()));

        return comments.stream()
                .collect(Collectors.groupingBy(Comment::getFeedUID, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())) // 댓글 수 기준 내림차순
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }
}

