package es.board.repository;


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


   List<Board> getFeed(String username);


}
