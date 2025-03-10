package es.board.service;

import es.board.controller.model.req.VoteResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface VoteService {



    CompletableFuture<Void> saveVote(VoteResponse vote,String  username, String userId);

    void cancelVote();


    VoteResponse getVoteContent();

    List<VoteResponse> getVoteAll();
}
