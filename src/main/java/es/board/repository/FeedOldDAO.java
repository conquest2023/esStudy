package es.board.repository;


import es.board.controller.model.dto.feed.PostDTO;
import es.board.controller.model.dto.feed.NoticeDTO;
import es.board.controller.model.dto.feed.TopWriter;
import es.board.infrastructure.es.document.Feed;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface FeedOldDAO {

    List<String> findPagingIds(int page,int size);

    List<Feed> findReplyCount(int page, int size);

    List<Feed> findCountComment(int page, int size);
    List<Feed> findViewDESC(int page, int size);
    Map<String, Object> findNoticeFeed(int page, int size);

    Map<String, Object> findDataFeed(int page, int size,String category);
    Map<String, Double> findDayAggregation();
    List<Feed> findWeekBestFeed(int page, int size);
    Map<String, Object> findUserMyPageLikeAndFeedCount(String userId);
    List<Feed> findRecommendFeed();
    List<TopWriter> findTopWriters();
    void saveNoticeFeed(NoticeDTO.Request dto, Long id);
    List<Feed> findUserRangeTimeFeed(String userId);
    String saveFeed(String index, PostDTO.Request dto) ;

    List<Feed> saveBulkFeed(List<Feed> pages) ;

    Feed indexSaveFeed(Feed board, int postId) ;


    List<Feed> findRangeTimeFeed(LocalDateTime startDate, LocalDateTime endTime) ;


    List<Feed> findLikeCount();


    List<Feed> findPagingMainFeed(int page, int size) ;

    List<Feed> findRecentFeed();

    List<Feed> findCategoryAndContent(String category);
    Feed modifyFeed(String id, PostDTO.Update eq);
    Feed findFeedDetail(String id);
    List<Feed> findSearchBoard(String text);

     double findSumLikeByPageOne(int page, int size);
      Feed findPopularFeedOne();
    Integer findUserLikeCount(String userId);

    Map<String, Object> findMypageUserList(String userId,int page,int size);

    double findUserFeedCount(String userId);
    void saveViewCounts(String id) ;
     List<Feed> findMonthPopularFeed();

    int findAllViewCount();
    List<Feed> findPopularFeedDESC(int page, int size);

    List<Feed> findMostViewFeed(int page, int size) ;
    Map<String, Object> fetchTotalFeedStats();

    void deleteFeed(String id);
}
