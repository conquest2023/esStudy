package es.board.service.impl;

import es.board.config.s3.S3Uploader;
import es.board.controller.model.mapper.FeedMapper;
import es.board.controller.model.req.FeedRequest;
import es.board.controller.model.req.FeedUpdate;
import es.board.controller.model.res.FeedCreateResponse;
import es.board.repository.FeedDAO;
import es.board.repository.LikeDAO;
import es.board.repository.document.Board;
import es.board.repository.entity.Post;
import es.board.service.FeedService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Builder
@Slf4j
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {


    private final FeedMapper feedMapper;

    private  final S3Uploader s3Uploader;

    private final FeedDAO feedDAO;

    private final AsyncService asyncService;


    private  final LikeDAO likeDAO;


    @Override
    public double getUserFeedCount(String userId) {

        return  feedDAO.findUserLikeCount(userId);
    }

    @Override
    public List<FeedRequest> getUserRangeTimeFeed(String userId) {
        return  feedMapper.BoardListToDTO(feedDAO.findUserRangeTimeFeed(userId));
    }

    @Override
    public Integer getUserLikeCount(String userId) {
        return  feedDAO.findUserLikeCount(userId);
    }



    @Override
    public FeedCreateResponse saveFeed(FeedCreateResponse feedSaveDTO) {
        Post post=new Post();
        post.esSettingId(feedSaveDTO);
        asyncService.savePostAsync(feedSaveDTO);
        return feedDAO.indexSaveFeed(feedSaveDTO);
    }

    @Override
    public List<String> getfeedUIDList(int page, int size) {
        return extractFeedUID(page, size);
    }

    @Override
    public List<FeedRequest> getCategoryFeed(String category) {
        return feedMapper.BoardListToDTO(feedDAO.findCategoryAndContent(category));
    }

    @Override
    public List<FeedRequest> getMonthPopularFeed() {

        return feedMapper.BoardListToDTO(feedDAO.findMonthPopularFeed());
    }

    @Override
    public List<FeedRequest> getPopularFeedDESC(int page, int size) {
        return  feedMapper.BoardListToDTO(feedDAO.findPopularFeedDESC(page,size));
    }

    @Override
    public List<FeedRequest> getRangeTimeFeed(LocalDateTime startDate, LocalDateTime endTime) {
        return feedMapper.BoardListToDTO(feedDAO.findRangeTimeFeed(startDate, endTime));
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

        return feedMapper.BoardToDTO(feedDAO.findPopularFeedOne());
    }

    @Override
    public List<FeedRequest> getRecentFeed() {

        return feedMapper.BoardListToDTO(feedDAO.findRecentFeed());
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

        return feedMapper.BoardListToDTO(feedDAO.findAllFeed());
    }

    @Override
    public double getTotalPage(int page, int size) {
        return feedDAO.findTotalPage(page, size);
    }

    @Override
    public List<FeedRequest> getLikeCount() {

        return feedMapper.BoardListToDTO(feedDAO.findLikeCount());
    }

    @Override
    public List<FeedRequest> getPagingFeed(int page, int size) {

        return feedMapper.BoardListToDTO(feedDAO.findPagingFeed(page, size));
    }

    @Override
    public List<FeedRequest> getMostViewFeed(int page, int size) {

        return feedMapper.BoardListToDTO(feedDAO.findMostViewFeed(page, size));
    }

    @Override
    public Double getTotalFeed() {
        return feedDAO.findSumFeed();
    }

    @Override
    public void deleteFeed(String id,String userId) {
        feedDAO.deleteFeedOne(id);
        asyncService.deletePostAsync(userId);
    }
    @Override
    public List<FeedRequest> getFeedUserList(String userId){

        return feedMapper.BoardListToDTO(feedDAO.findUserBoardList(userId));
    }
    @Override
    public FeedRequest getFeedId(String id) {
        return feedMapper.BoardToDTO(feedDAO.findIdOne(id));
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



