package es.board.service;

import es.board.controller.model.dto.feed.VoteDTO;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface VoteService {


    Map<String, Object>  getVotePageMainFeed(int page, int size);
    void deleteVoteFeed(String id, String userId);
    CompletableFuture<Void> saveFeedTicket(VoteDTO.Request vote, String  username, String userId);
    CompletableFuture<Void> createdFeedVote(VoteDTO.Request vote, String  username, String userId);

    CompletableFuture<Void> saveVote(VoteDTO.Request vote, String  username, String userId);


    CompletableFuture<Void> saveAgreeVote(VoteDTO.Request vote, String  username, String userId);

    List<VoteDTO.Request> getVotePageFeed(int page, int size);
    List<VoteDTO.Request> getVoteUserAll();

    Map<String, Object> getVoteTicketAll(String  id);


    List<VoteDTO.Request> getVoteFeedDetail(String feedUID);

    Map<String,Object> getVoteAggregation(Long id);

    VoteDTO.Request getVoteContent();

    VoteDTO.Request getVoteDetail(String feedUID);

    List<VoteDTO.Request> getVoteAll();
}
