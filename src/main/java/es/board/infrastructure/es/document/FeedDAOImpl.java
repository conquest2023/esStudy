package es.board.infrastructure.es.document;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
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
public class FeedDAOImpl implements FeedDAO {

    private final ElasticsearchClient client;

    @Override
    public List<Feed> findSearchContent(String content) {
        try {

            SearchResponse<Feed> response = client.search(s -> s
                            .index("content_read")
                            .size(1000)
                            .query(
                                    q -> q.match(m -> m
                                            .field("content")
                                            .query(content)))
                            .sort(sort ->
                                    sort.field(f -> f
                                            .field("createdAt")
                                            .order(SortOrder.Desc)
                                    ))
                    , Feed.class);
            return response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Error searching documents by text: {}", e.getMessage(), e);
            throw new IndexException("Failed to search feed by text", e);
        }
    }

    @Override
    public List<Feed> findSearchTitle(String title) {
        try {
            SearchResponse<Feed> response = client.search(s -> s
                            .index("content_read")
                            .size(1000)
                            .query(q -> q
                                    .bool(b -> b
                                            .filter(f -> f.term(t -> t.field("docType").value("feed")))
                                            .must(m -> m.match(sh -> sh.field("title").query(title)))
                                    )
                            )
                            .sort(sort -> sort.field(f -> f.field("createdAt").order(SortOrder.Desc))),
                    Feed.class);
            return response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .collect(Collectors.toList());
        }catch(IOException e){
            log.error("Error searching documents by text: {}", e.getMessage(), e);
            throw new IndexException("Failed to search feed by text", e);
        }
    }

    @Override
    public List<Feed> findSearchPost(String text) {
        try {
            SearchResponse<Feed> response = client.search(s -> s
                            .index("content_read")
                            .size(1000)
                            .query(q -> q
                                    .bool(b -> b
                                            // 1. 반드시(MUST) docType은 feed여야 함
                                            .filter(f -> f.term(t -> t.field("docType").value("feed")))

                                            // 2. 제목이나 내용 중 하나는 반드시 포함되어야 함
                                            .must(m -> m.bool(sb -> sb
                                                    .should(sh -> sh.match(t -> t.field("title").query(text)))
                                                    .should(sh -> sh.match(t -> t.field("content").query(text)))
                                                    .minimumShouldMatch("1") // should 중 최소 1개 일치 강제
                                            ))
                                    )
                            )
                            .sort(sort -> sort.field(f -> f.field("createdAt").order(SortOrder.Desc))),
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
                        .size(1000)
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
                        .sort(sort ->
                                sort.field(f -> f
                                        .field("createdAt")
                                        .order(SortOrder.Desc)
                                ))
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
