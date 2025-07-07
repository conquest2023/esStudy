package es.board.repository;


import es.board.controller.model.req.FeedDTO;
import es.board.controller.model.req.FeedUpdate;
import es.board.controller.model.req.NoticeRequest;
import es.board.controller.model.req.TopWriter;
import es.board.repository.document.Board;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface FeedDAO {

    List<String> findPagingIds(int page,int size);

    List<Board> findReplyCount(int page, int size);

    List<Board> findCountComment(int page, int size);
    List<Board> findViewDESC(int page, int size);
    Map<String, Object> findNoticeFeed(int page, int size);

    Map<String, Object> findDataFeed(int page, int size,String category);
    Map<String, Double> findDayAggregation();
    List<Board> findWeekBestFeed(int page, int size);
    Map<String, Object> findUserMyPageLikeAndFeedCount(String userId);
    List<Board> findRecommendFeed();
    List<TopWriter> findTopWriters();
    void saveNoticeFeed(NoticeRequest dto, Long id);
    List<Board> findUserRangeTimeFeed(String userId);
    String saveFeed(String index, FeedDTO.Response dto) ;

    List<Board> saveBulkFeed(List<Board> pages) ;

    Board indexSaveFeed(Board board,int postId) ;


    List<Board> findRangeTimeFeed(LocalDateTime startDate, LocalDateTime endTime) ;


    List<Board> findLikeCount();


    List<Board> findPagingMainFeed(int page, int size) ;

    List<Board> findRecentFeed();

    List<Board> findCategoryAndContent(String category);
    Board modifyFeed(String id, FeedUpdate eq);
    Board findFeedDetail(String id);
    List<Board> findSearchBoard(String text);

     double findSumLikeByPageOne(int page, int size);
      Board findPopularFeedOne();
    Integer findUserLikeCount(String userId);

    Map<String, Object> findMypageUserList(String userId,int page,int size);

    double findUserFeedCount(String userId);
    void saveViewCounts(String id) ;
     List<Board> findMonthPopularFeed();

    int findAllViewCount();
    List<Board> findPopularFeedDESC(int page,int size);

    List<Board> findMostViewFeed(int page, int size) ;
    Map<String, Object> fetchTotalFeedStats();

    void deleteFeed(String id);
}
