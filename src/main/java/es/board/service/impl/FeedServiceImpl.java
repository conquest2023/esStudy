package es.board.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.board.config.jwt.JwtTokenProvider;
//import es.board.config.s3.S3Uploader;
import es.board.controller.model.dto.feed.PostDTO;
import es.board.controller.model.mapper.FeedMapper;
import es.board.controller.model.dto.feed.TopWriter;
import es.board.repository.FeedDAO;
import es.board.repository.LikeDAO;
import es.board.repository.document.Board;
import es.board.infrastructure.entity.feed.PostImage;
import es.board.repository.entity.PointHistoryEntity;
import es.board.infrastructure.entity.feed.PostEntity;
import es.board.repository.entity.repository.*;
import es.board.service.FeedService;
import es.board.service.NotificationService;
import jakarta.persistence.EntityManager;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Builder
@Slf4j
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    @PersistenceContext
    private final EntityManager entityManager;


    private final FeedMapper feedMapper;

    private  final PostImageRepository feedImageRepository;

    private  final NotificationService notificationService;

    private  final PointHistoryRepository pointHistoryRepository;

    private  final RedisTemplate redisTemplate;


    private final JwtTokenProvider jwtTokenProvider;

    private final PostRepository postRepository;

    private final FeedDAO feedDAO;

    private final AsyncService asyncService;

    private final LikeDAO likeDAO;

//    private final LikeRepository likeRepository;

