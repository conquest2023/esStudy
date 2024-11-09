package es.board.repository.domain;


import es.board.model.req.ReqFeedDTO;
import es.board.model.res.FeedSaveDTO;
import es.board.repository.entity.Board;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BoardDAO {


   Board saveDTO(Board dto);

  Board save(Board board);



  Board updateDTO(Board updateDTO);



  List<Board> getFeedAll();

   Board getFeed(String id);

   void deleteFeed(String id);

}
