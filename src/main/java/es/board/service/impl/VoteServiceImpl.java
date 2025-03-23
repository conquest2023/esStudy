package es.board.service.impl;


import es.board.controller.model.mapper.FeedMapper;
import es.board.controller.model.req.VoteDTO;
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
    public CompletableFuture<Void> createdFeedVote(VoteDTO vote, String  username, String userId) {
        return CompletableFuture.supplyAsync(() -> {
            Vote savedVoteId = getSavedVoteId(vote,username, userId);
            asyncService.saveVoteAsync(feedMapper.changeVoteDtoToDocument(vote,savedVoteId.getFeedId(),username,userId,savedVoteId.getId()), savedVoteId.getId());
            return null;
        });
    }
    @Override
    public CompletableFuture<Void> saveVote(VoteDTO vote, String  username, String userId) {
        return CompletableFuture.supplyAsync(() -> {
            Vote savedVoteId = getSavedVoteId(vote,username, userId);
            asyncService.saveVoteAsync(feedMapper.fromVoteDto(savedVoteId,username,userId), savedVoteId.getId());
            return null;
        });
    }
    @Override
    public CompletableFuture<Void> saveAgreeVote(VoteDTO vote, String  username, String userId) {
        return CompletableFuture.supplyAsync(() -> {
            UserVote savedUserVoteId = getSavedAggregationVoteId(vote,username, userId);
            asyncService.saveAggregationVoteAsync(feedMapper.fromUserVoteDTO(savedUserVoteId,username,userId), savedUserVoteId.getId());
            return null;
        });
    }

    @Override
    public void deleteVoteFeed(String id, String userId) {
         voteDAO.deleteVoteFeed(id);
    }
    @Override
    public CompletableFuture<Void> saveFeedTicket(VoteDTO vote, String  username, String userId) {
        return CompletableFuture.supplyAsync(() -> {
//            UserVote savedVoteId = getSavedAggregationVoteId(vote,username, userId);
            asyncService.saveVoteTicketAsync(feedMapper.changeVoteDTOToAnalytics(vote,username,userId));
            return null;
        });
    }
    @Override
    public List<VoteDTO> getVotePageFeed(int page, int size) {
        List<VoteDTO> votes= feedMapper.fromVoteDTOList(voteDAO.findVotePageFeed(page,size));
        if (votes == null || votes.isEmpty()) {
            votes = new ArrayList<>();
        }
        return votes;
    }
    @Override
    public List<VoteDTO> getVoteUserAll() {
        return feedMapper.fromVoteDTOList(voteDAO.findFeedVoteAll());
    }

    @Override
    public Map<String, Object> getVoteTicketAll(String  id) {
        return  voteDAO.getVoteFeedStatistics(id);
    }

    @Override
    public List<VoteDTO> getVoteFeedDetail(String feedUID) {
        return null;
    }

    @Override
    public Map<String, Object> getVoteAggregation(Long id) {
        return  voteDAO.getVoteStatistics(String.valueOf(id));
    }

    @Override
    public VoteDTO getVoteContent() {

        return feedMapper.fromVoteDTO(voteRepository.findLatestVote());
    }

    @Override
    public VoteDTO getVoteDetail(String feedUID) {
        return feedMapper.fromDocumentVoteDTO(voteDAO.findVoteFeedDetail(feedUID));
    }

    @Override
    public List<VoteDTO> getVoteAll(){

        return  feedMapper.voteToDTOMainList(voteRepository.findAll());
    }

    private Vote getSavedVoteId(VoteDTO vote, String username, String userId) {
        return voteRepository.save(feedMapper.toVoteEntity(vote,username, userId));
    }

    private UserVote getSavedAggregationVoteId(VoteDTO vote, String username, String userId) {
        return voteUserRepository.save(feedMapper.userVoteToEntity(vote,username, userId));
    }
}
