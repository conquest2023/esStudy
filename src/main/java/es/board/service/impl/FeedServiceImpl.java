package es.board.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import es.board.model.req.FeedRequest;
import es.board.model.req.FeedUpdate;
import es.board.model.res.FeedCreateResponse;
import es.board.repository.FeedDAO;
import es.board.repository.LikeDAO;
import es.board.repository.document.Board;
import es.board.service.FeedService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RestClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Builder
@Slf4j
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final RestClient client;

    private final ElasticsearchClient esClient;

    private final FeedDAO feedDAO;

    private  final LikeDAO likeDAO;


    @Override
    public FeedCreateResponse saveFeed(FeedCreateResponse feedSaveDTO) {
        RandomFeedUID(feedSaveDTO);
        return feedDAO.indexSaveFeed(feedSaveDTO);
    }

    @Override
    public List<String> getfeedUIDList(int page, int size) {
        return extractFeedUID(page, size);
    }

    @Override
    public List<FeedRequest> getCategoryFeed(String category) {
        FeedRequest feedRequest = new FeedRequest();
        return feedRequest.BoardListToDTO(feedDAO.findCategoryAndContent(category));
    }

    @Override
    public List<FeedRequest> getMonthPopularFeed() {
        FeedRequest feedRequest = new FeedRequest();
        return feedRequest.BoardListToDTO(feedDAO.findMonthPopularFeed());
    }

    @Override
    public List<FeedRequest> getPopularFeedDESC(int page, int size) {
        FeedRequest feedRequest=new FeedRequest();
        return  feedRequest.BoardListToDTO(feedDAO.findPopularFeedDESC(page,size));
    }

    @Override
    public List<FeedRequest> getRangeTimeFeed(LocalDateTime startDate, LocalDateTime endTime) {
        FeedRequest reqFeedDTO = new FeedRequest();
        return reqFeedDTO.BoardListToDTO(feedDAO.findRangeTimeFeed(startDate, endTime));
    }

    @Override
    public double getSumLikeByPageOne(int page, int size) {
        return feedDAO.findSumLikeByPageOne(page, size);
    }

    @Override
    public int getViewCountAll() {
        return feedDAO.findAllViewCount();
    }

    @Override
    public FeedRequest getPopularFeedOne() {
        FeedRequest feedRequest = new FeedRequest();
        return feedRequest.BoardToDTO(feedDAO.findPopularFeedOne());
    }

    @Override
    public List<FeedRequest> getRecentFeed() {
        FeedRequest reqFeedDTO = new FeedRequest();
        return reqFeedDTO.BoardListToDTO(feedDAO.findRecentFeed());
    }

    @Override
    public List<FeedCreateResponse> createBulkFeed(List<FeedCreateResponse> feeds) {
        feedDAO.saveBulkFeed(bulkToEntity(feeds));
        return feeds;
    }

    @Override
    public List<Board> getSearchBoard(String text) {
        return feedDAO.findSearchBoard(text);
    }

    @Override
    public String createFeed(String indexName, FeedCreateResponse dto) {
        return feedDAO.saveFeed(indexName, dto);
    }

    @Override
    public List<FeedRequest> getFeed() {
        FeedRequest feedDTO = new FeedRequest();
        return feedDTO.BoardListToDTO(feedDAO.findAllFeed());
    }

    @Override
    public double getTotalPage(int page, int size) {
        return feedDAO.findTotalPage(page, size);
    }

    @Override
    public List<FeedRequest> getLikeCount() {
        FeedRequest req = new FeedRequest();
        return req.BoardListToDTO(feedDAO.findLikeCount());
    }

    @Override
    public List<FeedRequest> getPagingFeed(int page, int size) {
        FeedRequest req = new FeedRequest();
        return req.BoardListToDTO(feedDAO.findPagingFeed(page, size));
    }

    @Override
    public List<FeedRequest> getMostViewFeed(int page, int size) {
        FeedRequest req = new FeedRequest();
        return req.BoardListToDTO(feedDAO.findMostViewFeed(page, size));
    }

    @Override
    public Double getTotalFeed() {
        return feedDAO.findSumFeed();
    }

    @Override
    @Transactional
    public void deleteFeed(String id) {
        feedDAO.deleteFeedOne(id);
    }

    @Override
    public FeedRequest getFeedId(String id) {
        FeedRequest request = new FeedRequest();
        return request.BoardToDTO(feedDAO.findIdOne(id));
    }

    @Override
    public void saveViewCountFeed(String id) {
        Board view = feedDAO.findIdOne(id);
        feedDAO.saveViewCounts(id, view);
    }
    @Override
    public FeedUpdate updateFeed(String id, FeedUpdate update) {
        feedDAO.modifyFeed(id, update);
        return update;
    }
    @Override
    public  void plusLike(String id) {
        likeDAO.saveLike(id);
    }

    private static void RandomFeedUID(FeedCreateResponse feedSaveDTO) {
        String feedUID = UUID.randomUUID().toString();
        feedSaveDTO.setFeedUID(feedUID);
    }
    public List<Board> bulkToEntity(List<FeedCreateResponse> res) {
        List<Board> boards = new ArrayList<>();
        for (FeedCreateResponse dto : res) {
            Board feed = Board.builder()
                    .feedUID(dto.getFeedUID())
                    .title(dto.getTitle())
                    .description(dto.getDescription())
                    .likeCount(dto.getLikeCount())
                    .createdAt(LocalDateTime.now())
                    .build();
            boards.add(feed);
        }
        return boards;
    }
    private List<String> extractFeedUID(int page, int size) {
        List<String> feedUIDs = feedDAO.findPagingFeed(page, size).stream()
                .map(Board::getFeedUID)
                .collect(Collectors.toList());
        return feedUIDs;
    }

}



