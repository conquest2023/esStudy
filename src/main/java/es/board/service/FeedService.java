package es.board.service;

import es.board.controller.model.req.FeedRequest;
import es.board.controller.model.req.FeedUpdate;
import es.board.controller.model.res.FeedCreateResponse;
import es.board.repository.document.Board;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface FeedService {

    double getUserFeedCount(String userId);


    FeedCreateResponse saveFeed(FeedCreateResponse feedSaveDTO);

    List<FeedRequest> getCategoryFeed(String category);

    List<FeedRequest> getRangeTimeFeed(LocalDateTime startDate, LocalDateTime endTime);

    List<FeedRequest> getMonthPopularFeed();

    List<FeedRequest>  getPopularFeedDESC(int page,int size);
    double getSumLikeByPageOne(int page, int size);

    int getViewCountAll();

     void plusLike(String id);
    void saveViewCountFeed(String id);

    FeedRequest getPopularFeedOne();

    List<FeedRequest> getRecentFeed();

    List<FeedCreateResponse> createBulkFeed(List<FeedCreateResponse> comments);

    String createFeed(String indexName, FeedCreateResponse dto);

    List<FeedRequest> getFeed();

    double getTotalPage(int page, int size);

    List<FeedRequest> getLikeCount();

    List<Board> getSearchBoard(String text);

    List<FeedRequest> getPagingFeed(int page, int size);

    void deleteFeed(String id,int userId);

    List<FeedRequest> getMostViewFeed(int page, int size);

    List<String> getfeedUIDList(int page, int size);

    Double getTotalFeed();

    FeedUpdate updateFeed(String id, FeedUpdate update);

    FeedRequest getFeedId(String id);
}
