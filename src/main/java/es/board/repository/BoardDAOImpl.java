package es.board.repository;

import es.board.model.req.ReqFeedDTO;
import es.board.model.res.FeedSaveDTO;
import es.board.repository.entity.Board;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
@RequiredArgsConstructor
@Slf4j
public class BoardDAOImpl implements BoardDAO {


    private final BoardRepository boardRepository;


    @Override
    public Board saveDTO(Board dto) {
        log.info("save dto: {}", dto);
     return   boardRepository.save(dto);
    }
    @Override
    public Board save(Board board) {
     return    boardRepository.save(board);
    }

    @Override
    public Board updateDTO(Board updateDTO) {
        return boardRepository.save(updateDTO);
    }


    @Override
    public List<Board> getFeedAll() {
      return   boardRepository.findAllBy();
    }

    @Override
    public Board getFeed(String id) {

        return boardRepository.findAllById(id);
    }

    @Override
    public void deleteFeed(String id) {
        boardRepository.deleteById(id);
    }
}