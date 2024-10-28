package es.board.service;

import es.board.model.req.ReqFeedDTO;
import es.board.model.res.FeedSaveDTO;
import es.board.repository.BoardDAO;
import es.board.repository.entity.Board;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CrudServiceImpl implements crudService {

    private final BoardDAO boardDAO;


    @Override
    public void saveDTO(FeedSaveDTO dto) {
        Board board = new Board();
        boardDAO.saveDTO(board.BoardToEntity(dto));
    }

    @Override
    public void save(Board board) {
        boardDAO.save(board);
    }



    @Override
    public List<Board> getFeed(String username) {

      return   boardDAO.getFeed(username);

    }

    @Override
    public void delete(String id) {

    }
}
