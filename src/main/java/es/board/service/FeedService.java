package es.board.service;

import es.board.controller.model.dto.feed.FeedDTO;
import es.board.controller.model.dto.feed.TopWriter;
import es.board.repository.document.Board;
import es.board.repository.entity.FeedImage;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public interface FeedService {

    List<FeedDTO.Request> findCommentDESC(int page, int size);


    List<String> findPagingFeedIds(int page, int size);

    List<FeedDTO.Request> findReplyDESC(int page, int size);
    List<FeedDTO.Request> findViewDESC(int page, int size);

    FeedImage saveFeedImages(String imageUrl);
    Map<String, Object>  getNoticeFeed(int page, int size);
    Integer getPointAll(String userId);
    Map<String, Double> getDayAggregation();

    Map<String, Object> getDataFeed(int page, int size,String category);;
    double getUserFeedCount(String userId);

    List<FeedDTO.Request>  findWeekBestFeed(int page, int size);

    Map<String, Object> getUserMapageLikeAndFeedCount(String userId);
    List<FeedDTO.Request> getRecommendFeed();
    List<TopWriter>  getTopWriters();
//    List<FeedDTO> getUserRangeTimeFeed(String userId);
    Map<String, Object> getFeedUserList(String userId,int page,int size);
//    Integer getUserLikeCount(String userId);
    CompletableFuture<FeedDTO.Response> saveFeed(FeedDTO.Response feedSaveDTO);

    List<FeedDTO.Request> getCategoryFeed(String category);

    List<FeedDTO.Request> getRangeTimeFeed(LocalDateTime startDate, LocalDateTime endTime);

    List<FeedDTO.Request> getMonthPopularFeed();

    List<FeedDTO.Request>  getPopularFeedDESC(int page, int size);
//    double getSumLikeByPageOne(int page, int size);

//    int getViewCountAll();

     void plusLike(String id,String userId);

    void cancelLike(String id,String userId);

    void saveViewCountFeed(String id);

    FeedDTO.Request getPopularFeedOne();

     boolean isAlreadyLiked(String userId,String id);

    List<FeedDTO.Request> getRecentFeed();

    List<FeedDTO.Response> createBulkFeed(List<FeedDTO.Response> comments);

//    String createFeed(String indexName, FeedCreateResponse dto);

//    List<FeedDTO> getFeed();

    List<FeedDTO.Request> getLikeCount();

    List<Board> getSearchBoard(String text);

    List<FeedDTO.Request> getPagingFeed(int page, int size);

    void deleteFeed(String id,String userId);

    List<FeedDTO.Request> getMostViewFeed(int page, int size);

    List<String> getfeedUIDList(List<FeedDTO.Request> requests);

    Map<String, Object> getFetchTotalFeedStats();

    FeedDTO.Update updateFeed(String id, FeedDTO.Update update);

    FeedDTO.Request getFeedDetail(String id);
}
