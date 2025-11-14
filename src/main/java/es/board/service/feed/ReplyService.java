package es.board.service.feed;

import es.board.controller.model.dto.feed.ReplyDTO;

import java.util.List;

public interface ReplyService {


    void saveReply(String userId, ReplyDTO.Response response);

    List<ReplyDTO.Request> getReplys(String userId, int id);

    ReplyDTO.Request updateReply(long id, ReplyDTO.Update update);

    void deleteReply(long id);
}
