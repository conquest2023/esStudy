package es.board.service;

import es.board.controller.model.req.ReplyRequest;
import es.board.controller.model.res.ReplyCreate;

import java.util.List;
import java.util.Map;

public interface ReplyService {

    Map<String, List<ReplyRequest>> getRepliesGroupedByComment(String feedId);

    void saveReply(ReplyCreate response);
    List<ReplyRequest> getPartialReply(String id);
}
