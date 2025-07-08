package es.board.repository;

import es.board.controller.model.dto.feed.VoteDTO;
import es.board.repository.document.VoteDocument;

import java.util.List;
import java.util.Map;

public interface VoteDAO {
    void deleteVoteFeed(String id);


    Map<String, Object>  findFeedPagingVote( int page, int size);
    Map<String, Object> getVoteFeedStatistics(String id);
    void saveVoteTicket(VoteDTO.Request voteResponse);
    List<VoteDocument> findFeedVoteAll();
    Map<String, Object> getVoteStatistics(String id);
    void saveVoteContent(VoteDTO.Request voteResponse, Long id);

    void saveAggregationAgreeVote(VoteDTO.Request voteResponse, Long id);

    VoteDocument findVoteFeedDetail(String feedUID);
    List<VoteDocument> findVotePageFeed(int page, int size);

}
