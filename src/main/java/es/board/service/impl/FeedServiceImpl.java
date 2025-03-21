package es.board.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.board.config.jwt.JwtTokenProvider;
import es.board.config.s3.S3Uploader;
import es.board.controller.model.mapper.FeedMapper;
import es.board.controller.model.req.FeedRequest;
import es.board.controller.model.req.FeedUpdate;
import es.board.controller.model.req.TopWriter;
import es.board.controller.model.res.FeedCreateResponse;
import es.board.repository.FeedDAO;
import es.board.repository.LikeDAO;
import es.board.repository.document.Board;
import es.board.repository.entity.PointHistory;
import es.board.repository.entity.Post;
import es.board.repository.entity.entityrepository.LikeRepository;
import es.board.repository.entity.entityrepository.PointHistoryRepository;
import es.board.repository.entity.entityrepository.PostRepository;
import es.board.service.FeedService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import jakarta.persistence.PersistenceContext;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Builder
@Slf4j
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    @PersistenceContext
    private final EntityManager entityManager;


    private final FeedMapper feedMapper;

    private  final PointHistoryRepository pointHistoryRepository;

    private  final RedisTemplate redisTemplate;

    private final S3Uploader s3Uploader;

    private final JwtTokenProvider jwtTokenProvider;

    private final PostRepository postRepository;

    private final FeedDAO feedDAO;

    private final AsyncService asyncService;

    private final LikeDAO likeDAO;

    private final LikeRepository likeRepository;

    private  final ObjectMapper objectMapper;

    private  static  final String TOP5_USER_KEY= "TOP5_USER_feed_key";

    private  static  final String RECOMMEND_KEY= "Recommend_feed_key";

    private  final StringRedisTemplate stringRedisTemplate;

    @Override
    public double getUserFeedCount(String userId) {

        return feedDAO.findUserFeedCount(userId);
    }

    @Override
    public   List<FeedRequest> findWeekBestFeed(int page, int size) {
        return feedMapper.BoardListToDTO(feedDAO.findWeekBestFeed(page,size));
    }

    @Override
    public Map<String, Object> getUserMapageLikeAndFeedCount(String userId) {
        return  feedDAO.findUserMyPageLikeAndFeedCount(userId);
    }

    @Override
    public List<TopWriter> getTopWriters() {
        ValueOperations<String, List<TopWriter>> valueOps = redisTemplate.opsForValue();
        List<TopWriter> cachedData = valueOps.get(TOP5_USER_KEY);

        if (cachedData != null) {
            return cachedData;
        }
        List<TopWriter> topWriters = feedDAO.findTopWriters();

        valueOps.set(TOP5_USER_KEY, topWriters, 6, TimeUnit.HOURS);
        return topWriters;

    }

    @Override
    public List<FeedRequest> getUserRangeTimeFeed(String userId) {
        return feedMapper.BoardListToDTO(feedDAO.findUserRangeTimeFeed(userId));
    }

    @Override
    public Integer getUserLikeCount(String userId) {
        return feedDAO.findUserLikeCount(userId);
    }


    @Override
    public CompletableFuture<FeedCreateResponse> saveFeed(FeedCreateResponse feedSaveDTO) {
        return CompletableFuture.supplyAsync(() -> {
            checkValueFeed(feedSaveDTO);
            Map<String,Object> Ids = savePost(feedSaveDTO);
            grantFeedPoint(feedSaveDTO.getUserId());
            asyncService.savePostAsync(feedMapper.BoardToDocument(feedSaveDTO, (int) Ids.get("postId"), (String)Ids.get("feedUID")), (int) Ids.get("postId"));
            return feedSaveDTO;
        });
    }

    @Override
    public List<FeedRequest> getRecommendFeed() {

        ValueOperations<String, List<FeedRequest>> valueOps = redisTemplate.opsForValue();

        List<FeedRequest> cachedData = valueOps.get(RECOMMEND_KEY);

        if (cachedData != null) {
            return cachedData;
        }
        List<FeedRequest> feedRequests = feedMapper.BoardListToDTO(feedDAO.findRecommendFeed());

        valueOps.set(RECOMMEND_KEY, feedRequests, 6, TimeUnit.HOURS);
        return feedRequests;
    }

    @Override
    public List<String> getfeedUIDList(List<FeedRequest> requests) {
        return extractFeedUID(requests);
    }

    @Override
    public List<FeedRequest> getCategoryFeed(String category) {
        return feedMapper.BoardListToDTO(feedDAO.findCategoryAndContent(category));
    }

    @Override
    public List<FeedRequest> getMonthPopularFeed() {

        return feedMapper.BoardListToDTO(feedDAO.findMonthPopularFeed());
    }

    @Override
    public List<FeedRequest> getPopularFeedDESC(int page, int size) {
        return feedMapper.BoardListToDTO(feedDAO.findPopularFeedDESC(page, size));
    }

    @Override
    public List<FeedRequest> getRangeTimeFeed(LocalDateTime startDate, LocalDateTime endTime) {
        return feedMapper.BoardListToDTO(feedDAO.findRangeTimeFeed(startDate, endTime));
    }

    @Override
    public double getSumLikeByPageOne(int page, int size) {
        return feedDAO.findSumLikeByPageOne(page, size);
    }

    @Override
    public int getViewCountAll() {
        return feedDAO.findAllViewCount();
    }

    @Override
    public FeedRequest getPopularFeedOne() {

        return feedMapper.BoardToDTO(feedDAO.findPopularFeedOne());
    }

    @Override
    public List<FeedRequest> getRecentFeed() {

        return feedMapper.BoardListToDTO(feedDAO.findRecentFeed());
    }

    @Override
    public List<FeedCreateResponse> createBulkFeed(List<FeedCreateResponse> feeds) {
        feedDAO.saveBulkFeed(bulkToEntity(feeds));
        return feeds;
    }

    @Override
    public List<Board> getSearchBoard(String text) {
        return feedDAO.findSearchBoard(text);
    }

    @Override
    public String createFeed(String indexName, FeedCreateResponse dto) {

        return feedDAO.saveFeed(indexName, dto);
    }

    @Override
    public List<FeedRequest> getFeed() {

        return feedMapper.BoardListToDTO(feedDAO.findAllFeed());
    }

    @Override
    public double getTotalPage(int page, int size) {
        return feedDAO.findTotalPage(page, size);
    }

    @Override
    public Map<String, Double> getDayAggregation(){

        return  feedDAO.findDayAggregation();
    }

    @Override
    public List<FeedRequest> findDataFeed(int page, int size) {
        return  feedMapper.BoardListToDTO(feedDAO.findDataFeed(page,size));
    }

    @Override
    public List<FeedRequest> getLikeCount() {

        return feedMapper.BoardListToDTO(feedDAO.findLikeCount());
    }

    @Override
    public List<FeedRequest> getPagingFeed(int page, int size) {

        return feedMapper.BoardListToDTO(feedDAO.findPagingMainFeed(page, size));
    }

    @Override
    public List<FeedRequest> getMostViewFeed(int page, int size) {

        return feedMapper.BoardListToDTO(feedDAO.findMostViewFeed(page, size));
    }

    @Override
    public Map<String, Object> getFetchTotalFeedStats() {
        return feedDAO.fetchTotalFeedStats();
    }

    @Override
    public void deleteFeed(String id, String userId) {
        feedDAO.deleteFeedOne(id);
        asyncService.deletePostAsync(id, userId);
    }

    @Override
    public Map<String, Object> getFeedUserList(String userId,int page,int size) {

        return feedDAO.findMypageUserList(userId,page,size);
    }

    @Override
    public void saveViewCountFeed(String id) {

        feedDAO.saveViewCounts(id);
    }

    @Override
    public FeedUpdate updateFeed(String id, FeedUpdate update) {
        feedDAO.modifyFeed(id, update);
        return update;
    }

    @Override
    public void plusLike(String id, String userId) {
        if (isAlreadyLiked(id, userId)) {
            throw new IllegalStateException("이미 좋아요를 누른 상태입니다.");
        }
        likeRepository.save(feedMapper.LikeToEntity(id, userId));
        asyncService.postLike(userId, id);
    }

    @Override
    @Transactional
    public void cancelLike(String userId, String feedId) {

        if (isAlreadyLiked(userId, feedId)) {
            likeRepository.deleteByUserIdAndFeedUID(userId, feedId);
            asyncService.cancelLike(userId, feedId);
        } else {
            throw new IllegalStateException("좋아요를 누른 상태가 아닙니다.");
        }
    }


    @Override
    public FeedRequest getFeedDetail(String id) {

        return feedMapper.BoardToDTO(feedDAO.findFeedDetail(id));
    }

    @Override
    public boolean isAlreadyLiked(String userId, String id) {
        return likeRepository.existsByUserIdAndFeedUID(userId, id);
    }

    private void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("유저 이름을 입력해주세요.");
        }
        if (!username.matches("^[a-zA-Z0-9가-힣]+$")) {
            throw new IllegalArgumentException("유저 이름에는 특수문자를 사용할 수 없습니다.");
        }
    }


    public List<Board> bulkToEntity(List<FeedCreateResponse> res) {
        List<Board> boards = new ArrayList<>();
        for (FeedCreateResponse dto : res) {
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

    private Map<String,Object>  savePost(FeedCreateResponse feedSaveDTO) {
        Map<String,Object> Ids=new HashMap<>();
        Post savedPost = postRepository.save(feedMapper.PostToEntity(feedSaveDTO));
        Ids.put("postId",savedPost.getId());
        Ids.put("feedUID",savedPost.getFeedUID());
        return Ids;
    }

    private List<String> extractFeedUID(List<FeedRequest> requests) {
        List<String> feedUIDs = requests.stream()
                .map(FeedRequest::getFeedUID)
                .collect(Collectors.toList());
        return feedUIDs;
    }

    public void esSettingId(FeedCreateResponse feedSaveDTO, int id) {
    }
    private static void checkValueFeed(FeedCreateResponse feedSaveDTO) {
        log.info(feedSaveDTO.toString());

        if (isEmpty(feedSaveDTO.getTitle()) || isEmpty(feedSaveDTO.getDescription()) || isEmpty(feedSaveDTO.getCategory())) {
            throw new IllegalArgumentException("제목, 본문, 카테고리는 필수 입력값입니다.");
        }
    }
    private static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    public void grantFeedPoint(String userId) {
        String today = LocalDate.now().toString();
        String key = "feed:point:" + userId + ":" + today;

        Long currentCount = stringRedisTemplate.opsForValue().increment(key);

        if (currentCount == 1) {
            stringRedisTemplate.expire(key, Duration.ofDays(1));
        }
        if (currentCount > 5) {
            log.info("{}님은 오늘 피드 작성 포인트 한도(5개)를 초과했습니다.", userId);
            return;
        }
        createPointHistory(userId);
        log.info("피드 작성 포인트 지급 완료! 현재 작성 횟수: {}", currentCount);
    }
        public void createPointHistory(String userId) {
           PointHistory history = PointHistory.builder()
                    .userId(userId)
                    .pointChange(3)
                    .reason("피드")
                    .createdAt(LocalDateTime.now())
                    .build();
            pointHistoryRepository.save(history);
        }
}



