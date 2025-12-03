package es.board.domain.event;

import es.board.controller.model.dto.feed.LikeDto;
import es.board.controller.model.dto.poll.PollDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PollCreatedEvent {


    private final int postId;

    private final String userId;

    private final PollDto.Request request;



}
