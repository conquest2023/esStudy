package es.board.service.impl;

import es.board.controller.model.mapper.FeedMapper;
import es.board.controller.model.req.ReplyRequest;
import es.board.controller.model.res.CommentCreate;
import es.board.controller.model.res.ReplyCreate;
import es.board.repository.CommentDAO;
import es.board.repository.ReplyDAO;
import es.board.repository.document.Reply;
import es.board.service.NotificationService;
import es.board.service.ReplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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


    @Override
    public Map<String,Object> getPartialReply(String id){
        return replyDAO.findPartialReply(id);
    }
    @Override
    public  void  saveReply(ReplyCreate response){
        checkValueReply(response);
        String userId=commentDAO.findCommentUID(response.getCommentUID()).getUserId();
        replyDAO.saveReply(response);
        if (userId == null) {
            log.info("답글이 작성됨: {}", response.getFeedUID());
        } else {
            if (!userId.equals(response.getUserId())) {
                notificationService.sendReplyNotification(userId,
                        response.getUsername() + "님이 답글을 작성하였습니다: " + response.getContent());
            }
        }
    }

    public Map<String, List<ReplyRequest>> getRepliesGroupedByComment(String feedId) {
        List<ReplyRequest> replies =feedMapper.ReplyListToDTO((List<es.board.repository.document.Reply>) getPartialReply(feedId).get("replyList"));
        return replies.stream()
                .collect(Collectors.groupingBy(ReplyRequest::getCommentUID));
    }

    private static void checkValueReply(ReplyCreate response) {

        if (isEmpty(response.getUsername()) || isEmpty(response.getContent())) {
            throw new IllegalArgumentException("답글은 필수 입력값입니다.");
        }
    }
    private static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
