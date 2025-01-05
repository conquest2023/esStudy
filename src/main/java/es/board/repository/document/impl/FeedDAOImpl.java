package es.board.repository.document.impl;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import es.board.ex.IndexException;
import es.board.controller.model.req.FeedUpdate;
import es.board.controller.model.res.FeedCreateResponse;
import es.board.repository.FeedDAO;
import es.board.repository.document.Board;
import es.board.repository.entity.entityrepository.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Slf4j
@RequiredArgsConstructor
public class FeedDAOImpl implements FeedDAO {

    private final ElasticsearchClient client;

   private final BoardRepository boardRepository;

    private final int increment = 1;

    @Override
    public String saveFeed(String index, FeedCreateResponse dto) {
        dto.TimePush();
        try {
            IndexResponse response = client.index(i -> i
                    .index(index)
                    .document(dto));
            // 성공적으로 문서가 저장되면, 문서 ID를 반환.
            return response.id();
        } catch (IOException e) {
            // 오류가 발생한 경우 로그를 출력합니다.
            log.error("Error indexing document: {}", e.getMessage(), e);
            throw new IndexException("Failed to index the feed document", e); // 예외를 감싸서 던짐
        }
    }

    @Override
    public List<Board> saveBulkFeed(List<Board> pages) {
        BulkRequest.Builder br = new BulkRequest.Builder();
//        for (Board product : pages) {
//            log.info("인덱싱 중: {}", product);
//            br.operations(op -> op
//                    .index(idx -> idx
//                            .index("comment")
//                            .document(product)
//                    )
//            );
//        }
//        BulkResponse response = client.bulk(br.build());
//
//        // 에러가 발생한 경우 로그 출력
//        if (response.errors()) {
//            response.items().forEach(item -> {
//                if (item.error() != null) {
//                    log.error("Failed to index document with ID: {} Error: {}", item.id(), item.error().reason());
//                }
//            });
//        } else {
//            response.items().forEach(item -> {
//                log.info("Successfully indexed document with ID: {}", item.id());
//            });
//        }
//        return pages;
        return  null;
    }

