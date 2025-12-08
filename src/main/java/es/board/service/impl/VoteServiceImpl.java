//package es.board.service.impl;
//
//
//import es.board.controller.model.mapper.document.FeedDocumentMapper;
//import es.board.controller.model.dto.feed.VoteDTO;
//import es.board.repository.VoteDAO;
//import es.board.repository.document.VoteDocument;
//import es.board.repository.entity.UserVote;
//import es.board.repository.entity.Vote;
//import es.board.repository.entity.repository.VoteRepository;
//import es.board.repository.entity.repository.VoteUserRepository;
//import es.board.service.VoteService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.CompletableFuture;
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class VoteServiceImpl implements VoteService {
//
//    private final VoteRepository voteRepository;
//
//    private final AsyncService asyncService;
//
//    private  final VoteDAO voteDAO;
//
//    private  final VoteUserRepository voteUserRepository;
//
//    private final FeedDocumentMapper feedMapper;
//
//    @Override
//    public CompletableFuture<Void> createdFeedVote(VoteDTO.Request vote, String  username, String userId) {
//        return CompletableFuture.supplyAsync(() -> {
//            Vote savedVoteId = getSavedVoteId(vote,username, userId);
//            asyncService.saveVoteAsync(feedMapper.changeVoteDtoToDocument(vote,savedVoteId.getFeedId(),username,userId,savedVoteId.getId()), savedVoteId.getId());
//            return null;
//        });
//    }
//    @Override
//    public CompletableFuture<Void> saveVote(VoteDTO.Request vote, String  username, String userId) {
//        return CompletableFuture.supplyAsync(() -> {
//            Vote savedVoteId = getSavedVoteId(vote,username, userId);
//            asyncService.saveVoteAsync(feedMapper.fromVoteDto(savedVoteId,username,userId), savedVoteId.getId());
//            return null;
//        });
//    }
//    @Override
//    public CompletableFuture<Void> saveAgreeVote(VoteDTO.Request vote, String  username, String userId) {
//        return CompletableFuture.supplyAsync(() -> {
//            UserVote savedUserVoteId = getSavedAggregationVoteId(vote,username, userId);
//            asyncService.saveAggregationVoteAsync(feedMapper.fromUserVoteDTO(savedUserVoteId,username,userId), savedUserVoteId.getId());
//            return null;
//        });
//    }
//
//    @Override
//    public void deleteVoteFeed(String id, String userId) {
//         voteDAO.deleteVoteFeed(id);
//    }
//    @Override
//    public CompletableFuture<Void> saveFeedTicket(VoteDTO.Request vote, String  username, String userId) {
//        return CompletableFuture.supplyAsync(() -> {
////            UserVote savedVoteId = getSavedAggregationVoteId(vote,username, userId);
//            asyncService.saveVoteTicketAsync(feedMapper.changeVoteDTOToAnalytics(vote,username,userId));
//            return null;
//        });
//    }
//    @Override
//    public List<VoteDTO.Request> getVotePageFeed(int page, int size) {
//        List<VoteDTO.Request> votes= feedMapper.fromVoteDTOList(voteDAO.findVotePageFeed(page,size));
//        if (votes == null || votes.isEmpty()) {
//            votes = new ArrayList<>();
//        }
//        return votes;
//    }
//
//    @Override
//    public Map<String, Object>  getVotePageMainFeed(int page, int size) {
//        Map<String,Object> voteResult= voteDAO.findFeedPagingVote(page,size);
//        List<VoteDTO.Request> voteRequests = feedMapper.fromVoteDTOList((List<VoteDocument>) voteResult.get("data"));
//        return Map.of(
//                "totalPage", voteResult.get("totalPage"),
//                "data", voteRequests
//        );
//    }
//    @Override
//    public List<VoteDTO.Request> getVoteUserAll() {
//        return feedMapper.fromVoteDTOList(voteDAO.findFeedVoteAll());
//    }
//
//    @Override
//    public Map<String, Object> getVoteTicketAll(String  id) {
//        return  voteDAO.getVoteFeedStatistics(id);
//    }
//
//    @Override
//    public List<VoteDTO.Request> getVoteFeedDetail(String feedUID) {
//        return null;
//    }
//
//    @Override
//    public Map<String, Object> getVoteAggregation(Long id) {
//        return  voteDAO.getVoteStatistics(String.valueOf(id));
//    }
//
//    @Override
//    public VoteDTO.Request getVoteContent() {
//
//        return feedMapper.fromVoteDTO(voteRepository.findLatestVote());
//    }
//
//    @Override
//    public VoteDTO.Request getVoteDetail(String feedUID) {
//        return feedMapper.fromDocumentVoteDTO(voteDAO.findVoteFeedDetail(feedUID));
//    }
//
//    @Override
//    public List<VoteDTO.Request> getVoteAll(){
//
//        return  feedMapper.voteToDTOMainList(voteRepository.findAll());
//    }
//
//    private Vote getSavedVoteId(VoteDTO.Request vote, String username, String userId) {
//        return voteRepository.save(feedMapper.toVoteEntity(vote,username, userId));
//    }
//
//    private UserVote getSavedAggregationVoteId(VoteDTO.Request vote, String username, String userId) {
//        return voteUserRepository.save(feedMapper.userVoteToEntity(vote,username, userId));
//    }
//}
