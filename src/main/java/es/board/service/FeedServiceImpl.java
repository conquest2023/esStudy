package es.board.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import es.board.model.req.FeedRequest;
import es.board.model.req.FeedUpdate;
import es.board.model.res.FeedCreateResponse;
import es.board.repository.dao.FeedDAO;
import es.board.repository.document.Board;
import es.board.repository.document.Comment;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.client.RestClient;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Struct;
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
    public  FeedCreateResponse saveFeed(FeedCreateResponse feedSaveDTO) throws IOException {

     return  feedDAO.indexSaveFeed(feedSaveDTO);

    }

    @Override
    public List<FeedRequest> getCategoryFeed(String category) throws IOException {
        FeedRequest feedRequest=new FeedRequest();
        return  feedRequest.BoardListToDTO(feedDAO.findCategoryAndContent(category));
    }


    @Override
    public List<FeedRequest> getMonthPopularFeed() throws IOException {
        FeedRequest feedRequest=new FeedRequest();
        return  feedRequest.BoardListToDTO(feedDAO.findMonthPopularFeed());
    }

    @Override
    public List<FeedRequest> getRangeTimeFeed(LocalDateTime startDate,LocalDateTime endTime) throws IOException {
        FeedRequest reqFeedDTO=new FeedRequest();
        return   reqFeedDTO.BoardListToDTO(feedDAO.findRangeTimeFeed(startDate,endTime));
    }

    @Override
    public double getSumLikeByPageOne(int page, int size) throws IOException {

       return  (feedDAO.findSumLikeByPageOne(page,size));
    }

    @Override
    public FeedRequest getPopularFeedOne() throws IOException {
        FeedRequest feedRequest=new FeedRequest();
        return  feedRequest.BoardToDTO(feedDAO.findPopularFeedOne());
    }

    @Override
    public List<FeedRequest> getRecentFeed() throws IOException {
        FeedRequest reqFeedDTO=new FeedRequest();
        return reqFeedDTO.BoardListToDTO(feedDAO.findRecentFeed());
    }

    @Override
    public List<FeedCreateResponse> createBulkFeed(List<FeedCreateResponse> feeds) throws IOException {

        feedDAO.saveBulkFeed(bulkToEntity(feeds));

        return feeds;
    }

    @Override
    public List<Board> getSearchBoard(String text) throws IOException {
        return feedDAO.findSearchBoard(text);
    }

    @Override
    public String createFeed(String indexName, FeedCreateResponse dto) throws IOException {
        return feedDAO.saveFeed(indexName,dto);
    }

    @Override
    public List<FeedRequest> getFeed() throws IOException {
        FeedRequest feedDTO=new FeedRequest();
        return feedDTO.BoardListToDTO(feedDAO.findAllFeed());
    }

    @Override
    public double getTotalPage(int page, int size) throws IOException {
        return  feedDAO.findTotalPage(page,size);
    }

    @Override
    public List<FeedRequest> getLikeCount() throws IOException {
        FeedRequest req=new FeedRequest();
        return req.BoardListToDTO(feedDAO.findLikeCount());
    }

    @Override
    public List<FeedRequest> getPagingFeed(int page, int size) throws IOException {
        FeedRequest req=new FeedRequest();
        return  req.BoardListToDTO(feedDAO.findPagingFeed(page, size));
    }

    @Override
    public FeedRequest getFeedId(String id) throws IOException {
        FeedRequest request=new FeedRequest();

        return request.BoardToDTO(feedDAO.findIdOne(id));
    }


    public List<Board> bulkToEntity(List<FeedCreateResponse> res) {
        List<Board> boards = new ArrayList<>();
        for (FeedCreateResponse dto : res) {
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
    @Override
    public FeedUpdate updateFeed(String id, FeedUpdate update) throws Exception {
        feedDAO.modifyFeed(id,update);
        return  update;
    }



//    @Override
//    public List<FeedRequest> getFeedAll() {
//        FeedRequest reqFeedDTO=new FeedRequest();
//
//        List<FeedRequest> req=  reqFeedDTO.entityToDTO(boardDAO.getFeedAll());
//
//        return  req;
//    }


}

