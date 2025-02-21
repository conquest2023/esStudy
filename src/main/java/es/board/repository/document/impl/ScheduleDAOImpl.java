package es.board.repository.document.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import es.board.controller.model.req.ScheduleDTO;
import es.board.ex.IndexException;
import es.board.repository.ScheduleDAO;
import es.board.repository.document.Reply;
import es.board.repository.document.Schedule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;
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

                            )).sort(w-> w.field(f -> f
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
}
