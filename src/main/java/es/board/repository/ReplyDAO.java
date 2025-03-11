package es.board.repository;

import es.board.controller.model.res.ReplyCreate;
import es.board.repository.document.Reply;

import java.util.List;
import java.util.Map;

public interface ReplyDAO {

    Map<String, Object> findPartialReply(String id);

    Map<String, Double> findAggregationReply(List<String> feedUIDs);
    void saveReply(ReplyCreate dto);
}
