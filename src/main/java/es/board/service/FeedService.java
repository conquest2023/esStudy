package es.board.service;

import es.board.controller.model.req.FeedRequest;
import es.board.controller.model.req.FeedUpdate;
import es.board.controller.model.req.TopWriter;
import es.board.controller.model.res.FeedCreateResponse;
import es.board.repository.document.Board;
import es.board.repository.entity.FeedImage;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public interface FeedService {

    List<FeedRequest> findCommentDESC(int page, int size);


    List<String> findPagingFeedIds(int page, int size);

    List<FeedRequest> findReplyDESC(int page, int size);
    List<FeedRequest> findViewDESC(int page, int size);

    FeedImage saveFeedImages(String imageUrl);
    Map<String, Object>  getNoticeFeed(int page, int size);
    Integer getPointAll(String userId);
    Map<String, Double> getDayAggregation();

    Map<String, Object> getDataFeed(int page, int size,String category);;
    double getUserFeedCount(String userId);

    List<FeedRequest>  findWeekBestFeed(int page, int size);

    Map<String, Object> getUserMapageLikeAndFeedCount(String userId);
    List<FeedRequest> getRecommendFeed();
    List<TopWriter>  getTopWriters();
//    List<FeedRequest> getUserRangeTimeFeed(String userId);
    Map<String, Object> getFeedUserList(String userId,int page,int size);
//    Integer getUserLikeCount(String userId);
    CompletableFuture<FeedCreateResponse> saveFeed(FeedCreateResponse feedSaveDTO);

    List<FeedRequest> getCategoryFeed(String category);

    List<FeedRequest> getRangeTimeFeed(LocalDateTime startDate, LocalDateTime endTime);

    List<FeedRequest> getMonthPopularFeed();

    List<FeedRequest>  getPopularFeedDESC(int page,int size);
//    double getSumLikeByPageOne(int page, int size);

//    int getViewCountAll();

     void plusLike(String id,String userId);

    void cancelLike(String id,String userId);

    void saveViewCountFeed(String id);

    FeedRequest getPopularFeedOne();

     boolean isAlreadyLiked(String userId,String id);

    List<FeedRequest> getRecentFeed();

    List<FeedCreateResponse> createBulkFeed(List<FeedCreateResponse> comments);

//    String createFeed(String indexName, FeedCreateResponse dto);

//    List<FeedRequest> getFeed();

    List<FeedRequest> getLikeCount();

    List<Board> getSearchBoard(String text);

    List<FeedRequest> getPagingFeed(int page, int size);

    void deleteFeed(String id,String userId);

    List<FeedRequest> getMostViewFeed(int page, int size);

    List<String> getfeedUIDList(List<FeedRequest> requests);

    Map<String, Object> getFetchTotalFeedStats();

    FeedUpdate updateFeed(String id, FeedUpdate update);

    FeedRequest getFeedDetail(String id);
}
