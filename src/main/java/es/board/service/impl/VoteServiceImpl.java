package es.board.service.impl;


import es.board.controller.model.mapper.FeedMapper;
import es.board.controller.model.req.VoteResponse;
import es.board.repository.entity.Vote;
import es.board.repository.entity.entityrepository.VoteRepository;
import es.board.service.VoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class VoteServiceImpl implements VoteService {

    private final VoteRepository voteRepository;

    private final AsyncService asyncService;


    private final FeedMapper feedMapper;

    @Override
    public CompletableFuture<Void> saveVote(VoteResponse vote,String  username, String userId) {
        return CompletableFuture.supplyAsync(() -> {
            Vote savedVoteId = getSavedVoteId(vote,username, userId);
            asyncService.saveVoteAsync(feedMapper.voteToDTO(savedVoteId,username,userId), savedVoteId.getId());
            return null;
        });
    }

    @Override
    public void cancelVote() {

    }

    @Override
    public VoteResponse getVoteContent() {
           return feedMapper.voteToDTOMain(voteRepository.findLatestVote());
    }

    @Override
    public List<VoteResponse> getVoteAll(){
        return  feedMapper.voteToDTOMainList(voteRepository.findAll());
    }

    private Vote getSavedVoteId(VoteResponse vote,String username, String userId) {
        return voteRepository.save(feedMapper.voteToEntity(vote,username, userId));
    }
}
