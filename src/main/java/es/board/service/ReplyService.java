package es.board.service;

import es.board.model.req.ReplyRequest;
import es.board.model.res.ReplyCreateResponse;

import java.util.List;
import java.util.Map;

public interface ReplyService {

    Map<String, List<ReplyRequest>> getRepliesGroupedByComment(String feedId);

    void saveReply(ReplyCreateResponse response);
    List<ReplyRequest> getPartialReply(String id);
}