//    private  final SlackNotifier slackNotifier;

    private  final ObjectMapper objectMapper;

    private  static  final String TOP5_USER_KEY= "TOP_USER5_KEY";

    private  static  final String RECOMMEND_KEY= "Recommend_Feed_key";

    private  final StringRedisTemplate stringRedisTemplate;

    @Override
    public double getUserFeedCount(String userId) {

        return feedDAO.findUserFeedCount(userId);
    }

    @Override
    public   List<PostDTO.Response> findWeekBestFeed(int page, int size) {
        return feedMapper.fromBoardDtoList(feedDAO.findWeekBestFeed(page,size));
    }


    @Override
    public   List<PostDTO.Response> findCommentDESC(int page, int size) {
        return feedMapper.fromBoardDtoList(feedDAO.findCountComment(page,size));
    }

    @Override
    public List<String> findPagingFeedIds(int page, int size) {
        return feedDAO.findPagingIds(page,size);
    }

    @Override
    public   List<PostDTO.Response> findReplyDESC(int page, int size) {
        return feedMapper.fromBoardDtoList(feedDAO.findReplyCount(page,size));
    }


    @Override
    public   List<PostDTO.Response> findViewDESC(int page, int size) {
        return feedMapper.fromBoardDtoList(feedDAO.findViewDESC(page,size));
    }



    @Override
    public Map<String, Object> getUserMapageLikeAndFeedCount(String userId) {
        return  feedDAO.findUserMyPageLikeAndFeedCount(userId);
    }

    @Override
    public List<TopWriter> getTopWriters() {
//        ValueOperations<String, List<TopWriter>> valueOps = redisTemplate.opsForValue();
//        List<TopWriter> cachedData = valueOps.get(TOP5_USER_KEY);
//        if (cachedData != null) {
//            return cachedData;
//        }
//        List<TopWriter> topWriters = getWriters();
//        valueOps.set(TOP5_USER_KEY, topWriters, 3, TimeUnit.HOURS);
//        return topWriters;
        return  null;
    }

    @Override
    public CompletableFuture<PostDTO.Request> saveFeed(PostDTO.Request res) {
        return CompletableFuture.supplyAsync(() -> {
            checkValueFeed(res);
            Map<String,Object> Ids = savePost(res);
            asyncService.savePostAsync(feedMapper.toBoardDocument(res, (int) Ids.get("postId"), (String)Ids.get("feedUID")), (int) Ids.get("postId"));
//            slackNotifier.sendMessage(String.format("%s님이 \"%s\" 글을 작성하셨습니다",
//                    res.getUsername(),
//                    res.getDescription().replace("\"", "'")));
            List<String> imageUrls = extractImageUrls(res.getDescription());
//            if (!imageUrls.isEmpty() || res.getImageURL()!=null){
//                Long postId = ((Number) Ids.get("postId")).longValue();
////                feedImageRepository.updateFeedIdByImageUrls(postId, imageUrls);
//            }
            return res;
        }).thenApplyAsync(response -> {
//            grantFeedPoint(response.getUserId(),response.getUsername());
//            notificationService.sendPointNotification(response.getUserId(),response.getId(),"피드 작성 포인트를 발급 받으셨습니디");
            return  null;
        });

    }

    @Override
    public List<PostDTO.Response> getRecommendFeed() {

        ValueOperations<String, List<PostDTO.Response>> valueOps = redisTemplate.opsForValue();

        List<PostDTO.Response> cachedData = valueOps.get(RECOMMEND_KEY);

        if (cachedData != null) {
            return cachedData;
        }
        List<PostDTO.Response> feedRequests = feedMapper.fromBoardDtoList(feedDAO.findRecommendFeed());

        valueOps.set(RECOMMEND_KEY, feedRequests, 6, TimeUnit.HOURS);
        return feedRequests;
    }

    @Override
    public List<String> getfeedUIDList(List<PostDTO.Response> requests) {

        return null;
    }

    @Override
    public List<PostDTO.Response> getCategoryFeed(String category) {
        return feedMapper.fromBoardDtoList(feedDAO.findCategoryAndContent(category));
    }

    @Override
    public List<PostDTO.Response> getMonthPopularFeed() {

        return feedMapper.fromBoardDtoList(feedDAO.findMonthPopularFeed());
    }

    @Override
    public List<PostDTO.Response> getPopularFeedDESC(int page, int size) {
        return feedMapper.fromBoardDtoList(feedDAO.findPopularFeedDESC(page, size));
    }

    @Override
    public List<PostDTO.Response> getRangeTimeFeed(LocalDateTime startDate, LocalDateTime endTime) {
        return feedMapper.fromBoardDtoList(feedDAO.findRangeTimeFeed(startDate, endTime));
    }
    @Override
    public PostDTO.Response getPopularFeedOne() {

        return feedMapper.fromBoardDto(feedDAO.findPopularFeedOne());
    }

    @Override
    public List<PostDTO.Response> getRecentFeed() {

        return feedMapper.fromBoardDtoList(feedDAO.findRecentFeed());
    }

    @Override
    public List<PostDTO.Request> createBulkFeed(List<PostDTO.Request> feeds) {
        feedDAO.saveBulkFeed(bulkToEntity(feeds));
        return feeds;
    }

    @Override
    public List<Board> getSearchBoard(String text) {
        return feedDAO.findSearchBoard(text);
    }




    @Override
    public Map<String, Double> getDayAggregation(){

        return  feedDAO.findDayAggregation();
    }

    @Override
    public Map<String, Object> getDataFeed(int page, int size,String  category) {
        return  feedDAO.findDataFeed(page,size,category);
    }

    @Override
    public PostImage saveFeedImages(String imageUrl) {
        PostImage saved = feedImageRepository.save(
                PostImage.builder()
                        .imageUrl(imageUrl)
                        .createdAt(LocalDateTime.now())
                        .build()
        );
         return  saved;
    }

    @Override
    public Map<String, Object>  getNoticeFeed(int page, int size) {
        return feedDAO.findNoticeFeed(page,size);
    }

    @Override
    public List<PostDTO.Response> getLikeCount() {

        return feedMapper.fromBoardDtoList(feedDAO.findLikeCount());
    }

    @Override
    public List<PostDTO.Response> getPagingFeed(int page, int size) {

        return feedMapper.fromBoardDtoList(feedDAO.findPagingMainFeed(page, size));
    }

    @Override
    public List<PostDTO.Response> getMostViewFeed(int page, int size) {

        return feedMapper.fromBoardDtoList(feedDAO.findMostViewFeed(page, size));
    }

    @Override
    public Map<String, Object> getFetchTotalFeedStats() {
        return feedDAO.fetchTotalFeedStats();
    }

    @Override
    public void deleteFeed(String id, String userId) {
        feedDAO.deleteFeed(id);
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
    public PostDTO.Update updateFeed(String id, PostDTO.Update update) {
        feedDAO.modifyFeed(id, update);
        return update;
    }

    @Override
    public void plusLike(String id, String userId) {
        if (isAlreadyLiked(id, userId)) {
            throw new IllegalStateException("이미 좋아요를 누른 상태입니다.");
        }
//        likeRepository.save(feedMapper.toLikeEntity(id, userId));
        asyncService.postLike(userId, id);
    }

    @Override
    @Transactional
    public void cancelLike(String userId, String feedId) {
//
//        if (isAlreadyLiked(userId, feedId)) {
//            likeRepository.deleteByUserIdAndFeedUID(userId, feedId);
//            asyncService.cancelLike(userId, feedId);
//        } else {
//            throw new IllegalStateException("좋아요를 누른 상태가 아닙니다.");
//        }
    }


    @Override
    public PostDTO.Response getFeedDetail(String id) {

        return feedMapper.fromBoardDto(feedDAO.findFeedDetail(id));
    }

    @Override
    public Integer getPointAll(String userId) {
        return null;
    }

    @Override
    public boolean isAlreadyLiked(String userId, String id) {
        return true;
//        return likeRepository.existsByUserIdAndFeedUID(userId, id);
    }

    private void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("유저 이름을 입력해주세요.");
        }
        if (!username.matches("^[a-zA-Z0-9가-힣]+$")) {
            throw new IllegalArgumentException("유저 이름에는 특수문자를 사용할 수 없습니다.");
        }
    }


    public List<Board> bulkToEntity(List<PostDTO.Request> res) {
        List<Board> boards = new ArrayList<>();
        for (PostDTO.Request dto : res) {
            Board feed = Board.builder()
//                    .feedUID(dto.getFeedUID())
                    .title(dto.getTitle())
                    .description(dto.getDescription())
//                    .likeCount(dto.getLikeCount())
                    .createdAt(LocalDateTime.now())
                    .build();
            boards.add(feed);
        }
        return boards;
    }
    private List<String> extractImageUrls(String html) {
        List<String> urls = new ArrayList<>();
        Matcher matcher = Pattern.compile("<img[^>]+src=[\"']([^\"']+)[\"']").matcher(html);
        while (matcher.find()) {
            urls.add(matcher.group(1));
        }
        return urls;
    }

    private Map<String,Object>  savePost(PostDTO.Request feedSaveDTO) {
        Map<String,Object> Ids=new HashMap<>();
        PostEntity savedPost = postRepository.save(feedMapper.toPostEntity(feedSaveDTO));
        Ids.put("postId",savedPost.getId());
//        Ids.put("feedUID",savedPost.getFeedUID());
        return Ids;
    }


