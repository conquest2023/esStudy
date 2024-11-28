package es.board.repository.dao;


import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import es.board.model.req.FeedUpdate;
import es.board.model.res.FeedCreateResponse;
import es.board.repository.document.Board;
import es.board.repository.document.Comment;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FeedDAO {

   // Map<String, Object> search(String index)  throws IOException, ElasticsearchException;

    String saveFeed(String index, FeedCreateResponse dto) throws IOException;

    List<Board> saveBulkFeed(List<Board> pages) throws IOException;

    FeedCreateResponse indexSaveFeed(FeedCreateResponse dto) throws IOException;


    List<Board> findRangeTimeFeed(LocalDateTime startDate, LocalDateTime endTime) throws  IOException;


    List<Board> findAllFeed()  throws IOException, ElasticsearchException;

    List<Board> findLikeCount() throws IOException;

    List<Board> findPagingFeed(int page, int size) throws IOException;

    List<Board> findRecentFeed() throws IOException;

    List<Board> findCategoryAndContent(String category) throws IOException;
    Board modifyFeed(String id, FeedUpdate eq) throws Exception;
    Board findIdOne(String id) throws IOException ;
    List<Board> findSearchBoard(String text) throws IOException;

     double findSumLikeByPageOne(int page, int size) throws IOException;
      Board findPopularFeedOne() throws IOException;


    public List<Board> findMonthPopularFeed() throws IOException;

     double findTotalPage(int page, int size) throws IOException ;


//   Board getFeed(String id);


}