    @Override
    public FeedCreateResponse indexSaveFeed(FeedCreateResponse dto)  {
        dto.TimePush();
        try {
            IndexResponse response = client.index(i -> i
                    .index("board")
                    .document(dto));
            return dto;
        } catch (IOException e) {
            log.error("Error indexing document: {}", e.getMessage(), e);
            throw new IndexException("Failed to index the document", e); // 예외를 커스텀 예외로 던짐
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

        //        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
//        LocalDateTime inputTime = LocalDateTime.parse(time, formatter);
//        LocalDateTime timePlusOneDay = inputTime.plus(1, ChronoUnit.DAYS);
//        String endtime = timePlusOneDay.format(formatter);
    }


    @Override
    public List<Board> findAllFeed() {


        try {
            SearchResponse<Board> response = client.search(s -> s
                            .index("board")
                            .query(q -> q
                                    .matchAll(t -> t)), // 모든 문서 검색
                    Board.class);

            // Elasticsearch 응답에서 데이터를 추출
            List<Board> boards = response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .collect(Collectors.toList());

            return boards;
        } catch (IOException e) {
            // 예외 발생 시 로그 기록
            log.error("Error fetching all feed documents: {}", e.getMessage(), e);
            // 예외를 커스텀 예외로 감싸서 던짐
            throw new IndexException("Failed to fetch all feed documents", e);
        }
    }

    @Override
    public List<Board> findLikeCount() {

        try {
            SearchResponse<Board> response = client.search(s -> s
                            .index("board")
                            .query(q -> q.matchAll(t -> t))
                            .sort(sort -> sort.field(f -> f
                                    .field("likeCount") // 정렬 기준 필드: likeCount
                                    .order(SortOrder.Desc) // 내림차순 정렬
                            )),
                    Board.class);

            return response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Error fetching like count feed: {}", e.getMessage(), e);
            throw new IndexException("Failed to fetch like count feed", e); // 예외를 커스텀 예외로 감싸서 던짐
        }
    }

//    @Override
//    public void  modifyVisitCount(String userId) {
//        try {
//            // Step 1: feedUID로 _id 검색
//            SearchResponse<Board> searchResponse = client.search(s -> s
//                    .index("board")
//                    .query(q -> q
//                            .term(t -> t
//                                    .field("userId")
//                                    .value(userId)
//                            )
//                    ), Board.class
//            );
//            log.info(searchResponse.toString());
//            String documentId = searchResponse.hits().hits().get(0).id();
//            client.update(u -> u
//                    .index("board")
//                    .id(documentId)
//                    .refresh(Refresh.WaitFor)
//                    .script(s -> s
//                            .source("ctx._source.viewCount += params.increment")
//                            .params(Map.of("increment", JsonData.of(increment)))
//
//                    ), Board.class
//            );
//        } catch (IOException e) {
//            log.info("조회수 업데이트 실패");
//            throw new IndexException(e);
//        }
//    }

    @Override
    public List<Board> findPagingFeed(int page, int size) {
        try {
            SearchResponse<Board> response = client.search(s -> s
                            .index("board")
                            .from(page * size)
                            .size(size)
                            .sort(sort -> sort.field(f -> f
                                    .field("createdAt")
                                    .order(SortOrder.Desc) // 내림차순 정렬
                            ))
                            .query(q -> q.matchAll(t -> t)),
                    Board.class);

            return response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Error fetching paging feed: {}", e.getMessage(), e);
            throw new IndexException("Failed to fetch paging feed", e); // 예외를 커스텀 예외로 감싸서 던짐
        }
    }

    @Override
    public List<Board> findMostViewFeed(int page, int size) {
        try {
            SearchResponse<Board> response = client.search(s -> s
                            .index("board")
                            .from((page - 1) * size)
                            .size(size)
                            .sort(sort -> sort.field(f -> f
                                    .field("viewCount")
                                    .order(SortOrder.Desc)))
                            .query(q -> q.matchAll(t -> t)),
                    Board.class);

            return response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .collect(Collectors.toList());
        } catch (IOException  e) {
            log.error("Error fetching most viewed feed: {}", e.getMessage(), e);
            throw new IndexException("Failed to fetch most viewed feed", e); // 예외를 커스텀 예외로 감싸서 던짐
        }
    }

    @Override
    public double findTotalPage(int page, int size) {
        try {
            SearchResponse<Board> response = client.search(s -> s
                            .index("board")
                            .from(page * size)
                            .size(size)
                            .query(q -> q.matchAll(t -> t)),
                    Board.class);
            long totalHits = response.hits().total().value();
            return totalHits;
        } catch (IOException  e) {
            log.error("Error fetching total page count: {}", e.getMessage(), e);
            throw new IndexException("Failed to fetch total page count", e); // 예외를 커스텀 예외로 감싸서 던짐
        }
    }

    @Override
    @Transactional
    public void deleteFeedOne(String id)  {
        try {
            SearchResponse<Board> searchResponse = client.search(s -> s
                    .index("board")
                    .query(q -> q
                            .term(t -> t
                                    .field("feedUID.keyword")
                                    .value(id)
                            )
                    ), Board.class
            );
            String documentId = searchResponse.hits().hits().get(0).id();
            boardRepository.deleteById(documentId);
//            log.info("Successfully deleted board with id: {}", id);
        } catch (Exception e) {
            log.error("Error deleting board with id: {}", id, e);
            throw new RuntimeException("Failed to delete feed", e);
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
    public List<Board> findCategoryAndContent(String category)  {
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
            throw new IndexException("Failed to fetch category and content feed", e); // 예외를 커스텀 예외로 감싸서 던짐
        }
    }

    @Override
    public double findSumLikeByPageOne(int page, int size)  {


        try {
            // Elasticsearch 검색 및 집계 요청
            SearchResponse<Board> response = client.search(s -> s
                            .index("board")
                            .from(size) // 페이지 시작점
                            .size(size)
                            .aggregations("totalLikes", a -> a.sum(sum -> sum.field("likeCount"))),
                    Board.class);

            return response.aggregations()
                    .get("totalLikes")
                    .sum()
                    .value();
        } catch (IOException | ElasticsearchException e) {
            log.error("Error fetching sum of likes by page: {}", e.getMessage(), e);
            throw new IndexException("Failed to fetch sum of likes by page", e); // 예외를 커스텀 예외로 감싸서 던짐
        }
    }

    @Override
    public double findSumFeed()  {

        try {
            // Elasticsearch 검색 및 집계 요청
            SearchResponse<Board> response = client.search(s -> s
                            .index("board")
                            .aggregations("feedCount", a -> a.valueCount(vc -> vc.field("feedUID.keyword"))),
                    Board.class);

            Double feedCount = response.aggregations()
                    .get("feedCount")
                    .valueCount()
                    .value();

            return feedCount;
        } catch (IOException  e) {
            log.error("Error fetching sum of feed: {}", e.getMessage(), e);
            throw new IndexException("Failed to fetch sum of feed", e); // 예외를 커스텀 예외로 감싸서 던짐
        }
    }


    @Override
    public List<Board> findMonthPopularFeed()  {
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
        } catch (IOException  e) {
            log.error("Error fetching month popular feed: {}", e.getMessage(), e);
            throw new IndexException("Failed to fetch month popular feed", e); // 예외를 커스텀 예외로 감싸서 던짐
        }
    }

    @Override
    public List<Board> findPopularFeedDESC(int page,int size)  {
        try {
            SearchResponse<Board> response = client.search(s -> s
                            .index("board")
                            .size(size)
                            .sort(sort -> sort.field(f -> f
                                    .field("likeCount")
                                    .order(SortOrder.Desc)))
                            .query(q -> q.matchAll(t -> t))
                            ,Board.class);
            return response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .collect(Collectors.toList());
        } catch (IOException  e) {
            log.error("Error fetching month popular feed: {}", e.getMessage(), e);
            throw new IndexException("Failed to fetch month popular feed", e); // 예외를 커스텀 예외로 감싸서 던짐
        }
    }

    @Override
    public List<Board> findRecentFeed()  {

        try {
            SearchResponse<Board> response = client.search(s -> s
                            .index("board")
                            .query(q -> q.matchAll(t -> t))  // 모든 문서를 검색
                            .sort(sort -> sort.field(f -> f
                                    .field("createdAt")
                                    .order(SortOrder.Desc) // 내림차순 정렬
                            )),
                    Board.class);   // 결과를 Board 클래스 객체로 매핑

            return response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Error fetching recent feed: {}", e.getMessage(), e);
            throw new IndexException("Failed to fetch recent feed", e); // 예외를 커스텀 예외로 감싸서 던짐
        }
    }


    @Override
    public Board findIdOne(String id) {
        try {
            SearchResponse<Board> response = client.search(g -> g
                            .index("board")
                            .query(q -> q
                                    .term(t -> t
                                            .field("feedUID.keyword") //Keyword를 왜 썼을까?
                                            .value(id))),
                    Board.class);

            if (response.hits().hits().isEmpty()) {
                log.warn("Document not found for feedUID: {}", id);
                return null;
            }
            return response.hits().hits().get(0).source();
        } catch (IOException e) {
            log.error("Error fetching document by ID: {}", e.getMessage(), e);
            throw new IndexException("Failed to fetch document by ID", e); // 예외를 커스텀 예외로 감싸서 던짐
        }
    }

    @Override
    public void saveViewCounts(String feedUID, Board view) {
        try {
            // Step 1: feedUID로 _id 검색
            SearchResponse<Board> searchResponse = client.search(s -> s
                    .index("board")
                    .query(q -> q
                            .term(t -> t
                                    .field("feedUID.keyword")
                                    .value(feedUID)
                            )
                    ), Board.class
            );
            log.info(searchResponse.toString());
            String documentId = searchResponse.hits().hits().get(0).id();
            client.update(u -> u
                    .index("board")
                    .id(documentId) // _id 사용
                    .refresh(Refresh.WaitFor)
                    .script(s -> s
                            .source("ctx._source.viewCount += params.increment")
                            .params(Map.of("increment", JsonData.of(increment)))

                    ), Board.class
            );
        } catch (IOException e) {
            log.info("조회수 업데이트 실패");
            throw new IndexException(e);
        }
    }


    @Override
    public Board findPopularFeedOne()  {
        try {
            // Elasticsearch 검색 및 집계 요청
            SearchResponse<Board> response = client.search(s -> s
                            .query(q -> q.matchAll(t -> t))
                            .index("board")
                            .aggregations("totalLikes", a -> a
                                    .max(max -> max.field("likeCount")))
                            .query(q -> q.match(t -> t.field("likeCount")
                                    .query("totalLikes"))),
                    Board.class);

            // 검색된 문서 리스트 가져오기
            double maxLikes = response.aggregations()
                    .get("totalLikes")
                    .max()
                    .value();

            return null;
        } catch (IOException e) {
            log.error("Error fetching popular feed: {}", e.getMessage(), e);
            throw new IndexException("Failed to fetch popular feed", e); // 예외를 커스텀 예외로 감싸서 던짐
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
        }catch (IOException e){
            log.error("Error fetching FeedCount feed: {}", e.getMessage(), e);
            throw new IndexException("Failed to fetch popular feed", e); // 예외를 커스텀 예외로 감싸서 던짐
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

            // 검색 결과가 없는 경우 예외 처리
            if (searchResponse.hits().hits().isEmpty()) {
                throw new IndexException("게시물을 찾을 수 없습니다. feedUID: " + id);
            }

            String documentId = searchResponse.hits().hits().get(0).id();

            // 게시물 업데이트
            UpdateResponse<Board> response = client.update(u -> u
                    .index("board")
                    .id(documentId)
                    .doc(eq), Board.class);

            // 응답이 null인 경우 예외 처리
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
            throw new IndexException("Failed to modify feed", e); // 예외를 커스텀 예외로 감싸서 던짐
        }
    }

    @Override
    public int findAllViewCount() {
        try {
            SearchResponse<Board> searchResponse = client.search(s -> s
                    .index("board")
                            .aggregations("totalViews", a -> a.sum(sum -> sum.field("viewCount"))),
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
}




