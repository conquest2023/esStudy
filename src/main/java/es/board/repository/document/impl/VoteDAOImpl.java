package es.board.repository.document.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import es.board.controller.model.req.VoteDTO;
import es.board.ex.IndexException;
import es.board.repository.VoteDAO;
import es.board.repository.document.VoteAnalytics;
import es.board.repository.document.VoteDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class VoteDAOImpl implements VoteDAO {

    private  final ElasticsearchClient client;

    @Override
    public void saveVoteContent(VoteDTO voteResponse, Long id) {
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
    public void saveVoteTicket(VoteDTO voteResponse) {
        try {
            IndexResponse response = client.index(i -> i
                    .index("vote_analytics")
                    .document(voteResponse));
        } catch (IOException e) {
            log.error("Error indexing document: {}", e.getMessage(), e);
            throw new IndexException("Failed to index the document", e);
        }
    }
    @Override
    public void saveAggregationAgreeVote(VoteDTO voteResponse, Long id) {
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
    public List<VoteDocument> findFeedVoteAll() {
        try {
            SearchResponse<VoteDocument> response = client.search(s -> s
                            .index("data_votes")
                            .query(q->q
                                    .bool(b->b
                                    .filter(f -> f
                                            .term(t -> t
                                                    .field("category")
                                                    .value("투표"))))),
                   VoteDocument.class);
            return  response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Error fetching FeedCount feed: {}", e.getMessage(), e);
            throw new IndexException("Failed to fetch popular feed", e);
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
    @Override
    public Map<String, Object> getVoteFeedStatistics(String id) {
        try {
            SearchResponse<VoteAnalytics> response = client.search(s -> s
                            .index("vote_analytics")
                            .size(0)
                            .query(q -> q.bool(b ->
                                    b.filter(
                                            f -> f.term(t -> t.field("feedUID").value(id)))))
                            .aggregations("selectedOptions", a -> a.terms(v -> v.field("selectedOption")))
                            .aggregations("user_votes", a -> a.terms(t -> t.field("userId.keyword"))),
                    VoteAnalytics.class);
            Set<String> userIds = new HashSet<>();
            Aggregate userVotes = response.aggregations().get("user_votes");
            if (userVotes.sterms() != null) {
                for (StringTermsBucket bucket : userVotes.sterms().buckets().array()) {
                    userIds.add((bucket.key().stringValue()));
                }
            }
            Map<String, Integer> hashMap = new HashMap<>();
            Aggregate selectedOptions = response.aggregations().get("selectedOptions");
            if (selectedOptions.sterms() != null) {
                for (StringTermsBucket bucket : selectedOptions.sterms().buckets().array()) {
                    hashMap.put(bucket.key().stringValue(), (int) bucket.docCount());
                }
            }
            return Map.of(
                    "users",userIds,
                    "selectOption",hashMap
            );
        } catch (Exception e) {
            log.error("Elasticsearch 조회 중 오류 발생", e);
            return Map.of("error", "Elasticsearch 조회 실패");
        }
    }
    @Override
    public List<VoteDocument> findVotePageFeed(int page, int size) {
        try {
            SearchResponse<VoteDocument> response = client.search(s -> s
                    .index("data_votes")
                    .from(page * size)
                    .size(size)
                            .sort(sort -> sort.field(f -> f
                                    .field("createdAt")
                                    .order(SortOrder.Desc)
                            ))
                    .query(q -> q
                            .bool(b -> b
                                    .filter(
                                            st -> st.term(t -> t
                                                    .field("category")
                                                    .value("투표"))))), VoteDocument.class);
            return response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Error data Vote feed: {}", e.getMessage(), e);
            throw new IndexException("Failed to Vote feed", e);
        }
    }

    @Override
    public VoteDocument findVoteFeedDetail(String feedUID) {
        try {
            SearchResponse<VoteDocument> response = client.search(s -> s
                    .index("data_votes")
                    .query(q -> q
                            .bool(b -> b
                                    .filter(
                                            st -> st.term(t -> t
                                                    .field("feedUID")
                                                    .value(feedUID))))), VoteDocument.class);
            return  response.hits().hits().get(0).source();
        } catch (IOException e) {
            log.error("Error data Vote feed: {}", e.getMessage(), e);
            throw new IndexException("Failed to Vote feed", e);
            }
        }
    }
