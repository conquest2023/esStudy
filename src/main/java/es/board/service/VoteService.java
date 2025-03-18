package es.board.service;

import es.board.controller.model.req.VoteResponse;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface VoteService {

    CompletableFuture<Void> saveFeedVote(VoteResponse vote, String  username, String userId);

    CompletableFuture<Void> saveVote(VoteResponse vote,String  username, String userId);


    CompletableFuture<Void> saveAgreeVote(VoteResponse vote,String  username, String userId);
    void oppositeVote();

    List<VoteResponse> getVotePageFeed(int page, int size);
    List<VoteResponse> getVoteUserAll();

    List<VoteResponse> getVoteFeedDetail(String feedUID);

    Map<String,Object> getVoteAggregation(Long id);

    VoteResponse getVoteContent();

    VoteResponse getVoteDetail(String feedUID);

    List<VoteResponse> getVoteAll();
}
