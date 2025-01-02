package es.board.repository.document.impl;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.JsonData;
import es.board.ex.IndexException;
import es.board.repository.LikeDAO;
import es.board.repository.document.Board;
import es.board.repository.document.Comment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Map;

@Repository
@Slf4j
@RequiredArgsConstructor
public class LikeDAOImpl implements LikeDAO {
    private final int increment = 1;

    private final ElasticsearchClient client;
    @Override
    public int saveCommentLike(String id) {
        try {
            // 검색 쿼리 실행
            SearchResponse<Comment> searchResponse = client.search(s -> s
                    .index("comment")
                    .query(q -> q
                            .term(t -> t
                                    .field("commentUID")
                                    .value(id)
                            )
                    ), Comment.class
            );

            // 결과가 없으면 예외 처리
            if (searchResponse.hits().hits().isEmpty()) {
                throw new IndexException("No documents found for commentUID: " + id);  // 결과가 없으면 예외 처리
            }

            // 첫 번째 문서 ID 추출
            String documentId = searchResponse.hits().hits().get(0).id();

            // 업데이트 쿼리 실행
            client.update(u -> u
                    .index("comment")
                    .id(documentId)
                    .refresh(Refresh.WaitFor)
                    .script(s -> s
                            .source("ctx._source.likeCount += params.increment")
                            .params(Map.of("increment", JsonData.of(increment)))
                    ), Comment.class
            );

        } catch (IOException e) {
            log.error("좋아요 업데이트 실패", e);  // 에러 메시지와 스택 트레이스를 기록
            throw new IndexException("Error updating like count", e);  // 에러 원인도 함께 던짐
        }
        return 0;
    }

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
            log.info("좋아요 업데이트 실패");
            throw new IndexException(e);
        }
        return 0;
    }


    @Override
    public int cancelLike(String id) {
        return 0;
    }
}
