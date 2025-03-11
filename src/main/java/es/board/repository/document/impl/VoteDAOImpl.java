package es.board.repository.document.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import es.board.controller.model.req.VoteResponse;
import es.board.ex.IndexException;
import es.board.repository.VoteDAO;
import es.board.repository.document.VoteAnalytics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class VoteDAOImpl implements VoteDAO {

    private  final ElasticsearchClient client;

    @Override
    public void saveVoteContent(VoteResponse voteResponse, Long id) {
        try {
            IndexResponse response = client.index(i -> i
                    .index("data_votes")
                    .id(String.valueOf(id))
                    .document(voteResponse));
        } catch (IOException e) {
            log.error("Error indexing document: {}", e.getMessage(), e);
            throw new IndexException("Failed to index the document", e);
        }
    }

    @Override
    public void saveAggregationAgreeVote(VoteResponse voteResponse, Long id) {
        log.info(voteResponse.toString());
        try {
            IndexResponse response = client.index(i -> i
                    .index("vote_analytics")
                    .id(String.valueOf(voteResponse.getId()))
                    .document(voteResponse));
            log.info("response={}",response.toString());
        } catch (IOException e) {
            log.error("Error indexing document: {}", e.getMessage(), e);
            throw new IndexException("Failed to index the document", e);
        }
    }

    @Override
    public Map<String, Object> getVoteStatistics(String id) {
        try {
            SearchResponse<VoteAnalytics> response = client.search(s -> s
                            .index("vote_analytics")
                            .size(0)
                            .query(q -> q.bool(b ->
                                    b.filter(
                                            f -> f.term(t -> t.field("voteId").value(id)))
                                    ))
                            .aggregations("totalVotes", a -> a.valueCount(v -> v.field("upvote")))
                            .aggregations("upvote_count", a -> a.sum(v -> v.field("upvote")))
                            .aggregations("user_votes", a -> a.terms(t -> t.field("userId.keyword"))),
                    VoteAnalytics.class);
            log.info(response.toString());
            double totalVotes = response.aggregations().get("totalVotes").valueCount().value();
            double upvotes = response.aggregations().get("upvote_count").sum().value();
            double downvotes = totalVotes - upvotes;
            List<String> userIds = new ArrayList<>();
            Aggregate aggs = response.aggregations().get("user_votes");
                if (aggs.sterms() != null) {
                    for (StringTermsBucket bucket : aggs.sterms().buckets().array()) {
                       userIds.add((bucket.key().stringValue()));
                    }
                }


            return Map.of(
                    "totalVotes", (int) totalVotes,
                    "upvotes", (int) upvotes,
                    "downvotes", (int) downvotes,
                    "userIds",userIds
            );
        } catch (Exception e) {
            log.error("Elasticsearch 조회 중 오류 발생", e);
            return Map.of("error", "Elasticsearch 조회 실패");
        }
    }
}
