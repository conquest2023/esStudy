package es.board.service.impl;


import es.board.controller.model.mapper.FeedMapper;
import es.board.controller.model.req.VoteResponse;
import es.board.repository.VoteDAO;
import es.board.repository.entity.UserVote;
import es.board.repository.entity.Vote;
import es.board.repository.entity.entityrepository.VoteRepository;
import es.board.repository.entity.entityrepository.VoteUserRepository;
import es.board.service.VoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class VoteServiceImpl implements VoteService {

    private final VoteRepository voteRepository;

    private final AsyncService asyncService;

    private  final VoteDAO voteDAO;

    private  final VoteUserRepository voteUserRepository;


    private final FeedMapper feedMapper;

    @Override
    public CompletableFuture<Void> saveFeedVote(VoteResponse vote, String  username, String userId) {
        return CompletableFuture.supplyAsync(() -> {
            Vote savedVoteId = getSavedVoteId(vote,username, userId);
            asyncService.saveVoteAsync(feedMapper.voteToDocument(vote,savedVoteId.getFeedId(),username,userId), savedVoteId.getId());
            return null;
        });
    }
    @Override
    public CompletableFuture<Void> saveVote(VoteResponse vote,String  username, String userId) {
        return CompletableFuture.supplyAsync(() -> {
            Vote savedVoteId = getSavedVoteId(vote,username, userId);
            asyncService.saveVoteAsync(feedMapper.voteToDTO(savedVoteId,username,userId), savedVoteId.getId());
            return null;
        });
    }
    @Override
    public CompletableFuture<Void> saveAgreeVote(VoteResponse vote,String  username, String userId) {
        return CompletableFuture.supplyAsync(() -> {
            UserVote savedUserVoteId = getSavedAggregationVoteId(vote,username, userId);
            asyncService.saveAggregationVoteAsync(feedMapper.userVoteToDTO(savedUserVoteId,username,userId), savedUserVoteId.getId());
            return null;
        });
    }
    @Override
    public void oppositeVote() {

    }

    @Override
    public List<VoteResponse> getVotePageFeed(int page, int size) {
        List<VoteResponse> votes= feedMapper.voteDocumentToDTOList(voteDAO.findVotePageFeed(page,size));
        if (votes == null || votes.isEmpty()) {
            votes = new ArrayList<>();
        }
        return votes;
    }

    @Override
    public List<VoteResponse> getVoteUserAll() {
        return feedMapper.voteDocumentToDTOList(voteDAO.findFeedVoteAll());
    }

    @Override
    public List<VoteResponse> getVoteFeedDetail(String feedUID) {
        return null;
    }

    @Override
    public Map<String, Object> getVoteAggregation(Long id) {
        return  voteDAO.getVoteStatistics(String.valueOf(id));
    }

    @Override
    public VoteResponse getVoteContent() {

        return feedMapper.EntityToVoteDTO(voteRepository.findLatestVote());
    }

    @Override
    public VoteResponse getVoteDetail(String feedUID) {
        return feedMapper.DocumentToVoteDTO(voteDAO.findVoteFeedDetail(feedUID));
    }

    @Override
    public List<VoteResponse> getVoteAll(){

        return  feedMapper.voteToDTOMainList(voteRepository.findAll());
    }

    private Vote getSavedVoteId(VoteResponse vote,String username, String userId) {
        return voteRepository.save(feedMapper.voteToEntity(vote,username, userId));
    }

    private UserVote getSavedAggregationVoteId(VoteResponse vote, String username, String userId) {
        return voteUserRepository.save(feedMapper.userVoteToEntity(vote,username, userId));
    }
}
