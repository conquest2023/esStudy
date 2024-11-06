package es.board.repository;

import es.board.repository.domain.BoardDAO;
import es.board.repository.entity.Board;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BoardDAOImpl implements BoardDAO {

    private final  BoardRepository boardRepository;

    @Override
    public Board saveDTO(Board dto) {
       return boardRepository.save(dto);
    }

    @Override
    public Board save(Board board) {
        return  boardRepository.save(board);
    }

    @Override
    public Board updateDTO(Board updateDTO) {
        return updateDTO(updateDTO);
    }

    @Override
    public List<Board> getFeedAll() {
        return boardRepository.findAllBy();
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
