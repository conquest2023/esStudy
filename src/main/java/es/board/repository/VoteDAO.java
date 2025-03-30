package es.board.repository;

import es.board.controller.model.req.VoteRequest;
import es.board.repository.document.VoteDocument;

import java.util.List;
import java.util.Map;

public interface VoteDAO {
    void deleteVoteFeed(String id);
    Map<String, Object> getVoteFeedStatistics(String id);
    void saveVoteTicket(VoteRequest voteResponse);
    List<VoteDocument> findFeedVoteAll();
    Map<String, Object> getVoteStatistics(String id);
    void saveVoteContent(VoteRequest voteResponse, Long id);

    void saveAggregationAgreeVote(VoteRequest voteResponse, Long id);

    VoteDocument findVoteFeedDetail(String feedUID);
    List<VoteDocument> findVotePageFeed(int page, int size);

}
