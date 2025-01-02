package es.board.service.impl;

import es.board.controller.model.req.ReplyRequest;
import es.board.controller.model.res.ReplyCreateResponse;
import es.board.repository.ReplyDAO;
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

    @Override
    public List<ReplyRequest> getPartialReply(String id){
        ReplyRequest request=new ReplyRequest();

        return request.ReplyListToDTO(replyDAO.findPartialReply(id));
    }
    @Override
    public  void  saveReply(ReplyCreateResponse response){
        replyDAO.saveReply(response);
    }



    public Map<String, List<ReplyRequest>> getRepliesGroupedByComment(String feedId) {

        List<ReplyRequest> replies = getPartialReply(feedId);
//        log.info(replies.toString());
        return replies.stream()
                .collect(Collectors.groupingBy(ReplyRequest::getCommentUID));
    }
}
