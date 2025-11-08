package es.board.service;

import es.board.controller.model.dto.feed.ReplyDTO;

import java.util.List;

public interface ReplyService {


    void saveReply(String userId, ReplyDTO.Response response);

    List<ReplyDTO.Request> getReplys(String userId, int id);


    void deleteReply(long id);
}
