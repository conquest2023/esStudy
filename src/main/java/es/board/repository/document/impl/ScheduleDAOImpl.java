package es.board.repository.document.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch.core.*;
import es.board.controller.model.req.ScheduleDTO;
import es.board.ex.IndexException;
import es.board.repository.ScheduleDAO;
import es.board.repository.document.Reply;
import es.board.repository.document.Schedule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RequestOptions;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Repository
@Slf4j
public class ScheduleDAOImpl  implements ScheduleDAO {

    private final ElasticsearchClient client;


    @Override
    public void saveSchedule(Schedule schedule) {
        try {
            IndexResponse response = client.index(i -> i
                    .index("schedule_index")
                    .document(schedule));
            // 성공적으로 문서가 저장되면, 문서 ID를 반환.
        } catch (IOException e) {
            // 오류가 발생한 경우 로그를 출력합니다.
            log.error("Error indexing document: {}", e.getMessage(), e);
            throw new IndexException("Failed to index the feed document", e); // 예외를 감싸서 던짐
        }
    }

    @Override
    public void saveScheduleBulk(List<Schedule> schedules) {
        try {
            // 🔹 벌크 요청 빌더
            BulkRequest.Builder bulkRequest = new BulkRequest.Builder();

            // 🔹 문서 리스트를 벌크 요청에 추가
            for (Schedule schedule : schedules) {
                bulkRequest.operations(op -> op
                        .index(idx -> idx
                                .index("schedule_index")
                                .document(schedule)
                        )
                );
            }

            // 🔹 벌크 요청 실행
            BulkResponse response = client.bulk(bulkRequest.build());

            // 🔹 오류가 있는지 확인
            if (response.errors()) {
                log.error("Bulk indexing has failures: {}", response);
            } else {
                log.info("Bulk indexing completed successfully.");
            }

        } catch (IOException e) {
            log.error("Error performing bulk indexing: {}", e.getMessage(), e);
            throw new IndexException("Failed to perform bulk indexing", e);
        }
    }

    @Override
    public List<Schedule> searchSchedule(String userId, String query, String searchType, String sortType) {
        try {
            SearchResponse<Schedule> response = client.search(s -> s
                            .index("schedule_index")  // ✅ 검색할 인덱스 지정
                            .query(q -> q.bool(b -> b
                                    .filter(f -> f.term(t -> t.field("userId").value(userId)))  // ✅ user_id 필터
                                    .must(m -> {
                                        if (query != null && !query.isEmpty()) {
                                            switch (searchType) {
                                                case "title":
                                                    return m.match(mt -> mt.field("title").query(query));
                                                case "description":
                                                    return m.match(mt -> mt.field("description").query(query));
                                                case "category":
                                                    return m.match(mt -> mt.field("category").query(query));
                                            }
                                        }
                                        return m;
                                    })

                            )).sort(w -> w.field(f -> f
                                    .field("start_datetime")  // ✅ 일정 시작일 기준 정렬
                                    .order(SortOrder.Asc)     // ✅ 빠른 일정 순 (오름차순)
                            )),
                    Schedule.class
            );

            log.info("🔍 검색 결과: {}", response.toString());

            return response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .collect(Collectors.toList());

        } catch (IOException e) {
            log.error("❌ 검색 오류 발생: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }


    @Override
    public void deleteSchedule(Long id) {

        try {
            DeleteResponse response = client.delete(d -> d
                    .index("schedule_index")
                    .id(id.toString())
            );
            log.info("🗑 Elasticsearch 삭제 완료: " + id);
        } catch (IOException e) {
            log.error("🚨 Elasticsearch 삭제 중 오류 발생: {}", e.getMessage());
        }
    }

    @Override
    public void deleteRepeatSchedule(String userId, LocalDateTime start, LocalDateTime end) {
        log.info("🗑 삭제 요청: userId={}, start={}, end={}", userId, start, end);
        try {
            DeleteByQueryRequest request = new DeleteByQueryRequest.Builder()
                    .index("schedule_index")
                    .query(q -> q
                            .bool(b -> b
                                    .must(m -> m.term(t -> t.field("userId").value(userId))) // ✅ userId 정확히 매칭
                                    .must(m -> m.term(t -> t.field("startDatetime").value(FieldValue.of(start.toString()))))
                                    .must(m -> m.term(t -> t.field("endDatetime").value(FieldValue.of(end.toString()))))
                                    .must(m -> m.term(t -> t.field("isRepeat").value(true)))
                            )
                    )
                    .build();

            var response = client.deleteByQuery(request);

            log.info("✅ 반복 일정 Elasticsearch 삭제 완료: {}개 삭제됨", response.deleted());
        } catch (IOException e) {
            log.error("🚨 Elasticsearch 반복 일정 삭제 중 오류 발생: {}", e.getMessage());
        }
    }
}
