package es.board.service;

import es.board.model.req.ReqFeedDTO;
import es.board.model.req.UpdateFeedDTO;
import es.board.model.res.FeedSaveDTO;
import es.board.repository.domain.BoardDAO;
import es.board.repository.entity.Board;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Builder
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {


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
    public List<ReqFeedDTO> getFeedAll() {
        ReqFeedDTO reqFeedDTO=new ReqFeedDTO();

        List<ReqFeedDTO> req=  reqFeedDTO.entityToDTO(boardDAO.getFeedAll());

            return  req;
    }

    @Override
    public UpdateFeedDTO update(String id, UpdateFeedDTO updateFeedDTO) {

     Board board=boardDAO.getFeed(id);

     boardDAO.updateDTO(updateFeed(id,board,updateFeedDTO));

     return  updateFeedDTO;
    }
    @Override
    public Board getFeed(String id) {

        // String boardId=  boardDAO.getFeed(id);
          return   boardDAO.getFeed(id);

    }
    @Override
    public void delete(String id) {

        boardDAO.deleteFeed(id);
    }




    public  Board updateFeed(String id,Board board ,UpdateFeedDTO update){
        String username = update.getUsername() != null ? update.getUsername() : board.getUsername();
        String title = update.getTitle() != null ? update.getTitle() : board.getTitle();
        String description = update.getDescription() != null ? update.getDescription() : board.getDescription();

        return Board.builder()
                .id(id)
                .username(username)
                .title(title)
                .description(description)
                .updatedAt(LocalDate.now())
                .build();
    }
}
