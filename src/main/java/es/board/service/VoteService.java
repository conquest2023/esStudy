package es.board.service;

import es.board.controller.model.req.VoteRequest;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface VoteService {

    void deleteVoteFeed(String id, String userId);
    CompletableFuture<Void> saveFeedTicket(VoteRequest vote, String  username, String userId);
    CompletableFuture<Void> createdFeedVote(VoteRequest vote, String  username, String userId);

    CompletableFuture<Void> saveVote(VoteRequest vote, String  username, String userId);


    CompletableFuture<Void> saveAgreeVote(VoteRequest vote, String  username, String userId);

    List<VoteRequest> getVotePageFeed(int page, int size);
    List<VoteRequest> getVoteUserAll();

    Map<String, Object> getVoteTicketAll(String  id);


    List<VoteRequest> getVoteFeedDetail(String feedUID);

    Map<String,Object> getVoteAggregation(Long id);

    VoteRequest getVoteContent();

    VoteRequest getVoteDetail(String feedUID);

    List<VoteRequest> getVoteAll();
}
