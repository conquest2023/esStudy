package es.board.repository;


import es.board.controller.model.req.FeedUpdate;
import es.board.controller.model.res.FeedCreateResponse;
import es.board.repository.document.Board;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FeedDAO {

   // Map<String, Object> search(String index)  throws IOException, ElasticsearchException;

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

    List<Board> findUserBoardList(String userId);

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
