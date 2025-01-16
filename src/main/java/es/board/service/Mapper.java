package es.board.service;

import es.board.controller.model.req.FeedRequest;
import es.board.repository.document.Board;
import org.springframework.stereotype.Component;

import java.util.List;


@org.mapstruct.Mapper
@Component
public interface Mapper {

    FeedRequest toDTO(Board board);
    List<FeedRequest> toDTOList(List<Board> boards);
}
