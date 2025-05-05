package es.board.service.impl;

import es.board.controller.model.mapper.CommentMapper;
import es.board.controller.model.mapper.FeedMapper;
import es.board.controller.model.req.CommentRequest;
import es.board.controller.model.req.CommentUpdate;
import es.board.controller.model.req.FeedRequest;
import es.board.controller.model.res.CommentCreate;
import es.board.repository.CommentDAO;
import es.board.repository.LikeDAO;
import es.board.repository.ReplyDAO;
import es.board.repository.document.Comment;
import es.board.repository.entity.PointHistory;
import es.board.repository.entity.entityrepository.NotificationRepository;
import es.board.repository.entity.entityrepository.PointHistoryRepository;
import es.board.repository.entity.entityrepository.PostRepository;
import es.board.service.CommentService;
import es.board.service.NotificationService;
import es.board.service.event.FeedEvent;
import es.board.service.event.producer.CommentEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {


    private  final FeedMapper feedMapper;

    private  final CommentMapper commentMapper;

    private  final StringRedisTemplate stringRedisTemplate;

    private  final NotificationService notificationService;

    private  final CommentEventPublisher commentEventPublisher;

    private final CommentDAO commentDAO;

    private  final ReplyDAO replyDAO;

    private  final LikeDAO likeDAO;

    private  final PostRepository postRepository;

    private  final PointHistoryRepository pointHistoryRepository;

    private  final NotificationRepository notificationRepository;

    @Override
    public Map<String, Object> getUserComments(String userId) {

        return commentDAO.findUserComments(userId);
    }

    @Override
    public List<CommentRequest> getUserRangeTimeActive(String userId) {

      return  commentMapper.changeCommentListDTO(commentDAO.findUserRangeActive(userId));
    }

    @Override
    public List<FeedRequest> getFeedAndCommentMyPage(String userId,int page ,int size) {

        return  feedMapper.fromBoardDtoList(commentDAO.findFeedAndCommentMypage(userId,page,size));
    }

    @Override
     public List<Comment> getMyPageComment(String  userId, int num ,int size){
        return commentDAO.findMyPagePagingComment(userId,num,size);
    }
    @Override
    public List<Comment> getSearchComment(String text) {
        return commentDAO.findSearchComment(text);
    }


    @Override
    public Comment editComment(String id, CommentUpdate eq) {
        return commentDAO.modifyComment(id, commentMapper.convertDtoToEntity(eq));
    }

    @Override
    public List<CommentRequest> getRecentComment() {

        return commentMapper.changeCommentListDTO(commentDAO.findRecentComment());
    }

    @Override
    public void saveComment(CommentCreate dto) {
        checkValueComment(dto);
        commentDAO.saveCommentIndex(dto);
        String userId = postRepository.findByFeedUID(dto.getFeedUID());

        FeedEvent event = FeedEvent.builder()
                .feedUID(dto.getFeedUID())
                .postOwnerId(userId)
                .commentUID(dto.getCommentUID())
                .commenterId(dto.getUserId())
                .username(dto.getUsername())
                .content(dto.getContent())
                .createdAt(dto.getCreatedAt())
                .build();


        commentEventPublisher.publishCommentEvent(event);

//        if (userId!= null && !userId.equals(dto.getUserId())) {
//            notificationService.sendCommentNotification(userId, dto.getFeedUID(),
//                    dto.getUsername() + "님이 댓글을 작성하였습니다: " + dto.getContent());
//            notificationRepository.save(commentMapper.toCommentNotification(userId,dto));
//        }
//        grantCommentPoint(dto.getUserId(),dto.getUsername());
    }



    @Override
    public void plusCommentLike(String id) {
            likeDAO.saveCommentLike(id);
    }

    @Override
    public List<CommentCreate> saveBulkComment(List<CommentCreate> comments) {
        commentDAO.CreateManyComment(BulkToEntity(comments));
        return comments;
    }

    @Override
    public  Map<String, Object> findCommentsWithCount(String feedUID){
        return commentDAO.findCommentsWithCount(feedUID);
    }
    @Override
    public List<CommentRequest> getLikeCount() {

        return commentMapper.changeCommentListDTO(commentDAO.findLikeCount());
    }

    @Override
    public Map<String, Double> getCommentAndReplyAggregation(List<String> feedUIDs, int num, int size) {
        return getStringDoubleMap(feedUIDs, replyDAO.findAggregationReply(feedUIDs),
                commentDAO.findPagingComment(feedUIDs, num, size));
    }
    @Override
    public List<Comment> getMostCommentCount() {
        return commentDAO.findMostCommentCount();
    }


    @Override
    public List<CommentRequest> getCommentOne(String commentUID){

        return  commentMapper.changeCommentListDTO(commentDAO.findCommentId(commentUID));
    }

    @Override
    public Map<String,Long> getTodayCommentAggregation(){
            return commentDAO.findTodayCommentAggregation();
    }


    @Override
    public void deleteComment(String id) {
        commentDAO.deleteCommentId(id);
    }




    public List<Comment> BulkToEntity(List<CommentCreate> res) {
        List<Comment> comments = new ArrayList<>();
        for (CommentCreate dto : res) {
            Comment comment = Comment.builder()
                    .commentUID(dto.getCommentUID())
                    .username(dto.getUsername())
                    .content(dto.getContent())
                    .createdAt(dto.getCreatedAt())
                    .build();
            comments.add(comment);
        }
        return comments;
    }

    private static void checkValueComment(CommentCreate commentCreate) {
        if (isEmpty(commentCreate.getUsername()) || isEmpty(commentCreate.getContent())) {
            throw new IllegalArgumentException("내용은 필수 입력값입니다.");
        }
    }
    private static boolean isEmpty(String value) {

        return value == null || value.trim().isEmpty();
    }

    private static Map<String, Double> getStringDoubleMap(List<String> feedUIDs, Map<String, Double> replyCounts, Map<String, Double> commentCounts) {
        Map<String, Double> aggregatedData = new HashMap<>();
        for (String feedUID : feedUIDs) {
            double replyCount = replyCounts.getOrDefault(feedUID, 0.0);
            double commentCount = commentCounts.getOrDefault(feedUID, 0.0);
            aggregatedData.put(feedUID, replyCount + commentCount);
        }
        return aggregatedData;
    }

}
