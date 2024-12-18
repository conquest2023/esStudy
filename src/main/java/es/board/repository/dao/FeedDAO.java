package es.board.repository.dao;


import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import es.board.model.req.FeedUpdate;
import es.board.model.res.FeedCreateResponse;
import es.board.model.res.ViewCountResponse;
import es.board.repository.document.Board;
import es.board.repository.document.Comment;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FeedDAO {

   // Map<String, Object> search(String index)  throws IOException, ElasticsearchException;

    String saveFeed(String index, FeedCreateResponse dto) ;

    List<Board> saveBulkFeed(List<Board> pages) ;

    FeedCreateResponse indexSaveFeed(FeedCreateResponse dto) ;


    List<Board> findRangeTimeFeed(LocalDateTime startDate, LocalDateTime endTime) ;


    List<Board> findAllFeed()  ;

    List<Board> findLikeCount() ;

    List<Board> findPagingFeed(int page, int size) ;

    List<Board> findRecentFeed() ;

    List<Board> findCategoryAndContent(String category) ;
    Board modifyFeed(String id, FeedUpdate eq) ;
    Board findIdOne(String id)  ;
    List<Board> findSearchBoard(String text);

     double findSumLikeByPageOne(int page, int size);
      Board findPopularFeedOne();

    void saveViewCounts(String id, Board view) ;
     List<Board> findMonthPopularFeed();

    List<Board> findPopularFeedDESC(int page,int size);
    double findTotalPage(int page, int size)  ;

    List<Board> findMostViewFeed(int page, int size) ;
    double findSumFeed();

    void deleteFeedOne(String id);
//   Board getFeed(String id);


}
