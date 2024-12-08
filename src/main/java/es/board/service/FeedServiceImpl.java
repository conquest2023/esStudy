package es.board.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import es.board.model.req.FeedRequest;
import es.board.model.req.FeedUpdate;
import es.board.model.res.FeedCreateResponse;
import es.board.model.res.ViewCountResponse;
import es.board.repository.dao.FeedDAO;
import es.board.repository.document.Board;
import es.board.repository.document.Comment;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.elasticsearch.client.RestClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Struct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Builder
@Slf4j
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final RestClient client;

    private final ElasticsearchClient esClient;

    private final FeedDAO feedDAO;




    @Override
    public  FeedCreateResponse saveFeed(FeedCreateResponse feedSaveDTO) throws IOException {
        RandomFeedUID(feedSaveDTO);
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
    public  Double getTotalFeed() throws IOException {
        return  feedDAO.findSumFeed();
    }


    @Override
    @Transactional
    public void deleteFeed(String id) throws IOException {

        feedDAO.deleteFeedOne(id);
    }

    @Override
    public FeedRequest getFeedId(String id) throws IOException {
        FeedRequest request=new FeedRequest();

        return request.BoardToDTO(feedDAO.findIdOne(id));
    }

    @Override
    public  void  saveViewCountFeed(String  id) throws IOException {

       Board view= feedDAO.findIdOne(id);
       feedDAO.saveViewCounts(id,view);

//       view.plusCount();

    }



//    public FeedCreateResponse convertFileToBase64(FeedCreateResponse feedCreateResponse) throws IOException {
//        byte[] fileBytes = feedCreateResponse.getAttachFile().getBytes();
//        String  attachFileBase64 = Base64.encodeBase64String(fileBytes);
//
//        // 이미지 파일들을 Base64로 변환
//        List<String> base64ImageFiles = new ArrayList<>();
//        List<MultipartFile> imageFiles = feedCreateResponse.getImageFiles();
//        if (imageFiles != null) {
//            for (MultipartFile imageFile : imageFiles) {
//                byte[] imageBytes = imageFile.getBytes();
//                base64ImageFiles.add(Base64.encodeBase64String(imageBytes));  // 이미지 파일을 Base64로 인코딩
//            }
//        }
//        feedCreateResponse.ConvertToBase64(attachFileBase64,base64ImageFiles);
//        return feedCreateResponse;
//    }

    public List<Board> bulkToEntity(List<FeedCreateResponse> res) {
        List<Board> boards = new ArrayList<>();
        for (FeedCreateResponse dto : res) {
            Board feed = Board.builder()
                    .feedUID(dto.getFeedUID())
                    .title(dto.getTitle())
//                    .image(dto.getImage())
//                  .attachFile(dto.getAttachFileBase64())
//                   .imageFiles(dto.getBase64ImageFiles())
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

    private static void RandomFeedUID(FeedCreateResponse feedSaveDTO) {
        String feedUID= UUID.randomUUID().toString();
        feedSaveDTO.setFeedUID(feedUID);
    }
    }



