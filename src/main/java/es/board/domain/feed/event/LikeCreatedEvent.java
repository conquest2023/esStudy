package es.board.domain.feed.event;

import es.board.controller.model.dto.feed.LikeDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LikeCreatedEvent {


    private final int postId;

    private final String userId;

    private final LikeDto.Request request;



}
