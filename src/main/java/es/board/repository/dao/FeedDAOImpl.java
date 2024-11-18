package es.board.repository.dao;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.*;
import es.board.model.res.FeedCreateResponse;
import es.board.repository.document.Board;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import static java.rmi.server.LogStream.log;
@Repository
@Slf4j
@RequiredArgsConstructor
public class FeedDAOImpl implements FeedDAO {

    private final ElasticsearchClient client;

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
        log.info(pages.toString());
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
    public FeedCreateResponse indexSaveFeed(FeedCreateResponse dto) throws IOException {
        dto.TimePush();
        try {
            log.info(dto.toString());
            IndexResponse response = client.index(i -> i
                    .index("board")
                    .document(dto));
            return  dto;
        } catch (IOException e) {
            // 오류가 발생한 경우 로그를 출력합니다.
            System.err.println("Error indexing document: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Board> findRangeTimeFeed(String time) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        LocalDateTime inputTime = LocalDateTime.parse(time, formatter);

        LocalDateTime timePlusOneDay = inputTime.plus(1, ChronoUnit.DAYS);
        String endtime = timePlusOneDay.format(formatter);
        SearchResponse<Board> response = client.search(s -> s
                .index("board")
                .query(q -> q
                        .range(r->r
                                .date(v->v
                                        .gte(time)
                                        .lte(endtime)
                                                .field("createdAt")
                                ))), Board.class);
        List<Board> boards = response.hits().hits().stream()
                .map(hit -> hit.source())
                .collect(Collectors.toList());
        log.info(boards.toString());
        return boards;
    }


    @Override
    public  List<Board> findAllFeed() throws IOException, ElasticsearchException {
        SearchResponse<Board> response = client.search(s -> s
                        .index("board")
                        .query(q->q
                        .matchAll(t->t)),  //? match 와 matchAll의 차이
                        Board.class);
        List<Board> boards = response.hits().hits().stream()
                .map(hit -> hit.source())
                .collect(Collectors.toList());
        return boards;

    }

    @Override
    public List<Board> findContent(String text) throws IOException {
        SearchResponse<Board> response = client.search(s -> s
                        .index("board")
                        .query(q -> q
                                .match(t -> t
                                        .field("description")
                                        .query(text))),
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
                Board.class   // 결과를 Comment 클래스 객체로 매핑
        );

        List<Board> boards = response.hits().hits().stream()
                .map(hit -> hit.source())
                // Elasticsearch 문서를 Comment 객체로 변환
                .collect(Collectors.toList());
        return boards;
    }

    @Override
    public List<Board> findPagingFeed(int num) throws IOException {
        SearchResponse<Board> response = client.search(s -> s
                        .index("board")  // 'comments' 인덱스에서 검색
                        .from(num)
                        .size(num+1)
                        .query(q -> q
                                .matchAll(t ->t)),
                Board.class  // 결과를 Comment 클래스 객체로 매핑
        );

        List<Board> boards = response.hits().hits().stream()
                .map(hit -> hit.source())
                // Elasticsearch 문서를 Comment 객체로 변환
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
}
