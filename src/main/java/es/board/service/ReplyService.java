package es.board.service;

import es.board.model.req.ReplyRequest;
import es.board.model.res.ReplyCreateResponse;

import java.util.List;

public interface ReplyService {



    void saveReply(ReplyCreateResponse response);
    List<ReplyRequest> getPartialReply(String id);
}
