package es.board.repository.document.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import es.board.ex.IndexException;
import es.board.controller.model.res.ReplyCreate;
import es.board.repository.ReplyDAO;
import es.board.repository.document.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
@Slf4j
public class ReplyDAOImpl implements ReplyDAO {

    private final ElasticsearchClient client;

    @Override
    public List<Reply> findPartialReply(String id){
        try {
            SearchResponse<Reply> response = client.search(s -> s
                    .index("reply")
                    .query(q -> q
                            .bool(b->b
                                    .filter(f->f.term(t->t.field("feedUID")
                                    .value(id)))))

            ,Reply.class);
            return  response.hits().hits().stream()
                    .map(hit->hit.source())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error find Reply with id: {}", id, e);
            throw new RuntimeException("Failed to find Reply", e);
        }
    }

    @Override
    public void saveReply(ReplyCreate dto) {
        try{
            IndexResponse response=client.index(i->i
                    .index("reply")
                    .document(dto));
        }catch (IOException e){
            log.error("존재하지 않는 인덱스입니다");
            throw  new IndexException("존재하지 않는 인덱스입니다");
        }
    }
    }
