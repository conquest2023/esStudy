package es.board.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import es.board.model.req.ReqCommentDTO;
import es.board.model.req.ReqFeedDTO;
import es.board.model.req.UpdateFeedDTO;
import es.board.model.res.CommentSaveDTO;
import es.board.model.res.FeedSaveDTO;
import es.board.repository.dao.FeedDAO;
import es.board.repository.document.Board;
import es.board.repository.document.Comment;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Builder
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final RestClient client;

    private final ElasticsearchClient esClient;

    private final FeedDAO feedDAO;


    @Override
    public String searchBoard(String indexName) throws IOException {
        Request request = new Request("GET", "/" + indexName + "/_search");
        request.addParameter("pretty", "true");

        Response response = client.performRequest(request);

        return new String(response.getEntity().getContent().readAllBytes());
    }

    @Override
    public List<ReqFeedDTO> searchTimeDESC() throws IOException {
        ReqFeedDTO reqFeedDTO=new ReqFeedDTO();
        return reqFeedDTO.DTOFromEntity(feedDAO.SearchBoardTimeDESC());
    }

    @Override
    public List<FeedSaveDTO> BulkBoardTo(List<FeedSaveDTO> feeds) throws IOException {

        feedDAO.BulkIndex(BulkToEntity(feeds));

        return feeds;
    }

    @Override
    public String indexFeed(String indexName, FeedSaveDTO dto) throws IOException {
        return feedDAO.indexDocument(indexName,dto);
    }

    @Override
    public List<ReqFeedDTO> LikeBoardDESCTo() throws IOException {
        ReqFeedDTO req=new ReqFeedDTO();
        return req.DTOFromEntity(feedDAO.LikeDESCBring());
    }

    @Override
    public List<ReqFeedDTO> PagingSearchBoard(int num) throws IOException {
        ReqFeedDTO req=new ReqFeedDTO();
        return  req.DTOFromEntity(feedDAO.PagingSearchBring(num));
    }




    @Override
    public void saveDTO(FeedSaveDTO dto) {
        Board board = new Board();
        feedDAO.saveDTO(board.BoardToEntity(dto));
    }
    @Override
    public void save(Board board) {

        feedDAO.save(board);
    }

    @Override
    public List<ReqFeedDTO> getFeedAll() {
        return null;
    }


//    @Override
//    public UpdateFeedDTO update(String id, UpdateFeedDTO updateFeedDTO) {
//
//     Board board= feedDAO.getFeed(id);
//
//     feedDAO.updateDTO(updateFeed(id,board,updateFeedDTO));
//
//     return  updateFeedDTO;
//    }
//    @Override
//    public Board getFeed(String id) {
//
//        // String boardId=  boardDAO.getFeed(id);
//          return   feedDAO.getFeed(id);
//
//    }
    @Override
    public void delete(String id) {

        feedDAO.deleteFeed(id);
    }




    public  Board updateFeed(String id,Board board ,UpdateFeedDTO update){
        String username = update.getUsername() != null ? update.getUsername() : board.getUsername();
        String title = update.getTitle() != null ? update.getTitle() : board.getTitle();
        String description = update.getDescription() != null ? update.getDescription() : board.getDescription();

        return Board.builder()
                .feedUID(id)
                .username(username)
                .title(title)
                .description(description)
                .likeCount(update.getLikeCount())
                .updatedAt(LocalDate.now())
                .build();
    }


//    @Override
//    public List<ReqFeedDTO> getFeedAll() {
//        ReqFeedDTO reqFeedDTO=new ReqFeedDTO();
//
//        List<ReqFeedDTO> req=  reqFeedDTO.entityToDTO(boardDAO.getFeedAll());
//
//        return  req;
//    }

    public List<Board> BulkToEntity(List<FeedSaveDTO> res) {
        List<Board> boards = new ArrayList<>();
        for (FeedSaveDTO dto : res) {
            // 빌더 패턴을 사용해 Comment 객체 생성
            Board feed = Board.builder()
                    .feedUID(dto.getFeedUID())
                    .title(dto.getTitle())
                    .description(dto.getDescription())
                    .likeCount(dto.getLikeCount())
                    .createdAt(LocalDateTime.now())
                    .build();
            boards.add(feed);
        }
        return boards;
    }
}

