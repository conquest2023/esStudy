package es.board.repository;


import es.board.controller.model.req.FeedUpdate;
import es.board.controller.model.req.NoticeDTO;
import es.board.controller.model.req.TopWriter;
import es.board.controller.model.res.FeedCreateResponse;
import es.board.repository.document.Board;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface FeedDAO {

    Map<String, Object> findUserMapageLikeAndFeedCount(String userId);
    List<Board> findRecommendFeed();
    List<TopWriter> findTopWriters();
    void saveNoticeFeed(NoticeDTO dto, Long id);


    List<Board> findUserRangeTimeFeed(String userId);
    String saveFeed(String index, FeedCreateResponse dto) ;

    List<Board> saveBulkFeed(List<Board> pages) ;

    FeedCreateResponse indexSaveFeed(FeedCreateResponse dto) ;


    List<Board> findRangeTimeFeed(LocalDateTime startDate, LocalDateTime endTime) ;

    List<Board> findAllFeed();

    List<Board> findLikeCount();


    List<Board> findPagingFeed(int page, int size) ;

    List<Board> findRecentFeed();

    List<Board> findCategoryAndContent(String category);
    Board modifyFeed(String id, FeedUpdate eq);
    Board findFeedDetail(String id);
    List<Board> findSearchBoard(String text);

     double findSumLikeByPageOne(int page, int size);
      Board findPopularFeedOne();
    Integer findUserLikeCount(String userId);

    Map<String, Object> findMypageUserList(String userId);

    double findUserFeedCount(String userId);
    void saveViewCounts(String id) ;
     List<Board> findMonthPopularFeed();

    int findAllViewCount();
    List<Board> findPopularFeedDESC(int page,int size);
    double findTotalPage(int page, int size)  ;

    List<Board> findMostViewFeed(int page, int size) ;
    double findSumFeed();

    void deleteFeedOne(String id);
//   Board getFeed(String id);


}
