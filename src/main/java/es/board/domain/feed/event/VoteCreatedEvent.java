package es.board.domain.feed.event;

import es.board.controller.model.dto.poll.PollDto;
import es.board.controller.model.dto.poll.PollVoteDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VoteCreatedEvent {



    private final String userId;

    private final PollVoteDTO.Request request;


}
