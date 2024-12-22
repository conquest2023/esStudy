package es.board.repository.dao;

import es.board.model.res.ReplyCreateResponse;
import es.board.repository.document.Reply;

import java.util.List;

public interface ReplyDAO {

    List<Reply> findPartialReply(String id);

    void saveReply(ReplyCreateResponse dto);
}
