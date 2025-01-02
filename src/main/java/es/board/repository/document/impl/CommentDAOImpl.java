package es.board.repository.document.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.*;
import es.board.ex.IndexException;
import es.board.controller.model.res.CommentCreateResponse;
import es.board.repository.entity.entityrepository.CommentRepository;
import es.board.repository.CommentDAO;
import es.board.repository.document.Board;
import es.board.repository.document.Comment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CommentDAOImpl implements CommentDAO {

    private final ElasticsearchClient client;

    private final CommentRepository commentRepository;


    @Override
    public String createCommentOne(String index, CommentCreateResponse dto) {
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
    public String indexCommentSave(CommentCreateResponse dto) {

        try {
            IndexResponse response = client.index(i -> i
                    .index("comment")
                    .document(dto));
            log.info("hekkio");
            // 성공적으로 문서가 저장되면, 문서 ID를 반환.
            return response.id();
        } catch (IOException e) {
            log.error("Error indexing document: " + e.getMessage(), e);
            throw new IndexException("Failed to index the comment", e);
        }
    }

    @Override
    public List<Comment> findRecentComment() {
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
    public List<Comment> findDetailComment(String id)  {
        try {
            SearchResponse<Comment> response = client.search(g -> g
                            .index("comment")
                            .query(q -> q.match(t -> t.field("feedUID").query(id))),
                    Comment.class
            );

            List<Comment> comments = response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .collect(Collectors.toList());
            return comments;
        } catch (IOException e) {
            log.error("Error searching for comment by id", e);
            throw new IndexException("Failed to find comment by ID", e);
        }
    }

//    @Override
//    public Comment findCommentId(String id)  {
//        try {
//            SearchResponse<Comment> response = client.search(g -> g
//                            .index("comment")
//                            .query(q -> q.match(t -> t.field("feedUID").query(id))),
//                    Comment.class
//            );
//
//            // 검색된 결과를 Comment 객체로 변환
//            return response.hits().hits().get(0).source();
//        } catch (IOException e) {
//            log.error("Error searching for comment by id", e);
//            throw new IndexException("Failed to find comment by ID", e);
//        }
//    }


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
            SearchResponse<Comment> response = client.search(s -> s
                            .index("comment")
                            .query(q -> q.term(t -> t.field("feedUID").value(id))),
                    Comment.class
            );
            long commentCount = response.hits().total().value();
            return (int) commentCount;
        } catch (IOException e) {
            log.error("Error searching for comment by id", e);
            throw new IndexException("Failed to find comment by ID", e);
        }
    }



    @Override
    public Comment findCommentId(String commentUid) {
        try {
            SearchResponse<Comment> response = client.search(s -> s
                            .index("comment")
                            .query(q -> q.term(t -> t.field("commentUID").value(commentUid))),
                    Comment.class);

            // 첫 번째 hit의 source를 반환
            if (!response.hits().hits().isEmpty()) {
                return response.hits().hits().get(0).source(); // hits()에서 첫 번째 hit의 source를 가져옴
            } else {
                return null; // 결과가 없으면 null 반환
            }
        } catch (IOException e) {
            log.error("Error searching for comment by id", e);
            throw new IndexException("Failed to find comment by ID", e);
        }
    }
//        log.info(commentUid);



    @Override
    public void deleteCommentId(String commentId)  {
        try {
            // commentUID로 Elasticsearch에서 해당 댓글을 찾는 쿼리
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
    public Map<String, Long> findPagingComment(List<String> feedUIDs, int page, int size) {
        try {
            List<FieldValue> fieldValues = feedUIDs.stream()
                    .map(FieldValue::of)
                    .collect(Collectors.toList());

            SearchResponse<Comment> response = client.search(s -> s
                            .index("comment")
                            .size(1000)
                            .query(q -> q.terms(a -> a.field("feedUID").terms(v -> v.value(fieldValues))))
                    ,
                    Comment.class
            );

            List<Comment> comments = response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .collect(Collectors.toList());
            return aggregationCountComment(comments);
        } catch (IOException e) {
            log.error("Error finding paginated comments", e);
            throw new IndexException("Failed to find paginated comments", e);
        }
    }
    @Override
    public Map<String, Long> findPagingCommentDESC(List<String> feedUIDs, int page, int size) {
        List<FieldValue> fieldValues = feedUIDs.stream()
                .map(FieldValue::of)
                .collect(Collectors.toList());
        try {
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

