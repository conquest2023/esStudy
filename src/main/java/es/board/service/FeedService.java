package es.board.service;

import es.board.controller.model.dto.feed.PostDTO;
import es.board.controller.model.dto.feed.TopWriter;
import es.board.infrastructure.es.document.Feed;
import es.board.infrastructure.entity.feed.PostImage;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public interface FeedService {

    List<PostDTO.Request> findCommentDESC(int page, int size);


    List<String> findPagingFeedIds(int page, int size);

    List<PostDTO.Request> findReplyDESC(int page, int size);
    List<PostDTO.Request> findViewDESC(int page, int size);

    PostImage saveFeedImages(String imageUrl);
    Map<String, Object>  getNoticeFeed(int page, int size);
    Integer getPointAll(String userId);
    Map<String, Double> getDayAggregation();

    Map<String, Object> getDataFeed(int page, int size,String category);;
    double getUserFeedCount(String userId);

    List<PostDTO.Request>  findWeekBestFeed(int page, int size);

    Map<String, Object> getUserMapageLikeAndFeedCount(String userId);
    List<PostDTO.Request> getRecommendFeed();
    List<TopWriter>  getTopWriters();
//    List<PostDTO> getUserRangeTimeFeed(String userId);
    Map<String, Object> getFeedUserList(String userId,int page,int size);
//    Integer getUserLikeCount(String userId);
    CompletableFuture<PostDTO.Request> saveFeed(PostDTO.Request feedSaveDTO);

    List<PostDTO.Request> getCategoryFeed(String category);

    List<PostDTO.Request> getRangeTimeFeed(LocalDateTime startDate, LocalDateTime endTime);

    List<PostDTO.Request> getMonthPopularFeed();

    List<PostDTO.Request>  getPopularFeedDESC(int page, int size);
//    double getSumLikeByPageOne(int page, int size);

//    int getViewCountAll();

     void plusLike(String id,String userId);

    void cancelLike(String id,String userId);

    void saveViewCountFeed(String id);

    PostDTO.Request getPopularFeedOne();

     boolean isAlreadyLiked(String userId,String id);

    List<PostDTO.Request> getRecentFeed();

    List<PostDTO.Request> createBulkFeed(List<PostDTO.Request> comments);

//    String createFeed(String indexName, FeedCreateResponse dto);

//    List<PostDTO> getFeed();

    List<PostDTO.Request> getLikeCount();

    List<Feed> getSearchBoard(String text);

    List<PostDTO.Request> getPagingFeed(int page, int size);

    void deleteFeed(String id,String userId);

    List<PostDTO.Request> getMostViewFeed(int page, int size);

    List<String> getfeedUIDList(List<PostDTO.Request> requests);

    Map<String, Object> getFetchTotalFeedStats();

    PostDTO.Update updateFeed(String id, PostDTO.Update update);

    PostDTO.Request getFeedDetail(String id);
}
