package es.board.service;

import es.board.model.req.ReqCommentDTO;
import es.board.model.req.ReqFeedDTO;
import es.board.model.req.UpdateFeedDTO;
import es.board.model.res.CommentSaveDTO;
import es.board.model.res.FeedSaveDTO;
import es.board.repository.document.Board;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public interface FeedService {


   String searchBoard(String indexName) throws IOException;


   String SaveFeed(FeedSaveDTO feedSaveDTO) throws IOException;


    List<ReqFeedDTO> searchTimeDESC() throws IOException;

    List<FeedSaveDTO> BulkBoardTo(List<FeedSaveDTO> comments) throws IOException;

    String indexFeed(String indexName,FeedSaveDTO dto) throws IOException;

    List<ReqFeedDTO> searchAll() throws IOException;

    List<ReqFeedDTO> LikeBoardDESCTo() throws IOException;


    List<ReqFeedDTO> PagingSearchBoard(int num) throws IOException;

    void saveDTO(FeedSaveDTO dto);

    void save(Board board);



    List<ReqFeedDTO> getFeedAll();

//    UpdateFeedDTO update(String id, UpdateFeedDTO updateFeedDTO);


//    Board getFeed(String id);

    void delete(String id);




}
