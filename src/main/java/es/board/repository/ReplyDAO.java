package es.board.repository;

import es.board.controller.model.res.ReplyCreate;
import es.board.repository.document.Reply;

import java.util.List;

public interface ReplyDAO {

    List<Reply> findPartialReply(String id);

    void saveReply(ReplyCreate dto);
}
