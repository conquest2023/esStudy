package es.board.repository.dao;


import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import es.board.model.res.FeedSaveDTO;
import es.board.repository.document.Board;
import es.board.repository.document.Comment;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Repository
public interface FeedDAO {

    Map<String, Object> search(String index)  throws IOException, ElasticsearchException;

    String indexDocument(String index, FeedSaveDTO dto) throws IOException;

    List<Board> BulkIndex(List<Board> pages) throws IOException;

    List<Board> SearchTextBring(String text) throws IOException;

    List<Board> LikeDESCBring() throws IOException;

    List<Board> PagingSearchBring(int num) throws IOException;

    List<Board> SearchBoardTimeDESC() throws IOException;

   Board saveDTO(Board dto);

  Board save(Board board);

  Board updateDTO(Board updateDTO);


//   Board getFeed(String id);

   void deleteFeed(String id);

}
