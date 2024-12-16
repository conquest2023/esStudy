package es.board.repository.dao;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.JsonData;
import es.board.ex.IndexException;
import es.board.repository.document.Board;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Map;

@Repository
@Slf4j
@RequiredArgsConstructor
public class LikeDAOImpl implements  LikeDAO {
    private final int increment = 1;

    private final ElasticsearchClient client;

    @Override
    public int saveLike(String feedUID) {
        try {
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
                    .id(documentId)
                    .refresh(Refresh.WaitFor)
                    .script(s -> s
                            .source("ctx._source.likeCount += params.increment")
                            .params(Map.of("increment", JsonData.of(increment)))
                    ), Board.class
            );

        } catch (IOException e) {
            log.info("조회수 업데이트 실패");
            throw new IndexException(e);
        }
        return 0;
    }

    @Override
    public int cancelLike(String id) {
        return 0;
    }
}
