package es.board.repository;

import es.board.controller.model.req.VoteResponse;

import java.util.Map;

public interface VoteDAO {

    Map<String, Object> getVoteStatistics(String id);
    void saveVoteContent(VoteResponse voteResponse, Long id);

    void saveAggregationAgreeVote(VoteResponse voteResponse, Long id);

}
