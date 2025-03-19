package es.board.repository;

import es.board.controller.model.req.VoteDTO;
import es.board.repository.document.VoteDocument;

import java.util.List;
import java.util.Map;

public interface VoteDAO {

    Map<String, Object> getVoteFeedStatistics(String id);
    void saveVoteTicket(VoteDTO voteResponse);
    List<VoteDocument> findFeedVoteAll();
    Map<String, Object> getVoteStatistics(String id);
    void saveVoteContent(VoteDTO voteResponse, Long id);

    void saveAggregationAgreeVote(VoteDTO voteResponse, Long id);

    VoteDocument findVoteFeedDetail(String feedUID);
    List<VoteDocument> findVotePageFeed(int page, int size);

}
