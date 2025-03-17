package es.board.service;

import es.board.controller.model.req.VoteResponse;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface VoteService {

    CompletableFuture<Void> saveUserVote(VoteResponse vote,String  username, String userId);

    CompletableFuture<Void> saveVote(VoteResponse vote,String  username, String userId);


    CompletableFuture<Void> saveAgreeVote(VoteResponse vote,String  username, String userId);
    void oppositeVote();

    Map<String,Object> getVoteAggregation(Long id);

    VoteResponse getVoteContent();

    List<VoteResponse> getVoteAll();
}
