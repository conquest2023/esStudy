package es.board.service;

import es.board.model.req.ReqFeedDTO;
import es.board.model.res.FeedSaveDTO;
import es.board.repository.entity.Board;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public interface crudService {


    void saveDTO(FeedSaveDTO dto);

    void save(Board board);


    List<Board> getFeed(String username);

    void delete(String id);
}
