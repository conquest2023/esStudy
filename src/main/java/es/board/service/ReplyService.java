package es.board.service;

import es.board.controller.model.dto.feed.ReplyDTO;

import java.util.List;
import java.util.Map;

public interface ReplyService {

    Map<String, List<ReplyDTO.Request>> getRepliesGroupedByComment(String feedId);

    void saveReply(ReplyDTO.Response response);
    Map<String,Object> getPartialReply(String id);
}
