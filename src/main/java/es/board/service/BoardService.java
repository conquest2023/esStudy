package es.board.service;

import es.board.model.req.ReqFeedDTO;
import es.board.model.req.UpdateFeedDTO;
import es.board.model.res.FeedSaveDTO;
import es.board.repository.entity.Board;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public interface BoardService {


    void saveDTO(FeedSaveDTO dto);

    void save(Board board);


    List<ReqFeedDTO> getFeedAll();

    UpdateFeedDTO update(String id, UpdateFeedDTO updateFeedDTO);


    Board getFeed(String id);

    void delete(String id);




}
