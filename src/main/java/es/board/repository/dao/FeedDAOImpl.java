package es.board.repository.dao;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import es.board.model.req.FeedRequest;
import es.board.model.req.FeedUpdate;
import es.board.model.res.FeedCreateResponse;
import es.board.model.res.ViewCountResponse;
import es.board.repository.BoardRepository;
import es.board.repository.document.Board;
import es.board.repository.document.Comment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static java.rmi.server.LogStream.log;
@Repository
@Slf4j
@RequiredArgsConstructor
public class FeedDAOImpl implements FeedDAO {

    private final ElasticsearchClient client;

    private  final BoardRepository boardRepository;

    private final int increment=1;

    @Override
    public String saveFeed(String index, FeedCreateResponse dto) throws IOException {
        dto.TimePush();
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
    public List<Board> saveBulkFeed(List<Board> pages) throws IOException {


        BulkRequest.Builder br = new BulkRequest.Builder();
        for (Board product : pages) {
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
    public FeedCreateResponse indexSaveFeed(FeedCreateResponse dto) throws IOException {
        dto.TimePush();
        try {
            IndexResponse response = client.index(i -> i
                    .index("board")
                    .document(dto));
            return dto;
        } catch (IOException e) {
            // 오류가 발생한 경우 로그를 출력합니다.
            System.err.println("Error indexing document: " + e.getMessage());
            throw e;
        }
    }


    @Override
    public List<Board> findRangeTimeFeed(LocalDateTime startDate, LocalDateTime endDate) throws IOException {


        String start = startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
        String end = endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
        SearchResponse<Board> response = client.search(s -> s
                .index("board")
                .query(q -> q
                        .range(r -> r
                                .date(v -> v
                                        .gte(start)
                                        .lte(end)
                                        .field("createdAt")))), Board.class);
        List<Board> boards = response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());
        return boards;

        //        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
//        LocalDateTime inputTime = LocalDateTime.parse(time, formatter);
//        LocalDateTime timePlusOneDay = inputTime.plus(1, ChronoUnit.DAYS);
//        String endtime = timePlusOneDay.format(formatter);
    }


    @Override
    public List<Board> findAllFeed() throws IOException, ElasticsearchException {


        SearchResponse<Board> response = client.search(s -> s
                        .index("board")
                        .query(q -> q
                                .matchAll(t -> t)),  //? match 와 matchAll의 차이
                Board.class);
        List<Board> boards = response.hits().hits().stream()
                .map(hit -> hit.source())
                .collect(Collectors.toList());
        return boards;
    }

    @Override
    public List<Board> findLikeCount() throws IOException {
        SearchResponse<Board> response = client.search(s -> s
                        .index("board")
                        .query(q -> q.matchAll(t -> t))
                        .sort(sort -> sort.field(f -> f
                                .field("likeCount")  // 정렬 기준 필드: likeCount
                                .order(SortOrder.Desc)// 내림차순 정렬
                        )),
                Board.class);

        List<Board> boards = response.hits().hits().stream()
                .map(hit -> hit.source())
                // Elasticsearch 문서를 Comment 객체로 변환
                .collect(Collectors.toList());
        return boards;
    }

    @Override
    public List<Board> findPagingFeed(int page, int size) throws IOException {

        SearchResponse<Board> response = client.search(s -> s
                        .index("board")
                        .from(page * size)
                        .sort(sort -> sort.field(f -> f
                                .field("createdAt")
                                .order(SortOrder.Desc) // 내림차순 정렬
                        ))
                        .size(size)
                        .query(q -> q
                                .matchAll(t -> t)),
                Board.class);
        List<Board> boards = response.hits().hits().stream()
                .map(hit -> hit.source())
                .collect(Collectors.toList());
        return boards;
    }


    @Override
    public double findTotalPage(int page, int size) throws IOException {
        SearchResponse<Board> response = client.search(s -> s
                        .index("board")
                        .from(page * size)
                        .size(size)
                        .query(q -> q
                                .matchAll(t -> t)),
                Board.class
        );
        long totalHits = response.hits().total().value();

//        log.info(String.valueOf(totalHits));
        return totalHits;

    }

    @Override
    @Transactional
    public void deleteFeedOne(String id) throws IOException {
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

        log.info("Attempting to delete board with id: " + id);
        try {
            boardRepository.deleteById(documentId);
            log.info("Successfully deleted board with id: " + id);
        } catch (Exception e) {
            log.error("Error deleting board with id: " + id, e);
        }
    }


    @Override
    public List<Board> findSearchBoard(String text) throws IOException {
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
        List<Board> boards = response.hits().hits().stream()
                .map(hit -> hit.source())
                .collect(Collectors.toList());
        return boards;

    }

    @Override
    public List<Board> findCategoryAndContent(String category) throws IOException {
        SearchResponse<Board> response = client.search(s -> s
                        .index("board")
                        .query(q -> q
                                .bool(b -> b
                                        .must(m -> m.term(t -> t.field("category")
                                                .value(category)))
                                        .should(t -> t.match(m -> m
                                                .field("description")
                                                .query(category))))),
                Board.class);
        List<Board> boards = response.hits().hits().stream()
                .map(hit -> hit.source())
                .collect(Collectors.toList());
        return boards;

    }

    @Override
    public double findSumLikeByPageOne(int page, int size) throws IOException {


        // Elasticsearch 검색 및 집계 요청
        SearchResponse<Board> response = client.search(s -> s

                        .index("board")
                        .from(size) // 페이지 시작점
                        .size(size)
                        .aggregations("totalLikes", a -> a
                                .sum(sum -> sum.field("likeCount"))),
                Board.class);

        return response.aggregations()
                .get("totalLikes")
                .sum()
                .value();
    }


    @Override
    public List<Board> findMonthPopularFeed() throws IOException {
        int monthPage = 5;
        SearchResponse<Board> response = client.search(s -> s
                        .index("board")
                        .sort(sort -> sort.field(f -> f
                                .field("likeCount")
                                .order(SortOrder.Desc)))
                        .size(monthPage)
                        .query(q -> q
                                .matchAll(t -> t)),
                Board.class);
        List<Board> boards = response.hits().hits().stream()
                .map(hit -> hit.source())
                .collect(Collectors.toList());
        return boards;
    }

    @Override
    public List<Board> findRecentFeed() throws IOException {

        SearchResponse<Board> response = client.search(s -> s
                        .index("board")
                        .query(q -> q.matchAll(t -> t))  // 모든 문서를 검색
                        .sort(sort -> sort.field(f -> f
                                .field("createdAt")
                                .order(SortOrder.Desc)// 내림차순 정렬
                        )),
                Board.class   // 결과를 Comment 클래스 객체로 매핑
        );

        List<Board> boards = response.hits().hits().stream()
                .map(hit -> hit.source())
                // Elasticsearch 문서를 Comment 객체로 변환
                .collect(Collectors.toList());
        return boards;
    }


    @Override
    public Board findIdOne(String id) throws IOException {
//        log.info(id);
        SearchResponse<Board> response = client.search(g -> g
                        .index("board")
                        .query(q -> q
                                .term(t -> t
                                        .field("feedUID.keyword") //Keyword를 왜 썻을까?
                                        .value(id))),
                Board.class);
        if (response.hits().hits().isEmpty()) {
            log.warn("문서를 찾을 수 없습니다. feedUID: {}", id);
            return null;
        }
        return response.hits().hits().get(0).source();
    }

    @Override
    public  void saveViewCounts(String feedUID, Board view) throws IOException {
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
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("조회수 업데이트 실패");
        }
    }



    @Override
    public Board findPopularFeedOne() throws IOException {
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

        return  null;
    }



    @Override
    public Board modifyFeed(String id, FeedUpdate eq) throws Exception {
        UpdateResponse<Board> response= client.update(u -> u
                        .index("board")
                        .id(id)
                        .doc(eq),
                Board.class
        );

//        List<Board> comments = new ArrayList<>();
//        comments.add(Board);
        return response.get().source();
    }

    }





