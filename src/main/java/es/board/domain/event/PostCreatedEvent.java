package es.board.domain.event;

import es.board.controller.model.dto.feed.PostDTO;
import es.board.controller.model.dto.feed.ReplyDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostCreatedEvent {


    private final int postId;

    private final String userId;

    private final PostDTO.Request request;



}
