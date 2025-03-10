package es.board.repository.document.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import es.board.repository.VisitDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Repository
@RequiredArgsConstructor
@Slf4j
public class VisitDAOImpl implements VisitDAO {

    private final ElasticsearchClient client;

        @Override
        @Async("taskExecutor")
        public CompletableFuture<Void> saveIp(String userId, String ip, String userAgent) {
            try {
                client.index(i -> i
                        .index("visitor_tracking")
                        .id(userId + "_" + ip)
                        .document(Map.of(
                                "userId", userId,
                                "ipAddress", ip,
                                "userAgent", userAgent,
                                "visitedAt", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))));
                log.info("Visitor log saved asynchronously for: {}", ip);
            } catch (IOException e) {
                log.error("Error Ip document: {}", e.getMessage(), e);
            }
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public Map<String, Long> findVisitorStats(){
            try {
                SearchResponse<Void> response = client.search(s -> s
                                .index("visitor_tracking")
                                .size(0)
                                .aggregations("today_visitors", a -> a
                                        .filter(f -> f
                                                .range(r -> r
                                                        .date(d -> d
                                                                .field("visitedAt")
                                                                .gte("now/d+9h")
                                                                .lt("now+1d/d+9h"))))
                                        .aggregations("unique_today_users", agg -> agg
                                                .cardinality(c -> c.field("ipAddress.keyword"))))
                                .aggregations("current_visitors", a -> a
                                        .filter(f -> f
                                                .range(r -> r
                                                        .date(d -> d
                                                                .field("visitedAt")
                                                                .gte("now-5m+9h")
                                                                .lte("now+9h")
                                                                .format("epoch_millis"))))
                                        .aggregations("unique_current_users", agg -> agg
                                                .cardinality(c -> c.field("ipAddress.keyword"))))
                                .aggregations("total_visitors", a -> a
                                        .cardinality(c -> c.field("ipAddress.keyword"))),
                        Void.class);

                long todayVisitors = response.aggregations()
                        .get("today_visitors")
                        .filter()
                        .aggregations()
                        .get("unique_today_users")
                        .cardinality()
                        .value();

                long currentVisitors = response.aggregations()
                        .get("current_visitors")
                        .filter()
                        .aggregations()
                        .get("unique_current_users")
                        .cardinality()
                        .value();

                long totalVisitors = response.aggregations()
                        .get("total_visitors")
                        .cardinality()
                        .value();

                return Map.of(
                        "todayVisitors", todayVisitors,
                        "currentVisitors", currentVisitors,
                        "totalVisitors", totalVisitors
                );

            } catch (IOException e) {
                log.error("Error retrieving visitor stats", e);
                return Map.of(
                        "todayVisitors", 0L,
                        "currentVisitors", 0L,
                        "totalVisitors", 0L
                );
            }
        }
    }