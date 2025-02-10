package es.board.repository.document.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.*;
import es.board.controller.model.res.CommentCreate;
import es.board.ex.IndexException;
import es.board.repository.CommentDAO;
import es.board.repository.document.Board;
import es.board.repository.document.Comment;
//import es.board.repository.entity.entityrepository.CommentRepository;
import es.board.repository.entity.entityrepository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CommentDAOImpl implements CommentDAO {

    private final ElasticsearchClient client;

    private final CommentRepository commentRepository;


    @Override
    public double findUserCommentCount(String userId) {
        try {

            SearchResponse<Comment> response = client.search(s -> s
                            .index("comment")
                            .aggregations("commentCount", a -> a
                                    .filter(f -> f
                                            .term(t -> t
                                                    .field("userId")
                                                    .value(userId)))),
                    Comment.class);
            return response.aggregations()
                    .get("commentCount")
                    .filter()
                    .docCount();
        }catch (IOException e){
            log.error("Error fetching FeedCount feed: {}", e.getMessage(), e);
            throw new IndexException("Failed to fetch popular feed", e); // 예외를 커스텀 예외로 감싸서 던짐
        }
    }

    @Override
    public String createCommentOne(String index, CommentCreate dto) {
        dto.TimeNow();
        try {
            IndexResponse response = client.index(i -> i
                    .index(index)
                    .document(dto));
            // 성공적으로 문서가 저장되면, 문서 ID를 반환.
            return response.id();
        } catch (IOException e) {
            log.error("Error indexing document: " + e.getMessage(), e);
            throw new IndexException("Failed to index the comment", e);
        }
    }


    @Override
    public void saveCommentIndex(CommentCreate dto) {

        try {
            IndexResponse response = client.index(i -> i
                    .index("comment")
                    .document(dto));
            // 성공적으로 문서가 저장되면, 문서 ID를 반환.
//            return response.id();
        } catch (IOException e) {
            log.error("Error indexing document: " + e.getMessage(), e);
            throw new IndexException("Failed to index the comment", e);
        }
    }

    @Override
    public List<Comment> findRecentComment() {
        return null;
    }

    @Override
    public List<Comment> findSearchComment(String text)  {
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
    public List<Comment> findLikeCount() {
        try {
            SearchResponse<Comment> response = client.search(s -> s
                            .index("comment")  // 'comment' 인덱스에서 검색
                            .query(q -> q.matchAll(t -> t))  // 모든 문서를 검색
                            .sort(sort -> sort.field(f -> f
                                    .field("likeCount")  // 정렬 기준 필드: likeCount
                                    .order(SortOrder.Desc)) // 내림차순 정렬
                            ),
                    Comment.class   // 결과를 Comment 클래스 객체로 매핑
            );

            List<Comment> comments = response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .collect(Collectors.toList());
            return comments;
        } catch (IOException e) {
            log.error("Error searching comments by like count", e);
            throw new IndexException("Failed to find comments by like count", e);
        }
    }


    @Override
    public List<Comment> findPagingComment(int num) {
        try {
            SearchResponse<Comment> response = client.search(s -> s
                            .index("comment")  // 'comments' 인덱스에서 검색
                            .from(num)
                            .size(num + 3)
                            .query(q -> q.matchAll(t -> t)),
                    Comment.class  // 결과를 Comment 클래스 객체로 매핑
            );

            List<Comment> comments = response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .collect(Collectors.toList());
            return comments;
        } catch (IOException e) {
            log.error("Error searching comments with paging", e);
            throw new IndexException("Failed to find paginated comments", e);
        }
    }


    @Override
    public Comment modifyComment(String id, Comment eq)  {

        try {
            SearchResponse<Comment> searchResponse = client.search(s -> s
                            .index("comment")
                            .query(q -> q.term(t -> t.field("commentUID").value(id))),
                    Comment.class
            );
            log.info(id);

            if (searchResponse.hits().hits().isEmpty()) {
                throw new Exception("Comment not found with id: " + id);
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
                throw new Exception("Updated document not found");
            }
        } catch (IOException e) {
            log.error("Error modifying comment", e);
            throw new IndexException("Failed to modify comment", e);
        } catch (Exception e) {
            throw new IndexException(e);
        }
    }
    @Override
    public List<Board> findFeedAndComment(String userId){
        try{
            SearchResponse<Comment> response=client.search(s->s
                            .index("comment")
                            .query(q->q
                                    .term(t->t
                                            .field("userId.keyword")
                                            .value(userId)))
                    ,Comment.class);
         List<String> feedUIDS= response.hits().hits().stream()
                    .map(hit -> hit.source().getFeedUID())
                    .collect(Collectors.toList());
            List<FieldValue> fieldValues = feedUIDS.stream()
                    .map(FieldValue::of)
                    .collect(Collectors.toList());
            SearchResponse<Board> res = client.search(s -> s
                            .index("board")
                            .query(q -> q
                                    .terms(t -> t
                                            .field("feedUID.keyword")
                                            .terms(tf -> tf.value(fieldValues)
                                            ))), Board.class);

            return  res.hits().hits().stream()
                    .map(hit -> hit.source())
                    .collect(Collectors.toList());

        }catch (IOException  e) {
            log.error("Error fetching total page count: {}", e.getMessage(), e);
            throw new IndexException("Failed to fetch total page count", e); // 예외를 커스텀 예외로 감싸서 던짐
        }
    }


    @Override
    public List<Comment> findDetailComment(String id)  {
        try {
            SearchResponse<Comment> response = client.search(g -> g
                            .index("comment")
                    .query(q -> q
                            .bool(b -> b
                                    .should(
                                            s -> s.term(t -> t
                                                    .field("feedUID.keyword")
                                                    .value(id)))
                                    .should(
                                            s -> s.term(t -> t
                                                    .field("userId")
                                                    .value(id))))), Comment.class);
                return response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Error searching for comment by id", e);
            throw new IndexException("Failed to find comment by ID", e);
        }
    }



    @Override
    public List<Comment> CreateManyComment(List<Comment> pages) {

        try {
            BulkRequest.Builder br = new BulkRequest.Builder();
            log.info(pages.toString());
            for (Comment product : pages) {
                br.operations(op -> op
                        .index(idx -> idx
                                .index("comment")
                                .document(product)
                        )
                );
            }

            BulkResponse response = client.bulk(br.build());
            log.info(response.toString());

            if (response.errors()) {
                response.items().forEach(item -> {
                    if (item.error() != null) {
                        log.error("Failed to index document with ID: " + item.id() + " Error: " + item.error().reason());
                    }
                });
            } else {
                response.items().forEach(item -> {
                    log.info("Successfully indexed document with ID: " + item.id());
                });
            }

            return pages;
        } catch (IOException e) {
            log.error("Error bulk indexing comments", e);
            throw new IndexException("Failed to bulk index comments", e);
        }
    }
    @Override
    public int findSumComment(String id) {
        try {
            // Elasticsearch 검색 요청
            SearchResponse<Void> response = client.search(s -> s
                            .index("comment")
                            .query(q -> q.bool(b -> b.filter(f -> f.term(t -> t.field("feedUID").value(id)))))
                            .aggregations("feedUID_count", a -> a.valueCount(vc -> vc.field("feedUID"))),
                    Void.class // 검색 결과를 사용하지 않으므로 Void로 설정
            );

            // 집계 결과에서 값 추출
            double commentCount = response.aggregations()
                    .get("feedUID_count") // 집계 이름
                    .valueCount()         // ValueCount 집계 결과
                    .value();             // 실제 값
            return (int) commentCount;
        } catch (IOException e) {
            log.error("Error searching for comment by id", e);
            throw new IndexException("Failed to find comment by ID", e);
        }
    }

    @Override
    public List<Comment> findUserRangeActive(String userId) {

        String start = LocalDateTime.now().minusDays(7).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
        String end = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
        try {
            SearchResponse<Board> boardResponse = client.search(s -> s
                            .index("board")
                            .query(q -> q.bool(b -> b
                                  // 피드 소유자 조건
                                    .filter(f -> f.range(r -> r.date(v -> v.gte(start).lte(end).field("createdAt")))) // 날짜 범위 필터
                                    .must(m -> m.term(t -> t.field("userId").value(userId))) // userId가 매칭되는 경우만
                            ))
                            .sort(so -> so.field(f -> f.field("createdAt").order(SortOrder.Desc))) // 최신순 정렬
                    .source(a -> a.filter(f -> f.includes("feedUID"))),Board.class);
            List<String> feedUIDs = boardResponse.hits().hits().stream()
                    .map(hit -> hit.source().getFeedUID())
                    .collect(Collectors.toList());
            if (feedUIDs.isEmpty()) {
                return Collections.emptyList(); // 피드가 없으면 빈 결과 반환   .terms(tf -> tf.value(fieldValues)
            }
            List<FieldValue> fieldValues = feedUIDs.stream()
                    .map(FieldValue::of)
                    .collect(Collectors.toList());
            SearchResponse<Comment> commentResponse = client.search(s -> s
                            .index("comment")
                            .query(q -> q.bool(b -> b
                                    .must(m -> m.terms(
                                            t -> t.field("feedUID")
                                                    .terms(tf -> tf.value(fieldValues)))) // feedUID 조건 추가
                                    .must(m -> m.range(r -> r.date(v -> v.gte(start).lte(end).field("createdAt")))) // 날짜 범위 조건
                                    .mustNot(m -> m.term(t -> t.field("userId.keyword").value(userId))) // 본인이 작성한 댓글 제외
                            ))
                            .sort(so -> so.field(f -> f.field("createdAt").order(SortOrder.Desc))) // 최신순 정렬
                            .size(10),
                    Comment.class);
            return commentResponse.hits().hits().stream()
                    .map(hit -> hit.source())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Error searching for comment by id", e);
            throw new IndexException("Failed to find comment by ID", e);}}


    @Override
    public List<Comment> findCommentId(String commentUid) {
        try {
            SearchResponse<Comment> response = client.search(s -> s
                            .index("comment")
                            .query(q ->q.bool(t -> t.filter(f->
                                            f.term(
                                                    p->p.field("feedUID")
                                            .value(commentUid))))),
                    Comment.class);
            log.info(response.toString());
            return response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .collect(Collectors.toList());
          } catch (IOException e) {
            log.error("Error searching for comment by id", e);
            throw new IndexException("Failed to find comment by ID", e);
        }
    }


    @Override
    public void deleteCommentId(String commentId)  {
        try {
            SearchResponse<Comment> searchResponse = client.search(s -> s
                            .index("comment")
                            .query(q -> q.term(t -> t.field("commentUID").value(commentId))),
                    Comment.class
            );

            // 검색 결과가 비어 있는지 확인
            if (searchResponse.hits().hits().isEmpty()) {
                log.warn("No comment found with commentUID: " + commentId);
                return;  // 결과가 없으면 삭제를 하지 않고 메서드를 종료
            }
            // 결과가 있을 경우 첫 번째 문서의 ID를 가져옴
            String documentId = searchResponse.hits().hits().get(0).id();
            commentRepository.deleteById(documentId);  // 댓글 삭제
            log.info("Successfully deleted comment with commentUID: " + commentId);

        } catch (IOException e) {
            log.error("Error deleting comment with commentUID: " + commentId, e);
            throw new IndexException("Failed to delete comment with commentUID", e);
        }
    }

    @Override
    public Map<String, Double> findPagingComment(List<String> feedUIDs, int page, int size) {
        List<FieldValue> fieldValues = feedUIDs.stream()
                .map(FieldValue::of)
                .collect(Collectors.toList());
        try {
            SearchResponse<Comment> response = client.search(s -> s
                            .index("comment")
                            .query(q -> q
                                    .bool(b -> b.filter(f -> f.terms(
                                            a -> a.field("feedUID")
                                                    .terms(v -> v.value(fieldValues))))))
                            .aggregations("feed_comment_count", a -> a
                                    .terms(t -> t
                                            .field("feedUID") // 게시글 ID별 댓글 수 집계
                                            .size(feedUIDs.size()) // 최대 게시글 수만큼 집계
                                    )
                                    .aggregations("comment_count", ag -> ag
                                            .valueCount(v -> v.field("feedUID")) // 각 게시글에 해당하는 댓글 수를 집계
                                    )), Comment.class);
             return  response.aggregations()
                    .get("feed_comment_count")
                    .sterms()
                    .buckets().array().stream()
                    .collect(Collectors.toMap(
                            bucket -> bucket.key().stringValue(),
                            bucket -> bucket.aggregations()
                                    .get("comment_count")         // 중첩된 value_count 집계 이름
                                    .valueCount().value()));
        }   catch (IOException e) {
        log.error("Error searching for paginated comments in descending order", e);
        throw new IndexException("Failed to find paginated comments in descending order", e);}
    }
    @Override
    public Map<String, Double> findPagingCommentDESC(List<String> feedUIDs, int page, int size) {
        List<FieldValue> fieldValues = feedUIDs.stream()
                .map(FieldValue::of)
                .collect(Collectors.toList());
        try {
            SearchResponse<Comment> response = client.search(s -> s
                            .index("comment")
                            .query(q -> q
                                    .bool(b->b.filter(f->f.terms(
                                            a -> a.field("feedUID")
                                                    .terms(v -> v.value(fieldValues))))))
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

            return response.aggregations()
                    .get("feed_comment_count") // 상위 terms 집계 이름
                    .sterms()                  // String Terms 집계로 변환
                    .buckets().array().stream() // 버킷 리스트 스트림 생성
                    .collect(Collectors.toMap(
                            bucket -> bucket.key().stringValue(), // feedUID 값 (키)
                            bucket -> bucket.aggregations()
                                    .get("comment_count")         // 중첩된 value_count 집계 이름
                                    .valueCount().value()));
        } catch (IOException e) {
            log.error("Error searching for paginated comments in descending order", e);
            throw new IndexException("Failed to find paginated comments in descending order", e);
        }
    }

    @Override
    public List<Comment> findCommentAll()  {
        try {
            SearchResponse<Comment> response = client.search(s -> s
                            .index("comment")
                            .query(q -> q.matchAll(t -> t)),
                    Comment.class
            );

            List<Comment> comments = response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .collect(Collectors.toList());
            return comments;
        } catch (IOException e) {
            log.error("Error finding all comments", e);
            throw new IndexException("Failed to find all comments", e);
        }

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

