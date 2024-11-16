package es.board.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import es.board.model.req.FeedRequest;
import es.board.model.res.FeedCreateResponse;
import es.board.repository.dao.FeedDAO;
import es.board.repository.document.Board;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.client.RestClient;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
    public String saveFeed(FeedCreateResponse feedSaveDTO) throws IOException {

        return feedDAO.indexSaveFeed(feedSaveDTO);

    }

    @Override
    public List<FeedRequest> getRecentFeed() throws IOException {
        FeedRequest reqFeedDTO=new FeedRequest();
        return reqFeedDTO.dtoToFeed(feedDAO.findRecentFeed());
    }

    @Override
    public List<FeedCreateResponse> createBulkFeed(List<FeedCreateResponse> feeds) throws IOException {

        feedDAO.saveBulkFeed(BulkToEntity(feeds));

        return feeds;
    }

    @Override
    public String createFeed(String indexName, FeedCreateResponse dto) throws IOException {
        return feedDAO.saveFeed(indexName,dto);
    }

    @Override
    public List<FeedRequest> getFeedList() throws IOException {
        FeedRequest feedDTO=new FeedRequest();
        return feedDTO.dtoToFeed(feedDAO.findAllFeed());
    }

    @Override
    public List<FeedRequest> getLikeCountList() throws IOException {
        FeedRequest req=new FeedRequest();
        return req.dtoToFeed(feedDAO.findLikeCount());
    }

    @Override
    public List<FeedRequest> getPagingFeedList(int num) throws IOException {
        FeedRequest req=new FeedRequest();
        return  req.dtoToFeed(feedDAO.findPagingFeed(num));
    }



    public List<Board> BulkToEntity(List<FeedCreateResponse> res) {
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


//    @Override
//    public List<FeedRequest> getFeedAll() {
//        FeedRequest reqFeedDTO=new FeedRequest();
//
//        List<FeedRequest> req=  reqFeedDTO.entityToDTO(boardDAO.getFeedAll());
//
//        return  req;
//    }


}

