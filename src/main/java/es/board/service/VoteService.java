package es.board.service;

import es.board.controller.model.req.VoteDTO;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface VoteService {

    void deleteVoteFeed(String id, String userId);
    CompletableFuture<Void> saveFeedTicket(VoteDTO vote, String  username, String userId);
    CompletableFuture<Void> createdFeedVote(VoteDTO vote, String  username, String userId);

    CompletableFuture<Void> saveVote(VoteDTO vote, String  username, String userId);


    CompletableFuture<Void> saveAgreeVote(VoteDTO vote, String  username, String userId);

    List<VoteDTO> getVotePageFeed(int page, int size);
    List<VoteDTO> getVoteUserAll();

    Map<String, Object> getVoteTicketAll(String  id);


    List<VoteDTO> getVoteFeedDetail(String feedUID);

    Map<String,Object> getVoteAggregation(Long id);

    VoteDTO getVoteContent();

    VoteDTO getVoteDetail(String feedUID);

    List<VoteDTO> getVoteAll();
}
