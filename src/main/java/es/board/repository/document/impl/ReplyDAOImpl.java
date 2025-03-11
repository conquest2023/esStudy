package es.board.repository.document.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import es.board.ex.IndexException;
import es.board.controller.model.res.ReplyCreate;
import es.board.repository.ReplyDAO;
import es.board.repository.document.Comment;
import es.board.repository.document.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
@Slf4j
public class ReplyDAOImpl implements ReplyDAO {

    private final ElasticsearchClient client;

    @Override
    public Map<String, Object> findPartialReply(String id){
        try {
            SearchResponse<Reply> response = client.search(s -> s
                    .index("reply")
                            .sort(sort -> sort.field(f -> f
                                    .field("createdAt")
                                    .order(SortOrder.Asc)
                            ))
                    .query(q -> q
                            .bool(b->b
                                    .filter(f->f.term(t->t.field("feedUID")
                                    .value(id))))),
            Reply.class);

            List<Reply> replies= response.hits().hits().stream()
                    .map(hit->hit.source())
                    .collect(Collectors.toList());
            Map<String, Object> result = new HashMap<>();
            result.put("replyList", replies);

            return result;
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
    @Override
    public Map<String, Double> findAggregationReply(List<String> feedUIDs) {
        List<FieldValue> fieldValues = feedUIDs.stream()
                .map(FieldValue::of)
                .collect(Collectors.toList());
        try {
            SearchResponse<Reply> response = client.search(s -> s
                    .index("reply")
                    .size(0)
                    .query(q -> q
                            .bool(b -> b.filter(f -> f.terms(
                                    a -> a.field("feedUID")
                                            .terms(v -> v.value(fieldValues))))))
                    .aggregations("feed_reply_count", a -> a
                            .terms(t -> t
                                    .field("feedUID")
                                    .size(feedUIDs.size())
                            )
                            .aggregations("reply_count", ag -> ag
                                    .valueCount(v -> v.field("feedUID"))
                            )), Reply.class);
            return  response.aggregations()
                    .get("feed_reply_count")
                    .sterms()
                    .buckets().array().stream()
                    .collect(Collectors.toMap(
                            bucket -> bucket.key().stringValue(),
                            bucket -> bucket.aggregations()
                                    .get("reply_count")
                                    .valueCount().value()));
        }   catch (IOException e) {
            log.error("Error searching for reply in descending order", e);
            throw new IndexException("Failed to find reply in descending order", e);}
    }
}
