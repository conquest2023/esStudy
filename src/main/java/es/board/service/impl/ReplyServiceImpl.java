package es.board.service.impl;

import es.board.controller.model.mapper.FeedMapper;
import es.board.controller.model.req.ReplyRequest;
import es.board.controller.model.res.ReplyCreate;
import es.board.repository.CommentDAO;
import es.board.repository.ReplyDAO;
import es.board.repository.entity.PointHistory;
import es.board.repository.entity.entityrepository.NoticeRepository;
import es.board.repository.entity.entityrepository.NotificationRepository;
import es.board.repository.entity.entityrepository.PointHistoryRepository;
import es.board.service.NotificationService;
import es.board.service.ReplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class ReplyServiceImpl implements ReplyService {

    private final ReplyDAO replyDAO;

    private  final CommentDAO commentDAO;

    private  final FeedMapper feedMapper;

    private  final NotificationService notificationService;

    private  final StringRedisTemplate stringRedisTemplate;

    private  final PointHistoryRepository pointHistoryRepository;

    private  final NotificationRepository notificationRepository;
    @Override
    public Map<String,Object> getPartialReply(String id){
        return replyDAO.findPartialReply(id);
    }
    @Override
    public  void  saveReply(ReplyCreate response){
        checkValueReply(response);
        String userId=commentDAO.findCommentUID(response.getCommentUID()).getUserId();
        replyDAO.saveReply(response);
        grantReplyPoint(response.getUserId());
        if (!userId.equals(response.getUserId())) {
                notificationService.sendReplyNotification(userId, response.getFeedUID(), response.getUsername() + "님이 답글을 작성하였습니다: " + response.getContent());
        }
        notificationRepository.save(feedMapper.toReplyNotification(userId,response));
    }
    public Map<String, List<ReplyRequest>> getRepliesGroupedByComment(String feedId) {
        List<ReplyRequest> replies =feedMapper.fromReplyDtoList((List<es.board.repository.document.Reply>) getPartialReply(feedId).get("replyList"));
        return replies.stream()
                .collect(Collectors.groupingBy(ReplyRequest::getCommentUID));
    }

    private static void checkValueReply(ReplyCreate response) {

        if (isEmpty(response.getContent())) {
            throw new IllegalArgumentException("답글은 필수 입력값입니다.");
        }
    }

    public void grantReplyPoint(String userId) {
        String today = LocalDate.now().toString();
        String key = "reply:point:" + userId + ":" + today;
        Long currentCount = stringRedisTemplate.opsForValue().increment(key);
        if (currentCount == 1) {
            stringRedisTemplate.expire(key, Duration.ofDays(1));
        }
        if (currentCount > 15) {
            log.info("{}님은 오늘 답글 작성 포인트 한도(10개)를 초과했습니다.", userId);
            return;
        }
        createPointHistory(userId);
        log.info("답글 작성 포인트 지급 완료! 현재 작성 횟수: {}", currentCount);
    }
    public void createPointHistory(String userId) {
        PointHistory history = PointHistory.builder()
                .userId(userId)
                .pointChange(3)
                .reason("답글")
                .createdAt(LocalDateTime.now())
                .build();
        pointHistoryRepository.save(history);
    }
    private static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
