package es.board.service;

import es.board.model.req.FeedRequest;
import es.board.model.req.FeedUpdate;
import es.board.model.res.FeedCreateResponse;
import es.board.repository.document.Board;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public interface FeedService {


   //String searchBoard(String indexName) throws IOException;


    FeedCreateResponse saveFeed(FeedCreateResponse feedSaveDTO) throws IOException;

    List<FeedRequest> getCategoryFeed(String category) throws  IOException;


    List<FeedRequest> getRangeTimeFeed(LocalDateTime startDate,LocalDateTime endTime) throws IOException;


    List<FeedRequest> getMonthPopularFeed() throws IOException;

    double getSumLikeByPageOne(int page, int size) throws IOException;

    void  saveViewCountFeed(String  id) throws IOException;
    FeedRequest getPopularFeedOne() throws IOException;

    List<FeedRequest> getRecentFeed() throws IOException;

    List<FeedCreateResponse> createBulkFeed(List<FeedCreateResponse> comments) throws IOException;

    String createFeed(String indexName, FeedCreateResponse dto) throws IOException;

    List<FeedRequest> getFeed() throws IOException;


    double getTotalPage(int page,int size) throws  IOException;

    List<FeedRequest> getLikeCount() throws IOException;

    List<Board> getSearchBoard(String text) throws IOException;
    List<FeedRequest> getPagingFeed(int page, int size) throws IOException;

    void deleteFeed(String id) throws IOException;

    Double getTotalFeed() throws IOException;
    public FeedUpdate updateFeed(String id, FeedUpdate update) throws Exception;

   FeedRequest getFeedId(String id) throws IOException;

}