//    private List<String> extractFeedUID(List<PostDTO.Response> requests) {
//        List<String> feedUIDs = requests.stream()
//                .map(PostDTO.Response::getFeedUID)
//                .collect(Collectors.toList());
//        return feedUIDs;
//    }
    private static void checkValueFeed(PostDTO.Request feedSaveDTO) {
        if (isEmpty(feedSaveDTO.getTitle()) || isEmpty(feedSaveDTO.getDescription()) || isEmpty(feedSaveDTO.getCategory())) {
            throw new IllegalArgumentException("제목, 본문, 카테고리는 필수 입력값입니다.");
        }
    }
    private static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
    public void grantFeedPoint(String userId,String username) {
        if ((userId == null) || username.equals("heong") || username.equals("asd")
                || (Objects.equals(username, "익명"))){

            log.info("포인트 지급불가");
            return;
        }
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
           PointHistoryEntity history = PointHistoryEntity.builder()
                    .userId(userId)
                    .pointChange(3)
//                    .username(username)
                    .reason("피드")
                    .createdAt(LocalDateTime.now())
                    .build();
            pointHistoryRepository.save(history);
        }

//    private List<TopWriter> getWriters() {
//        List<TopWriter> topWriters = feedDAO.findTopWriters();
//        List<PointHistoryEntity> pointHistories = pointHistoryRepository.findByUserAllId();
//        Map<String, Integer> pointMap = pointHistories.stream()
//                .collect(Collectors.toMap(PointHistoryEntity::getUsername,
//                                        PointHistoryEntity::getPointChange));
//        for (TopWriter writer : topWriters) {
//            Integer points = pointMap.get(writer.getUsername());
//            if (points != null) {
//                writer.setTotalCount(writer.getTotalCount() + points);
//            }
//        }
//        topWriters.sort(Comparator.comparingDouble(TopWriter::getTotalCount).reversed());
//
//        return topWriters;
//    }

//    @Scheduled(cron = "0 0 * * * *") // 매 시 정각
//    public void cleanUnusedImages() {
//        File uploadDir = new File("/Users/wngud/esfile/file/");
//        File[] files = uploadDir.listFiles();
//
//        if (files == null) return;
//
//        for (File file : files) {
//            String url = "/static/feed-images/" + file.getName();
//            boolean inUse = feedImageRepository.existsByImageUrl(url);
//
//            if (!inUse) {
//                file.delete();
////                s3Uploader.deleteFile(url);
//                log.info("삭제됨: {}", file.getName());
//            }
//        }
//    }
}



