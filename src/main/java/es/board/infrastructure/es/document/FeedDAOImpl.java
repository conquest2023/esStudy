package es.board.infrastructure.es.document;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import es.board.ex.IndexException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FeedDAOImpl implements FeedDAO{

    private final ElasticsearchClient client;

    @Override
    public List<Feed> findSearchPost(String text) {
        try {
            SearchResponse<Feed> response = client.search(s -> s
                            .index("content_read")
                            .query(q -> q
                                    .bool(b -> b
                                            .should(
                                                    a -> a.match(t -> t
                                                            .field("title")
                                                            .query(text)))
                                            .should(
                                                    t -> t.match(m -> m
                                                            .field("content")
                                                            .query(text))))),
                    Feed.class);
            return response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Error searching documents by text: {}", e.getMessage(), e);
            throw new IndexException("Failed to search feed by text", e);
        }
    }

    @Override
    public List<Feed> findSearchUserPost(String username) {
        try {
        SearchResponse<Feed> response = client.search(s -> s
                .index("content_read")
                        .size(10000)
                        .query(q -> q
                        .bool(b -> b
                                .filter(
                                        a -> a.term(t -> t
                                                .field("docType")
                                                .value("feed")))
                                .filter(
                                        t -> t.match(m -> m
                                        .field("username")
                                        .query(username)))))

                ,Feed.class);

            return response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Error fetching user board stats: {}", e.getMessage(), e);
            throw new IndexException("Failed to fetch user board stats", e);
        }
    }
}
