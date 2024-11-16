package es.board.service;

import es.board.model.req.FeedRequest;
import es.board.model.res.FeedCreateResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public interface FeedService {


   //String searchBoard(String indexName) throws IOException;


   String saveFeed(FeedCreateResponse feedSaveDTO) throws IOException;


    List<FeedRequest> getRecentFeed() throws IOException;

    List<FeedCreateResponse> createBulkFeed(List<FeedCreateResponse> comments) throws IOException;

    String createFeed(String indexName, FeedCreateResponse dto) throws IOException;

    List<FeedRequest> getFeedList() throws IOException;

    List<FeedRequest> getLikeCountList() throws IOException;


    List<FeedRequest> getPagingFeedList(int num) throws IOException;







}
