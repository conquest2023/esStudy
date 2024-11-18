package es.board.repository.dao;


import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import es.board.model.res.FeedCreateResponse;
import es.board.repository.document.Board;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

@Repository
public interface FeedDAO {

   // Map<String, Object> search(String index)  throws IOException, ElasticsearchException;

    String saveFeed(String index, FeedCreateResponse dto) throws IOException;

    List<Board> saveBulkFeed(List<Board> pages) throws IOException;

    FeedCreateResponse indexSaveFeed(FeedCreateResponse dto) throws IOException;


    List<Board> findRangeTimeFeed(String time) throws  IOException;


    List<Board> findAllFeed()  throws IOException, ElasticsearchException;

    List<Board> findContent(String text) throws IOException;

    List<Board> findLikeCount() throws IOException;

    List<Board> findPagingFeed(int num) throws IOException;

    List<Board> findRecentFeed() throws IOException;





//   Board getFeed(String id);


}
