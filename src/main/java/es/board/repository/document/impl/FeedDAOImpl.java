package es.board.repository.document.impl;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import es.board.controller.model.req.NoticeRequest;
import es.board.controller.model.req.TopWriter;
import es.board.ex.IndexException;
import es.board.controller.model.req.FeedUpdate;
import es.board.controller.model.res.FeedCreateResponse;
import es.board.repository.FeedDAO;
import es.board.repository.document.Board;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
@RequiredArgsConstructor
public class FeedDAOImpl implements FeedDAO {

    private final ElasticsearchClient client;
    private final int increment = 1;

    @Override
    public String saveFeed(String index, FeedCreateResponse dto) {
        dto.TimePush();
        try {
            IndexResponse response = client.index(i -> i
                    .index(index)
                    .document(dto));

            return response.id();
        } catch (IOException e) {

            log.error("Error indexing document: {}", e.getMessage(), e);
            throw new IndexException("Failed to index the feed document", e);
        }
    }
    @Override
    public List<Board> findUserRangeTimeFeed(String userId) {

        String start = LocalDateTime.now().minusDays(7).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
        String end = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
        try {
            SearchResponse<Board> response = client.search(s -> s
                    .index("board")
                    .query(q -> q
                            .bool(b -> b
                                    .must(m -> m.term(t -> t.field("userId").value(userId)))
                                    .must(m -> m.range(r ->
                                            r.date(v ->
                                                    v.gte(start).lte(end).field("createdAt"))))
                            )
                    ).sort(so -> so.field(f -> f.field("createdAt").order(SortOrder.Desc))).size(5), Board.class);
            return response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Error searching documents by date range: {}", e.getMessage(), e);
            throw new IndexException("Failed to search feed by date range", e);
        }
    }
    @Override
    public List<Board> saveBulkFeed(List<Board> pages) {
        return null;
    }

    @Override
    public Board indexSaveFeed(Board board, int postId) {
        try {
            IndexResponse response = client.index(i -> i
                    .index("board")
                    .id(String.valueOf(postId))
                    .document(board));
            return board;
        } catch (IOException e) {
            log.error("Error indexing document: {}", e.getMessage(), e);
            throw new IndexException("Failed to index the document", e);
        }
    }


    @Override
    public void saveNoticeFeed(NoticeRequest dto, Long id) {
        log.info(dto.toString());
        try {
            IndexResponse response = client.index(i -> i
                    .index("board")
                    .document(dto));
        } catch (IOException e) {
            log.error("Error indexing document: {}", e.getMessage(), e);
            throw new IndexException("Failed to index the document", e);
        }
    }


    @Override
    public List<Board> findRangeTimeFeed(LocalDateTime startDate, LocalDateTime endDate) {

        String start = startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
        String end = endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
        try {
            SearchResponse<Board> response = client.search(s -> s
                    .index("board")
                    .query(q -> q
                            .range(r -> r
                                    .date(v -> v
                                            .gte(start)
                                            .lte(end)
                                            .field("createdAt")))), Board.class);
            return response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Error searching documents by date range: {}", e.getMessage(), e);
            throw new IndexException("Failed to search feed by date range", e);
        }

    }

    @Override
    public List<Board> findLikeCount() {

        try {
            SearchResponse<Board> response = client.search(s -> s
                            .index("board")
                            .query(q -> q.matchAll(t -> t))
                            .sort(sort -> sort.field(f -> f
                                    .field("likeCount")
                                    .order(SortOrder.Desc)
                            )),
                    Board.class);

            return response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Error fetching like count feed: {}", e.getMessage(), e);
            throw new IndexException("Failed to fetch like count feed", e);
        }
    }

    @Override
    public Integer findUserLikeCount(String userId) {

        try {
            SearchResponse<Board> response = client.search(s -> s
                            .index("board")
                            .query(q -> q
                                    .term(t -> t
                                            .field("userId")
                                            .value(userId)))
                            .size(0)
                            .aggregations("like_count", a -> a
                                    .sum(d -> d
                                            .field("likeCount"))),
                    Board.class);
            return (int) response.aggregations()
                    .get("like_count")
                    .sum()
                    .value();

        } catch (IOException e) {
            log.error("Error fetching like count feed: {}", e.getMessage(), e);
            throw new IndexException("Failed to fetch like count feed", e);
        }
    }

