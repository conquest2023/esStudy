package es.board.repository.document.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import es.board.controller.model.req.TopWriter;
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
    public  Map<String,Long> findTodayCommentAggregation(){
            String start = LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
            String end = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
            Map<String, Long> stats = new HashMap<>();
        try {
            SearchResponse<Void> commentResponse = client.search(s -> s
                    .index("comment")
                    .size(0)
                    .query(q -> q
                            .bool(b -> b
                                    .filter(f -> f.range(r -> r
                                            .date(d -> d
                                                    .field("createdAt")
                                                    .gte(start)
                                                    .lte(end))))))
                    .aggregations("today_comment_count", a -> a
                            .cardinality(c -> c.field("commentUID"))
                    ), Void.class
            );

            long commentCount = commentResponse.aggregations()
                    .get("today_comment_count")
                    .cardinality()
                    .value();

            stats.put("commentCount", commentCount);

            log.info(" 댓글 수: {}",commentCount);

        } catch (IOException e) {
            log.error("Elasticsearch 집계 조회 오류: {}", e.getMessage(), e);
            throw new RuntimeException("오늘 통계를 가져오는 데 실패했습니다.", e);
        }

            return stats;
        }

    @Override
    public Map<String, Object> findCommentsWithCount(String feedUID) {
        try {
            SearchResponse<Comment> response = client.search(s -> s
                            .index("comment")
                            .sort(sort -> sort.field(f -> f
                                    .field("createdAt")
                                    .order(SortOrder.Asc)
                            ))
                            .query(q -> q.bool(b -> b.filter(f -> f.term(t -> t.field("feedUID").value(feedUID)))))
                            .aggregations("feedUID_count", a -> a.valueCount(vc -> vc.field("feedUID"))),
                    Comment.class);

            List<Comment> comments = response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());

            double commentCount = response.aggregations()
                    .get("feedUID_count")
                    .valueCount()
                    .value();

            Map<String, Object> result = new HashMap<>();
            result.put("comments", comments);
            result.put("commentCount", (int) commentCount);

            return result;

        } catch (IOException e) {
            log.error("Error searching for comments by feedUID", e);
            throw new IndexException("Failed to find comments by feedUID", e);
        }
    }

    @Override
    public Comment findCommentUID(String commentId)  {
        try {
        SearchResponse<Comment> response = client.search(s -> s
                        .index("comment")
                        .query(q -> q
                                .bool(b -> b
                                        .filter(
                                                a-> a.term(t -> t
                                                        .field("commentUID")
                                                        .value(commentId))))),
                Comment.class);
              return   response.hits().hits().get(0).source();
        } catch (IOException e) {
            log.error("Error fetching commentUID: {}", e.getMessage(), e);
            throw new IndexException("Failed to commentUID", e);
        }
    }
    @Override
    public Map<String, Object> findUserComments(String userId) {
        try {
            SearchResponse<Comment> response = client.search(s -> s
                            .index("comment")
                            .size(1000)
                            .query(q -> q
                                    .term(t -> t
                                            .field("userId")
                                            .value(userId)))
                            .sort(st -> st
                                    .field(f -> f
                                            .field("createdAt")
                                            .order(SortOrder.Desc)))
                            .trackTotalHits(t -> t.enabled(true)),
                    Comment.class);

            long totalComments = response.hits().total().value();
            List<Comment> commentList = response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .collect(Collectors.toList());
            return Map.of(
                    "totalComments", totalComments,
                    "comments", commentList
            );

        } catch (IOException e) {
            log.error("Error fetching user comments: {}", e.getMessage(), e);
            throw new IndexException("Failed to fetch user comments", e);
        }
    }

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
            throw new IndexException("Failed to fetch popular feed", e);
        }
    }

    @Override
    public String createCommentOne(String index, CommentCreate dto) {
        dto.TimeNow();
        try {
            IndexResponse response = client.index(i -> i
                    .index(index)
                    .document(dto));
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
                            .index("comment")
                            .query(q -> q.matchAll(t -> t))
                            .sort(sort -> sort.field(f -> f
                                    .field("likeCount")
                                    .order(SortOrder.Desc))),
                    Comment.class);
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
    public List<Comment> findMyPagePagingComment(String userId,int page,int size) {
        try {
            SearchResponse<Comment> response = client.search(s -> s
                            .index("comment")
                            .from(page * size)
                            .size(size)
                            .query(q->q
                            .bool(b -> b
                                    .filter(
                                            a -> a.term(t -> t
                                                    .field("userId.keyword")
                                                    .value(userId)))))
                            .sort(st -> st
                                    .field(f -> f
                                            .field("createdAt")
                                            .order(SortOrder.Desc))),
                    Comment.class);

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
    public List<Board> findFeedAndCommentMypage(String userId,int page ,int size){
        try {
            SearchResponse<Comment> response = client.search(s -> s
                            .index("comment")
                            .from(page * size)
                            .size(size)
                            .query(q -> q
                                    .bool(b -> b
                                            .filter(
                                                    a -> a.term(t -> t
                                                            .field("userId")
                                                            .value(userId)))))
                    .sort(sort -> sort
                                    .field(f -> f
                                            .field("createdAt")
                                            .order(SortOrder.Desc)
                                    )),
                    Comment.class);
            List<String> feedUIDs = response.hits().hits().stream()
                    .map(hit -> hit.source().getFeedUID())
                    .collect(Collectors.toList());

            if (feedUIDs.isEmpty()) {
                return Collections.emptyList();
            }
            List<FieldValue> fieldValues = feedUIDs.stream()
                    .map(FieldValue::of)
                    .collect(Collectors.toList());
            SearchResponse<Board> res = client.search(s -> s
                    .index("board")
                    .query(q -> q
                            .terms(t -> t
                                    .field("feedUID.keyword")
                                    .terms(tf -> tf.value(fieldValues))
                            )
                    )
                    .sort(sort -> sort
                            .field(f -> f
                                    .field("createdAt")
                                    .order(SortOrder.Desc)
                            )
                    ), Board.class);
            return res.hits().hits().stream()
                    .map(hit -> hit.source())
                    .collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
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
            SearchResponse<Void> response = client.search(s -> s
                            .index("comment")
                            .query(q -> q.bool(b -> b.filter(f -> f.term(t -> t.field("feedUID").value(id)))))
                            .aggregations("feedUID_count", a -> a.valueCount(vc -> vc.field("feedUID"))),
                    Void.class);
            double commentCount = response.aggregations()
                    .get("feedUID_count")
                    .valueCount()
                    .value();
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
                                    .filter(f -> f.range(r -> r.date(v -> v.gte(start).lte(end).field("createdAt")))) // 날짜 범위 필터
                                    .must(m -> m.term(t -> t.field("userId").value(userId)))
                            ))
                            .sort(so -> so.field(f -> f.field("createdAt").order(SortOrder.Desc)))
                    .source(a -> a.filter(f -> f.includes("feedUID"))),Board.class);
            List<String> feedUIDs = boardResponse.hits().hits().stream()
                    .map(hit -> hit.source().getFeedUID())
                    .collect(Collectors.toList());
            if (feedUIDs.isEmpty()) {
                return Collections.emptyList();
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
    public List<Comment> findCommentId(String commentUID) {
        try {
            SearchResponse<Comment> response = client.search(s -> s
                            .index("comment")
                            .query(q ->q.bool(t -> t.filter(f->
                                            f.term(
                                                    p->p.field("feedUID")
                                            .value(commentUID))))),
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
            SearchResponse<Comment> response = client.search(s -> s
                            .index("comment")
                            .source(sou -> sou.filter(f -> f.includes(List.of())))
                            .query(q -> q.term(t -> t.field("commentUID").value(commentId))),
                    Comment.class
            );
            if (response.hits().hits().isEmpty()) {
                log.warn("No comment found with commentUID: " + commentId);
                return;
            }
            String documentId = response.hits().hits().get(0).id();
            commentRepository.deleteById(documentId);
            log.info("Successfully deleted comment with commentUID: " + commentId);

        } catch (IOException e) {
            log.error("Error deleting comment with commentUID: " + commentId, e);
            throw new IndexException("Failed to delete comment with commentUID", e);
        }
    }
    @Override
    public List<Comment> findMostCommentCount() {
        try {
            SearchResponse<Void> response = client.search(s -> s
                            .index("comment")
                            .size(0)
                            .aggregations("most_commented_feeds", a -> a
                                    .terms(t -> t
                                            .field("feedUID")
                                            .size(10)
                                    )
                            ),
                    Void.class
            );
            log.info(response.toString());
            return null;
        } catch (IOException e) {
            log.error("Error searching for comment by id", e);
            throw new IndexException("Failed to find comment by ID", e);
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
                            .size(0)
                            .query(q -> q
                                    .bool(b -> b.filter(f -> f.terms(
                                            a -> a.field("feedUID")
                                                    .terms(v -> v.value(fieldValues))))))
                            .aggregations("feed_comment_count", a -> a
                                    .terms(t -> t
                                            .field("feedUID")
                                            .size(feedUIDs.size())
                                    )
                                    .aggregations("comment_count", ag -> ag
                                            .valueCount(v -> v.field("feedUID"))
                                    )), Comment.class);
             return  response.aggregations()
                    .get("feed_comment_count")
                    .sterms()
                    .buckets().array().stream()
                    .collect(Collectors.toMap(
                            bucket -> bucket.key().stringValue(),
                            bucket -> bucket.aggregations()
                                    .get("comment_count")
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
                                            .field("feedUID.keyword")
                                            .size(feedUIDs.size())
                                    )
                                    .aggregations("comment_count", ag -> ag
                                            .valueCount(v -> v.field("feedUID"))
                                    )
                            ),
                    Comment.class);

            return response.aggregations()
                    .get("feed_comment_count")
                    .sterms()
                    .buckets().array().stream()
                    .collect(Collectors.toMap(
                            bucket -> bucket.key().stringValue(),
                            bucket -> bucket.aggregations()
                                    .get("comment_count")
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
    @Override
    public List<TopWriter> findTopCommentWriters() {
        try {
            SearchResponse<Comment> response = client.search(
                    s -> s.index("comment")
                            .size(0)
                            .query(q -> q
                                    .bool(b -> b
                                            .must(m -> m.exists(e -> e.field("userId")))))
                            .aggregations("top_writers", a -> a
                                    .terms(t -> t
                                            .field("username.keyword")
                                            .size(10))
                                    .aggregations("total_comments", subAgg -> subAgg
                                            .sum(sum -> sum
                                                    .field("commentUID.keyword")))),
                    Comment.class);
            return response.aggregations()
                    .get("top_writers")
                    .sterms()
                    .buckets()
                    .array()
                    .stream()
                    .map(bucket -> new TopWriter(
                            bucket.key().stringValue(),
                            bucket.aggregations().get("total_comments").sum().value()
                    ))
                    .filter(writer -> writer.getUsername() != null && !writer.getUsername().isEmpty() && !writer.getUsername().equals("asd") && !writer.getUsername().equals("undefined") && !writer.getUsername().equals("익명") && !writer.getUsername().equals("작성자"))
                    .sorted(Comparator.comparingDouble(TopWriter::getViewCount).reversed())
                    .collect(Collectors.toList());


        } catch (IOException e) {
            log.error("Top 유저 가져오기 실패!", e);
            throw new IndexException(e);
        }
    }
}

