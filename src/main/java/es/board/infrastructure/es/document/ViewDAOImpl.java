package es.board.infrastructure.es.document;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import es.board.ex.IndexException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ViewDAOImpl implements ViewLogDAO {


    private final ElasticsearchClient client;

    @Override
    public List<View> findUserDailyViewHistory(String userId, LocalDateTime ago) {


        LocalDateTime shiftedBase = ago.minusHours(9);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        String start = shiftedBase.minusDays(1).format(formatter);
        String end = shiftedBase.format(formatter);
        try {
            SearchResponse<View> response = client.search(s -> s
                            .size(100)
                            .index("view-events-v1")
                            .query(q -> q
                            .bool(b -> b
                                    .filter(m -> m.term(t ->
                                            t.field("viewerId")
                                                    .value(userId)
                                    ))
                                    .must(m -> m.range(r ->
                                            r.date(v ->
                                                    v.gte(start).lte(end)
                                                            .field("viewedAt")))))
                    ),
                    View.class);
            return response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Error searching documents by date range: {}", e.getMessage(), e);
            throw new IndexException("Failed to search feed by date range", e);
        }
    }


    @Override
    public List<View> findUsersDailyViewHistorys(List<String> userIds, LocalDateTime ago) {
        LocalDateTime shiftedBase = ago.minusHours(9);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        String start = shiftedBase.minusDays(1).format(formatter);
        String end = shiftedBase.format(formatter);
        try {
            SearchResponse<View> response = client.search(s -> s
                            .size(1000)
                            .index("view-events-v1")
                            .query(q -> q
                                    .bool(b -> b
                                            .filter(m -> m.terms(t -> t
                                                    .field("viewerId")
                                                    .terms(v -> v.value(userIds.stream()
                                                            .map(FieldValue::of)
                                                            .toList()))
                                            ))
                                            .must(m -> m.range(r -> r
                                                    .date(v -> v.gte(start).lte(end)
                                                            .field("viewedAt"))))
                                    )
                            ),
                    View.class);
            return response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Error searching documents by date range: {}", e.getMessage(), e);
            throw new IndexException("Failed to search feed by date range", e);
        }
    }
}