    @Override
    public Map<String, Object> findMypageUserList(String userId, int page, int size) {

        try {

            SearchResponse<Board> response = client.search(s -> s
                            .index("board")
                            .from(page * size)
                            .size(size)
                            .query(q -> q
                                    .term(t -> t
                                            .field("userId")
                                            .value(userId)))
                            .sort(st -> st
                                    .field(f -> f
                                            .field("createdAt")
                                            .order(SortOrder.Desc))),
                    Board.class);
            List<Board> boardList = response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());

            long totalBoards = response.hits().total().value();
            return Map.of(
                    "totalBoards", totalBoards,
                    "boardList", boardList
            );

        } catch (IOException e) {
            log.error("Error fetching user board list: {}", e.getMessage(), e);
            throw new IndexException("Failed to fetch user board list", e);
        }
    }

    @Override
    public List<Board> findPagingMainFeed(int page, int size) {
        try {
            SearchResponse<Board> response = client.search(s -> s
                            .index("board")
                            .from(page * size)
                            .size(size)
                            .sort(sort -> sort.field(f -> f
                                    .field("createdAt")
                                    .order(SortOrder.Desc)
                            )),
                    Board.class);
            return response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Error fetching paging feed: {}", e.getMessage(), e);
            throw new IndexException("Failed to fetch paging feed", e);
        }
    }

    @Override
    public Map<String, Object> findUserMyPageLikeAndFeedCount(String userId) {
        try {
            SearchResponse<Board> response = client.search(s -> s
                            .index("board")
                            .size(0)
                            .query(q -> q
                                    .bool(b -> b
                                            .filter(
                                                    a -> a.term(t -> t
                                                            .field("userId")
                                                            .value(userId)))))
                            .aggregations("totalLikes", a -> a
                                    .sum(sum -> sum.field("likeCount"))),
                    Board.class);
            long totalBoards = response.hits().total().value();
            double totalLikes = response.aggregations().get("totalLikes").sum().value();

            return Map.of(
                    "totalBoards", totalBoards,
                    "totalLikes", (int) totalLikes
            );

        } catch (IOException e) {
            log.error("Error fetching user board stats: {}", e.getMessage(), e);
            throw new IndexException("Failed to fetch user board stats", e);
        }
    }

    @Override
    public List<Board> findMostViewFeed(int page, int size) {
        try {
            SearchResponse<Board> response = client.search(s -> s
                            .index("board")
                            .from(page * size)
                            .size(size)
                            .sort(sort -> sort.field(f -> f
                                    .field("viewCount")
                                    .order(SortOrder.Desc))), Board.class);
            return response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Error fetching most viewed feed: {}", e.getMessage(), e);
            throw new IndexException("Failed to fetch most viewed feed", e);
        }
    }

    @Override
    public void deleteFeed(String id) {
        try {

            DeleteResponse deleteResponse = client.delete(d -> d
                    .index("board")
                    .id(id)
            );
            if (deleteResponse.result().toString().equals("NotFound")) {
                log.warn("삭제할 문서를 찾을 수 없습니다: {}", id);
                throw new IllegalArgumentException("해당 게시글을 찾을 수 없습니다.");
            }

            log.info("게시글 삭제 완료 (ID: {})", id);

        } catch (ElasticsearchException e) {
            log.error("Elasticsearch 삭제 오류 (ID: {}): {}", id, e.getMessage(), e);
            throw new RuntimeException("Elasticsearch 삭제 중 오류 발생", e);
        } catch (Exception e) {
            log.error("게시글 삭제 실패 (ID: {}): {}", id, e.getMessage(), e);
            throw new RuntimeException("게시글 삭제 중 오류 발생", e);
        }
    }
    @Override
    public List<Board> findSearchBoard(String text) {
        try {
            SearchResponse<Board> response = client.search(s -> s
                            .index("board")
                            .query(q -> q
                                    .bool(b -> b
                                            .should(
                                                    a -> a.match(t -> t
                                                            .field("title")
                                                            .query(text)))
                                            .should(
                                                    t -> t.match(m -> m
                                                            .field("description")
                                                            .query(text))))),
                    Board.class);
            return response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Error searching documents by text: {}", e.getMessage(), e);
            throw new IndexException("Failed to search feed by text", e);
        }
    }
    @Override
    public List<Board> findCategoryAndContent(String category) {
        try {
            SearchResponse<Board> response = client.search(s -> s
                            .index("board")
                            .query(q -> q
                                    .bool(b -> b
                                            .must(m -> m.term(t -> t.field("category").value(category)))
                                            .should(t -> t.match(m -> m.field("description").query(category))))),
                    Board.class);

            return response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .collect(Collectors.toList());
        } catch (IOException | ElasticsearchException e) {
            log.error("Error fetching category and content feed: {}", e.getMessage(), e);
            throw new IndexException("Failed to fetch category and content feed", e);
        }
    }

    @Override
    public List<Board> findRecommendFeed() {
        try {
            SearchResponse<Board> response = client.search(s -> s
                            .index("board")
                            .sort(sort -> sort.field(f -> f
                                    .field("createdAt")
                                    .order(SortOrder.Desc)))
                            .query(q -> q
                                    .bool(b -> b
                                            .filter(m -> m.term(t -> t.field("category").value("추천")))))
                            .size(3),
                    Board.class);

            return response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .collect(Collectors.toList());
        } catch (IOException | ElasticsearchException e) {
            log.error("Error fetching Recommend category: {}", e.getMessage(), e);
            throw new IndexException("Failed to fetch category", e);
        }
    }
    @Override
    public double findSumLikeByPageOne(int page, int size) {
        try {

            SearchResponse<Board> response = client.search(s -> s
                            .index("board")
                            .from(size)
                            .size(size)
                            .aggregations("totalLikes", a -> a.sum(sum -> sum.field("likeCount"))),
                    Board.class);
            return response.aggregations()
                    .get("totalLikes")
                    .sum()
                    .value();
        } catch (IOException | ElasticsearchException e) {
            log.error("Error fetching sum of likes by page: {}", e.getMessage(), e);
            throw new IndexException("Failed to fetch sum of likes by page", e);
        }
    }
    @Override
    public Map<String, Object> fetchTotalFeedStats() {
        try {
            SearchResponse<Board> response = client.search(s -> s
                            .index("board")
                            .size(0)
                            .aggregations("totalFeedCount",
                                    a -> a.valueCount(vc -> vc.field("feedUID.keyword")))
                            .trackTotalHits(t -> t.enabled(true)),
                    Board.class);
            long totalFeedCount = (long) response.aggregations().get("totalFeedCount").valueCount().value();

            return Map.of(
                    "totalFeedCount", totalFeedCount
            );

        } catch (IOException e) {
            log.error("Error fetching total feed stats: {}", e.getMessage(), e);
            throw new IndexException("Failed to fetch total feed stats", e);
        }
    }


    @Override
    public List<Board> findMonthPopularFeed() {
        try {
            int monthPage = 5;
            SearchResponse<Board> response = client.search(s -> s
                            .index("board")
                            .sort(sort -> sort.field(f -> f
                                    .field("likeCount")
                                    .order(SortOrder.Desc)))
                            .size(monthPage)
                            .query(q -> q.matchAll(t -> t)),
                    Board.class);

            return response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Error fetching month popular feed: {}", e.getMessage(), e);
            throw new IndexException("Failed to fetch month popular feed", e);
        }
    }

    @Override
    public List<Board> findPopularFeedDESC(int page, int size) {
        try {
            SearchResponse<Board> response = client.search(s -> s
                            .index("board")
                            .size(size)
                            .sort(sort -> sort.field(f -> f
                                    .field("likeCount")
                                    .order(SortOrder.Desc)))
                            .query(q -> q.matchAll(t -> t))
                    , Board.class);
            return response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Error fetching month popular feed: {}", e.getMessage(), e);
            throw new IndexException("Failed to fetch month popular feed", e);
        }
    }

    @Override
    public List<Board> findRecentFeed() {

        try {
            SearchResponse<Board> response = client.search(s -> s
                            .index("board")
                            .query(q -> q.matchAll(t -> t))
                            .sort(sort -> sort.field(f -> f
                                    .field("createdAt")
                                    .order(SortOrder.Desc)
                            )),
                    Board.class);

            return response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Error fetching recent feed: {}", e.getMessage(), e);
            throw new IndexException("Failed to fetch recent feed", e);
        }
    }

    @Override
    public List<Board> findWeekBestFeed(int page, int size) {
        String start = LocalDateTime.now().minusDays(7).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
        String end = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
        try {
            SearchResponse<Board> response = client.search(s -> s
                            .index("board")
                            .from(page * size)
                            .size(size)
                            .sort(sort -> sort.field(f -> f
                                    .field("viewCount")
                                    .order(SortOrder.Desc)
                            ))
                            .query(q -> q
                                    .bool(b -> b
                                            .filter(f -> f.range(r -> r
                                                    .date(d -> d
                                                            .field("createdAt")
                                                            .gte(start)
                                                            .lte(end))))))
                    , Board.class);
            return response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Error fetching month popular feed: {}", e.getMessage(), e);
            throw new IndexException("Failed to fetch month popular feed", e);
        }
    }


    @Override
    public List<Board> findViewDESC(int page, int size) {
        try {
            SearchResponse<Board> response = client.search(s -> s
                            .index("board")
                            .from(page * size)
                            .size(size)
                            .sort(sort -> sort.field(f -> f
                                    .field("viewCount")
                                    .order(SortOrder.Desc)
                            ))
                    , Board.class);
            return response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Error fetching month popular feed: {}", e.getMessage(), e);
            throw new IndexException("Failed to fetch month popular feed", e);
        }
    }

    @Override
    public List<Board> findCountComment(int page, int size) {
        try {
            SearchResponse<Void> response = client.search(s -> s
                            .index("comment")
                            .size(0)
                            .from(page * size)
                            .aggregations("totalFeed",
                                    a -> a.terms(t -> t
                                            .field("feedUID")
                                            .size(1000)))
                    ,Void.class);
            // 전체 버킷에서 자바로 잘라서 페이징
            List<String> feedUIDs = response.aggregations()
                    .get("totalFeed")
                    .sterms()
                    .buckets()
                    .array()
                    .stream()
                    .skip((long) page * size)
                    .limit(size)
                    .map(bucket -> bucket.key().stringValue())
                    .collect(Collectors.toList());

            List<FieldValue> fieldValues = feedUIDs.stream()
                    .map(FieldValue::of)
                    .collect(Collectors.toList());

            SearchResponse<Board> res = client.search(s -> s
                    .index("board")
                    .query(q -> q
                            .terms(t -> t
                                    .field("feedUID.keyword")
                                    .terms(tf -> tf.value(fieldValues))
                            )), Board.class);
            return res.hits().hits().stream()
                    .map(hit -> hit.source())
                    .collect(Collectors.toList());


        } catch (IOException e) {
            log.error("Error fetching month popular feed: {}", e.getMessage(), e);
            throw new IndexException("Failed to fetch month popular feed", e);
        }
    }

    @Override
    public List<Board> findReplyCount(int page, int size) {
        try {
            SearchResponse<Void> response = client.search(s -> s
                            .index("reply")
                            .size(0)
                            .from(page * size)
                            .aggregations("totalReply",
                                    a -> a.terms(t -> t
                                            .field("feedUID")
                                            .size(1000)))
                    ,Void.class);
            // 전체 버킷에서 자바로 잘라서 페이징
            List<String> feedUIDs = response.aggregations()
                    .get("totalReply")
                    .sterms()
                    .buckets()
                    .array()
                    .stream()
                    .skip((long) page * size)
                    .limit(size)
                    .map(bucket -> bucket.key().stringValue())
                    .collect(Collectors.toList());

            List<FieldValue> fieldValues = feedUIDs.stream()
                    .map(FieldValue::of)
                    .collect(Collectors.toList());

            SearchResponse<Board> res = client.search(s -> s
                    .index("board")
                    .query(q -> q
                            .terms(t -> t
                                    .field("feedUID.keyword")
                                    .terms(tf -> tf.value(fieldValues))
                            )), Board.class);
            return res.hits().hits().stream()
                    .map(hit -> hit.source())
                    .collect(Collectors.toList());


        } catch (IOException e) {
            log.error("Error fetching month popular feed: {}", e.getMessage(), e);
            throw new IndexException("Failed to fetch month popular feed", e);
        }
    }

    @Override
    public Map<String, Object> findDataFeed(int page, int size, String category) {
        try {
            SearchResponse<Board> response = client.search(s -> s
                    .index("board")
                    .from(page * size)
                    .sort(sort -> sort.field(f -> f
                            .field("createdAt")
                            .order(SortOrder.Desc)))
                    .size(size)
                    .trackTotalHits(t -> t.enabled(true))
                    .query(q -> q
                            .bool(b -> b
                                    .filter(
                                            st -> st.term(t -> t
                                                    .field("category")
                                                    .value(category))))), Board.class);
                List<Board> datas = response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());
            long totalCount = response.hits().total().value();
            return Map.of(
                    "data", datas,
                    "total", totalCount
            );
        } catch (IOException e) {
            log.error("Error data feed: {}", e.getMessage(), e);
            throw new IndexException("Failed to data feed", e);
        }
    }
    @Override
    public Map<String, Object> findNoticeFeed(int page, int size) {
        try {
            SearchResponse<Board> response = client.search(s -> s
                            .index("board")
                            .query(q -> q
                                    .term(t -> t
                                            .field("category")
                                            .value("공지사항")
                                    )
                            )
                            .sort(sort -> sort.field(f -> f
                                    .field("createdAt")
                                    .order(SortOrder.Desc)))
                            .from(page * size)
                            .size(size)
                            .trackTotalHits(t -> t.enabled(true)),
                    Board.class
            );
            List<Board> notices = response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());
            long totalCount = response.hits().total().value();
            return Map.of(
                    "data", notices,
                    "total", totalCount
            );
        } catch (IOException e) {
            log.error("Error getting notice feed", e);
            throw new IndexException("Failed to get notice feed", e);
        }
    }

    @Override
    public Board findFeedDetail(String id) {
        try {
            SearchResponse<Board> response = client.search(g -> g
                    .index("board")
                    .query(q -> q
                            .bool(b -> b
                                    .filter(
                                            s -> s.term(t -> t
                                                    .field("feedUID.keyword")
                                                    .value(id))))), Board.class);
            if (response.hits().hits().isEmpty()) {
                log.warn("Document not found for ID: {}", id);
                return null;
            }
            return response.hits().hits().get(0).source();
        } catch (IOException e) {
            log.error("Error fetching document by ID: {}", e.getMessage(), e);
            throw new IndexException("Failed to fetch document by ID", e);
        }
    }

    @Override
    public Map<String, Double> findDayAggregation() {
        String start = LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
        String end = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
        Map<String, Double> stats = new HashMap<>();

        try {
            SearchResponse<Void> response = client.search(s -> s
                    .index("board")
                    .size(0)
                    .query(q -> q
                            .bool(b -> b
                                    .filter(f -> f.range(r -> r
                                            .date(d -> d
                                                    .field("createdAt")
                                                    .gte(start)
                                                    .lte(end)
                                            )))
                            )
                    )
                    .aggregations("today_post_count", a -> a
                            .cardinality(c -> c.field("feedUID.keyword"))
                    )
                    .aggregations("today_view_count", a -> a
                            .sum(sum -> sum.field("viewCount"))
                    ), Void.class
            );

            double postCount = response.aggregations()
                    .get("today_post_count")
                    .cardinality()
                    .value();

            double viewCount = response.aggregations()
                    .get("today_view_count")
                    .sum()
                    .value();

            stats.put("postCount", postCount);
            stats.put("viewCount", viewCount);

            log.info("오늘 게시글 수: {}, 오늘 조회수: {}", postCount, viewCount);

        } catch (IOException e) {
            log.error("Elasticsearch 집계 조회 오류: {}", e.getMessage(), e);
            throw new RuntimeException("오늘 통계를 가져오는 데 실패했습니다.", e);
        }

        return stats;
    }


    @Override
    public void saveViewCounts(String feedUID) {
        try {
            client.updateByQuery(u -> u
                    .index("board")
                    .script(s -> s
                            .source("ctx._source.viewCount += params.increment")
                            .params(Map.of("increment", JsonData.of(increment)))
                    )
                    .query(q -> q
                            .term(t -> t
                                    .field("feedUID.keyword")
                                    .value(feedUID))));
        } catch (IOException e) {
            log.info("조회수 업데이트 실패");
            throw new IndexException(e);
        }
    }


    @Override
    public Board findPopularFeedOne() {
        try {

            SearchResponse<Board> response = client.search(s -> s
                            .query(q -> q.matchAll(t -> t))
                            .index("board")
                            .aggregations("totalLikes", a -> a
                                    .max(max -> max.field("likeCount")))
                            .query(q -> q.match(t -> t.field("likeCount")
                                    .query("totalLikes"))),
                    Board.class);


            double maxLikes = response.aggregations()
                    .get("totalLikes")
                    .max()
                    .value();

            return null;
        } catch (IOException e) {
            log.error("Error fetching popular feed: {}", e.getMessage(), e);
            throw new IndexException("Failed to fetch popular feed", e);
        }
    }

    @Override
    public double findUserFeedCount(String userId) {
        try {

            SearchResponse<Board> response = client.search(s -> s
                            .index("board")
                            .aggregations("feedCount", a -> a
                                    .filter(f -> f
                                            .term(t -> t
                                                    .field("userId")
                                                    .value(userId)))),
                    Board.class);
            return response.aggregations()
                    .get("feedCount")
                    .filter()
                    .docCount();
        } catch (IOException e) {
            log.error("Error fetching FeedCount feed: {}", e.getMessage(), e);
            throw new IndexException("Failed to fetch popular feed", e);
        }
    }

    @Override
    public Board modifyFeed(String id, FeedUpdate eq) {
        try {
            SearchResponse<Board> searchResponse = client.search(s -> s
                    .index("board")
                    .query(q -> q
                            .term(t -> t
                                    .field("feedUID.keyword")
                                    .value(id)
                            )
                    ), Board.class);

            if (searchResponse.hits().hits().isEmpty()) {
                throw new IndexException("게시물을 찾을 수 없습니다. feedUID: " + id);
            }

            String documentId = searchResponse.hits().hits().get(0).id();

            UpdateResponse<Board> response = client.update(u -> u
                    .index("board")
                    .id(documentId)
                    .doc(eq), Board.class);

            GetResponse<Board> getResponse = client.get(g -> g
                    .index("board")
                    .id(documentId), Board.class);

            if (getResponse.found()) {
                return getResponse.source();
            } else {
                throw new IndexException("업데이트된 문서를 찾을 수 없습니다. feedUID: " + id);
            }
        } catch (IOException e) {
            log.error("Error modifying feed: {}", e.getMessage(), e);
            throw new IndexException("Failed to modify feed", e);
        }
    }

    @Override
    public int findAllViewCount() {
        try {
            SearchResponse<Board> searchResponse = client.search(s -> s
                            .index("board")
                            .size(0)
                            .aggregations("totalViews",
                                    a -> a.sum(sum ->
                                            sum.field("viewCount"))),
                    Board.class);
            return (int) searchResponse.aggregations()
                    .get("totalViews")
                    .sum()
                    .value();
        } catch (IOException e) {
            log.info("조회수 가져오기 실패");
            throw new IndexException(e);
        }
    }

    @Override
    public List<TopWriter> findTopWriters() {
        try {
            SearchResponse<Board> response = client.search(s -> s
                            .index("board")
                            .size(0)
                            .query(q -> q
                                    .bool(b -> b
                                            .must(m -> m.exists(e -> e.field("userId")))
                                    )
                            )
                            .aggregations("top_writers", topWriters -> topWriters
                                    .terms(t -> t
                                            .field("username.keyword")
                                            .size(30))
                                    .aggregations("total_views", totalViews -> totalViews
                                            .sum(sum -> sum.field("viewCount")))
                                    .aggregations("sort_bucket", sortBucket -> sortBucket
                                            .bucketSort(bs -> bs
                                                    .sort(t -> t
                                                            .field(f -> f
                                                                    .field("total_views")
                                                                    .order(SortOrder.Desc)))
                                                    .size(15)))), Board.class);
            List<StringTermsBucket> buckets = response.aggregations()
                    .get("top_writers")
                    .sterms()
                    .buckets()
                    .array();
            return buckets.stream()
                    .map(bucket -> new TopWriter(
                            bucket.key().stringValue(),
                            bucket.aggregations().get("total_views").sum().value()))
                    .filter(writer ->
                            writer.getUsername() != null &&
                                    !writer.getUsername().isEmpty() &&
                                    !writer.getUsername().equals("관리자")&&
                                    !writer.getUsername().equals("익명") &&
                                    !writer.getUsername().equals("asd") &&
                                    !writer.getUsername().equals("안녕") &&
                                    !writer.getUsername().equals("hoeng"))
                                    .limit(6)
                                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Top 유저 가져오기 실패!", e);
            return Collections.emptyList();
        }
    }
}




